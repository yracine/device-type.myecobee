/**
 *  MyEcobeeInit (Service Manager)
 *  Copyright 2016 Yves Racine
 *  LinkedIn profile: www.linkedin.com/in/yracine
 *  Refer to readme file for installation instructions.
 *     https://github.com/yracine/device-type.myecobee/blob/master/README.md
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights,
 *  trade secret in the Background technology. May be subject to consulting fees under an Agreement 
 *  between the Developer and the Customer. Developer grants a non exclusive perpetual license to use
 *  the Background technology in the Software developed for and delivered to Customer under this
 *  Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * 
 * Software Distribution is restricted and shall be done only with Developer's written approval.
 *
 *
**/
definition(
    name: "${get_APP_NAME()}",
    namespace: "yracine",
    author: "Yves Racine",
    description: "Connect your Ecobee thermostat to SmartThings.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)
preferences {
	page(name: "about", title: "About", nextPage: "auth")
	page(name: "auth", title: "ecobee", content:"authPage", nextPage:"deviceList")
	page(name: "deviceList", title: "ecobee", content:"ecobeeDeviceList",nextPage: "otherSettings")
	page(name: "otherSettings", title: "Other Settings", content:"otherSettings", install:true)
}

mappings {
    path("/oauth/initialize") {action: [GET: "oauthInitUrl"]}
    path("/oauth/callback") {action: [GET: "callback"]}
}

def about() {
 	dynamicPage(name: "about", install: false, uninstall: true) {
 		section("About") {	
			paragraph "${get_APP_NAME()}, the smartapp that connects your Ecobee thermostat to SmartThings via cloud-to-cloud integration"
			paragraph "Version 3.1.6\n" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
					title:"Paypal donation..."
			paragraph "Copyright�2014 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information...", 
					description: "http://github.com/yracine/device-type.myecobee"
		}
	}        
}

def otherSettings() {
	dynamicPage(name: "otherSettings", title: "Other Settings", install: true, uninstall: false) {
		section("Polling at which interval in minutes (range=[5,10,15,30],default=20 min.)?") {
			input "givenInterval", "number", title:"Interval", required: false
		}
		section("Handle/Notify any exception proactively [default=false, you will not receive any exception notification]") {
			input "handleExceptionFlag", "bool", title: "Handle exceptions proactively?", required: false
		}
		section("Watchdogs") {
			input (name:"tempSensor", type:"capability.temperatureMeasurement", title: "What do I use as temperature sensor to restart smartapp processing?",
				required: false, description: "Optional Watchdog- just use a single regular polling watchdog")
			input (name:"motionSensor", type:"capability.motionSensor", title: "What do I use as a motion sensor to restart smartapp processing?",
				required: false, description: "Optional Watchdog -just use a single regular polling watchdog")
			input (name:"energyMeter", type:"capability.powerMeter", title: "What do I use as energy sensor to restart smartapp processing?",
				required: false, description: "Optional Watchdog-  just use a single regular polling watchdog")
			input (name:"powerSwitch", type:"capability.switch", title: "What do I use as Master on/off switch to restart smartapp processing?",
				required: false, description: "Optional Watchdog - just use a single regular polling watchdog")
		}
		section("Notifications & Logging") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required:
				false
			input("recipients", "contact", title: "Send notifications to", required: false)
			input "phoneNumber", "phone", title: "Send a text message?", required: false
			input "notifyAlerts", "bool", title: "Daily Notifications for any ecobee Alerts?", required:false
			input "detailedNotif", "bool", title: "Detailed Logging & Notifications?", required:false
			input "logFilter", "enum", title: "log filtering [Level 1=ERROR only,2=<Level 1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>]?", required:false, metadata: [values: [1,2,3,4,5]]
		}
		section("Enable Amazon Echo/Ask Alexa Notifications [optional, default=false]") {
			input (name:"askAlexaFlag", title: "Ask Alexa verbal Notifications?", type:"bool",
				description:"optional",required:false)
		}
		section([mobileOnly:true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
	}
}

def authPage() {
	traceEvent(settings.logFilter,"authPage(),atomicState.oauthTokenProvided=${atomicState?.auth?.oauthTokenProvided}", detailedNotif)

	if (!atomicState.accessToken) {
		traceEvent(settings.logFilter,"about to create access token", detailedNotif)
		createAccessToken()
		atomicState.accessToken = state.accessToken
	}

	def description = "Required"
	def uninstallAllowed = false

	if (atomicState.authToken) {

		// TODO: Check if it's valid
		if (true) {
			description = "You are connected."
			uninstallAllowed = true
			atomicState?.oauthTokenProvided=true            
		} else {
			description = "Required" // Worth differentiating here vs. not having atomicState.authToken? 
		}
	}

	def redirectUrl = "${get_ST_URI_ROOT()}/oauth/initialize?appId=${app.id}&access_token=${atomicState.accessToken}&apiServerUrl=${getServerUrl()}"

	traceEvent(settings.logFilter,"authPage>atomicState.authToken=${atomicState.authToken},atomicState.oauthTokenProvided=${atomicState?.oauthTokenProvided}, RedirectUrl = ${redirectUrl}",
		detailedNotif)


	// get rid of next button until the user is actually auth'd

	if (!atomicState?.oauthTokenProvided) {

		return dynamicPage(name: "auth", title: "Login", nextPage:null, uninstall:uninstallAllowed, submitOnChange: true) {
			section(){
				paragraph "Tap below to log in to the ecobee portal and authorize SmartThings access. Be sure to scroll down on page 2 and press the 'Allow' button."
				href url:redirectUrl, style:"embedded", required:true, title:"ecobee>", description:description
			}
		}

	} else {

		return dynamicPage(name: "auth", title: "Log In", nextPage:"deviceList", uninstall:uninstallAllowed,submitOnChange: true) {
			section(){
				paragraph "Tap Next to continue to setup your thermostats."
				href url:redirectUrl, style:"embedded", state:"complete", title:"ecobee", description:description
			}
		}

	}


}

def ecobeeDeviceList() {
	traceEvent(settings.logFilter,"ecobeeDeviceList>begin", detailedNotif, get_LOG_TRACE())

	def stats = getEcobeeThermostats()

	traceEvent(settings.logFilter,"ecobeeDeviceList>device list: $stats", detailedNotif)

	def ems = getEcobeeThermostats("ems")


	stats = stats + ems
	traceEvent(settings.logFilter,"ecobeeDeviceList>device list: $stats", detailedNotif)
	def p = dynamicPage(name: "deviceList", title: "Select Your Thermostats [3 max].", uninstall: true) {
		section(""){
			paragraph "Tap below to see the list of ecobee thermostats available in your ecobee account. If you have disconnect issues with your ST account, select only 1 tstat and create 1 instance of MyEcobeeInit per tstat"
			input(name: "thermostats", title:"", type: "enum", required:true, multiple:true, description: "Tap to choose", metadata:[values:stats])
		}
	}

	traceEvent(settings.logFilter,"list p: $p",detailedNotif)
	return p
}


def setParentAuthTokens(auth_data) {
	if (auth_data.authexptime > atomicState.authexptime) {
		if (handleException) {
/*
			For Debugging purposes, due to the fact that logging is not working when called (separate thread)
			send("MyEcobeeInit>setParentAuthTokens>begin auth_data: $auth_data")
*/
			traceEvent(settings.logFilter,"setParentAuthTokens>begin auth_data: $auth_data",detailedNotif)
		}            
		atomicState.refreshToken = auth_data?.refresh_token
		atomicState.authToken = auth_data?.access_token
		atomicState.expiresIn=auth_data?.expires_in
		atomicState.tokenType = auth_data?.token_type
		atomicState.authexptime= auth_data?.authexptime
		refreshAllChildAuthTokens()
		if (handleException) {
/*
			For Debugging purposes, due to the fact that logging is not working when called (separate thread)
			send("MyEcobeeInit>setParentAuthTokens>atomicState =$atomicState")
*/
			traceEvent(settings.logFilter,"setParentAuthTokens>setParentAuthTokens>atomicState =$atomicState",detailedNotif)
		}            
	}        

}

def refreshAllChildAuthTokens() {
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	traceEvent(settings.logFilter,"refreshAllChildAuthTokens>begin updating children with ${atomicState.auth}")
*/

	def children= getChildDevices()
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	traceEvent(settings.logFilter,"refreshAllChildAuthtokens> refreshing ${children.size()} thermostats",detailedNotif)
*/

	children.each { 
/*
		For Debugging purposes, due to the fact that logging is not working when called (separate thread)
		traceEvent(settings.logFilter,"refreshAllChildAuthTokens>begin updating $it.deviceNetworkId with ${$atomicState.auth}",detailedNotif)
*/
    	it.refreshChildTokens(atomicState) 
	}
}
def refreshThisChildAuthTokens(child) {
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	traceEvent(settings.logFilter,"refreshThisChildAuthTokens>begin child id: ${child.device.deviceNetworkId}, updating it with ${atomicState}", detailedNotif)
*/
	child.refreshChildTokens(atomicState)

/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	send("refreshThisChildAuthTokens>end")
*/
}

boolean refreshParentTokens() {

	if (isTokenExpired()) {
		if (refreshAuthToken()) {
			refreshAllChildAuthTokens()
			return true            
		}		        
	} else {
		refreshAllChildAuthTokens()
		return true            
	}    
	return false    
    
}
def getEcobeeThermostats(String type="") {
	traceEvent(settings.logFilter,"getEcobeeThermostats>begin getting ecobee Thermostats list", detailedNotif)
	def msg
    
	def requestBody

	if ((type == "") || (type == null)) {
    
	 	requestBody = '{"selection":{"selectionType":"registered","selectionMatch":""}}'
	} else {
		requestBody = '{"selection":{"selectionType":"managementSet","selectionMatch":"/"}}'
	}    
    
	traceEvent(settings.logFilter,"getEcobeeThermostats>requestBody=${requestBody}", detailedNotif)
	def deviceListParams = [
		uri: "${get_URI_ROOT()}",
		path: "/1/thermostat",
		headers: ["Content-Type": "text/json", "Authorization": "Bearer ${atomicState.authToken}"],
		query: [format: 'json', body: requestBody]
	]

	traceEvent(settings.logFilter,"getEcobeeThermostats>device list params: $deviceListParams",detailedNotif)

	def stats = [:]
	try {
		httpGet(deviceListParams) { resp ->

			if (resp.status == 200) {
				resp.data.thermostatList.each { stat ->
					def dni = [ app.id, stat.name, stat.identifier ].join('.')
					traceEvent(settings.logFilter,"getEcobeeThermostats>found ${stat.name}...", detailedNotif)
					stats[dni] = stat.name
				}
				                
			} else {
				traceEvent(settings.logFilter,"getEcobeeThermostats>http status: ${resp.status}",detailedNotif)

				//refresh the auth token
				if (resp.status == 500 || resp.data.status.code == 14) {
					if (handleException) {            
						traceEvent(settings.logFilter,"getEcobeeThermostats>Need to refresh your auth_token!, about to call refreshAuthToken()",detailedNotif)
					}                        
					refreshAuthToken()
                    
				} else {
					if (handleException) {            
						traceEvent(settings.logFilter,"getEcobeeThermostats>http status=${resp.status}: authentication error, invalid authentication method, lack of credentials, (resp status= ${resp.data.status.code})",
							true, LOG_ERROR)
					}                        
				}
			}
    	}        
	} catch (java.net.UnknownHostException e) {
		msg ="Unknown host - check the URL " + deviceListParams.uri
		traceEvent(settings.logFilter,msg, true, get_LOG_ERROR())   
	} catch (java.net.NoRouteToHostException t) {
		msg= "No route to host - check the URL " + deviceListParams.uri
		traceEvent(settings.logFilter,msg, true, get_LOG_ERROR())   
	} catch (java.io.IOException e) {
		traceEvent(settings.logFilter,"getEcobeeThermostats>$e while getting list of thermostats, probable cause: not the right account for this type (${type}) of thermostat " +
			deviceListParams, detailedNotif, get_LOG_WARN())            
	} catch (e) {
		msg= "exception $e while getting list of thermostats" 
		if (handleException) {            
			traceEvent(settings.logFilter,msg, true, get_LOG_ERROR())   
		}                        
	}

	traceEvent(settings.logFilter,"thermostats: $stats", detailedNotif)

	return stats
}


def refreshAuthToken() {
	traceEvent(settings.logFilter,"refreshAuthToken>about to refresh auth token", detailedNotif)
	boolean result=false
	def REFRESH_SUCCESS_CODE=200    
	def UNAUTHORIZED_CODE=401    
    
	def stcid = getSmartThingsClientId()

	def refreshParams = [
			method: 'POST',
			uri   : "${get_URI_ROOT()}",
			path  : "/token",
			query : [grant_type: 'refresh_token', code: "${atomicState.refreshToken}", client_id: stcid]
	]


	def jsonMap
    
	try {
    
		httpPost(refreshParams) { resp ->

			if (resp.status == REFRESH_SUCCESS_CODE) {
				traceEvent(settings.logFilter,"refreshAuthToken>Token refresh done resp = ${resp}", detailedNotif)

				jsonMap = resp.data

				if (resp.data) {

					traceEvent(settings.logFilter,"refreshAuthToken>resp.data",detailedNotif)
					atomicState.refreshToken = resp?.data?.refresh_token
					atomicState.authToken = resp?.data?.access_token
					atomicState.expiresIn=resp?.data?.expires_in
					atomicState.tokenType = resp?.data?.token_type
					def authexptime = new Date((now() + (resp?.data?.expires_in  * 1000))).getTime()
					atomicState.authexptime=authexptime 						                        
					traceEvent(settings.logFilter,"refreshAuthToken>new refreshToken = ${atomicState.refreshToken}", detailedNotif)
					traceEvent(settings.logFilter,"refreshAuthToken>new authToken = ${atomicState.authToken}", detailedNotif)
					if (handleException) {                        
						traceEvent(settings.logFilter,"MyEcobeeInit>refreshAuthToken>,new authToken = ${atomicState.authToken}", detailedNotif)
						traceEvent(settings.logFilter,"refreshAuthToken>new authexptime = ${atomicState.authexptime}", detailedNotif)
					}                            
					traceEvent(settings.logFilter,"refreshAuthToken>new authexptime = ${atomicState.authexptime}", detailedNotif)
					result=true                    

				} /* end if resp.data */
			} else { 
				result=false                    
				traceEvent(settings.logFilter,"refreshAuthToken>refreshAuthToken failed ${resp.status} : ${resp.status.code}", detailedNotif)
				if (handleException) {            
					traceEvent(settings.logFilter,"refreshAuthToken failed ${resp.status} : ${resp.status.code}", detailedNotif)
				} /* end handle expception */                        
			} /* end if resp.status==200 */
		} /* end http post */
	} catch (groovyx.net.http.HttpResponseException e) {
			atomicState.exceptionCount=atomicState.exceptionCount+1             
			if (e.statusCode == UNAUTHORIZED_CODE) { //this issue might comes from exceed 20sec app execution, connectivity issue etc
				log.error "refreshAuthToken>exception $e"
				if (handleException) {            
					traceEvent(settings.logFilter,"refreshAuthToken>exception $e", detailedNotif,get_LOG_ERROR())
				}            
			}            
	}    
    
    return result
}



def installed() {
	settings.detailedNotif=true 		// initial value
	settings.logFilter=get_LOG_DEBUG()	// initial value
	traceEvent(settings.logFilter,"Installed with settings: ${settings}", detailedNotif)
	initialize()
}

def updated() {
	traceEvent(settings.logFilter,"Updated with settings: ${settings}", detailedNotif)

	unsubscribe()
	try {    
		unschedule()
	} catch (e) {
		traceEvent(settings.logFilter,"updated>exception $e, continue processing", detailedNotif)    
	}    
	initialize()
}

def offHandler(evt) {
	traceEvent(settings.logFilter,"$evt.name: $evt.value", detailedNotif)
}



def rescheduleHandler(evt) {
	traceEvent(settings.logFilter,"$evt.name: $evt.value", detailedNotif)
	rescheduleIfNeeded()		
}


private def delete_child_devices() {
	def delete
    
	// Delete any that are no longer in settings

	if(!thermostats) {
		traceEvent(settings.logFilter,"delete_child_devices>delete all ecobobee thermostats", detailedNotif)
		delete = getAllChildDevices()
	} else {
		delete = getChildDevices().findAll { !thermostats.contains(it.deviceNetworkId) }
	}


	delete.each { 
		try {    
			deleteChildDevice(it.deviceNetworkId) 
		} catch (e) {
			traceEvent(settings.logFilter,"delete_child_devices>exception $e while deleting ecobee thermostat ${it.deviceNetworkId}", detailedNotif, get_LOG_ERROR())
		}   
	}
	traceEvent(settings.logFilter,"delete_child_devices>deleted ${delete.size()} ecobee thermostats", detailedNotif)


}

private def create_child_devices() {

   	int countNewChildDevices =0
	def devices = thermostats.collect { dni ->

		def d = getChildDevice(dni)
		traceEvent(settings.logFilter,"create_child_devices>looping thru thermostats, found id $dni", detailedNotif)

		if(!d) {
			def tstat_info  = dni.tokenize('.')
			def thermostatId = tstat_info.last()
 			def name = tstat_info[1]
			def labelName = 'My ecobee ' + "${name}"
			traceEvent(settings.logFilter,"create_child_devices>about to create child device with id $dni, thermostatId = $thermostatId, name=  ${name}", detailedNotif)
			d = addChildDevice(getChildNamespace(), getChildName(), dni, null,
				[label: "${labelName}"]) 
			d.initialSetup( getSmartThingsClientId(), atomicState, thermostatId ) 	// initial setup of the Child Device
			traceEvent(settings.logFilter,"create_child_devices>created ${labelName} with id $dni", detailedNotif)
			countNewChildDevices++            
		} else {
			traceEvent(settings.logFilter,"create_child_devices>found ${d.displayName} with id $dni already exists", detailedNotif)
			try {
				if (d.isTokenExpired()) {  // called refreshAllChildAuthTokens when updated
 					refreshAllChildAuthTokens()    
				}
			} catch (e) {
				traceEvent(settings.logFilter,"create_child_devices>exception $e while trying to refresh existing tokens in child $d", detailedNotif, get_LOG_ERROR())
            
			}            
		}

	}

	traceEvent(settings.logFilter,"create_child_devices>created $countNewChildDevices, total=${devices.size()} thermostats", detailedNotif)
	    

}

def initialize() {
    
	traceEvent(settings.logFilter,"initialize begin...", detailedNotif)
	atomicState?.exceptionCount=0    
	atomicState?.poll = [ last: 0, rescheduled: now() ]
    
	Integer delay = givenInterval ?: 20 // By default, do it every 20 min.
	delete_child_devices()	
	create_child_devices()
    
	//Subscribe to different events (ex. sunrise and sunset events) to trigger rescheduling if needed
	subscribe(location, "sunrise", rescheduleIfNeeded)
	subscribe(location, "sunset", rescheduleIfNeeded)
	subscribe(location, "mode", rescheduleIfNeeded)
	subscribe(location, "sunriseTime", rescheduleIfNeeded)
	subscribe(location, "sunsetTime", rescheduleIfNeeded)
	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", rescheduleHandler, [filterEvents: false])
		subscribe(powerSwitch, "switch.on", rescheduleHandler, [filterEvents: false])
	}
	if (tempSensor)	{
		subscribe(tempSensor,"temperature", rescheduleHandler,[filterEvents: false])
	}
	if (motionSensor)	{
		subscribe(motionSensor,"motion", rescheduleHandler,[filterEvents: false])
	}
	if (energyMeter)	{
		subscribe(energyMeter,"energy", rescheduleHandler,[filterEvents: false])
	}

	subscribe(app, appTouch)

	traceEvent(settings.logFilter,"initialize>polling delay= ${delay}...", detailedNotif)
	rescheduleIfNeeded()   
	atomicState?.alerts=[:]   
}

def appTouch(evt) {
	rescheduleIfNeeded()
}

def rescheduleIfNeeded(evt) {
	if (evt) traceEvent(settings.logFilter,"rescheduleIfNeeded>$evt.name=$evt.value", detailedNotif)
	Integer delay = givenInterval ?: 5 // By default, do it every 5 min.
	BigDecimal currentTime = now()    
	BigDecimal lastPollTime = (currentTime - (atomicState?.poll["last"]?:0))  
	if (lastPollTime != currentTime) {    
		Double lastPollTimeInMinutes = (lastPollTime/60000).toDouble().round(1)      
		traceEvent(settings.logFilter,"rescheduleIfNeeded>last poll was  ${lastPollTimeInMinutes.toString()} minutes ago", detailedNotif)
	}
	if (((atomicState?.poll["last"]?:0) + (delay * 60000) < currentTime) && canSchedule()) {
		traceEvent(settings.logFilter,"rescheduleIfNeeded>scheduling takeAction in ${delay} minutes..", detailedNotif)
		if ((delay >=5) && (delay <10)) {      
			runEvery5Minutes(takeAction)
		} else if ((delay >=10) && (delay <15)) {  
			runEvery10Minutes(takeAction)
		} else if ((delay >=15) && (delay <30)) {  
			runEvery15Minutes(takeAction)
		} else {  
			runEvery30Minutes(takeAction)
		}
		takeAction()
	}
    
    
	// Update rescheduled state
    
	if (!evt) atomicState.poll["rescheduled"] = now()
}



def takeAction() {
	traceEvent(settings.logFilter,"takeAction>begin", detailedNotif,get_LOG_TRACE())
	def exceptionCheck    
	def MAX_EXCEPTION_COUNT=10
	boolean handleException = (handleExceptionFlag)?: false
	def todayDay = new Date().format("dd",location.timeZone)
	def alertsInfo    
    
	if ((!atomicState?.today) || (todayDay != atomicState?.today)) {
		atomicState?.alerts=[:] // reinitialize the alerts & exceptionCount every day
		atomicState?.exceptionCount=0   
		atomicState?.sendExceptionCount=0        
		atomicState?.today=todayDay        
	}   
	Integer delay = givenInterval ?: 5 // By default, do it every 5 min.
	atomicState?.poll["last"] = now()
		
	//schedule the rescheduleIfNeeded() function
    
	if (((atomicState?.poll["rescheduled"]?:0) + (delay * 60000)) < now()) {
		traceEvent(settings.logFilter,"takeAction>scheduling rescheduleIfNeeded() in ${delay} minutes..",true,get_LOG_INFO())
		unschedule()        
		schedule("0 0/${delay} * * * ?", rescheduleIfNeeded)
		// Update rescheduled state
		atomicState?.poll["rescheduled"] = now()
	}
    
    

	def devices = thermostats.collect { dni ->
		def d = getChildDevice(dni)
		if (d) { 
			traceEvent(settings.logFilter,"takeAction>Looping thru thermostats, found id $dni, about to poll ${d.displayName}",true, get_LOG_INFO())
			d.poll()
			exceptionCheck = d.currentVerboseTrace.toString()
			if (handleException) {            
				if ((exceptionCheck) && ((exceptionCheck.contains("exception") || (exceptionCheck.contains("error")) && 
					(!exceptionCheck.contains("Java.util.concurrent.TimeoutException")) && (!exceptionCheck.contains("No signature of method: physicalgraph.device.CommandService.executeAction")) &&
					(!exceptionCheck.contains("UndeclaredThrowableException"))))) {  
				// check if there is any exception or an error reported in the verboseTrace associated to the device (except the ones linked to rate limiting).
					atomicState.exceptionCount=atomicState.exceptionCount+1    
					traceEvent(settings.logFilter,"found exception/error after polling, exceptionCount= ${atomicState?.exceptionCount}: $exceptionCheck", true, get_LOG_ERROR(), true) 
				} else {             
					// reset exception counter            
					atomicState?.exceptionCount=0      
				}                
			}   /* end if handleException */
        
			// check for ecobee alerts        
			def alerts = d.currentValue("alerts")
			if (alerts) {
				alertsInfo= alerts.split(',')
			}
			def alertsSoFar=atomicState?.alerts        
			if ((alerts && alertsInfo) && (alerts.toUpperCase() != 'NONE')) {
				traceEvent(settings.logFilter,"found some ecobee alerts: ${alertsInfo} at ${d.displayName}", detailedNotif, get_LOG_INFO())
				alertsInfo.each {         
					traceEvent(settings.logFilter,"new alert ${it}, Alerts stored so far for today: ${alertsSoFar}", detailedNotif)
					String query = "name==${d.displayName} && type==${it}"                
					if ((!alertsSoFar) || (!(alertsSoFar.findAll{query}))) {
						// If the atomicState variable has not reported this alert for the given thermostat yet             
						d.getAlertText(it) // get the alert text
						def alertText= d.currentValue("alertText")
						if (alertText) {
							traceEvent(settings.logFilter,"${alertText} at ${d.displayName}", true, get_LOG_INFO(), notifyAlerts)
						}     
						alertsSoFar = alertsSoFar + [name: d.displayName, type:it]
						atomicState?.alerts=alertsSoFar                    
						traceEvent(settings.logFilter,"atomicState.alerts for today (${atomicState?.today}) after alert processing of ${it}: ${atomicState?.alerts}", detailedNotif)
					} // if !alertsSoFar                       
				} // for each alert            
			} // if alerts            
			if (handleException) {    
				if ((exceptionCheck) && (exceptionCheck.contains("Unauthorized")) && (atomicState?.exceptionCount>=MAX_EXCEPTION_COUNT)) {
					// need to authenticate again    
					atomicState.authToken=null                    
					atomicState?.oauthTokenProvided=false
					traceEvent(msg="$exceptionCheck after ${atomicState?.exceptionCount} errors, press on 'ecobee' and re-login..." , true, get_LOG_ERROR())
				} else if (atomicState?.exceptionCount==MAX_EXCEPTION_COUNT) {
					traceEvent(settings.logFilter,"too many exceptions/errors, $exceptionCheck (${atomicState?.exceptionCount} errors so far), you may need to press on 'ecobee' and re-login..." ,
						true, get_LOG_ERROR(), true)
				}
			} /* end if handleException */        

		} // if (d)         
	}
	traceEvent(settings.logFilter,"takeAction>end", detailedNotif, get_LOG_TRACE())

}



def isTokenExpired() {
	def buffer_time_expiration=5  // set a 5 min. buffer time before token expiration to avoid auth_err 
	def time_check_for_exp = now() + (buffer_time_expiration * 60 * 1000);
	traceEvent(settings.logFilter,"isTokenExpired>expiresIn timestamp: ${atomicState?.auth?.authexptime} > timestamp check for exp: ${time_check_for_exp}?", detailedNotif)
	if (atomicState?.authexptime > time_check_for_exp) {
		traceEvent(settings.logFilter,"isTokenExpired>not expired", detailedNotif)
//		send "isTokenExpired>not expired in MyEcobeeInit"
		return false
	}
	traceEvent(settings.logFilter,"isTokenExpired>expired", detailedNotif)
//	send "isTokenExpired>expired in MyEcobeeInit"
	return true    
}


def oauthInitUrl() {
	traceEvent(settings.logFilter,"oauthInitUrl>begin", detailedNotif)
	def stcid = getSmartThingsClientId();

	atomicState.oauthInitState = UUID.randomUUID().toString()

	def oauthParams = [
		response_type: "code",
		scope: "ems,smartWrite",
		client_id: stcid,
		state: atomicState.oauthInitState,
		redirect_uri: "${get_ST_URI_ROOT()}/oauth/callback"
	]

	redirect(location: "${get_URI_ROOT()}/authorize?${toQueryString(oauthParams)}")
}


def callback() {
	traceEvent(settings.logFilter,"callback>swapping token: $params", detailedNotif)

	def code = params.code
	def oauthState = params.state
	// Validate the response from the 3rd party by making sure oauthState == atomicState.oauthInitState as expected
	if (oauthState == atomicState.oauthInitState){

		def stcid = getSmartThingsClientId()

		def tokenParams = [
			grant_type: "authorization_code",
			code: params.code,
			client_id: stcid,
			redirect_uri: "${get_ST_URI_ROOT()}/oauth/callback"            
		]
		def tokenUrl = "${get_URI_ROOT()}/token?" + toQueryString(tokenParams)

		traceEvent(settings.logFilter,"callback>Swapping token $params", detailedNotif)

		def jsonMap
		httpPost(uri:tokenUrl) { resp ->
			jsonMap = resp.data
			atomicState.refreshToken = jsonMap.refresh_token
			atomicState.authToken = jsonMap.access_token
			atomicState.expiresIn=jsonMap.expires_in
			atomicState.tokenType = jsonMap.token_type
			def authexptime = new Date((now() + (jsonMap.expires_in * 1000))).getTime()
			atomicState.authexptime = authexptime
		}
		success()

    } else {
        traceEvent(settings.logFilter,"callback() failed. Validation of state did not match. oauthState != state.oauthInitState", true, get_LOG_ERROR())
    }

}

def success() {

	def html = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=640">
<title>Withings Connection</title>
<style type="text/css">
	@font-face {
		font-family: 'Swiss 721 W01 Thin';
		src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
		src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
			 url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
			 url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
			 url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
		font-weight: normal;
		font-style: normal;
	}
	@font-face {
		font-family: 'Swiss 721 W01 Light';
		src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
		src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
			 url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
			 url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
			 url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
		font-weight: normal;
		font-style: normal;
	}
	.container {
		width: 560px;
		padding: 40px;
		/*background: #eee;*/
		text-align: center;
	}
	img {
		vertical-align: middle;
	}
	img:nth-child(2) {
		margin: 0 30px;
	}
	p {
		font-size: 2.2em;
		font-family: 'Swiss 721 W01 Thin';
		text-align: center;
		color: #666666;
		padding: 0 40px;
		margin-bottom: 0;
	}
/*
	p:last-child {
		margin-top: 0px;
	}
*/
	span {
		font-family: 'Swiss 721 W01 Light';
	}
</style>
</head>
<body>
	<div class="container">
		<img src="https://s3.amazonaws.com/smartapp-icons/Partner/ecobee%402x.png" width="216" height="216" alt="ecobee icon" />
		<img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/connected-device-icn%402x.png" alt="connected device icon" />
		<img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" />
		<p>Your ecobee Account is now connected to SmartThings!</p>
		<p>Click 'Done' to finish setup.</p>
	</div>
</body>
</html>
"""

	render contentType: 'text/html', data: html
}


def fail() {
	def message = """
		<p>There was an error connecting your ecobee account with SmartThings</p>
		<p>Please try again.</p>
	"""
	displayMessageAsHtml(message)
}

def displayMessageAsHtml(message) {
	def html = """
		<!DOCTYPE html>
		<html>
			<head>
			</head>	
			<body>
				<div>
					${message}
				</div>
			</body>
		</html>
	"""
	render contentType: 'text/html', data: html
}

def getChildDeviceIdsString() {
	return thermostats.collect { it.split(/\./).last() }.join(',')
}

def toJson(Map m) {
	return new org.codehaus.groovy.grails.web.json.JSONObject(m).toString()
}

def toQueryString(Map m) {
	return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

def getChildNamespace() { "yracine" }
def getChildName() { "My Ecobee Device" }

def getServerUrl() { return getApiServerUrl()  }

def getSmartThingsClientId() { "qqwy6qo0c2lhTZGytelkQ5o8vlHgRsrO" }

private def get_URI_ROOT() {
	return "https://api.ecobee.com"
}
private def get_ST_URI_ROOT() {
	return "https://graph.api.smartthings.com"
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
	if (msg.contains("exception")) {
		atomicState?.sendExceptionCount=atomicState?.sendExceptionCount+1         
		traceEvent(settings.logFilter,"checking sendExceptionCount=${atomicState?.sendExceptionCount} vs. max=${MAX_EXCEPTION_MSG_SEND}", detailedNotif)
		if (atomicState?.sendExceptionCount >= MAX_EXCEPTION_MSG_SEND) {
			traceEvent(settings.logFilter,"send>reached $MAX_EXCEPTION_MSG_SEND exceptions, exiting", detailedNotif)
			return        
		}        
	}    
	def message = "${get_APP_NAME()}>${msg}"


	if (sendPushMessage != "No") {
		if (location.contactBookEnabled && recipients) {
			traceEvent(settings.logFilter,"contact book enabled", false, get_LOG_INFO())
			sendNotificationToContacts(message, recipients)
    	} else {
			traceEvent(settings.logFilter,"contact book not enabled", false, get_LOG_INFO())
			sendPush(message)
		}            
	}
	if (askAlexa) {
		sendLocationEvent(name: "AskAlexaMsgQueue", value: "${get_APP_NAME()}", isStateChange: true, descriptionText: msg)        
	}        
	
	if (phoneNumber) {
		log.debug("sending text message")
		sendSms(phoneNumber, message)
	}
}



private def get_APP_NAME() {
	return "MyEcobeeInit"
}