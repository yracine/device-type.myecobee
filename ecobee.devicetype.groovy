/***
 *  My Ecobee Device
 *
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-03-31
 *  Code: https://github.com/yracine/device-type.myecobee
 *
 * INSTALLATION STEPS
 * ==================
 *
 * 1) Connect to the ecobee portal (www.ecobee.com) and create an application key with an application name (such as ecobeeTstat)
 *    and indicate the PIN method authentication (at the bottom of the window).
 *
 * 2) Create a new device type (https://graph.api.smartthings.com/ide/devices)
 *     Name: MyEcobee Device
 *     Author: Yves Racine
 *    
 * 3) Create a new device (https://graph.api.smartthings.com/device/list)
 *     Name: Your Choice
 *     Device Network Id: Your Choice
 *     Type: My Ecobee Device (should be the last option)
 *     Location: Choose the correct location
 *     Hub/Group: (optional) leave blank or set it up to your liking
 *
 * 4) Update device's preferences
 *     Click on the new device 
 *     Click the edit button next to Preferences
 *     Fill in your device 
 *        (a) <appKey> provided at the ecobee web portal in step 1
 *        (b) <serial number> of your ecobee thermostat
 *        (c) <trace> when needed, set to true to get more tracing
 *        (d) <holdType> set to nextTransition or indefinite (by default) 
 *        see https://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml for more details
 *
 * 5) To get an ecobee PIN (double authentication), create a small app with the following code and install it.
 *
 *  preferences {
 *   
 *	   section("Initialize this ecobee thermostat") {
 *		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
 *	   }
 *
 *   }
 *
 * def installed() {
 *   
 *   log.debug "installed> calling getEcobeePinAuth... "
 *   ecobee.getEcobeePinAndAuth()
 * }
 *
 * 6) Click on your ecobee device again to get the 4-alphanumeric PIN from the list ((https://graph.api.smartthings.com/device/list). 
 *    It should appear under the verboseTrace attribute.
 *
 * 7) Go to the ecobee web portal within the next 9 minutes and enter your pin number under settings/my apps
 *
 * 8) Your device should now be ready to process your commands
 *
 * Copyright (C) 2014 Yves Racine <yracine66@gmail.com>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions: The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
 
// for the UI
import groovy.json.JsonBuilder
import java.net.URLEncoder

preferences {
    	input("thermostatId", "text", title: "Serial #", description: "The serial number of your thermostat")
    	input("appKey", "text", title: "App Key", description: "The application key given by Ecobee")
    	input("trace", "text", title: "trace", description: "Set it to true to enable tracing")
    	input("holdType", "text", title: "holdType", description: "Set it 'nextTransition' or 'indefinite' (by default)")
	}
metadata {
    definition (name: "My Ecobee Device", author: "Yves Racine") {
        capability "Polling"
        capability "Thermostat"
        capability "Relative Humidity Measurement"
        capability "Temperature Measurement"

        attribute "heatLevelUp", "string"
        attribute "heatLevelDown", "string"
        attribute "coolLevelUp", "string"
        attribute "coolLevelDown", "string"
        attribute "verboseTrace", "string"
        attribute "humidifierMode", "string"
        attribute "dehumidifierMode", "string"
        attribute "humidifierLevel", "string" 
        attribute "dehumidifierLevel", "string" 
        attribute "condensationAvoid", "string" 
    
        command "humidifierAuto"
        command "humidifierOff"
        command "setHumidifierLevel"
        command "dehumidifierAuto"
        command "dehumidifierOff"
        command "setDehumidifierLevel"
        command "setFanMinOnTime"	
        command "setThermostatFanMode"    
        command "setCondensationAvoid"
        command "createVacation"
        command "deleteVacation"
        command "getEcobeePinAndAuth"
        command "getThermostatInfo"
        command "getThermostatSummary"
        command "iterateCreateVacation"
        command "iterateDeleteVacation"
        command "iterateResumeProgram"
        command "iterateSetHold"
        command "resumeProgram"
        command "resumeThisTstat"
        command "setAuthTokens"
        command "setHold"
        command "heatLevelUp"
        command "heatLevelDown"
        command "coolLevelUp"
        command "coolLevelDown"
        command "setFanMinOnTime"
        command "auxHeatOnly"
    }

    simulator {
        // TODO: define status and reply messages here
    }
 
    tiles {
         valueTile("temperature", "device.temperature", width: 2, height: 2, canChangeIcon: true) {
             state("temperature", label: '${currentValue}°', unit:"C", backgroundColors: [
                 [value: 0, color: "#153591"],
    	         [value: 8, color: "#1e9cbb"],
                 [value: 14, color: "#90d2a7"],
            	 [value: 20, color: "#44b621"],
                 [value: 24, color: "#f1d801"],
                 [value: 29, color: "#d04e00"],
                 [value: 36, color: "#bc2323"]           
              ])       
        }

        standardTile("mode", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state "heat", label:'${name}', action:"thermostat.off", icon: "st.Weather.weather14",backgroundColor:"#ffffff"
            state "off", label:'${name}', action:"thermostat.cool", icon: "st.Outdoor.outdoor19"
            state "cool", label:'${name}', action:"thermostat.heat", icon: "st.Weather.weather7"
        }
        standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false, decoration: "flat") {
            state "fanAuto", label:'', action:"thermostat.fanOn",icon: "st.thermostat.fan-auto" 
            state "fanOn", label:'', action:"thermostat.fanOff", icon: "st.thermostat.fan-on" 
            state "fanOff", label:'  ', action:"thermostat.fanAuto", icon: "st.thermostat.fan-off"
        }
 
        valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat") { 
            state "heat", label:'${currentValue}° heat', unit:"C", backgroundColor:"#ffffff"
        }
        valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false, decoration: "flat") {
            state "cool", label:'${currentValue}° cool', unit:"C", backgroundColor:"#ffffff"
        }
        valueTile("humidity", "device.humidity", inactiveLabel: false, decoration: "flat") {
            state "default", label:'${currentValue}%Hum', unit:"humidity"
        }
        standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state "default", action:"polling.poll", icon:"st.secondary.refresh"
        }
        standardTile("resProgram", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state "default", label:'ResumeProg',action:"resumeThisTstat", icon:"st.Office.office7",backgroundColor:"#ffffff"
        }
        standardTile("heatLevelUp", "device.heatingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "heatLevelUp", label:'  ', action:"heatLevelUp", icon:"st.thermostat.thermostat-up"
        }
        standardTile("heatLevelDown", "device.heatingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "heatLevelDown", label:'  ', action:"heatLevelDown", icon:"st.thermostat.thermostat-down"
        }
        standardTile("coolLevelUp", "device.coolingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "coolLevelUp", label:'  ', action:"coolLevelUp", icon:"st.thermostat.thermostat-up"
        }
        standardTile("coolLevelDown", "device.coolingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "coolLevelDown", label:'  ', action:"coolLevelDown", icon:"st.thermostat.thermostat-down"
       
        }

        main "temperature"
        details(["temperature", "mode", "fanMode", "heatLevelDown", "heatingSetpoint", "heatLevelUp", "coolLevelDown", "coolingSetpoint", "coolLevelUp", "humidity", "refresh", "resProgram"])

    }
}

def coolLevelUp(){

    int nextLevel = device.currentValue("coolingSetpoint") + 1
    
    def scale = getTemperatureScale()
    if (scale == 'C') { 
    
        if( nextLevel > 30){
    	    nextLevel = 30
        }
    }
    else {
    

        if( nextLevel > 99){
    	    nextLevel = 99
        }
    
    }
    setCoolingSetpoint(nextLevel)
}

def coolLevelDown(){
    int nextLevel = device.currentValue("coolingSetpoint") - 1
    def scale = getTemperatureScale()
    if (scale == 'C') { 
        if( nextLevel < 10){
    	    nextLevel = 10
        }
    }
    else {
    
        if( nextLevel < 50){
    	    nextLevel = 50
        }
    }
    setCoolingSetpoint(nextLevel)
}

def heatLevelUp(){
    int nextLevel = device.currentValue("heatingSetpoint") + 1

    def scale = getTemperatureScale()
    if (scale == 'C') { 
        if( nextLevel > 30){
    	    nextLevel = 30
        }
    }
    else {
        if( nextLevel > 99){
    	    nextLevel = 99
        }
        
    }
    setHeatingSetpoint(nextLevel)
}

def heatLevelDown(){
    int nextLevel = device.currentValue("heatingSetpoint") - 1
    
    def scale = getTemperatureScale()
    if (scale == 'C') { 
        if( nextLevel < 10){
    	    nextLevel = 10
        }
    }
    else {
    
        if( nextLevel < 50){
    	    nextLevel = 50
        }
    }
    setHeatingSetpoint(nextLevel)
}


// handle commands
def setHeatingSetpoint(temp) {
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"),temp, null)
    sendEvent(name: 'heatingSetpoint', value: temp)
}
 
def setCoolingSetpoint(temp) {
    setHold(settings.thermostatId,  temp, device.currentValue("heatingSetpoint"),null) 
    sendEvent(name: 'coolingSetpoint', value: temp)
}
 
 
def off() {
    setThermostatMode('off')
}
 
def heat() {
    setThermostatMode('heat')
}
 
def emergencyHeat() {
    setThermostatMode('heat')
}
 
def auxHeatOnly() {
    setThermostatMode('auxHeatOnly')
}


def cool() {
    setThermostatMode('cool')
}
 
def setThermostatMode(mode) {
    mode = mode == 'emergency heat'? 'heat' : mode
    setHold(settings.thermostatId,device.currentValue("coolingSetpoint"),device.currentValue("heatingSetpoint"),
       ['hvacMode':"${mode}"]) 
    sendEvent(name: 'thermostatMode', value: mode)
}
 
def fanOn() {
    setThermostatFanMode('on')
}
 
def fanAuto() {
    setThermostatFanMode('auto')
}
 
def fanOff() {
    setThermostatFanMode('off')
}


def setFanMinOnTime(minutes) {
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"), device.currentValue("heatingSetpoint"),
        ['vent':'minontime','ventilatorMinOnTime':"${minutes}"]) 
    sendEvent(name: 'fanMinOnTime', value: minutes)
}

def setThermostatFanMode(mode) {    
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"), device.currentValue("heatingSetpoint"),
         ['vent':"${mode}"]) 
    sendEvent(name: 'thermostatFanMode', value: mode)
}

def setCondensationAvoid(flag) {  // set the flag to true or false
    flag = flag == 'true'? 'true' : 'false'
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"), device.currentValue("heatingSetpoint"),
         ['condensationAvoid':"${flag}"]) 
    sendEvent(name: 'condensationAvoid', value: flag)
}


def auto() {
    setThermostatMode('auto')
}


def dehumidifierAuto() {
    setDehumidifierMode('auto')
}

def dehumidifierOff() {
    setDehumidifierMode('off')
}

 
def setDehumidifierMode(mode) {  
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"), device.currentValue("heatingSetpoint"),
          ['dehumidifierMode':"${mode}"]) 
    sendEvent(name: 'dehumidifierMode', value: mode)
}

def setDehumidifierLevel(level) {
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"), device.currentValue("heatingSetpoint"),
          ['dehumidifierLevel':"${level}"]) 
    sendEvent(name: 'dehumidifierLevel', value: level)
}

def humidifierAuto() {
    setHumidifierMode('auto')
}
 
def humidifierOff() {
    setHumidifierMode('off')
}
 
def setHumidifierMode(mode) {    
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"), device.currentValue("heatingSetpoint"),
          ['humidifierMode':"${mode}"]) 
    sendEvent(name: 'humidifierMode', value: mode)
}
 
 

def setHumidifierLevel(level) {
    
    setHold(settings.thermostatId, device.currentValue("coolingSetpoint"),device.currentValue("heatingSetpoint"),
          ['humidity':"${level}"]) 
    sendEvent(name: 'humidifierLevel', value: level)
}

// parse events into attributes
def parse(String description) {
    
}
 
def poll() {
    
    if (settings.trace) {
         
        log.debug "poll> about to execute with settings ${settings}.."
        sendEvent name: "verboseTrace", value: "poll>about to execute with settings ${settings}"
    }
    getThermostatInfo(settings.thermostatId)
    
    sendEvent(name: 'thermostatMode', value: data.thermostatList[0].settings.hvacMode)
    sendEvent(name: 'thermostatFanMode', value: data.thermostatList[0].settings.vent)
    sendEvent(name: 'humidity', value: data.thermostatList[0].runtime.actualHumidity)
    sendEvent(name: 'thermostatMode', value: data.thermostatList[0].settings.hvacMode)
    if (data.thermostatList.settings.hasHumidifier) {
        sendEvent(name: 'humidifierMode', value: data.thermostatList[0].settings.humidifierMode)
        sendEvent(name: 'humidifierLevel', value: data.thermostatList[0].settings.humidity)
        
    }
    if (data.thermostatList.settings.hasDehumidifier) {
        sendEvent(name: 'dehumidifierMode', value: data.thermostatList[0].settings.dehumidifierMode)
        sendEvent(name: 'dehumidifierLevel', value: data.thermostatList[0].settings.dehumidifierLevel)
    }
    def scale = getTemperatureScale()
    if (scale =='C') {
        def actualTempFormat = String.format('%2.1f', actualTemp.round(1))
        def desiredCoolFormat = String.format('%2.1f', desiredCoolTemp.round(1))
        def desiredHeatFormat = String.format('%2.1f', desiredHeatTemp.round(1))
        def actualTempFormat = String.format('%2.1f', actualTemp)
        def desiredCoolFormat = String.format('%2.1f', desiredCoolTemp)
        def desiredHeatFormat = String.format('%2.1f', desiredHeatTemp)
        sendEvent(name: 'temperature', value: actualTempFormat, 
            unit:"C", state: data.thermostatList[0].settings.hvacMode)
        sendEvent(name: 'coolingSetpoint', value: desiredCoolFormat, unit: "C")
        sendEvent(name: 'heatingSetpoint', value:  desiredHeatFormat, unit: "C")
    
    }        
    else {
    
        sendEvent(name: 'temperature', value: (data.thermostatList[0].runtime.actualTemperature), 
            unit:"F", state: data.thermostatList[0].settings.hvacMode)
        sendEvent(name: 'coolingSetpoint', value: (data.thermostatList[0].runtime.desiredCool), unit: "F")
        sendEvent(name: 'heatingSetpoint', value: (data.thermostatList[0].runtime.desiredHeat), unit: "F")
    
    }
}



def refresh() {
     poll()
}


def resumeThisTstat() {

     resumeProgram(settings.thermostatId)
}


def api(method,  args, success = {}) {
    String URI_ROOT= "https://api.ecobee.com/1"
    
    if(!isLoggedIn()) {
        if (settings.trace) {
           log.debug "Need to login"
        }   
        login()
    }
    
    if (settings.trace) {
       log.debug "api> logged in"
    }   
    if (isTokenExpired()) {
        if (settings.trace) {
            log.debug "api> need to refresh tokens"
        }    
        if (!refresh_tokens()) {
           if (settings.trace) {
               log.debug "api> refresh_tokens failed, try login again..."
           }    
           login()
        }
        
    }
    
    def args_encoded = URLEncoder.encode(args.toString(),"UTF-8")


    def methods = [
        'thermostatSummary': [uri: "${URI_ROOT}/thermostatSummary?format=json&body=${args_encoded}", type: 'get'],        
        'thermostatInfo':    [uri: "${URI_ROOT}/thermostat?format=json&body=${args_encoded}", type: 'get'],
        'setHold':           [uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
        'resumeProgram':     [uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
        'createVacation':    [uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
        'deleteVacation':    [uri: "${URI_ROOT}/thermostat?format=json", type: 'post']
    ]

    def request = methods.getAt(method)

    if (settings.trace) {

       log.debug "api> about to call doRequest with (unencoded) args = ${args}"
  	   sendEvent name: "verboseTrace", value: "api> about to call doRequest with (unencoded) args = ${args}"
       
    }   

    doRequest(request.uri, args_encoded, request.type, success)

}
 


// Need to be authenticated in before this is called. So don't call this. Call api.

def doRequest(uri, args, type, success) {
    
    
    def params = [
            uri: uri,
            headers: [
            	'Authorization':"${data.auth.token_type} ${data.auth.access_token}",
                'Content-Type':"application/json",
                'charset': "UTF-8",
                'Accept':"application/json"
  	        ],
            body: args
    ]
    
    try {

        if (settings.trace) {
           
  	        sendEvent name: "verboseTrace", value: "doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}"
            log.debug "doRequest> ${type}> uri ${params.uri}, args= ${args}"
        }
        if(type == 'post') {
            httpPostJson(params, success)
            
        } else if (type == 'get') {    
            params.body=null  // already in the URL request
            httpGet(params, success)

        }
    } catch ( java.net.UnknownHostException e) {
    	log.error "doRequest> Unknown host - check the URL " + params.uri
    	sendEvent name: "verboseTrace", value: "doRequest> Unknown host"
   	} catch (java.net.NoRouteToHostException e) {
    	log.error "doRequest> No route to host - check the URL " + params.uri
    	sendEvent name: "verboseTrace", value: "doRequest> No route to host"
    } catch (java.io.IOException e) {
      	log.error "doRequest> general or malformed request error " + params.body
        sendEvent name: "verboseTrace", value: "doRequest> general or malformed request body error " + params.body
    }
}


// tstatType ='managementSet' for utilities or other managmement sets, 
//            'registered' for SMART thermostat, 
//             null if not relevant for the given method
// thermostatId could be a list of serial# separated by "," 

private def build_body_request(method, tstatType, thermostatId,  tstatParams =[], tstatSettings=[]) {

    def selectionJson=null
    def selection=null
    
    if (settings.trace) {
        log.debug "build_body_request> about to build selection for method= ${method} & thermostatId= ${thermostatId}"
    }
    if (method == 'thermostatSummary') {
    
       
        selection = [selection: [selectionType: tstatType, 
                    selectionMatch: '']
                    ]                                    
                       
    }
    else if (method == 'thermostatInfo') {
    
        selection = [selection: [selectionType:'thermostats',
                                    selectionMatch:thermostatId, 
                                    includeSettings:'true',
                                    includeRuntime:'true']
                    ]
 
                     
    }
    else {
        selection = [selectionType:'thermostats',selectionMatch:thermostatId]
        
    
    }
    selectionJson = new JsonBuilder(selection)
    if (settings.trace) {
        log.debug "build_body_request> about to build request for method = ${method} & thermostatId= ${thermostatId} with selection = ${selectionJson}"
    }
    if ((tstatSettings != null) && (tstatSettings != "")) {
        
        def function_clause = [type:method,params:tstatParams]
        def bodyWithSettings= [functions:[function_clause],selection:selection,thermostat:[settings:tstatSettings]]
        def bodyWithSettingsJson = new JsonBuilder(bodyWithSettings)
        if (settings.trace) {
            log.debug "build_tstat_request done>  for method = ${method}, body with settings= ${bodyWithSettingsJson}"
        }    
        return bodyWithSettingsJson
    }
    else if ((tstatParams != null) && (tsatParams != "")) {
        def function_clause = [type:method,params:tstatParams]
        def simpleBody= [functions:[function_clause],selection:selection]
        
        def simpleBodyJson = new JsonBuilder(simpleBody)
        if (settings.trace) {
            log.debug "build_tstat_request done>  for method = ${method}, body w/o settings= ${simpleBody}"
        }    
        return simpleBodyJson
    }
    else if (method == 'resumeProgram') {

        def function_clause = [type:method]
        def simpleBody= [functions:[function_clause],selection:selection]
        
        def simpleBodyJson = new JsonBuilder(simpleBody)
        if (settings.trace) {
            log.debug "build_tstat_request done>  for method = ${method}, body w/o params= ${simpleBody}"
        }  
        return simpleBodyJson

    }
    else {
    
        if (settings.trace) {
            log.debug "build_tstat_request done> for method = ${method}, body for selection ${selectionJson}"
        }    
        return selectionJson
    
    }
    
}

// tstatType ='managementSet' or 'registered'
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml


def iterateSetHold(tstatType, coolingSetPoint, heatingSetPoint, tstatSettings=[]) {    

    Integer MAX_TSTAT_BATCH=25
    def tstatlist=null
    
    if (data.thermostatCount==null)
    {
    
         getThermostatSummary(tstatType)
    }
    if (settings.trace) {
        log.debug "iterateSetHold> about to loop ${data.thermostatCount} thermostat(s)"
   	    sendEvent name: "verboseTrace", value: "iterateSetHold> about to loop ${data.thermostatCount} thermostat(s)"
    }    
    for (i in 0..data.thermostatCount-1) {
    
         def thermostatDetails = data.revisionList[i].split(':')
         def Id = thermostatDetails[0]
         def thermostatName = thermostatDetails[1]
         def connected = thermostatDetails[2]
         def thermostatRevision = thermostatDetails[3]
         def alertRevision = thermostatDetails[4]
         def runtimeRevision = thermostatDetails[5]
         
         if (connected) {
             if (i==0) {
                 tstatlist = Id
             }
             if ((i==MAX_TSTAT_BATCH) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
                 if (settings.trace) {
             
       	             sendEvent name: "verboseTrace", value: "iterateSetHold>about to call setHold for ${tstatlist}"
                     log.debug "iterateSethold> about to call setHold for ${tstatlist}"
                 }
                 setHold(tstatlist, coolingSetPoint, heatingSetPoint, tstatSettings) 
                 tstatlist = Id
             
             }
             else {
                 tstatlist = tstatlist + "," +  Id
             }     
             
         }        
    }      
}

// thermostatId could be a list of serial# separated by ",", no spaces 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml

def setHold(thermostatId, coolingSetPoint, heatingSetPoint, tstatSettings= []) {    
    Integer targetCoolTemp=null
    Integer targetHeatTemp=null
    def tstatParams=null
    
    if (settings.trace) {
        log.debug "setHold> called with values ${coolingSetPoint}, ${heatingSetPoint}, ${tstatSettings} for ${thermostatId}"
    }    
    def scale= getTemperatureScale()
    if (scale == 'C') {
        targetCoolTemp =  (cToF(coolingSetPoint)*10)  // need to send temperature in F multiply by 10
        targetHeatTemp =  (cToF(heatingSetPoint)*10) 
    }
    else {
    
        targetCoolTemp =  coolingSetPoint*10     // need to send temperature in F multiply by 10
        targetHeatTemp =  heatingSetPoint*10
        
    
    }
    if (settings.trace) {
	   sendEvent name: "verboseTrace", value: "setHold>about to build_body_req with settings=${tstatSettings}"
    }
    /* if settings.holdType has a value, include it in the list of params
    */
    
    if (settings.holdType != "") {
    
        tstatParams = [coolHoldTemp:targetCoolTemp.toString(),heatHoldTemp:targetHeatTemp.toString(),   
                       holdType:"${settings.holdType}"
                      ]
    
    }
    else {
    
        tstatParams = [coolHoldTemp:targetCoolTemp.toString(),heatHoldTemp:targetHeatTemp.toString()]
    }
    
    def bodyReq = build_body_request('setHold', null, thermostatId, tstatParams, tstatSettings)
    
    
    if (settings.trace) {
        log.debug "sethold> about to call api with body = ${bodyReq} for ${thermostatId}"
  	    sendEvent name: "verboseTrace", value: "setHold>about to call api with settings ${tstatSettings}, body=${bodyReq}"
    }    
    for (i in 0..1) { // try 2 times if needed
    
        api('setHold', bodyReq) {resp->
    
            def statusCode = resp.data.status.code
            def message = resp.data.status.message
            if (!statusCode) {
            
                if (settings.trace) {
                    log.debug "setHold> fan mode= ${data.thermostatList.settings.vent}, mode=${data.thermostatList.settings.hvacMode}" 
                    log.debug "setHold> current Temp= ${data.thermostatList[0].runtime.actualTemperature}, desiredHeat=${data.thermostatList[0].runtime.desiredHeat}"
    	            sendEvent name: "verboseTrace", value: "setHold>done for ${thermostatId}"
                }
                return
            
            }
            else {
                log.error "setHold> error= ${statusCode}, message = ${message}"
    	        sendEvent name: "verboseTrace", value: "setHold> ${statusCode} for ${thermostatId}"
            }
        }        
        
    }
}


// tstatType ='managementSet' or 'registered'


def iterateCreateVacation(tstatType, vacationName, targetCoolTemp, targetHeatTemp, targetStartDateTime, targetEndDateTime) {    
    Integer MAX_TSTAT_BATCH=25
    def tstatlist=null

    if (data.thermostatCount==null)
    {
    
         getThermostatSummary(tstatType)
    }
    if (settings.trace) {
        log.debug "iterateCreateVacation> about to loop ${data.thermostatCount}"
   	    sendEvent name: "verboseTrace", value: "iterateCreateVacation> about to loop ${data.thermostatCount} thermostat(s)"
    }    
    for (i in 0..data.thermostatCount-1) {
    
         def thermostatDetails = data.revisionList[i].split(':')
         def Id = thermostatDetails[0]
         def thermostatName = thermostatDetails[1]
         def connected = thermostatDetails[2]
         def thermostatRevision = thermostatDetails[3]
         def alertRevision = thermostatDetails[4]
         def runtimeRevision = thermostatDetails[5]
         
         if (connected) {
         
             if (i==0) {
                 tstatlist = Id
             }
             if ((i==MAX_TSTAT_BATCH) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
                 if (settings.trace) {
             
       	             sendEvent name: "verboseTrace", value: "iterateCreateVacation>about to call createVacation for ${tstatlist}"
                     log.debug "iterateCreateVacation>about to call createVacation for ${tstatlist}"
                 }
                 createVacation(tstatlist, vacationName, targetCoolTemp, targetHeatTemp, targetStartDateTime, targetEndDateTime) 
                 tstatlist = Id
             
             }
             else {
                 tstatlist = tstatlist + "," + Id
             }     
         }    
    
    }
   
}
// thermostatId could be a list of serial# separated by ",", no spaces 

def createVacation(thermostatId, vacationName, targetCoolTemp, targetHeatTemp, targetStartDateTime, targetEndDateTime) {    
     
     
    def vacationStartDate = String.format('%tY-%<tm-%<td',targetStartDateTime) 
    def vacationStartTime = String.format('%tH:%<tM:%<tS', targetStartDateTime) 
    def vacationEndDate = String.format('%tY-%<tm-%<td', targetEndDateTime) 
    def vacationEndTime = String.format('%tH:%<tM:%<tS', targetEndDateTime) 
    
    Integer targetCool=null
    Integer targetHeat=null
    
    def scale= getTemperatureScale()
    if (scale == 'C') {
        targetCool =  (cToF(targetCoolTemp)*10) as Integer // need to send temperature in F multiply by 10
        targetHeat =  (cToF(targetHeatTemp)*10) as Integer
    }
    else {
    
        targetCool =  targetCoolTemp*10 as Integer     // need to send temperature in F multiply by 10
        targetHeat =  targetHeatTemp*10 as Integer
    
    }

    def vacationParams = [ 
        name:vacationName,
        coolHoldTemp:targetCool.toString(),
        heatHoldTemp:targetHeat.toString(),
        startDate:vacationStartDate, 
        startTime:vacationStartTime,
        endDate:vacationEndDate,  
        endTime:vacationEndTime 
    ]
    def bodyReq = build_body_request('createVacation', null, thermostatId,  vacationParams, null)
    
    if (settings.trace) {
        log.debug "createVacation> about to call api with body = ${bodyReq} for ${thermostatId} "
    }
    api('createVacation', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
            
            log.debug "${vacationName} created for ${thermostatId}"
            if (settings.trace) {
             
                sendEvent name: "verboseTrace", value: "createVacation>done for ${thermostatId}"
            }
        }
        else {
            log.error "createVacation> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "createVacation>${statusCode} for ${thermostatId}"
        }
    } 
}

// tstatType ='managementSet' or 'registered'


def iterateDeleteVacation(tstatType, vacationName) {
    Integer MAX_TSTAT_BATCH=25
    def tstatlist=null

    if (data.thermostatCount == null) {
    
        getThermostatSummary(tstatType)
    }
    
    if (settings.trace) {
        log.debug "iterateDeleteVacation> about to loop ${data.thermostatCount}"
   	    sendEvent name: "verboseTrace", value: "iterateDeleteVacation> about to loop ${data.thermostatCount} thermostat(s)"
    }    
    for (i in 0..data.thermostatCount-1) {
    
         def thermostatDetails = data.revisionList[i].split(':')
         def Id = thermostatDetails[0]
         def thermostatName = thermostatDetails[1]
         def connected = thermostatDetails[2]
         def thermostatRevision = thermostatDetails[3]
         def alertRevision = thermostatDetails[4]
         def runtimeRevision = thermostatDetails[5]
         
         if (connected) {
         
             if (i==0) {
                 tstatlist = Id
             }
             if ((i==MAX_TSTAT_BATCH) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
                 if (settings.trace) {
             
      	             sendEvent name: "verboseTrace", value: "iterateDeleteVacation> about to call deleteVacation for ${tstatlist}"
                     log.debug "iterateDeleteVacation> about to call deleteVacation for ${tstatlist}"
                 }
                 deleteVacation(tstatlist, vacationName) 
                 tstatlist = Id
             
             }
             else {
                 tstatlist = tstatlist + "," + Id
             }     
             
         }    
    
    }
   
}

// thermostatId could be a list of serial# separated by ",", no spaces 

def deleteVacation(thermostatId, vacationName) {
     
    def vacationParams = [name:vacationName]
    
    def bodyReq = build_body_request('deleteVacation', null, thermostatId, vacationParams, null)
    
    if (settings.trace) {
        log.debug "deleteVacation> about to call api with body = ${bodyReq} for ${thermostatId}"
    }    

    api('deleteVacation', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
            
            if (settings.trace) {
            
                log.debug "deleteVacation>${vacationName} deleted done for ${thermostatId}"
    	        sendEvent name: "verboseTrace", value: "deleteVacation>done for ${thermostatId}"
            }
            
        }
        else {
            log.error "deleteVacation> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "deleteVacation>${statusCode} for ${thermostatId}"
        }
    }    
}

// tstatType ='managementSet' or 'registered'

def iterateResumeProgram(tstatType) {
    Integer MAX_TSTAT_BATCH=25
    def tstatlist=null

    if (data.thermostatCount==null){
    
         getThermostatSummary(tstatType)
    }
    if (settings.trace) {
        log.debug "iterateResumeProgram> about to loop ${data.thermostatCount}"
   	    sendEvent name: "verboseTrace", value: "iterateResumeProgram> about to loop ${data.thermostatCount} thermostat(s)"
    }    
    for (i in 0..data.thermostatCount-1) {
    
         def thermostatDetails = data.revisionList[i].split(':')
         def Id = thermostatDetails[0]
         def thermostatName = thermostatDetails[1]
         def connected = thermostatDetails[2]
         def thermostatRevision = thermostatDetails[3]
         def alertRevision = thermostatDetails[4]
         def runtimeRevision = thermostatDetails[5]
         
         if (connected) {
         
             if (i==0) {
                 tstatlist = Id
             }
             if ((i==MAX_TSTAT_BATCH) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
                 if (settings.trace) {
      	             sendEvent name: "verboseTrace", value: "iterateResumeProgram> about to call resumeProgram for ${tstatlist}"
                     log.debug "iterateResumeProgram> about to call resumeProgram for ${tstatlist}"
                 }    
                 resumeProgram(tstatlist)
                 tstatlist = Id
             
             }
             else {
                 tstatlist = tstatlist + "," + Id
             }     
             
         }    
    
    }
   
}

// thermostatId could be a list of serial# separated by ",", no spaces 

def resumeProgram(thermostatId) {
     
    
    def bodyReq = build_body_request('resumeProgram', null, thermostatId, null, null)

    if (settings.trace) {
        log.debug "resumeProgram> about to call api with body = ${bodyReq} for ${thermostatId}"
    }    
    

    // do the resumeProgram 3 times to make sure it is resumed.
    
    api('resumeProgram', bodyReq) {
        sendEvent name: "verboseTrace", value: "resumeProgram> 1st done for ${thermostatId}"            
    }
    api('resumeProgram', bodyReq) {
        sendEvent name: "verboseTrace", value: "resumeProgram> 2nd done for ${thermostatId}"            
    }
    api('resumeProgram', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
            
            if (settings.trace) {
            
                log.debug  "resumeProgram> final resume done for ${thermostatId}"            
                sendEvent name: "verboseTrace", value: "resumeProgram> final resume done for ${thermostatId}"            
            }
            
        }
        else {
            log.error "resumeProgram> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "resumeProgram>${statusCode} for ${thermostatId}"
        }
    
    }
}

// thermostatId could be a list of serial# separated by ",", no spaces 

def getThermostatInfo(thermostatId){
    
    if (settings.trace) {
        log.debug "getThermostatInfo> about to call build_body_request for thermostatId = ${thermostatId}..."
    }
    def bodyReq = build_body_request('thermostatInfo', 'registered', thermostatId, null, null)
    if (settings.trace) {
        log.debug "getThermostatInfo> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}..."
    }
    api('thermostatInfo', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
            
            data.thermostatList = resp.data.thermostatList
            def thermostatName = data.thermostatList.name  
            // divide the temperature by 10 before for display later.
            data.thermostatList[0].runtime.actualTemperature = data.thermostatList[0].runtime.actualTemperature /10
            data.thermostatList[0].runtime.desiredCool = data.thermostatList[0].runtime.desiredCool /10
            data.thermostatList[0].runtime.desiredHeat = data.thermostatList[0].runtime.desiredHeat /10
            if (settings.trace) {
     	        log.debug "getTstatInfo> got info for ${thermostatId} name=${thermostatName}, features=${resp.data}"
            }    
            def runtimeSettings = data.thermostatList.runtime
            def thermostatSettings = data.thermostatList.settings
            
            if (settings.trace) {
            
    	        sendEvent name: "verboseTrace", value: "getTstatInfo> currentTemp=${runtimeSettings.actualTemperature},${thermostatId},hvacMode = ${thermostatSettings.hvacMode}," + 
                    "vent = ${thermostatSettings.vent}, desiredHeat = ${runtimeSettings.desiredHeat} desiredCool = ${runtimeSettings.desiredCool}," +
                    "current Humidity = ${runtimeSettings.actualHumidity} desiredHumidity = ${runtimeSettings.desiredHumidity},humidifierMode= ${thermostatSettings.humidifierMode}," +
                    "desiredDehumidity =  ${runtimeSettings.desiredDehumidity} dehumidifierMode= ${thermostatSettings.dehumidifierMode}"

                log.debug "getTstatInfo> thermostatId = ${thermostatId}, name = ${thermostatName},  hvacMode = ${thermostatSettings.hvacMode}," +
                    "vent = ${thermostatSettings.vent}, desiredHeat = ${runtimeSettings.desiredHeat} desiredCool = ${runtimeSettings.desiredCool}," +
                    "current Humidity = ${runtimeSettings.actualHumidity} desiredHumidity = ${runtimeSettings.desiredHumidity},humidifierMode= ${thermostatSettings.humidifierMode}," +
                    "desiredDehumidity =  ${runtimeSettings.desiredDehumidity} dehumidifierMode= ${thermostatSettings.dehumidifierMode}"
            }            
        }
        else {
            log.error "getThermostatInfo> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "getTstatInfo>error=${statusCode} for ${thermostatId}"
        }
                                   
    }
    
}

def getThermostatSummary(tstatType) {  // tstatType ='managementSet' or 'registered'
    
   
    def bodyReq = build_body_request('thermostatSummary', tstatType, null, null, null)
    if (settings.trace) {
        log.debug "getThermostatSummary> about to call api with body = ${bodyReq}"
    }
    api('thermostatSummary', bodyReq) {resp->

        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
    	    data.revisionList = resp.data.revisionList
            data.thermostatCount= data.revisionList.size()
            data.revisionList.each() { 
                def thermostatDetails = it.split(':')
                def thermostatId = thermostatDetails[0]
                def thermostatName = thermostatDetails[1]
                def connected = thermostatDetails[2]
                def thermostatRevision = thermostatDetails[3]
                def alertRevision = thermostatDetails[4]
                def runtimeRevision = thermostatDetails[5]
                if (settings.trace) {
            
                    log.debug "getThermostatSummary> thermostatId = ${thermostatId}, name = ${thermostatName}, connected =${connected}"
        	        sendEvent name: "verboseTrace", value: "getTstatSummary> found ${thermostatId},name=${thermostatName},connected=${connected}"
                }

           }     
        }
        else {
            log.error "getThermostatSummary> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "getTstatSummary> error={statusCode}"
        }
    
    }

}


def refresh_tokens() {
 
    String URI_ROOT= "https://api.ecobee.com/"
    String appKey= settings.appKey
    
    def method =[
        headers: [
            'Content-Type': "application/json",
            'charset': "UTF-8"
        ],
        uri: "${URI_ROOT}/token?" +
            "grant_type=refresh_token&" + 
            "code=${data.auth.refresh_token}&" +
            "client_id=${appKey}" 
    ]        
    if (settings.trace) {
        log.debug "refresh_tokens> uri = ${method.uri}"
    }    

    def successRefreshTokens= {resp->
        if (settings.trace) {
            log.debug "refresh_tokens> response = ${resp.data}"
        }    
        data.auth.access_token = resp.data.access_token
        data.auth.refresh_token = resp.data.refresh_token
        data.auth.expires_in = resp.data.expires_in
        data.auth.token_type = resp.data.token_type
        data.auth.scope = resp.data.scope
    }

    try {
        httpPostJson(method, successRefreshTokens) 

    } catch ( java.net.UnknownHostException e) {
    	    log.error "refresh_tokens> Unknown host - check the URL " + method.uri
    	    sendEvent name: "verboseTrace", value: "refresh_tokens> Unknown host"
            return false
    } catch (java.net.NoRouteToHostException t) {
    	    log.error "refresh_tokens> No route to host - check the URL " + method.uri
    	    sendEvent name: "verboseTrace", value: "refresh_tokens> No route to host"
            return false
    } catch (java.io.IOException e) {
    	    log.error "refresh_tokens> Authentication error, ecobee site cannot be reached"
            sendEvent name: "verboseTrace", value: "refresh_tokens> Auth error"
            return false
    } catch (any) {
    	    log.error "refresh_tokens> general error " + method.uri
    	    sendEvent name: "verboseTrace", value: "refresh_tokens> general error at ${method.uri}"
            return false
    }   
    // determine token's expire time
    def now = new Date().getTime();
    def authexptime=  new Date( (now + (data.auth.expires_in *60 *1000)) ).getTime()
    data.auth.authexptime = authexptime
    if (settings.trace) {
            
        log.debug "refresh_tokens> expires in ${data.auth.expires_in} minutes"
	    log.debug "refresh_tokens> data_auth.expires_in in time = ${authexptime}"
        sendEvent name: "verboseTrace", value: "refresh_tokens>expire in ${data.auth.expires_in} minutes"
    }

    return true

}

def login() {

    if (settings.trace) {
        log.debug "login> about to call setAuthTokens"
    }    
    setAuthTokens()
 
    if (!isLoggedIn) {
        if (settings.trace) {
            log.debug "login> no access_token..., failed"
        }    
        exit
    }            
    
}

def getEcobeePinAndAuth() {
    String SCOPE = "smartWrite,ems"
    String URI_ROOT= "https://api.ecobee.com"
    String appKey= settings.appKey
    
    def method=[
        
        headers: [
            'Content-Type': "application/json",
            'charset': "UTF-8"
        ],
        uri: "${URI_ROOT}/authorize?" +
            "response_type=ecobeePin&" + 
            "client_id=${appKey}&" + 
            "scope=${SCOPE}" 
    ]
    def successEcobeePin = {response->
        if (settings.trace) {
            log.debug "getEcobeePinAndAuth> response = ${response.data}"
        }    
        data.auth=response.data 
        data.auth.code = response.data.code
 
        data.auth.expires_in = response.data.expires_in
        data.auth.interval = response.data.interval
        data.auth.ecobeePin = response.data.ecobeePin
        sendEvent name: "verboseTrace", value: "getEcobeePin>${data.auth.ecobeePin}"

        data.auth.access_token=null     // for isLoggedIn() later
        data.thermostatCount=null      // for iterate functions later
    }
    try {
        httpGet(method, successEcobeePin ) 
           
    } catch ( java.net.UnknownHostException e) {
        log.error "getEcobeePinAndAuth> Unknown host - check the URL " + method.uri
        sendEvent name: "verboseTrace", value: "getEcobeePin> Unknown host " + method.uri
        return
    } catch (java.net.NoRouteToHostException t) {
        log.error "getEcobeePinAndAuth> No route to host - check the URL " + method.uri
        sendEvent name: "verboseTrace", value: "getEcobeePin> No route to host " + method.uri
        return
    } catch (java.io.IOException e) {
        log.error "getEcobeePinAndAuth> Authentication error, bad URL or settings missing " + method.uri
        sendEvent name: "verboseTrace", value: "getEcobeePin> Auth error " + method.uri
        return
    } catch (any) {
        log.error "getEcobeePinAndAuth> general error " + method.uri
        sendEvent name: "verboseTrace", value: "getEcobeePin> general error " + method.uri
        return
    }    
    if (settings.trace) {
        log.debug "getEcobeePinAndAuth> ecobeePin= ${data.auth.ecobeePin}, authorizationCode=${data.auth.code},scope=${data.auth.scope} expires_in=${data.auth.expires_in} done"
    }    
}


    
    
def setAuthTokens(){
    String URI_ROOT= "https://api.ecobee.com"
    String appKey= settings.appKey

    def method=[
        headers: [
            'X-nl-protocol-version': 1,
            'Content-Type': "application/json",
            'charset': "UTF-8"
        ],
        uri: "${URI_ROOT}/token?" +
            "grant_type=ecobeePin&" + 
             "code=${data.auth.code}&" +
             "client_id=${appKey}"
    ]

  
    if (data.auth.access_token == null){
        def successTokens = {resp->
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
            
        } catch ( java.net.UnknownHostException e) {
    	    log.error "setAuthTokens> Unknown host - check the URL " + method.uri
    	    sendEvent name: "verboseTrace", value: "setAuthTokens> Unknown host " + method.uri
            return
        } catch (java.net.NoRouteToHostException t) {
    	    log.error "setAuthTokens> No route to host - check the URL " + method.uri
    	    sendEvent name: "verboseTrace", value: "setAuthTokens> No route to host" + method.uri
            return
        } catch (java.io.IOException e) {
    	    log.error "setAuthTokens> Auth error, ecobee site cannot be reached " + method.uri
            sendEvent name: "verboseTrace", value: "setAuthTokens> Auth error " + method.uri
            return
        } catch (any) {
    	    log.error "setAuthTokens> general error " + method.uri
    	    sendEvent name: "verboseTrace", value: "setAuthTokens>general error " + method.uri
            return
        }   
         // determine token's expire time
        def now = new Date().getTime();
        def authexptime=  new Date( (now + (data.auth.expires_in *60 *1000)) ).getTime()
        data.auth.authexptime = authexptime
        if (settings.trace) {
        	log.debug "setAuthTokens> expires in ${data.auth.expires_in} minutes"
            log.debug "setAuthTokens> data_auth.expires_in in time = ${authexptime}"
     	    sendEvent name: "verboseTrace", value: "setAuthTokens>expire in ${data.auth.expires_in} minutes"
        }    
    }
}


def isLoggedIn() {

    if (data.auth == null) {
    
        if (settings.trace) {
            log.debug "isLoggedIn> no data auth"
        }    
        return false
 
    }
    else {
        if (data.auth.access_token == null) {
             if (settings.trace) {
         	     log.debug "isLoggedIn> no access token"
        	     return false
             }   
        }    
    }
    return true
}

def isTokenExpired() {
    
    def now = new Date().getTime();
    if (settings.trace) {
        log.debug "isTokenExpired> check expires_in: ${data.auth.authexptime} > time now: ${now}"
    }
    
    if (data.auth.authexptime > now) {
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

def cToF(temp) {
    return (temp * 1.8 + 32)
}
 
def fToC(temp) {
    return (temp - 32) / 1.8
}
