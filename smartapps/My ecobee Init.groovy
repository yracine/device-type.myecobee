 /**
 *	My ecobee Init (Service Manager)
 *
 *	Author: scott
 *	Date: 2013-08-07
 *
 *  Last Modification:
 *      JLH - 01-23-2014 - Update for Correct SmartApp URL Format
 *      JLH - 02-15-2014 - Fuller use of ecobee API
 *		Y.Racine Nov 2014 - Simplified the Service Manager as much as possible to reduce tight coupling with 
 *							its child device types (device handlers) for better performance and reliability.
 *      linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 **/
definition(
    name: "MyEcobeeInit",
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
	page(name: "deviceList", title: "ecobee", content:"ecobeeDeviceList", install:true)
}

mappings {
	path("/swapToken") {
		action: [
			GET: "swapToken"
		]
	}
}

def about() {
 	dynamicPage(name: "about", install: false, uninstall: true) {
 		section("About") {	
			paragraph "My Ecobee Init, the smartapp that connects your Ecobee thermostat to SmartThings via cloud-to-cloud integration"
			paragraph "Version 1.9\n\n" +
			"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
			"Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information...", 
			description: "http://github.com/yracine/device-type.myecobee"
		}
	}        
}

def authPage() {
	log.debug "authPage()"

	if(!atomicState.accessToken)
	{
		log.debug "about to create access token"
		createAccessToken()
		atomicState.accessToken = state.accessToken
	}


	def description = "Required"
	def uninstallAllowed = false
	def oauthTokenProvided = false

	if(atomicState.authToken)
	{
		// TODO: Check if it's valid
		if(true)
		{
			description = "You are connected."
			uninstallAllowed = true
			oauthTokenProvided = true
		}
		else
		{
			description = "Required" // Worth differentiating here vs. not having atomicState.authToken? 
			oauthTokenProvided = false
		}
	}

	def redirectUrl = oauthInitUrl()

	log.debug "RedirectUrl = ${redirectUrl}"

	// get rid of next button until the user is actually auth'd

	if (!oauthTokenProvided) {

		return dynamicPage(name: "auth", title: "Login", nextPage:null, uninstall:uninstallAllowed) {
			section(){
				paragraph "Tap below to log in to the ecobee portal and authorize SmartThings access. Be sure to scroll down on page 2 and press the 'Allow' button."
				href url:redirectUrl, style:"embedded", required:true, title:"ecobee", description:description
			}
		}

	} else {

		return dynamicPage(name: "auth", title: "Log In", nextPage:"deviceList", uninstall:uninstallAllowed) {
			section(){
				paragraph "Tap Next to continue to setup your thermostats."
				href url:redirectUrl, style:"embedded", state:"complete", title:"ecobee", description:description
			}
		}

	}


}

def ecobeeDeviceList() {
	log.debug "ecobeeDeviceList()"

	def stats = getEcobeeThermostats()

	log.debug "device list: $stats"

	def ems = getEcobeeThermostats("ems")

	log.debug "device list: $ems"

	stats = stats + ems
    
	def p = dynamicPage(name: "deviceList", title: "Select Your Thermostats", uninstall: true) {
		section(""){
			paragraph "Tap below to see the list of ecobee thermostats available in your ecobee account and select the ones you want to connect to SmartThings."
			input(name: "thermostats", title:"", type: "enum", required:true, multiple:true, description: "Tap to choose", metadata:[values:stats])
		}
	}

	log.debug "list p: $p"
	return p
}

def getEcobeeThermostats(String type="") {
	log.debug "getting device list"
	def requestBody
    
	if ((type == "") || (type == null)) {
    
	 	requestBody = '{"selection":{"selectionType":"registered","selectionMatch":""}}'
	} else {
		requestBody = '{"selection":{"selectionType":"managementSet","selectionMatch":"/"}}'
	}    
    
	def deviceListParams = [
		uri: "https://api.ecobee.com",
		path: "/1/thermostat",
		headers: ["Content-Type": "text/json", "Authorization": "Bearer ${atomicState.authToken}"],
		query: [format: 'json', body: requestBody]
	]

	log.debug "_______AUTH______ ${atomicState.authToken}"
	log.debug "device list params: $deviceListParams"

	def stats = [:]
	try {
		httpGet(deviceListParams) { resp ->

			if(resp.status == 200) {
/*        
        		int i=0    // Used to simulate many thermostats
*/
				resp.data.thermostatList.each { stat ->
					def dni = [ app.id, stat.name, stat.identifier ].join('.')
					stats[dni] = getThermostatDisplayName(stat)
/*
					dni = [ app.id, stat.name, i,stat.identifier ].join('.')    // Used to simulate many thermostats     
					stats[dni] = getThermostatDisplayName(stat)
*/
				}
			} else {
				log.debug "http status: ${resp.status}"

				//refresh the auth token
				if (resp.status == 500 && resp.data.status.code == 14)
				{
					log.debug "Storing the failed action to try later"
					data.action = "getEcobeeThermostats"
					log.debug "Refreshing your auth_token!"
				} else {
					log.error "Authentication error, invalid authentication method, lack of credentials, etc."
				}
			}
            
    	}        
	} catch (java.net.UnknownHostException e) {
		log.error "getEcobeeThermostats> Unknown host - check the URL " + deviceListParams.uri
	} catch (java.net.NoRouteToHostException t) {
		log.error "getEcobeeThermostats> No route to host - check the URL " + deviceListParams.uri
	} catch (java.io.IOException e) {
		log.error "getEcobeeThermostats> Probable cause: not the right account for this type (${type}) of thermostat " +
			deviceListParams
    }

	log.debug "thermostats: $stats"

	return stats
}

def setParentAuthTokens(auth_data) {
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	sendPush("setParentAuthTokens>begin auth_data: $auth_data")
*/

	atomicState.refreshToken = auth_data?.refresh_token
	atomicState.authToken = auth_data?.access_token
	atomicState.expiresIn=auth_data?.expires_in
	atomicState.tokenType = auth_data?.token_type
	atomicState.authexptime= auth_data?.authexptime
	refreshAllChildAuthTokens()
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	sendPush("setParentAuthTokens>New atomicState: $atomicState")
*/
}

def refreshAllChildAuthTokens() {
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	sendPush("refreshAllChildAuthTokens>begin updating children with $atomicState")
*/

	def children= getChildDevices()
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	sendPush("refreshAllChildAuthtokens> refreshing ${children.size()} thermostats")
*/

	children.each { 
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
		sendPush("refreshAllChildAuthTokens>begin updating $it.deviceNetworkId with $atomicState")
*/
    	it.refreshChildTokens(atomicState) 
	}
}

def refreshThisChildAuthTokens(child) {
/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	sendPush("refreshThisChildAuthTokens>begin child id: ${child.device.deviceNetworkId}, updating it with ${atomicState}")
*/
	child.refreshChildTokens(atomicState)

/*
	For Debugging purposes, due to the fact that logging is not working when called (separate thread)
	sendPush("refreshThisChildAuthTokens>end")
*/
}


def getThermostatDisplayName(stat) {
	log.debug "getThermostatDisplayName"
	if(stat?.name)
	{
		return stat.name.toString()
	}

	return (getThermostatTypeName(stat) + " (${stat.identifier})").toString()
}

def getThermostatId(stat) {
	log.debug "getThermostatId"
	return (stat?.idenfifier)
}

def getThermostatTypeName(stat) {
	log.debug "getThermostatTypeName"
	return stat.modelNumber == "siSmart" ? "Smart Si" : (stat.modelNumber=="idtSmart") ? "Smart" : (stat.modelNumber=="athenaSmart") ? "Ecobee3" : "Ems"
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}


private def delete_child_devices() {
	def delete
	// Delete any that are no longer in settings
	if(!thermostats)
	{
		log.debug "delete thermostats"
		delete = getAllChildDevices()
	}
	else
	{
		delete = getChildDevices().findAll { !thermostats.contains(it.deviceNetworkId) }
	}

	log.debug "deleting ${delete.size()} thermostats"
	delete.each { deleteChildDevice(it.deviceNetworkId) }

}

private def create_child_devices() {

   	int i =0
	def devices = thermostats.collect { dni ->

		def d = getChildDevice(dni)
		log.debug "Looping thru thermostats, found id $dni"

		if(!d)
		{
			log.debug "atomicState (i=$i) $atomicState "
			def tstat_info  = dni.tokenize('.')
			def thermostatId = tstat_info.last()
 			def name = tstat_info[1]
/*            
			i++		// Used to simulate many thermostats
			def labelName = 'My ecobee ' + "${name}_${i}"
*/                    
			def labelName = 'My ecobee ' + "${name}"
			log.debug "About to create child device with id $dni, thermostatId = $thermostatId, name=  ${name}"
			d = addChildDevice(getChildNamespace(), getChildName(), dni, null,
				[label: "${labelName}"]) 
			d.initialSetup( getSmartThingsClientId(), atomicState, thermostatId ) 	// initial setup of the Child Device
			log.debug "created ${d.displayName} with id $dni"
		}
		else
		{
			log.debug "found ${d.displayName} with id $dni already exists"
		}

	}

	log.debug "created ${devices.size()} thermostats"



}

def initialize() {
    
	log.debug "initialize"

	delete_child_devices()	
	create_child_devices()
    
	takeAction()
	// set up internal poll timer
	def pollTimer = 20

	log.trace "setting poll to ${pollTimer}"
	schedule("0 0/${pollTimer.toInteger()} * * * ?", takeAction)
}

def takeAction() {
	log.trace "takeAction>begin"
	def devices = thermostats.collect { dni ->
		def d = getChildDevice(dni)
		log.debug "takeAction>Looping thru thermostats, found id $dni, about to poll"
		d.poll()
	}
	log.trace "takeAction>end"
}


def oauthInitUrl() {
	log.debug "oauthInitUrl"
	def stcid = getSmartThingsClientId();

	atomicState.oauthInitState = UUID.randomUUID().toString()

	def oauthParams = [
		response_type: "code",
		scope: "ems,smartWrite",
		client_id: stcid,
		state: atomicState.oauthInitState,
		redirect_uri: buildRedirectUrl()
	]

	return "https://api.ecobee.com/authorize?" + toQueryString(oauthParams)
}

def buildRedirectUrl() {
	log.debug "buildRedirectUrl"
	return serverUrl + "/api/token/${atomicState.accessToken}/smartapps/installations/${app.id}/swapToken"
}

def swapToken() {
	log.debug "swapping token: $params"
	debugEvent ("swapping token: $params", true)

	def code = params.code
	def oauthState = params.state

	def stcid = getSmartThingsClientId()

	def tokenParams = [
		grant_type: "authorization_code",
		code: params.code,
		client_id: stcid,
		redirect_uri: buildRedirectUrl()
	]

	def tokenUrl = "https://api.ecobee.com/token?" + toQueryString(tokenParams)

	log.debug "Swapping token $params"

	def jsonMap
	httpPost(uri:tokenUrl) { resp ->
		jsonMap = resp.data
	}

	log.debug "Swapped token for $jsonMap"
	debugEvent ("swapped token for $jsonMap", true)

	atomicState.refreshToken = jsonMap.refresh_token
	atomicState.authToken = jsonMap.access_token
	atomicState.expiresIn=jsonMap.expires_in
	atomicState.tokenType = jsonMap.token_type
	def authexptime = new Date((now() + (atomicState.expiresIn * 60 * 1000))).getTime()
	atomicState.authexptime = authexptime


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
		<img src="https://s3.amazonaws.com/smartapp-icons/Partner/ecobee%402x.png" alt="ecobee icon" />
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

def getServerUrl() { return "https://graph.api.smartthings.com" }

def getSmartThingsClientId() { "qqwy6qo0c2lhTZGytelkQ5o8vlHgRsrO" }


def debugEvent(message, displayEvent) {

	def results = [
		name: "appdebug",
		descriptionText: message,
		displayed: displayEvent
	]
	log.debug "Generating AppDebug Event: ${results}"
	sendEvent (results)
}
