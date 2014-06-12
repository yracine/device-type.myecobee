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
 *        (e) <ecobeeType> set to 'registered' (by default) or 'managementSet'
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
    	input("holdType", "text", title: "holdType", description: "Set it 'nextTransition' or 'indefinite' (latter by default)")
    	input("ecobeeType", "text", title: "ecobee Tstat Type", description: "Set it to 'registered' or 'managementSet'(former by default)")
	}
metadata {
	// Automatically generated. Make future change here.
	definition (name: "My Ecobee Device", author: "Yves Racine") {
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Polling"
		capability "Thermostat"

		attribute "thermostatName", "string"
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
		attribute "groups", "string"
		attribute "equipementStatus", "string"
		attribute "programScheduleName", "string"
		attribute "programFanMode", "string"
		attribute "programType", "string"
		attribute "programCoolTemp", "string"
		attribute "programHeatTemp", "string"
		attribute "weatherDateTime", "string"
		attribute "weatherStation", "string"
		attribute "weatherCondition", "string"
		attribute "weatherTemperature", "string"
		attribute "weatherPressure", "string"
		attribute "weatherRelativeHumidity", "string"
		attribute "weatherWindSpeed", "string"
		attribute "weatherWindDirection", "string"
		attribute "weatherPop", "string"
		attribute "weatherTempHigh", "string"
		attribute "weatherTempLow", "string"

		command "setHumidifierLevel"
		command "setDehumidifierLevel"
		command "setFanMinOnTime"
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
		command "setAuthTokens"
		command "setHold"
		command "heatLevelUp"
		command "heatLevelDown"
		command "coolLevelUp"
		command "coolLevelDown"
		command "resumeThisTstat"
		command "auxHeatOnly"
		command "setThermostatFanMode"
		command "humidifierOff"
		command "dehumidifierOff"
		command "humidifierAuto"
		command "updateGroup"
		command "getGroups"
		command "iterateUpdateGroup"
		command "createGroup"
		command "deleteGroup"
		command "dehumidifierOn"
	}

    definition (name: "My Ecobee Device", author: "Yves Racine") {
        capability "Polling"
        capability "Thermostat"
        capability "Relative Humidity Measurement"
        capability "Temperature Measurement"

        attribute "thermostatName", "string"
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
        attribute "groups", "string"
        attribute "equipementStatus", "string"
        
        //program attributes        
        
        attribute "programScheduleName", "string"
        attribute "programFanMode", "string"
        attribute "programType", "string"
        attribute "programCoolTemp", "string"
        attribute "programHeatTemp", "string"

        
        // weather attributes         
        attribute "weatherDateTime", "string"
        attribute "weatherStation", "string"
        attribute "weatherCondition","string"	
        attribute "weatherTemperature","string"
        attribute "weatherPressure", "string"
        attribute "weatherRelativeHumidity", "string"
        attribute "weatherWindSpeed", "string"	 
        attribute "weatherWindDirection", "string"
        attribute "weatherPop","string"
        attribute "weatherTempHigh", "string"
        attribute "weatherTempLow", "string"
        
        command "humidifierAuto"
        command "humidifierOff"
        command "setHumidifierLevel"
        command "dehumidifierOn"
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
        command "getGroups"    
        command "updateGroup"
        command "createGroup"
        command "deleteGroup"
        command "iterateUpdateGroup"
    }

    simulator {
        // TODO: define status and reply messages here
    }
 
    tiles {
        valueTile("name", "device.thermostatName", inactiveLabel: false, width: 1,height: 1,decoration: "flat") {
            state "default", label:'${currentValue}'
        }
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
            state "default", label:'Humidity\n${currentValue}%', unit:"humidity"
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
        valueTile("equipementStatus", "device.equipementStatus", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
             state "default", label:'Run\n${currentValue}'
        }

        // Program Tiles
        valueTile("programScheduleName", "device.programScheduleName", inactiveLabel: false,width: 1, height: 1, decoration: "flat") {
            state "default", label:'Mode\n${currentValue}'
        }
        valueTile("programType", "device.programType", inactiveLabel: false,width: 1, height: 1, decoration: "flat") {
            state "default", label:'ProgType\n${currentValue}'
        }
        valueTile("programCoolTemp", "device.programCoolTemp",inactiveLabel: false,width: 1, height: 1,decoration: "flat") {
            state "default", label:'ProgCool ${currentValue}°'
        }
        valueTile("programHeatTemp", "device.programHeatTemp", inactiveLabel: false,width: 1, height: 1, decoration: "flat") {
            state "default", label:'ProgHeat ${currentValue}°'
        }
        standardTile("resProgram", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state "default", label:'ResumeProg',action:"resumeThisTstat", icon:"st.Office.office7",backgroundColor:"#ffffff"
        }
        // Weather Tiles

        valueTile("weatherCondition", "device.weatherCondition", inactiveLabel: false,width: 2, height: 1, decoration: "flat") {
            state "default", label:'Forecast ${currentValue}'
        }
        valueTile("weatherTemperature", "device.weatherTemperature", inactiveLabel: false,width: 1, height: 1, decoration: "flat") {
            state "default", label:'Out Temp ${currentValue}°', unit:"C"
        }
        valueTile("weatherRelativeHumidity", "device.weatherRelativeHumidity", inactiveLabel: false,width: 1, height: 1.5, decoration: "flat") {
            state "default", label:'Out Hum ${currentValue}%', unit:"humidity"
        }
        valueTile("weatherTempHigh", "device.weatherTempHigh", inactiveLabel: false, width: 1, height: 1,decoration: "flat") {
            state "default", label:'Fcast High ${currentValue}°', unit:"C"
        }
        valueTile("weatherTempLow", "device.weatherTempLow", inactiveLabel: false,width: 1, height: 1, decoration: "flat") {
            state "default", label:'Fcast Low ${currentValue}°', unit:"C"
        }
        valueTile("weatherPressure", "device.weatherPressure", inactiveLabel: false, width: 1, height: 1,decoration: "flat") {
            state "default", label:'Pressure ${currentValue}', unit:"hpa"
        }
        valueTile("weatherWindDirection", "device.weatherWindDirection", inactiveLabel: false, width: 1, height: 1,decoration: "flat") {
            state "default", label:'W.Dir ${currentValue}'
        }
        valueTile("weatherWindSpeed", "device.weatherWindSpeed", inactiveLabel: false, width: 1, height: 1,decoration: "flat") {
            state "default", label:'W.Speed ${currentValue}'
        }
        valueTile("weatherPop", "device.weatherPop", inactiveLabel: false, width: 1, height: 1,decoration: "flat") {
            state "default", label:'PoP\n${currentValue}%', unit:"%"
        }
        valueTile("groups", "device.groups", inactiveLabel: false, width: 2, height: 1,decoration: "flat") {
            state "default", label:'${currentValue}%'
        }
       
        standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
            state "default", action:"polling.poll", icon:"st.secondary.refresh"
        }


        main "temperature"
        details(["name","groups","temperature", "mode", "fanMode", "heatLevelDown", "heatingSetpoint", "heatLevelUp", "coolLevelDown", "coolingSetpoint", "coolLevelUp", 
                "equipementStatus", "humidity", "programScheduleName",  "programType", "programCoolTemp", "programHeatTemp",  "resProgram",
                "weatherCondition", "weatherTemperature", "weatherRelativeHumidity", "weatherTempHigh", 
                "weatherTempLow", "weatherPressure", "weatherWindDirection", "weatherWindSpeed", "weatherPop","refresh",])

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


def auto() {
    setThermostatMode('auto')
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

def dehumidifierOn() {
    setDehumidifierMode('on')
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
    def ecobeeType = 'registered'   // by default, the ecobeeType is 'registered'
    
    
    if (settings.ecobeeType =='managementSet') {
    
        ecobeeType = settings.ecobeeType    
    }
         
    
    getThermostatSummary(ecobeeType)
    getThermostatInfo(settings.thermostatId)
        
    sendEvent(name: 'thermostatName', value: data.thermostatList[0].name)
    
    sendEvent(name: 'thermostatMode', value: data.thermostatList[0].settings.hvacMode)
    sendEvent(name: 'thermostatFanMode', value: data.thermostatList[0].settings.vent)
    sendEvent(name: 'humidity', value: data.thermostatList[0].runtime.actualHumidity, unit:"%")
    sendEvent(name: 'thermostatMode', value: data.thermostatList[0].settings.hvacMode)
    
    
    if (data.thermostatList.settings.hasHumidifier) {
        sendEvent(name: 'humidifierMode', value: data.thermostatList[0].settings.humidifierMode)
        sendEvent(name: 'humidifierLevel', value: data.thermostatList[0].settings.humidity,unit:"%")
        
    }
    if (data.thermostatList.settings.hasDehumidifier) {
        sendEvent(name: 'dehumidifierMode', value: data.thermostatList[0].settings.dehumidifierMode)
        sendEvent(name: 'dehumidifierLevel', value: data.thermostatList[0].settings.dehumidifierLevel, unit:"%")
    }

    // post weather events
    
    sendEvent(name: 'weatherStation', value: data.thermostatList[0].weather.weatherStation)
    sendEvent(name: 'weatherDateTime', value: data.thermostatList[0].weather.forecasts[0].dateTime)
    sendEvent(name: 'weatherCondition', value: data.thermostatList[0].weather.forecasts[0].condition)
    sendEvent(name: 'weatherPressure', value: data.thermostatList[0].weather.forecasts[0].pressure, unit:"hpa")
    sendEvent(name: 'weatherRelativeHumidity', value: data.thermostatList[0].weather.forecasts[0].relativeHumidity, 
        unit:"%")
    sendEvent(name: 'weatherWindDirection', value: data.thermostatList[0].weather.forecasts[0].windDirection + " Winds")
    sendEvent(name: 'weatherPop', value: data.thermostatList[0].weather.forecasts[0].pop, unit:"%")

    // post program events

    if (settings.trace) {
         
        log.debug "poll>climates = ${data.thermostatList[0].program.climates}"
        log.debug "poll>Current Climate Ref=${data.thermostatList[0].program.currentClimateRef}"
    }
   
    // Get the current climate object
    def currentClimate 
    
    data.thermostatList[0].program.climates.each() {
    
         if (it.climateRef== data.thermostatList[0].program.currentClimateRef){
             currentClimate = it
             exit
         }
    }
     
    sendEvent(name: 'programScheduleName', value: currentClimate.name )
    sendEvent(name: 'programFanMode', value: currentClimate.vent)
    sendEvent(name: 'programType', value: currentClimate.type)

    def scale = getTemperatureScale()
    if (scale =='C') {
        float actualTemp= fToC(data.thermostatList[0].runtime.actualTemperature)
        float desiredCoolTemp =  fToC(data.thermostatList[0].runtime.desiredCool)
        float desiredHeatTemp = fToC(data.thermostatList[0].runtime.desiredHeat)
        def actualTempFormat = String.format('%2.1f', actualTemp.round(1))
        def desiredCoolFormat = String.format('%2.1f', desiredCoolTemp.round(1))
        def desiredHeatFormat = String.format('%2.1f', desiredHeatTemp.round(1))
        sendEvent(name: 'temperature', value: actualTempFormat, 
            unit:"C", state: data.thermostatList[0].settings.hvacMode)
        sendEvent(name: 'coolingSetpoint', value: desiredCoolFormat, unit: "C")
        sendEvent(name: 'heatingSetpoint', value:  desiredHeatFormat, unit: "C")
        
        // post weather temps
        float weatherTemp =  fToC(data.thermostatList[0].weather.forecasts[0].temperature)
        float weatherTempHigh = fToC(data.thermostatList[0].weather.forecasts[0].tempHigh)
        float weatherTempLow = fToC(data.thermostatList[0].weather.forecasts[0].tempLow)
        def weatherTempFormat = String.format('%2.1f', weatherTemp.round(1))
        def weatherHighFormat = String.format('%2.1f', weatherTempHigh.round(1))
        def weatherLowFormat = String.format('%2.1f', weatherTempLow.round(1))
        sendEvent(name: 'weatherTemperature', value: weatherTempFormat, unit: "C")
        sendEvent(name: 'weatherTempHigh', value: weatherHighFormat, unit: "C")
        sendEvent(name: 'weatherTempLow', value: weatherLowFormat, unit: "C")
        
        float windSpeed = milesToKm((data.thermostatList[0].weather.forecasts[0].windSpeed.toFloat()/1000))
        String windSpeedFormat = String.format('%2.1f',windSpeed.round(1))
        sendEvent(name: 'weatherWindSpeed', value: windSpeedFormat, unit:'kmh')

        // post program temps

        float programHeatTemp= fToC((currentClimate.heatTemp/10))
        float programCoolTemp = fToC((currentClimate.coolTemp/10))
        def programCoolFormat = String.format('%2.1f', programCoolTemp.round(1))
        def programHeatFormat = String.format('%2.1f', programHeatTemp.round(1))
        
        sendEvent(name: 'programCoolTemp', value: programCoolFormat, unit: "C")
        sendEvent(name: 'programHeatTemp', value: programHeatFormat,  unit: "C")


    }        
    else {
    
        sendEvent(name: 'temperature', value: (data.thermostatList[0].runtime.actualTemperature), 
            unit:"F", state: data.thermostatList[0].settings.hvacMode)
        sendEvent(name: 'coolingSetpoint', value: (data.thermostatList[0].runtime.desiredCool), unit: "F")
        sendEvent(name: 'heatingSetpoint', value: (data.thermostatList[0].runtime.desiredHeat), unit: "F")

       // post weather temps
        sendEvent(name: 'weatherTemperature', value: data.thermostatList[0].weather.forecasts[0].temperature,  
            unit: "F")
        sendEvent(name: 'weatherTempHigh', value: data.thermostatList[0].weather.forecasts[0].tempHigh,
            unit: "F")
        sendEvent(name: 'weatherTempLow', value: data.thermostatList[0].weather.forecasts[0].tempLow, 
            unit: "F")
        float windSpeed = data.thermostatList[0].weather.forecasts[0].windSpeed.toFloat()/1000
        String windSpeedFormat = String.format('%2.1f',windSpeed.round(1))
        sendEvent(name: 'weatherWindSpeed', value: windSpeedFormat, unit:'mph')

        // post program temps

        sendEvent(name: 'programCoolTemp', value: (currentClimate.coolTemp/10), unit: "F")
        sendEvent(name: 'programHeatTemp', value: (currentClimate.heatTemp/10), unit: "F")
    
    }
    
    def equipStatus = 'Idle'
    
    data.statusList.each() {
        def equipStatusDetails = it.split(':')
        if (equipStatusDetails[0] == settings.thermostatId) {     // Get the right equipement status for the thermostatId

            if (equipStatusDetails.size() >1) {
            
                equipStatus = equipStatusDetails[1]
            }    
            if (settings.trace) {
                log.debug "poll> thermostatId = ${equipStatusDetails[0]}, status= ${equipStatus}"
                sendEvent name: "verboseTrace", value: "poll> thermostatId = ${equipStatusDetails[0]}, status= ${equipStatus}"
            }
            exit
        }
            
    }
    if (equipStatus != 'Idle') {
        equipStatus = equipStatus + ' running'
    }
    
    sendEvent(name: 'equipementStatus', value: equipStatus)
    
    log.debug "poll> about to execute getGroups"
    sendEvent name: "verboseTrace", value: "poll> about to execute getGroups"
        
    // get Groups associated to this thermostatId
        
    getGroups(settings.thermostatId)
         
    // post group names associated to this thermostatId

    def groupList = 'Groups '
    if (data.groups.size() == 0) {
            
        groupList = 'No groups'
    }
    else {
        for (i in 0..data.groups.size()-1) { 
            groupList = groupList + ' \n' + data.groups[i].groupName
        }

    }
    if (settings.trace) {
        log.debug "poll> thermostatId = ${settings.thermostatId}, groups= ${groupList}"
        sendEvent name: "verboseTrace", value: "poll> thermostatId = ${settings.thermostatId}, groups= ${groupList}"
    }
    sendEvent(name: 'groups', value: groupList)
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
        'deleteVacation':    [uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
        'getGroups':         [uri: "${URI_ROOT}/group?format=json&body=${args_encoded}", type: 'get'],
        'updateGroup':       [uri: "${URI_ROOT}/group?format=json", type: 'post']
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
// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 

private def build_body_request(method, tstatType, thermostatId,  tstatParams =[], tstatSettings=[]) {

    def selectionJson=null
    def selection=null
    
    if (settings.trace) {
        log.debug "build_body_request> about to build selection for method= ${method} & thermostatId= ${thermostatId}"
    }
    if (method == 'thermostatSummary') {
    
       
        selection = [selection: [selectionType: tstatType, selectionMatch: '',includeEquipmentStatus:'true']
                    ]                                    
                       
    }
    else if (method == 'thermostatInfo') {
    
        selection = [selection: [selectionType:'thermostats',
                                    selectionMatch:thermostatId, 
                                    includeSettings:'true',
                                    includeRuntime:'true',
                                    includeProgram:'true',
                                    includeWeather:'true'
                                ]
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
            log.debug "build_tstat_request done>  for method = ${method}, body w/o settings= ${simpleBodyJson}"
        }    
        return simpleBodyJson
    }
    else if (method == 'resumeProgram') {

        def function_clause = [type:method]
        def simpleBody= [functions:[function_clause],selection:selection]
        
        def simpleBodyJson = new JsonBuilder(simpleBody)
        if (settings.trace) {
            log.debug "build_tstat_request done>  for method = ${method}, body w/o params= ${simpleBodyJson}"
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
             if ((i==(MAX_TSTAT_BATCH-1)) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
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

// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
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
             if ((i==(MAX_TSTAT_BATCH-1)) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
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
// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 

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
             if ((i==(MAX_TSTAT_BATCH-1)) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
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

// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 

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
             if ((i==(MAX_TSTAT_BATCH-1)) || (i==(data.thermostatCount-1))){  // process a batch of maximum 25 thermostats according to API doc
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

// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 

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

// Only valid for Smart and Antenna thermostats
// Get all groups related to a thermostatId
// thermostatId can be only 1 thermostat (not a list)

def getGroups(thermostatId) {    

    
    if (settings.trace) {
        log.debug "getGroups> about to assemble bodyReq thermostatId = ${thermostatId}..."
        sendEvent name: "verboseTrace", value: "getGroups> about to assemble bodyReq thermostatId = ${thermostatId}..."        
    }

    def bodyReq = '{"selection":{"selectionType":"registered"}}'       
    
    if (settings.trace) {
        log.debug "getGroups> about to call api with body = ${bodyReq}..."
    }
    api('getGroups', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        
        if (!statusCode) {
  
            data.groups = resp.data.groups
            if (settings.trace) {
            
                log.debug  "getGroups>done for ${thermostatId}," +
                           "group size = ${data.groups.size()}, groups data = ${data.groups}"   
                sendEvent name: "verboseTrace", value: "getGroups>done for ${thermostatId}," +
                           "group size = ${data.groups.size()}, groups data = ${data.groups}"   
            }
          
            if (data.groups.size() ==0) {
                return
            }
            if ((thermostatId != null) &&  (thermostatId != "")) {
                           
                def groupData=null
                if (settings.trace) {
            
                    log.debug  "getGroups>about to process ${data.groups}" 
                    sendEvent name: "verboseTrace", value: "getGroups>about to process ${data.groups}"
                }
        
                int k=0
                if (data.groups.thermostats.size() > 0) {

                    for (i in 0..data.groups.size()-1) {
                        for (j in 0..data.groups.thermostats.size()-1) {
                            if (data.groups[i].thermostats[j] == thermostatId) {
                                if (settings.trace) {
            
                                    log.debug  "getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}" 
                                    sendEvent name: "verboseTrace", value: "getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}"
                                }
                            }
                            else {
                                data.groups[i] = ' '   // Not the right group for this thermostat
                            
                            }
                        }    
                    }    
                }
            }   
        }
        else {
            log.error "getGroups>> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "getGroups>>${statusCode} for ${thermostatId}"
        }
    
    }
    

}

// Only valid for Smart and Antenna thermostats
// Get all groups related to a thermostatId and update them with the groupSettings
// thermostatId can be only 1 thermostat (not a list), if null or empty, then defaulted to this thermostatId (settings)
// groupSettings could be a map of settings separated by ",", no spaces; 
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml


def iterateUpdateGroup(thermostatId, groupSettings=[]) {

    if ((thermostatId == null) || (thermostatId == "")) {
        thermostatId = settings.thermostatId
    }
    if (data.groups==null){
    
         getGroups(thermostatId)
    }
    if (settings.trace) {
        log.debug "iterateUpdateGroup> about to loop ${data.groups.size()}"
   	    sendEvent name: "verboseTrace", value: "iterateUpdateGroup> about to loop ${data.groups.size()}"    
    }
    if (data.groups.size()==0) {
        return
    }
    for (i in 0..data.groups.size()-1) {
    
         def groupName = data.groups[i].groupName
         def groupRef = data.groups[i].groupRef
         def synchronizeSchedule = data.groups[i].synchronizeSchedule
         def synchronizeVacation = data.groups[i].synchronizeVacation
         def synchronizeSystemMode =data.groups[i].synchronizeSystemMode
         if (settings.trace) {
      	         sendEvent name: "verboseTrace", value: "iterateUpdateGroup> about to call updateGroup for ${groupName}"
                 log.debug "iterateUpdateGroup> about to call updateGroup for ${groupName}, groupRef= ${groupRef}," +
                     "synchronizeSystemMode=${synchronizeSystemMode}, synchronizeVacation=${synchronizeVacation}" +
                     "synchronizeSchedule=${synchronizeSchedule}"
     
         }    
         updateGroup(groupRef, groupName, thermostatId, groupSettings)
         if (settings.trace) {
            log.debug "iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}"
   	        sendEvent name: "verboseTrace", value: "iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}"   
         }     
    
    }
   
}

// Only valid for Smart and Antenna thermostats
// If groupRef is not provided, it is assumed that a group creation needs to be done
// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//    if no thermostatID is provided, it is defaulted to this thermostatId (setttings)
// groupSettings could be a map of settings separated by ",", no spaces; 
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml

def updateGroup(groupRef, groupName, thermostatId, groupSettings=[]) {    
    String updateGroupParams
    
    def groupSettingsJson = new JsonBuilder(groupSettings)
    def groupSet = groupSettingsJson.toString().minus('{').minus('}')
    
      
    if ((thermostatId == null) || (thermostatId == "")) {
        thermostatId = settings.thermostatId
    }
    
    
    if (settings.trace) {
        log.debug "updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}..."
        sendEvent name: "verboseTrace", value: "updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}"           
    }
    
    if ((groupRef != null) && (groupRef !="")) {
   
        updateGroupParams = '"groupRef":"' + groupRef + '","groupName":"' + groupName + 
           '",' + groupSet  + ',"thermostats":["' + thermostatId + '"]' 
    }
    else {
    
   
        updateGroupParams =  '"groupName":"'  + groupName + '",' + groupSet  + ',"thermostats":["' + thermostatId + '"]'
    
    }
    if (settings.trace) {
        log.debug "updateGroup> updateGroupParams=${updateGroupParams}"
        sendEvent name: "verboseTrace", value: "updateGroup> updateGroupParams=${updateGroupParams}"
    }
    def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' + updateGroupParams + '}]}'       
    
    if (settings.trace) {
        log.debug "updateGroup> about to call api with body = ${bodyReq} for groupName =${groupName},thermostatId = ${thermostatId}..."
    }
    api('updateGroup', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
  
            if (settings.trace) {
            
                log.debug  "updateGroup>done for groupName =${groupName}, ${thermostatId}"            
                sendEvent name: "verboseTrace", value: "updateGroup>done for groupName =${groupName}, ${thermostatId}"            
            }
            
        }
        else {
            log.error "updateGroup> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "updateGroup>${statusCode} for ${thermostatId}"
        }
    
    }

  
}
def deleteGroup(groupRef, groupName) {    
    String updateGroupParams
    
    if ((groupRef != null) && (groupRef !="")) {
   
        updateGroupParams = '"groupRef":"' + groupRef + '","groupName":"' + groupName + '"'
    }
    else {
    
   
        updateGroupParams =  '"groupName":"'  + groupName + '"'
    
    }
    if (settings.trace) {
        log.debug "deleteGroup> updateGroupParams=${updateGroupParams}"
        sendEvent name: "verboseTrace", value: "deleteGroup> updateGroupParams=${updateGroupParams}"
    }
    def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' + updateGroupParams + '}]}'       
    
    if (settings.trace) {
        log.debug "deleteGroup> about to call api with body = ${bodyReq} for groupName =${groupName}, groupRef = ${groupRef}"
    }
    api('updateGroup', bodyReq) {resp->
        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
  
            if (settings.trace) {
            
                log.debug  "deleteGroup>done for groupName =${groupName}, groupRef = ${groupRef}"            
                sendEvent name: "verboseTrace", value: "deleteGroup>done for groupName =${groupName},groupRef = ${groupRef}"            
            }
            
        }
        else {
            log.error "deleteGroup> error= ${statusCode}, message = ${message}"
    	    sendEvent name: "verboseTrace", value: "deteteGroup>${statusCode} for ${groupName},groupRef = ${groupRef}"
        }
    
    }

  
}

// Only valid for Smart and Antenna thermostats
// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//    if no thermostatID is provided, it is defaulted to this thermostatId (setttings)
// groupSettings could be a map of settings separated by ",", no spaces; 
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml

def createGroup(groupName, thermostatId, groupSettings=[]) {    
    
    updateGroup(null, groupName, thermostatId, groupSettings)  
}

// thermostatId would be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 

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
            data.thermostatList[0].weather.forecasts[0].temperature = data.thermostatList[0].weather.forecasts[0].temperature/10
            data.thermostatList[0].weather.forecasts[0].tempLow = data.thermostatList[0].weather.forecasts[0].tempLow/10
            data.thermostatList[0].weather.forecasts[0].tempHigh = data.thermostatList[0].weather.forecasts[0].tempHigh/10
            
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

// tstatType ='managementSet' or 'registered'
def getThermostatSummary(tstatType) {  
    
   
    def bodyReq = build_body_request('thermostatSummary', tstatType, null, null, null)
    if (settings.trace) {
        log.debug "getThermostatSummary> about to call api with body = ${bodyReq}"
    }
    api('thermostatSummary', bodyReq) {resp->

        def statusCode = resp.data.status.code
        def message = resp.data.status.message
        if (!statusCode) {
    	    data.revisionList = resp.data.revisionList
            data.statusList = resp.data.statusList
            data.thermostatCount= data.revisionList.size()
            for (i in 0..data.thermostatCount-1) {
                def thermostatDetails = data.revisionList[i].split(':')
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
                def equipStatusDetails = data.statusList[i].split(':')
                if (settings.trace) {
                    String equipStatus= 'Idle'
                    if (equipStatusDetails.size() > 1) {
                         equipStatus = equipStatusDetails[1] + " running"
                    }     
                    log.debug "getThermostatSummary> thermostatId = ${equipStatusDetails[0]}, status= ${equipStatus}"
        	        sendEvent name: "verboseTrace", value: "getThermostatSummary> thermostatId = ${equipStatusDetails[0]},status= ${equipStatus}"
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
    def successEcobeePin = {resp->
        if (settings.trace) {
            log.debug "getEcobeePinAndAuth> response = ${resp.data}"
        }    
        data.auth=resp.data 
        data.auth.code = resp.data.code
 
        data.auth.expires_in = resp.data.expires_in
        data.auth.interval = resp.data.interval
        data.auth.ecobeePin = resp.data.ecobeePin
        sendEvent name: "verboseTrace", value: "getEcobeePin>${data.auth.ecobeePin}"

        data.auth.access_token=null     // for isLoggedIn() later
        data.thermostatCount=null      // for iterate functions later
        data.groups=null               // for iterateUpdateGroups later
  
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

def milesToKm(distance) {
    return (distance * 1.609344)
}

