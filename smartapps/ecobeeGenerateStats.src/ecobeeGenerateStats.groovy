/**
 *  ecobeeGenerateStats
 *
 *  Copyright Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 *  in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 *  Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 *  to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *
 *  Software Distribution is restricted and shall be done only with Developer's written approval.
 * 
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
import java.text.SimpleDateFormat 
definition(
    name: "${get_APP_NAME()}",
    namespace: "yracine",
    author: "Yves Racine",
    description: "This smartapp allows a user to generate runtime stats (daily by scheduling or based on custom dates) on their devices controlled by ecobee such as a heating & cooling component,fan, dehumidifier/humidifier/HRV/ERV. ",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "${get_APP_NAME()}, the smartapp that generates daily runtime reports about your ecobee components"
		paragraph "Version 3.0" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.me/ecomatiqhomes",
				title:"Paypal donation..."
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("Generate daily stats for this ecobee thermostat") {
		input "ecobee", "capability.thermostat", title: "MyEcobee?"

	}
	section("Start date for the initial run, format = YYYY-MM-DD") {
		input "givenStartDate", "text", title: "Beginning Date [default=yesterday]", required: false
	}        
	section("Start time for initial run HH:MM (24HR)") {
		input "givenStartTime", "text", title: "Beginning time [default=00:00]"	, required: false
	}        
	section("End date for the initial run = YYYY-MM-DD") {
		input "givenEndDate", "text", title: "End Date [default=today]", required: false
	}        
	section("End time for the initial run (24HR)" ) {
		input "givenEndTime", "text", title: "End time [default=00:00]", required: false
	}        
	if (isST()) {    
    		section( "Notifications" ) {
	    		input "sendPushMessage", "enum", title: "Send a push notification?", options:["Yes", "No"], required: false, default:"No"
	    		input "phoneNumber", "phone", title: "Send a text message?", required: false	
	    	}	
	    	section("Enable Amazon Echo/Ask Alexa Notifications [optional, default=false]") {
		    input (name:"askAlexaFlag", title: "Ask Alexa verbal Notifications?", type:"bool",
    			description:"optional",required:false)
		    	input (name:"listOfMQs",  type:"enum", title: "List of the Ask Alexa Message Queues (default=Primary)", options: state?.askAlexaMQ, multiple: true, required: false,
			    	description:"optional")            
    			input "AskAlexaExpiresInDays", "number", title: "Ask Alexa's messages expiration in days (default=2 days)?", required: false
	    	}
    	}        
	section("Logging") {
		input "detailedNotif", "bool", title: "Detailed logging?", required:false
	}
	section([mobileOnly:true]) {
        	label title: "Assign a name for this SmartApp", required: false
    	}
    
    
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	atomicState?.retries=0    
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	unschedule()    
	initialize()
}

boolean isST() { 
    return (getHub() == "SmartThings") 
}

private getHub() {
    def result = "SmartThings"
    if(state?.hub == null) {
        try { [value: "value"]?.encodeAsJson(); } catch (e) { result = "Hubitat" }
        state?.hub = result
    }
    log.debug "hubPlatform: (${state?.hub})"
    return state?.hub
}
def initialize() {

	atomicState?.timestamp=''
	atomicState?.componentAlreadyProcessed=' '

	runIn((1*60),	"generateStats") // run 1 minute later as it requires notification.     
	subscribe(app, appTouch)
	atomicState?.poll = [ last: 0, rescheduled: now() ]

	//Subscribe to different events (ex. sunrise and sunset events) to trigger rescheduling if needed
	subscribe(location, "sunset", rescheduleIfNeeded)
	subscribe(location, "mode", rescheduleIfNeeded)
	subscribe(location, "sunsetTime", rescheduleIfNeeded)
	subscribe(location, "askAlexaMQ", askAlexaMQHandler)
	rescheduleIfNeeded()   
}

def askAlexaMQHandler(evt) {
	if (!evt) return
	switch (evt.value) {
		case "refresh":
		state?.askAlexaMQ = evt.jsonData && evt.jsonData?.queues ? evt.jsonData.queues : []
		log.info("askAlexaMQHandler>refresh value=$state?.askAlexaMQ")        
		break
	}
}

def rescheduleIfNeeded(evt) {
	if (evt) log.debug("rescheduleIfNeeded>$evt.name=$evt.value")
	Integer delay = (24*60) // By default, do it every day
	BigDecimal currentTime = now()    
	BigDecimal lastPollTime = (currentTime - (atomicState?.poll["last"]?:0))  
 
	if (lastPollTime != currentTime) {    
		Double lastPollTimeInMinutes = (lastPollTime/60000).toDouble().round(1)      
		log.info "rescheduleIfNeeded>last poll was  ${lastPollTimeInMinutes.toString()} minutes ago"
	}
	if (((atomicState?.poll["last"]?:0) + (delay * 60000) < currentTime)) {
		log.info "rescheduleIfNeeded>scheduling dailyRun in ${delay} minutes.."
//		generate the stats every day at 0:10

		schedule("0 10 0 * * ?", dailyRun)    
	}
    
	// Update rescheduled state
    
	if (!evt) atomicState?.poll["rescheduled"] = now()
}
   

def appTouch(evt) {
	atomicState?.timestamp=''
	atomicState?.componentAlreadyProcessed=' '
	generateStats()
}



void dailyRun() {
	Integer delay = (24*60) // By default, do it every day
	atomicState?.poll["last"] = now()
		
	//schedule the rescheduleIfNeeded() function
    
	if (((atomicState?.poll["rescheduled"]?:0) + (delay * 60000)) < now()) {
		log.info "takeAction>scheduling rescheduleIfNeeded() in ${delay} minutes.."
		schedule("0 10 0 * * ?", rescheduleIfNeeded)    
		// Update rescheduled state
		atomicState?.poll["rescheduled"] = now()
	}
	settings.givenStartDate=null
	settings.givenStartTime=null
	settings.givenEndDate=null
	settings.givenEndTime=null
	if (detailedNotif) {    
		log.debug("dailyRun>for $ecobee, about to call generateStats() with settings.givenEndDate=${settings.givenEndDate}")
	}    
	atomicState?.componentAlreadyProcessed=' '
	atomicState?.retries=0
	generateStats()
    
}

void reRunIfNeeded() {
	if (detailedNotif) {    
		log.debug("reRunIfNeeded>About to call generateStats() with state.componentAlreadyProcessed=${atomicState?.componentAlreadyProcessed}")
	}    
	generateStats()
   
}



private String formatISODateInLocalTime(dateInString, timezone='') {
	def myTimezone=(timezone)?TimeZone.getTimeZone(timezone):location.timeZone 
	if ((dateInString==null) || (dateInString.trim()=="")) {
		return (new Date().format("yyyy-MM-dd HH:mm:ss", myTimezone))
	}    
	SimpleDateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	Date ISODate = ISODateFormat.parse(dateInString)
	String dateInLocalTime =new Date(ISODate.getTime()).format("yyyy-MM-dd HH:mm:ss", myTimezone)
	log.debug("formatDateInLocalTime>dateInString=$dateInString, dateInLocalTime=$dateInLocalTime")    
	return dateInLocalTime
}


private def formatDate(dateString) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz")
	Date aDate = sdf.parse(dateString)
	return aDate
}

private def get_nextComponentStats(component=' ') {
	def nextInLine=[:]

	def components = [
		' ': 
			[position:1, next: 'auxHeat1'
			], 
		'auxHeat1': 
			[position:2, next: 'auxHeat2'
			], 
		'auxHeat2': 
			[position:3, next: 'auxHeat3'
			], 
		'auxHeat3': 
			[position:4, next: 'compCool1'
			], 
		'compCool1': 
			[position:5, next: 'compCool2'
			], 
		'compCool2': 
			[position:6, next: 'humidifier'
			],
		'humidifier': 
			[position:7, next: 'dehumidifier'
			],
		'dehumidifier': 
			[position:8, next: 'ventilator'
			],
		'ventilator': 
			[position:9, next: 'fan'
			],
		'fan': 
			[position:10, next: 'compHeat1'
			],
		'compHeat1': 
			[position:11, next: 'compHeat2'
			], 
		'compHeat2': 
			[position:12, next: 'compHeat3'
			], 
		'compHeat3': 
			[position:13, next: 'done'
			]
		]
	try {
		nextInLine = components.getAt(component)
	} catch (any) {
		if (detailedNotif) {
			log.debug "get_nextComponentStats>${component} not found"
		}   
		nextInLine=[position:1,next:'auxHeat1']        
	}        
    
	if (detailedNotif) {
		log.debug "get_nextComponentStats>got ${component}'s next component from components table= ${nextInLine}"
	}
	return nextInLine
		            
}


void generateStats() {	
	Integer MAX_POSITION=13
	Integer MAX_RETRIES=4
	float runtimeTotalYesterday,runtimeTotalDaily    
	String dateInLocalTime = new Date().format("yyyy-MM-dd", location.timeZone) 
	def delay = 2
    
	atomicState?.retries=	((atomicState?.retries==null) ?:0).toInteger() +1

	if (detailedNotif) {    
        log.debug("${get_APP_NAME()}>atomicState.retries=${atomicState?.retries}")
	}    	
	try {
		unschedule(reRunIfNeeded)
	} catch (e) {
    
		if (detailedNotif) {    
			log.debug("${get_APP_NAME()}>Exception $e while unscheduling reRunIfNeeded")
		}    	
	}    
    
	if (atomicState?.retries.toInteger() >= MAX_RETRIES) { 
		if (detailedNotif) {    
			log.debug("${get_APP_NAME()}>Max retries reached, exiting")
			send("max retries reached ${atomicState?.retries}), exiting")
		}    	
	}    	
    
	def component=atomicState?.componentAlreadyProcessed    // use logic to restart the batch process if needed due to ST rate limiting
	def nextComponent  = get_nextComponentStats(component) // get next Component To Be Processed	
	if (detailedNotif) {    
		log.debug("${get_APP_NAME()}>for $ecobee, about to process nextComponent=${nextComponent}, state.componentAlreadyProcessed=${atomicState?.componentAlreadyProcessed}")
	}    	
	if (atomicState?.timestamp == dateInLocalTime && (nextComponent.position.toInteger() >=MAX_POSITION)) {
		return // the daily stats are already generated 
	} else {    	
		// schedule a rerun till the stats are generated properly
		schedule("0 0/${delay} * * * ?", reRunIfNeeded)
		
	}    	
	
	String timezone = new Date().format("zzz", location.timeZone)
	String dateAtMidnight = dateInLocalTime + " 00:00 " + timezone    
	if (detailedNotif) {    
		log.debug("${get_APP_NAME()}>date at Midnight= ${dateAtMidnight}")
	}    	
	Date endDate = formatDate(dateAtMidnight) 
	Date startDate = endDate -1
        
	def reportStartDate = (settings.givenStartDate) ?: startDate.format("yyyy-MM-dd", location.timeZone)
	def reportStartTime=(settings.givenStartTime) ?:"00:00"    
	def dateTime = reportStartDate + " " + reportStartTime + " " + timezone
	startDate = formatDate(dateTime)
	Date yesterday = startDate-1    

	if (detailedNotif) {
		log.debug("${get_APP_NAME()}>start dateTime = ${dateTime}, startDate in UTC = ${startDate.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
	}    
	def reportEndDate = (settings.givenEndDate) ?: endDate.format("yyyy-MM-dd", location.timeZone) 
	def reportEndTime=(settings.givenEndTime) ?:"00:00"    
	dateTime = reportEndDate + " " + reportEndTime + " " + timezone
	endDate = formatDate(dateTime)
	if (detailedNotif) {    
		log.debug("${get_APP_NAME()}>end dateTime = ${dateTime}, endDate in UTC =${endDate.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
	}
    
    
	// Get the auxHeat1's runtime for startDate-endDate period
	component = 'auxHeat1'
	if (nextComponent?.position.toInteger() <= 1) { 
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentAuxHeat1RuntimeDaily) ? ecobee.currentAuxHeat1RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
    
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentAuxHeat1RuntimeYesterday)? ecobee.currentAuxHeat1RuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
	}     
	
	int heatStages = ecobee.currentHeatStages.toInteger()
    
	component = 'auxHeat2'
	if (heatStages >1 && (nextComponent.position.toInteger() <= 2) ) { 
    
//	Get the auxHeat2's runtime for startDate-endDate period
 	
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentAuxHeat2RuntimeDaily)? ecobee.currentAuxHeat2RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On  ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentAuxHeat2RuntimeYesterday)? ecobee.currentAuxHeat2RuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
    } else {     
    	atomicState?.componentAlreadyProcessed=component
    }

	component = 'auxHeat3'
	if (heatStages >2 && nextComponent.position.toInteger() <= 3) { 
    
//	Get the auxHeat3's runtime for startDate-endDate period
 	
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentAuxHeat3RuntimeDaily)? ecobee.currentAuxHeat3RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)},${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentAuxHeat3RuntimeYesterday)? ecobee.currentAuxHeat3RuntimeYesterday.toFloat().round(2):0
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
    } else {     
    	atomicState?.componentAlreadyProcessed=component
    }

// Get the compCool1's runtime for startDate-endDate period

	int coolStages = ecobee.currentCoolStages.toInteger()
	component = 'compCool1'

	if (nextComponent.position.toInteger() <= 4) {
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentCompCool1RuntimeDaily)? ecobee.currentCompCool1RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for the day before
		runtimeTotalYesterday = (ecobee.currentCompCool1RuntimeYesterday)? ecobee.currentCompCool1RuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
	}     
        
//	Get the compCool2's runtime for startDate-endDate period

	component = 'compCool2'
	if (coolStages >1 && nextComponent.position.toInteger() <= 5) {
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentCompCool2RuntimeDaily)? ecobee.currentCompCool2RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for the day before
		runtimeTotalYesterday = (ecobee.currentCompCool2RuntimeYesterday)? ecobee.currentCompCool2RuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday ) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
    } else {     
    	atomicState?.componentAlreadyProcessed=component
    }

	
	def hasDehumidifier = (ecobee.currentHasDehumidifier) ? ecobee.currentHasDehumidifier : 'false' 
	def hasHumidifier = (ecobee.currentHasHumidifier) ? ecobee.currentHasHumidifier : 'false' 
	def hasHrv = (ecobee.currentHasHrv)? ecobee.currentHasHrv : 'false' 
	def hasErv = (ecobee.currentHasErv)? ecobee.currentHasErv : 'false' 

	component = "humidifier"
	if (hasHumidifier=='true' && (nextComponent.position.toInteger() <= 6)) {
	 	// Get the humidifier's runtime for startDate-endDate period
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentHumidifierRuntimeDaily)? ecobee.currentHumidifierRuntimeDaily.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
    
	}

	component = 'dehumidifier'
	if (hasDehumidifier=='true' && (nextComponent.position.toInteger() <= 7)) {
	// Get the dehumidifier's for startDate-endDate period
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentDehumidifierRuntimeDaily)? ecobee.currentDehumidifierRuntimeDaily.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     

	}
	component = 'ventilator'
	if (hasHrv=='true' || hasErv=='true' && (nextComponent.position.toInteger() <= 8)) {
 	// Get the ventilator's runtime for  startDate-endDate period
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentVentilatorRuntimeDaily)? ecobee.currentVentilatorRuntimeDaily.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     

	}

 	component = 'fan'
// 	Get the fan's runtime for startDate-endDate period
	if (nextComponent.position.toInteger() <= 9) {

		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentFanRuntimeDaily)? ecobee.currentFanRuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)},${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for the day before
		runtimeTotalYesterday = (ecobee.currentFanRuntimeYesterday)? ecobee.currentFanRuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
	}     

	// Get the compHeat1's runtime for startDate-endDate period
	component = 'compHeat1'
	if (nextComponent.position.toInteger() <= 10) { 
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentCompHeat1RuntimeDaily) ? ecobee.currentCompHeat1RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
    
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentCompHeat1RuntimeYesterday)? ecobee.currentCompHeat1RuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
	}     
	
    
	component = 'compHeat2'
	if (heatStages >1 && (nextComponent.position.toInteger() <=11) ) { 
    
//	Get the compHeat2's runtime for startDate-endDate period
 	
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentCompHeat2RuntimeDaily)? ecobee.currentCompHeat2RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On  ${String.format('%tF', startDate)}, ${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentCompHeat2RuntimeYesterday)? ecobee.currentCompHeat2RuntimeYesterday.toFloat().round(2):0
		atomicState?.componentAlreadyProcessed=component
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
    } else {     
    	atomicState?.componentAlreadyProcessed=component
    }
	component = 'compHeat3'
	if (heatStages >2 && nextComponent.position.toInteger() <= 12) { 
    
//	Get the compHeat3's runtime for startDate-endDate period
 	
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentCompHeat3RuntimeDaily)? ecobee.currentCompHeat3RuntimeDaily.toFloat().round(2):0
		if (runtimeTotalDaily) {
			send "On ${String.format('%tF', startDate)},${ecobee} ${component}'s runtime stats=${runtimeTotalDaily} minutes", settings.askAlexaFlag
		}     
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentCompHeat3RuntimeYesterday)? ecobee.currentCompHeat3RuntimeYesterday.toFloat().round(2):0
		if (detailedNotif && runtimeTotalYesterday) {
			send "And, on ${String.format('%tF', yesterday)}, ${ecobee} ${component}'s runtime stats for the day before=${runtimeTotalYesterday} minutes"
		}     
    } else {     
    	atomicState?.componentAlreadyProcessed=component
    }

	nextComponent  = get_nextComponentStats(component) // get nextComponentToBeProcessed	
	if (nextComponent.position.toInteger() >= MAX_POSITION) {
		send "generated ${ecobee}'s daily stats done for ${String.format('%tF', startDate)} - ${String.format('%tF', endDate)} period"
		atomicState?.timestamp = dateInLocalTime // save the local date to avoid re-execution    
		unschedule(reRunIfNeeded) // No need to reschedule again as the stats are completed.
//		atomicState?.retries=0		        
	}
	
}

void generateRuntimeReport(component, startDate, endDate, frequence='daily') {

	if (detailedNotif) {
		log.debug("${get_APP_NAME()}>For ${ecobee} ${component}, about to call getReportData with endDate in UTC =${endDate.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
	}        
	ecobee.getReportData(null, startDate, endDate, null, null, component,false)
	if (detailedNotif) {
		log.debug("${get_APP_NAME()}>For ${ecobee} ${component}, about to call generateRuntimeReportEvents with endDate in UTC =${endDate.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
	}        
	ecobee.generateReportRuntimeEvents(component, startDate,endDate, 0, null,frequence)

}

private send(msg, askAlexa=false) {
	def message = "${get_APP_NAME()}>${msg}"
	if (sendPushMessage == "Yes") {
			sendPush(message)
	}
	if (askAlexa) {
		def expiresInDays=(AskAlexaExpiresInDays)?:2    
		sendLocationEvent(
			name: "AskAlexaMsgQueue", 
			value: "${get_APP_NAME()}", 
			isStateChange: true, 
			descriptionText: msg, 
			data:[
				queues: listOfMQs,
				expires: (expiresInDays*24*60*60)  /* Expires after 2 days by default */
			]
		)
	} /* End if Ask Alexa notifications*/

	if (phone) {
		log.debug("sending text message")
		sendSms(phone, message)
	}
    
	log.debug msg
    
}

private def get_APP_NAME() {
	return "ecobeeGenerateStats"
}

