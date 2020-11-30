/***
 *
 *  Copyright Yves Racine
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
 *  Override the user's manual hold settings
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
 import java.text.SimpleDateFormat
 
// Automatically generated. Make future change here.
definition(
	name: "${get_APP_NAME()}",
	namespace: "yracine",
	author: "Yves Racine",
	description: "The smartapp can override the user's manual hold settings at the thermostat unit and replace it with a hold with some end date/time (useful for rental units)",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

def get_APP_VERSION() {return "1.1"}


preferences {

	page(name: "About", title: "About", install: false , uninstall: true, nextPage: "selectThermostats") {
		section("") {
			paragraph image:"${getCustomImagePath()}ecohouse.jpg", "${get_APP_NAME()},The smartapp can override the user's hold settings at the thermostat unit and replace it with a hold with some end date/time (useful for rental units)"
			paragraph "Version ${get_APP_VERSION()}\n" + 
				"If you like this smartapp, please support the developer via PayPal and click on the Paypal link below\n" 
					href url: "https://www.paypal.me/ecomatiqhomes",
					title:"Paypal donation" 
			paragraph "Copyright©2018-2020 Yves Racine\n"
				href url:"http://www.maisonsecomatiq.com/#!home/mainPage", style:"embedded", required:false, title:"More information..."  
 					description: "http://www.maisonsecomatiq.com/#!home/mainPage"
		} /* end section about */
	}        
	page(name: "selectThermostats", title: "Select Thermostat for Hold Override, Choose one condition below", install: false , uninstall: false, nextPage: "Notifications") {
		section("Override any holds at this ecobee thermostat with a new ecobee's hold override") {
			input "ecobee", "capability.thermostat", title: "MyEcobee Thermostat"
		}
		section("Hold until the next transition at the ecobee schedule [default=false]") {
			input "nextTransitionFlag", "bool", title: "Until the next Transition?", required:false
		}
		section("Or Number of hours for the new hold override [default=no HoldHours]") {
			input "givenNbHr", "number", title: "Number of hours for the hold override", required:false
		}
		section("Or End date for the new Hold override [default=no datetime hold]") {
			input "givenEndDate", "text", title: "End Date, [format = YYYY-MM-DD], ex. 2019-01-01", required:false
			input "givenEndTime", "text", title: "End time,[HH:MM 24HR] ex. 23:05:00", required:false
		}
		section("Or get start/end dates from lock Manager API URL, current user is assumed to be in the first 10 slots [format: https://graph.api.smartthings.com/api/smartapps/installations/ID/api?access_token=ACCESS_TOKEN]") {
			input "APIUrl", "text", title: "lock Manager API URL?", required:false
		}
		section("What do I use for the Master on/off switch to enable/disable smartapp processing? [optional]") {
			input (name:"powerSwitch", type:"capability.switch", required: false, description: "Optional")
		}
		section("Set for specific ST location mode(s) [default=all]") {
			def enumModes=[]
			location.modes.each {
				enumModes << it.name
			}    
        
			input "selectedModes", "enum", options: enumModes, multiple:true, required: false
		}
     
	}        
    	page(name: "Notifications", title: "Notifications & Logging Options", install: true, uninstall: false) {
		if (isST()) {    
			section("") {
		    		input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required:
					false
				input "phoneNumber", "phone", title: "Send a text message?", required: false
    				input "detailedNotif", "bool", title: "Detailed Logging & Notifications?", required:false
	    			input "logFilter", "enum", title: "log filtering [Level 1=ERROR only,2=<Level 1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>]?",required:false, options:[1,2,3,4,5]
				          
			}
    			section("Enable Amazon Echo/Ask Alexa Notifications for events logging (optional)") {
	    			input (name:"askAlexaFlag", title: "Ask Alexa verbal Notifications [default=false]?", type:"bool",
		    			description:"optional",required:false)
			    	input (name:"listOfMQs",  type:"enum", title: "List of the Ask Alexa Message Queues (default=Primary)", options: state?.askAlexaMQ, multiple: true, required: false,
					description:"optional")            
    				input "AskAlexaExpiresInDays", "number", title: "Ask Alexa's messages expiration in days (optional,default=2 days)?", required: false
	    		}
        	}            
		section("Logging") {
    			input "detailedNotif", "bool", title: "Detailed Logging?", required:false
	    		input "logFilter", "enum", title: "log filtering [Level 1=ERROR only,2=<Level 1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>]?",required:false, options:[1,2,3,4,5]
				          
		}
		section([mobileOnly: true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
	}

}




def appTouch(evt) {
	traceEvent(settings.logFilter,"appTouch>location.mode= $location.mode, about to takeAction (manual appTouch)", detailedNotif)
	takeAction() 
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

def installed() {
	traceEvent(settings.logFilter, "Installed with settings: ${settings}", detailedNotif)
	initialize()
}


def initialize() {
	traceEvent(settings.logFilter, "initialize>about to execute with ${settings.inspect()}", detailedNotif)
	subscribe(location, changeMode)
	subscribe(app, appTouch)
	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler, [filterEvents: false])
		subscribe(powerSwitch, "switch.on", onHandler, [filterEvents: false])
	}
	subscribe(ecobee, "heatingSetpoint", heatingSetpointHandler)    
	subscribe(ecobee, "coolingSetpoint", coolingSetpointHandler)
	subscribe(ecobee, "programScheduleName", checkOverrideHandler)

}

def checkOverrideHandler(evt) {
	traceEvent(settings.logFilter,"checkOverrideHandler>evt=${evt.value}",settings.detailedNotif)
	def heatSP = ecobee.currentHeatingSetpoint
	def coolSP = ecobee.currentCoolingSetpoint
	String currentMode= ecobee.currentThermostatMode    
	if (evt.value.contains('hold')) { // if a temporary hold is set, then change the baselines
		if (currentMode in ['cool', 'auto', 'off']) {
			save_new_cool_baseline_value(coolSP)		        
			traceEvent(settings.logFilter,"checkOverrideHandler>new cooling baseline=$coolSP, manual hold set",settings.detailedNotif, get_LOG_INFO())
		}    
		if (currentMode in ['heat', 'auto', 'off']) {
			save_new_heat_baseline_value(heatSP)		        
			traceEvent(settings.logFilter,"checkOverrideHandler>new heating baseline=$heatSP, manual hold set",settings.detailedNotif, get_LOG_INFO())
		}   
		takeAction()        
	}
	if (evt.value.contains('_auto')) { // if climateRef auto is set, then change the baselines
		if ((currentMode in ['cool', 'auto', 'off']) && (state?.scheduleCoolSetpoint != coolSP)) {
			save_new_cool_baseline_value(coolSP)		        
			traceEvent(settings.logFilter,"checkOverrideHandler>new cooling baseline=$coolSP, climateRef= ${evt.value} set",settings.detailedNotif, get_LOG_INFO())
			        
		}    
		if ((currentMode in ['heat', 'auto', 'off']) && (state?.scheduleHeatSetpoint != heatSP)) {
			save_new_heat_baseline_value(heatSP)		        
			traceEvent(settings.logFilter,"checkOverrideHandler>new heating baseline=$heatSP, climateRef= ${evt.value} set",settings.detailedNotif, get_LOG_INFO())
		}   
	}
}    

void save_new_cool_baseline_value(coolSP) {
	state?.scheduleCoolSetpoint= coolSP
	traceEvent(settings.logFilter,"save_new_cool_baseline_value>new cooling baseline=$coolSP",settings.detailedNotif, get_LOG_INFO())
}

void save_new_heat_baseline_value(heatSP) {
	state?.scheduleHeatSetpoint= heatSP
	traceEvent(settings.logFilter,"save_new_heat_baseline_value>new heating baseline=$heatSP",settings.detailedNotif, get_LOG_INFO())
}

def updated() {
	unsubscribe()
	try {
		unschedule()
	} catch (e) {	
		traceEvent(settings.logFilter,"updated>exception $e while calling unschedule()",settings.detailedNotif, get_LOG_ERROR())
	}
	initialize() 
				        
}

def offHandler(evt) {
	traceEvent(settings.logFilter,"offHandler>$evt.name: $evt.value",settings.detailedNotif)
	takeAction()    
}

def onHandler(evt) {
	traceEvent(settings.logFilter,"onHandler>$evt.name: $evt.value",settings.detailedNotif)
	takeAction()
}

def heatingSetpointHandler(evt) {
	traceEvent(settings.logFilter,"heatingSetpointHandler>heating Setpoint now: $evt.value",settings.detailedNotif)
}
def coolingSetpointHandler(evt) {
	traceEvent(settings.logFilter,"coolingSetpointHandler>cooling Setpoint now: $evt.value",settings.detailedNotif)
}



def takeAction() {
	traceEvent(settings.logFilter,"takeAction>About to override user's hold  at ${ecobee} thermostat...", detailedNotif)

	def todayDay = new Date().format("dd",location.timeZone)
	if ((!state?.today) || (todayDay != state?.today)) {
		state?.exceptionCount=0   
		state?.sendExceptionCount=0        
		state?.today=todayDay        
	}   
    
	if (powerSwitch?.currentSwitch == "off") {
		traceEvent(settings.logFilter, "${powerSwitch.name} is off, no longer processing any hold till virtual switch is on...",true, get_LOG_INFO(),true)
		return
	}
	boolean foundMode=selectedModes.find{it == (location.currentMode as String)} 
	if ((selectedModes != null) && (!foundMode)) {
		traceEvent(settings.logFilter, "Not the right mode to run the smartapp, location.mode= $location.mode, selectedModes=${selectedModes},foundMode=${foundMode}, exiting",true, get_LOG_INFO(), true)
		return            
	}
    
	def heatSP = ecobee.currentHeatingSetpoint
	def coolSP = ecobee.currentCoolingSetpoint
	def fanMode= ecobee.currentValue("thermostatFanMode")
    
	// create a new hold with the current settings but with some end date/time
    
    
	def todayDate = new Date().format("yyyy-MM-dd",location.timeZone)
	def todayTime = new Date().format("HH:mm:ss",location.timeZone)
	if (nextTransitionFlag) {
//		ecobee.resumeProgram()
		ecobee.setHoldWithHoldType('',coolSP,heatSP, fanMode,'nextTransition' )
        
	} else if (givenNbHr && givenNbHr.isInteger()) {
//		ecobee.resumeProgram()
		ecobee.setHoldWithHoldType('',coolSP,heatSP, fanMode,'holdHours', givenNbHr )
	} else if (givenEndDate && givenEndDate instanceof String) {   
//		ecobee.resumeProgram()
		ecobee.setHoldWithHoldType('',coolSP,heatSP, fanMode,'dateTime', null,todayDate,todayTime,givenEndDate,givenEndTime )
	} else if (APIUrl) {
		int pos_https=APIUrl.indexOf("https://",0)    
		if (pos_https != -1) {        
			processAPIUrl(coolSP,heatSP, fanMode)
		} else {
			traceEvent(settings.logFilter,"$APIUrl is not a valid URL for lock Manager API, exiting",settings.detailedNotif,
				get_LOG_ERROR(),true)
		}        
	} else {
		traceEvent(settings.logFilter,"No specific hold Type input, exiting",settings.detailedNotif,
			get_LOG_WARN(),true)
		return    	
	}    
	def exceptionCheck= ecobee.currentVerboseTrace?.toString()
	if ((exceptionCheck) && ((exceptionCheck.contains("exception") || (exceptionCheck.contains("error"))))) {
		// check if there is any exception or an error reported in the verboseTrace associated to the device (except the ones linked to rate limiting).
		state?.exceptionCount= state?.exceptionCount+1     
		traceEvent(settings.logFilter,"Found exception/error while setting hold override, exceptionCount= ${state?.exceptionCount}: $exceptionCheck",settings.detailedNotif,
			get_LOG_ERROR(),true)
		return            
	} else {             
		// reset exception counter            
		state?.exceptionCount=0       
	}                
	if (nextTransitionFlag) {
		traceEvent(settings.logFilter,"Set the hold override at ${ecobee} with current heatSP=${heatSP},coolSP=${coolSP},fanMode=${fanMode} until next Transition at ecobee's schedule",settings.detailedNotif,
			get_LOG_INFO(),true)
	} else if (givenNbHr) {
		traceEvent(settings.logFilter,"Set the hold override at ${ecobee} with current heatSP=${heatSP},coolSP=${coolSP},fanMode=${fanMode} for the next ${givenNbHr} hours...",settings.detailedNotif,
			get_LOG_INFO(),true)
	} else if (givenEndDate) {   
		traceEvent(settings.logFilter,"Set the hold override at ${ecobee} with current heatSP=${heatSP},coolSP=${coolSP},fanMode=${fanMode},startDate=$todayDate,startTime=$todayTime,endDate=${givenEndDate},endTime=${givenEndTime}",settings.detailedNotif,
			get_LOG_INFO(),true)
	}             
	traceEvent(settings.logFilter,"takeAction>end", detailedNotif)

}

def processAPIUrl(coolSP,heatSP, fanMode) {
	traceEvent(settings.logFilter, "processAPIUrl>begin with coolSP=$coolSP, heatSP=$heatSP, fanMode=$fanMode")
	def SUCCESS_CODE=201

	def command='{get:[1,2,3,4,5,6,7,8,9,10]}'
    
	def method = [
		headers: [
			'Content-Type': "application/json",
			'charset': "UTF-8",
		],
		uri: "${APIUrl}",
		body: command // it's assumed that the current user is in the first 10 slots.
	]
    
	traceEvent(settings.logFilter,"processAPIUrl>command=$command, method=$method")    
	def currTime = now()	

	def successCall = {resp ->
		def statusCode = resp.status
		if (statusCode != SUCCESS_CODE) {
			traceEvent(settings.logFilter,"processAPIUrl>not able to send $method to API Lock Manager, code=${statusCode}", true, get_LOG_ERROR(),true)
            return false
		}
		resp.data.processed.each {
			def user= it.user
			def name= it.name
			def type = it.type 						// ex. "Permanent", "One time"
			def startDateString= it.startDate 		// ex. "2019-06-01"
			def expDateString= it.expDate 			// ex. "2019-08-02"
			def startTime = it.startTime  			// ex. "03:05"
			def expTime = it.expTime				// ex. "23:04",
			Date startDate=null,expDate=null

			String timezone = new Date().format("zzz", location.timeZone)
			if (startDateString && !startTime) {
				String dateAtMidnight = startDateString + " 00:00 " + timezone    
				startDate = formatDate(dateAtMidnight)
			}
			if (startDateString && startTime) {
				String dateString = startDateString + " $startTime " + timezone    
				startDate = formatDate(dateString)
			}
			if (expDateString && !expTime) {
				String dateAtMidnight = expDateString + " 00:00 " + timezone    
				endDate = formatDate(dateAtMidnight)
			}
			if (expDateString && expTime) {
				String dateString = expDateString + " $expTime " + timezone    
				endDate = formatDate(dateString)
			}
			traceEvent(settings.logFilter,"processAPIUrl>found user $user ($type) with startDate $startDateString/$startTime and endDate $expDateString/$expTime",settings.detailedNotif)
			traceEvent(settings.logFilter,"processAPIUrl>found user $user ($type), checking current time $currTime vs. startDate $startDate/endDate $endDate",settings.detailedNotif)
			if ((startDate && (currTime >= startDate)) && (endDate && (currTime <= endDate))) {
				
				traceEvent(settings.logFilter,"processAPIUrl>user $user, name $name is the current user, just set the override hold with coolSP=$coolSP,heatSP=$heatSP,fanMode=$fanMode till $expDateString $expTime",settings.detailedNotif,
					get_LOG_INFO(), true)
//				ecobee.resumeProgam()
				ecobee.setHoldWithHoldType('',coolSP,heatSP, fanMode,'dateTime', null,startDateString,startTime,expDateString,expTime )					                 
				return
			}                
		}        
		traceEvent(settings.logFilter,"processAPIUrl>not found any user with the rigth start & end dates/times",settings.detailedNotif)
        
	}

	try {
		traceEvent(settings.logFilter,"processAPIUrl>about to post..."  )  
		httpPostJson(method, successCall)
	} catch (java.net.UnknownHostException e) {
		traceEvent(settings.logFilter,"processAPIUrl> Unknown host for ${APIUrl}", settings.trace, get_LOG_ERROR())
	} catch (java.net.NoRouteToHostException e) {
		traceEvent(settings.logFilter,"processAPIUrl> Unknown host for ${APIUrl}", settings.trace, get_LOG_ERROR())
	} catch (e) {
		traceEvent(settings.logFilter,"processAPIUrl> exception $e for ${APIUrl}", settings.trace, get_LOG_ERROR())
	} 

}

private def formatDate(dateString) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz")
	Date aDate = sdf.parse(dateString)
	return aDate
}


def getCustomImagePath() {
	return "http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/"
}    

private def getStandardImagePath() {
	return "http://cdn.device-icons.smartthings.com"
}

private int get_LOG_ERROR()	{return 1}
private int get_LOG_WARN()	{return 2}
private int get_LOG_INFO()	{return 3}
private int get_LOG_DEBUG()	{return 4}
private int get_LOG_TRACE()	{return 5}

def traceEvent(filterLog, message, displayEvent=false, traceLevel=4, sendMessage=false) {
	int LOG_ERROR= get_LOG_ERROR()
	int LOG_WARN=  get_LOG_WARN()
	int LOG_INFO=  get_LOG_INFO()
	int LOG_DEBUG= get_LOG_DEBUG()
	int LOG_TRACE= get_LOG_TRACE()
	int filterLevel=(filterLog)?filterLog.toInteger():get_LOG_WARN()


	if (filterLevel >= traceLevel) {
		if (displayEvent) {    
			switch (traceLevel) {
				case LOG_ERROR:
					log.error "${message}"
				break
				case LOG_WARN:
					log.warn "${message}"
				break
				case LOG_INFO:
					log.info "${message}"
				break
				case LOG_TRACE:
					log.trace "${message}"
				break
				case LOG_DEBUG:
				default:            
					log.debug "${message}"
				break
			}                
		}			                
		if (sendMessage) send (message,settings.askAlexaFlag) //send message only when true
	}        
}


private send(msg, askAlexa=false) {
	int MAX_EXCEPTION_MSG_SEND=5

	// will not send exception msg when the maximum number of send notifications has been reached
	if ((msg.contains("exception")) || (msg.contains("error"))) {
		state?.sendExceptionCount=state?.sendExceptionCount+1         
		traceEvent(settings.logFilter,"checking sendExceptionCount=${state?.sendExceptionCount} vs. max=${MAX_EXCEPTION_MSG_SEND}", detailedNotif)
		if (state?.sendExceptionCount >= MAX_EXCEPTION_MSG_SEND) {
			traceEvent(settings.logFilter,"send>reached $MAX_EXCEPTION_MSG_SEND exceptions, exiting", detailedNotif)
			return        
		}        
	}    
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
		
	if (phoneNumber) {
		log.debug("sending text message")
		sendSms(phoneNumber, message)
	}
}

private def get_APP_NAME() {
	return "ecobeeTstatHoldOverride"
}
