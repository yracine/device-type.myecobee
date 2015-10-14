}
	if (settings.trace) {
		log.debug "refreshParentTokens>about to call parent.setParentAuthTokens"
	}         
	parent.setParentAuthTokens(data.auth)
	if (settings.trace) {
		log.debug "refreshParentTokens>end"
	}         
}

private void login() {
	if (settings.trace) {
		log.debug "login> about to call setAuthTokens"
	}
	if (data?.auth?.thermostatId) {
    	// Created by initalSetup
		if (settings.trace) {
			log.debug "login> about to call refreshThisChildAuthTokens"
		}
		parent.refreshThisChildAuthTokens(this)
	} else { 
		setAuthTokens()
	}    
	if (!isLoggedIn()) {
		if (settings.trace) {
			log.error "login> no auth_token..., failed"
		}
		return
	}
}

void getEcobeePinAndAuth() {
	String SCOPE = "smartWrite,ems"
	def method = 
	[
		headers: [
			'Content-Type': "application/json",
			'charset': "UTF-8"
			],
		uri: "${get_URI_ROOT()}/authorize?" +
		"response_type=ecobeePin&" +
		"client_id=${get_appKey()}&" +
		"scope=${SCOPE}"
	]
	def successEcobeePin = {resp ->
		if (settings.trace) {
			log.debug "getEcobeePinAndAuth> response = ${resp.data}"
		}
		data?.auth = resp.data
		data.auth.code = resp.data.code
		data.auth.expires_in = resp.data.expires_in
		data.auth.interval = resp.data.interval
		data.auth.ecobeePin = resp.data.ecobeePin
		sendEvent name: "verboseTrace", value:"getEcobeePin>${data.auth.ecobeePin}"
		data.auth.access_token = null // for isLoggedIn() later
		data.thermostatCount = null // for iterate functions later
		data.groups = null // for iterateUpdateGroups later
	}
	try {
		httpGet(method, successEcobeePin)
	} catch (java.net.UnknownHostException e) {
		log.error "getEcobeePinAndAuth> Unknown host - check the URL " + method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> Unknown host " +
			method.uri
		return
	} catch (java.net.NoRouteToHostException t) {
		log.error "getEcobeePinAndAuth> No route to host - check the URL " + method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> No route to host " +
			method.uri
		return
	} catch (java.io.IOException e) {
		log.error "getEcobeePinAndAuth> Authentication error, bad URL or settings missing " +
			method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> Auth error " + method.uri
		return
	} catch (any) {
		log.error "getEcobeePinAndAuth> general error " + method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> general error " +
			method.uri
		return
	}
	if (settings.trace) {
		log.debug "getEcobeePinAndAuth> ecobeePin= ${data.auth.ecobeePin}, authorizationCode=${data.auth.code},scope=${data.auth.scope} expires_in=${data.auth.expires_in} done"
	}
}

void setAuthTokens() {
	def method = 
	[
		headers: [
			'X-nl-protocol-version': 1,
			'Content-Type': "application/json",
			'charset': "UTF-8"
			],
		uri: "${get_URI_ROOT()}/token?" +
		"grant_type=ecobeePin&" +
		"code=${data.auth.code}&" +
		"client_id=${get_appKey()}"
	]
	if (data?.auth?.access_token == null) {
		def successTokens = {resp ->
			data.auth.access_token = resp.data.access_token
			data.auth.refresh_token = resp.data.refresh_token
			data.auth.expires_in = resp.data.expires_in
			data.auth.token_type = resp.data.token_type
			data.auth.scope = resp.data.scope
			if (settings.trace) {
				log.debug "setAuthTokens> accessToken= ${data.auth.access_token}, refreshToken=${data.auth.refresh_token}," +
					"tokenType=${data.auth.token_type},scope=${data.auth.scope}"
			}
		}
		try {
			log.debug "setAuthTokens> about to call httpPost with code= ${data.auth.code}"
			httpPostJson(method, successTokens)

		} catch (java.net.UnknownHostException e) {
			log.error "setAuthTokens> Unknown host - check the URL " + method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens> Unknown host " +
				method.uri
			return
		} catch (java.net.NoRouteToHostException t) {
			log.error "setAuthTokens> No route to host - check the URL " + method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens> No route to host" +
				method.uri
			return
		} catch (java.io.IOException e) {
			log.error "setAuthTokens> Auth error, ecobee site cannot be reached " +
				method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens> Auth error " +
				method.uri
			return
		} catch (any) {
			log.error "setAuthTokens> general error " + method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens>general error " +
				method.uri
			return
		}
		// determine token's expire time
		def authexptime = new Date((now() + (data.auth.expires_in * 60 * 1000))).getTime()
		data.auth.authexptime = authexptime
		if (settings.trace) {
			log.debug "setAuthTokens> expires in ${data.auth.expires_in} minutes"
			log.debug "setAuthTokens> data_auth.expires_in in time = ${authexptime}"
			sendEvent name: "verboseTrace", value:
				"setAuthTokens>expire in ${data.auth.expires_in} minutes"
		}
	}
}
private def isLoggedIn() {
	if (data.auth == null) {
		if (settings.trace) {
			log.debug "isLoggedIn> no data auth"
		}
		return false
	} else {
		if (data.auth.access_token == null) {
			if (settings.trace) {
				log.debug "isLoggedIn> no access token"
				return false
			}
		}
	}
	return true
}
private def isTokenExpired() {
	def buffer_time_expiration=5   // set a 5 min. buffer time before token expiration to avoid auth_err 
	def time_check_for_exp = now() + (buffer_time_expiration * 60 * 1000);
	if (settings.trace) {
		log.debug "isTokenExpired> check expires_in: ${data.auth.authexptime} > time check for exp: ${time_check_for_exp}"
	}
	if (data.auth.authexptime > time_check_for_exp) {
		if (settings.trace) {
			log.debug "isTokenExpired> not expired"
		}
		return false
	}
	if (settings.trace) {
		log.debug "isTokenExpired> expired"
	}
	return true
}


// Determine ecobee type from tstatType or settings
// tstatType =managementSet or registered (no spaces). 
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
private def determine_ecobee_type_or_location(tstatType) {
	def ecobeeType
	def modelNumber = getModelNumber()
    
	if (settings.trace) {
		log.debug "determine_ecobee_type>begin ecobeeType = ${tstatType}, modelNumber=${modelNumber}"
	}
	if ((tstatType != null) && (tstatType != "")) {
		ecobeeType = tstatType.trim()
	} else if ((settings?.ecobeeType != null) && (settings?.ecobeeType != "")) {
		ecobeeType = settings.ecobeeType.trim()
		
	} else if (modelNumber.toUpperCase().contains("EMS")) {
    
		ecobeeType = 'managementSet'
		settings.ecobeeType='managementSet'
    
	} else {
		// by default, the ecobee type is 'registered'
		ecobeeType = 'registered'
		settings.ecobeeType='registered'
	}
	if (settings.trace) {
		log.debug "determine_ecobee_type>end ecobeeType = ${ecobeeType}"
	}
	return ecobeeType
}

// Determine id from settings or initalSetup
def determine_tstat_id(tstat_id) {
	def tstatId
    
	if ((tstat_id != null) && (tstat_id != "")) {
		tstatId = tstat_id.trim()
	} else if ((settings.thermostatId != null) && (settings.thermostatId  != "")) {
		tstatId = settings.thermostatId.trim()
		if (settings.trace) {
			log.debug "determine_tstat_id> tstatId = ${settings.thermostatId}"
		}
	} else if ((settings.thermostatId == null) || (settings.thermostatId  == "")) {
		settings.appKey = data.auth.appKey
		settings.thermostatId = data.auth.thermostatId
		tstatId=data.auth.thermostatId
		if (settings.trace) {
			log.debug "determine_tstat_id> tstatId from data= ${data.auth.thermostatId}"
		}
	}
	return tstatId
}

// Get the appKey for authentication
private def get_appKey() {
	return settings.appKey
}    

// Called by My ecobee Init for initial creation of a child Device
void initialSetup(device_client_id, auth_data, device_tstat_id) {
/*
	settings.trace='true'
*/	
	if (settings.trace) {
		log.debug "initialSetup>begin"
		log.debug "initialSetup> device_tstat_Id = ${device_tstat_id}"
		log.debug "initialSetup> device_client_id = ${device_client_id}"
	}	
	settings.appKey= device_client_id
	settings.thermostatId = device_tstat_id

	data?.auth = settings    
	data.auth.access_token = auth_data.authToken
	data.auth.refresh_token = auth_data.refreshToken
	data.auth.expires_in = auth_data.expiresIn
	data.auth.token_type = auth_data.tokenType
	data.auth.authexptime= auth_data?.authexptime
	if (settings.trace) {
		log.debug "initialSetup> settings = $settings"
		log.debug "initialSetup> data_auth = $data.auth"
		log.debug "initialSetup>end"
	}
	getThermostatInfo(thermostatId)
	def ecobeeType=determine_ecobee_type_or_location("")
	data.auth.ecobeeType = ecobeeType
	state?.exceptionCount=0    
	state?.lastPollTimestamp = now()
}

def toQueryString(Map m) {
	return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

private def cToF(temp) {
	return (temp * 1.8 + 32)
}
private def fToC(temp) {
	return (temp - 32) / 1.8
}
private def milesToKm(distance) {
	return (distance * 1.609344) 
}
private def get_URI_ROOT() {
	return "https://api.ecobee.com"
}
// Maximum tstat batch size (25 thermostats max may be processed in batch)
private def get_MAX_TSTAT_BATCH() {
	return 25
}
