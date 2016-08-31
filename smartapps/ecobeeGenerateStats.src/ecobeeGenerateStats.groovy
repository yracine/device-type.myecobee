/**
 *  ecobeeGenerateStats
 *
 *  Copyright 2015 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
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
    name: "ecobeeGenerateStats",
    namespace: "yracine",
    author: "Yves Racine",
    description: "This smartapp allows a ST user to generate runtime stats (daily by scheduling or based on custom dates) on their devices controlled by ecobee such as a heating & cooling component,fan, dehumidifier/humidifier/HRV/ERV. ",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "ecobeeGenerateStats, the smartapp that generates daily & weekly runtime reports about your ecobee components"
		paragraph "Version 2.1" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
				title:"Paypal donation..."
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("Generate daily stats for this ecobee thermostat") {
		input "ecobee", "device.myEcobeeDevice", title: "Ecobee?"

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
	section( "Notifications" ) {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes", "No"]], required: false
		input "phoneNumber", "phone", title: "Send a text message?", required: false
    }
	section("Detailed Notifications") {
		input "detailedNotif", "bool", title: "Detailed Notifications?", required:false
    }
    
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	unschedule()    
	initialize()
}

def initialize() {
	// generate the stats every day at 1:00 (am)

	runIn((1*60),	"generateStats") // run 1 minute later as it requires notification.     
	subscribe(app, appTouch)
	state?.poll = [ last: 0, rescheduled: now() ]

	//Subscribe to different events (ex. sunrise and sunset events) to trigger rescheduling if needed
	subscribe(location, "sunset", rescheduleIfNeeded)
	subscribe(location, "mode", rescheduleIfNeeded)
	subscribe(location, "sunsetTime", rescheduleIfNeeded)

	rescheduleIfNeeded()   
}


def rescheduleIfNeeded(evt) {
	if (evt) log.debug("rescheduleIfNeeded>$evt.name=$evt.value")
	Integer delay = (24*60) // By default, do it every day
	BigDecimal currentTime = now()    
	BigDecimal lastPollTime = (currentTime - (state?.poll["last"]?:0))  
 
	if (lastPollTime != currentTime) {    
		Double lastPollTimeInMinutes = (lastPollTime/60000).toDouble().round(1)      
		log.info "rescheduleIfNeeded>last poll was  ${lastPollTimeInMinutes.toString()} minutes ago"
	}
	if (((state?.poll["last"]?:0) + (delay * 60000) < currentTime) && canSchedule()) {
		log.info "rescheduleIfNeeded>scheduling dailyRun in ${delay} minutes.."
		schedule("0 0 1 * * ?", dailyRun)    
	}
    
	// Update rescheduled state
    
	if (!evt) state.poll["rescheduled"] = now()
}
   

def appTouch(evt) {
	generateStats()
}



void dailyRun() {
	Integer delay = (24*60) // By default, do it every day
	state?.poll["last"] = now()
		
	//schedule the rescheduleIfNeeded() function
    
	if (((state?.poll["rescheduled"]?:0) + (delay * 60000)) < now()) {
		log.info "takeAction>scheduling rescheduleIfNeeded() in ${delay} minutes.."
		schedule("0 0 1 * * ?", rescheduleIfNeeded)    
		// Update rescheduled state
		state?.poll["rescheduled"] = now()
	}
	settings.givenStartDate=null
	settings.givenStartTime=null
	settings.givenEndDate=null
	settings.givenEndTime=null
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


void generateStats() {
    
	String dateInLocalTime = new Date().format("yyyy-MM-dd", location.timeZone) 
	String timezone = new Date().format("zzz", location.timeZone)
	String dateAtMidnight = dateInLocalTime + " 00:00 " + timezone    
	log.debug("generateStats>date at Midnight= ${dateAtMidnight}")
	Date endDate = formatDate(dateAtMidnight) 
	Date startDate = endDate -1
	Date yesterday = startDate-1    
	Date aWeekAgo= endDate -7    
        
	def givenStartDate = (settings.givenStartDate) ?: startDate.format("yyyy-MM-dd", location.timeZone)
	def givenStartTime=(settings.givenStartTime) ?:"00:00"    
	def dateTime = givenStartDate + " " + givenStartTime + " " + timezone
	startDate = formatDate(dateTime)
	log.debug("generateStats>start dateTime = ${dateTime}, startDate in UTC = ${startDate.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
    
	def givenEndDate = (settings.givenEndDate) ?: endDate.format("yyyy-MM-dd", location.timeZone) 
	def givenEndTime=(settings.givenEndTime) ?:"00:00"    
	dateTime = givenEndDate + " " + givenEndTime + " " + timezone
	endDate = formatDate(dateTime)
	log.debug("generateStats>end dateTime = ${dateTime}, endDate in UTC =${endDate.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")

	// Get the auxHeat1's runtime for startDate-endDate period
	def component = "auxHeat1"
	generateRuntimeReport(component,startDate, endDate)
	float runtimeTotalDaily = (ecobee.currentAuxHeat1RuntimeDaily) ? ecobee.currentAuxHeat1RuntimeDaily.toFloat().round(2):0
	if (detailedNotif ) {
		send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
	}     
    
	log.debug("generateStats>yesterday: startDate in UTC = ${yesterday.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
	generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
	float runtimeTotalYesterday = (ecobee.currentAuxHeat1RuntimeYesterday)? ecobee.currentAuxHeat1RuntimeYesterday.toFloat().round(2):0
	if (detailedNotif ) {
		send "ecobeeGenerateStats>generated $component's runtime stats for the day before=${runtimeTotalYesterday} min. for ${String.format('%tF', yesterday)}"
	}     
	log.debug("generateStats>weekly: startDate in UTC = ${aWeekAgo.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
	generateRuntimeReport(component,aWeekAgo, endDate,'weekly') // generate stats for the week
	float runtimeTotalAvgWeekly = (ecobee.currentAuxHeat1RuntimeAvgWeekly)? ecobee.currentAuxHeat1RuntimeAvgWeekly.toFloat().round(2):0
	if (detailedNotif ) {
		send "ecobeeGenerateStats>generated $component avg weekly's runtime  stats=${runtimeTotalAvgWeekly} min. since ${String.format('%tF', aWeekAgo)}"
	}     

	generateRuntimeReport(component,aWeekAgo, endDate,'weekly') // generate stats for the week
	runtimeTotalAvgWeekly = (ecobee.currentAuxHeat1RuntimeAvgWeekly)? ecobee.currentAuxHeat1RuntimeAvgWeekly.toFloat().round(2):0
	if (detailedNotif ) {
		send "ecobeeGenerateStats>generated $component avg weekly's runtime  stats=${runtimeTotalAvgWeekly} min. since ${String.format('%tF', aWeekAgo)}"
	}     


	int heatStages = ecobee.currentHeatStages.toInteger()
    
    
	if (heatStages >1) { 
    
//	Get the auxHeat2's runtime for startDate-endDate period
 	
		component = "auxHeat2"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentAuxHeat2RuntimeDaily)? ecobee.currentAuxHeat2RuntimeDaily.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     
		log.debug("generateStats>yesterday: startDate in UTC = ${yesterday.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentAuxHeat2RuntimeYesterday)? ecobee.currentAuxHeat2RuntimeYesterday.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats for the day before=${runtimeTotalYesterday} min. for ${String.format('%tF', yesterday)}"
		}     
		log.debug("generateStats>weekly: startDate in UTC = ${aWeekAgo.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
		generateRuntimeReport(component,aWeekAgo, endDate,'weekly') // generate stats for the week
		runtimeTotalAvgWeekly = (ecobee.currentAuxHeat2RuntimeAvgWeekly)? ecobee.currentAuxHeat2RuntimeAvgWeekly.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component avg weekly's runtime  stats=${runtimeTotalAvgWeekly} min. since ${String.format('%tF', aWeekAgo)}"
		}     
	}     

	if (heatStages >2) { 
    
//	Get the auxHeat3's runtime for startDate-endDate period
 	
		component = "auxHeat3"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentAuxHeat3RuntimeDaily)? ecobee.currentAuxHeat3RuntimeDaily.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     
		log.debug("generateStats>yesterday: startDate in UTC = ${yesterday.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for yesterday
		runtimeTotalYesterday = (ecobee.currentAuxHeat3RuntimeYesterday)? ecobee.currentAuxHeat3RuntimeYesterday.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats for the day before=${runtimeTotalYesterday} min. for ${String.format('%tF', yesterday)}"
		}     
		log.debug("generateStats>weekly: startDate in UTC = ${aWeekAgo.format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"))}")
		generateRuntimeReport(component,aWeekAgo, endDate,'weekly') // generate stats for the week
		runtimeTotalAvgWeekly = (ecobee.currentAuxHeat3RuntimeAvgWeekly)? ecobee.currentAuxHeat3RuntimeAvgWeekly.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component avg weekly's runtime  stats=${runtimeTotalAvgWeekly} min. since ${String.format('%tF', aWeekAgo)}"
		}     
	}     

// Get the compCool1's runtime for startDate-endDate period

	int coolStages = ecobee.currentCoolStages.toInteger()

	component = "compCool1"
	generateRuntimeReport(component,startDate, endDate)
	runtimeTotalDaily = (ecobee.currentCompCool1RuntimeDaily)? ecobee.currentCompCool1RuntimeDaily.toFloat().round(2):0
	if (detailedNotif) {
		send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
	}     
	generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for the day before
	runtimeTotalYesterday = (ecobee.currentCompCool1RuntimeYesterday)? ecobee.currentCompCool1RuntimeYesterday.toFloat().round(2):0
	if (detailedNotif ) {
		send "ecobeeGenerateStats>generated $component's runtime stats for the day before=${runtimeTotalYesterday} min. for ${String.format('%tF', yesterday)}"
	}     
	generateRuntimeReport(component,aWeekAgo, endDate,'weekly') // generate stats for the week
	runtimeTotalAvgWeekly = (ecobee.currentCompCool1RuntimeAvgWeekly)? ecobee.currentCompCool1RuntimeAvgWeekly.toFloat().round(2):0
	if (detailedNotif ) {
		send "ecobeeGenerateStats>generated $component avg weekly's runtime  stats=${runtimeTotalAvgWeekly} min. since ${String.format('%tF', aWeekAgo)}"
	}     
    
//	Get the compCool2's runtime for startDate-endDate period

	if (coolStages >1) { 
		component = "compCool2"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentCompCool2RuntimeDaily)? ecobee.currentCompCool2RuntimeDaily.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		} 
		generateRuntimeReport(component,yesterday, startDate,'yesterday') // generate stats for the day before
		runtimeTotalYesterday = (ecobee.currentCompCool2RuntimeYesterday)? ecobee.currentCompCool2RuntimeYesterday.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats for the day before=${runtimeTotalYesterday} min. for ${String.format('%tF', yesterday)}"
		}     
		generateRuntimeReport(component,aWeekAgo, endDate,'weekly') // generate stats for the week
		runtimeTotalAvgWeekly = (ecobee.currentCompCool2RuntimeAvgWeekly)? ecobee.currentCompCool2RuntimeAvgWeekly.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component avg weekly's runtime  stats=${runtimeTotalAvgWeekly} min. since ${String.format('%tF', aWeekAgo)}"
		}     
	} 


// 	Get the fan's runtime for startDate-endDate period
 	component = "fan"
	generateRuntimeReport(component,startDate, endDate)
	runtimeTotalDaily = (ecobee.currentFanRuntimeDaily)? ecobee.currentFanRuntimeDaily.toFloat().round(2):0
	if (detailedNotif) {
		send "ecobeeGenerateStats>generated $component's runtime Daily=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
	}     

	def hasDehumidifier = (ecobee.currentHasDehumidifier) ? ecobee.currentHasDehumidifier : 'false' 
	def hasHumidifier = (ecobee.currentHasHumidifier) ? ecobee.currentHasHumidifier : 'false' 
	def hasHrv = (ecobee.currentHasHrv)? ecobee.currentHasHrv : 'false' 
	def hasErv = (ecobee.currentHasErv)? ecobee.currentHasErv : 'false' 

	if (hasHumidifier=='true') {
	 	// Get the humidifier's runtime for startDate-endDate period
		component = "humidifier"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentHumidifierRuntimeDaily)? ecobee.currentHumidifierRuntimeDaily.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     
    
	}

	if (hasDehumidifier=='true') {
	// Get the dehumidifier's for startDate-endDate period
		component = "dehumidifier"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentDehumidifierRuntimeDaily)? ecobee.currentDehumidifierRuntimeDaily.toFloat().round(2):0
		if (detailedNotif ) {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     

	}
	if (hasHrv=='true' || hasErv=='true') {
 	// Get the ventilator's runtime for  startDate-endDate period
		component = "ventilator"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = (ecobee.currentVentilatorRuntimeDaily)? ecobee.currentVentilatorRuntimeDaily.toFloat().round(2):0
		if (detailedNotif) {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     

	}
	send "ecobeeGenerateStats>generated stats done for ${String.format('%tF', startDate)} - ${String.format('%tF', endDate)} period"

}

private void generateRuntimeReport(component, startDate, endDate, frequence='daily') {

	ecobee.getReportData("", startDate, endDate, null, null, component,false)
	ecobee.generateReportRuntimeEvents(component, startDate,endDate, 0, null,frequence)

}

private send(msg) {
	if ( sendPushMessage != "No" ) {
		log.debug( "sending push message" )
		sendPush( msg )
       
	}
	if ( phoneNumber ) {
		log.debug( "sending text message" )
 		sendSms( phoneNumber, msg )
	}

	log.debug msg
}

