/***
 *  My Ecobee Device
 *  Copyright 2014 Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Code: https://github.com/yracine/device-type.myecobee
 *  Refer to readme file for installation instructions.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 * 
 */
import groovy.json.JsonBuilder
import java.net.URLEncoder
// for the UI
preferences {
	input("thermostatId", "text", title: "Serial #", description:
		"The serial number of your thermostat (no spaces)")
	input("appKey", "text", title: "App Key", description:
		"The application key given by Ecobee (no spaces)")
	input("trace", "text", title: "trace", description:
		"Set it to true to enable tracing (no spaces) or leave it empty (no tracing)"
	)
	input("holdType", "text", title: "holdType", description:
		"Set it to nextTransition or indefinite (latter by default)")
	input("ecobeeType", "text", title: "ecobee Tstat Type", description:
		"Set it to registered (by default) or managementSet (no spaces)")
}
metadata {
	// Automatically generated. Make future change here.
	definition(name: "My Ecobee Device", author: "Yves Racine",  namespace: "yracine") {
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Polling"
		capability "Thermostat"
		capability "Refresh"
		capability "Presence Sensor"

		attribute "thermostatName", "string"
		attribute "heatLevelUp", "string"
		attribute "heatLevelDown", "string"
		attribute "coolLevelUp", "string"
		attribute "coolLevelDown", "string"
		attribute "verboseTrace", "string"
		attribute "fanMinOnTime", "string"
		attribute "humidifierMode", "string"
		attribute "dehumidifierMode", "string"
		attribute "humidifierLevel", "string"
		attribute "dehumidifierLevel", "string"
		attribute "condensationAvoid", "string"
		attribute "groups", "string"
		attribute "equipmentStatus", "string"
		attribute "alerts", "string"
		attribute "programScheduleName", "string"
		attribute "programFanMode", "string"
		attribute "programType", "string"
		attribute "programCoolTemp", "string"
		attribute "programHeatTemp", "string"
		attribute "programEndTimeMsg", "string"
		attribute "weatherDateTime", "string"
		attribute "weatherSymbol", "string"
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
		attribute "plugName", "string"
		attribute "plugState", "string"
		attribute "plugSettings", "string"
		attribute "hasHumidifier", "string"
		attribute "hasDehumidifier", "string"
		attribute "hasErv", "string"
		attribute "hasHrv", "string"
		attribute "ventilatorMinOnTime", "string"
		attribute "ventilatorMode", "string"
		attribute "programDisplayName", "string"
		attribute "thermostatOperatingState", "string"        
		attribute "climateList", "string"	
		attribute "modelNumber", "string"
		attribute "followMeComfort", "string"
		attribute "autoAway", "string"
        
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
		command "resumeThisTstat"        
		command "setAuthTokens"
		command "setHold"
		command "setHoldExtraParams"
		command "heatLevelUp"
		command "heatLevelDown"
		command "coolLevelUp"
		command "coolLevelDown"
		command "auxHeatOnly"
		command "setThermostatFanMode"
		command "dehumidifierOff"
		command "dehumidifierOn"
		command "humidifierOff"
		command "humidifierAuto"
		command "setHumidifierLevel"
		command "setDehumidifierLevel"
		command "updateGroup"
		command "getGroups"
		command "iterateUpdateGroup"
		command "createGroup"
		command "deleteGroup"
		command "updateClimate"
		command "iterateUpdateClimate"
		command "createClimate"
		command "deleteClimate"
		command "setClimate"
		command "iterateSetClimate"
		command "controlPlug" // not tested as I don't own a smartplug
		command "ventilatorOn"
		command "ventilatorAuto"
		command "ventilatorOff"
		command "ventilatorAuto"
		command "setVentilatorMinOnTime"
		command "awake"
		command "away"
		command "present"
		command "home"
		command "sleep"
		command "quickSave"
		command "setThisTstatClimate"
		command "setThermostatSettings"
		command "iterateSetThermostatSettings"
		command "getThermostatOperatingState"
		command "getEquipmentStatus"
		command "refreshChildTokens" 
		command "autoAway"
		command "followMeComfort"
}        

simulator {
		// TODO: define status and reply messages here
	}
	tiles {
		valueTile("name", "device.thermostatName", inactiveLabel: false, width: 1,
			height: 1, decoration: "flat") {
			state "default", label: '${currentValue}\n '
		}
		valueTile("groups", "device.groups", inactiveLabel: false, width: 1, 
			height: 1, decoration: "flat") {
			state "default", label: '${currentValue}%'
		}
		valueTile("temperature", "device.temperature", width: 2, height: 2,
			canChangeIcon: true) {
//		If one prefers Celcius over Farenheits, just comment out the temperature in Farenheits 
//		and remove the comment below to have the right color scale in Celcius.
//		This issue will be solved as soon as Smartthings supports dynamic tiles
//		valueTile("temperature", "device.temperature", width: 2, height: 2) {
//			state("temperature", label: '${currentValue}°', unit: "C", 
//            		backgroundColors: [
//					[value: 0, color: "#153591"],
//					[value: 8, color: "#1e9cbb"],
//					[value: 14, color: "#90d2a7"],
//					[value: 20, color: "#44b621"],
//					[value: 24, color: "#f1d801"],
//					[value: 29, color: "#d04e00"],
//					[value: 36, color: "#bc2323"]
//				])
//		}
			state("temperature", label:'${currentValue}°', unit:"F",
			backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				])
		}
		standardTile("mode", "device.thermostatMode", inactiveLabel: false,
			decoration: "flat") {
			state "heat", label: '${name}', action: "thermostat.off", 
				icon: "st.Weather.weather14", backgroundColor: "#ffffff"
			state "off", label: '${name}', action: "thermostat.cool", 
				icon: "st.Outdoor.outdoor19"
			state "cool", label: '${name}', action: "thermostat.heat", 
				icon: "st.Weather.weather7"
		}
		standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false,
			decoration: "flat") {
			state "auto", label: '${name}', action: "thermostat.fanOn", 
				icon: "st.Appliances.appliances11"
			state "on", label: '${name}', action: "thermostat.fanAuto", 
				icon: "st.Appliances.appliances11"
		}
		standardTile("switchProgram", "device.programDisplayName", 
			inactiveLabel: false, width: 1, height: 1, decoration: "flat") {
			state "Home", label: '${name}', action: "sleep", 
				icon: "st.Home.home4"
			state "Sleep", label: '${name}', action: "awake", 
 				icon: "st.Bedroom.bedroom2"
			state "Awake", label: '${name}', action: "away", 
				icon: "st.Outdoor.outdoor20"
			state "Away", label: '${name}', action: "quickSave", 
				icon: "st.presence.car.car"
			state "QuickSave", label: '${name}', action: "present", 
				icon: "st.Home.home1"
			state "Custom", label: 'Custom', action: "resumeThisTstat", 
				icon: "st.Office.office6"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false,
			decoration: "flat") {
			state "heat", label: '${currentValue}° heat', unit: "C", 
			backgroundColor: "#ffffff"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false,
			decoration: "flat") {
			state "cool", label: '${currentValue}° cool', unit: "C", 
			backgroundColor: "#ffffff"
		}
		valueTile("humidity", "device.humidity", inactiveLabel: false, 
			decoration: "flat") {
			state "default", label: 'Humidity\n${currentValue}%', unit: "humidity"
		}
		standardTile("heatLevelUp", "device.heatingSetpoint", canChangeIcon: false,
			inactiveLabel: false, decoration: "flat") {
			state "heatLevelUp", label: '  ', action: "heatLevelUp", 
			icon: "st.thermostat.thermostat-up"
		}
		standardTile("heatLevelDown", "device.heatingSetpoint", canChangeIcon: false,
			inactiveLabel: false, decoration: "flat") {
			state "heatLevelDown", label: '  ', action: "heatLevelDown", 
 			icon:"st.thermostat.thermostat-down"
		}
		standardTile("coolLevelUp", "device.coolingSetpoint", canChangeIcon: false,
			inactiveLabel: false, decoration: "flat") {
			state "coolLevelUp", label: '  ', action: "coolLevelUp", 
			icon: "st.thermostat.thermostat-up"
		}
		standardTile("coolLevelDown", "device.coolingSetpoint", canChangeIcon: false,
			inactiveLabel: false, decoration: "flat") {
			state "coolLevelDown", label: '  ', action: "coolLevelDown", 
			icon: "st.thermostat.thermostat-down"
		}
		valueTile("equipStatus", "device.equipmentStatus", inactiveLabel: false,
			decoration: "flat", width: 3, height: 1) {
			state "default", label: '${currentValue}'
		}
//		One could also use thermostatOperatingState as display value for equipStatus (in line with default ecobee device's status)
//		However, it does not contain humidifier/dehumidifer/HRV/ERV/aux heat
//		components' running states, just the basic thermostat states (heating, cooling, fan only).
//		To use this tile instead of the above, just comment out the above tile, and remove comments below.
//		valueTile("equipStatus", "device.thermostatOperatingState", inactiveLabel: false,
//			decoration: "flat", width: 3, height: 1) {
//			state "default", label: '${currentValue}'
//		}
		valueTile("programEndTimeMsg", "device.programEndTimeMsg", inactiveLabel:
			false, decoration: "flat", width: 3, height: 1) {
			state "default", label: '${currentValue}'
		}
		valueTile("fanMinOnTime", "device.fanMinOnTime", inactiveLabel: false,
			decoration: "flat", width: 1, height: 1) {
			state "default", label: 'FanMin ${currentValue}'
		}
		valueTile("alerts", "device.alerts", inactiveLabel: false, decoration: "flat",
			width: 2, height: 1) {
			state "default", label: '${currentValue}'
		}
		// Program Tiles
		valueTile("programScheduleName", "device.programScheduleName", inactiveLabel:
			false, width: 1, height: 1, decoration: "flat") {
			state "default", label: 'Mode\n${currentValue}'
		}
		valueTile("programType", "device.programType", inactiveLabel: false, width: 1,
			height: 1, decoration: "flat") {
			state "default", label: 'ProgType\n${currentValue}'
		}
		valueTile("programCoolTemp", "device.programCoolTemp", inactiveLabel: false,
			width: 1, height: 1, decoration: "flat") {
			state "default", label: 'ProgCool ${currentValue}°'
		}
		valueTile("programHeatTemp", "device.programHeatTemp", inactiveLabel: false,
			width: 1, height: 1, decoration: "flat") {
			state "default", label: 'ProgHeat ${currentValue}°'
		}
		standardTile("resProgram", "device.thermostatMode", inactiveLabel: false,
			decoration: "flat") {
			state "default", label: 'ResumeProg', action: "resumeThisTstat", 
            		icon: "st.Office.office7", backgroundColor: "#ffffff"
		}
		// Weather Tiles
		standardTile("weatherIcon", "device.weatherSymbol", inactiveLabel: false, width: 1, height: 1,
			decoration: "flat") {
			state "-2",			label: 'updating...',		icon: "st.unknown.unknown.unknown"
			state "0",			label: 'Sunny',			icon: "st.Weather.weather14"
			state "1",			label: 'FewClouds',		icon: "st.Weather.weather15"
			state "2",			label: 'PartlyCloudy',		icon: "st.Weather.weather15"
			state "3",			label: 'MostlyCloudy',		icon: "st.Weather.weather15"
			state "4",			label: 'Overcast',		icon: "st.Weather.weather13"
			state "5",			label: 'Drizzle',		icon: "st.Weather.weather9"
			state "6",			label: 'Rain',			icon: "st.Weather.weather10"
			state "7",			label: 'FreezingRain',		icon: "st.Weather.weather10"
			state "8",			label: 'Showers',		icon: "st.Weather.weather10"
			state "9",			label: 'Hail',			icon: "st.custom.wuk.sleet"
			state "10",			label: 'Snow',			icon: "st.Weather.weather6"
			state "11",			label: 'Flurries',		icon: "st.Weather.weather6"
			state "12",			label: 'Sleet',			icon: "st.Weather.weather6"
			state "13",			label: 'Blizzard',		icon: "st.Weather.weather7"
			state "14",			label: 'Pellets',		icon: "st.custom.wuk.sleet"
			state "15",			label: 'ThunderStorms',		icon: "st.custom.wuk.tstorms"
			state "16",			label: 'Windy',			icon: "st.Transportation.transportation5"
			state "17",			label: 'Tornado',		icon: "st.Weather.weather1"
			state "18",			label: 'Fog',			icon: "st.Weather.weather13"
			state "19",			label: 'Hazy',			icon: "st.Weather.weather13"
			state "20",			label: 'Smoke',			icon: "st.Weather.weather13"
			state "21",			label: 'Dust',			icon: "st.Weather.weather13"
		}
		valueTile("weatherDateTime", "device.weatherDateTime", inactiveLabel: false,
			width: 2, height: 1, decoration: "flat") {
			state "default", label: '${currentValue}'
		}
		valueTile("weatherConditions", "device.weatherCondition", 
			inactiveLabel: false, width: 2, height: 1, decoration: "flat") {
			state "default", label: 'Forecast ${currentValue}'
		}
		valueTile("weatherTemperature", "device.weatherTemperature", inactiveLabel:
			false, width: 1, height: 1, decoration: "flat") {
			state "default", label: 'Out Temp ${currentValue}°', unit: "C"
		}
		valueTile("weatherRelativeHumidity", "device.weatherRelativeHumidity",
			inactiveLabel: false, width: 1, height: 1.5, decoration: "flat") {
			state "default", label: 'Out Hum ${currentValue}%', unit: "humidity"
		}
		valueTile("weatherTempHigh", "device.weatherTempHigh", inactiveLabel: false,
			width: 1, height: 1, decoration: "flat") {
			state "default", label: 'Fcast High ${currentValue}°', unit: "C"
		}
		valueTile("weatherTempLow", "device.weatherTempLow", inactiveLabel: false,
			width: 1, height: 1, decoration: "flat") {
			state "default", label: 'Fcast Low ${currentValue}°', unit: "C"
		}
		valueTile("weatherPressure", "device.weatherPressure", inactiveLabel: false,
			width: 1, height: 1, decoration: "flat") {
			state "default", label: 'Pressure ${currentValue}', unit: "hpa"
		}
		valueTile("weatherWindDirection", "device.weatherWindDirection",
			inactiveLabel: false, width: 1, height: 1, decoration: "flat") {
			state "default", label: 'W.Dir ${currentValue}'
		}
		valueTile("weatherWindSpeed", "device.weatherWindSpeed", inactiveLabel: false,
			width: 1, height: 1, decoration: "flat") {
			state "default", label: 'W.Speed ${currentValue}'
		}
		valueTile("weatherPop", "device.weatherPop", inactiveLabel: false, width: 1,
			height: 1, decoration: "flat") {
			state "default", label: 'PoP\n${currentValue}%', unit: "%"
		}
		standardTile("refresh", "device.thermostatMode", inactiveLabel: false,
			decoration: "flat") {
			state "default", action: "polling.poll", icon: "st.secondary.refresh"
		}
		main "temperature"
		details(["name", "groups", "mode", "temperature", "fanMode", "switchProgram",
			"heatLevelDown", "heatingSetpoint", "heatLevelUp", "coolLevelDown",
			"coolingSetpoint", "coolLevelUp",
			"equipStatus", "programEndTimeMsg", "humidity", "alerts",
			"fanMinOnTime", "programScheduleName", "programType", "programCoolTemp",
			"programHeatTemp", "resProgram",
			"weatherIcon", "weatherDateTime", "weatherConditions",
			"weatherTemperature", "weatherRelativeHumidity", "weatherTempHigh",
			"weatherTempLow", "weatherPressure", "weatherWindDirection",
			"weatherWindSpeed", "weatherPop", "refresh",
		])
	}
}
void coolLevelUp() {
	int nextLevel = device.currentValue("coolingSetpoint") + 1
	def scale = getTemperatureScale()
	if (scale == 'C') {
		if (nextLevel > 30) {
			nextLevel = 30
		}
	} else {
		if (nextLevel > 99) {
			nextLevel = 99
		}
	}
	setCoolingSetpoint(nextLevel)
}
void coolLevelDown() {
	int nextLevel = device.currentValue("coolingSetpoint") - 1
	def scale = getTemperatureScale()
	if (scale == 'C') {
		if (nextLevel < 10) {
			nextLevel = 10
		}
	} else {
		if (nextLevel < 50) {
			nextLevel = 50
		}
	}
	setCoolingSetpoint(nextLevel)
}
void heatLevelUp() {
	int nextLevel = device.currentValue("heatingSetpoint") + 1
	def scale = getTemperatureScale()
	if (scale == 'C') {
		if (nextLevel > 30) {
			nextLevel = 30
		}
	} else {
		if (nextLevel > 99) {
			nextLevel = 99
		}
	}
	setHeatingSetpoint(nextLevel)
}
void heatLevelDown() {
	int nextLevel = device.currentValue("heatingSetpoint") - 1
	def scale = getTemperatureScale()
	if (scale == 'C') {
		if (nextLevel < 10) {
			nextLevel = 10
		}
	} else {
		if (nextLevel < 50) {
			nextLevel = 50
		}
	}
	setHeatingSetpoint(nextLevel)
}

// handle commands
void setHeatingSetpoint(temp) {
	def thermostatId= determine_tstat_id("") 	    
	setHold(thermostatId, device.currentValue("coolingSetpoint"), temp,
		null, null)
	sendEvent(name: 'heatingSetpoint', value: temp)
}
void setCoolingSetpoint(temp) {
	def thermostatId= determine_tstat_id("") 	    
	setHold(settings.thermostatId, temp, device.currentValue("heatingSetpoint"),
		null, null)
	sendEvent(name: 'coolingSetpoint', value: temp)
}
void off() {
	setThermostatMode('off')
}
void auto() {
	setThermostatMode('auto')
}
void heat() {
	setThermostatMode('heat')
}
void emergencyHeat() {
	setThermostatMode('heat')
}
void auxHeatOnly() {
	setThermostatMode('auxHeatOnly')
}
void cool() {
	setThermostatMode('cool')
}
void setThermostatMode(mode) {
	mode = mode == 'emergency heat' ? 'heat' : mode
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['hvacMode': "${mode}"])
	sendEvent(name: 'thermostatMode', value: mode)
}
void fanOn() {
	setThermostatFanMode('on')
}
void fanAuto() {
	setThermostatFanMode('auto')
}
void fanOff() { // fanOff is not supported, setting it to 'auto' instead.
	setThermostatFanMode('auto')
}
void setThermostatFanMode(mode) {
	def thermostatId= determine_tstat_id("") 	    
	setHold(thermostatId, device.currentValue("coolingSetpoint"), device
		.currentValue("heatingSetpoint"),
		mode, null)
	sendEvent(name: 'thermostatFanMode', value: mode)
}
void setFanMinOnTime(minutes) {
	setThermostatSettings(settings.thermostatId, ['fanMinOnTime': "${minutes}"])
	sendEvent(name: 'fanMinOnTime', value: minutes)
}
void ventilatorOn() {
	setVentilatorMode('on')
}
void ventilatorOff() {
	setVentilatorMode('off')
}
void ventilatorAuto() {
	setVentilatorMode('auto')
}
void setVentilatorMinOnTime(minutes) {
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['vent': "minontime",
			'ventilatorMinOnTime': "${minutes}"])
	sendEvent(name: 'ventilatorMinOnTime', value: minutes)
	sendEvent(name: 'ventilatorMode', value: "minontime")
}
void setVentilatorMode(mode) {
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['vent': "${mode}"])
	sendEvent(name: 'ventilatorMode', value: mode)
}
void setCondensationAvoid(flag) { // set the flag to true or false
	flag = flag == 'true' ? 'true' : 'false'
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['condensationAvoid': "${flag}"])
	sendEvent(name: 'condensationAvoid', value: flag)
}
void dehumidifierOn() {
	setDehumidifierMode('on')
}
void dehumidifierOff() {
	setDehumidifierMode('off')
}
void setDehumidifierMode(mode) {
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['dehumidifierMode': "${mode}"])
	sendEvent(name: 'dehumidifierMode', value: mode)
}
void setDehumidifierLevel(level) {
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['dehumidifierLevel': "${level}"])
	sendEvent(name: 'dehumidifierLevel', value: level)
}
void humidifierAuto() {
	setHumidifierMode('auto')
}
void humidifierOff() {
	setHumidifierMode('off')
}
void setHumidifierMode(mode) {
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['humidifierMode': "${mode}"])
	sendEvent(name: 'humidifierMode', value: mode)
}
void setHumidifierLevel(level) {
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['humidity': "${level}"])
	sendEvent(name: 'humidifierLevel', value: level)
}
void followMeComfort(flag) {
	flag = flag == 'true' ? 'true' : 'false'
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['followMeComfort': "${flag}"])
	sendEvent(name: 'followMeComfort', value: flag)
}
void autoAway(flag) {
	flag = flag == 'true' ? 'true' : 'false'
	def thermostatId= determine_tstat_id("") 	    
	setThermostatSettings(thermostatId, ['autoAway': "${flag}"])
	sendEvent(name: 'autoAway', value: flag)
}

void awake() {
	setThisTstatClimate("Awake")    
}
void away() {
	setThisTstatClimate("Away")
	sendEvent(name: "presence", value: "not present")
}
void present() {
	def currentProgramType = device.currentValue("programType")
	if (currentProgramType == "HOLD") { // get rid of overrides before applying new climate
		resumeThisTstat()
	}
	home()
}
void home() {
	setThisTstatClimate("Home")
	sendEvent(name: "presence", value: "present")

}
void sleep() {
	setThisTstatClimate("Sleep")
}
void quickSave() {
	def thermostatId= determine_tstat_id("") 	    
	def currentProgramType = device.currentValue("programType")
	if (currentProgramType.toUpperCase() == 'VACATION') {
		if (settings.trace) {
			log.debug "quickSave>thermostatId = ${thermostatId},cannot do quickSave due to vacation settings"
			sendEvent name: "verboseTrace", value:
				"quickSave>thermostatId = ${thermostatId},cannot do quickSave switch due to vacation settings"
		}
		return
	}
	float quickSaveSetBack, quickSaveSetForw, quickSaveHeating, quickSaveCooling
	def scale = getTemperatureScale()
	if (scale == 'C') {
		quickSaveSetBack = data.thermostatList[0].settings.quickSaveSetBack / 2 // approximate conversion of differential to celcius
		quickSaveSetForw = data.thermostatList[0].settings.quickSaveSetForward / 2
		quickSaveCooling = fToC(data.thermostatList[0].runtime.desiredCool)
		quickSaveHeating = fToC(data.thermostatList[0].runtime.desiredHeat)
	} else {
		quickSaveSetBack = data.thermostatList[0].settings.quickSaveSetBack
		quickSaveSetForw = data.thermostatList[0].settings.quickSaveSetForward
		quickSaveCooling = data.thermostatList[0].runtime.desiredCool
		quickSaveHeating = data.thermostatList[0].runtime.desiredHeat
	}
	quickSaveCooling = (quickSaveCooling + quickSaveSetForw).round(0)
	quickSaveHeating = (quickSaveHeating - quickSaveSetBack).round(0)
	setHold(thermostatId, quickSaveCooling, quickSaveHeating, null, null)
	sendEvent(name: 'coolingSetpoint', value: quickSaveCooling.toString())
	sendEvent(name: 'heatingSetpoint', value: quickSaveHeating.toString())
	sendEvent(name: 'programScheduleName', value: "QuickSave")
	sendEvent(name: 'programDisplayName', value: "QuickSave")
}
  
void setThisTstatClimate(climateName) {
	def thermostatId= determine_tstat_id("") 	    
	def currentProgram = device.currentValue("programScheduleName")
	def currentProgramType = device.currentValue("programType").trim().toUpperCase()
	if (currentProgramType == 'VACATION') {
		if (settings.trace) {
			log.debug "setThisTstatClimate>thermostatId = ${settings.thermostatId},cannot do the prog switch due to vacation settings"
			sendEvent name: "verboseTrace", value:
					"setThisTstatClimate>thermostatId = ${settings.thermostatId},cannot do the prog switch due to vacation settings"
		}
		return
	}
	// If climate is different from current one, then change it to the given climate
	if (currentProgram.toUpperCase() != climateName.trim().toUpperCase()) {
		setClimate(thermostatId, climateName)
		sendEvent(name: 'programScheduleName', value: climateName)
		poll() // to refresh the values in the UI
	}
}
// parse events into attributes
def parse(String description) {

}

void poll() {
	def tstatId,ecobeeType
    
	def thermostatId= determine_tstat_id("") 	    
	
	getThermostatInfo(thermostatId)

	// determine if there is an event running
    
	Integer indiceEvent = 0
	Boolean foundEvent = false
	if (data.thermostatList[0].events.size > 0) {
		for (i in 0..data.thermostatList[0].events.size() - 1) {
			if (data.thermostatList[0].events[i].running) {
				indiceEvent = i // save the right indice associated to the Event that is currently running
				foundEvent = true
				exit
			}
		}
	}

	def currentClimate = null
	// Get the current Climate 
	data.thermostatList[0].program.climates.each() {
		if (it.climateRef == data.thermostatList[0].program.currentClimateRef) {
			currentClimate = it
			exit
		}
	}

	if (settings.trace) {
		log.debug "poll>thermostatId = ${thermostatId},Current Climate Ref=${data.thermostatList[0].program.currentClimateRef}"
		sendEvent name: "verboseTrace", value:
			"poll>thermostatId = ${thermostatId},Current Climate Ref=${data.thermostatList[0].program.currentClimateRef}"
		if (foundEvent) {
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},indiceEvent=${indiceEvent}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event name=${data.thermostatList[0].events[indiceEvent].name}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event type=${data.thermostatList[0].events[indiceEvent].type}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's coolHoldTemp=${data.thermostatList[0].events[indiceEvent].coolHoldTemp}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's heatHoldTemp=${data.thermostatList[0].events[indiceEvent].heatHoldTemp}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's fan mode=${data.thermostatList[0].events[indiceEvent].fan}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's fanMinOnTime=${data.thermostatList[0].events[indiceEvent].fanMinOnTime}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's vent mode=${data.thermostatList[0].events[indiceEvent].vent}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's ventilatorMinOnTime=${data.thermostatList[0].events[indiceEvent].ventilatorMinOnTime}"
			sendEvent name: "verboseTrace", value:
				"poll>thermostatId = ${thermostatId},event's running=${data.thermostatList[0].events[indiceEvent].running}"
		}
	}
    
	ecobeeType = determine_ecobee_type_or_location(ecobeeType)
	def progDisplayName = getCurrentProgName()
    
	def dataEvents = [
 		thermostatName:data.thermostatList[0].name,
		thermostatMode:data.thermostatList[0].settings.hvacMode,
		temperature: data.thermostatList[0].runtime.actualTemperature,
		humidity:data.thermostatList[0].runtime.actualHumidity,
		coolingSetpoint: (foundEvent)? data.thermostatList[0].events[indiceEvent].coolHoldTemp: 
			data.thermostatList[0].runtime.desiredCool,
		heatingSetpoint: (foundEvent)? data.thermostatList[0].events[indiceEvent].heatHoldTemp: 
			data.thermostatList[0].runtime.desiredHeat,
		modelNumber: data.thermostatList[0].modelNumber,
		equipmentStatus:getEquipmentStatus(),
		thermostatOperatingState: getThermostatOperatingState(),
		hasHumidifier:data.thermostatList[0].settings.hasHumidifier.toString(),
		hasDehumidifier:data.thermostatList[0].settings.hasDehumidifier.toString(),
		hasHrv:data.thermostatList[0].settings.hasHrv.toString(),
		hasErv:data.thermostatList[0].settings.hasErv.toString(),
		programScheduleName: (foundEvent)? data.thermostatList[0].events[indiceEvent].name : currentClimate.name,
		programType: (foundEvent)?data.thermostatList[0].events[indiceEvent].type : currentClimate.type,
		programEndTimeMsg: (foundEvent)? "${data.thermostatList[0].events[indiceEvent].type}" +
				" ends at ${data.thermostatList[0].events[indiceEvent].endDate} " +
				"${data.thermostatList[0].events[indiceEvent].endTime.substring(0,5)}":
 				"No Events running",
		thermostatFanMode: (foundEvent)? data.thermostatList[0].events[indiceEvent].fan: 
			data.thermostatList[0].runtime.desiredFanMode,
		fanMinOnTime: (foundEvent)? data.thermostatList[0].events[indiceEvent].fanMinOnTime.toString() :
			data.thermostatList[0].settings.fanMinOnTime.toString(),
		programFanMode: (data.thermostatList[0].settings.hvacMode == 'cool')? currentClimate.coolFan : currentClimate.heatFan,
		programDisplayName: progDisplayName,
		weatherStation:data.thermostatList[0].weather.weatherStation,
		weatherSymbol:data.thermostatList[0].weather.forecasts[0].weatherSymbol.toString(),
		weatherTemperature:data.thermostatList[0].weather.forecasts[0].temperature,
		weatherDateTime:"Weather as of\n ${data.thermostatList[0].weather.forecasts[0].dateTime.substring(0,16)}",
		weatherCondition:data.thermostatList[0].weather.forecasts[0].condition,
		weatherTemp: data.thermostatList[0].weather.forecasts[0].temperature,
		weatherTempHigh: data.thermostatList[0].weather.forecasts[0].tempHigh, 
		weatherTempLow: data.thermostatList[0].weather.forecasts[0].tempLow,
		weatherWindSpeed: (data.thermostatList[0].weather.forecasts[0].windSpeed/1000),		// divided by 1000 for display
		weatherPressure:data.thermostatList[0].weather.forecasts[0].pressure.toString(),
		weatherRelativeHumidity:data.thermostatList[0].weather.forecasts[0].relativeHumidity,
		weatherWindDirection:data.thermostatList[0].weather.forecasts[0].windDirection + " Winds",
		weatherPop:data.thermostatList[0].weather.forecasts[0].pop.toString(),
		programCoolTemp:(currentClimate.coolTemp / 10),										// divided by 10 for display
		programHeatTemp:(currentClimate.heatTemp / 10),
		alerts: getAlerts(),
		groups: (ecobeeType.toUpperCase() == 'REGISTERED')? getThermostatGroups(thermostatId) : 'No groups',
		climateList: getClimateList(),
        presence: (progDisplayName.toUpperCase()!='AWAY')? 'present':'not present'
	]
     
	if (data.thermostatList[0].events[indiceEvent] != null && 'quickSave' == data.thermostatList[0].events[indiceEvent].type ) {
			dataEvents.programEndTimeMsg ="Quicksave running"
	}

	generateEvent(dataEvents)
	if (data.thermostatList[0].settings.hasHumidifier) {
		sendEvent(name: 'humidifierMode', value: data.thermostatList[0].settings.humidifierMode)
		sendEvent(name: 'humidifierLevel', value: data.thermostatList[0].settings.humidity,
			unit: "%")
	}
	if (data.thermostatList[0].settings.hasDehumidifier) {
		sendEvent(name: 'dehumidifierMode', value: data.thermostatList[0].settings.dehumidifierMode)
		sendEvent(name: 'dehumidifierLevel', value: data.thermostatList[0].settings.dehumidifierLevel,
			unit: "%")
	}
	if ((data.thermostatList[0].settings.hasHrv) || (data.thermostatList[0].settings
		.hasErv)) {
		sendEvent(name: 'ventilatorMinOnTime', value: data.thermostatList[0].settings
			.ventilatorMinOnTime)
		sendEvent(name: 'ventilatorMode', value: data.thermostatList[0].settings.vent)
	}
    
}


private void generateEvent(Map results)
{
	if (settings.trace) {
		log.debug "generateEvents>parsing data $results"
	}
    
	if(results)
	{
		results.each { name, value ->
			def isDisplayed = true
            
		if ((name.toUpperCase().contains("TEMP"))|| (name.toUpperCase().contains("SETPOINT"))) {
				float tempValue = getTemperature(value).toFloat().round(1)
				def isChange = isTemperatureStateChange(device, name, tempValue.toString())
				isDisplayed = isChange
				sendEvent(name: name, value: tempValue.toString(), unit: getTemperatureScale(), displayed: isDisplayed)                                     									 
		} else if (name.toUpperCase().contains("SPEED")) {
 				float speedValue = getSpeed(value).toFloat().round(1)
				def isChange = isStateChange(device, name, speedValue.toString())
				isDisplayed = isChange
				sendEvent(name: name, value: speedValue.toString(), unit: getDistanceScale(), displayed: isDisplayed)                                     									 
		} else if (name.toUpperCase().contains("HUMIDITY")) {
 				float humidityValue = value.toFloat().round(1)
				def isChange = isStateChange(device, name, humidityValue.toString())
				isDisplayed = isChange
				sendEvent(name: name, value: humidityValue.toString(), unit: "%", displayed: isDisplayed)                                     									 
 		} else {
				def isChange = isStateChange(device, name, value)
				isDisplayed = isChange

				sendEvent(name: name, value: value, isStateChange: isChange, displayed: isDisplayed)       
			}
		}
	}
}


private def getCurrentProgName() {
	def AWAY_PROG = 'Away'
	def SLEEP_PROG = 'Sleep'
	def HOME_PROG = 'Home'
	def AWAKE_PROG = 'Awake'
	def CUSTOM_PROG = 'Custom'
	def QUICKSAVE = 'QuickSave'

	def progCurrentName = device.currentValue("programScheduleName")
	def progType = device.currentValue("programType")
	progType = (progType == null) ? "": progType.trim().toUpperCase()	
	progCurrentName = (progCurrentName == null) ? "": progCurrentName.trim().toUpperCase()
	if ((progCurrentName != AWAY_PROG) && (progCurrentName != SLEEP_PROG) && (
			progCurrentName != AWAKE_PROG) &&
		(progCurrentName != HOME_PROG) && (progCurrentName != QUICKSAVE)) {
		progCurrentName = (progType == 'VACATION') ? AWAY_PROG : CUSTOM_PROG
	}
	return progCurrentName
}

private def getAlerts() {

	def alerts = null
	if (data.thermostatList[0].alerts.size() > 0) {
		alerts = 'Alert(s) '
		for (i in 0..data.thermostatList[0].alerts.size() - 1) {
			alerts = (i > 0) ? ' \n' + alerts + data.thermostatList[0].alerts[i].notificationType :
				alerts +
				data.thermostatList[0].alerts[i].notificationType
		}
	}
	alerts = (alerts != null) ? alerts + '\ngo to ecobee portal' : 'No alerts'
	return alerts
}

private def getThermostatGroups(thermostatId) {

	def groupList = 'No groups'
	getGroups(thermostatId)
	if (data.groups.size() > 0) {
		groupList = 'Group(s) '
		def j=0
		for (i in 0..data.groups.size() - 1) {
			if (data.groups[i].groupName != '') {
				groupList = (j > 0) ? ' \n' + groupList + data.groups[i].groupName :
					groupList + data.groups[i].groupName
				j++    
			}        
		}
	}	
	return groupList
}

private def getTemperature(value) {
	def farenheits = value
	if(getTemperatureScale() == "F"){
		return farenheits
	} else {
		return fToC(farenheits)
	}
}

private def getSpeed(value) {
	def miles = value
	if(getTemperatureScale() == "F"){
		return miles
	} else {
		return milesToKm(miles) 
	}
}

private def getDistanceScale() {
	def scale= getTemperatureScale()
	if (scale == 'C') {
		return "km"
	}
	return "miles"
}


// Get the EquipmentStatus including all components (HVAC, fan, dehumidifier/humidifier,HRV/ERV, aux heat)
// To be called after a poll() or refresh() to have the latest status
def getEquipmentStatus() {
	def equipStatus = (data.thermostatList[0].equipmentStatus.size() != 0) ? data
		.thermostatList[0].equipmentStatus + ' running' : 'Idle'
	return equipStatus
}
// Get the basic thermostat status (heating,cooling,fan only)
// To be called after a poll() or refresh() to have the latest status

def getThermostatOperatingState() {

	def equipStatus = device.currentValue("equipmentStatus")
	if (equipStatus == null) 
 		return ""
	
	equipStatus = equipStatus.trim().toUpperCase()
    
	def currentOpState = equipStatus.contains('HEAT')? 'heating' : (equipStatus.contains('COOL')? 'cooling' : 'idle')
	if ((currentOpState == 'idle') && device.currentValue("thermostatFanMode").toUpperCase().contains('ON') ) {
		currentOpState = 'fan only' 
	}  
	return currentOpState
}

// thermostatId may only be a specific thermostatId or "" (for current thermostat)
// To be called after a poll() or refresh() to have the latest status
private def getClimateList(thermostatId) {
	def climateList=""
    
	for (i in 0..data.thermostatList[0].program.climates.size() - 1) {
		climateList = data.thermostatList[0].program.climates[i].name  + "," + climateList
	}
	if (settings.trace) {
		log.debug "getClimateList>climateList=${climateList}"
		sendEvent name: "verboseTrace", value:
			"getClimateList>climateList=${climateList}"
	}
	return climateList
}


void refresh() {
	poll()
}

void resumeThisTstat() {
	def thermostatId= determine_tstat_id("") 	    
	resumeProgram(thermostatId) 
	poll()
}
private void api(method, args, success = {}) {
	String URI_ROOT = "${get_URI_ROOT()}/1"
	if (!isLoggedIn()) {
		login()
	}
	if (isTokenExpired()) {
		if (settings.trace) {
			log.debug "api> need to refresh tokens"
		}
		if (!refresh_tokens()) {
			login()
		}
	}
	def args_encoded = URLEncoder.encode(args.toString(), "UTF-8")
	def methods = [
		'thermostatSummary': 
			[uri:"${URI_ROOT}/thermostatSummary?format=json&body=${args_encoded}", 
      			type:'get'],
		'thermostatInfo': 
			[uri:"${URI_ROOT}/thermostat?format=json&body=${args_encoded}", 
          		type: 'get'],
		'setThermostatSettings':
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'setHold': 
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'resumeProgram': 
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'createVacation': 
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'deleteVacation': 
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'getGroups': 
			[uri: "${URI_ROOT}/group?format=json&body=${args_encoded}",
			type: 'get'],
		'updateGroup': 
			[uri: "${URI_ROOT}/group?format=json", type: 'post'],
		'updateClimate': 
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'controlPlug': 
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post']
		]
	def request = methods.getAt(method)
	if (settings.trace) {
		log.debug "api> about to call doRequest with (unencoded) args = ${args}"
		sendEvent name: "verboseTrace", value:
			"api> about to call doRequest with (unencoded) args = ${args}"
	}
	doRequest(request.uri, args_encoded, request.type, success)
}

// Need to be authenticated in before this is called. So don't call this. Call api.
private void doRequest(uri, args, type, success) {
	def params = [
		uri: uri,
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type': "application/json",
			'charset': "UTF-8",
			'Accept': "application/json"
		],
		body: args
	]
	try {
		if (settings.trace) {
//			sendEvent name: "verboseTrace", value: "doRequest>token= ${data.auth.access_token}"
			sendEvent name: "verboseTrace", value:
				"doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}"
				log.debug "doRequest> ${type}> uri ${params.uri}, args= ${args}"
		}
		if (type == 'post') {
			httpPostJson(params, success)
		} else if (type == 'get') {
			params.body = null // parameters already in the URL request
			httpGet(params, success)
		}
	} catch (java.net.UnknownHostException e) {
		log.error "doRequest> Unknown host - check the URL " + params.uri
		sendEvent name: "verboseTrace", value: "doRequest> Unknown host"
	} catch (java.net.NoRouteToHostException e) {
		log.error "doRequest> No route to host - check the URL " + params.uri
		sendEvent name: "verboseTrace", value: "doRequest> No route to host"
	} catch (java.io.IOException e) {
		log.error "doRequest> general or malformed request error " + params.body
		sendEvent name: "verboseTrace", value:
			"doRequest> general or malformed request body error " + params.body
	}
}

// tstatType =managementSet or registered (no spaces).  
//		registered is for SMART & SMART-SI thermostats, 
//		managementSet is for EMS thermostat
//		may also be set to a specific locationSet (ex. /Toronto/Campus/BuildingA)
//		may be set to null if not relevant for the given method
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
private def build_body_request(method, tstatType="registered", thermostatId, tstatParams = [],
	tstatSettings = []) {
	def selectionJson = null
	def selection = null
    
	if (method == 'thermostatSummary') {
		if (tstatType.trim().toUpperCase() == 'REGISTERED') {
			selection = [selection: [selectionType: 'registered', selectionMatch: '',
							includeEquipmentStatus: 'true']
						]
		} else {
			// If tstatType is different than managementSet, it is assumed to be locationSet specific (ex./Toronto/Campus/BuildingA)
			selection = (tstatType.trim().toUpperCase() == 'MANAGEMENTSET') ? 
            	// get all EMS thermostats from the root
				[selection: [selectionType: 'managementSet', selectionMatch: '/',
					includeEquipmentStatus: 'true']
				] : // Or Specific to a location
				[selection: [selectionType: 'managementSet', selectionMatch: tstatType.trim(),
					includeEquipmentStatus: 'true']
				]
		}
		selectionJson = new JsonBuilder(selection)
		return selectionJson
	} else if (method == 'thermostatInfo') {
		selection = [selection: [selectionType: 'thermostats',
			selectionMatch: thermostatId,
			includeSettings: 'true',
			includeRuntime: 'true',
			includeProgram: 'true',
			includeWeather: 'true',
			includeAlerts: 'true',
			includeEvents: 'true',
			includeEquipmentStatus: 'true'
			]
		]
		selectionJson = new JsonBuilder(selection)
		return selectionJson
	} else {
		selection = [selectionType: 'thermostats', selectionMatch: thermostatId]
	}
	selectionJson = new JsonBuilder(selection)
	if (settings.trace) {
		log.debug "build_body_request> about to build request for method = ${method} & thermostatId= ${thermostatId} with selection = ${selectionJson}"
	}
	if ((method != 'setThermostatSettings') && (tstatSettings != null) && (tstatSettings != [])) {
		def function_clause = ((tstatParams != null) && (tsatParams != [])) ? 
			[type:method, params: tstatParams
			] : 
			[type: method]
		def bodyWithSettings = [functions: [function_clause], selection: selection,
				thermostat: [settings: tstatSettings]
			]
		def bodyWithSettingsJson = new JsonBuilder(bodyWithSettings)
		return bodyWithSettingsJson
	} else if (method == 'setThermostatSettings') {
		def bodyWithSettings = [selection: selection,thermostat: [settings: tstatSettings]
			]
		def bodyWithSettingsJson = new JsonBuilder(bodyWithSettings)
		return bodyWithSettingsJson
	} else if ((tstatParams != null) && (tsatParams != [])) {
		def function_clause = [type: method, params: tstatParams]
		def simpleBody = [functions: [function_clause], selection: selection]
		def simpleBodyJson = new JsonBuilder(simpleBody)
		return simpleBodyJson
	} else {
		def function_clause = [type: method]
		def simpleBody = [functions: [function_clause], selection: selection]
		def simpleBodyJson = new JsonBuilder(simpleBody)
		return simpleBodyJson
    }    
}

// iterateSetThermostatSettings: iterate thru all the thermostats under a specific account and set the desired settings
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// settings can be anything supported by ecobee 
//		at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
void iterateSetThermostatSettings(tstatType, tstatSettings = []) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
			"iterateSetThermostatSettings>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateSetThermostatSettings>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateSetThermostatSettings>about to call setThermostatSettings for ${tstatlist}"
					log.debug "iterateSetThermostatSettings> about to call setThermostatSettings for ${tstatlist}"
				}
				setThermostatSettings(tstatlist,tstatSettings)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
void setThermostatSettings(thermostatId,tstatSettings = []) {
    
	thermostatId= determine_tstat_id(thermostatId) 	    
	if (settings.trace) {
		log.debug
			"setThermostatSettings>called with values ${tstatSettings} for ${thermostatId}"
		sendEvent name: "verboseTrace", value:
			"setThermostatSettings>called with values ${tstatSettings} for ${thermostatId}"
	}
	def bodyReq = build_body_request('setThermostatSettings',null,thermostatId,null,tstatSettings)
	api('setThermostatSettings', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:
					"setThermostatSettings>done for ${thermostatId}"
			}
		} else {
			log.error "setThermostatSettings> error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"setThermostatSettings>error ${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// iterateSetHold: iterate thru all the thermostats under a specific account and create the hold event
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// settings can be anything supported by ecobee 
//		at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// extraHoldParams may be any other sethold or events properties  
//		see https://www.ecobee.com/home/developer/api/documentation/v1/objects/Event.shtml for more details
void iterateSetHold(tstatType, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = [], extraHoldParams=[]) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
			"iterateSetHold>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateSetHold>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateSetHold>about to call setHold for ${tstatlist}"
					log.debug "iterateSethold> about to call setHold for ${tstatlist}"
				}
				setHoldExtraParams(tstatlist, coolingSetPoint, heatingSetPoint, fanMode,
					tstatSettings, extraHoldParams)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// This setHold function is kept for better compatibility with previous releases
void setHold(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = []) {
    
    // Call the setHoldExtraParams function with null value as extraHoldParams 
	setHoldExtraParams(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
		tstatSettings, null) 
}

// New version of setHold with access to extra setHold params when needed (ex. to set ventilator event's properties).
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// extraHoldParams may be any other sethold or events properties  
//		see https://www.ecobee.com/home/developer/api/documentation/v1/objects/Event.shtml
void setHoldExtraParams(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = [], extraHoldParams=[]) {
    
	Integer targetCoolTemp = null,targetHeatTemp = null
	def tstatParams = null
	thermostatId = determine_tstat_id(thermostatId)
	if (settings.trace) {
		log.debug
			"sethold>called with values ${coolingSetPoint}, ${heatingSetPoint}, ${fanMode}, ${tstatSettings} for ${thermostatId}"
		sendEvent name: "verboseTrace", value:
			"sethold>called with values ${coolingSetPoint}, ${heatingSetPoint}, ${fanMode}, ${tstatSettings} for ${thermostatId}"
	}
	def scale = getTemperatureScale()
	if (scale == 'C') {
		targetCoolTemp = (cToF(coolingSetPoint) * 10) // need to send temperature in F multiply by 10
		targetHeatTemp = (cToF(heatingSetPoint) * 10)
	} else {
		targetCoolTemp = coolingSetPoint * 10 // need to send temperature in F multiply by 10
		targetHeatTemp = heatingSetPoint * 10
	}
	/* if settings.holdType has a value, include it in the list of params
	 */
	if ((settings.holdType != null) && (settings.holdType.trim() != "")) {
		tstatParams = ((fanMode != null) & (fanMode != "")) ? 
          		[coolHoldTemp:targetCoolTemp, heatHoldTemp: targetHeatTemp, fan: fanMode, 
             			holdType:"${settings.holdType.trim()}"
             		] : 
        		[coolHoldTemp: targetCoolTemp, heatHoldTemp: targetHeatTemp, 
             			holdType:"${settings.holdType.trim()}"
			]
	} else {
		tstatParams = ((fanMode != null) & (fanMode != "")) ? 
			[coolHoldTemp:targetCoolTemp, heatHoldTemp: targetHeatTemp, fan: fanMode] : 
        	[coolHoldTemp: targetCoolTemp, heatHoldTemp: targetHeatTemp]

	}
	// Add the extraHoldParams if any
    	if ((extraHoldParams != null) && (extraHoldParams != [])) {
		tstatParams = tstatParams + extraHoldParams
    		
	}
	def bodyReq = build_body_request('setHold',null,thermostatId,tstatParams,tstatSettings)
	api('setHold', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:
					"setHold>done for ${thermostatId}"
			}
		} else {
			log.error "setHold> error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"setHold>error ${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// iterateCreateVacation: iterate thru all the thermostats under a specific account and create the vacation
// tstatType =managementSet or registered (no spaces).  
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void iterateCreateVacation(tstatType, vacationName, targetCoolTemp,
	targetHeatTemp, targetStartDateTime, targetEndDateTime) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
			"iterateCreateVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateCreateVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateCreateVacation>about to call createVacation for ${tstatlist}"
					log.debug "iterateCreateVacation>about to call createVacation for ${tstatlist}"
				}
				createVacation(tstatlist, vacationName, targetCoolTemp, targetHeatTemp,
					targetStartDateTime, targetEndDateTime)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
void createVacation(thermostatId, vacationName, targetCoolTemp, targetHeatTemp,
	targetStartDateTime, targetEndDateTime) {
    
	thermostatId = determine_tstat_id(thermostatId)
	def vacationStartDate = String.format('%tY-%<tm-%<td', targetStartDateTime)
	def vacationStartTime = String.format('%tH:%<tM:%<tS', targetStartDateTime)
	def vacationEndDate = String.format('%tY-%<tm-%<td', targetEndDateTime)
	def vacationEndTime = String.format('%tH:%<tM:%<tS', targetEndDateTime)
	Integer targetCool = null,targetHeat = null
	def scale = getTemperatureScale()
	if (scale == 'C') {
		targetCool = (cToF(targetCoolTemp) * 10) as Integer // need to send temperature in F multiply by 10
		targetHeat = (cToF(targetHeatTemp) * 10) as Integer
	} else {
		targetCool = targetCoolTemp * 10 as Integer // need to send temperature in F multiply by 10
		targetHeat = targetHeatTemp * 10 as Integer
	}
	def vacationParams = [
		name: vacationName.trim(),
		coolHoldTemp: targetCool,
		heatHoldTemp: targetHeat,
		startDate: vacationStartDate,
		startTime: vacationStartTime,
		endDate: vacationEndDate,
		endTime: vacationEndTime
		]
	def bodyReq = build_body_request('createVacation',null,thermostatId,vacationParams)
	if (settings.trace) {
		log.debug "createVacation> about to call api with body = ${bodyReq} for ${thermostatId} "
	}
	api('createVacation', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			log.debug "${vacationName} created for ${thermostatId}"
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:
					"createVacation>done for ${thermostatId}"
			}
		} else {
			log.error "createVacation>error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"createVacation>error ${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// iterateDeleteVacation: iterate thru all the thermostats under a specific account and delete the vacation
// tstatType =managementSet or registered (no spaces).  
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void iterateDeleteVacation(tstatType, vacationName) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0
    
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
        	"iterateDeleteVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateDeleteVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateDeleteVacation> about to call deleteVacation for ${tstatlist}"
					log.debug "iterateDeleteVacation> about to call deleteVacation for ${tstatlist}"
				}
				deleteVacation(tstatlist, vacationName)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
void deleteVacation(thermostatId, vacationName) {
  
	thermostatId = determine_tstat_id(thermostatId)
	def vacationParams = [name: vacationName.trim()]
	def bodyReq = build_body_request('deleteVacation',null,thermostatId,vacationParams)
	api('deleteVacation', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				log.debug "deleteVacation>${vacationName} deleted done for ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"deleteVacation>done for ${thermostatId}"
			}
		} else {
			log.error "deleteVacation> error= ${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"deleteVacation>error ${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// iterateResumeProgram: iterate thru all the thermostats under a specific account and resume their program
void iterateResumeProgram(tstatType) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		sendEvent name: "verboseTrace", value:
			"iterateResumeProgram>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { // process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateResumeProgram> about to call resumeProgram for ${tstatlist}"
					log.debug "iterateResumeProgram> about to call resumeProgram for ${tstatlist}"
				}
				resumeProgram(tstatlist)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
void resumeProgram(thermostatId=settings.thermostatId) {
  
	thermostatId = determine_tstat_id(thermostatId)
	def bodyReq = build_body_request('resumeProgram',null,thermostatId,null)
	if (settings.trace) {
		log.debug "resumeProgram> about to call api with body = ${bodyReq} for ${thermostatId}"
	}
	// do the resumeProgram 3 times to make sure it is resumed.
	api('resumeProgram', bodyReq) {
		if (settings.trace) {
			log.debug "resumeProgram> 1st done for ${thermostatId}"
			sendEvent name: "verboseTrace", value:
				"resumeProgram> 1st done for ${thermostatId}"
		}
	}
	api('resumeProgram', bodyReq) {
		if (settings.trace) {
			log.debug "resumeProgram> 2nd done for ${thermostatId}"
			sendEvent name: "verboseTrace", value:
				"resumeProgram> 2nd done for ${thermostatId}"
		}
	
	}
	api('resumeProgram', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				log.debug "resumeProgram> final resume done for ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"resumeProgram> final resume done for ${thermostatId}"
			}
		} else {
			log.error "resumeProgram>error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"resumeProgram>error=${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// Get all groups related to a thermostatId or all groups
// thermostatId may only be 1 thermostat (not a list) or null (for all groups)
def getGroups(thermostatId) {

	thermostatId = determine_tstat_id(thermostatId)
	def ecobeeType = determine_ecobee_type_or_location("")
	if (settings.trace) {
		log.debug "getGroups>ecobee Type = $ecobeeType for thermostatId = $thermostatId"
	}
	if (ecobeeType.toUpperCase() != 'REGISTERED') {
		if (settings.trace) {
			log.debug "getGroups>managementSet is not a valid settings.ecobeeType for getGroups"
			sendEvent name: "verboseTrace", value:
				"getGroups>managementSet is not a valid settings.ecobeeType for getGroups"
		}
		data.groups = null
		return
	}
	def bodyReq = '{"selection":{"selectionType":"registered"}}'
	api('getGroups', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			data.groups = resp.data.groups
			if (settings.trace) {
				log.debug "getGroups>done for ${thermostatId}," +
					"group size = ${data.groups.size()}, groups data = ${data.groups}"
				sendEvent name: "verboseTrace", value:
					"getGroups>done for ${thermostatId}," +
					"group size = ${data.groups.size()}, groups data = ${data.groups}"
			}
			if (data.groups.size() == 0) {
				return
			}
			if ((thermostatId != null) && (thermostatId != "")) {
				if (data.groups.thermostats.size() > 0) {
					for (i in 0..data.groups.size() - 1) {
						def foundTstat = false
						for (j in 0..data.groups[i].thermostats.size() - 1) {
							if (data.groups[i].thermostats[j] == thermostatId) {
								if (settings.trace) {
									log.debug "getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}"
									sendEvent name: "verboseTrace", value:
										"getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}"
								}
								foundTstat=true
							}
						}   
						if (!foundTstat) {
							data.groups[i].groupName = '' // Not the right group for this thermostat, set it to blanks
						}    
					}
				}
			}
		} else {
			log.error "getGroups>> error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"getGroups>error ${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// iterateUpdateGroup: iterate thru all the Groups under a specific account and update their settings
// Get all groups related to a thermostatId and update them with the groupSettings
// thermostatId may only be 1 thermostat (not a list), if null or empty, then defaulted to this thermostatId (settings)
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// groupSettings may be a map of settings separated by ",", no spaces; 
// 	For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void iterateUpdateGroup(thermostatId, groupSettings = []) {

	thermostatId = determine_tstat_id(thermostatId)
	getGroups(thermostatId)
	if (settings.trace) {
		log.debug "iterateUpdateGroup>about to loop ${data.groups.size()}"
		sendEvent name: "verboseTrace", value:
			"iterateUpdateGroup> about to loop ${data.groups.size()}"
	}
	if (data.groups.size() == 0) {
		return
	}
	for (i in 0..data.groups.size() - 1) {
		def groupName = data.groups[i].groupName
		def groupRef = data.groups[i].groupRef
		def synchronizeSchedule = data.groups[i].synchronizeSchedule
		def synchronizeVacation = data.groups[i].synchronizeVacation
		def synchronizeSystemMode = data.groups[i].synchronizeSystemMode
		if (settings.trace) {
			sendEvent name: "verboseTrace", value:
				"iterateUpdateGroup> about to call updateGroup for ${groupName}"
			log.debug "iterateUpdateGroup> about to call updateGroup for ${groupName}, groupRef= ${groupRef}," +
				"synchronizeSystemMode=${synchronizeSystemMode}, synchronizeVacation=${synchronizeVacation}" +
				"synchronizeSchedule=${synchronizeSchedule}"
		}
		updateGroup(groupRef, groupName, thermostatId, groupSettings)
		if (settings.trace) {
			log.debug "iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}"
			sendEvent name: "verboseTrace", value:
				"iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}"
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// If groupRef is not provided, it is assumed that a group creation needs to be done
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// groupSettings could be a map of settings separated by ",", no spaces; 
//	For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void updateGroup(groupRef, groupName, thermostatId, groupSettings = []) {
	String updateGroupParams
	def groupSettingsJson = new JsonBuilder(groupSettings)
	def groupSet = groupSettingsJson.toString().minus('{').minus('}')

	thermostatId = determine_tstat_id(thermostatId)
	if (settings.trace) {
		log.debug "updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}..."
		sendEvent name: "verboseTrace", value:
			"updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}"
	}
	if ((groupRef != null) && (groupRef.trim() != "")) {

		updateGroupParams = '"groupRef":"' + groupRef.trim() + '","groupName":"' +
			groupName.trim() +
			'",' + groupSet + ',"thermostats":["' + thermostatId + '"]'
	} else {
		updateGroupParams = '"groupName":"' + groupName.trim() + '",' + groupSet +
			',"thermostats":["' + thermostatId + '"]'
	}
	def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' +
		updateGroupParams + '}]}'
	if (settings.trace) {
		log.debug "updateGroup> about to call api with body = ${bodyReq} for groupName =${groupName},thermostatId = ${thermostatId}..."
	}
	api('updateGroup', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				log.debug "updateGroup>done for groupName =${groupName}, ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"updateGroup>done for groupName =${groupName}, ${thermostatId}"
			}
		} else {
			log.error "updateGroup> error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"updateGroup>error ${statusCode.toString()} for ${thermostatId}"
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void deleteGroup(groupRef, groupName) {
	String updateGroupParams

	if ((groupRef != null) && (groupRef.trim() != "")) {
		updateGroupParams = '"groupRef":"' + groupRef.trim() + '","groupName":"' +
			groupName.trim() + '"'
	} else {
		updateGroupParams = '"groupName":"' + groupName.trim() + '"'
	}
	if (settings.trace) {
		log.debug "deleteGroup> updateGroupParams=${updateGroupParams}"
		sendEvent name: "verboseTrace", value:
			"deleteGroup> updateGroupParams=${updateGroupParams}"
	}
	def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' +
		updateGroupParams + '}]}'
	if (settings.trace) {
		log.debug "deleteGroup> about to call api with body = ${bodyReq} for groupName =${groupName}, groupRef = ${groupRef}"
	}
	api('updateGroup', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				log.debug "deleteGroup>done for groupName =${groupName}, groupRef = ${groupRef}"
				sendEvent name: "verboseTrace", value:
					"deleteGroup>done for groupName =${groupName},groupRef = ${groupRef}"
			}
		} else {
			log.error "deleteGroup> error=  ${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"deteteGroup>error ${statusCode.toString()} for ${groupName},groupRef = ${groupRef}"
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// groupSettings could be a map of settings separated by ",", no spaces; 
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void createGroup(groupName, thermostatId, groupSettings = []) {
	updateGroup(null, groupName, thermostatId, groupSettings)
}

// thermostatId may only be 1 thermostat (not a list) 
// climate name is the name of the climate to be created (ex. "Bedtime").
// isOptimized is 'true' or 'false'
// isOccupied is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void createClimate(thermostatId, climateName, coolTemp, heatTemp, isOptimized, isOccupied,
	coolFan, heatFan) {
    
	updateClimate(thermostatId, climateName, 'false', null, coolTemp, heatTemp,
		isOptimized, isOccupied, coolFan, heatFan)
}

// thermostatId can be only 1 thermostat (not a list) 
// climate name is the name of the climate to be deleted (ex. "Bedtime").
void deleteClimate(thermostatId, climateName, substituteClimateName) {
	updateClimate(thermostatId, climateName, 'true', substituteClimateName, null,
		null, null, null, null, null)
}
// iterateSetClimate: iterate thru all the thermostats under a specific account and set their Climate
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// climate name is the name of the climate bet set to(ex. "Home", "Away").
void iterateSetClimate(tstatType, climateName) {
	Integer MAX_TSTAT_BATCH = 25
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type(tsatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug "iterateSetClimate> ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateSetClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				setClimate(tstatlist, climateName)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
//	The settings.thermostatId (input) is the default one
// climate name is the name of the climate to be set to (ex. "Home", "Away").
void setClimate(thermostatId, climateName) {
	def climateRef = null
	def tstatParams

	thermostatId = determine_tstat_id(thermostatId)
	getThermostatInfo(thermostatId)
	for (i in 0..data.thermostatList.size() - 1) {
		def foundClimate = data.thermostatList[i].program.climates.find{ it.name.toUpperCase() == climateName.toUpperCase() }
		if (foundClimate) {
        		climateRef = foundClimate.climateRef
			if (settings.trace) {
				log.debug "setClimate>climateRef ${climateRef} found for thermostatId =${data.thermostatList[i].identifier}"
				sendEvent name: "verboseTrace", value:
					"setClimate>Climate ${climateRef} found for thermostatId =${data.thermostatList[i].identifier}"
			}        
		} else {
			if (settings.trace) {
				log.debug "setClimate>Climate ${climateName} not found for thermostatId =${data.thermostatList[i].identifier}"
				sendEvent name: "verboseTrace", value:
					"setClimate>Climate ${climateName} not found for thermostatId =${data.thermostatList[i].identifier}"
			}        
			continue
		}
        tstatParams =((settings.holdType != null) && (settings.holdType.trim() != "")) ?
			[holdClimateRef:"${climateRef}", holdType:"${settings.holdType.trim()}"
			] :
			[holdClimateRef:"${climateRef}"
			]
		def bodyReq = build_body_request('setHold',null, data.thermostatList[i].identifier,tstatParams)
		api('setHold', bodyReq) {resp ->
			def statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				if (settings.trace) {
					log.debug "setClimate>done for thermostatId =${thermostatId}, climateName =${climateName}"
					sendEvent name: "verboseTrace", value:
						"setClimate>done for thermostatId =${data.thermostatList[i].identifier},climateName =${climateName}"
				}
			} else {
				log.error "setClimate>error=${statusCode.toString()}, message = ${message}"
				sendEvent name: "verboseTrace", value:
					"setClimate>error ${statusCode.toString()} for ${climateName}"
			}
		}

	} /* end for */
}

// iterateUpdateClimate: iterate thru all the thermostats under a specific account and update their Climate
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// climate name is the name of the climate to be updated (ex. "Home", "Away").
// deleteClimateFlag is set to 'true' if the climate needs to be deleted (should not be part of any schedule beforehand)
// subClimateName is the climateName that will replace the original climateName in the schedule (can be null when not needed)
// isOptimized is 'true' or 'false'
// isOccupied is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void iterateUpdateClimate(tstatType, climateName, deleteClimateFlag,
	subClimateName, coolTemp, heatTemp, isOptimized, isOccupied, coolFan, heatFan) {
    
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug "iterateUpdateClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateUpdateClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:
					"iterateUpdateClimate> about to call updateClimate for thermostatId =${id}"
				log.debug "iterateUpdateClimate> about to call updateClimate for thermostatId =${id}"
			}
			updateClimate(Id, climateName, deleteClimateFlag, subClimateName, coolTemp,
				heatTemp, isOptimized, isOccupied, coolFan, heatFan)
		}
	}
}

// thermostatId may only be 1 thermostat (not a list) 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// climate name is the name of the climate to be updated (ex. "Home", "Away").
// deleteClimateFlag is set to 'true' if the climate needs to be deleted (should not be part of any schedule beforehand)
// substituteClimateName is the climateName that will replace the original climateName in the schedule (can be null when not needed)
// isOptimized is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void updateClimate(thermostatId, climateName, deleteClimateFlag,
		substituteClimateName, coolTemp, heatTemp, isOptimized, isOccupied, coolFan, heatFan) {
  
	Integer targetCoolTemp,targetHeatTemp
	def foundClimate = null, foundSubstituteClimate =null
	String scheduleAsString
	def substituteClimateRef = null
	def climateRefToBeReplaced = null

	thermostatId = determine_tstat_id(thermostatId)
	if ((coolTemp != null) && (heatTemp != null)) {
		if (settings.trace) {
			log.debug "updateClimate>thermostatId =${thermostatId} coolTemp=${coolTemp}, heatTemp= ${heatTemp}"
			sendEvent name: "verboseTrace", value:
				"updateClimate>thermostatId =${thermostatId} coolTemp=${coolTemp}, heatTemp= ${heatTemp}"
		}
		def scale = getTemperatureScale()
		if (scale == 'C') {
			targetCoolTemp = (cToF(coolTemp) * 10) as Integer // need to send temperature in F multiply by 10
			targetHeatTemp = (cToF(heatTemp) * 10) as Integer
		} else {
			targetCoolTemp = (coolTemp * 10) as Integer // need to send temperature in F multiply by 10
			targetHeatTemp = (heatTemp * 10) as Integer
		}
	}
	getThermostatInfo(thermostatId)
	if ((substituteClimateName != null) && (substituteClimateName != "")) { // find the subsituteClimateRef for the subsitute Climate Name if not null

		foundClimate = data.thermostatList[0].program.climates.find{ it.name.toUpperCase() == climateName.toUpperCase() }
		foundSubstituteClimate = data.thermostatList[0].program.climates.find { it.name.toUpperCase() == substituteClimateName.toUpperCase() }
		if (foundClimate) {
			climateRefToBeReplaced = foundClimate.climateRef       
			if (settings.trace) {
				log.debug "updateClimate>climateRef ${climateRefToBeReplaced} found for climateName ${climateName}"
				sendEvent name: "verboseTrace", value:
					"updateClimate>climateRef ${climateRefToBeReplaced} found for climateName ${climateName}"
			}                    
		} else {
			if (settings.trace) {
				log.debug "updateClimate>climateName ${substituteClimateName} for substitution not found"
				sendEvent name: "verboseTrace", value:
					"updateClimate>substituteClimateName ${substituteClimateName} for substitution not found"
			}
			return
		}
		if (foundSubstituteClimate) {
 			substituteClimateRef = foundSubstituteClimate.climateRef       
			if (settings.trace) {
				log.debug "updateClimate>substituteClimateRef ${substituteClimateRef} found for ${substituteClimateName}"
				sendEvent name: "verboseTrace", value:
					"updateClimate>substituteClimateRef ${substituteClimateRef} found for ${substituteClimateName} "
			}
		} else {
			if (settings.trace) {
				log.debug "updateClimate>substituteClimateName ${substituteClimateName} for substitution not found"
				sendEvent name: "verboseTrace", value:
					"updateClimate>substituteClimateName ${substituteClimateName}  for substitution not found"
			}
			return
		}
	}
	def bodyReq =
		'{"selection":{"selectionType":"thermostats","selectionMatch":"' +
		thermostatId + '"}'
	bodyReq = bodyReq + ',"thermostat":{"program":{"schedule":['
	for (i in 0..data.thermostatList[0].program.schedule.size() - 1) {
		bodyReq = bodyReq + '['
		// loop thru all the schedule items to create the json structure
		// replace the climateRef with right substituteClimateRef if needed
		for (j in 0..data.thermostatList[0].program.schedule[i].size() - 1) {
			if (substituteClimateRef != null) {
				scheduleAsString = (data.thermostatList[0].program.schedule[i][j] ==
					climateRefToBeReplaced) ?
					'"' + substituteClimateRef + '"' :
					'"' + data.thermostatList[0].program.schedule[i][j].toString() + '"'
			} else {
				scheduleAsString = '"' + data.thermostatList[0].program.schedule[i][j].toString() +
						'"'
			}
			bodyReq = (j == 0) ? bodyReq + scheduleAsString : bodyReq + ',' +
				scheduleAsString
		}
		bodyReq = (i == (data.thermostatList[0].program.schedule.size() - 1)) ?
			bodyReq + ']' : bodyReq + '],'
	}
	bodyReq = bodyReq + '],"climates":['
	foundClimate = false
	for (i in 0..data.thermostatList[0].program.climates.size() - 1) {
		if ((i != 0) && (i < data.thermostatList[0].program.climates.size())) {
			bodyReq = bodyReq + ','
		}
		if ((deleteClimateFlag == 'true') && (climateName.trim().toUpperCase() ==
			data.thermostatList[0].program.climates[i].name.toUpperCase())) {
			foundClimate = true
			if (settings.trace) {
				log.debug "updateClimate>thermostatId =${thermostatId}, deleteClimateFlag=${deleteClimateFlag},Climate ${climateName} to be deleted..."
				sendEvent name: "verboseTrace", value:
					"updateClimate>thermostatId =${thermostatId}, deleteClimateFlag=${deleteClimateFlag},Climate ${climateName} to be deleted..."
			}
			bodyReq = bodyReq.substring(0, (bodyReq.length() - 1)) // trim the last ','
		} else if ((deleteClimateFlag == 'false') && (climateName.trim().toUpperCase() ==
			data.thermostatList[0].program.climates[i].name.toUpperCase())) {
			// update the Climate Object
			foundClimate = true
			bodyReq = bodyReq + '{"name":"' + data.thermostatList[0].program.climates[i]
				.name + '","climateRef":"' +
				data.thermostatList[0].program.climates[i].climateRef + '","coolTemp":"' +
				targetCoolTemp + '","heatTemp":"' + targetHeatTemp + '","isOptimized":"' + isOptimized +
					'","isOccupied":"' + isOccupied +                
					'","coolFan":"' + coolFan + '","heatFan":"' + heatFan + '"}'
		} else {
			bodyReq = bodyReq + '{"name":"' + data.thermostatList[0].program.climates[i]
				.name + '","climateRef":"' +
				data.thermostatList[0].program.climates[i].climateRef + '"}'
		}
	}
	if (!foundClimate) { // this is a new Climate object to create
		if (settings.trace) {
			log.debug "updateClimate>thermostatId =${thermostatId},Climate ${climateName} to be created"
			sendEvent name: "verboseTrace", value:
					"updateClimate>thermostatId =${thermostatId},Climate ${climateName} to be created"
		}

		bodyReq = bodyReq + ',{"name":"' + climateName.capitalize().trim() +
			'","coolTemp":"' + targetCoolTemp +
			'","heatTemp":"' + targetHeatTemp + '","isOptimized":"' + isOptimized +
			'","isOccupied":"' + isOccupied  + '","coolFan":"' + coolFan + '","heatFan":"' + heatFan + '"' +
			',"ventilatorMinOnTime":"5"}' // workaround due to ecobee create Climate bug, to be removed
	}
	bodyReq = bodyReq + ']}}}'
	api('updateClimate', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				log.debug "updateClimate>done for thermostatId =${thermostatId}, climateName =${climateName}"
				sendEvent name: "verboseTrace", value:
					"updateClimate>done for thermostatId =${thermostatId},climateName =${climateName}"
			}
		} else {
			log.error "updateClimate>error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
					"updateClimate>error ${statusCode.toString()} for ${climateName}"
		}

	}
}

// thermostatId may only be 1 thermostat (not a list) 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
// plugName is the name of the plug name to be controlled 
// plugState is the state to be set
// plugSettings are the different settings at https://www.ecobee.com/home/developer/api/documentation/v1/functions/ControlPlug.shtml
void controlPlug(thermostatId, plugName, plugState, plugSettings = []) {
	def plugSettingsJson = new JsonBuilder(plugSettings)
	def plugSet = plugSettingsJson.toString().minus('{').minus('}')

	thermostatId = determine_tstat_id(thermostatId)
	if (settings.trace) {
		log.debug "controlPlug> about to assemble bodyReq for plugName =${plugName}, thermostatId = ${thermostatId}, groupSettings=${plugSet}..."
		sendEvent name: "verboseTrace", value:
			"updateGroup> about to assemble bodyReq for plugName =${plugName}, thermostatId = ${thermostatId}, plugSettings=${plugSet}"
	}
	def bodyReq =
		'{"selection":{"selectionType":"thermostats","selectionMatch":"' +
		thermostatId + '"}'
	bodyReq = bodyReq +
		',"functions":[{"type":"controlPlug","params":{"plugName":"' + plugName +
		'","plugState":"' + plugState + '"'

	// add the holdType (workaround till ecobee fixes the issue)
    
	if ((settings.holdType != null) && (settings.holdType.trim() != "")) {
		bodyReq = bodyReq + '"holdType":"' + settings.holdType.trim() + '"'
	} else {
    
		bodyReq = bodyReq + '"holdType":"indefinite"'
	}
	// add the plugSettings if any
	if ((plugSettings != null) && (plugSettings != [])) {
		bodyReq = bodyReq + ',' + plugSet
	}

	bodyReq = bodyReq + '}}]}'
	api('controlPlug', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			if (settings.trace) {
				log.debug "controlPlug>done for thermostatId =${thermostatId}, plugName =${plugName}"
				sendEvent name: "verboseTrace", value:
					"controlPlug>done for thermostatId =${thermostatId},plugName =${plugName}"
			}
			// post plug values 
			sendEvent name: "plugName", value: "${plugName}"
			sendEvent name: "plugState", value: "${plugState}"
			if ((plugSettings != null) && (plugSettings != '')) {
				sendEvent name: "plugSettings", value: "${plugSettings}"
			}
		} else {
			log.error "controlPlug>error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"controlPlug>error ${statusCode.toString()} for ${plugName}"
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
//	if no thermostatId is provided, it is defaulted to the thermostatId specified in the settings (input)
void getThermostatInfo(thermostatId=settings.thermostatId) {

	if (settings.trace) {
		log.debug "getThermostatInfo> about to call build_body_request for thermostatId = ${thermostatId}..."
	}
	def bodyReq = build_body_request('thermostatInfo',null,thermostatId,null)
	if (settings.trace) {
		log.debug "getThermostatInfo> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}..."
	}
	api('thermostatInfo', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			data.thermostatList = resp.data.thermostatList
			def thermostatName = data.thermostatList[0].name
			// divide the temperature by 10 before for display or calculations later.
			data.thermostatList[0].runtime.actualTemperature = data.thermostatList[0].runtime
				.actualTemperature / 10
			data.thermostatList[0].runtime.desiredCool = data.thermostatList[0].runtime
				.desiredCool / 10
			data.thermostatList[0].runtime.desiredHeat = data.thermostatList[0].runtime
				.desiredHeat / 10
			data.thermostatList[0].weather.forecasts[0].temperature = data.thermostatList[
				0].weather.forecasts[0].temperature / 10
			data.thermostatList[0].weather.forecasts[0].tempLow = data.thermostatList[
				0].weather.forecasts[0].tempLow / 10
			data.thermostatList[0].weather.forecasts[0].tempHigh = data.thermostatList[
				0].weather.forecasts[0].tempHigh / 10
			data.thermostatList[0].settings.quickSaveSetBack = data.thermostatList[0].settings
				.quickSaveSetBack / 10
			data.thermostatList[0].settings.quickSaveSetForward = data.thermostatList[
				0].settings.quickSaveSetForward / 10
			if (data.thermostatList[0].events.size() > 0) {
				for (i in 0..data.thermostatList[0].events.size() - 1) {
					if (data.thermostatList[0].events[i].running) {
						// Divide the running events' temps by 10 for display later
						data.thermostatList[0].events[i].coolHoldTemp = data.thermostatList[0].events[
							i].coolHoldTemp / 10
						data.thermostatList[0].events[i].heatHoldTemp = data.thermostatList[0].events[
							i].heatHoldTemp / 10
					}
				}
			}
			if (settings.trace) {
				log.debug "getTstatInfo> got info for ${thermostatId} name=${thermostatName}, features=${resp.data}"
			}
			def runtimeSettings = data.thermostatList[0].runtime
			def thermostatSettings = data.thermostatList[0].settings
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:
					"getTstatInfo> currentTemp=${runtimeSettings.actualTemperature},${thermostatId},hvacMode = ${thermostatSettings.hvacMode}," +
					"fan = ${runtimeSettings.desiredFanMode}, fanMinOnTime = ${thermostatSettings.fanMinOnTime}, desiredHeat = ${runtimeSettings.desiredHeat} desiredCool = ${runtimeSettings.desiredCool}," +
					"current Humidity = ${runtimeSettings.actualHumidity}, desiredHumidity = ${runtimeSettings.desiredHumidity},humidifierMode= ${thermostatSettings.humidifierMode}," +
					"desiredDehumidity =  ${runtimeSettings.desiredDehumidity} dehumidifierMode= ${thermostatSettings.dehumidifierMode}"
				log.debug "getTstatInfo> thermostatId = ${thermostatId}, name = ${thermostatName},  hvacMode = ${thermostatSettings.hvacMode}," +
					"fan = ${runtimeSettings.desiredFanMode},fanMinOnTime = ${thermostatSettings.fanMinOnTime}, desiredHeat = ${runtimeSettings.desiredHeat} desiredCool = ${runtimeSettings.desiredCool}," +
					"current Humidity = ${runtimeSettings.actualHumidity} desiredHumidity = ${runtimeSettings.desiredHumidity},humidifierMode= ${thermostatSettings.humidifierMode}," +
					"desiredDehumidity =  ${runtimeSettings.desiredDehumidity} dehumidifierMode= ${thermostatSettings.dehumidifierMode}"
			}
		} else {
			log.error "getThermostatInfo> error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"getTstatInfo>error=${statusCode} for ${thermostatId}"
		}
	}
}

// tstatType =managementSet or registered (no spaces). 
// May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void getThermostatSummary(tstatType) {

	def bodyReq = build_body_request('thermostatSummary',tstatType,null,null)
	if (settings.trace) {
		log.debug "getThermostatSummary> about to call api with body = ${bodyReq}"
	}
	api('thermostatSummary', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			data.revisionList = resp.data.revisionList
			data.statusList = resp.data.statusList
			data.thermostatCount = data.revisionList.size()
			for (i in 0..data.thermostatCount - 1) {
				def thermostatDetails = data.revisionList[i].split(':')
				def thermostatId = thermostatDetails[0]
				def thermostatName = thermostatDetails[1]
				def connected = thermostatDetails[2]
				def thermostatRevision = thermostatDetails[3]
				def alertRevision = thermostatDetails[4]
				def runtimeRevision = thermostatDetails[5]
				if (settings.trace) {
					log.debug "getThermostatSummary> thermostatId = ${thermostatId}, name = ${thermostatName}, connected =${connected}"
					sendEvent name: "verboseTrace", value:
						"getTstatSummary> found ${thermostatId},name=${thermostatName},connected=${connected}"
				}
			}
		} else {
			log.error "getThermostatSummary> error=${statusCode.toString()}, message = ${message}"
			sendEvent name: "verboseTrace", value:
				"getTstatSummary> error= ${statusCode.toString()}"
		}
	}
}
/* Return thermostat's current Model Number */
def getModelNumber() {

	if (settings.trace) {
		log.debug "getModelNumber>thermostatId=${data.thermostatList[0]?.identifier}," +
        	"modelNumber=${data.thermostatList[0]?.modelNumber}"
	}
	return ((data.thermostatList[0].identifier)? data.thermostatList[0].modelNumber: "")
}

private def refresh_tokens() {
	def method = 
	[
		headers: [
			'Content-Type': "application/json",
			'charset': "UTF-8"
			],
		uri: "${get_URI_ROOT()}/token?" +
		"grant_type=refresh_token&" +
		"code=${data.auth.refresh_token}&" +
		"client_id=${get_appKey()}"
	]
	if (settings.trace) {
		log.debug "refresh_tokens> uri = ${method.uri}"
	}
	def successRefreshTokens = {resp ->
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
	} catch (java.net.UnknownHostException e) {
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
		sendEvent name: "verboseTrace", value:
			"refresh_tokens> general error at ${method.uri}"
		return false
	}
	// determine token's expire time
	def authexptime = new Date((now() + (data.auth.expires_in * 60 * 1000))).getTime()
	data.auth.authexptime = authexptime
// If this thermostat was created by initialSetup, go and refresh the parent and all other children
	if (data.auth.thermostatId) {		// Created by initalSetup, need to refresh Parent tokens and other children
		refreshParentTokens()
	}        
	if (settings.trace) {
		log.debug "refresh_tokens> expires in ${data.auth.expires_in} minutes"
		log.debug "refresh_tokens> data_auth.expires_in in time = ${authexptime}"
		sendEvent name: "verboseTrace", value:
			"refresh_tokens>expire in ${data.auth.expires_in} minutes"
	}
	return true
}
void refreshChildTokens(auth) {
	if (settings.trace) {
		log.debug "refreshChildTokens>begin token auth= $auth"
	}
	data.auth.access_token = auth.authToken
	data.auth.refresh_token = auth.refreshToken
	data.auth.expires_in = auth.expiresIn
	data.auth.token_type = auth.tokenType
	data.auth.scope = auth.scope
	data.auth.authexptime = auth.authexptime
	if (settings.trace) {
		log.debug "refreshChildTokens>end data.auth=$data.auth"
	}
}

private void refreshParentTokens() {
	if (settings.trace) {
		log.debug "refreshParentTokens>begin data.auth = ${data.auth}"
	}
	if (settings.trace) {
		log.debug "refreshParentTokens>auth=$auth, about to call parent.setParentAuthTokens"
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
	if (!isLoggedIn) {
		if (settings.trace) {
			log.debug "login> no access_token..., failed"
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
		data.auth = resp.data
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
	if (data.auth.access_token == null) {
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
		log.debug "isTokenExpired> auth: ${data.auth}"
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
		
	} else if (modelNumber.contains("Ems")) {
    
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
	settings.trace='true'
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
}

def toQueryString(Map m) {
	return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
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
private def get_URI_ROOT() {
	return "https://api.ecobee.com"
}
// Maximum tstat batch size (25 thermostats max may be processed in batch)
private def get_MAX_TSTAT_BATCH() {
	return 25
}
