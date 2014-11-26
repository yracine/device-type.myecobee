/**
 *	Ecobee Init (Service Manager)
 *
 */
definition(
    name: "Ecobee Init",
    namespace: "yracine",
    author: "Yves Racine",
    description: "Connect your Ecobee thermostat to SmartThings.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)
preferences {
	page(name: "auth", title: "ecobee", nextPage:"deviceList", content:"authPage", uninstall: true)
	page(name: "deviceList", title: "ecobee", content:"ecobeeDeviceList", install:true)
}

mappings {
	path("/swapToken") {
		action: [
			GET: "swapToken"
		]
	}
}

def authPage()
{
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
				paragraph "Tap below to log in to the ecobee service and authorize SmartThings access. Be sure to scroll down on page 2 and press the 'Allow' button."
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

def ecobeeDeviceList()
{
	log.debug "ecobeeDeviceList()"

	def stats = getEcobeeThermostats()

	log.debug "device list: $stats"

	def p = dynamicPage(name: "deviceList", title: "Select Your Thermostats", uninstall: true) {
		section(""){
			paragraph "Tap below to see the list of ecobee thermostats available in your ecobee account and select the ones you want to connect to SmartThings."
			input(name: "thermostats", title:"", type: "enum", required:true, multiple:true, description: "Tap to choose", metadata:[values:stats])
		}
	}

	log.debug "list p: $p"
	return p
}

def getEcobeeThermostats()
{
	log.debug "getting device list"

	def requestBody = '{"selection":{"selectionType":"registered","selectionMatch":"","includeRuntime":true}}'

	def deviceListParams = [
		uri: "https://api.ecobee.com",
		path: "/1/thermostat",
		headers: ["Content-Type": "text/json", "Authorization": "Bearer ${atomicState.authToken}"],
		query: [format: 'json', body: requestBody]
	]

	log.debug "_______AUTH______ ${atomicState.authToken}"
	log.debug "device list params: $deviceListParams"

	def stats = [:]
	httpGet(deviceListParams) { resp ->

		if(resp.status == 200)
		{
            int i =0
			resp.data.thermostatList.each { stat ->
				def dni = [ app.id, stat.name, i++, stat.identifier ].join('.')
				stats[dni] = getThermostatDisplayName(stat)
				dni = [ app.id, stat.name, i++, stat.identifier ].join('.')
				stats[dni] = getThermostatDisplayName(stat)
                
			}
		}
		else
		{
			log.debug "http status: ${resp.status}"

			//refresh the auth token
			if (resp.status == 500 && resp.data.status.code == 14)
			{
				log.debug "Storing the failed action to try later"
				data.action = "getEcobeeThermostats"
				log.debug "Refreshing your auth_token!"
				refreshAuthToken()
			}
			else
			{
				log.error "Authentication error, invalid authentication method, lack of credentials, etc."
			}
		}
	}

	log.debug "thermostats: $stats"

	return stats
}

def getThermostatDisplayName(stat)
{
	log.debug "getThermostatDisplayName"
	if(stat?.name)
	{
		return stat.name.toString()
	}

	return (getThermostatTypeName(stat) + " (${stat.identifier})").toString()
}

def getThermostatId(stat)
{
	log.debug "getThermostatId"
	return (stat?.idenfifier)
}

def getThermostatTypeName(stat)
{
	log.debug "getThermostatTypeName"
	return stat.modelNumber == "siSmart" ? "Smart Si" : "Smart"
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	// createAccessToken()


	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
	log.debug "initialize"
	def devices = thermostats.collect { dni ->

		def d = getChildDevice(dni)
		log.debug "Looping thru thermostats, found id $dni"

		if(!d)
		{
            def tstat_info  = dni.tokenize('.')
			def thermostatId = tstat_info.last()
            def name = tstat_info[1]
            def labelName = 'My ecobee ' + "${name}"
			log.debug "About to create child device with id $dni, thermostatId = $thermostatId, name=  ${name}"
			d = addChildDevice(getChildNamespace(), getChildName(), dni, null,
            	[label: "${labelName}", completedSetup: true])
			d.initialSetup( getSmartThingsClientId(), atomicState,thermostatId )  // initial setup 
			log.debug "created ${d.displayName} with id $dni"
		}
		else
		{
			log.debug "found ${d.displayName} with id $dni already exists"
		}

		return d
	}

	log.debug "created ${devices.size()} thermostats"

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


def oauthInitUrl()
{
	log.debug "oauthInitUrl"
	// def oauth_url = "https://api.ecobee.com/authorize?response_type=code&client_id=qqwy6qo0c2lhTZGytelkQ5o8vlHgRsrO&redirect_uri=http://localhost/&scope=smartRead,smartWrite&state=abc123" 
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

def buildRedirectUrl()
{
	log.debug "buildRedirectUrl"
	// return serverUrl + "/api/smartapps/installations/${app.id}/token/${atomicState.accessToken}"
	return serverUrl + "/api/token/${atomicState.accessToken}/smartapps/installations/${app.id}/swapToken"
}

def swapToken()
{
	log.debug "swapping token: $params"
	debugEvent ("swapping token: $params", true)

	def code = params.code
	def oauthState = params.state

	// TODO: verify oauthState == atomicState.oauthInitState



	// https://www.ecobee.com/home/token?grant_type=authorization_code&code=aliOpagDm3BqbRplugcs1AwdJE0ohxdB&client_id=qqwy6qo0c2lhTZGytelkQ5o8vlHgRsrO&redirect_uri=https://graph.api.smartthings.com/
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


def getChildDeviceIdsString()
{
	return thermostats.collect { it.split(/\./).last() }.join(',')
}

def toJson(Map m)
{
	return new org.codehaus.groovy.grails.web.json.JSONObject(m).toString()
}

def toQueryString(Map m)
{
	return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

private refreshAuthToken() {
	log.debug "refreshing auth token"
	debugEvent("refreshing OAUTH token", true)

	def stcid = getSmartThingsClientId()

	def refreshParams = [
		method: 'POST',
		uri: "https://api.ecobee.com",
		path: "/token",
		query: [grant_type:'refresh_token', code:"${atomicState.refreshToken}", client_id:stcid],

		//data?.refreshToken
	]

	log.debug refreshParams

	//changed to httpPost
	try{
		def jsonMap
		httpPost(refreshParams) { resp ->

			if(resp.status == 200)
			{
				log.debug "Token refreshed...calling saved RestAction now!"

				debugEvent("Token refreshed ... calling saved RestAction now!", true)

				log.debug resp

				jsonMap = resp.data

				if (resp.data) {

					log.debug resp.data
					debugEvent ("Response = ${resp.data}", true)

					atomicState.refreshToken = resp?.data?.refresh_token
					atomicState.authToken = resp?.data?.access_token

					debugEvent ("Refresh Token = ${atomicState.refreshToken}", true)
					debugEvent ("OAUTH Token = ${atomicState.authToken}", true)

					if (data?.action && data?.action != "") {
						log.debug data.action

						"{data.action}"()

						//remove saved action
						data.action = ""
					}

				}
				data.action = ""
			}
			else
			{
				log.debug "refresh failed ${resp.status} : ${resp.status.code}"
			}
		}

		// atomicState.refreshToken = jsonMap.refresh_token
		// atomicState.authToken = jsonMap.access_token
	}
	catch(Exception e)
	{
		log.debug "caught exception refreshing auth token: " + e
	}
}



def sendJson(String jsonBody)
{

	//log.debug "_____AUTH_____ ${atomicState.authToken}"

	def cmdParams = [
		uri: "https://api.ecobee.com",

		path: "/1/thermostat",
		headers: ["Content-Type": "application/json", "Authorization": "Bearer ${atomicState.authToken}"],
		body: jsonBody
	]

	def returnStatus = -1

	try{
		httpPost(cmdParams) { resp ->

			if(resp.status == 200) {

				log.debug "updated ${resp.data}"
				debugEvent("updated ${resp.data}", true)
				returnStatus = resp.data.status.code
				if (resp.data.status.code == 0)
					log.debug "Successful call to ecobee API."
				else {
					log.debug "Error return code = ${resp.data.status.code}"
					debugEvent("Error return code = ${resp.data.status.code}", true)
				}
			}
			else
			{
				log.error "sent Json & got http status ${resp.status} - ${resp.status.code}"
				debugEvent ("sent Json & got http status ${resp.status} - ${resp.status.code}", true)

				//refresh the auth token
				if (resp.status == 500 && resp.status.code == 14)
				{
					//log.debug "Storing the failed action to try later"
					log.debug "Refreshing your auth_token!"
					debugEvent ("Refreshing OAUTH Token", true)
					refreshAuthToken()
					return false
				}
				else
				{
					debugEvent ("Authentication error, invalid authentication method, lack of credentials, etc.", true)
					log.error "Authentication error, invalid authentication method, lack of credentials, etc."
					return false
				}
			}
		}
	}
	catch(Exception e)
	{
		log.debug "Exception Sending Json: " + e
		debugEvent ("Exception Sending JSON: " + e, true)
		return false
	}

	if (returnStatus == 0)
		return true
	else
		return false
}


def getChildNamespace() { "" }
def getChildName() { "My Ecobee Device" }

def getServerUrl() { return "https://graph.api.smartthings.com" }
def getSmartThingsClientId() { "{AppKey from Ecobee here!}" }

def debugEvent(message, displayEvent) {

	def results = [
		name: "appdebug",
		descriptionText: message,
		displayed: displayEvent
	]
	log.debug "Generating AppDebug Event: ${results}"
	sendEvent (results)

}
