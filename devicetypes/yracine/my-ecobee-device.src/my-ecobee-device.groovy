/***
 *  My Ecobee Device
 *  Copyright 2014 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Version 5.7.4
 *  Refer to readme file for installation instructions.
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
 *  Software Distribution is restricted and shall be done only with Developer's written approval.
 */
import java.text.SimpleDateFormat

include 'asynchttp_v1'

// for the UI

preferences {

//	Preferences are no longer required when created with the Service Manager (MyEcobeeInit).

	input("thermostatId", "text", title: "Serial #", description:
		"The serial number of your thermostat (no spaces)")
	input("appKey", "text", title: "App Key", description:
		"The application key given by Ecobee (no spaces)")
	input("trace", "bool", title: "trace", description:
		"Set it to true to enable tracing (no spaces) or leave it empty (no tracing)")
	input("holdType", "text", title: "holdType", description:
		"Set it to nextTransition or indefinite (latter by default)")
	input("ecobeeType", "text", title: "ecobee Tstat Type", description:
		"Set it to registered (by default) or managementSet (no spaces)")
	input("logFilter", "number",title: "(1=ERROR only,2=<1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>)",  range: "1..5",
 		description: "optional" )        
}
metadata {
	// Automatically generated. Make future change here.
	definition(name: "My Ecobee Device", author: "Yves Racine",  namespace: "yracine") {
		capability "Thermostat"
		capability "thermostatHeatingSetpoint"
		capability "thermostatCoolingSetpoint"
		capability "thermostatSetpoint"
		capability "thermostatMode"
		capability "thermostatFanMode"
		capability "thermostatOperatingState"        
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Polling"
		capability "Refresh"
		capability "Presence Sensor"
		capability "Actuator"
		capability "Health Check"
        
		attribute "thermostatId", "string"
		attribute "thermostatName", "string"
		attribute "temperatureDisplay", "string"
		attribute "coolingSetpointDisplay", "string"
		attribute "heatingSetpointDisplay", "string"
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
		attribute "alertText", "string"        
		attribute "programScheduleName", "string"
		attribute "programFanMode", "string"
		attribute "programType", "string"
		attribute "programCoolTemp", "string"
		attribute "programHeatTemp", "string"
		attribute "programCoolTempDisplay", "string"
		attribute "programHeatTempDisplay", "string"
		attribute "programEndTimeMsg", "string"
		attribute "weatherDateTime", "string"
		attribute "weatherSymbol", "string"
		attribute "weatherStation", "string"
		attribute "weatherCondition", "string"
		attribute "weatherTemperatureDisplay", "string"
		attribute "weatherPressure", "string"
		attribute "weatherRelativeHumidity", "string"
		attribute "weatherWindSpeed", "string"
		attribute "weatherWindDirection", "string"
		attribute "weatherPop", "string"
		attribute "weatherTempHigh", "string"
		attribute "weatherTempLow", "string"
		attribute "weatherTempHighDisplay", "string"
		attribute "weatherTempLowDisplay", "string"
		attribute "plugName", "string"
		attribute "plugState", "string"
		attribute "plugSettings", "string"
		attribute "hasHumidifier", "string"
		attribute "hasDehumidifier", "string"
		attribute "hasErv", "string"
		attribute "hasHrv", "string"
		attribute "ventilatorMinOnTime", "string"
		attribute "ventilatorMode", "string"
		attribute "programNameForUI", "string"
		attribute "thermostatOperatingState", "string"        
		attribute "climateList", "string"	
		attribute "modelNumber", "string"
		attribute "followMeComfort", "string"
		attribute "autoAway", "string"
		attribute "intervalRevision", "string"
		attribute "runtimeRevision", "string"
		attribute "thermostatRevision", "string"
		attribute "heatStages", "string"
		attribute "coolStages", "string"
		attribute "climateName", "string"
		attribute "setClimate", "string"
		attribute "auxMaxOutdoorTemp","string"
		attribute "stage1HeatingDifferentialTemp", "string"        
		attribute "stage1CoolingDifferentialTemp", "string"        
		attribute "stage1HeatingDissipationTime", "string"
		attribute "stage1CoolingDissipationTime", "string"
        
		// Report Runtime events
        
		attribute "auxHeat1RuntimeInPeriod", "string"
		attribute "auxHeat2RuntimeInPeriod", "string"
		attribute "auxHeat3RuntimeInPeriod", "string"
		attribute "compCool1RuntimeInPeriod", "string"
		attribute "compCool2RuntimeInPeriod", "string"
		attribute "dehumidifierRuntimeInPeriod", "string"
		attribute "humidifierRuntimeInPeriod", "string"
		attribute "ventilatorRuntimeInPeriod", "string"
		attribute "fanRuntimeInPeriod", "string"

		attribute "auxHeat1RuntimeDaily", "string"
		attribute "auxHeat2RuntimeDaily", "string"
		attribute "auxHeat3RuntimeDaily", "string"
		attribute "compCool1RuntimeDaily", "string"
		attribute "compCool2RuntimeDaily", "string"
		attribute "dehumidifierRuntimeDaily", "string"
		attribute "humidifierRuntimeDaily", "string"
		attribute "ventilatorRuntimeDaily", "string"
		attribute "fanRuntimeDaily", "string"
		attribute "reportData", "string"

		attribute "auxHeat1RuntimeYesterday", "string"
		attribute "auxHeat2RuntimeYesterday", "string"
		attribute "auxHeat3RuntimeYesterday", "string"
		attribute "compCool1RuntimeYesterday", "string"
		attribute "compCool2RuntimeYesterday", "string"

		attribute "auxHeat1RuntimeAvgWeekly", "string"
		attribute "auxHeat2RuntimeAvgWeekly", "string"
		attribute "auxHeat3RuntimeAvgWeekly", "string"
		attribute "compCool1RuntimeAvgWeekly", "string"
		attribute "compCool2RuntimeAvgWeekly", "string"

		attribute "auxHeat1RuntimeAvgMonthly", "string"
		attribute "auxHeat2RuntimeAvgMonthly", "string"
		attribute "auxHeat3RuntimeAvgMonthly", "string"
		attribute "compCool1RuntimeAvgMonthly", "string"
		attribute "compCool2RuntimeAvgMonthly", "string"


		// Report Sensor Data & Stats
		        
		attribute "reportSensorMetadata", "string"
		attribute "reportSensorData", "string"
		attribute "reportSensorAvgInPeriod", "string"
		attribute "reportSensorMinInPeriod", "string"
		attribute "reportSensorMaxInPeriod", "string"
		attribute "reportSensorTotalInPeriod", "string"
        
		// Remote Sensor Data & Stats

		attribute "remoteSensorData", "string"
		attribute "remoteSensorTmpData", "string"
		attribute "remoteSensorHumData", "string"
		attribute "remoteSensorOccData", "string"
		attribute "remoteSensorAvgTemp", "string"
		attribute "remoteSensorAvgHumidity", "string"
		attribute "remoteSensorMinTemp", "string"
		attribute "remoteSensorMinHumidity", "string"
		attribute "remoteSensorMaxTemp", "string"
		attribute "remoteSensorMaxHumidity", "string"


		// Recommendations 
        
		attribute "tip1Text", "string"
		attribute "tip1Level", "string"
		attribute "tip1Solution", "string"
		attribute "tip2Text", "string"
		attribute "tip2Level", "string"
		attribute "tip2Solution", "string"
		attribute "tip3Text", "string"
		attribute "tip3Level", "string"
		attribute "tip3Solution", "string"
		attribute "tip4Text", "string"
		attribute "tip4Level", "string"
		attribute "tip4Solution", "string"
		attribute "tip5Text", "string"
		attribute "tip5Level", "string"
		attribute "tip5Solution", "string"
        

		command "levelUp"
		command "levelDown"
		command "setTemperature", ["number"]
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
		command "humidifierManual"
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
		command "controlPlug" 
		command "ventilatorOn"
		command "ventilatorAuto"
		command "ventilatorOff"
		command "setVentilatorMinOnTime"
		command "awake"
		command "away"
		command "present"
		command "home"
		command "asleep"
		command "quickSave"
		command "setThisTstatClimate"
		command "setThermostatSettings"
		command "iterateSetThermostatSettings"
		command "getEquipmentStatus"
		command "refreshChildTokens" 
		command "autoAway"
		command "followMeComfort"
		command "getReportData"
		command "generateReportRuntimeEvents"
		command "generateReportSensorStatsEvents"
		command "getThermostatRevision"
		command "generateRemoteSensorEvents"
		command "getRemoteSensorUpdate"   
		command "getTips"  
		command "resetTips"     
		command "getAlertText"        
	}        
	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
    
		multiAttributeTile(name:"thermostatMulti", type:"thermostat", width:6, height:4,canChangeIcon: true) {
			tileAttribute("device.temperatureDisplay", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}', unit:"dF", backgroundColor:"#269bd2") 
			}
			tileAttribute("device.temperatureDisplay", key: "VALUE_CONTROL") {
				attributeState("default", action: "setTemperature")
				attributeState("VALUE_UP", action: "levelUp")
				attributeState("VALUE_DOWN", action: "levelDown")
			}
			tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue}%', unit:"%")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621")
				attributeState("heating", backgroundColor:"#ffa81e")
				attributeState("cooling", backgroundColor:"#269bd2")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label:'${name}')
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
				attributeState("auto", label:'${name}')
			}
			tileAttribute("device.heatingSetpointDisplay", key: "HEATING_SETPOINT") {
				attributeState("default", label:'${currentValue}', unit:"dF")
			}
			tileAttribute("device.coolingSetpointDisplay", key: "COOLING_SETPOINT") {
				attributeState("default", label:'${currentValue}', unit:"dF")
			}
		}
		valueTile("temperatureDisplay", "device.temperatureDisplay", width: 2, height: 2) {
			state("temperatureDisplay", label:'${currentValue}', unit:"dF",
			backgroundColors: getBackgroundColors())
		}
		valueTile("name", "device.thermostatName", inactiveLabel: false, width: 2,
			height: 2, decoration: "flat") {
			state "default", label: '${currentValue}\n', 
			backgroundColor: "#ffffff"
		}
		standardTile("heatLevelUp", "device.heatingSetpoint", width: 3, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "heatLevelUp", label:'Heat', action:"heatLevelUp", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/heatUp.png", backgroundColor: "#ffffff"
		}
		standardTile("heatLevelDown", "device.heatingSetpoint", width: 3, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "heatLevelDown", label:'Heat', action:"heatLevelDown", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/heatDown.png", backgroundColor: "#ffffff"

		}
		valueTile("heatingSetpoint", "device.heatingSetpointDisplay", width: 3, height: 2, inactiveLabel: false) {
			state ("heat", label:'${currentValue}', unit:"F",
			backgroundColors:[
				[value: 0, color: "#153591"],
				[value: 7, color: "#1e9cbb"],
				[value: 15, color: "#90d2a7"],
				[value: 23, color: "#44b621"],
				[value: 29, color: "#f1d801"],
				[value: 33, color: "#d04e00"],
				[value: 36, color: "#bc2323"],
				// Fahrenheit Color Range
				[value: 40, color: "#153591"],
				[value: 44, color: "#1e9cbb"],
				[value: 59, color: "#90d2a7"],
				[value: 74, color: "#44b621"],
				[value: 84, color: "#f1d801"],
				[value: 92, color: "#d04e00"],
				[value: 96, color: "#bc2323"]
			])
		}
		valueTile("coolingSetpoint", "device.coolingSetpointDisplay", width: 3, height: 2, inactiveLabel: false,decoration: "flat") {
			state ("cool", label:'${currentValue}', unit:"F",
			backgroundColors:[
				[value: 0, color: "#153591"],
				[value: 7, color: "#1e9cbb"],
				[value: 15, color: "#90d2a7"],
				[value: 23, color: "#44b621"],
				[value: 29, color: "#f1d801"],
				[value: 33, color: "#d04e00"],
				[value: 36, color: "#bc2323"],
				// Fahrenheit Color Range
				[value: 40, color: "#153591"],
				[value: 44, color: "#1e9cbb"],
				[value: 59, color: "#90d2a7"],
				[value: 74, color: "#44b621"],
				[value: 84, color: "#f1d801"],
				[value: 92, color: "#d04e00"],
				[value: 96, color: "#bc2323"]
			])
			            

		}

		standardTile("coolLevelUp", "device.coolingSetpoint", width: 3, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "coolLevelUp", label:'Cool', action:"coolLevelUp", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/coolUp.png",  backgroundColor: "#ffffff"
		}

		standardTile("coolLevelDown", "device.coolingSetpoint", width: 3, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "coolLevelDown", label:'Cool', action:"coolLevelDown", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/coolDown.png",backgroundColor: "#ffffff"
		}

 
/*        
		valueTile("heatingSetpoint", "device.heatingSetpointDisplay", width: 3, height: 2, inactiveLabel: false) {
			state "heat", label:'${currentValue}', unit:"F",
			backgroundColors: getBackgroundColors()
		}
		valueTile("coolingSetpoint", "device.coolingSetpointDisplay", width: 3, height: 2, inactiveLabel: false,decoration: "flat") {
			state "cool", label:'${currentValue}', unit:"F",
			backgroundColors: getBackgroundColors()
		}
        
		controlTile("coolSliderControl", "device.coolingSetpoint", "slider", height: 1, width:6, inactiveLabel: false) {
			state "setCoolingSetpoint", action:"thermostat.setCoolingSetpoint", backgroundColor: "#1e9cbb"
		}
		controlTile("heatSliderControl", "device.heatingSetpoint", "slider", height: 1, width: 6, inactiveLabel: false) {
			state "setHeatingSetpoint", action:"thermostat.setHeatingSetpoint", backgroundColor:"#d04e00"
		}
        
		standardTile("heatLevelDown", "device.heatingSetpoint", width: 2, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "heatLevelDown", label:'Heat', action:"heatLevelDown", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/heatDown.png", backgroundColor: "#ffffff"

		}
		standardTile("heatLevelUp", "device.heatingSetpoint", width: 2, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "heatLevelUp", label:'Heat', action:"heatLevelUp", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/heatUp.png", backgroundColor: "#ffffff"
		}

		standardTile("heatingSetpoint", "device.heatingSetpointDisplay", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "heat", label:'${currentValue}∞ heat', unit:"F", backgroundColor:"#ffffff",
			icon: "home1-icn@2x"            
		}
		standardTile("coolLevelDown", "device.coolingSetpoint", width: 2, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "coolLevelDown", label:'Cool', action:"coolLevelDown", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/coolDown.png",backgroundColor: "#ffffff"
		}

		standardTile("coolLevelUp", "device.coolingSetpoint", width: 2, height: 1, canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
			state "coolLevelUp", label:'Cool', action:"coolLevelUp", icon:"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/coolUp.png",  backgroundColor: "#ffffff"
		}

		standardTile("coolingSetpoint", "device.coolingSetpointDisplay", height: 1, width: 2,inactiveLabel: false, decoration: "flat") {
			state "cool", label:'${currentValue}∞ cool', unit:"F", backgroundColor:"#ffffff",
			icon: "home1-icn@2x"            
		}
        
*/
 		standardTile("mode", "device.thermostatMode", inactiveLabel: false,
			decoration: "flat", width: 2, height: 2,) {
			state "heat", label: '${name}', action: "thermostat.off", 
				icon: "http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/heatMode.png", backgroundColor: "#ffffff",
				nextState: "off"
			state "off", label: '${name}', action: "thermostat.cool", 
				icon: "st.Outdoor.outdoor19", 
				nextState: "cool"
			state "cool", label: '${name}', action: "thermostat.auto", 
				icon: "http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/coolMode.png",
				nextState: "auto"
			state "auto", label: '${name}',action: "thermostat.heat", 
				icon: "http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/autoMode.png",
				nextState: "heat"
 			backgroundColor: "#ffffff"
		}
        
		standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false,
			decoration: "flat", width: 2, height: 2) {
			state "auto", label: '${name}', action: "thermostat.fanOn", 
				icon: "st.Appliances.appliances11", backgroundColor: "#ffffff",
				nextState: "on"
			state "on", label: '${name}', action: "thermostat.fanAuto", 
				icon: "st.Appliances.appliances11",backgroundColor: "#ffffff",
				nextState: "auto"
		}
		standardTile("operatingState", "device.thermostatOperatingState", width: 2, height: 2) {
			state "idle", label:'${name}', backgroundColor:"#ffffff"
			state "heating", label:'${name}', backgroundColor:"#ffa81e"
			state "cooling", label:'${name}', backgroundColor:"#269bd2"
		}
		standardTile("switchProgram", "device.programNameForUI",  canChangeIcon: false,
			inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "Home", label: 'Set Program Now:${name}', action: "asleep", 
				icon: "st.Home.home4",backgroundColor: "#ffffff",
				nextState: "Sleep"
			state "Sleep", label: 'Set Program Now: ${name}', action: "awake", 
 				icon: "st.Bedroom.bedroom2",backgroundColor: "#ffffff",
				nextState: "Awake"
			state "Awake", label: 'Set Program Now: ${name}', action: "away", 
				icon: "st.Outdoor.outdoor20",backgroundColor: "#ffffff",
				nextState: "Away"
			state "Away", label: 'Set Program Now: ${name}', action: "quickSave", 
				icon: "st.presence.car.car",backgroundColor: "#ffffff",
				nextState: "Save"
			state "Save", label: 'Set Program Now: ${name}', action: "home", 
				icon: "st.Home.home1",backgroundColor: "#ffffff",
				nextState: "Home"
			state "Other", label: 'Set Program Now: ${name}', action: "resumeThisTstat", 
				icon: "st.Office.office6",backgroundColor: "#ffffff"
 			
                   
		}
		valueTile("equipStatus", "device.equipmentStatus", inactiveLabel: false,
			decoration: "flat", width: 6, height: 2) {
			state "default", label: 'Equipment Status\n ${currentValue}',
 			backgroundColor: "#ffffff"
		}
		valueTile("programEndTimeMsg", "device.programEndTimeMsg", inactiveLabel: false,	
			decoration: "flat", width: 2, height: 2) {
			state "default", label: '${currentValue}'
		}
		valueTile("alerts", "device.alerts", inactiveLabel: false, decoration: "flat",
			width: 6, height: 2) {
			state "default", label: 'Alerts\n ${currentValue}',
 			backgroundColor: "#ffffff"
		}
        
		standardTile("fanMinOnTime", "device.fanMinOnTime", inactiveLabel: false,
			decoration: "flat", width: 2, height: 2, canChangeIcon: false,) {
			state "default", label: 'FanMinTime ${currentValue}',
			icon: "st.Appliances.appliances11"
		}
		// Program Tiles
		standardTile("programScheduleName", "device.programScheduleName", inactiveLabel: false, 
			width: 2, height: 2, decoration: "flat",  canChangeIcon: false) {
			state "default", label: 'Mode ${currentValue}',
			icon: "st.Office.office7",
			backgroundColor: "#ffffff"
		}
		standardTile("programType", "device.programType", inactiveLabel: false, 
			width: 2,height: 2, decoration: "flat",  canChangeIcon: false) {
			state "default", label: 'Prog Type ${currentValue}', 
			icon: "st.Office.office7"
		}
		standardTile("programCoolTemp", "device.programCoolTempDisplay", 
			width: 2, height: 2, decoration: "flat",inactiveLabel: false, canChangeIcon: false) {
			state "default", label: 'Prog Cool ${currentValue}',
			icon: "st.Office.office7"
		}
		standardTile("programHeatTemp", "device.programHeatTempDisplay", 
			width: 2, height: 2, decoration: "flat",inactiveLabel: false,  canChangeIcon: false) {
			state "default", label: 'Prog Heat ${currentValue}', 			
			icon: "st.Office.office7"
		}
		standardTile("resProgram", "device.thermostatMode", inactiveLabel: false,
			decoration: "flat",width: 2, height: 2, canChangeIcon: false) {
			state "default", label: 'Resume Program', action: "resumeThisTstat", 
			icon: "st.Office.office7"
		}
		// Weather Tiles
		standardTile("weatherIcon", "device.weatherSymbol", inactiveLabel: false, width: 2, height: 2,
			decoration: "flat",  canChangeIcon: false) {
			state "-2",			label: 'updating...',	icon: "st.unknown.unknown.unknown"
			state "0",			label: 'Sunny',			icon: "st.Weather.weather14"
			state "1",			label: 'FewClouds',		icon: "st.Weather.weather15"
			state "2",			label: 'PartlyCloudy',	icon: "st.Weather.weather15"
			state "3",			label: 'MostlyCloudy',	icon: "st.Weather.weather15"
			state "4",			label: 'Overcast',		icon: "st.Weather.weather13"
			state "5",			label: 'Drizzle',		icon: "st.Weather.weather9"
			state "6",			label: 'Rain',			icon: "st.Weather.weather10"
			state "7",			label: 'FreezingRain',	icon: "st.Weather.weather10"
			state "8",			label: 'Showers',		icon: "st.Weather.weather10"
			state "9",			label: 'Hail',			icon: "st.custom.wuk.sleet"
			state "10",			label: 'Snow',			icon: "st.Weather.weather6"
			state "11",			label: 'Flurries',		icon: "st.Weather.weather6"
			state "12",			label: 'Sleet',			icon: "st.Weather.weather6"
			state "13",			label: 'Blizzard',		icon: "st.Weather.weather7"
			state "14",			label: 'Pellets',		icon: "st.custom.wuk.sleet"
			state "15",			label: 'ThunderStorms',	icon: "st.custom.wuk.tstorms"
			state "16",			label: 'Windy',			icon: "st.Transportation.transportation5"
			state "17",			label: 'Tornado',		icon: "st.Weather.weather1"
			state "18",			label: 'Fog',			icon: "st.Weather.weather13"
			state "19",			label: 'Hazy',			icon: "st.Weather.weather13"
			state "20",			label: 'Smoke',			icon: "st.Weather.weather13"
			state "21",			label: 'Dust',			icon: "st.Weather.weather13",
 			backgroundColor: "#ffffff"
            
		}
		standardTile("weatherDateTime", "device.weatherDateTime", inactiveLabel: false,
			width: 3, height: 2, decoration: "flat", canChangeIcon: false) {
			state "default", label: '${currentValue}', 
			icon: "st.Weather.weather11",
 			backgroundColor: "#ffffff"
		}
		standardTile("weatherConditions", "device.weatherCondition", 
			inactiveLabel: false, width: 3, height: 2, decoration: "flat", canChangeIcon: false) {
			state "default", label: 'Forecast\n ${currentValue}',
			icon: "st.Weather.weather11",
 			backgroundColor: "#ffffff"
		}
		standardTile("weatherTemperature", "device.weatherTemperatureDisplay", inactiveLabel:false, width: 2, height: 2, 
			decoration: "flat", canChangeIcon: false) {
			state "default", label: 'Outdoor Temp ${currentValue}', unit: "C",
			icon: "st.Weather.weather2",
			backgroundColor: "#ffffff"
		}
		standardTile("weatherRelativeHumidity", "device.weatherRelativeHumidity",
			inactiveLabel: false, width: 2, height: 2,decoration: "flat") {
			state "default", label: 'Outdoor Hum ${currentValue}%', unit: "humidity",
			icon: "st.Weather.weather2",
 			backgroundColor: "#ffffff"
		}
		valueTile("weatherTempHigh", "device.weatherTempHigh", inactiveLabel: false,
			width: 2, height: 2, decoration: "flat") {
			state "default", label: 'Forecast\nHigh\n${currentValue}', unit: "C",
 			backgroundColor: "#ffffff"
		}
		valueTile("weatherTempLow", "device.weatherTempLow", inactiveLabel: false,
			width: 2, height: 2, decoration: "flat") {
			state "default", label: 'Forecast\nLow\n${currentValue}', unit: "C",
 			backgroundColor: "#ffffff"
		}
		valueTile("weatherPressure", "device.weatherPressure", inactiveLabel: false,
			width: 2, height: 2, decoration: "flat") {
			state "default", label: 'Pressure\n${currentValue}', unit: "hpa",
 			backgroundColor: "#ffffff"
		}
		valueTile("weatherWindDirection", "device.weatherWindDirection",
			inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
			state "default", label: 'Wind\nDirections\n${currentValue}',
 			backgroundColor: "#ffffff"
		}
		valueTile("weatherWindSpeed", "device.weatherWindSpeed", inactiveLabel: false,
			width: 2, height: 2, decoration: "flat") {
			state "default", label: 'Wind Speed\n${currentValue}',
 			backgroundColor: "#ffffff"
		}
		standardTile("weatherPop", "device.weatherPop", inactiveLabel: false, 
 			width: 2,height: 2,  decoration: "flat", canChangeIcon: false) {
			state "default", label: 'Rain Prob. ${currentValue}%', unit: "%",
			icon: "st.Weather.weather2",
 			backgroundColor: "#ffffff"
		}
		standardTile("refresh", "device.temperature", inactiveLabel: false, canChangeIcon: false,
			decoration: "flat",width: 2, height: 2) {
			state "default", label: 'Refresh',action: "refresh", icon:"st.secondary.refresh", 			
			backgroundColor: "#ffffff"
		}
		standardTile("present", "device.presence", inactiveLabel: false, height:2, width:2, canChangeIcon: false) {
			state "not present", label:'${name}', backgroundColor: "#ffffff", icon:"st.presence.tile.presence-default" 
			state "present", label:'${name}', backgroundColor: "#ffffff", icon:"st.presence.tile.presence-default" 
		}
		htmlTile(name:"graphHTML", action: "getGraphHTML", width: 6, height: 8,  whitelist: ["www.gstatic.com"])
       
       
		main("thermostatMulti")
		details(["thermostatMulti",
			"name",	"mode",	"fanMode",
/*            
			"heatSliderControl",
			"heatingSetpoint",            
			"coolSliderControl",
			"coolingSetpoint",            
*/           
 			"heatLevelUp", "coolLevelUp", "heatingSetpoint", "coolingSetpoint", "heatLevelDown", "coolLevelDown",
/*
 			"heatLevelUp", "heatLevelDown", "heatingSetpoint", 
 			"coolLevelUp", "coolLevelDown", "coolingSetpoint", 
*/            
			"programType","programHeatTemp", "programCoolTemp",
			"switchProgram", "programScheduleName",
			"resProgram", "refresh",
			"programEndTimeMsg","fanMinOnTime", 
			"equipStatus",
 			"alerts",
			"weatherDateTime", "weatherConditions",
			"weatherTemperature", 
/*			 Commented out in new UI            
			"weatherIcon", "weatherTempHigh", "weatherTempLow", "weatherPressure",
			"weatherWindDirection", "weatherWindSpeed",
*/            
			"weatherRelativeHumidity","weatherPop",
//			"present",            
			"graphHTML"            
		])

	}
    
}


def getBackgroundColors() {
	def results
	if (state?.scale =='C') {
				// Celsius Color Range
		results=
			[        
				[value: 0, color: "#153591"],
				[value: 7, color: "#1e9cbb"],
				[value: 15, color: "#90d2a7"],
				[value: 23, color: "#44b621"],
				[value: 29, color: "#f1d801"],
				[value: 33, color: "#d04e00"],
				[value: 36, color: "#bc2323"]
			]
	} else {
		results =
				// Fahrenheit Color Range
			[        
				[value: 40, color: "#153591"],
				[value: 44, color: "#1e9cbb"],
				[value: 59, color: "#90d2a7"],
				[value: 74, color: "#44b621"],
				[value: 84, color: "#f1d801"],
				[value: 92, color: "#d04e00"],
				[value: 96, color: "#bc2323"]
			]  
	}
	return results    
}

mappings {
	path("/getGraphHTML") {action: [GET: "getGraphHTML"]}
}

void installed() {
	def HEALTH_TIMEOUT= (60 * 60)
	sendEvent(name: "checkInterval", value: HEALTH_TIMEOUT, data: [protocol: "cloud", displayed:(settings.trace?:false)])
	state?.scale=getTemperatureScale() 
	if (settings.trace) { 
			log.debug("installed>$device.displayName installed with settings: ${settings.inspect()} and state variables= ${state.inspect()}")
	}
}  

def updated() {
	def HEALTH_TIMEOUT= (60 * 60)
	sendEvent(name: "checkInterval", value: HEALTH_TIMEOUT, data: [protocol: "cloud", displayed:(settings.trace?:false)])
    
	state?.runtimeStatsTable=null
	state?.runtimeAvgWeeklyStatsTable=null
	state?.runtimeAvgMonthlyStatsTable=null
	state?.scale=getTemperatureScale() 
	traceEvent(settings.logFilter,"updated>$device.displayName updated with settings: ${settings.inspect()} and state variables= ${state.inspect()}", settings.trace)
	retrieveDataForGraph()        
}

private def evaluate(temp, heatingSetpoint, coolingSetpoint, nosetpoint=false) {
	traceEvent(settings.logFilter,"evaluate($temp, $heatingSetpoint, $coolingSetpoint)", settings.trace)
	def threshold = (state?.scale == 'C') ? 0.5 : 1
	def current = device.currentValue("thermostatOperatingState")
	def mode = device.currentValue("thermostatMode")
	def heating = false
	def cooling = false
	def idle = false
 	def dataEvents=[:]
    
	if (mode in ["heat","emergency heat","auto"]) {
		if ((temp - heatingSetpoint) >= threshold) {
			heating = true
			if (!nosetpoint) {            
				setHeatingSetpoint(temp)
				traceEvent(settings.logFilter,"increasing heat to $temp", settings.trace)
			}                
			dataEvents= ['thermostatOperatingState':"heating"] 
		}
		else if ((heatingSetpoint - temp) >= threshold) {
			idle = true
			if (!nosetpoint) {            
				setHeatingSetpoint(temp)
				traceEvent(settings.logFilter,"decreasing heat to $temp", settings.trace)
			}	        
		}
	}
	if (mode in ["cool","auto"]) {
		if ((coolingSetpoint - temp) >= threshold) {
			cooling = true
			if (!nosetpoint) {            
				setCoolingSetpoint(temp)
				traceEvent(settings.logFilter,"increasing cool to $temp", settings.trace)
			}	        
			dataEvents= dataEvents + ['thermostatOperatingState':"cooling"] 
		} else if (((temp - coolingSetpoint) >= threshold) && (!heating)) {
			idle = true	
			if (!nosetpoint) {            
				setCoolingSetpoint(temp)
				traceEvent(settings.logFilter,"decreasing cool to $temp", settings.trace)
			}	        
		}
	}
	if (idle && !heating && !cooling) {
			dataEvents= dataEvents + ['thermostatOperatingState':"idle"] 
				traceEvent(settings.logFilter,"idle state", settings.trace)
            
	}
	if (!nosetpoint) {            
		generateEvent(dataEvents)
	}
	
}

void setTemperature(value) {
	traceEvent(settings.logFilter,"setTemperature>initial value= $value")
	def mode = device.currentValue("thermostatMode")
	def heatTemp = device.currentValue("heatingSetpoint")
	def coolTemp = device.currentValue("coolingSetpoint")
	def curTemp = device.currentValue("temperature")
	switch(mode) {
		case 'heat':
			state?.currentTileValue= (!state?.currentTileValue)? heatTemp: state?.currentTileValue 
		break
		case 'cool':
			state?.currentTileValue= (!state?.currentTileValue)? coolTemp: state?.currentTileValue 
		break
		case 'auto':
		case 'off':        
		default:
			state?.currentTileValue= (!state?.currentTileValue)? curTemp: state?.currentTileValue 
		break
	}            

	if (state?.scale== 'C') { 		
    
		double temp  
		if (value==-1 || value == 0) {
			temp = state?.currentTileValue - 0.5           
		} else if (value==1) {
			temp = state?.currentTileValue + 0.5            
		} else {        
			temp = (value <= state?.currentTileValue)? (state?.currentTileValue - 0.5) : ( state?.currentTileValue + 0.5)   
		}        
		traceEvent(settings.logFilter,"setTemperature in Celsius>after temp correction= $temp vs. state.currentTileValue=$state.currentTileValue",settings.trace)
		evaluate(temp, heatTemp, coolTemp)
		state?.currentTileValue=temp	        
	} else {
		int temp        
		if (value==-1 || value == 0) {
			temp = state?.currentTileValue - 1           
		} else if (value==1) {
			temp = state?.currentTileValue + 1            
		} else {        
			temp = (value <= state?.currentTileValue)? state?.currentTileValue - 1 : state?.currentTileValue + 1   
		}        
		traceEvent(settings.logFilter,"setTemperature in Farenheit>after temp= $temp vs. state.currentTileValue=$state.currentTileValue",settings.trace)
		evaluate(temp, heatTemp, coolTemp)
		state?.currentTileValue=temp	        
	}        
        
}		

private void updateCurrentTileValue() {
	def heatTemp = device.currentValue("heatingSetpoint")
	def coolTemp = device.currentValue("coolingSetpoint")
	def curTemp = device.currentValue("temperature")
	def mode = device.currentValue("thermostatMode")
	switch(mode) {
		case 'heat':
			state?.currentTileValue=  heatTemp 
		break
		case 'cool':
			state?.currentTileValue= coolTemp
		break
		case 'auto':
		case 'off':        
		default:
			state?.currentTileValue= curTemp 
		break
	}            
}

void levelUp() {
	setTemperature(1)
}

void levelDown() {
	setTemperature(-1)
}
void coolLevelUp() {
	def scale = state?.scale
	if (scale == 'C') {
		double nextLevel = device.currentValue("coolingSetpoint").toDouble() 
		nextLevel = (nextLevel + 0.5).round(1)        
		if (nextLevel > 30) {
			nextLevel = 30
		}
		setCoolingSetpoint(nextLevel)
	} else {
		int nextLevel = device.currentValue("coolingSetpoint") 
		nextLevel = nextLevel + 1    
		if (nextLevel > 99) {
			nextLevel = 99
		}
		setCoolingSetpoint(nextLevel)
	}
	updateCurrentTileValue()    
}

void coolLevelDown() {
	def scale = state?.scale
	if (scale == 'C') {
		double nextLevel = device.currentValue("coolingSetpoint").toDouble() 
		nextLevel = (nextLevel - 0.5).round(1)        
		if (nextLevel < 10) {
			nextLevel = 10.0
		}
		setCoolingSetpoint(nextLevel)
	} else {
		int nextLevel = device.currentValue("coolingSetpoint") 
		nextLevel = (nextLevel - 1)
		if (nextLevel < 50) {
			nextLevel = 50
		}
		setCoolingSetpoint(nextLevel)
	}
	updateCurrentTileValue()    
}
void heatLevelUp() {
	def scale = state?.scale
	if (scale == 'C') {
		double nextLevel = device.currentValue("heatingSetpoint").toDouble() 
		nextLevel = (nextLevel + 0.5).round(1)        
		if (nextLevel > 30) {
			nextLevel = 30.0
		}
		setHeatingSetpoint(nextLevel)
	} else {
		int nextLevel = device.currentValue("heatingSetpoint") 
		nextLevel = (nextLevel + 1)
		if (nextLevel > 99) {
			nextLevel = 99
		}
		setHeatingSetpoint(nextLevel)
	}
	updateCurrentTileValue()    
}
void heatLevelDown() {
	def scale = state?.scale
	if (scale == 'C') {
		double nextLevel = device.currentValue("heatingSetpoint").toDouble() 
		nextLevel = (nextLevel - 0.5).round(1)        
		if (nextLevel < 10) {
			nextLevel = 10.0
		}
		setHeatingSetpoint(nextLevel)
	} else {
		int nextLevel = device.currentValue("heatingSetpoint")
		nextLevel = (nextLevel - 1)
		if (nextLevel < 50) {
			nextLevel = 50
		}
		setHeatingSetpoint(nextLevel)
	}
	updateCurrentTileValue()    
}

// handle commands


void setHeatingSetpoint(temp) {
	def scale = state?.scale
	setHold("", device.currentValue("coolingSetpoint"), temp,
		null, null)
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'heatingSetpoint', value: temp,unit: scale,isStateChange:true)
	sendEvent(name: 'heatingSetpointDisplay', value: temp,unit: scale,isStateChange:true)
	def currentMode = device.currentValue("thermostatMode")
	if (currentMode=='heat')  {
		sendEvent(name: 'thermostatSetpoint', value: temp,unit: scale,isStateChange:true)     
	}
    
  	if (currentMode=='auto') {  
		def currentCoolingSetpoint= device.currentValue("coolingSetpoint")
	 	def medianPoint= ((temp + currentCoolingSetpoint)/2).toFloat().round(1)
		sendEvent(name: 'thermostatSetpoint', value: medianPoint,unit: scale, isStateChange:true)     
        
  	}      
	    
        
}


void setCoolingSetpoint(temp) {
	def scale = state?.scale
	setHold("", temp, device.currentValue("heatingSetpoint"),
		null, null)
	def currentMode = device.currentValue("thermostatMode")
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'coolingSetpoint', value: temp, unit: scale, isStateChange:true)
	sendEvent(name: 'coolingSetpointDisplay', value: temp, unit: scale, isStateChange:true)
	if (currentMode=='cool') {
		sendEvent(name:'thermostatSetpoint', value: temp, unit: scale,isStateChange:true)     
	}
  	if (currentMode=='auto') {  
		def currentHeatingSetpoint= device.currentValue("heatingSetpoint")
	 	def medianPoint= ((temp + currentHeatingSetpoint)/2).toFloat().round(1)
		sendEvent(name: 'thermostatSetpoint', value: medianPoint,unit: scale, isStateChange:true)     
        
  	}      
    
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
void fanOn() {
	setThermostatFanMode('on')
}
void fanAuto() {
	setThermostatFanMode('auto')
}
void fanOff() { // fanOff is not supported, setting it to 'auto' instead.
	setThermostatFanMode('auto')
}
def fanCirculate() {
	fanAuto()
	setFanMinOnTime(15)	// Set a minimum of 15 minutes of fan per hour
}
void setThermostatFanMode(mode) {
	mode = (mode == 'off') ? 'auto' : mode
	setHold("", device.currentValue("coolingSetpoint"), device
		.currentValue("heatingSetpoint"),
		mode, null)
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'thermostatFanMode', value: mode,isStateChange:true)
}
void setFanMinOnTime(minutes) {
	setThermostatSettings("", ['fanMinOnTime': "${minutes}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'fanMinOnTime', value: minutes,isStateChange:true)
}

void setThermostatMode(mode) {
	mode = mode == 'emergency heat' ? 'heat' : mode
	setThermostatSettings("", ['hvacMode': "${mode}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'thermostatMode', value: mode,isStateChange:true)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"),
		true)    
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
	setThermostatSettings("", ['vent': "minontime",
			'ventilatorMinOnTime': "${minutes}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'ventilatorMinOnTime', value: minutes)
	sendEvent(name: 'ventilatorMode', value: "minontime",isStateChange:true)
}
void setVentilatorMode(mode) {
	setThermostatSettings("", ['vent': "${mode}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'ventilatorMode', value: mode,isStateChange:true)
}
void setCondensationAvoid(flag) { // set the flag to true or false
	flag = flag == 'true' ? 'true' : 'false'
 	def mode = (flag=='true')? 'auto': 'manual'
	setHumidifierMode(mode)
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'condensationAvoid', value: flag,isStateChange:true)
}
void dehumidifierOn() {
	setDehumidifierMode('on')
}
void dehumidifierOff() {
	setDehumidifierMode('off')
}
void setDehumidifierMode(mode) {
	setThermostatSettings("", ['dehumidifierMode': "${mode}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'dehumidifierMode', value: mode,isStateChange:true)
}
void setDehumidifierLevel(level) {
	setThermostatSettings("", ['dehumidifierLevel': "${level}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'dehumidifierLevel', value: level,isStateChange:true)
}
void humidifierAuto() {
	setHumidifierMode('auto')
}
void humidifierManual() {
	setHumidifierMode('manual')
}
void humidifierOff() {
	setHumidifierMode('off')
}
void setHumidifierMode(mode) {
	setThermostatSettings("", ['humidifierMode': "${mode}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'humidifierMode', value: mode,isStateChange:true)
}
void setHumidifierLevel(level) {
	setThermostatSettings("", ['humidity': "${level}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'humidifierLevel', value: level,isStateChange:true)
}
// Only valid for ecobee3 thermostats, not for EMS or Smart, Smart-SI thermostats)
void followMeComfort(flag) {
	flag = flag == 'true' ? 'true' : 'false'
	setThermostatSettings("", ['followMeComfort': "${flag}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'followMeComfort', value: flag,isStateChange:true)
}
// Only valid for ecobee3 thermostats, not for EMS or Smart, Smart-SI thermostats)

void autoAway(flag) {
	flag = flag == 'true' ? 'true' : 'false'
	setThermostatSettings("", ['autoAway': "${flag}"])
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
	sendEvent(name: 'autoAway', value: flag,isStateChange:true)
}

void awake() {
	setThisTstatClimate("Awake")    
}
void away() {
	setThisTstatClimate("Away")
}
void present() {
	home()
}
void home() {
	setThisTstatClimate("Home")

}
void asleep() {
	setThisTstatClimate("Sleep")
}
void quickSave() {
	def thermostatId= determine_tstat_id("") 	    
	def currentProgramType = device.currentValue("programType")
	if (currentProgramType.toUpperCase() == 'VACATION') {
		traceEvent(settings.logFilter,"quickSave>thermostatId = ${thermostatId},cannot do quickSave switch due to vacation settings",settings.trace)
		return
	}
	float quickSaveSetBack, quickSaveSetForw, quickSaveHeating, quickSaveCooling
	def scale = state?.scale
	if (scale == 'C') {
		quickSaveSetBack = data.thermostatList[0].settings.quickSaveSetBack.toFloat() / 2 // approximate conversion of differential to celcius
		quickSaveSetForw = data.thermostatList[0].settings.quickSaveSetForward.toFloat() / 2
		quickSaveCooling = fToC(data.thermostatList[0].runtime.desiredCool.toFloat())
		quickSaveHeating = fToC(data.thermostatList[0].runtime.desiredHeat.toFloat())
	} else {
		quickSaveSetBack = data.thermostatList[0].settings.quickSaveSetBack.toFloat()
		quickSaveSetForw = data.thermostatList[0].settings.quickSaveSetForward.toFloat()
		quickSaveCooling = data.thermostatList[0].runtime.desiredCool.toFloat()
		quickSaveHeating = data.thermostatList[0].runtime.desiredHeat.toFloat()
	}
	quickSaveCooling = (quickSaveCooling + quickSaveSetForw).round(0)
	quickSaveHeating = (quickSaveHeating - quickSaveSetBack).round(0)
	setHold(thermostatId, quickSaveCooling, quickSaveHeating, null, null)
	def quickSaveMap = ['coolingSetpoint': quickSaveCooling,		
		'heatingSetpoint': quickSaveHeating,
		'programScheduleName': "Save",
		'programNameForUI': "Save"
	]        
	generateEvent(quickSaveMap)    
	    
}
  
void setThisTstatClimate(climateName) {

	def thermostatId= determine_tstat_id("") 	    
	def currentProgram = device.currentValue("programScheduleName")
	def currentProgramType = device.currentValue("programType").trim().toUpperCase()
	if (currentProgramType == 'VACATION') {
		traceEvent(settings.logFilter,"setThisTstatClimate>thermostatId = ${settings.thermostatId},cannot do the prog switch due to vacation settings",settings.trace)
		return
	}
    
	resumeProgram()
	setClimate(thermostatId, climateName)
	def exceptionCheck=device.currentValue("verboseTrace")
	if ((exceptionCheck) && (!exceptionCheck.contains("done"))) {
		return    
	}    
        
	sendEvent(name: 'programScheduleName', value: climateName, isStateChange:true)
	sendEvent(name: 'programNameForUI', value: climateName, isStateChange: true)
	sendEvent(name: 'setClimate', value: climateName, isStateChange: true)
        
	refresh_thermostat(thermostatId) // to refresh the values in the UI
	if (climateName.toUpperCase().contains('AWAY')) { 
		sendEvent(name: "presence", value: "not present", isStateChange:true)
	} else {        
		sendEvent(name: "presence", value: "present",isStateChange:true)
	}    
}

// parse events into attributes
def parse(String description) {

}

// thermostatId is single thermostatId (not a list)
private def refresh_thermostat(thermostatId, refreshValues=null) {
    
	if (refreshValues==null) {
		getThermostatInfo(thermostatId)
		String exceptionCheck = device.currentValue("verboseTrace")
		if ((exceptionCheck) && (exceptionCheck.contains("exception")) || (exceptionCheck.contains("error"))) {
		// check if there is any exception or an error reported in the verboseTrace associated to the device 
			traceEvent(settings.logFilter, "poll>$exceptionCheck", true, get_LOG_ERROR()) 
			return    
		}    
	} else { // refresh values are passed and stored in data thermostatList
    
		data?.thermostatList=refreshValues.thermostatList
	}    
    
	def ecobeeType = determine_ecobee_type_or_location(tstatType)

	// determine if there is an event running
    
	Integer indiceEvent = 0    
	boolean foundEvent = false
	def foundEventClimateRef =null    
	if (data.thermostatList[0].events) {
		for (i in 0..data.thermostatList[0].events.size() - 1) {
			if (data.thermostatList[0].events[i].running) {
				indiceEvent = i // save the right indice associated to the Event that is currently running
				foundEvent = true
				foundEventClimateRef= (data.thermostatList[0].events[i].holdClimateRef !='')? data.thermostatList[0].events[i].holdClimateRef : null								                
				exit
			}
		}
	}

	def currentClimate = null
	def eventClimate = null
	// Get the current & event Climates 
	data.thermostatList[0].program.climates.each() {
		if ((foundEventClimateRef) && (it.climateRef == foundEventClimateRef)) {
			eventClimate = it
		}
		if (it.climateRef == data.thermostatList[0].program.currentClimateRef) {
			currentClimate = it
		}
	}

	def programScheduleName= (foundEventClimateRef)? eventClimate.name: 
		((foundEvent)? data.thermostatList[0].events[indiceEvent].name:currentClimate.name)
	def programType= (foundEvent)?data.thermostatList[0].events[indiceEvent].type : currentClimate.type
	def dataEvents = [
		'programScheduleName': programScheduleName,
		'programType': programType,
		'coolingSetpoint': (foundEvent)? data.thermostatList[0].events[indiceEvent].coolHoldTemp: 
			data.thermostatList[0].runtime.desiredCool,
		'coolingSetpointDisplay': (foundEvent)? data.thermostatList[0].events[indiceEvent].coolHoldTemp: 
			data.thermostatList[0].runtime.desiredCool,
		'heatingSetpoint': (foundEvent)? data.thermostatList[0].events[indiceEvent].heatHoldTemp: 
			data.thermostatList[0].runtime.desiredHeat,
		'heatingSetpointDisplay': (foundEvent)? data.thermostatList[0].events[indiceEvent].heatHoldTemp: 
			data.thermostatList[0].runtime.desiredHeat,
	]    
	generateEvent(dataEvents)
    
	def progDisplayName = getCurrentProgName()
	String currentClimateTemplate= (foundEventClimateRef)? eventClimate.name: currentClimate.name  // if no program's climate set, then use current program
	traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},Current Climate Ref=${data.thermostatList[0].program.currentClimateRef},currentClimateTemplate=${currentClimateTemplate}",
			settings.trace)            
       
	if (foundEvent) {
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},indiceEvent=${indiceEvent}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event name=${data.thermostatList[0].events[indiceEvent].name}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event type=${data.thermostatList[0].events[indiceEvent].type}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's coolHoldTemp=${data.thermostatList[0].events[indiceEvent].coolHoldTemp}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's heatHoldTemp=${data.thermostatList[0].events[indiceEvent].heatHoldTemp}",,settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's fan mode=${data.thermostatList[0].events[indiceEvent].fan}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's fanMinOnTime=${data.thermostatList[0].events[indiceEvent].fanMinOnTime}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's vent mode=${data.thermostatList[0].events[indiceEvent].vent}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's ventilatorMinOnTime=${data.thermostatList[0].events[indiceEvent].ventilatorMinOnTime}",
			settings.trace)            
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's running=${data.thermostatList[0].events[indiceEvent].running}",settings.trace)
		traceEvent(settings.logFilter,"refresh_thermostat>thermostatId = ${thermostatId},event's holdClimateRef =${foundEventClimateRef}",settings.trace)
	}
	def currentHeatingSetpointInF=(state.scale=='C')? cToF(device.currentValue("heatingSetpoint")): (device.currentValue("heatingSetpoint"))
	def currentCoolingSetpointInF=(state.scale=='C')? cToF(device.currentValue("coolingSetpoint")): (device.currentValue("coolingSetpoint"))
  	def medianPointInF  
  	if (data.thermostatList[0].settings.hvacMode=='auto') {  
	 	medianPointInF= ((currentHeatingSetpointInF + currentCoolingSetpointInF)/2).toFloat()
  	}      
	def alerts=getAlerts()
	dataEvents = [
 		thermostatId:data.thermostatList[0].identifier.toString(),
 		thermostatName:data.thermostatList[0].name,
		thermostatMode:data.thermostatList[0].settings.hvacMode,
		temperature: data.thermostatList[0].runtime.actualTemperature,
		temperatureDisplay: data.thermostatList[0].runtime.actualTemperature,
		humidity:data.thermostatList[0].runtime.actualHumidity,
		thermostatSetpoint:(data.thermostatList[0].settings.hvacMode =='cool'? (currentCoolingSetpointInF*10): // multiply by 10 before generateEvent
			(data.thermostatList[0].settings.hvacMode=='auto')? (medianPointInF*10): (currentHeatingSetpointInF*10)),
		modelNumber: data.thermostatList[0].modelNumber,
		equipmentStatus:getEquipmentStatus(),
		thermostatOperatingState: getThermostatOperatingState(),
		hasHumidifier:data.thermostatList[0].settings.hasHumidifier.toString(),
		hasDehumidifier:data.thermostatList[0].settings.hasDehumidifier.toString(),
		hasHrv:data.thermostatList[0].settings.hasHrv.toString(),
		hasErv:data.thermostatList[0].settings.hasErv.toString(),
		programEndTimeMsg: (foundEvent)? "${data.thermostatList[0].events[indiceEvent].type}" +
				" ends at\n${data.thermostatList[0].events[indiceEvent].endDate} " +
				"${data.thermostatList[0].events[indiceEvent].endTime.substring(0,5)}":
 				"No Events running",
		thermostatFanMode: (foundEvent)? data.thermostatList[0].events[indiceEvent].fan: 
			data.thermostatList[0].runtime.desiredFanMode,
		fanMinOnTime: (foundEvent)? data.thermostatList[0].events[indiceEvent].fanMinOnTime.toString() :
			data.thermostatList[0].settings.fanMinOnTime.toString(),
		programFanMode: (data.thermostatList[0].settings.hvacMode == 'cool')? currentClimate.coolFan : currentClimate.heatFan,
		programNameForUI: progDisplayName,
		weatherStation:data.thermostatList[0].weather.weatherStation,
		weatherSymbol:data.thermostatList[0].weather.forecasts[0].weatherSymbol.toString(),
		weatherTemperature:data.thermostatList[0].weather.forecasts[0].temperature,
		weatherTemperatureDisplay:data.thermostatList[0].weather.forecasts[0].temperature,
		weatherDateTime:"Weather as of ${data.thermostatList[0].weather.forecasts[0].dateTime.substring(0,16)}",
		weatherCondition:data.thermostatList[0].weather.forecasts[0].condition,
		weatherTemp: data.thermostatList[0].weather.forecasts[0].temperature,
		weatherTempDisplay: data.thermostatList[0].weather.forecasts[0].temperature,
		weatherTempHigh: data.thermostatList[0].weather.forecasts[0].tempHigh, 
		weatherTempLow: data.thermostatList[0].weather.forecasts[0].tempLow,
		weatherTempHighDisplay: data.thermostatList[0].weather.forecasts[0].tempHigh, 
		weatherTempLowDisplay: data.thermostatList[0].weather.forecasts[0].tempLow,
		weatherWindSpeed: (data.thermostatList[0].weather.forecasts[0].windSpeed/1000),		// divided by 1000 for display
		weatherPressure:data.thermostatList[0].weather.forecasts[0].pressure.toString(),
		weatherRelativeHumidity:data.thermostatList[0].weather.forecasts[0].relativeHumidity,
		weatherWindDirection:data.thermostatList[0].weather.forecasts[0].windDirection + " Winds",
		weatherPop:data.thermostatList[0].weather.forecasts[0].pop.toString(),        
		programCoolTemp:currentClimate.coolTemp,									
		programHeatTemp:currentClimate.heatTemp,
		programCoolTempDisplay:currentClimate.coolTemp,								
		programHeatTempDisplay:currentClimate.heatTemp,
		alerts: ((alerts)? alerts :'None'),
//		groups: (ecobeeType.toUpperCase() == 'REGISTERED')? getThermostatGroups(thermostatId) : 'No groups',
		climateList: getClimateList(),
		heatStages:data.thermostatList[0].settings.heatStages.toString(),
		coolStages:data.thermostatList[0].settings.coolStages.toString(),
		presence: (progDisplayName.toUpperCase().contains('AWAY'))? "not present":"present",
		climateName: currentClimate.name,
		setClimate: currentClimateTemplate
	]
          
	if (foundEvent && (data.thermostatList[0]?.events[indiceEvent]?.type.toUpperCase() == 'QUICKSAVE')) {
		dataEvents.programEndTimeMsg ="Quicksave running"
	}
    
	generateEvent(dataEvents)
	if (data.thermostatList[0].settings.hasHumidifier=='true') {
		dataEvents = [
			humidifierMode: data.thermostatList[0].settings.humidifierMode,
			humidifierLevel:data.thermostatList[0].settings.humidity
		]
		generateEvent(dataEvents)        
	}
	if (data.thermostatList[0].settings.hasDehumidifier=='true') {
		dataEvents = [
			dehumidifierMode: data.thermostatList[0].settings.dehumidifierMode,
			dehumidifierLevel:data.thermostatList[0].settings.dehumidifierLevel
		]            
		generateEvent(dataEvents)                    
	}
	if ((data.thermostatList[0].settings.hasHrv== 'true') || (data.thermostatList[0].settings.hasErv=='true')) {
		dataEvents = [
			ventilatorMinOnTime:data.thermostatList[0].settings.ventilatorMinOnTime.toString(),
			ventilatorMode: data.thermostatList[0].settings.vent
		]            
		generateEvent(dataEvents)                    
	}
	int heatStages=device.currentValue("heatStages")?.toInteger()        
	int coolStages=device.currentValue("coolStages")?.toInteger()        
	if (heatStages>1) {
		dataEvents = [
			auxMaxOutdoorTemp:data.thermostatList[0].settings.auxMaxOutdoorTemp,
			stage1HeatingDifferentialTemp:data.thermostatList[0].settings.stage1HeatingDifferentialTemp,   
			stage1HeatingDissipationTime:data.thermostatList[0].settings.stage1HeatingDissipationTime.toString()
		]            
		generateEvent(dataEvents)                    
	}
	if (coolStages>1) {
		dataEvents = [
			stage1CoolingDifferentialTemp:data.thermostatList[0].settings.stage1CoolingDifferentialTemp,        
			stage1CoolingDissipationTime:data.thermostatList[0].settings.stage1CoolingDissipationTime.toString()
		]            
		generateEvent(dataEvents)                    
	}
	updateCurrentTileValue() 
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"),
		true)   
	traceEvent(settings.logFilter,"poll>done for thermostatId =${thermostatId}", settings.trace)
}

// refresh() has a different polling interval as it is called by the UI (contrary to poll).
void refresh() {
	Date endDate= new Date()
	Date startDate = endDate -1    
	def thermostatId= determine_tstat_id("") 	    
	def poll_interval=0.25   // set a 15 sec. poll interval to avoid unecessary load on ecobee servers
	def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
	if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
		traceEvent(settings.logFilter,"refresh>thermostatId = ${thermostatId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), not refreshing data...",
			settings.trace)	            
		return
	}
	state.lastPollTimestamp = now()
	refresh_thermostat(thermostatId)
  
}

void poll() {
	String URI_ROOT = "${get_URI_ROOT()}/1"
    
	def thermostatId= determine_tstat_id("") 	    

	def poll_interval=1   // set a minimum of 1 min. poll interval to avoid unecessary load on ecobee servers
	def time_check_for_poll = (now() - (poll_interval * 60 * 1000))
	if ((state?.lastPollTimestamp) && (state?.lastPollTimestamp > time_check_for_poll)) {
		traceEvent(settings.logFilter,"poll>thermostatId = ${thermostatId},time_check_for_poll (${time_check_for_poll} < state.lastPollTimestamp (${state.lastPollTimestamp}), not refreshing data...",
			settings.trace, get_LOG_INFO())            
		return
	}
	if (!thermostat_revision_changed("","")) {
    
		// if there are no changes in the thermostat, runtime or interval revisions, stop the polling as values at ecobee haven't changed since last poll()
		return
	}
    
	if (isTokenExpired()) {
		traceEvent(settings.logFilter,"poll>need to refresh tokens", settings.trace, get_LOG_WARN())
       
		if (!refresh_tokens()) {
			traceEvent(settings.logFilter,"poll>$exceptionCheck, not able to renew the refresh token", settings.trace, get_LOG_ERROR())         
		} else {
        
			// Reset Exceptions counter as the refresh_tokens() call has been successful 
			state?.exceptionCount=0
		}            
        
	}
    
	def bodyReq = build_body_request('thermostatInfo',null,thermostatId,null)
	traceEvent(settings.logFilter,"poll>about to call pollAsyncResponse with body = ${bodyReq} for thermostatId = ${thermostatId}...", settings.trace)
	    
	def args_encoded = java.net.URLEncoder.encode(bodyReq.toString(), "UTF-8")
	def params = [
		uri: "${URI_ROOT}/thermostat?format=json&body=${args_encoded}",
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type': "application/json",
			'charset': "UTF-8",
			'Accept': "application/json"
		]
	]
	asynchttp_v1.get('pollAsyncResponse', params)

}

def pollAsyncResponse(response, data) {	
	def TOKEN_EXPIRED=401
  
	if (response.hasError()) {
		traceEvent(settings.logFilter,"pollAsyncResponse>ecobee response error: $response.errorMessage", true, get_LOG_ERROR())
		state?.exceptionCount=state?.exceptionCount +1        
	}        
	if (response?.status == TOKEN_EXPIRED) { // token is expired
		traceEvent(settings.logFilter,"pollAsyncResponse>ecobee's Access token has expired, trying to refresh tokens now...", settings.trace, get_LOG_WARN())
		refresh_tokens()      
	} else {
		try {
			// json response already parsed into JSONElement object
			def responseValues = response.json    
			if (responseValues) {
           
				def thermostatId = responseValues?.thermostatList[0].identifier       
                
				if (settings.trace) {
					def thermostatName = responseValues?.thermostatList[0]?.name           
					traceEvent(settings.logFilter,
						"pollAsyncResponse> thermostatId=${thermostatId},name=${thermostatName},hvacMode=${responseValues?.thermostatList[0]?.settings?.hvacMode}," +
						"fan=${responseValues?.thermostatList[0]?.runtime?.desiredFanMode},desiredHeat=${responseValues?.thermostatList[0]?.runtime?.desiredHeat}," +
						"desiredCool=${responseValues?.thermostatList[0]?.runtime?.desiredCool}", settings.trace)
				}
                
				refresh_thermostat(thermostatId, responseValues) 
				state.lastPollTimestamp = now()
				state?.exceptionCount=0                 
			       
			}                
                
		} catch (e) {
			traceEvent(settings.logFilter,"pollAsyncResponse>ecobee - error parsing json from response: $e", settings.trace, get_LOG_ERROR())
			return            
		}
		retrieveDataForGraph() 
	}        
}    

private void generateEvent(Map results) {
	traceEvent(settings.logFilter,"generateEvent>parsing data $results", settings.trace)
    
	state?.scale = getTemperatureScale() // make sure to display in the right scale
	def scale = state?.scale
    
	if (results) {
		results.each { name, value ->
			def isDisplayed = true

// 			Temperature variable names for display contain 'display'            

			if (name.toUpperCase().contains("DISPLAY")) {  

				String tempValueString 
				double tempValue 
				if (scale == "F") {
					tempValue = getTemperature(value).toDouble().round()
					tempValueString = String.format('%2d', tempValue.intValue())            
				} else {
					tempValue = getTemperature(value).toDouble().round(1)
					tempValueString = String.format('%2.1f', tempValue)
				}
				def isChange = isTemperatureStateChange(device, name, tempValueString)
                
				isDisplayed = isChange
				sendEvent(name: name, value: tempValueString, unit: scale, displayed: isDisplayed)                                     									 

// 			Temperature variable names contain 'temp' or 'setpoint' (not for display)           

			} else if ((name.toUpperCase().contains("TEMP")) || (name.toUpperCase().contains("SETPOINT"))) {  
                                
				double tempValue = getTemperature(value).round(1)
				String tempValueString = String.format('%2.1f', tempValue)
				def isChange = isTemperatureStateChange(device, name, tempValueString)
                
				isDisplayed = isChange
				sendEvent(name: name, value: tempValueString, unit: scale, displayed: isDisplayed)                                     									 
			} else if (name.toUpperCase().contains("SPEED")) {

// 			Speed variable names contain 'speed'

 				double speedValue = getSpeed(value).round(1)
				def isChange = isStateChange(device, name, speedValue.toString())
				isDisplayed = isChange
				sendEvent(name: name, value: speedValue.toString(), unit: getDistanceScale(), displayed: isDisplayed)                                     									 
			} else if ((name.toUpperCase().contains("HUMIDITY")) || (name.toUpperCase().contains("LEVEL"))) {
 				double humValue = value.toDouble().round(0)
				String humValueString = String.format('%2d', humValue.intValue())
				def isChange = isStateChange(device, name, humValueString)
				isDisplayed = isChange
				sendEvent(name: name, value: humValueString, unit: "%", displayed: isDisplayed)                                     									 
			} else if (name.toUpperCase().contains("DATA")) { // data variable names contain 'data'

				sendEvent(name: name, value: value, displayed: (settings.trace?:false))                                     									 

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
	def CUSTOM_PROG = 'Other'
	def QUICKSAVE = 'Save'

	def progCurrentName = device.currentValue("programScheduleName")
	def progType = device.currentValue("programType")
	progType = (progType == null) ? "": progType.trim().toUpperCase()	
	progCurrentName = (progCurrentName == null) ? "": progCurrentName.trim()
	if ((progCurrentName != AWAY_PROG) && (progCurrentName != SLEEP_PROG) && (
		progCurrentName != AWAKE_PROG) &&
		(progCurrentName != HOME_PROG) && (progCurrentName != QUICKSAVE)) {
		progCurrentName = (progType == 'VACATION') ? AWAY_PROG : CUSTOM_PROG
	}
	return progCurrentName
}

private def getAlerts() {
	
	def alerts = ""
	if (data.thermostatList[0].alerts.size() > 0) {
		for (i in 0..data.thermostatList[0].alerts.size() - 1) {
			if (!alerts.contains(data.thermostatList[0].alerts[i].notificationType)) {     
				// only add different alerts
				alerts =  alerts  + data.thermostatList[0].alerts[i].notificationType + ','

			}                
		}
	}	
	// Remove last comma
	if (alerts) {    
		alerts = alerts.substring(0,(alerts.length()-1))
	}        
	return alerts
}

void getAlertText(alertType) {
	if (data.thermostatList[0].alerts.size() > 0) {
		for (i in 0..data.thermostatList[0].alerts.size() - 1) {
			if (data.thermostatList[0].alerts[i].notificationType== alertType) {
				traceEvent(settings.logFilter,"getAlertText>found alertType=${data.thermostatList[0].alerts[i].notificationType}", settings.trace)	
				sendEvent (name: "alertText", value:data.thermostatList[0].alerts[i].text, displayed: settings.trace)
				return                
			}                
		}
	}
	sendEvent (name: "alertText", value: "", displayed: settings.trace ) 
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
	double farenheits = value.toDouble()/10  // divide by 10 before display
	if (state?.scale == "C") {
		return fToC(farenheits)
	} else {
		return farenheits
	}
}

private def getSpeed(value) {
	double miles = value
	if (state?.scale == "F"){
		return miles
	} else {
		return milesToKm(miles)
	}
}

private def getDistanceScale() {
	def scale= state?.scale
	if (scale == 'C') {
		return "kmh"
	}
	return "mph"
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

	def equipStatus = getEquipmentStatus()
	
	equipStatus = equipStatus.trim().toUpperCase()
    
	def currentOpState = equipStatus.contains('HEAT')? 'heating' : (equipStatus.contains('COOL')? 'cooling' : 
    	equipStatus.contains('FAN')? 'fan only': 'idle')
	return currentOpState
}

// thermostatId may only be a specific thermostatId or "" (for current thermostat)
// To be called after a poll() or refresh() to have the latest status
private def getClimateList(thermostatId) {
	def climateList=""
    
	for (i in 0..data.thermostatList[0].program.climates.size() - 1) {
		climateList = data.thermostatList[0].program.climates[i].name  + "," + climateList
	}
	traceEvent(settings.logFilter,"getClimateList>climateList=${climateList}", settings.trace)
	return climateList
}


void resumeThisTstat() {
	def thermostatId= determine_tstat_id("") 	    
	resumeProgram() 
	refresh_thermostat(thermostatId)
}

private void api(method, args, success = {}) {
	def MAX_EXCEPTION_COUNT=5
	String URI_ROOT = "${get_URI_ROOT()}/1"
    
	if (!isLoggedIn()) {
		login()
		
	}   
    
	if (isTokenExpired()) {
		traceEvent(settings.logFilter,"api>need to refresh tokens",settings.trace)
       
		if (!refresh_tokens()) {
			if ((exceptionCheck) && (state.exceptionCount >= MAX_EXCEPTION_COUNT) && (exceptionCheck.contains("Unauthorized"))) {
				traceEvent(settings.logFilter,"api>$exceptionCheck, not able to renew the refresh token;need to re-login to ecobee via MyEcobeeInit....", true, get_LOG_ERROR())         
			}
		} else {
        
			// Reset Exceptions counter as the refresh_tokens() call has been successful 
			state.exceptionCount=0
		}            
        
	}
    
	def args_encoded = java.net.URLEncoder.encode(args.toString(), "UTF-8")
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
			[uri: "${URI_ROOT}/thermostat?format=json", type: 'post'],
		'runtimeReport': 
			[uri:"${URI_ROOT}/runtimeReport?format=json&body=${args_encoded}", 
          		type: 'get'],
		]
	def request = methods.getAt(method)
	traceEvent(settings.logFilter,"api> about to call doRequest with (unencoded) args = ${args}", settings.trace)
	doRequest(request.uri, args_encoded, request.type, success)
	if (state.exceptionCount >= MAX_EXCEPTION_COUNT) {
		def exceptionCheck=device.currentValue("verboseTrace")
		traceEvent(settings.logFilter,"api>error: found a high number of exceptions (${state.exceptionCount}), last exceptionCheck=${exceptionCheck}, about to reset counter",
			settings.trace, get_LOG_ERROR())  
		sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		if (!exceptionCheck.contains("Unauthorized")) {          
			state.exceptionCount = 0  // reset the counter as long it's not unauthorized exception
		}            
	}        

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
		]
	]
	traceEvent(settings.logFilter,"doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}", settings.trace)
	try {
		traceEvent(settings.logFilter,"doRequest>about to ${type} with uri ${params.uri}, (encoded)args= ${args}",settings.trace)
		if (type == 'post') {
			params?.body = args
			httpPostJson(params, success)

		} else if (type == 'get') {
			httpGet(params, success)
		}
		/* when success, reset the exception counter */
		state.exceptionCount=0
		traceEvent(settings.logFilter,"doRequest>done with ${type}", settings.trace)

	} catch (java.net.UnknownHostException e) {
		traceEvent(settings.logFilter,"doRequest> Unknown host ${params.uri}", settings.trace, get_LOG_ERROR())
	} catch (java.net.NoRouteToHostException e) {
		traceEvent(settings.logFilter,"doRequest>No route to host - check the URL ${params.uri} ", settings.trace, get_LOG_ERROR())
	} 
}


// tstatType =managementSet or registered (no spaces).  
//		registered is for SMART & SMART-SI thermostats, 
//		managementSet is for EMS thermostat
//		may also be set to a specific locationSet (ex. /Toronto/Campus/BuildingA)
//		may be set to null if not relevant for the given method
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
private def build_body_request(method, tstatType="registered", thermostatId, tstatParams = [],
	tstatSettings = [], includeSensor=false) {
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
		selectionJson = new groovy.json.JsonBuilder(selection)
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
			includeEquipmentStatus: 'true',
			includeSensors: 'true'
			]
		]
		selectionJson = new groovy.json.JsonBuilder(selection)
		return selectionJson
	} else if (method == 'remoteSensorUpdate') {
		selection = [selection: [selectionType: 'thermostats',
			selectionMatch: thermostatId,
			includeSensors: 'true'
			]
		]
		selectionJson = new groovy.json.JsonBuilder(selection)
		return selectionJson
	} else {
		selection = [selectionType: 'thermostats', selectionMatch: thermostatId]
	}
	selectionJson = new groovy.json.JsonBuilder(selection)
	if ((method != 'setThermostatSettings') && (tstatSettings != null) && (tstatSettings != [])) {
		def function_clause = ((tstatParams != null) && (tsatParams != [])) ? 
			[type:method, params: tstatParams] : 
			[type: method]
		def bodyWithSettings = [functions: [function_clause], selection: selection,
				thermostat: [settings: tstatSettings]
			]
		def bodyWithSettingsJson = new groovy.json.JsonBuilder(bodyWithSettings)
		return bodyWithSettingsJson
	} else if (method == 'setThermostatSettings') {
		def bodyWithSettings = [selection: selection,thermostat: [settings: tstatSettings]
			]
		def bodyWithSettingsJson = new groovy.json.JsonBuilder(bodyWithSettings)
		return bodyWithSettingsJson
	} else if ((tstatParams != null) && (tsatParams != [])) {
		def function_clause = [type: method, params: tstatParams]
		def simpleBody = [functions: [function_clause], selection: selection]
		def simpleBodyJson = new groovy.json.JsonBuilder(simpleBody)
		return simpleBodyJson
	} else {
		def function_clause = [type: method]
		def simpleBody = [functions: [function_clause], selection: selection]
		def simpleBodyJson = new groovy.json.JsonBuilder(simpleBody)
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
	traceEvent(settings.logFilter,"iterateSetThermostatSettings>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)",settings.trace)
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
				traceEvent(settings.logFilter,"iterateSetThermostatSettings>about to call setThermostatSettings for ${tstatlist}", settings.trace)
				setThermostatSettings("${tstatlist}",tstatSettings)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
void setThermostatSettings(thermostatId,tstatSettings = []) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
   	thermostatId= determine_tstat_id(thermostatId) 	    
	traceEvent(settings.logFilter,"setThermostatSettings>called with values ${tstatSettings} for ${thermostatId}",settings.trace)
	def bodyReq = build_body_request('setThermostatSettings',null,thermostatId,null,tstatSettings)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('setThermostatSettings', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"setThermostatSettings>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()", settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"setThermostatSettings>done for ${thermostatId}", settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"setThermostatSettings> error=${statusCode.toString()},message=${message} for ${thermostatId}", true, get_LOG_ERROR())
			} /* end if statusCode */
		} /* end api call */                
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"setThermostatSettings>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
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
	traceEvent(settings.logFilter,"iterateSetHold>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)",settings.trace)

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
				traceEvent(settings.logFilter,"iterateSetHold>about to call setHold for ${tstatlist}", settings.trace)
				setHoldExtraParams("${tstatlist}", coolingSetPoint, heatingSetPoint, fanMode,
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
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// extraHoldParams may be any other sethold or events properties  
//		see https://www.ecobee.com/home/developer/api/documentation/v1/objects/Event.shtml
void setHoldExtraParams(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = [], extraHoldParams=[]) {    
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
	def holdType    
    
	Integer targetCoolTemp = null,targetHeatTemp = null
	def tstatParams = null
	thermostatId = determine_tstat_id(thermostatId)
	traceEvent(settings.logFilter,"sethold>called with values ${coolingSetPoint}, ${heatingSetPoint}, ${fanMode}, ${tstatSettings} for ${thermostatId}", settings.trace)
	def scale = state?.scale
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
    
		if (settings.holdType.trim().toUpperCase()=="NEXTTRANSITION") { // to avoid any issue with the wrong case in the preference parameter.
			holdType="nextTransition"			        
		} else { // the other values (dateTime and holdHours) are not supported as they require a lot of parameters to work.
			holdType="indefinite"			        
		}        
		tstatParams = ((fanMode != null) & (fanMode != "")) ? 
          		[coolHoldTemp:targetCoolTemp, heatHoldTemp: targetHeatTemp, fan: fanMode, 
             			holdType:"${holdType}"
             		] : 
        		[coolHoldTemp: targetCoolTemp, heatHoldTemp: targetHeatTemp, 
             			holdType:"${holdType}"
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
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('setHold', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"setHold>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",
					settings.trace, get_LOG_WARN())                
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"setHold>done for ${thermostatId}",settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				log.error 
					"setHold> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				traceEvent(settings.logFilter,"setHold>> error=${statusCode.toString()},message=${message} for ${thermostatId}",true, get_LOG_ERROR())
			} /* end if statusCode */
		} /* end api call */
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"setHold>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
        
	}/* end while */
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
	traceEvent(settings.logFilter,"iterateCreateVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)",settings.trace)
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
				traceEvent(settings.logFilter,"iterateCreateVacation>about to call createVacation for ${tstatlist}", settings.trace)
				createVacation("${tstatlist}", vacationName, targetCoolTemp, targetHeatTemp,
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

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void createVacation(thermostatId, vacationName, targetCoolTemp, targetHeatTemp,
	targetStartDateTime, targetEndDateTime) {    
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    
	thermostatId = determine_tstat_id(thermostatId)
	def vacationStartDate = String.format('%tY-%<tm-%<td', targetStartDateTime)
	def vacationStartTime = String.format('%tH:%<tM:%<tS', targetStartDateTime)
	def vacationEndDate = String.format('%tY-%<tm-%<td', targetEndDateTime)
	def vacationEndTime = String.format('%tH:%<tM:%<tS', targetEndDateTime)
	Integer targetCool = null,targetHeat = null

	def scale = state?.scale
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
	traceEvent(settings.logFilter,"createVacation> about to call api with body = ${bodyReq} for ${thermostatId} ", settings.trace)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('createVacation', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"createVacation>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",
					settings.trace, get_LOG_WARN())                
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"createVacation>done for ${thermostatId}", settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"createVacation>error=${statusCode.toString()},message=${message} for ${thermostatId}", true, get_LOG_ERROR())
			}
		} /* end api */            
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"createVacation>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
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
	traceEvent(settings.logFilter,"iterateDeleteVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)", settings.trace)
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
				traceEvent(settings.logFilter,"iterateDeleteVacation> about to call deleteVacation for ${tstatlist}", settings.trace)
				deleteVacation("${tstatlist}", vacationName)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void deleteVacation(thermostatId, vacationName) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
 
  
	thermostatId = determine_tstat_id(thermostatId)
	def vacationParams = [name: vacationName.trim()]
	def bodyReq = build_body_request('deleteVacation',null,thermostatId,vacationParams)
    
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('deleteVacation', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"deleteVacation>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()", settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"deleteVacation>done for ${thermostatId}", settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"deleteVacation>error ${statusCode.toString()},message=${message} for ${thermostatId}", true, get_LOG_ERROR())
			}                    
		} /* end api */
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"deleteVacation>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	}  /* end while */
}

// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// iterateResumeProgram: iterate thru all the thermostats under a specific account and resume their program
void iterateResumeProgram(tstatType) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	traceEvent(settings.logFilter,"iterateResumeProgram>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)", settings.trace)
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
				traceEvent(settings.logFilter,"iterateResumeProgram> about to call resumeProgram for ${tstatlist}", settings.trace)
				resumeProgram("${tstatlist}")
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// resumeAllFlag, if true then a resume all will be sent, otherwise, just a partial resume will be done.
void resumeProgram(thermostatId=settings.thermostatId, resumeAllFlag=true) {  
	thermostatId = determine_tstat_id(thermostatId)
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    

	def resumeParams=null
    
 	if (resumeAllFlag==true) {
		resumeParams = [resumeAll: 'true']
	}
    
	def bodyReq = build_body_request('resumeProgram',null,thermostatId,resumeParams)
	traceEvent(settings.logFilter,"resumeProgram> about to call api with body = ${bodyReq} for ${thermostatId}", settings.trace)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('resumeProgram', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"resumeProgram>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",settings.trace,
					get_LOG_WARN())                    
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"resumeProgram>resume done for ${thermostatId}", settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"resumeProgram>error=${statusCode.toString()},message=${message} for ${thermostatId}",true, get_LOG_ERROR())
			}                    
		} /* end api */
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"resumeProgram>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// Get all groups related to a thermostatId or all groups
// thermostatId may only be 1 thermostat (not a list) or null (for all groups)
void getGroups(thermostatId) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    
	thermostatId = determine_tstat_id(thermostatId)
	def ecobeeType = determine_ecobee_type_or_location("")
	traceEvent(settings.logFilter,"getGroups>ecobee Type = $ecobeeType for thermostatId = $thermostatId",settings.trace)
	if (ecobeeType.toUpperCase() != 'REGISTERED') {
		traceEvent(settings.logFilter,"getGroups>managementSet is not a valid settings.ecobeeType for getGroups",settings.trace, get_LOG_WARN())
		data.groups = null
		return
	}
	def bodyReq = '{"selection":{"selectionType":"registered"}}'
	api('getGroups', bodyReq) {resp ->
		statusCode = resp?.data?.status?.code
		def message = resp?.data?.status?.message
		if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
			traceEvent(settings.logFilter,"getGroups>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()", settings.trace, get_LOG_WARN())
			refresh_tokens()     
		}
		def exceptionCheck=device.currentValue("verboseTrace")
		if (exceptionCheck.contains("exception")) {
			statusCode=(statusCode)?statusCode:3
		}
		if (!statusCode) {
			/* when success, reset the exception counter */
			state.exceptionCount=0
			data.groups = resp.data.groups
			traceEvent(settings.logFilter,"getGroups>done for ${thermostatId},group size = ${data.groups.size()}, groups data = ${data.groups}", settings.trace)
			if (data.groups.size() == 0) {
				return
			}
			if ((thermostatId != null) && (thermostatId != "")) {
				if (data.groups.thermostats.size() > 0) {
					for (i in 0..data.groups.size() - 1) {
						def foundTstat = false
						for (j in 0..data.groups[i].thermostats.size() - 1) {
							if (data.groups[i].thermostats[j] == thermostatId) {
								traceEvent(settings.logFilter,"getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}", settings.trace)
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
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"getGroups>error ${statusCode.toString()},message=${message} for ${thermostatId}", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// iterateUpdateGroup: iterate thru all the Groups under a specific account and update their settings
// Get all groups related to a thermostatId and update them with the groupSettings
// thermostatId may only be 1 thermostat (not a list), if null or empty, then defaulted to this thermostatId (settings)
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// groupSettings may be a map of settings separated by ",", no spaces; 
// 	For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void iterateUpdateGroup(thermostatId, groupSettings = []) {
	thermostatId = determine_tstat_id(thermostatId)
	getGroups(thermostatId)
	traceEvent(settings.logFilter,"iterateUpdateGroup> about to loop ${data.groups.size()}", settings.trace)
	if (data.groups.size() == 0) {
		return
	}
	for (i in 0..data.groups.size() - 1) {
		def groupName = data.groups[i].groupName
		def groupRef = data.groups[i].groupRef
		def synchronizeSchedule = data.groups[i].synchronizeSchedule
		def synchronizeVacation = data.groups[i].synchronizeVacation
		def synchronizeSystemMode = data.groups[i].synchronizeSystemMode
		traceEvent(settings.logFilter,"iterateUpdateGroup> about to call updateGroup for ${groupName}, groupRef= ${groupRef}," +
				"synchronizeSystemMode=${synchronizeSystemMode}, synchronizeVacation=${synchronizeVacation}" +
				"synchronizeSchedule=${synchronizeSchedule}", settings.trace)
		updateGroup(groupRef, groupName, thermostatId, groupSettings)
		traceEvent(settings.logFilter,"iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}",settings.trace)
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// If groupRef is not provided, it is assumed that a group creation needs to be done
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// groupSettings could be a map of settings separated by ",", no spaces; 
//	For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void updateGroup(groupRef, groupName, thermostatId, groupSettings = []) {
	String updateGroupParams
	def groupSettingsJson = new groovy.json.JsonBuilder(groupSettings)
	def groupSet = groupSettingsJson.toString().minus('{').minus('}')
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
 

	thermostatId = determine_tstat_id(thermostatId)
	traceEvent(settings.logFilter,"updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}", settings.trace)
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
	traceEvent(settings.logFilter,"updateGroup> about to call api with body = ${bodyReq} for groupName =${groupName},thermostatId = ${thermostatId}...", settings.trace)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck
		api('updateGroup', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"updateGroup>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()", settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"updateGroup>done for groupName =${groupName}, ${thermostatId}", settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"updateGroup>error ${statusCode.toString()},message=${message} for ${thermostatId}", true, get_LOG_ERROR())
			} /* end if statusCode */
		} /* end api call */                
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"updateGroup>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void deleteGroup(groupRef, groupName) {
	String updateGroupParams
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    
	if ((groupRef != null) && (groupRef.trim() != "")) {
		updateGroupParams = '"groupRef":"' + groupRef.trim() + '","groupName":"' +
			groupName.trim() + '"'
	} else {
		updateGroupParams = '"groupName":"' + groupName.trim() + '"'
	}
	traceEvent(settings.logFilter,"deleteGroup> updateGroupParams=${updateGroupParams}",settings.trace)
	def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' +
		updateGroupParams + '}]}'
	traceEvent(settings.logFilter,"deleteGroup> about to call api with body = ${bodyReq} for groupName =${groupName}, groupRef = ${groupRef}", settings.trace)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck
		api('updateGroup', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"deleteGroup>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"deleteGroup>done for groupName =${groupName},groupRef = ${groupRef}", settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"deteteGroup>error ${statusCode.toString()},message= ${message} for ${groupName},groupRef= ${groupRef}", true, get_LOG_ERROR())
			}                    
		} /* end api */
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"deleteGroup>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
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
	traceEvent(settings.logFilter,"iterateSetClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)", settings.trace)
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
				setClimate("${tstatlist}", climateName, sensors)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
//	The settings.thermostatId (input) is the default one
// climate name is the name of the climate to be set to (ex. "Home", "Away").
// paramsMap: parameters Map, ex. [holdType:'nextTransition']  
//	see https://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml 

void setClimate(thermostatId, climateName, paramsMap=[]) {
	def climateRef = null
	def tstatParams
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    
    
	if (thermostatId) {
//		call getThermostatInfo if a value for thermostatId is provided to make sure to have the right thermostat information

		getThermostatInfo(thermostatId) 
	}   
	thermostatId = determine_tstat_id(thermostatId)
	for (i in 0..data.thermostatList.size() - 1) {
		def foundClimate = data.thermostatList[i].program.climates.find{ it.name.toUpperCase() == climateName.toUpperCase() }
		if (foundClimate) {
			climateRef = foundClimate.climateRef
			traceEvent(settings.logFilter,"setClimate>Climate ${climateRef} found for thermostatId =${data.thermostatList[i].identifier}",settings.trace)
		} else {
			traceEvent(settings.logFilter,"setClimate>Climate ${climateName} not found for thermostatId =${data.thermostatList[i].identifier}",settings.trace, get_LOG_WARN())
			continue
		}
        
		tstatParams = ((settings.holdType != null) && (settings.holdType.trim() != "")) ?
				[holdClimateRef:"${climateRef}", holdType:"${settings.holdType.trim()}"
				] :
				[holdClimateRef:"${climateRef}"
				]
           	
		tstatParams = tstatParams + paramsMap            
		def bodyReq = build_body_request('setHold',null, data.thermostatList[i].identifier,tstatParams)	
		int statusCode=1
		int j=0        
		while ((statusCode) && (j++ <2)) { // retries once if api call fails
			def exceptionCheck
			api('setHold', bodyReq) {resp ->
				statusCode = resp?.data?.status?.code
				def message = resp?.data?.status?.message
				if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
					traceEvent(settings.logFilter,"setClimate>thermostatId=${data.thermostatList[i].identifier},error $statusCode, need to call refresh_tokens()",settings.trace, get_LOG_WARN())
					refresh_tokens()     
				}
				exceptionCheck=device.currentValue("verboseTrace")
				if (!statusCode) {
					/* when success, reset the exception counter */
					state.exceptionCount=0
					/* Post the setClimate value */    
					sendEvent(name: 'setClimate', value: climateName, isStateChange:true)
					traceEvent(settings.logFilter,"setClimate>done for thermostatId =${data.thermostatList[i].identifier},climateName =${climateName}",settings.trace)
				} else {
					state.exceptionCount = state.exceptionCount +1     
					traceEvent(settings.logFilter,"setClimate>error ${statusCode.toString()},message=${message} while setting climate ${climateName} for thermostatId =${data.thermostatList[i].identifier}",
						true,  get_LOG_ERROR())
				} /* end if statusCode */
			} /* end api call */                   
			if (exceptionCheck?.contains("exception")) {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"setClimate>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
				sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
				statusCode=(statusCode)?:3 //internal error            
			}                
		} /* end while */               
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
	traceEvent(settings.logFilter,"iterateUpdateClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)",settings.trace)
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			traceEvent(settings.logFilter,"iterateUpdateClimate> about to call updateClimate for thermostatId =${id}",settings.trace)
			updateClimate(Id, climateName, deleteClimateFlag, subClimateName, coolTemp,
				heatTemp, isOptimized, isOccupied, coolFan, heatFan)
		}
	}
}

// thermostatId may only be 1 thermostat (not a list) 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// climate name is the name of the climate to be updated (ex. "Home", "Away").
// deleteClimateFlag is set to 'true' if the climate needs to be deleted (should not be part of any schedule beforehand)
// substituteClimateName is the climateName that will replace the original climateName in the schedule (can be null when not needed)
// isOptimized is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void updateClimate(thermostatId, climateName, deleteClimateFlag,
		substituteClimateName, coolTemp, heatTemp, isOptimized, isOccupied, coolFan, heatFan) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	Integer targetCoolTemp,targetHeatTemp
	def foundClimate = null, foundSubstituteClimate =null
	String scheduleAsString
	def substituteClimateRef = null
	def climateRefToBeReplaced = null

	if (thermostatId) {
//		call getThermostatInfo if a value for thermostatId is provided to make sure to have the right thermostat information

		getThermostatInfo(thermostatId) 
	}        
	thermostatId = determine_tstat_id(thermostatId)
	if ((coolTemp != null) && (heatTemp != null)) {
		traceEvent(settings.logFilter,"updateClimate>thermostatId =${thermostatId} coolTemp=${coolTemp}, heatTemp= ${heatTemp}",settings.trace)
		def scale = state?.scale
		if (scale == 'C') {
			targetCoolTemp = (cToF(coolTemp) * 10) as Integer // need to send temperature in F multiply by 10
			targetHeatTemp = (cToF(heatTemp) * 10) as Integer
		} else {
			targetCoolTemp = (coolTemp * 10) as Integer // need to send temperature in F multiply by 10
			targetHeatTemp = (heatTemp * 10) as Integer
		}
	}        
	if ((substituteClimateName != null) && (substituteClimateName != "")) { // find the subsituteClimateRef for the subsitute Climate Name if not null

		foundClimate = data.thermostatList[0].program.climates.find{ it.name.toUpperCase() == climateName.toUpperCase() }
		foundSubstituteClimate = data.thermostatList[0].program.climates.find { it.name.toUpperCase() == substituteClimateName.toUpperCase() }
		if (foundClimate) {
			climateRefToBeReplaced = foundClimate.climateRef       
			traceEvent(settings.logFilter,"updateClimate>climateRef ${climateRefToBeReplaced} found for climateName ${climateName}",settings.trace)
		} else {
			traceEvent(settings.logFilter,"updateClimate>climateName ${substituteClimateName} for substitution not found",settings.trace, get_LOG_WARN())
			return
		}
		if (foundSubstituteClimate) {
 			substituteClimateRef = foundSubstituteClimate.climateRef       
			traceEvent(settings.logFilter,"updateClimate>substituteClimateRef ${substituteClimateRef} found for ${substituteClimateName} ",settings.trace)
		} else {
			traceEvent(settings.logFilter,"updateClimate>substituteClimateName ${substituteClimateName} for substitution not found",settings.trace, get_LOG_WARN())
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
			traceEvent(settings.logFilter,"updateClimate>thermostatId =${thermostatId}, deleteClimateFlag=${deleteClimateFlag},Climate ${climateName} to be deleted...", settings.trace)
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
		traceEvent(settings.logFilter,"updateClimate>thermostatId =${thermostatId},Climate ${climateName} to be created",settings.trace)

		bodyReq = bodyReq + ',{"name":"' + climateName.capitalize().trim() +
			'","coolTemp":"' + targetCoolTemp +
			'","heatTemp":"' + targetHeatTemp + '","isOptimized":"' + isOptimized +
			'","isOccupied":"' + isOccupied  + '","coolFan":"' + coolFan + '","heatFan":"' + heatFan + '"' +
			',"ventilatorMinOnTime":"5"}' // workaround due to ecobee create Climate bug, to be removed
	}
	bodyReq = bodyReq + ']}}}'

	String URI_ROOT = "${get_URI_ROOT()}/1"
	def args_encoded = java.net.URLEncoder.encode(bodyReq.toString(), "UTF-8")

	def params = [
		uri: "${URI_ROOT}/thermostat?format=json",
		headers: [
			'Authorization': "${data.auth.token_type} ${data.auth.access_token}",
			'Content-Type': "application/json",
			'charset': "UTF-8",
			'Accept': "application/json"
		],
		body: args_encoded
	]

	asynchttp_v1.post('updateClimateResponse', params)
	traceEvent(settings.logFilter,"updateClimate>Async command sent", settings.trace)	
}


def updateClimateResponse(response, data) {
def TOKEN_EXPIRED=401
	if (response.hasError()) {
		traceEvent(settings.logFilter,"updateClimateResponse>ecobee response error: $response.errorMessage", true, get_LOG_ERROR())
		state?.exceptionCount=state?.exceptionCount +1        
	}        
	if (response?.status == TOKEN_EXPIRED) { // token is expired
		traceEvent(settings.logFilter,"updateClimateResponse>ecobee's Access token has expired, trying to refresh tokens now...", settings.trace, get_LOG_WARN())
		refresh_tokens()      
	} else {
		if (response.status == 200) {
			traceEvent(settings.logFilter,"updateClimateResponse> - Command sent successfully",settings.trace)
		} else {
			traceEvent(settings.logFilter,"updateClimateResponse> - Command failed, Error: $response.status",settings.trace,get_LOG_ERROR())
			state?.exceptionCount=state?.exceptionCount +1        
		}
	}
}

// controlPlug() only works with Smart-02 thermostats as smart plugs are now obsolete
// thermostatId may only be 1 thermostat (not a list) 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// plugName is the name of the plug name to be controlled 
// plugState is the state to be set
// plugSettings are the different settings at https://www.ecobee.com/home/developer/api/documentation/v1/functions/ControlPlug.shtml
void controlPlug(thermostatId, plugName, plugState, plugSettings = []) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
	def plugSettingsJson = new groovy.json.JsonBuilder(plugSettings)
	def plugSet = plugSettingsJson.toString().minus('{').minus('}')

	thermostatId = determine_tstat_id(thermostatId)
	traceEvent(settings.logFilter,"updateGroup> about to assemble bodyReq for plugName =${plugName}, thermostatId = ${thermostatId}, plugSettings=${plugSet}",settings.trace)
	def bodyReq =
		'{"selection":{"selectionType":"thermostats","selectionMatch":"' +
		thermostatId + '"}'
	bodyReq = bodyReq +
		',"functions":[{"type":"controlPlug","params":{"plugName":"' + plugName +
		'","plugState":"' + plugState + '"'

	// add the plugSettings if any
	if (plugSettings) {
		bodyReq = bodyReq + ',' + plugSet
	}
	bodyReq = bodyReq + '}}]}'
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck
		api('controlPlug', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"controlPlug>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				traceEvent(settings.logFilter,"controlPlug>done for thermostatId =${thermostatId},plugName =${plugName}",settings.trace)
				// post plug values 
 				def plugEvents = [
					plugName: plugName, 
 					plugState: plugState
 				]
                
				if (plugSettings) {
					plugEvents = plugEvents + [plugSettings: plugSet]
				}
				generateEvent(plugEvents)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"controlPlug>error ${statusCode.toString()},message=${message} for thermostatId =${thermostatId},plugName =${plugName}", true, get_LOG_ERROR())
			} /* end if statusCode */
		} /* end api call */               
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"controlPlug>exception=${exceptionCheck}, loop counter=$j", true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}
// thermostatId shall refer to a single thermostat to avoid processing too much data
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// reportColumn shall be 1 component only (ex. auxHeat1, coolComp1, fan, ventilator, humidifier, dehumidifier)
// startDateTime and endDateTime should be in UTC timezone format
// startInterval & endInterval may be null. In that case, the intervals are calculated using the startDateTime and endDateTime
// includeSensorData may be 'true' or 'false'
// postData may be 'true' or 'false', by default the latter

// See https://www.ecobee.com/home/developer/api/documentation/v1/operations/get-runtime-report.shtml for more details
// If you want to retrieve the data, then set postData to 'true', by default, reportdata and sensorData are not posted
//
// 	One can loop on the reportData or sensorData with the following logic 
//		ecobee.getReportData("", oneHourAgo, now, null, null, "dehumidifier",'false','true')
//		def reportData = ecobee.currentReportData.toString().split(",,")
//            
//		for (i in 0..reportData.size()-1) {
//			def rowDetails
//			try {
//				rowDetails = reportData[i].split(',')
//				def dateReportData = rowDetails[0]
//				def timeReportData = rowDetails[1]
//				def valueReportData = rowDetails[2]
//			
//			} catch (any)
//				log.error "no values ($rowDetails) for $component at $i"  // values are not always provided
//				continue
//			} 
//		}

void getReportData(thermostatId, startDateTime, endDateTime, startInterval, endInterval, reportColumn, includeSensorData, postData='false') {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
	double TOTAL_MILLISECONDS_PER_DAY=(24*60*60*1000)	
	def REPORT_TIME_INTERVAL=5
	def REPORT_MAX_INTERVALS_PER_DAY=287
	int beginInt, endInt

	thermostatId = determine_tstat_id(thermostatId)
	double nbDaysInPeriod = ((endDateTime.getTime() - startDateTime.getTime()) /TOTAL_MILLISECONDS_PER_DAY).round(2)
        
	if (nbDaysInPeriod > 31) {  // Report period should not be bigger than 31 days to avoid summarizing too much data.
		traceEvent(settings.logFilter,"getReportData> report's period too big (${nbDaysInPeriod.toString()} > 2)", true, get_LOG_ERROR())
		return
	} 
	if (thermostatId.contains(',')) {  // Report should run on a single thermostat only
		traceEvent(settings.logFilter,"getReportData> report should run on a single thermostatId only (${thermostatId})", true, get_LOG_ERROR())
		return
	}
    
	if (reportColumn.contains(',')) {  // Report should run on a single component only
		traceEvent(settings.logFilter,"getReportData> report should run on a single component only (${reportColumn})",true, get_LOG_ERROR())
		return
	}
	traceEvent(settings.logFilter,"getReportData> startDate in UTC timezone =${String.format('%tF %<tT',startDateTime)}," +
        	"endDate in UTC timezone =${String.format('%tF %<tT',endDateTime)}", settings.trace)
   	state?.reportRevision = newReportRevision
	state?.componentReport = reportColumn
    
	beginInt = (startInterval == null)? get_interval(startDateTime): startInterval.toInteger()
	endInt = (endInterval == null)? get_interval(endDateTime): endInterval.toInteger()
	Calendar startCalendar = startDateTime.toCalendar()
	Calendar endCalendar = endDateTime.toCalendar()
	traceEvent(settings.logFilter,"getReportData> startInterval = ${beginInt}, endInterval = ${endInt}",settings.trace)
	def bodyReq = '{"startInterval":"' + beginInt.toString() + '","endInterval":"' + endInt.toString() + 
    				'","startDate":"' + String.format('%tY-%<tm-%<td',startDateTime) + '",' + '"endDate":"' +
					String.format('%tY-%<tm-%<td',endDateTime) + '",' +
					'"columns":"' +  reportColumn + '","includeSensors":"' + includeSensorData + '",' +
					'"selection":{"selectionType":"thermostats","selectionMatch":"' + thermostatId + '"}}'
	traceEvent(settings.logFilter,"getReportData> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}...",settings.trace)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('runtimeReport', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"getReportData>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				data.reportList = resp.data.reportList
				data.startDate = resp.data.startDate
				data.endDate = resp.data.endDate
				data.startInterval = resp.data.startInterval
				data.endInterval = resp.data.endInterval
				data.columns = resp.data.columns
				if (includeSensorData=='true') {
					data.sensorList =  resp.data.sensorList
				}  
				def reportData = ""
				def reportSensorMetadata=""
				def reportSensorData =""
                
				if (postData=='true') {
					traceEvent(settings.logFilter,"getReportData>about to post reportData = $data.reportList.rowList[0].toString()",settings.trace)
					reportData = data.reportList?.rowList[0].toString().minus('[').minus(']')
					if (includeSensorData=='true') {
						reportSensorMetadata = new groovy.json.JsonBuilder(data.sensorList?.sensors[0])  // metadata is in Json format
						reportSensorData = data.sensorList?.data[0].toString().minus('[').minus(']')
					}   
				}   
				generateEvent(['reportData':reportData,'reportSensorMetadata':reportSensorMetadata,
					'reportSensorData':reportSensorData])
				traceEvent(settings.logFilter,"getReportData> startDate= ${data.startDate}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> endDate= ${data.endDate}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> startInterval= ${data.startInterval}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> endInterval= ${data.endInterval}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> columns= ${data.columns}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> reportList= ${data.reportList}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> sensorList= ${data.sensorList}",settings.trace)
				traceEvent(settings.logFilter,"getReportData> postData= ${postData}",settings.trace)
				traceEvent(settings.logFilter,"getReportData>done for thermostatId ${thermostatId}", settings.trace)
        	        
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"getReportData>error=${statusCode},message= ${message} for ${thermostatId}",settings.trace)			
			} /* end if statusCode */
		} /* end api call */                
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"getReportData>exception=${exceptionCheck}, loop counter=$j",true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}

private int get_interval(Date dateTime) {
	def REPORT_TIME_INTERVAL=5
	Calendar c = dateTime.toCalendar()
	int intervalHr = (c.get(Calendar.HOUR_OF_DAY)>0) ? 
		(c.get(Calendar.HOUR_OF_DAY) * 60) / REPORT_TIME_INTERVAL :0 
	int intervalMin = (c.get(Calendar.MINUTE)>= REPORT_TIME_INTERVAL) ? 
		c.get(Calendar.MINUTE) / REPORT_TIME_INTERVAL :0 
	traceEvent(settings.logFilter,"get_interval> Calendar hour= ${c.get(Calendar.HOUR_OF_DAY)}",settings.trace)
	traceEvent(settings.logFilter,"get_interval> Calendar minute= ${c.get(Calendar.MINUTE)}",settings.trace)
	traceEvent(settings.logFilter,"get_interval> intervalHr ${intervalHr}",settings.trace)
	traceEvent(settings.logFilter,"get_interval> intervalMin= ${intervalMin}",settings.trace)
	return (intervalHr + intervalMin)
}

// getReportData() must be called prior to calling generateReportRuntimeEvents() function
// component may be auxHeat1, compCool1, fan, ventilator, humidifier, dehumidifier, etc.
// startInterval & endInterval may be null. 
//	Intervals will be then defaulted to the ones used to generate the report
// typeEvent may be 'daily' or others

void generateReportRuntimeEvents(component, startDateTime, endDateTime, startInterval, endInterval, typeEvent ='daily') {
	double TOTAL_MILLISECONDS_PER_DAY=(24*60*60*1000)	
	def REPORT_TIME_INTERVAL=5
	def REPORT_MAX_INTERVALS_PER_DAY=287
	int beginInt, endInt
    
	double nbDaysInPeriod = ((endDateTime.getTime() - startDateTime.getTime()) /TOTAL_MILLISECONDS_PER_DAY).round(2)
        
	if (nbDaysInPeriod > 31) {  // Report period should not be bigger than 31 days to avoid summarizing too much data.
		traceEvent(settings.logFilter,"generateReportRuntimeEvents> report's period too big (${nbDaysInPeriod.toString()} > 2)", true, get_LOG_ERROR())
		return
	} 

	double totalRuntime, avgRuntime
	double runtimeInMin   // Calculations are done in minutes
    
	beginInt = (startInterval == null)? get_interval(startDateTime): startInterval.toInteger()
	endInt = (endInterval == null)? get_interval(endDateTime): endInterval.toInteger()
	Calendar startCalendar = startDateTime.toCalendar()
	Calendar endCalendar = endDateTime.toCalendar()
	if ((endCalendar.get(Calendar.MONTH) != startCalendar.get(Calendar.MONTH)) || 
		(endCalendar.get(Calendar.DATE) != startCalendar.get(Calendar.DATE))) {
		endInt += nbDaysInPeriod.intValue() * REPORT_MAX_INTERVALS_PER_DAY 
	}
	traceEvent(settings.logFilter,"generateReportRuntimeEvents> startInterval = ${beginInt}, endInterval = ${endInt}",settings.trace)
	if (component.contains('auxHeat1')) {
		totalRuntime = calculate_report_stats('auxHeat1', beginInt, endInt, 'report')
		if (typeEvent== 'daily') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
 			sendEvent name: "auxHeat1RuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'yesterday') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "auxHeat1RuntimeYesterday", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'weekly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 7 / 60).round(2) :0
			sendEvent name: "auxHeat1RuntimeAvgWeekly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'monthly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 30 / 60).round(2) :0
			sendEvent name: "auxHeat1RuntimeAvgMonthly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {	            
 			sendEvent name: "auxHeat1RuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('auxHeat2')) {
		totalRuntime = calculate_report_stats('auxHeat2', beginInt, endInt,'report')
		if (typeEvent== 'daily') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "auxHeat2RuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'yesterday') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "auxHeat2RuntimeYesterday", value:"${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'weekly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 7 / 60).round(2) :0
			sendEvent name: "auxHeat2RuntimeAvgWeekly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'monthly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 30 / 60).round(2) :0
			sendEvent name: "auxHeat2RuntimeAvgMonthly", value: "${runtimeInMin.toString()}",isStateChange:true
 		} else {           
			sendEvent name: "auxHeat2RuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('auxHeat3')) {
		totalRuntime = calculate_report_stats('auxHeat3', beginInt, endInt,'report')
		if (typeEvent== 'daily') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "auxHeat3RuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'yesterday') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "auxHeat3RuntimeYesterday", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'weekly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 7 / 60).round(2) :0
			sendEvent name: "auxHeat3RuntimeAvgWeekly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'monthly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 30 / 60).round(2) :0
			sendEvent name: "auxHeat3RuntimeAvgMonthly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "auxHeat3RuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('compCool1')) {
		totalRuntime = calculate_report_stats('compCool1', beginInt, endInt,'report')
		if (typeEvent== 'daily') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "compCool1RuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'yesterday') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "compCool1RuntimeYesterday", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'weekly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 7 / 60).round(2) :0
			sendEvent name: "compCool1RuntimeAvgWeekly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'monthly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 30 / 60).round(2) :0
			sendEvent name: "compCool1RuntimeAvgMonthly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "compCool1RuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('compCool2')) {
		totalRuntime = calculate_report_stats('coolComp2', beginInt, endInt,'report')
		if (typeEvent== 'daily') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "coolComp2RuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'yesterday') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
			sendEvent name: "compCool2RuntimeYesterday", value:"${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'weekly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 7 / 60).round(2) :0
			sendEvent name: "compCool2RuntimeAvgWeekly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else if (typeEvent== 'monthly') {
			runtimeInMin = (totalRuntime >60) ? (totalRuntime / 30 / 60).round(2) :0
			sendEvent name: "compCool2RuntimeAvgMonthly", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "coolComp2RuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('fan')) {
		totalRuntime = calculate_report_stats('fan', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "fanRuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "fanRuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('ventilator')) {
 		totalRuntime = calculate_report_stats('ventilator', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "ventilatorRuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "ventilatorRuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}
	}
	if (component.contains('dehumidifier')) {
		totalRuntime = calculate_report_stats('dehumidifier', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "dehumidifierRuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "dehumidifierRuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
		}                
	} else if (component.contains('humidifier')) {
		totalRuntime = calculate_report_stats('humidifier', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "humidifierRuntimeDaily", value: "${runtimeInMin.toString()}",isStateChange:true
		} else {            
			sendEvent name: "humidifierRuntimeInPeriod", value: "${runtimeInMin.toString()}",isStateChange:true
                
		}
	}
}
// getReportData() must be called prior to calling the generateReportSensorStatsEvents
// sensorId may be null or a specific sensorId as specified in the sensor metadata
//	see https://www.ecobee.com/home/developer/api/documentation/v1/objects/RuntimeSensorMetadata.shtml
// startInterval & endInterval may be null. 
//	Intervals will be then defaulted to the ones used to generate the report
// operation may be one or several stats separated by comma (ex. avg, min, max, total)

void generateReportSensorStatsEvents(sensorId, startDateTime, endDateTime, startInterval, endInterval, operation) {
	double TOTAL_MILLISECONDS_PER_DAY=(24*60*60*1000)	
	def REPORT_TIME_INTERVAL=5
	def REPORT_MAX_INTERVALS_PER_DAY=287
	int beginInt, endInt
	boolean foundSensor=false
    
	double nbDaysInPeriod = ((endDateTime.getTime() - startDateTime.getTime()) /TOTAL_MILLISECONDS_PER_DAY).round(2)

	float runtimeSensorStat
    
	beginInt = (startInterval == null)? get_interval(startDateTime): startInterval.toInteger()
	endInt = (endInterval == null)? get_interval(endDateTime): endInterval.toInteger()
	Calendar startCalendar = startDateTime.toCalendar()
	Calendar endCalendar = endDateTime.toCalendar()
	if (endCalendar.get(Calendar.DATE) != startCalendar.get(Calendar.DATE)) {
		endInt += nbDaysInPeriod.intValue() * REPORT_MAX_INTERVALS_PER_DAY 
	}
	traceEvent(settings.logFilter,"generateSensorRuntimeEvents> startInterval = ${beginInt}, endInterval = ${endInt}",settings.trace)
	if (sensorId != null) {
		foundSensor = data.sensorList.sensors[0].find{ it.sensorId == sensorId }
	}
	if (!foundSensor) {
		traceEvent(settings.logFilter,"generateReportSensorStatsEvents> sensor ${sensorId} not found in last sensor data from getReportData()",true, get_LOG_ERROR())
		return
	}    
	if (operation.contains('avg')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor', 'avg')
 		sendEvent name: "reportSensorAvgInPeriod", value: runtimeSensorStat.round(2).toString() 
	}
	if (operation.contains('min')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor', 'min')
 		sendEvent name: "reportSensorMinInPeriod", value: runtimeSensorStat.round(2).toString()
	}
	if (operation.contains('max')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor', 'max')
 		sendEvent name: "reportSensorMaxInPeriod", value: runtimeSensorStat.round(2).toString()
	}
	if (operation.contains('total')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor')
 		sendEvent name: "reportSensorTotalInPeriod", value: runtimeSensorStat.round(2).toString()
	}
}

private def calculate_report_stats(component, startInterval, endInterval, typeData, operation='total') {
	double total=0	
	int nbRows=0
	int max=0
	def min = null
	int rowValue
    
	int startRow = (startInterval != null) ? startInterval: data.startInterval.toInteger()
	int rowCount = (typeData=='sensor')? data.sensorList.data[0].size(): data.reportList.rowList[0].size()
	int lastRow =  Math.min(endInterval,rowCount)

	traceEvent(settings.logFilter,"calculate_report_stats> about to process rowCount= ${rowCount},startRow=${startRow},lastRow=${lastRow}",settings.trace)
	if (lastRow <= startRow) {    
		traceEvent(settings.logFilter,"calculate_report_stats>lastRow=${lastRow} is not greater than startRow=${startRow}",true, get_LOG_ERROR())
		return null
	}
	for (i in startRow..lastRow -1) {
		def rowDetails = (typeData=='sensor')? data.sensorList.data[0][i].split(","): data.reportList.rowList[0][i].split(",")
		try {
			rowValue = rowDetails[2]?.toInteger()
			total += rowValue
			nbRows++
			max = Math.max(rowValue,max)
			min = (min==null) ? rowValue : Math.min(rowValue,min)
		} catch (any) {
			traceEvent(settings.logFilter,"calculate_report_stats> no values ($rowDetails) for $component at $i",settings.trace, get_LOG_WARN())
			continue
		}	
	}
    
	int avg = ((nbRows >1) ? total/nbRows:total)
	traceEvent(settings.logFilter,"calculate_report_stats> total= ${total} for $component component",settings.trace)
	traceEvent(settings.logFilter,"calculate_report_stats> nbRows with value= ${nbRows} for $component component",settings.trace)
	traceEvent(settings.logFilter,"calculate_report_stats> avg= ${avg} for $component component",settings.trace)
	traceEvent(settings.logFilter,"calculate_report_stats> max= ${max} for $component component",settings.trace)
	traceEvent(settings.logFilter,"calculate_report_stats> min= ${min} for $component component",settings.trace)
	if (operation == 'avg') {
		return avg    
	}
	if (operation == 'max') {	
		return max    	
	}
	if (operation == 'min') {	
		return min    	
	}
	return total
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void getRemoteSensorUpdate(thermostatId=settings.thermostatId) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    
	def bodyReq = build_body_request('remoteSensorUpdate',null,thermostatId,null)
	traceEvent(settings.logFilter,"getRemoteSensorUpdate> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}...",settings.trace)
	int statusCode=1
	int j=0    
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck    
		api('thermostatInfo', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"getRemoteSensorUpdate>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()", settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				data?.thermostatList = resp.data.thermostatList
				def thermostatName = data.thermostatList[0].name
				if (data.thermostatList[0].remoteSensors) {
					traceEvent(settings.logFilter,"getRemoteSensorUpdate>found remote sensor values for thermostatId=${thermostatId},name=${thermostatName}",settings.trace)
				} else {
					traceEvent(settings.logFilter,"getRemoteSensorUpdate>No remote sensor values for thermostatId=${thermostatId},name=${thermostatName}",settings.trace)
				}        
				traceEvent(settings.logFilter,"getRemoteSensorUpdate>done for ${thermostatId}",settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"getRemoteSensorUpdate>error=${statusCode},message=${message} for ${thermostatId}",true, get_LOG_ERROR())
			} /* end if statusCode */                 
		} /* end api call */
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"getRemoteSensorUpdate>exception=${exceptionCheck}, loop counter=$j",true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}
// getThermostatInfo() or getRemoteSensorUpdate() must be called before calling generateRemoteSensorEvents
// thermostatId shall refer to a single thermostat to avoid processing too much data
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// postData may be true or false, by default the latter
// bypassThrottling may be true or false, by default the latter

void generateRemoteSensorEvents(thermostatId,postData=false,bypassThrottling=false) {
	def REMOTE_SENSOR_TYPE="ecobee3_remote_sensor"
	def REMOTE_THERMOSTAT_TYPE="thermostat"
	def REMOTE_SENSOR_OCCUPANCY='occupancy'
	def REMOTE_SENSOR_TEMPERATURE='temperature'
	def REMOTE_SENSOR_HUMIDITY='humidity'
    
	int nbTempSensorInUse=0
	int nbHumSensorInUse=0
	float value,totalTemp=0,totalHum=0, avgTemp=0, avgHum=0
	float maxTemp=0, maxHum=0
	def minTemp=null
	def minHum=null
    
	if ((thermostatId!=null) && (thermostatId !="")) {
		if (thermostatId.contains(",")) {
			traceEvent(settings.logFilter,"generateRemoteSensorEvents>thermostatId ${thermostatId} is not valid", true, get_LOG_ERROR())
			return
		}
	} else {	
			thermostatId = determine_tstat_id(thermostatId)
	}
	if (bypassThrottling) {      
		getRemoteSensorUpdate(thermostatId)    
	}
	    
     	
	/* Reset all remote sensor data values */
	def remoteData = []
	def remoteTempData = ""
	def remoteHumData = ""
	def remoteOccData = ""
    
	if (data.thermostatList[0].remoteSensors) {
		for (i in 0..data.thermostatList[0].remoteSensors.size() - 1) {
			traceEvent(settings.logFilter,"generateRemoteSensorEvents>found sensor ${data.thermostatList[0].remoteSensors[i]} at (${i})", settings.trace)
			if ((data.thermostatList[0].remoteSensors[i]?.type != REMOTE_SENSOR_TYPE) &&
			 (data.thermostatList[0].remoteSensors[i]?.type != REMOTE_THERMOSTAT_TYPE)) {
				traceEvent(settings.logFilter,"generateRemoteSensorEvents>found sensor type ${data.thermostatList[0].remoteSensors[i].type} at (${i}, skipping it)",settings.trace, get_LOG_WARN())
 				// not a remote sensor
 				continue
			}
			if (!data.thermostatList[0].remoteSensors[i].capability) {
				traceEvent(settings.logFilter,"generateRemoteSensorEvents>looping i=${i}, no capability values found...",settings.trace, get_LOG_WARN())
				continue            
			}            
			if (postData) {
				traceEvent(settings.logFilter,"generateRemoteSensorEvents>adding ${data.thermostatList[0].remoteSensors[i]} to remoteData",settings.trace)
				remoteData << data.thermostatList[0].remoteSensors[i]  // to be transformed into Json later
			} 
			for (j in 0..data.thermostatList[0].remoteSensors[i].capability.size()-1) {
				traceEvent(settings.logFilter,"generateRemoteSensorEvents>looping i=${i},found ${data.thermostatList[0].remoteSensors[i].capability[j]} at j=${j}",settings.trace)
				if (data.thermostatList[0].remoteSensors[i].capability[j].type == REMOTE_SENSOR_TEMPERATURE) {
					if ((data.thermostatList[0].remoteSensors[i].capability[j].value==null) || 
						(!data.thermostatList[0].remoteSensors[i].capability[j].value.isInteger())) {
						traceEvent(settings.logFilter,"generateRemoteSensorEvents>looping i=${i},j=${j}; found temp value, not valid integer: ${data.thermostatList[0].remoteSensors[i].capability[j].value}",
							settings.trace, get_LOG_WARN())                        
						continue
					}                    
					// Divide the sensor temperature by 10 
					value =(data.thermostatList[0].remoteSensors[i].capability[j].value.toFloat()/10).round(1)
 					remoteTempData = remoteTempData + data.thermostatList[0].remoteSensors[i].id + "," +
						data.thermostatList[0].remoteSensors[i].name + "," +
						data.thermostatList[0].remoteSensors[i].capability[j].type + "," + value.toString() + ",,"
					totalTemp = totalTemp + value
					maxTemp = Math.max(value,maxTemp)
					minTemp = (minTemp==null)? value: Math.min(value,minTemp)
					nbTempSensorInUse++
				} else if (data.thermostatList[0].remoteSensors[i].capability[j].type == REMOTE_SENSOR_HUMIDITY) {
					if ((data.thermostatList[0].remoteSensors[i].capability[j].value==null) || 
						(!data.thermostatList[0].remoteSensors[i].capability[j].value.isInteger())) {
						traceEvent(settings.logFilter,"generateRemoteSensorEvents>looping i=${i},j=${j}; found hum value, not valid integer: ${data.thermostatList[0].remoteSensors[i].capability[j].value}",
							settings.trace, get_LOG_WARN())                        
						continue
					}                    
					remoteHumData = remoteHumData + data.thermostatList[0].remoteSensors[i].id + "," + 
						data.thermostatList[0].remoteSensors[i].name + "," +
						data.thermostatList[0].remoteSensors[i].capability[j].type + "," + data.thermostatList[0].remoteSensors[i].capability[j].value + ",,"
					value =data.thermostatList[0].remoteSensors[i].capability[j].value.toFloat()
					totalHum = totalHum + value
					maxHum = Math.max(value,maxHum)
					minHum = (minHum==null)? value: Math.min(value,minHum)
					nbHumSensorInUse++
				} else if (data.thermostatList[0].remoteSensors[i].capability[j].type == REMOTE_SENSOR_OCCUPANCY) {
					if (data.thermostatList[0].remoteSensors[i].capability[j].value==null) { 
						traceEvent(settings.logFilter,"generateRemoteSensorEvents>looping i=${i},j=${j}; found occ value, not valid: ${data.thermostatList[0].remoteSensors[i].capability[j].value}",
							settings.trace, get_LOG_WARN())                        
						continue
					}                    
					remoteOccData = remoteOccData + data.thermostatList[0].remoteSensors[i].id + "," + 
						data.thermostatList[0].remoteSensors[i].name + "," +
						data.thermostatList[0].remoteSensors[i].capability[j].type + "," + data.thermostatList[0].remoteSensors[i].capability[j].value + ",,"
				} 
				                        
			} /* end for remoteSensor Capabilites */
		} /* end for remoteSensor data */
	}                        

	if (nbTempSensorInUse >0) {
		avgTemp = (totalTemp / nbTempSensorInUse).round(1)
		traceEvent(settings.logFilter,"generateRemoteSensorEvents>avgTemp for remote sensors= ${avgTemp},totalTemp=${totalTemp},nbTempSensors=${nbTempSensorInUse}", settings.trace)
	}                        
	def remoteSensorEvents = [
		remoteSensorOccData: "${remoteOccData.toString()}",
		remoteSensorAvgTemp: (avgTemp * 10),  // for display in the device, need to multiply by 10.
 		remoteSensorTmpData: "${remoteTempData.toString()}",
		remoteSensorMinTemp: ((minTemp!=null)? (minTemp *10):0),
		remoteSensorMaxTemp: (maxTemp *10)
	]    
	if (nbHumSensorInUse >0) {
		avgHum = (totalHum / nbHumSensorInUse).round()
		traceEvent(settings.logFilter,"generateRemoteSensorEvents>avgHum for remote sensors= ${avgHum},totalHum=${totalHum},nbHumSensors=${nbHumSensorInUse}",settings.trace)
		remoteSensorEvents = remoteSensorEvents + [remoteSensorHumData: "${remoteHumData.toString()}",remoteSensorAvgHumidity: avgHum,	
			remoteSensorMinHumidity: ((minHum!=null)?minHum:0),	remoteSensorMaxHumidity: maxHum]        
	}                        
	def remoteDataJson=""
 	if (remoteData != []) {
		remoteDataJson = new groovy.json.JsonBuilder(remoteData)
		remoteSensorEvents = remoteSensorEvents + [remoteSensorData: "${remoteDataJson.toString()}"]
	}
	traceEvent(settings.logFilter,"generateRemoteSensorEvents>remoteDataJson=${remoteDataJson}",settings.trace)
	traceEvent(settings.logFilter,"generateRemoteSensorEvents>remoteSensorEvents to be sent= ${remoteSensorEvents}",settings.trace)
	generateEvent(remoteSensorEvents)
}
    
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void getThermostatInfo(thermostatId=settings.thermostatId) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    
	traceEvent(settings.logFilter,"getThermostatInfo> about to call build_body_request for thermostatId = ${thermostatId}...",settings.trace)
	def bodyReq = build_body_request('thermostatInfo',null,thermostatId,null)
	traceEvent(settings.logFilter,"getThermostatInfo> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}...",settings.trace)
	int statusCode=1
	int j=0    
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck
		api('thermostatInfo', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"getThermostatInfo>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()", settings.trace, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				data?.thermostatList = resp.data.thermostatList
				def thermostatName = data.thermostatList[0].name           
				def runtimeSettings = data.thermostatList[0].runtime
				def thermostatSettings = data.thermostatList[0].settings
				traceEvent(settings.logFilter,"getThermostatInfo> thermostatId=${thermostatId},name=${thermostatName},hvacMode=${thermostatSettings.hvacMode}," +
						"fan=${runtimeSettings.desiredFanMode},fanMinOnTime=${thermostatSettings.fanMinOnTime},desiredHeat=${runtimeSettings.desiredHeat},desiredCool=${runtimeSettings.desiredCool}," +
						"current Humidity= ${runtimeSettings.actualHumidity},desiredHumidity=${runtimeSettings.desiredHumidity},humidifierMode=${thermostatSettings.humidifierMode}," +
						"desiredDehumidity= ${runtimeSettings.desiredDehumidity},dehumidifierMode=${thermostatSettings.dehumidifierMode}", 
						settings.trace)                        
				traceEvent(settings.logFilter,"getTstatInfo>done for ${thermostatId}",settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"getTstatInfo>error=${statusCode},message=${message} for ${thermostatId}",true, get_LOG_ERROR())			
			} /* end if statusCode */                 
		} /* end api call */
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"getThermostatInfo> exception=${exceptionCheck}, loop counter=$j",true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}
// tstatType =managementSet or registered (no spaces). 
// May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// thermostatId may be a single thermostat only
// returns true if intervalRevision, thermostatRevision or runtimeRevision has changed or false otherwise.

private boolean thermostat_revision_changed(tstatType, thermostatId) {	
	getThermostatRevision(tstatType, thermostatId)   
	def runtimeRevision=device.currentValue("runtimeRevision")
	def intervalRevision=device.currentValue("intervalRevision")
	def thermostatRevision=device.currentValue("thermostatRevision")
	traceEvent(settings.logFilter,"thermostat_revision_changed>done for ${thermostatId},intervalRevision=$intervalRevision,runtimeRevision=$runtimeRevision,thermostatRevision=$thermostatRevision," +
			"state.intervalRevision=${state?.intervalRevision},state.runtimeRevision=${state?.runtimeRevision},state.thermostatRevision=${state?.thermostatRevision}", settings.trace)
	if ((state?.runtimeRevision == runtimeRevision) &&
		(state?.intervalRevision == intervalRevision) &&
		(state?.thermostatRevision == thermostatRevision)) {
		traceEvent(settings.logFilter,"thermostat_revision_changed>same revisions, no changes at ecobee for thermostatId ${thermostatId}",settings.trace)
		return false
	} else {
		traceEvent(settings.logFilter,"thermostat_revision_changed>found revision changes at ecobee for thermostatId ${thermostatId} "  + 
				"state.intervalRevision=${state?.intervalRevision},state.runtimeRevision=${state?.runtimeRevision},state.thermostatRevision=${state?.thermostatRevision}",settings.trace)
		state?.intervalRevision=intervalRevision
		state?.runtimeRevision=runtimeRevision
		state?.thermostatRevision=thermostatRevision
		return true
	}            
}
// tstatType =managementSet or registered (no spaces). 
// May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// thermostatId may be a single thermostat only
void getThermostatRevision(tstatType, thermostatId) {
	def runtimeRevision,intervalRevision,thermostatRevision
    
	thermostatId = determine_tstat_id(thermostatId)
	tstatType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		thermostatRevision = thermostatDetails[3]
		runtimeRevision = thermostatDetails[5]
		intervalRevision = thermostatDetails[6]
		if ((thermostatId == id) && (connected=='true')) {
			sendEvent name: "runtimeRevision", value: runtimeRevision
			sendEvent name: "intervalRevision", value: intervalRevision
			sendEvent name: "thermostatRevision", value: thermostatRevision
			traceEvent(settings.logFilter,"getThermostatRevision>done for ${thermostatId},intervalRevision=$intervalRevision,runtimeRevision=$runtimeRevision,thermostatRevision=$thermostatRevision",
				settings.trace)            
			
		} else if ((thermostatId == id) && (connected=='false')) {
			traceEvent(settings.logFilter,"getThermostatRevision>thermostatId ${id} not connected",true, get_LOG_WARN())
		}       
	} /* end for */   
}
// tstatType =managementSet or registered (no spaces). 
// May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void getThermostatSummary(tstatType) {
	def ECOBEE_NEED_TOKEN_REFRESH=14
	def TOKEN_EXPIRED=401    
    

	def bodyReq = build_body_request('thermostatSummary',tstatType,null,null)
	traceEvent(settings.logFilter,"getThermostatSummary> about to call api with body = ${bodyReq}",settings.trace)
	int statusCode=1
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		def exceptionCheck
		api('thermostatSummary', bodyReq) {resp ->
			statusCode = resp?.data?.status?.code
			def message = resp?.data?.status?.message
			if ((resp?.status==TOKEN_EXPIRED) || ((resp?.status == 500) && (statusCode == ECOBEE_NEED_TOKEN_REFRESH))) {
				traceEvent(settings.logFilter,"getThermostatInfo>thermostatId=${thermostatId},error $statusCode, need to call refresh_tokens()",true, get_LOG_WARN())
				refresh_tokens()     
			}
			exceptionCheck=device.currentValue("verboseTrace")
			if (!statusCode) {
				/* when success, reset the exception counter */
				state.exceptionCount=0
				data?.revisionList = resp.data.revisionList
				data?.statusList = resp.data.statusList
				data?.thermostatCount = data.revisionList.size()
				for (i in 0..data.thermostatCount - 1) {
					def thermostatDetails = data.revisionList[i].split(':')
					def thermostatId = thermostatDetails[0]
					def thermostatName = thermostatDetails[1]
					def connected = thermostatDetails[2]
					def thermostatRevision = thermostatDetails[3]
					def alertRevision = thermostatDetails[4]
					def runtimeRevision = thermostatDetails[5]
					def intervalRevision = thermostatDetails[6]
					traceEvent(settings.logFilter,"getThermostatSummary>thermostatId=${thermostatId},name=${thermostatName},connected =${connected}",settings.trace)
					traceEvent(settings.logFilter,"getThermostatSummary>intervalRevision=${intervalRevision},runtimeRevision=${runtimeRevision},thermostatRevision=${thermostatRevision}",
						settings.trace)                    
					traceEvent(settings.logFilter,"getTstatSummary> found ${thermostatId},name=${thermostatName},connected=${connected}",settings.trace)
				} /* end for */                        
				traceEvent(settings.logFilter,"getTstatSummary>done",settings.trace)
			} else {
				state.exceptionCount = state.exceptionCount +1     
				traceEvent(settings.logFilter,"getTstatSummary> error= ${statusCode.toString()},message=${message}",true, get_LOG_ERROR())
			} /* end if statusCode */
		}  /* end api call */              
			            
		if (exceptionCheck?.contains("exception")) {
			state.exceptionCount = state.exceptionCount +1     
			traceEvent(settings.logFilter,"getThermostatSummary>exception=${exceptionCheck}, loop counter=$j",true, get_LOG_ERROR())
			sendEvent(name: "verboseTrace", value: "", displayed:(settings.trace?:false)) // reset verboseTrace            
			statusCode=(statusCode)?:3 //internal error            
		}                
	} /* end while */
}
// poll(), refresh() or getThermostatInfo() must be called prior to calling the getModelNumber() method 
// Return thermostat's current Model Number */
private def getModelNumber() {

	traceEvent(settings.logFilter,"getModelNumber>thermostatId=${data.thermostatList[0]?.identifier}," +
        	"modelNumber=${data.thermostatList[0]?.modelNumber}", settings.trace)
	return ((device.currentValue("thermostatId"))? data.thermostatList[0].modelNumber: "")
}
private def refreshLocalAuthToken() {	
	boolean refresh_success=false
	def REFRESH_SUCCESS_CODE=200    
 
	traceEvent(settings.logFilter,"refreshLocalAuthToken>about to refresh auth token", settings.trace)
    
	def stcid = get_appKey()

	def refreshParams = [
			method: 'POST',
			uri   : "${get_URI_ROOT()}",
			path  : "/token",
			query : [grant_type: 'refresh_token', code: "${data.auth.refresh_token}", client_id: stcid]

		]

	traceEvent(settings.logFilter,"refreshLocalAuthToken>refreshParams=$refreshParams", settings.trace)
	def jsonMap
	httpPost(refreshParams) { resp ->
		if (resp.status == REFRESH_SUCCESS_CODE) {
			traceEvent(settings.logFilter,"refreshLocalAuthToken>token refresh done resp = ${resp.data}",settings.trace)
			jsonMap = resp.data

			if (resp.data) {

				data.auth.access_token = resp.data.access_token
				data.auth.refresh_token = resp.data.refresh_token
				data.auth.expires_in = resp.data.expires_in
				data.auth.token_type = resp.data.token_type
				data.auth.scope = resp.data.scope
				def authexptime = new Date((now() + (resp?.data?.expires_in  * 1000))).getTime()
				data.auth.authexptime=authexptime 						                        
				traceEvent(settings.logFilter,"refreshLocalAuthToken>new authexptime = ${authexptime}",settings.trace)
				traceEvent(settings.logFilter,"refreshLocalAuthToken>new access token = ${data.auth.access_token}",settings.trace)
				traceEvent(settings.logFilter,"refreshLocalAuthToken>new refresh token = ${data.auth.refresh_token}",settings.trace)
				refresh_success=true   
			} /* end if resp.data */
		} else {
			traceEvent(settings.logFilter,"refreshLocalAuthToken>refresh failed ${resp.status} : ${resp.status.code}",true, get_LOG_ERROR())
		} /* end if http status == 200 */
	} /* end if http post */           

	return refresh_success	        
}

private def refresh_tokens() {
	def buffer_time_refresh=2  // set a 2 min. buffer time before re-attempting refresh 
	def time_check_for_refresh = (now() - (buffer_time_refresh * 60 * 1000))
    
	if (data.auth?.thermostatId && (isTokenExpired())) {    
 		traceEvent(settings.logFilter,"refresh_tokens>trying to get tokens from parent first to avoid repetitive calls to parent.refreshParentTokens()",true, get_LOG_WARN())
 		parent.refreshThisChildAuthTokens(this) 
		if (!isTokenExpired()) {    
			state?.lastRefreshTimestamp= now()
			return true
		}    
	}    
	if ((state?.lastRefreshTimestamp) && (state?.lastRefreshTimestamp > time_check_for_refresh)) {
		traceEvent(settings.logFilter,"refresh_tokens>time_check_for_refresh (${time_check_for_refresh} < state.lastRefreshTimestamp (${state?.lastRefreshTimestamp}), not refreshing tokens...",
			settings.trace, get_LOG_TRACE())        
		return false
	}
//	if device created by initialSetup (Service Manager)

	if (data.auth?.thermostatId) {
		if (!parent.refreshParentTokens()) {
			traceEvent(settings.logFilter,"refresh_tokens>warning: local tokens still expired after refreshParentTokens() call", settings.trace, get_LOG_WARN())
			refreshLocalAuthToken() // If not successful, refresh the device tokens locally
			parent.setParentAuthTokens(data.auth)
			traceEvent(settings.logFilter,"refresh_tokens>warning: called refreshLocalAuthToken() after parent call failure",true, get_LOG_WARN())
		}            
	}  else {  /* standalone device with PIN */
		refreshLocalAuthToken()
	}    
	if (data.auth?.thermostatId && isTokenExpired()) {
		traceEvent(settings.logFilter,"refresh_tokens>local tokens still expired after call to refreshParentTokens,trying to get tokens from parent again",true, get_LOG_WARN())
		parent.refreshThisChildAuthTokens(this) 
	}  	
	if (!isTokenExpired()) {    
		state?.lastRefreshTimestamp= now()
		return true
	}  	        
	traceEvent(settings.logFilter,"refresh_tokens>local tokens still expired after refresh_tokens() call",true, get_LOG_WARN())
	return false
}
private def refreshChildTokens(auth) {
	traceEvent(settings.logFilter,"refreshChildTokens>begin new token auth= $auth",settings.trace)
	traceEvent(settings.logFilter,"refreshChildTokens>old data.auth= $data.auth",settings.trace)
	if ((!data?.auth?.authexptime) || ((data?.auth?.authexptime) && (auth.authexptime > data?.auth?.authexptime))) {    
		data.auth.access_token = auth.authToken
		data.auth.refresh_token = auth.refreshToken
		data.auth.expires_in = auth.expiresIn
		data.auth.token_type = auth.tokenType
		data.auth.scope = auth.scope
		data.auth.authexptime = auth.authexptime
		traceEvent(settings.logFilter,"refreshChildTokens>end newly refreshed data.auth=$data.auth",settings.trace)
	}  
	if (!isTokenExpired()) {
		return true   
	}       
	traceEvent(settings.logFilter,"refreshChildTokens>warning:tokens still expired",true, get_LOG_WARN())
	return false    
}
private void login() {
	traceEvent(settings.logFilter,"login> about to call setAuthTokens",settings.trace)
	if (data?.auth?.thermostatId) {
    	// Created by initalSetup
		traceEvent(settings.logFilter,"login>should be already logged in...",true, get_LOG_ERROR())
		parent.refreshThisChildAuthTokens(this) 
        
	} else { 
		setAuthTokens()
	}    
	if (!isLoggedIn()) {
		traceEvent(settings.logFilter,"login> no auth_token..., failed",true, get_LOG_ERROR())
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
		traceEvent(settings.logFilter,"getEcobeePinAndAuth> response = ${resp.data}",settings.trace)
		data?.auth = resp.data
		data.auth.code = resp.data.code
		data.auth.expires_in = resp.data.expires_in
		data.auth.interval = resp.data.interval
		data.auth.ecobeePin = resp.data.ecobeePin
		traceEvent(settings.logFilter,"getEcobeePin>${data.auth.ecobeePin}", settings.trace)
		data.auth.access_token = null // for isLoggedIn() later
		data.thermostatCount = null // for iterate functions later
		data.groups = null // for iterateUpdateGroups later
	}
	try {
		httpGet(method, successEcobeePin)
	} catch (java.net.UnknownHostException e) {
		traceEvent(settings.logFilter,"getEcobeePinAndAuth>Unknown host - check the URL " + method.uri,true, get_LOG_ERROR())
		return
	} catch (java.net.NoRouteToHostException e) {
		traceEvent(settings.logFilter,"getEcobeePin> No route to host " +	method.uri, true, get_LOG_ERROR())
		return
	} catch (java.io.IOException e) {
		traceEvent(settings.logFilter,"getEcobeePinAndAuth> Authentication error, bad URL or settings missing " +
			method.uri, true, get_LOG_ERROR())
		return
	} catch (any) {
		traceEvent(settings.logFilter,"getEcobeePin> general error " +	method.uri, true, get_LOG_ERROR())
		return
	}
	traceEvent(settings.logFilter,"getEcobeePinAndAuth> ecobeePin= ${data.auth.ecobeePin}, authorizationCode=${data.auth.code},scope=${data.auth.scope} expires_in=${data.auth.expires_in} done",
		settings.trace)
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
			traceEvent(settings.logFilter,"setAuthTokens> accessToken= ${data.auth.access_token}, refreshToken=${data.auth.refresh_token}," +
					"tokenType=${data.auth.token_type},scope=${data.auth.scope}",settings.trace)
		}
		try {
			traceEvent(settings.logFilter,"setAuthTokens> about to call httpPost with code= ${data.auth.code}",settings.trace)
			httpPostJson(method, successTokens)

		} catch (java.net.UnknownHostException e) {
			traceEvent(settings.logFilter,"setAuthTokens> Unknown host " + method.uri, true, get_LOG_ERROR())
			return
		} catch (java.net.NoRouteToHostException t) {
			traceEvent(settings.logFilter,"setAuthTokens> No route to host" +	method.uri, true, get_LOG_ERROR())
			return
		} catch (java.io.IOException e) {
			traceEvent(settings.logFilter,"setAuthTokens> Auth error " + method.uri, true, get_LOG_ERROR())
			return
		} catch (any) {
			traceEvent(settings.logFilter,"setAuthTokens>general error " + method.uri, true, get_LOG_ERROR())
			return
		}
		// determine token's expire time
		def authexptime = new Date((now() + (data.auth.expires_in  * 1000))).getTime()
		data.auth.authexptime = authexptime
		traceEvent(settings.logFilter,"setAuthTokens> expires in ${data.auth.expires_in} seconds", settings.trace)
		traceEvent(settings.logFilter,"setAuthTokens> data_auth.expires_in in time = ${authexptime}",settings.trace)
	}
}
private def isLoggedIn() {
	if (data?.auth == null) {
		traceEvent(settings.logFilter,"isLoggedIn> no data auth", settings.trace, get_LOG_WARN())
		return false
	} else {
		if (data?.auth?.access_token == null) {
			traceEvent(settings.logFilter,"isLoggedIn> no access token", true, get_LOG_WARN())
			return false
		}
	}
	return true
}
private def isTokenExpired() {
	def buffer_time_expiration=5  // set a 1 min. buffer time 
	def time_check_for_exp = now() + (buffer_time_expiration * 60 * 1000)
	if (!data?.auth?.authexptime) {
		login()    
	}    
	double authExpTimeInMin= ((data?.auth?.authexptime - time_check_for_exp)/60000).toDouble().round(0)  
	traceEvent(settings.logFilter,"isTokenExpired>expiresIn timestamp: ${data.auth.authexptime} > timestamp check for exp: ${time_check_for_exp}?",settings.trace)
	traceEvent(settings.logFilter,"isTokenExpired>expires in ${authExpTimeInMin.intValue()} minutes",settings.trace)
	traceEvent(settings.logFilter,"isTokenExpired>data.auth= $data.auth",settings.trace)
	if (authExpTimeInMin <0) {
		traceEvent(settings.logFilter,"isTokenExpired>auth token buffer time  expired (${buffer_time_expiration} min.), countdown is ${authExpTimeInMin.intValue()} minutes, need to refresh tokens now!",
			settings.trace, get_LOG_WARN())        
	}    
	if (authExpTimeInMin < (0-buffer_time_expiration)) {
		traceEvent(settings.logFilter,"isTokenExpired>refreshing tokens is more at risk (${authExpTimeInMin} min.),exception count may increase if tokens not refreshed!", settings.trace, get_LOG_WARN())
	}    
	if (data.auth.authexptime > time_check_for_exp) {
		traceEvent(settings.logFilter,"isTokenExpired> not expired...", settings.trace)
		return false
	}
	traceEvent(settings.logFilter,"isTokenExpired> expired...", settings.trace)
	return true
}


// Determine ecobee type from tstatType or settings
// tstatType =managementSet or registered (no spaces). 
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
private def determine_ecobee_type_or_location(tstatType) {
	def ecobeeType
	def modelNumber = getModelNumber()
    
	traceEvent(settings.logFilter,"determine_ecobee_type>begin ecobeeType = ${tstatType}, modelNumber=${modelNumber}",settings.trace)
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
	traceEvent(settings.logFilter,"determine_ecobee_type>end ecobeeType = ${ecobeeType}",settings.trace)
	return ecobeeType
}

// Determine id from settings or initalSetup
private def determine_tstat_id(tstat_id) {
	def tstatId
    
	if ((tstat_id != null) && (tstat_id != "")) {
		tstatId = tstat_id.trim()
		settings.thermostatId = tstatId
	} else if ((settings.thermostatId != null) && (settings.thermostatId  != "")) {
		tstatId = settings.thermostatId.trim()
		traceEvent(settings.logFilter,"determine_tstat_id> tstatId = ${settings.thermostatId}", settings.trace)
	} else if ((settings.thermostatId == null) || (settings.thermostatId  == "")) {
		settings.appKey = data.auth.appKey
		settings.thermostatId = data.auth.thermostatId
		tstatId=data.auth.thermostatId
		traceEvent(settings.logFilter,"determine_tstat_id> tstatId from data= ${data.auth.thermostatId}",settings.trace)
	}
	if ((tstat_id != "") && (tstat_id != tstatId)) {
		sendEvent(name: "thermostatId", displayed: (settings.trace?:false),value: tstatId)    
	}
	return tstatId
}

// Get the appKey for authentication
private def get_appKey() {
	return settings.appKey
}    

// Called by My ecobee Init for initial creation of a child Device
void initialSetup(device_client_id, auth_data, device_tstat_id) {
//	settings.trace=true
	traceEvent(settings.logFilter,"initialSetup>begin",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> device_tstat_Id = ${device_tstat_id}",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> device_client_id = ${device_client_id}",settings.trace)
	settings.appKey= device_client_id
	settings.thermostatId = device_tstat_id

	data?.auth = settings    
	data.auth.access_token = auth_data.authToken
	data.auth.refresh_token = auth_data.refreshToken
	data.auth.expires_in = auth_data.expiresIn
	data.auth.token_type = auth_data.tokenType
	data.auth.authexptime= auth_data?.authexptime
	traceEvent(settings.logFilter,"initialSetup> settings = $settings",settings.trace)
	traceEvent(settings.logFilter,"initialSetup> data_auth = $data.auth",settings.trace)
	traceEvent(settings.logFilter,"initialSetup>end",settings.trace)

	getThermostatInfo(device_tstat_id)
	def ecobeeType=determine_ecobee_type_or_location("")
	data.auth.ecobeeType = ecobeeType
	state?.exceptionCount=0    
	state?.scale = getTemperatureScale()
	state?.currentMode=device.currentValue("thermostatMode")    
    
}


private def get_stats_attribute(attribute, startDate,endDate,operation='avg',filterEvents=false,value=null) {
	def result
	int count    
	def recentStates = device.statesBetween(attribute, startDate, endDate, [max:200])
	def MIN_TEMP_VALUE=0
	def MAX_TEMP_VALUE=120
    
	if ((!value) && filterEvents) {    
		count =recentStates.count {(it.floatValue >MIN_TEMP_VALUE && it.floatValue < MAX_TEMP_VALUE)}
	} else if (!value) {    
		count =recentStates.count {it.value}
	} else {
		count =recentStates.count {(it.value == value)}    
	}    
	switch (operation) {    
		case 'total':
			if (filterEvents) {             
				result =recentStates.inject(0) { total, i -> total + ((i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)? i.floatValue :0) }
			} else {
				result =recentStates.inject(0) { total, i -> total + i.floatValue }
	
    		}
			break        
		case 'avg':
		case 'deviation':
			float avgResult        
			if (filterEvents) {             
				avgResult = recentStates.inject(0) { total, i -> total + ((i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)? i.floatValue:0)}
			} else {
				avgResult = recentStates.inject(0) { total, i -> total + i.floatValue}
			}            
			avgResult = (count)? (avgResult /count).round(1):0        
			if (operation == 'avg') {
				result=avgResult            
				break
			}   
			if (filterEvents) {             
				result = recentStates.inject(0) { totalDev, i -> totalDev + ((i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)? 
					((i.value.toFloat() - avgResult).abs()) : 0)}
			} else {  
				result = recentStates.inject(0) { totalDev, i -> totalDev + ((i.floatValue - avgResult).abs())}
			}			                    
			result = (count)? (result /count).round(2):0        
//			traceEvent(settings.logFilter,"get_stats_attribute>for ${attribute}, avg=${avgResult},avgDeviation=${result},count=${count}",settings.trace)
			break            
		case 'min':
			if (filterEvents) {             
				result =recentStates.inject(MAX_TEMP_VALUE) {min, i -> Math.min(min,((i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)?i.floatValue:MAX_TEMP_VALUE))}
			} else {
				result =recentStates.inject(MAX_TEMP_VALUE) {min, i -> Math.min(min, i.floatValue) }
			}            
			break            
		case 'max':
			if (filterEvents) {             
				result =recentStates.inject(MIN_TEMP_VALUE) {max, i -> Math.max(max,((i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)?i.floatValue:MIN_TEMP_VALUE)) }
			} else {
				result =recentStates.inject(MIN_TEMP_VALUE) {max, i -> Math.max(max,i.floatValue) }
			}            
			break            
		case 'values':
        
			if (filterEvents) {             
				result =recentStates.inject([]) {valueSet, i -> (i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)?
					valueSet << i.floatValue: {} }
			} else if (value) {
				def valuesFound =recentStates.find {it.value==value }
				result = valuesFound.inject([])  {valueSet, i -> valueSet << i.value}               
                
			} else {                 
				result =recentStates.inject([]) {valueSet, i -> valueSet << i.value}
			}                
			                
			break            
		case 'dates':
			if (filterEvents) {             
				result =recentStates.inject([]) {valueSet, i -> (i.floatValue >MIN_TEMP_VALUE && i.floatValue < MAX_TEMP_VALUE)?
					valueSet << i.date.getTime(): {} }
			} else if (value) {
				def valuesFound =recentStates.find {it.value==value }
//				if (settings.trace) {                
//					valuesFound.each {                
//						traceEvent(settings.logFilter,"get_stats_attribute>dates valueFound= ${it.value}, date=${it.date.getTime()}, true)
//					}                        
//				}                    
				result = valuesFound.inject([])  {valueSet, i -> valueSet << i.date.getTime()}               
//				traceEvent(settings.logFilter,"get_stats_attribute>dates result set= ${result}",settings.trace)
			} else {                 
				result =recentStates.inject([]) {valueSet, i ->  valueSet << i.date.getTime()}
			}                
			break            
		default:
			result=count
		break            
	}    
	return result    
}

private def get_recommendation(tip) {

	def tips = [
		'tip01': 
			[level:1, 
				text:"You should set the default Sleep climate at ecobee according to your Sleep schedule and use energy efficient settings as indicated on the energy.gov website." +
				"You can save as much as 10% to 20% a year on heating and cooling by doing a temperature setback or setforward of 7 degrees Farenheit or 3 degrees Celsius for 8 hours a day from its normal setting." +
				"A temperature setback is when you lower your desired temperature before your thermostat calls for heat." +
				"A temperature setforward is when you increase your desired temperature before your thermostat calls for cool."
			], 
		'tip02': 
			[level:1, 
				text:"You should set the default Away climate at ecobee according to your work schedule and use energy efficient settings as indicated on the energy.gov website." +
				"You can save as much as 10% to 20% a year on heating and cooling by doing a temperature setback or setforward of 7 degrees Farenheit or 3 degrees Celsius for 8 hours a day from its normal setting." +
				"A temperature setback is when you lower your desired temperature before your thermostat calls for heat." +
				"A temperature setforward is when you increase your desired temperature before your thermostat calls for cool."
			],
		'tip03': 
			[level:1, 
 				text: "When you have more than one thermostat registered to your account, you can group them together. Adjustments made on one thermostat are then made to ALL thermostats in that group. It will allow you to determine which features within a specific group are synchronized. " +
				"For example if you select Synchronize User Preferences, when you configure any user preference on one thermostat (i.e. choose Celsius vs. Fahrenheit) ALL thermostats within that group will change to Celsius.  Consult your ecobee user guide for more details."			
			],
		'tip04': 
			[level:1, 
				text: "To reduce the number of cycles, you can try to increase the Heat and Cool Minimum On Time and the Heat and Cool Differential settings at the thermostat unit in Main Menu, Settings, Installation Settings, and finally Thresholds for longer cycles." +
				"You can also try to increase the Heat or Cool dissipation time"  +
				" at the thermostat unit, which is 30 seconds by default. The dissipation time settings control the fan runtime in seconds after a cool or heat cycle which helps to evacuate any hot or cool air from the ducts." + 
				" Consult the ecobee user guide for more details."
			],
		'tip05': 
			[level:1, 
				text:"The default Hold action is ìUntil you change it.î You can configure how long a temperature hold will remain in effect. On the thermostat, Select Main Menu, Settings,Preferences, then Select Hold action."+
				"Your options are: a) 2 hours (Holds temperature change for a period of 2 hours, then resumes your normal schedule)." +
				"b) 4 hours, c) until the next scheduled activity or comfort setting and the last one d) Until you change it. For maximum efficiency, it is recommended to use the 'until the next scheduled activity or transition' as ecobee will use its own scheduling as much as possible and avoid permanent holds." 
			],
		'tip06': 
			[level:1, 
				text:"Access Control uses a 4-digit security code to prevent people, such as guests and children, from making changes to your thermostat settings. " +
				"To enable Access Control on the Thermostat, select Main Menu, Settings, Access Control, Enable Security Code, Enter a New Code, and finally Save."			
			],
		'tip10': 
			[level:2, 
				text:"You can save as much as 10% to 20% a year on heating and cooling by doing a temperature setback or setforward of 7 degrees Farenheit or 3 degrees Celsius for 8 hours a day from its normal setting." 
			],
		'tip13': 
			[level:2, 
				text:"The location of your thermostat and remote sensors (if any) can affect its performance and efficiency. Read the manufacturer's installation instructions to prevent ghost readings or unnecessary furnace or air conditioner cycling." +
				"To operate properly, the thermostat and any remote sensor must be on an interior wall away from direct sunlight, drafts, doorways, skylights, and windows. It should be located where natural room air currentsñwarm air rising, cool air sinkingñoccur." +
				"Also, make sure to change your furnace filter and clean your A/C evaporator coils using mild detergents and water for maximum equipment efficiency."
			],
		'tip14': 
			[level:2, 
				text:"Your ecobee thermostat works with wireless remote sensors to measure temperature and occupancy in multiple rooms and make smarter heating and cooling decisions for you. They keep you comfortable while saving you money." +
				"Just as you can select which sensors participate in Comfort Settings, you can select which donít participate." +                 
				"To configure your sensor participation settings, select Menu, Sensors, Sensor Name, Participation and select all of the Comfort Settings you want this sensor to participate in."			
			],
		'tip15': 
			[level:2, 
				text:"Setting your fanís minimum runtime per hour at the thermostat unit in Main Menu, Settings, Installation Settings, and finally Thresholds  will help distribute the air around the home, creating more even temperature in the home. Youíll be more comfortable, and with more even temperatures the ecobee will not engage your HVAC as often to compensate for hot and cold spots. " +
				"You will be more comfortable at home and save energy while youíre at it. To change the MinimumFanOnTime setting, Select Main Menu, Quick Changes, Fan on your thermostat unit or mobile app. "+                
				"You can also set a MinimumFanOnTime value per ecobee schedule using the ecobeeFanMinOnTime or ecobeeSetZoneWithSchedule smartapps.  Contact ecomatiq homes.com for more details."
			],
		'tip16': 
			[level:2, 
				text:"You should change the default Sleep climate at ecobee according to your Sleep schedule and use energy efficient settings as indicated on the energy.gov website." +
				"You can save as much as 10% to 20% a year on heating and cooling by doing a temperature setback or setforward of 7 degrees Farenheit or 3 degrees Celsius for 8 hours a day from its normal setting." 
			], 
		'tip17': 
			[level:2, 
				text:"You should set the default Away climate at ecobee according to your work schedule and use energy efficient settings as indicated on the energy.gov website." +
				"You can save as much as 10% to 20% a year on heating and cooling by doing a temperature setback or setforward of 7 degrees Farenheit or 3 degrees Celsius for 8 hours a day from its normal setting." 
			],
		'tip18': 
			[level:2, 
				text:"If you're away for a long period of time, you should use the Vacation mode. The Vacation feature on your ecobee3 helps you conserve energy while you are away for extended periods and ensures your home is at a comfortable temperature before you arrive home. " +
				"The Vacation feature is smart, which means you can leave your normal schedule as-is, and your ecobee3 will automatically return to it when your vacation ends. You can even create multiple vacations, so you can program it right when you book your vacations, and not have to think about it again." 
			],
		'tip20': 
			[level:3, 
				text:"To maintain comfort inside the home, the humidity inside the home must be controlled along with the temperature of the air. Consider a warm summers day, if the humidity inside the home increases, the air will hold more heat " +
				"and the air conditioner will need to run longer to offset both the humidity and the warm air. The same principle applies for heating as it takes more time to heat air heavy with humidity."
			],
		'tip21': 
			[level:3, 
				text:"To maintain comfort inside the home, the humidity inside the home must be controlled along with the temperature of the air. As you don't have a dehumidifer connected to ecobee, you can use " +
					"the A/C to dehumidify your home by using the A/C Overcool option. In the thermostat menu, Select Settings, Installation Settings, Thresholds, and finally AC Overcool Max." +
					"The amount the system will cool your house beyond the desired temperature in the currently used Comfort Setting is determined by the desired temperature itself."                    
			],
		'tip22': 
			[level:3, 
				text:"To maintain comfort inside the home, the humidity inside the home must be controlled along with the temperature of the air. Your dehumidifier is connected to your ecobee, you should then use " +
					"ecomatiq homes specialized MonitorAndSetEcobeeHumidity smartapp to activate automatically your dehumidifier when required. Contact ecomatiq homes.com for more details."
			],
		'tip23': 
			[level:3, 
				text:"Your HRV or ERV is connected to your ecobee, you should then use " +
					"ecomatiq homes zoned heating and cooling smartapps to automatically activate your HRV/ERV for free cooling. Contact ecomatiq homes.com for more details."
			],
		'tip24': 
			[level:3, 
				text:"To maintain comfort inside the home, the humidity inside the home must be controlled along with the temperature of the air. Your HRV/ERV is connected to your ecobee, you should then use " +
					"ecomatiq homes specialized MonitorAndSetEcobeeHumidity smartapp to activate automatically your dehumidifier when required. Contact ecomatiq homes.com for more details."
			],
		'tip25': 
			[level:3, 
				text:"The auxMaxOutdoorTemp parameter is found in Installation Settings,Thresholds, and finally Aux Heat Settings. This is the temperature at which ecobee will not run auxiliary Heat under any circumstances. " +
				"This is an energy savings feature which is designed to allow people very concerned with savings to ensure the auxiliary Heat does not run if it is mild outside. " +
 				"This value should be set to a high value close to your comfort temperature." +
				"The auxOutdoorTemp setting should not be set at or below freezing temperature; otherwise, if you need auxHeat and the temperature is low, your pipes may freeze."
			],
		'tip26': 
			[level:3, 
				text:"Here are some concepts about staging: when set to auto for a given type of heating (i.e. heat pump or resistive strips), ecobee calculates the time it will take to achieve setpoint (considering outside temperature" + 
				"and the way your house and equipment perform) and if that time is more than 10 minutes, ecobee will upstage right away. You can override this behavior using " +
				"the following ecobee settings at the thermostat unit: Compressor Stage 1 Max Runtime, Compressor to Aux Temp Delta, Compressor to Stage2 Temp Delta, and Compressor Reverse Staging." +
				"If reverse staging is enabled, the thermostat will cycle down from the higher stages so that as ecobee approaches the set point, it will only be running instage 1." +
				"In terms of seeing when stage 1 or 2 are running, if you have configured your auxHeat as two-stage, when you go to the ecobee web portal," + 
				"under the HomeIQ menu item, stage 1 and stage 2 auxHeat will be plotted separately on the graph. You can use the toggle buttons in the legend" + 
				"at the bottom of the graph to turn off or on different values so that you can look at specific values more easily." +
				"Please consult your HVAC manual for your best equipment settings. You can also contact ecobee support if needed."
			],
		'tip27': 
			[level:3, 
				text:"To maintain comfort inside the home, the humidity inside the home must be controlled along with the temperature of the air. Your humidifier is connected to your ecobee, you should then use " +
					"ecomatiq homes specialized MonitorAndSetEcobeeHumidity smartapp to activate automatically your humidifier when required. In Winter, you can take advantage of the frost control feature that you can set at the thermostat unit or in the smartapp." +
					"For more details about MonitorAndSetEcobeeHumidity, contact ecomatiq homes.com."                    
			],
		'tip30': 
			[level:3, 
				text:"Check if your thermostat has the smart recovery feature enabled at the unit in Main Menu, Settings, Preferences. As you use your ecobee, it learns how long it takes for your home to " +
				"reach your desired temperature, taking into account the performance of your equipment and how the weather effects the regulation of the indoor temperature."                
			],
		'tip31': 
			[level:3, 
				text:"The outdoor temperature has been fairly constant, yet your A/C or Furnace runtime has increased in the last day." +
				"Try to use the ecomatiq homes windowOrDoorOpen smartapp to avoid cooling or heating your home when a door or window contact is open for too long." +
				"For more comfort, you should also try to heat or cool your home using the zoned heating and cooling smartapps available at www.ecomatiq homes.com."
		],
		'tip32': 
			[level:3, 
				text:"To reduce the number of cooling cycles and cooling runtime, you should try " +
					"ecomatiq homes zoned heating and cooling smartapps to activate automatically a damper or big fan or evaporative cooler switch to enable free cooling." +
					"Contact ecomatiq homes.com for more details."
			],
		'tip40': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Heat instead of your main HVAC component."
			],
		'tip41': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Heat instead of your main HVAC component."
			],
		'tip42': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Heat instead of your main HVAC component."
			],
		'tip43': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Heat instead of your main HVAC component."
			],
		'tip44': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Heat instead of your main HVAC component."
			],
		'tip45': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Cool instead of your main HVAC component."
			],
		'tip46': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Cool instead of your main HVAC component."
			],
		'tip47': 
			[level:4, 
				text:"Over time, you may want to keep track your HVAC efficiency and this smartapp can give you some metrics about it.  Consult your manual and make sure that your HVAC " +
				"can reach its balance point. Your HVAC efficiency changes with outdoor temperature. The balance point is the temperature at which it is more efficient to run Auxiliary Cool instead of your main HVAC component."
			]
	]
	def recommendation = tips.getAt(tip)
	traceEvent(settings.logFilter,"get_recommendation_text>got ${tip}'s text from tips table", settings.trace)
	return recommendation
}

private void initialize_tips() {
	for (int i=1;i<=get_MAX_TIPS();i++) {    
		def attribute = "tip${i}Text"    
		sendEvent name: attribute, value: "",displayed: (settings.trace?:false)
		attribute = "tip${i}Level"
		sendEvent name: attribute, value: "",displayed: (settings.trace?:false)
		attribute = "tip${i}Solution"
		sendEvent name: attribute, value: "",displayed: (settings.trace?:false)
	}        

}

private boolean isWeekday() {
	Calendar myDate = Calendar.getInstance()
	int dow = myDate.get (Calendar.DAY_OF_WEEK)
	boolean isWeekday = ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY))
	return isWeekday
}

void resetTips() {
	traceEvent(settings.logFilter,"resetTips>begin with state?.tips=${state.tips}, about to reset the state variable",settings.trace)
	state?.tips=[] // reset the state variable which saves all tips previously given
}

void getTips(level=1) {
	if (state?.tips==null) {resetTips()}
	state?.currentTip=0
    
	def scale = state?.scale
	def tipsAlreadyGiven = (state?.tips != [])? state.tips: []
	def maxTips=get_MAX_TIPS()    
	traceEvent(settings.logFilter,"getTips>begin with level=$level",settings.trace)
	traceEvent(settings.logFilter,"getTips>tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)
	initialize_tips()   
	def recommendation=null
	def attribute    
	boolean isWeekday=isWeekday()    
    
	float MAX_DEVIATION_TEMPERATURE= (scale=='F')?0.6:0.3
	float MAX_DIFFERENCE_TEMPERATURE=(scale=='F')?3:1.5
	float MAX_DEVIATION_OUTDOOR_TEMP=(scale=='F')?5:2.5
	float MIN_DEVIATION_COOLING_SETPOINT= (scale=='F')?6:3
	float MIN_DEVIATION_HEATING_SETPOINT= (scale=='F')?6:3
	int MAX_HUMIDITY_LEVEL=55
	int HUMIDITY_DIFF_ALLOWED=3
	int MILLISECONDS_PER_HOUR=(60 * 60 * 1000)    
	int MAX_HOLD_THRESHOLD=3
	int MAX_HEATING_CYCLE=15
	int MAX_COOLING_CYCLE=15
    
	def countHeating=0, countCooling=0,countSleep=0,countAway=0,countHome=0,sleepDuration=0,awayDuration=0,countHolds=0
	def modelNumber=getModelNumber()
	def component

	Date endDate= new Date()
	Date startDate = endDate -1
	Date aWeekAgo=endDate-7
	if ((level == 1) ||  (level == 0)) {
    
		def minCoolingSetpoint,maxCoolingSetpoint
		def minHeatingSetpoint,maxHeatingSetpoint
		def coolingValuesSet=get_stats_attribute("coolingSetpoint",startDate,endDate,'values')
		def heatingValuesSet=get_stats_attribute("heatingSetpoint",startDate,endDate,'values')
		if (mode in ['cool','auto', 'off']) {    
//			avgCoolingSetpoint=get_stats_attribute("coolingSetpoint",startDate,endDate,'avg',true)
			countCooling=get_stats_attribute("thermostatOperatingState",startDate,endDate,'count',false,'cooling')
			minCoolingSetpoint=coolingValuesSet.min()    
			maxCoolingSetpoint=coolingValuesSet.max()    
		}        
		if (mode in ['heat','auto', 'off']) {
//			avgHeatingSetpoint=get_stats_attribute("heatingSetpoint",startDate,endDate,'avg',true)
			countHeating=get_stats_attribute("thermostatOperatingState",startDate,endDate,'count',false,'heating')
			minHeatingSetpoint=heatingValuesSet.min()    
			maxHeatingSetpoint=heatingValuesSet.max()    
		}    
		countHolds=get_stats_attribute("programScheduleName",startDate,endDate,'count',false,'hold')  
		countAway=get_stats_attribute("climateName",startDate,endDate,'count',false,'Away')  
		countSleep=get_stats_attribute("setClimate",startDate,endDate,'count',false,'Sleep')  
		countHome=get_stats_attribute("setClimate",startDate,endDate,'count',false,'Home')  
		def outdoorTemp = device.currentValue("weatherTemperature")
		int currentIndoorHum = device.currentValue("humidity")            
		def currentTemp = device.currentValue("temperature")            
    
		if (settings.trace) {    
			sendEvent  name: "verboseTrace", value:"getTips>currentTemp = $currentTemp"
			sendEvent  name: "verboseTrace", value:"getTips>currentIndoorHum = $currentIndoorHum"            
			sendEvent  name: "verboseTrace", value: "getTips>avgTemperature=$avgTemperature"    
			sendEvent  name: "verboseTrace", value:"getTips>outdoorTemp=$outdoorTemp"
			sendEvent  name: "verboseTrace", value: "getTips>devTemperature=$devTemperature"
			sendEvent  name: "verboseTrace", value: "getTips>countCooling=$countCooling"
			sendEvent  name: "verboseTrace", value: "getTips>countHeating=$countHeating"
			sendEvent  name: "verboseTrace", value: "getTips>countHolds=$countHolds"
			sendEvent  name: "verboseTrace",value: "get_tips>coolingSetPoint values=$coolingValuesSet"
			sendEvent  name: "verboseTrace",value: "get_tips>heatingSetPoint values=$heatingValuesSet"
			sendEvent  name: "verboseTrace",value: "getTips>min coolingSetpoint=$minCoolingSetpoint"
			sendEvent  name: "verboseTrace",value: "getTips>min heatingSetpoint=$minHeatingSetpoint"
			sendEvent  name: "verboseTrace",value: "getTips>max coolingSetpoint=$maxCoolingSetpoint"
			sendEvent  name: "verboseTrace",value: "getTips>max heatingSetpoint=$maxHeatingSetpoint"
			sendEvent  name: "verboseTrace", value: "getTips>countAway=$countAway"
			sendEvent  name: "verboseTrace", value: "getTips>countSleep=$countSleep"
		}   
		if (!tipsAlreadyGiven.contains("tip01")) {
			if (countHeating && !countSleep) {
				recommendation= get_recommendation('tip01')
				tipsAlreadyGiven.add('tip01')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: Your thermostat's minimum heating setpoint of ${minHeatingSetpoint} degrees could be decreased at Nights. Current tip is: " + recommendation.text
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip01,heating",displayed: (settings.trace?:false)
			}        
			if ((!tipsAlreadyGiven.contains("tip01")) && countCooling && !countSleep) {
				recommendation= get_recommendation('tip01')
				tipsAlreadyGiven.add('tip01')                
				state?.currentTip=state?.currentTip +1
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: Your thermostat's maximum cooling setpoint of ${maxCoolingSetpoint} degrees could be increased at Nights. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip01,cooling",displayed: (settings.trace?:false)
			}        
		} /* end of tip01 logic */
		if ((isWeekday) && (!tipsAlreadyGiven.contains("tip02"))) {
			if ((mode in ['heat','auto', 'off']) && countHeating && !countAway) {
				recommendation= get_recommendation('tip02')
				tipsAlreadyGiven.add('tip02')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: Your thermostat's minimum heating setpoint of ${minHeatingSetpoint} degrees could be decreased during workdays when away for work. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip02,heating",displayed: (settings.trace?:false)
			}        
			if (((!tipsAlreadyGiven.contains("tip02")) && countCooling) && !countAway) {
				recommendation= get_recommendation('tip02')
				tipsAlreadyGiven.add('tip02')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: Your thermostat's maximum cooling setpoint of ${maxCoolingSetpoint} degrees could be increased during workdays when away for work. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip02,cooling",displayed: (settings.trace?:false)
			}        
		} /* end of tip02 logic */
		if ((state?.currentTip < maxTips) && (data.thermostatCount > 1) && (!tipsAlreadyGiven.contains("tip03"))) {
			recommendation= get_recommendation('tip03')
			tipsAlreadyGiven.add('tip03')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: It seems that you have ${data.thermostatCount} ecobee thermostats at your location." + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip03",displayed: (settings.trace?:false)
		}	  
           
		if ((state?.currentTip < maxTips) && !tipsAlreadyGiven.contains("tip04") ) {
			if ((countHeating) && (countHeating.toInteger() >= MAX_HEATING_CYCLE)) {
				recommendation= get_recommendation('tip04')
				tipsAlreadyGiven.add('tip04')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: There were ${countHeating} heating cycles in the last 24 hours. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip04,heating",displayed: (settings.trace?:false)
			} 
			if ((state?.currentTip < maxTips) && ((countCooling) && (countCooling.toInteger() >= MAX_COOLING_CYCLE))) {
				recommendation= get_recommendation('tip04')
				tipsAlreadyGiven.add('tip04')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: There were ${countCooling} cooling cycles in the last 24 hours. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip04,cooling",displayed: (settings.trace?:false)
			}            
        
		} /* end of tip11 logic */
		if ((state?.currentTip < maxTips) && (countHolds >= MAX_HOLD_THRESHOLD) && (!tipsAlreadyGiven.contains("tip05"))) {
			recommendation= get_recommendation('tip05')
			tipsAlreadyGiven.add('tip05')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: Your thermostat has been on hold ${countHolds} times during the day.  Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip05",displayed: (settings.trace?:false)
		} /* end of tip05 logic */
		if ((state?.currentTip < maxTips) && (countHolds >= MAX_HOLD_THRESHOLD) && (!tipsAlreadyGiven.contains("tip06"))) {
			recommendation= get_recommendation('tip06')
			tipsAlreadyGiven.add('tip06')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: Your thermostat has been on hold ${countHolds} times during the day.  Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip06",displayed: (settings.trace?:false)
		} /* end of tip06 logic */
		state?.tips=tipsAlreadyGiven    
		traceEvent(settings.logFilter,"getTips>end Level 1 with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)
	} /* end of level 1 */    
	    

	if ((state?.currentTip < maxTips) && ((level == 2) ||  (level == 0))) {
		tipsAlreadyGiven=get_tips_level2()    
		state?.tips = tipsAlreadyGiven 
	}  
    
	if ((state?.currentTip < maxTips) && ((level == 3) ||  (level == 0))) {
		tipsAlreadyGiven=get_tips_level3()    
		state?.tips = tipsAlreadyGiven 
	}  
    
	if ((state?.currentTip < maxTips)  && ((level ==4) || (level ==0)) && ((heatStages>1) || (coolStages>1))) {
		tipsAlreadyGiven=get_tips_level4()    
		state?.tips = tipsAlreadyGiven 
	}    
	traceEvent(settings.logFilter,"getTips>end with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)

}

private def get_tips_level2() {
	traceEvent(settings.logFilter,"get_tips_level2>begin with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)
	def scale = state?.scale
	def tipsAlreadyGiven = (state?.tips != [])? state.tips: []
	def maxTips=get_MAX_TIPS()    
	def countHeating=0, countCooling=0,sleepDuration=0,awayDuration=0,countHolds=0
	def component

	def recommendation=null
	def attribute    
	boolean isWeekday=isWeekday()    
    
	float MAX_DEVIATION_TEMPERATURE= (scale=='F')?0.6:0.3
	float MAX_DIFFERENCE_TEMPERATURE=(scale=='F')?3:1.5
	int MAX_HEATING_CYCLE=15
	int MAX_COOLING_CYCLE=15
	int MAX_COOLING_MINUTES_TIME=600   
	int MAX_HEATING_MINUTES_TIME=300    
	float MAX_DEVIATION_OUTDOOR_TEMP=(scale=='F')?5:2.5
	int MILLISECONDS_PER_HOUR=(60 * 60 * 1000)    
	int MIN_USUAL_SLEEP_DURATION=7
	int MIN_USUAL_AWAY_DURATION=4 
	int MAX_USUAL_AWAY_DURATION=20
	float MINIMUM_FAN_RUNTIME=400    
	Date endDate= new Date()
	Date startDate = endDate -1
	Date aWeekAgo=endDate-7
    

	String mode = device.currentValue("thermostatMode")    
	int currentIndoorHum = device.currentValue("humidity")            
	def currentTemp = device.currentValue("temperature")            
	def outdoorTemp = device.currentValue("weatherTemperature")
	def minCoolingSetpoint,maxCoolingSetpoint
	def minHeatingSetpoint,maxHeatingSetpoint
	def coolingValuesSet=get_stats_attribute("coolingSetpoint",startDate,endDate,'values')
	def heatingValuesSet=get_stats_attribute("heatingSetpoint",startDate,endDate,'values')
    
	if (mode in ['cool','auto', 'off']) {    
		countCooling=get_stats_attribute("thermostatOperatingState",startDate,endDate,'count',false,'cooling')
		minCoolingSetpoint=coolingValuesSet.min()    
		maxCoolingSetpoint=coolingValuesSet.max()    
	}        
	if (mode in ['heat','auto', 'off']) {
		countHeating=get_stats_attribute("thermostatOperatingState",startDate,endDate,'count',false,'heating')
		minHeatingSetpoint=heatingValuesSet.min()    
		maxHeatingSetpoint=heatingValuesSet.max()    
	}    
	def awayDates=get_stats_attribute("setClimate",startDate,endDate,'dates',false,'Away')
	def homeDates=get_stats_attribute("setClimate",startDate,endDate,'dates',false,'Home')
	def sleepDates=get_stats_attribute("setClimate",startDate,endDate,'dates',false,'Sleep')
	def countAway=get_stats_attribute("climateName",startDate,endDate,'count',false,'Away')  
	def countSleep=get_stats_attribute("setClimate",startDate,endDate,'count',false,'Sleep')  
	def countHome=get_stats_attribute("setClimate",startDate,endDate,'count',false,'Home')  
	if (countHome && countSleep) {    
		def homeMaxDate=homeDates.max()
		def sleepMaxDate=sleepDates.max()
		sleepDuration=(((homeMaxDate.toLong() - sleepMaxDate.toLong() ).abs()) / MILLISECONDS_PER_HOUR).toFloat().round(1)    
	}
	if (countHome && countAway) {    
		def homeMaxDate=homeDates.max()
		def awayMaxDate=awayDates.max()
		awayDuration=(((homeMaxDate.toLong() - awayMaxDate.toLong()).abs()) / MILLISECONDS_PER_HOUR).toFloat().round(1)    
	}
	def remoteMinTemp = get_stats_attribute("remoteSensorMinTemp",startDate,endDate,'min')     
	def remoteMaxTemp = get_stats_attribute("remoteSensorMaxTemp",startDate,endDate,'max')     
	def remoteAvgTemp = get_stats_attribute("remoteSensorAvgTemp",startDate,endDate,'avg')
	def avgTemperature=get_stats_attribute("temperature",startDate,endDate,'avg')
	def devTemperature=get_stats_attribute("temperature",startDate,endDate,'deviation')
    
	if (settings.trace) {    
		sendEvent  name: "verboseTrace", value: "get_tips_level2>currentTemp = $currentTemp"
		sendEvent  name: "verboseTrace", value:"get_tips_level2>currentIndoorHum = $currentIndoorHum"            
		sendEvent  name: "verboseTrace", value:"get_tips_level2>outdoorTemp=$outdoorTemp"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>countCooling=$countCooling"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>countHeating=$countHeating"
		sendEvent  name: "verboseTrace",value: "get_tips_level2>coolingSetPoint values=$coolingValuesSet"
		sendEvent  name: "verboseTrace",value: "get_tips_level2>heatingSetPoint values=$heatingValuesSet"
		sendEvent  name: "verboseTrace",value: "get_tips_level2>min heatingSetpoint=$minHeatingSetpoint"
		sendEvent  name: "verboseTrace",value: "get_tips_level2>min coolingSetpoint=$minCoolingSetpoint"
		sendEvent  name: "verboseTrace",value: "get_tips_level2>max coolingSetpoint=$maxCoolingSetpoint"
		sendEvent  name: "verboseTrace",value: "get_tips_level2>max heatingSetpoint=$maxHeatingSetpoint"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>awayDates=$awayDates"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>sleepDates=$sleepDates"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>sleepDuration=$sleepDuration"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>awayDuration=$awayDuration"
		sendEvent  name: "verboseTrace", value: "get_tips_level2>homeDates=$homeDates"
		sendEvent  name: "verboseTrace",value:"get_tips_level2>remoteMinTemp=$remoteMinTemp"
		sendEvent  name: "verboseTrace",value:"get_tips_level2>remoteMaxTemp=$remoteMaxTemp"        	
		sendEvent  name: "verboseTrace",value:"get_tips_level2>remoteAvgTemp=$remoteAvgTemp" 
	}   
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip10")) && (countHeating && maxHeatingSetpoint && minHeatingSetpoing) && ((maxHeatingSetpoint.toFloat() -minHeatingSetpoint.toFloat()) <= MIN_DEVIATION_HEATING_SETPOINT)) {
		recommendation= get_recommendation('tip10')
		tipsAlreadyGiven.add('tip10')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: There is a not a big difference between your thermostat's minimum heating setpoint of ${minHeatingSetpoint} and your maximum heating setpoint of ${maxHeatingSetpoint} degrees in the last 24 hours. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip10,heating",displayed: (settings.trace?:false)
	}        
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip10")) && (countCooling && maxCoolingSetpoint && minCoolingSetpoing) && ((maxCoolingSetpoint.toFloat() -minCoolingSetpoint.toFloat()) <= MIN_DEVIATION_COOLING_SETPOINT)) {
		recommendation= get_recommendation('tip10')
		tipsAlreadyGiven.add('tip10')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: There is a not a big difference between your thermostat's maximum cooling setpoint of ${maxCoolingSetpoint} and your minimum cooling setpoint of ${minCoolingSetpoint} degrees in the last 24 hours. Current tip is: " + recommendation.text,
		displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip10,cooling",displayed: (settings.trace?:false)
	} /* end of tip10 logic */
        

	if (devTemperature.toFloat() >=MAX_DEVIATION_TEMPERATURE) {
		recommendation= get_recommendation('tip13')
		tipsAlreadyGiven.add('tip13')                 
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your average indoor temperature of ${avgTemperature} degrees has not been constant in the last 24 hours as the standard deviation is ${devTemperature} degrees. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)                
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		if ((!tipsAlreadyGiven.contains("tip13")) && (remoteMinTemp && remoteAvgTemp)) {
			if ((((remoteMinTemp.toFloat() - remoteAvgTemp.toFloat()).abs()) > MAX_DIFFERENCE_TEMPERATURE) ||
				(((remoteMaxTemp.toFloat() - remoteAvgTemp.toFloat()).abs()) > MAX_DIFFERENCE_TEMPERATURE)) {
				recommendation= get_recommendation('tip13')
				float delta_temp_max = (remoteMaxTemp.toFloat() - remoteAvgTemp.toFloat()).abs().round(1)                
				float delta_temp_min = (remoteMinTemp.toFloat() - remoteAvgTemp.toFloat()).abs().round(1)                
				tipsAlreadyGiven.add('tip13')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: Your average remote sensors' temperature is ${remoteAvgTemp} degrees, but their temperature varies quite a lot (+${delta_temp_max}/-${delta_temp_min} degrees) from room to room. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip13",displayed: (settings.trace?:false)
			} else if ((!tipsAlreadyGiven.contains("tip14")) && (remoteMinTemp && remoteAvgTemp)) {
				recommendation= get_recommendation('tip14')
				tipsAlreadyGiven.add('tip14')                
				state?.currentTip=state?.currentTip +1             
				attribute ="tip${state?.currentTip}Text"                
				sendEvent name: attribute, value: "Observation: The temperature at the thermostat varies quite significantly during the day as the standard deviation is ${devTemperature} degrees. Current tip is: " + recommendation.text,
					displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Level"                
				sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
				attribute ="tip${state?.currentTip}Solution"                
				sendEvent name: attribute, value: "tip14",displayed: (settings.trace?:false)
			} /* end tip 14 */
		}         
	}	
	if ((state?.currentTip < maxTips) && ((fanRuntime) && (fanRuntime < MINIMUM_FAN_RUNTIME)) &&  (!tipsAlreadyGiven.contains("tip15")) && (remoteMinTemp && remoteAvgTemp))  {
		float delta_temp_max = (remoteMaxTemp.toFloat() - remoteAvgTemp.toFloat()).abs().round(1)                
		float delta_temp_min = (remoteMinTemp.toFloat() - remoteAvgTemp.toFloat()).abs().round(1)                
		if ((delta_temp_max > MAX_DIFFERENCE_TEMPERATURE) ||
			(delta_temp_min > MAX_DIFFERENCE_TEMPERATURE)) {
			recommendation= get_recommendation('tip15')
			tipsAlreadyGiven.add('tip15')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: Your Fan Runtime was ${fanRuntime} minutes yesterday, and you have some wide temperature deltas (+${delta_temp_max}/-${delta_temp_min} degrees) vs. the average of ${remoteAvgTemp} degrees " + 
				"between all your remote sensors. Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip15",displayed: (settings.trace?:false)
		}  		  
	}  		  
     
	if ((state?.currentTip < maxTips) &&  (!tipsAlreadyGiven.contains("tip16")) && (sleepDuration) && (sleepDuration.toFloat() < MIN_USUAL_SLEEP_DURATION)) {
		recommendation= get_recommendation('tip16')
		tipsAlreadyGiven.add('tip16')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your Sleep program at ecobee was used for approximately ${sleepDuration} hour(s) " + 
			"which is below the usual sleep schedule of ${MIN_USUAL_SLEEP_DURATION} hours. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip16",displayed: (settings.trace?:false)
    
	}  	/* end of tip16 logic */  	  
	if ((isWeekday) && (state?.currentTip < maxTips) &&  (!tipsAlreadyGiven.contains("tip17")) && (awayDuration) && (awayDuration.toFloat() < MIN_USUAL_AWAY_DURATION)) {
		recommendation= get_recommendation('tip17')
		tipsAlreadyGiven.add('tip17')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your Away program at ecobee was used for approximately ${awayDuration} hour(s) in the last day " + 
			"which is below the usual Away schedule of ${MIN_USUAL_AWAY_DURATION} hours if you work 9-5. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip17",displayed: (settings.trace?:false)
	}  	/* end of tip17 logic */  	
        
	if ((state?.currentTip < maxTips) &&  (!tipsAlreadyGiven.contains("tip18")) && (awayDuration) && (awayDuration.toFloat() > MAX_USUAL_AWAY_DURATION)) {
		recommendation= get_recommendation('tip18')
		tipsAlreadyGiven.add('tip18')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your Away program at ecobee was used for approximately ${awayDuration} hour(s) in the last day " + 
			"which is above the usual Away schedule of ${MAX_USUAL_AWAY_DURATION} hours if you work 9-5. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip18",displayed: (settings.trace?:false)
	}  	/* end of tip18 logic */  	
        
	state?.tips=tipsAlreadyGiven    
	traceEvent(settings.logFilter,"getTips>end Level 2 with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace) 	     
	return tipsAlreadyGiven    

}   /* end getLevel 2 */  
 

private def get_tips_level3() {
	traceEvent(settings.logFilter,"get_tips_level3>begin with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)
	def scale = state?.scale
	def maxTips=get_MAX_TIPS()    
	def tipsAlreadyGiven = (state?.tips != [])? state.tips: []
	def recommendation=null
	def attribute,component    
	boolean isWeekday=isWeekday()    
    
	float MAX_DEVIATION_TEMPERATURE= (scale=='F')?0.6:0.3
	float MAX_DIFFERENCE_TEMPERATURE=(scale=='F')?3:1.5
	float MIN_DEVIATION_COOLING_SETPOINT= (scale=='F')?6:3
	float MIN_DEVIATION_HEATING_SETPOINT= (scale=='F')?6:3
	int MAX_HEATING_CYCLE=15
	int MAX_COOLING_CYCLE=15
	int MAX_COOLING_MINUTES_TIME=600    
	int MAX_HEATING_MINUTES_TIME=300    
	float MAX_DEVIATION_OUTDOOR_TEMP=(scale=='F')?5:2.5
	int MAX_HUMIDITY_LEVEL=55
	int HUMIDITY_DIFF_ALLOWED=3
	int MILLISECONDS_PER_HOUR=(60 * 60 * 1000)    
    
	Date endDate= new Date()
	Date startDate = endDate -1
	Date aWeekAgo=endDate-7
    
	String mode = device.currentValue("thermostatMode")    
	int currentIndoorHum = device.currentValue("humidity")            
	def currentTemp = device.currentValue("temperature")            
	def outdoorTemp = device.currentValue("weatherTemperature")
	def devOutdoorTemperature=get_stats_attribute("weatherTemperature",aWeekAgo,endDate,'deviation')
	def avgOutdoorTemperature=get_stats_attribute("weatherTemperature",aWeekAgo,endDate,'avg')
	int targetHumidity = find_ideal_indoor_humidity(outdoorTemp)
	int heatStages = device.currentValue("heatStages")?.toInteger()
	int coolStages = device.currentValue("coolStages")?.toInteger()
	def stage1HeatingDifferentialTemp= device.currentValue("stage1HeatingDifferentialTemp")
	def stage1HeatingDissipationTime= device.currentValue("stage1HeatingDissipationTime")
	def auxMaxOutdoorTemp=device.currentValue("auxMaxOutdoorTemp")

	def dailyHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeDaily")?.toFloat()
	def yesterdayHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeYesterday")?.toFloat()
	def avgWeeklyHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeAvgMonthly")?.toFloat()
	def dailyHeatingRuntimeAux2 = device.currentValue("auxHeat2RuntimeDaily")?.toFloat()
	def yesterdayHeatingRuntimeAux2 = device.currentValue("auxHeat2RuntimeYesterday")?.toFloat()
	def avgWeeklyHeatingRuntimeAux2  = device.currentValue("auxHeat2RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyHeatingRuntimeAux2  = device.currentValue("auxHeat2RuntimeAvgMonthly")?.toFloat()
	def dailyHeatingRuntimeAux3  = device.currentValue("auxHeat3RuntimeDaily")?.toFloat()
	def yesterdayHeatingRuntimeAux3  = device.currentValue("auxHeat3RuntimeYesterday")?.toFloat()
	def avgWeeklyHeatingRuntimeAux3 = device.currentValue("auxHeat3RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyHeatingRuntimeAux3 = device.currentValue("auxHeat3RuntimeAvgMonthly")?.toFloat()
	def dailyCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeDaily")?.toFloat()
	def yesterdayCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeYesterday")?.toFloat()
	def avgWeeklyCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeAvgMonthly")?.toFloat()
	def dailyCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeDaily")?.toFloat()
	def yesterdayCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeYesterday")?.toFloat()
	def avgWeeklyCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeAvgMonthly")?.toFloat()
	def fanRuntime = device.currentValue("fanRuntimeDaily")?.toFloat()
	def hasDehumidifier = (device.currentValue("hasDehumidifier")) ?: 'false'
	def hasHumidifier = (device.currentValue("hasHumidifier")) ?: 'false'
	def hasHrv = (device.currentValue("hasHrv")) ?: 'false'
	def hasErv = (device.currentValue("hasErv")) ?: 'false'
	float dailyCoolingRuntime =  (((dailyCoolingRuntimeCool1)?:0) + ((dailyCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float yesterdayCoolingRuntime = (((yesterdayCoolingRuntimeCool1)?:0) + ((yesterdayCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float avgWeeklyCoolingRuntime = (((avgWeeklyCoolingRuntimeCool1)?:0) + ((avgWeeklyCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float avgMonthlyCoolingRuntime = (((avgMonthlyCoolingRuntimeCool1)?:0) + ((avgMonthlyCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float dailyHeatingRuntime = (((dailyHeatingRuntimeAux1)?:0) + ((dailyHeatingRuntimeAux2)?:0) + ((dailyHeatingRuntimeAux3)?:0)).toFloat().round(1)
	float yesterdayHeatingRuntime = (((yesterdayHeatingRuntimeAux1)?:0) + ((yesterdayHeatingRuntimeAux2)?:0) + ((yesterdayHeatingRuntimeAux3)?:0)).toFloat().round(1)
	float avgWeeklyHeatingRuntime = (((avgWeeklyHeatingRuntimeAux1)?:0) + ((avgWeeklyHeatingRuntimeAux2)?:0) + 
		((avgWeeklyHeatingRuntimeAux3)?:0)).toFloat().round(1)
	float avgMonthlyHeatingRuntime = (((avgMonthlyHeatingRuntimeAux1)?:0) + ((avgMonthlyHeatingRuntimeAux2)?:0) + 
		((avgMonthlyHeatingRuntimeAux3)?:0)).toFloat().round(1)
        
	if (settings.trace) {        
		sendEvent  name: "verboseTrace", value: "get_tips_level3>avgOutdoorTemperature=$avgOutdoorTemperature"    
		sendEvent  name: "verboseTrace", value: "get_tips_level3>devOutdoorTemperature=$devOutdoorTemperature"    
		sendEvent  name: "verboseTrace",value:"get_tips_level3>targetHumidity=$targetHumidity"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>avgMonthlyCoolingRuntime=$avgMonthlyCoolingRuntime minutes"
		sendEvent  name: "verboseTrace", value: "get_tips_level3>avgWeeklyCoolingRuntime=$avgWeeklyCoolingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>avgMonthlyHeatingRuntime=$avgMonthlyHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value: "get_tips_level3>avgWeeklyHeatingRuntime=$avgWeeklyHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>yesterdayCoolingRuntime=$yesterdayCoolingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>yesterdayHeatingRuntime=$yesterdayHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>dailyCoolingRuntime=$dailyCoolingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>dailyHeatingRuntime=$dailyHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>fanRuntime=$fanRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>hasDehumidifier=$hasDehumidifier"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>hasHumidifier=$hasHumidifier"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>hasHrv=$hasHrv"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>hasErv=$hasErv"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>coolStages=$coolStages"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>heatStages=$heatStages"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>auxOutdoorTemp=$auxOutdoorTemp"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>stage1HeatingDifferentialTemp =$stage1HeatingDifferentialTemp"
		sendEvent  name: "verboseTrace",value:"get_tips_level3>stage1HeatingDissipationTime =$stage1HeatingDissipationTime"
	}
    
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip20")) && (currentIndoorHum > (targetHumidity + HUMIDITY_DIFF_ALLOWED))) {
		recommendation= get_recommendation('tip20')
		tipsAlreadyGiven.add('tip20')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your indoor humidity is currently ${currentIndoorHum}% " + 
			"which is well above your ideal indoor humidity of ${targetHumidity}% based on the current outdoor temperature of ${outdoorTemp} degrees. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip20",displayed: (settings.trace?:false)
 	}  		  
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip21")) && (hasdehumidifier !='true' && hasErv!='true' && hasHrv !='true') && (countCooling) && (currentIndoorHum > (targetHumidity + HUMIDITY_DIFF_ALLOWED))) { 
		recommendation= get_recommendation('tip21')
		tipsAlreadyGiven.add('tip21')                
		state?.currentTip=state?.currentTip +1            
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your indoor humidity is currently ${currentIndoorHum}% " + 
				"which is well above your ideal indoor humidity of ${targetHumidity}% based on the current outdoor temperature of ${outdoorTemp} degrees. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip21",displayed: (settings.trace?:false)
	}
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip22")) && (hasDehumidifier =='true') && (currentIndoorHum > (targetHumidity + HUMIDITY_DIFF_ALLOWED))) {
		recommendation= get_recommendation('tip22')
		tipsAlreadyGiven.add('tip22')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your indoor humidity is currently ${currentIndoorHum}% " + 
				"which is well above your ideal indoor humidity of ${targetHumidity}% based on the current outdoor temperature of ${outdoorTemp} degrees. Current tip is: " + recommendation.text
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip22",displayed: (settings.trace?:false)
    
	} else if ((state?.currentTip < maxTips) &&  (!tipsAlreadyGiven.contains("tip23")) && (hasErv=='true' || hasHrv =='true') && (countCooling) && (currentIndoorHum > (targetHumidity + HUMIDITY_DIFF_ALLOWED))) {
		recommendation= get_recommendation('tip23')
		tipsAlreadyGiven.add('tip23')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your indoor humidity is currently ${currentIndoorHum}% " + 
				"which is well above your ideal indoor humidity of ${targetHumidity}% based on the current outdoor temperature of ${outdoorTemp} degrees. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip23",displayed: (settings.trace?:false)
	}    
	if ((state?.currentTip < maxTips) &&  (!tipsAlreadyGiven.contains("tip24")) && (hasErv=='true' || hasHrv =='true') && (currentIndoorHum > (targetHumidity + HUMIDITY_DIFF_ALLOWED))) {
		recommendation= get_recommendation('tip24')
		tipsAlreadyGiven.add('tip24')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your indoor humidity is currently ${currentIndoorHum}% " + 
				"which is well above your ideal indoor humidity of ${targetHumidity}% based on the current outdoor temperature of ${outdoorTemp} degrees. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip24",displayed: (settings.trace?:false)
	}  	  /* end of tip24 logic */  	  
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip25")) && (heatStages>1)) { 
		recommendation= get_recommendation('tip25')
		tipsAlreadyGiven.add('tip25')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: You have a ${heatStages}-stage heating system and your auxMaxOutdoorTemp setting is ${auxMaxOutdoorTemp}." +  
			"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
             
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip25",displayed: (settings.trace?:false)

	} /* end of tip25 logic */  
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip26")) && (heatStages>1)) { 
		recommendation= get_recommendation('tip26')
		tipsAlreadyGiven.add('tip26')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		component = (heatStages==1)? "Furnace" : "${heatStages}-stage Furnace"  			                
		sendEvent name: attribute, value: "Observation: You have a ${heatStages}-stage heating system. Here are some of your current settings for auxHeat: stage1HeatingDifferentialTemp=${stage1HeatingDifferentialTemp} " +  
			"which is the difference between current temperature and set-point that will trigger stage-2 heating and stage1HeatingDissipationTime=${stage1HeatingDissipationTime} seconds, which is the time after a heating cycle that the fan will run for to extract any heating left in the system - 30 second default." + 
			"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip26",displayed: (settings.trace?:false)

	} /* end of tip26 logic */  
	if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip27")) && (hasHumidifier=='true') && 
		(currentIndoorHum < (targetHumidity -HUMIDITY_DIFF_ALLOWED))) {	        
		recommendation= get_recommendation('tip27')
		tipsAlreadyGiven.add('tip27')                
		state?.currentTip=state?.currentTip +1             
		attribute ="tip${state?.currentTip}Text"                
		sendEvent name: attribute, value: "Observation: Your indoor humidity is currently ${currentIndoorHum}% " + 
				"which is well below your ideal indoor humidity of ${targetHumidity}% based on the current outdoor temperature of ${outdoorTemp} degrees. Current tip is: " + recommendation.text,
			displayed: (settings.trace?:false)                
		attribute ="tip${state?.currentTip}Level"                
		sendEvent name: attribute, value: recommendation.level,	displayed: (settings.trace?:false)                
		attribute ="tip${state?.currentTip}Solution"                
		sendEvent name: attribute, value: "tip27",displayed: (settings.trace?:false)
	}    /* end of tip27 logic */   
    
	if ((state?.currentTip < maxTips) && (devOutdoorTemperature.toFloat() <= MAX_DEVIATION_OUTDOOR_TEMP) && (dailyCoolingRuntime)) {
		if ((!tipsAlreadyGiven.contains("tip30")) && ((yesterdayCoolingRuntime && avgWeeklyCoolingRuntime) && 
 			(dailyCoolingRuntime > yesterdayCoolingRuntime  ) ||
			(dailyCoolingRuntime> MAX_COOLING_MINUTES_TIME) ||         
			(dailyCoolingRuntime > avgWeeklyCoolingRuntime))) {
			recommendation= get_recommendation('tip30')
			tipsAlreadyGiven.add('tip30')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (coolStages==1)? "A/C" : "${coolStages}-stage A/C"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyCoolingRuntime} minutes yesterday which is higher than the day before " + 
				"or your A/C runs more than ${MAX_COOLING_MINUTES_TIME/60} hours a day or your daily A/C Runtime is higher than the average weekly Runtime of ${avgWeeklyCoolingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip30,cooling",displayed: (settings.trace?:false)
		}
		if ((!tipsAlreadyGiven.contains("tip30")) && ((yesterdayHeatingRuntime && avgWeeklyHeatingRuntime) && 
 			(dailyHeatingRuntime > yesterdayHeatingRuntime  ) ||
			(dailyHeatingRuntime> MAX_HEATING_MINUTES_TIME) ||         
			(dailyHeatingRuntime > avgWeeklyHeatingRuntime))) {
			recommendation= get_recommendation('tip30')
			tipsAlreadyGiven.add('tip30')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages==1)? "Furnace" : "${heatStages}-stage Furnace"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyHeatingRuntime} minutes yesterday which is higher than the day before " + 
				"or your Furnace runs more than ${MAX_HEATING_MINUTES_TIME/60} hours a day or or your daily Furnace Runtime is higher than the average weekly Runtime of ${avgWeeklyHeatingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip30,heating",displayed: (settings.trace?:false)
		}
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip31")) && ((yesterdayCoolingRuntime) && 
 			(dailyCoolingRuntime  > yesterdayCoolingRuntime )) ||
			(dailyCoolingRuntime > MAX_COOLING_MINUTES_TIME)) {        
			recommendation= get_recommendation('tip31')
			tipsAlreadyGiven.add('tip31')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (coolStages==1)? "A/C" : "${coolStages}-stage A/C"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyCoolingRuntime} minutes yesterday which is higher than the day before " + 
				"or your A/C runs more than ${MAX_COOLING_MINUTES_TIME/60} hours a day. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip31,cooling",displayed: (settings.trace?:false)
		}              
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip31")) && ((yesterdayHeatingRuntime) &&  
			(dailyHeatingRuntime  > yesterdayHeatingRuntime )) ||         
			 (dailyHeatingRuntime > MAX_HEATING_MINUTES_TIME)) {        
			recommendation= get_recommendation('tip31')
			tipsAlreadyGiven.add('tip31')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages==1)? "Furnace" : "${heatStages}-stage Furnace"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyHeatingRuntime} minutes yesterday which is higher than the day before " + 
				"or your Furnace runs more than ${MAX_HEATING_MINUTES_TIME/60} hours a day. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip31,heating",displayed: (settings.trace?:false)
		} /* end of tip31 logic */       
		if ((state?.currentTip < maxTips) &&  (!tipsAlreadyGiven.contains("tip31")) && ((avgWeeklyHeatingRuntime) && 
			(dailyHeatingRuntime > avgWeeklyHeatingRuntime))) {
			recommendation= get_recommendation('tip31')
			tipsAlreadyGiven.add('tip31')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages==1)? "Furnace" : "${heatStages}-stage Furnace"  			                
			sendEvent name: attribute, value: "Observation:The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyHeatingRuntime} minutes yesterday which is higher than " + 
				"the weekly average Runtime of ${avgWeeklyHeatingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip31,heating",displayed: (settings.trace?:false)
		} /* end of tip31 logic */       
		if ((state?.currentTip < maxTips) && ((avgWeeklyCoolingRuntime) &&
			(dailyCoolingRuntime > avgWeeklyCoolingRuntime))) {
			recommendation= get_recommendation('tip31')
			tipsAlreadyGiven.add('tip31')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (coolStages==1)? "A/C" : "${coolStages}-stage A/C"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyCoolingRuntime} minutes yesterday which is higher than " + 
				"the weekly average Runtime of ${avgWeeklyCoolingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip31,cooling",displayed: (settings.trace?:false)
		} /* end of tip31 logic */       
		if ((state?.currentTip < maxTips) && ((avgMonthlyCoolingRuntime) && 
			(dailyCoolingRuntime > avgMonthlyCoolingRuntime))) {
			recommendation= get_recommendation('tip31')
			tipsAlreadyGiven.add('tip31')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (coolStages==1)?"A/C": "A/C stage ${coolStages}"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyCoolingRuntime} minutes yesterday which is higher than " + 
				"the monthly average Runtime of ${avgMonthlyCoolingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip31,cooling",displayed: (settings.trace?:false)
		} /* end of tip31 logic */       
		if ((state?.currentTip < maxTips) && ((avgMonthlyHeatingRuntime) && 
			(dailyHeatingRuntime > avgMonthlyHeatingRuntime))) {
			recommendation= get_recommendation('tip31')
			tipsAlreadyGiven.add('tip31')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages==1)?"Furnace": "Furnace stage ${heatStages}"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyHeatingRuntime} minutes yesterday which is higher than " + 
				"the monthly average runtime of ${avgMonthlyHeatingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
                
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip31,heating",displayed: (settings.trace?:false)
		} /* end of tip31 logic */       
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip32")) && ((yesterdayCoolingRuntime && avgWeeklyCoolingRuntime) && 
 			(dailyCoolingRuntime > yesterdayCoolingRuntime  ) ||
			(dailyCoolingRuntime> MAX_COOLING_MINUTES_TIME) ||         
			(dailyCoolingRuntime > avgWeeklyCoolingRuntime))) {
			recommendation= get_recommendation('tip32')
			tipsAlreadyGiven.add('tip32')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (coolStages==1)? "A/C" : "${coolStages}-stage A/C"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature has been ${avgOutdoorTemperature} degrees in the past week with a standard deviation of ${devOutdoorTemperature}. Your ${component} Runtime was ${dailyCoolingRuntime} minutes yesterday which is higher than the day before " + 
				"or your A/C runs more than ${MAX_COOLING_MINUTES_TIME/60} hours a day or your daily A/C Runtime is higher than the average weekly Runtime of ${avgWeeklyCoolingRuntime} minutes. Current tip is: " + recommendation.text,
				displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip32",displayed: (settings.trace?:false)
		}
	}        
	traceEvent(settings.logFilter,"get_tips_level3>end with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)    
	return tipsAlreadyGiven    
}   /* end getLevel 3 */  

private def get_tips_level4() {
	traceEvent(settings.logFilter,"get_tips_level4>begin with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)
	def tipsAlreadyGiven = (state?.tips != [])? state.tips: []
	def scale = state?.scale
	def maxTips=get_MAX_TIPS()    
	def recommendation=null
	def attribute,component   
	boolean isWeekday=isWeekday()    
/*
	def MAX_DEVIATION_TEMPERATURE= (scale=='F')?3:1.5
*/    
	float MAX_DEVIATION_TEMPERATURE= (scale=='F')?0.6:0.3
	float MAX_DIFFERENCE_TEMPERATURE=(scale=='F')?3:1.5
	float MIN_DEVIATION_COOLING_SETPOINT= (scale=='F')?6:3
	float MIN_DEVIATION_HEATING_SETPOINT= (scale=='F')?6:3
	int MAX_HEATING_CYCLE=10
	int MAX_COOLING_CYCLE=10
	int MAX_COOLING_MINUTES_TIME=600    
	int MAX_HEATING_MINUTES_TIME=300    
	float MAX_DEVIATION_OUTDOOR_TEMP=(scale=='F')?5:3
	int MAX_HUMIDITY_LEVEL=55
	int HUMIDITY_DIFF_ALLOWED=5
	int MILLISECONDS_PER_HOUR=(60 * 60 * 1000)    
	float RATIO_MIN_AUX_HEAT= 80
	float RATIO_MIN_AUX_COOL= 80
	float RATIO_MIN_AUX_COOL_FOR_HIGH_TEMP=60   
	float RATIO_MIN_AUX_HEAT_FOR_LOW_TEMP=50
	float LOW_TEMP_THRESHOLD_FOR_HEATING=(scale=='F')?15:-10    
	float HIGH_TEMP_THRESHOLD_FOR_COOLING=(scale=='F')?80:26    
    
	Date endDate= new Date()
	Date startDate = endDate -1
	Date aWeekAgo=endDate-7
	int heatStages = device.currentValue("heatStages")?.toInteger()
	int coolStages = device.currentValue("coolStages")?.toInteger()
    
 	def devOutdoorTemperature=get_stats_attribute("weatherTemperature",aWeekAgo,endDate,'deviation')
	def avgOutdoorTemperature=get_stats_attribute("weatherTemperature",aWeekAgo,endDate,'avg')
	def dailyHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeDaily")?.toFloat()
	def yesterdayHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeYesterday")?.toFloat()
	def avgWeeklyHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyHeatingRuntimeAux1 = device.currentValue("auxHeat1RuntimeAvgMonthly")?.toFloat()
	def dailyHeatingRuntimeAux2 = device.currentValue("auxHeat2RuntimeDaily")?.toFloat()
	def yesterdayHeatingRuntimeAux2 = device.currentValue("auxHeat2RuntimeYesterday")?.toFloat()
	def avgWeeklyHeatingRuntimeAux2  = device.currentValue("auxHeat2RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyHeatingRuntimeAux2  = device.currentValue("auxHeat2RuntimeAvgMonthly")?.toFloat()
	def dailyHeatingRuntimeAux3  = device.currentValue("auxHeat3RuntimeDaily")?.toFloat()
	def yesterdayHeatingRuntimeAux3  = device.currentValue("auxHeat3RuntimeYesterday")?.toFloat()
	def avgWeeklyHeatingRuntimeAux3 = device.currentValue("auxHeat3RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyHeatingRuntimeAux3 = device.currentValue("auxHeat3RuntimeAvgMonthly")?.toFloat()
	def dailyCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeDaily")?.toFloat()
	def yesterdayCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeYesterday")?.toFloat()
	def avgWeeklyCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyCoolingRuntimeCool1 = device.currentValue("compCool1RuntimeAvgMonthly")?.toFloat()
	def dailyCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeDaily")?.toFloat()
	def yesterdayCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeYesterday")?.toFloat()
	def avgWeeklyCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeAvgWeekly")?.toFloat()
	def avgMonthlyCoolingRuntimeCool2 = device.currentValue("compCool2RuntimeAvgMonthly")?.toFloat()

	float dailyCoolingRuntime =  (((dailyCoolingRuntimeCool1)?:0) + ((dailyCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float yesterdayCoolingRuntime = (((yesterdayCoolingRuntimeCool1)?:0) + ((yesterdayCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float avgWeeklyCoolingRuntime = (((avgWeeklyCoolingRuntimeCool1)?:0) + ((avgWeeklyCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float avgMonthlyCoolingRuntime = (((avgMonthlyCoolingRuntimeCool1)?:0) + ((avgMonthlyCoolingRuntimeCool2)?:0)).toFloat().round(1)
	float dailyHeatingRuntime = (((dailyHeatingRuntimeAux1)?:0) + ((dailyHeatingRuntimeAux2)?:0) + ((dailyHeatingRuntimeAux3)?:0)).toFloat().round(1)
	float yesterdayHeatingRuntime = (((yesterdayHeatingRuntimeAux1)?:0) + ((yesterdayHeatingRuntimeAux2)?:0) + ((yesterdayHeatingRuntimeAux3)?:0)).toFloat().round(1)
	float avgWeeklyHeatingRuntime = (((avgWeeklyHeatingRuntimeAux1)?:0) + ((avgWeeklyHeatingRuntimeAux2)?:0) + 
		((avgWeeklyHeatingRuntimeAux3)?:0)).toFloat().round(1)
	float avgMonthlyHeatingRuntime = (((avgMonthlyHeatingRuntimeAux1)?:0) + ((avgMonthlyHeatingRuntimeAux2)?:0) + 
		((avgMonthlyHeatingRuntimeAux3)?:0)).toFloat().round(1)

	if (settings.trace) {
		sendEvent  name: "verboseTrace",value:"get_tips_level4>avgMonthlyHeatingRuntime=$avgMonthlyHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value: "get_tips_level4>avgWeeklyHeatingRuntime=$avgWeeklyHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level4>yesterdayHeatingRuntime=$yesterdayHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level4>dailyHeatingRuntime=$dailyHeatingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level4>avgMonthlyCoolingRuntime=$avgMonthlyCoolingRuntime minutes"
		sendEvent  name: "verboseTrace", value: "get_tips_level4>avgWeeklyCoolingRuntime=$avgWeeklyCoolingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level4>yesterdayCoolingRuntime=$yesterdayCoolingRuntime minutes"
		sendEvent  name: "verboseTrace",value:"get_tips_level4>dailyCoolingRuntime=$dailyCoolingRuntime minutes"
	}
	if (heatStages>1) { 
		traceEvent(settings.logFilter,"get_tips_level4>About to calculate Aux Heating ratios",settings.trace)
		float heatAuxRuntimeDaily=0,heatAuxRuntimeYesterday=0,heatAuxRuntimeAvgWeekly=0,heatAuxRuntimeAvgMonthly=0         
		heatAuxRuntimeDaily= (((dailyHeatingRuntimeAux1)?:0) + ((heatStages==3)? dailyHeatingRuntimeAux2:0)).round(1)
		heatAuxRuntimeYesterday=(((yesterdayHeatingRuntimeAux1)?:0) + ((heatStages==3)?yesterdayHeatingRuntimeAux2:0)).round(1)
		heatAuxRuntimeAvgWeekly=(((avgWeeklyHeatingRuntimeAux1)?:0) + ((heatStages==3)?avgWeeklyHeatingRuntimeAux2:0)).round(1)
		heatAuxRuntimeAvgMonthly=(((avgMonthlyAvgHeatingRuntimeAux1)?:0) + ((heatStages==3)?avgMonthlyHeatingRuntimeAux2:0)).round(1)
		if (settings.trace) {
			sendEvent  name: "verboseTrace",value:"get_tips_level4>heatAuxRuntimeDaily=$heatAuxRuntimeDaily minutes"
			sendEvent  name: "verboseTrace", value: "get_tips_level4>heatAuxRuntimeYesterday=$heatAuxRuntimeYesterday minutes"
			sendEvent  name: "verboseTrace",value:"get_tips_level4>heatAuxRuntimeAvgWeekly=$heatAuxRuntimeAvgWeekly minutes"
			sendEvent  name: "verboseTrace",value:"get_tips_level4>heatAuxRuntimeAvgMonthly=$heatAuxRuntimeAvgMonthly minutes"
		}        
		float ratioAuxDaily=((heatAuxRuntimeDaily/dailyHeatingRuntime)*100).round(1)
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxHeatDaily=$ratioAuxDaily %",settings.trace)
		float ratioAuxYesterday=((heatAuxRuntimeYesterday/yesterdayHeatingRuntime)*100).round(1)
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxHeatYesterday=$ratioAuxYesterday %",settings.trace)
		float ratioAuxWeekly=((heatAuxRuntimeWeekly/avgWeeklyHeatingRuntime)*100).round(1)
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxHeatWeekly=$ratioAuxWeekly %",settings.trace)
		float ratioAuxMonthly=((heatAuxRuntimeMonthly/avgMonthlyHeatingRuntime)*100).round(1)
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxHeatMonthly=$ratioAuxMonthly %",settings.trace)
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip40")) &&  (dailyHeatingRuntime) && (((avgOutdoorTemperature > LOW_TEMP_THRESHOLD_FOR_HEATING)) &&
			(ratioAuxDaily < RATIO_MIN_AUX_HEAT)) || (((avgOutdoorTemperature <=  LOW_TEMP_THRESHOLD_FOR_HEATING)) &&
			(ratioAuxDaily < RATIO_MIN_AUX_HEAT_FOR_LOW_TEMP))) {            
			recommendation= get_recommendation('tip40')
			tipsAlreadyGiven.add('tip40')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages>2)? "Aux Heat Stage 1 and 2" : "Aux Heat Stage 1"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees. "+
				"You have a ${heatStages}-stage heating system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the Total Heating Runtime is lower than ${RATIO_MIN_AUX_HEAT}%" +
				"Yesterday's Auxiliary Heat Runtime is ${heatAuxRuntimeDaily} versus the total Heating Runtime of ${dailyHeatingRuntime} minutes." +  
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip40",displayed: (settings.trace?:false)
		} /* end of tip40 logic */                   
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip41")) &&  (yesterdayHeatingRuntime) && (ratioAuxDaily < ratioAuxYesterday)) {            
			recommendation= get_recommendation('tip41')
			tipsAlreadyGiven.add('tip41')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages>2)? "Aux Heat Stage 1 and 2" : "Aux Heat Stage 1"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${heatStages}-stage heating system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the Total Heating Runtime is lower than the day before's ratio of ${ratioAuxYesterday}%." +  
				"Yesterday's Auxiliary Heat Runtime is ${heatAuxRuntimeDaily} versus the day before's Heat Aux Runtime of ${heatAuxRuntimeYesterday} minutes." +
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip41",displayed: (settings.trace?:false)
		} /* end of tip41 logic */                   
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip42")) &&  (avgWeeklyHeatingRuntime) && (ratioAuxDaily < ratioAuxWeekly)) {            
			recommendation= get_recommendation('tip42')
			tipsAlreadyGiven.add('tip42')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages>2)? "Aux Heat Stage 1 and 2" : "Aux Heat Stage 1"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${heatStages}-stage heating system and the yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the Total Heating Runtime is lower than the weekly average ratio of ${ratioAuxWeekly}%." +  
				"Yesterday's Auxiliary Heat Runtime is ${heatAuxRuntimeDaily} versus the weekly Average Auxiliary Heat Runtime of ${heatAuxRuntimeWeekly} minutes." +
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip42",displayed: (settings.trace?:false)
		} /* end of tip42 logic */                   
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip43")) &&  (avgMonthlyHeatingRuntime) && (ratioAuxDaily < ratioAuxMonthly)) {            
			recommendation= get_recommendation('tip43')
			tipsAlreadyGiven.add('tip43')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			component = (heatStages>2)? "Aux Heat Stage 1 and 2" : "Aux Heat Stage 1"  			                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${heatStages}-stage heating system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the Total Heating Runtime is lower than the monthly average ratio of ${ratioAuxMonthly}%." +  
				"Yesterday's Auxiliary Heat Runtime is ${heatAuxRuntimeDaily} versus the monthly Average Auxiliary Heat Runtime of ${heatAuxRuntimeMonthly} minutes." +
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip43",displayed: (settings.trace?:false)
		} /* end of tip43 logic */       
	} /* end if heatStages */        

	
	if (coolStages>1) { 
		traceEvent(settings.logFilter,"get_tips_level4>About to calculate Aux Cooling ratios",settings.trace)
		float ratioAuxDaily=0,ratioAuxYesterday=0,ratioAuxWeekly=0,ratioAuxMonthly=0        
		if (dailyCoolingRuntimeCool1) {
			ratioAuxDaily=((dailyCoolingRuntimeCool1/dailyCoolingRuntime)*100).round(1)
		}            
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxCoolDaily=$ratioAuxDaily %",settings.trace)
		if (yesterdayCoolingRuntimeCool1) {
			ratioAuxYesterday=((yesterdayCoolingRuntimeCool1/yesterdayCoolingRuntime)*100).round(1)
		}            
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxCoolYesterday=$ratioAuxYesterday %",settings.trace)
		if (avgWeeklyCoolingRuntimeCool1) {
			ratioAuxWeekly=((avgWeeklyCoolingRuntimeCool1/avgWeeklyCoolingRuntime)*100).round(1)
		}            
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxCoolWeekly=$ratioAuxWeekly %",settings.trace)
		if (avgWeeklyCoolingRuntimeCool1) {
			ratioAuxMonthly=((avgWeeklyCoolingRuntimeCool1/avgMonthlyCoolingRuntime)*100).round(1)
		}            
		traceEvent(settings.logFilter,"get_tips_level4>ratioAuxCoolMonthly=$ratioAuxMonthly %",settings.trace)

		component = "Aux Cool Stage 1"  			                
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip44")) &&  (dailyCoolingRuntime) && (((avgOutdoorTemperature > HIGH_TEMP_THRESHOLD_FOR_COOLING)) &&
			(ratioAuxDaily < RATIO_MIN_AUX_COOL)) || (((avgOutdoorTemperature >=  HIGH_TEMP_THRESHOLD_FOR_COOLING)) &&
			(ratioAuxDaily < RATIO_MIN_AUX_COOL_FOR_HIGH_TEMP))) {             
			recommendation= get_recommendation('tip44')
			tipsAlreadyGiven.add('tip44')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${coolStages}-stage cooling system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the Total Cooling Runtime is lower than ${RATIO_MIN_AUX_COOL}%." +  
				"Yesterday's Auxiliary Cool Runtime is ${dailyCoolingRuntimeCool1} versus the total Cooling Runtime of ${dailyCoolingRuntime} minutes." +  
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip44",displayed: (settings.trace?:false)
		} /* end of tip44 logic */                   
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip45")) && (yesterdayCoolingRuntime) && (ratioAuxDaily < ratioAuxYesterday)) {            
			recommendation= get_recommendation('tip45')
			tipsAlreadyGiven.add('tip45')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${coolStages}-stage cooling system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the total Cooling Runtime is lower than the day before's ratio of ${ratioAuxYesterday}%." +  
				"Yesterday's Auxiliary Cool Runtime is ${dailyCoolingRuntimeCool1} versus the day before's Auxiliary Cooling Runtime of ${yesterdayCoolingRuntimeCool1} minutes." +  
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip45",displayed: (settings.trace?:false)
		} /* end of tip45 logic */                   
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip46")) &&  (avgWeeklyCoolingRuntime) && (ratioAuxDaily < ratioAuxWeekly)) {            
			recommendation= get_recommendation('tip46')
			tipsAlreadyGiven.add('tip46')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${coolStages}-stage cooling system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the total Cooling Time is lower than the weekly average ratio of ${ratioAuxWeekly}%." +  
				"Yesterday's Auxiliary Cool Runtime is ${dailyCoolingRuntimeCool1} versus the weekly Average Auxiliary Cooling Runtime of ${avgWeeklyCoolingRuntime} minutes." +  
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip46",displayed: (settings.trace?:false)
		} /* end of tip46 logic */                   
		if ((state?.currentTip < maxTips) && (!tipsAlreadyGiven.contains("tip47")) &&  (avgMonthlyCoolingRuntime) && (ratioAuxDaily < ratioAuxMonthly)) {            
			recommendation= get_recommendation('tip47')
			tipsAlreadyGiven.add('tip47')                
			state?.currentTip=state?.currentTip +1             
			attribute ="tip${state?.currentTip}Text"                
			sendEvent name: attribute, value: "Observation: The average outdoor temperature in the past week has been ${avgOutdoorTemperature} degrees with a standard deviation of ${devOutdoorTemperature} degrees." +
				"You have a ${coolStages}-stage cooling system and yesterday's ${component} ratio of ${ratioAuxDaily}% usage versus the total Cooling Runtime is lower than the monthly average ratio of ${ratioAuxMonthly}%." +  
				"Yesterday's Auxiliary Cool Runtime is ${dailyCoolingRuntimeCool1} versus the monthly average Auxiliary Cooling Runtime of ${avgMonthlyCoolingRuntime} minutes." +  
				"Current tip is: " + recommendation.text,displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Level"                
			sendEvent name: attribute, value: recommendation.level, displayed: (settings.trace?:false)
			attribute ="tip${state?.currentTip}Solution"                
			sendEvent name: attribute, value: "tip47",displayed: (settings.trace?:false)
		} /* end of tip47 logic */                   
        
	}    
	traceEvent(settings.logFilter,"get_tips_level4>end with tipsAlreadyGiven=$tipsAlreadyGiven,state.tips=${state?.tips}",settings.trace)
	return tipsAlreadyGiven    
}   /* end getLevel 4 */  

private int find_ideal_indoor_humidity(outsideTemp) {
	def scale = state?.scale
	float outdoorTemp=(scale=='C')?outsideTemp.toFloat():fToC(outsideTemp.toFloat())	
	// -30C => 30%, at 0C => 45%

	int targetHum = 45 + (0.5 * outdoorTemp)
	return (Math.max(Math.min(targetHum, 60), 30))
}

private def toQueryString(Map m) {
	return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

private def cToF(temp) {
	return (temp * 1.8 + 32)
}
private def fToC(temp) {
	return (temp - 32).toFloat() / 1.8
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

private def get_MAX_TIPS() {
	return 5
}

private def getCustomImagePath() {
	return "http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/"
}

private def formatDate(dateString) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz")
	Date aDate = sdf.parse(dateString)
	return aDate
}

def retrieveDataForGraph() {
	def scale = state?.scale
	Date todayDate = new Date()
	def todayDay = new Date().format("dd",location.timeZone)
	String mode = device.currentValue("thermostatMode")    
	String todayInLocalTime = todayDate.format("yyyy-MM-dd", location.timeZone)
	String timezone = new Date().format("zzz", location.timeZone)
	String todayAtMidnight = todayInLocalTime + " 00:00 " + timezone
	Date startOfToday = formatDate(todayAtMidnight)
	Date startOfWeek = startOfToday -7
	def MIN_DEVIATION_TEMP=(scale=='C'?1:2)    
    
	traceEvent(settings.logFilter,"today at Midnight in local time= ${todayAtMidnight}",settings.trace)
	def temperatureTable = []
	def tempData = device.statesSince("weatherTemperature", startOfWeek,[max:200])
	def previousValue,maxInd=tempData?.size()-1    
	for (int i=maxInd; (i>=0);i--) {
    	if (i !=maxInd) previousValue = tempData[i+1]?.floatValue
		// filter some values        
		if ((i==0) || (i==maxInd) || ((tempData[i].floatValue <= (previousValue - MIN_DEVIATION_TEMP)) || (tempData[i].floatValue >= (previousValue + MIN_DEVIATION_TEMP)) )) {
			temperatureTable.add([tempData[i].date.getTime(),tempData[i].floatValue])
		}
	}
	def heatingSetpointTable = []
	def coolingSetpointTable = []
	def heatingSetpointData = device.statesSince("heatingSetpoint", startOfWeek, [max:100])
	def coolingSetpointData = device.statesSince("coolingSetpoint", startOfWeek, [max:100])
	previousValue=null
	maxInd=heatingSetpointData?.size()-1    
	for (int i=maxInd; (i>=0);i--) {
		// filter some values        
    	if (i !=maxInd) previousValue = heatingSetpointData[i+1]?.floatValue
		if ((i==0) || (i==maxInd) || ((heatingSetpointData[i]?.floatValue <= (previousValue - MIN_DEVIATION_TEMP)) || (heatingSetpointData[i]?.floatValue >= (previousValue + MIN_DEVIATION_TEMP)) )) {
			heatingSetpointTable.add([heatingSetpointData[i].date.getTime(),heatingSetpointData[i].floatValue])
		}		           
	}
	previousValue=null
	maxInd=coolingSetpointData?.size()-1    
	for (int i=maxInd; (i>=0);i--) {
    	if (i !=maxInd) previousValue = coolingSetpointData[i+1]?.floatValue
		// filter some values        
		if ((i==0) || (i==maxInd) || ((coolingSetpointData[i]?.floatValue <= (previousValue - MIN_DEVIATION_TEMP)) || (coolingSetpointData[i]?.floatValue >= (previousValue + MIN_DEVIATION_TEMP)) )) {
 			coolingSetpointTable.add([coolingSetpointData[i].date.getTime(),coolingSetpointData[i].floatValue])
		}            
	}
	if (heatingSetpointTable == []) { // if heatingSetpoint has not changed for a week
		def currentHeatingSetpoint=device.currentValue("heatingSetpoint")
		heatingSetpointTable.add([startOfWeek.getTime(),currentHeatingSetpoint])		        
		heatingSetpointTable.add([todayDate.getTime(),currentHeatingSetpoint])		        
	} else {
		def currentHeatingSetpoint=device.currentValue("heatingSetpoint")
		heatingSetpointTable.add([todayDate.getTime(),currentHeatingSetpoint])		        
	}    
 	if (coolingSetpointTable == []) {  // if coolingSetpoint has not changed for a week
		def currentCoolingSetpoint=device.currentValue("coolingSetpoint")
		coolingSetpointTable.add([startOfWeek.getTime(),currentCoolingSetpoint])		        
		coolingSetpointTable.add([todayDate.getTime(),currentCoolingSetpoint])		        
	} else {
		def currentCoolingSetpoint=device.currentValue("coolingSetpoint")
		coolingSetpointTable.add([todayDate.getTime(),currentCoolingSetpoint])		        
	}    
 	if (temperatureTable == []) {  // if coolingSetpoint has not changed for a week
		def currentTemperature=device.currentValue("weatherTemperature")
		temperatureTable.add([todayDate.getTime(),currentTemperature])		        
	}
    
	if (mode=='auto') {    
		float median = ((device.currentValue("coolingSetpoint") + device.currentValue("heatingSetpoint"))?.toFloat()) /2
		float currentTempAtTstat = device.currentValue("temperature")?.toFloat()        
		if (currentTempAtTstat> median) {
			mode='cool'
		} else {
			mode='heat'
		}   
	}
	if (((!state?.today) || (state?.today != todayDay)) || (state?.currentMode != mode)) {
		state?.runtimeStatsTable=null				
		state?.runtimeAvgWeeklyStatsTable=null				
		state?.runtimeAvgMonthlyStatsTable=null			
		state?.today=todayDay        
		state?.currentMode=mode        
	}

	def runtimeStatsTable = (state?.runtimeStatsTable)?:null  
	def runtimeAvgWeeklyStatsTable = (state?.runtimeAvgWeeklyStatsTable)?:null 
	def runtimeAvgMonthlyStatsTable = (state?.runtimeAvgMonthlyStatsTable)?:null
	
	if ( runtimeStatsTable == null || runtimeAvgWeeklyStatsTable == null || runtimeAvgMonthlyStatsTable == null) {
		traceEvent(settings.logFilter,"Querying ST for stats dataÖ",settings.trace, get_LOG_TRACE())
		runtimeStatsTable = []
		runtimeAvgWeeklyStatsTable = []
		runtimeAvgMonthlyStatsTable = []
		if (mode=='cool') {
			def coolStatsData = device.statesSince("compCool1RuntimeDaily", startOfWeek, [max:10])
			def coolWeeklyStatsData = device.statesSince("compCool1RuntimeAvgWeekly", startOfWeek, [max:10])
			def coolMonthlyStatsData = device.statesSince("compCool1RuntimeAvgMonthly",startOfWeek, [max:10])
			coolStatsData.reverse().each() {
				runtimeStatsTable.add([it.date.getTime(),it.floatValue])
			}
			coolWeeklyStatsData.reverse().each() {
				runtimeAvgWeeklyStatsTable.add([it.date.getTime(),it.floatValue])
			}
			coolMonthlyStatsData.reverse().each() {
				runtimeAvgMonthlyStatsTable.add([it.date.getTime(),it.floatValue])
			}
            
		} else {
			def heatStatsData = device.statesSince("auxHeat1RuntimeDaily", startOfWeek, [max:10])
			def heatWeeklyStatsData = device.statesSince("auxHeat1RuntimeAvgWeekly", startOfWeek, [max:10])
			def heatMonthlyStatsData = device.statesSince("auxHeat1RuntimeAvgMonthly", startOfWeek, [max:10])
			heatStatsData.reverse().each() {
				runtimeStatsTable.add([it.date.getTime(),it.floatValue])
			}
			heatWeeklyStatsData.reverse().each() {
				runtimeAvgWeeklyStatsTable.add([it.date.getTime(),it.floatValue])
			}
			heatMonthlyStatsData.reverse().each() {
				runtimeAvgMonthlyStatsTable.add([it.date.getTime(),it.floatValue])
			}
            
		}
		if (runtimeStatsTable == []) { // if no stats are found
			runtimeStatsTable.add([startOfWeek.getTime(),0])		        
			runtimeStatsTable.add([todayDate.getTime(),0])		        
		}        
		if (runtimeAvgWeeklyStatsTable == []) { // if no stats are found
			runtimeAvgWeeklyStatsTable.add([startOfWeek.getTime(),0])		        
			runtimeAvgWeeklyStatsTable.add([todayDate.getTime(),0])		        
		}        
		if (runtimeAvgMonthlyStatsTable == []) { // if no stats are found
			runtimeAvgMonthlyStatsTable.add([startOfWeek.getTime(),0])		        
			runtimeAvgMonthlyStatsTable.add([todayDate.getTime(),0])		        
            
		}        
		        
	}
    
	state?.temperatureTable = temperatureTable
	state?.runtimeStatsTable = runtimeStatsTable
	state?.runtimeAvgWeeklyStatsTable = runtimeAvgWeeklyStatsTable
	state?.runtimeAvgMonthlyStatsTable = runtimeAvgMonthlyStatsTable
	state?.heatingSetpointTable = heatingSetpointTable
	state?.coolingSetpointTable = coolingSetpointTable
	traceEvent(settings.logFilter,"state.currentMode= ${state?.currentMode}",settings.trace)    
	traceEvent(settings.logFilter,"temperatureTable (size=${state?.temperatureTable.size()}) =${state?.temperatureTable}",settings.trace)  
	traceEvent(settings.logFilter,"heatingSetpointTable (size=${state?.heatingSetpointTable.size()}) =${state?.heatingSetpointTable}",settings.trace)  
	traceEvent(settings.logFilter,"coolingSetpointTable (size=${state?.coolingSetpointTable.size()}) =${state?.coolingSetpointTable}",settings.trace)  
	traceEvent(settings.logFilter,"runtimeStatsTable (size=${state?.runtimeStatsTable.size()}) =${state?.runtimeStatsTable}",settings.trace)  
	traceEvent(settings.logFilter,"runtimeAvgWeeklyStatsTable=${state?.runtimeAvgWeeklyStatsTable}",settings.trace)  
	traceEvent(settings.logFilter,"runtimeAvgMonthlyStatsTable=${state?.runtimeAvgMonthlyStatsTable}",settings.trace)  
}

def getStartTime() {
	long startTime = new Date().getTime().toLong()
	if (state?.temperatureTable?.size() > 0) {
		startTime = state.temperatureTable.min{it[0].toLong()}[0].toLong()
	}
	if (state?.runtimeStatsTable?.size() > 0) {
		startTime = Math.min(startTime, state.runtimeStatsTable.min{it[0].toLong()}[0].toLong())
	}
	if (settings.trace) {    
		log.debug ("startTime=$startTime")    
	}        
	return startTime
}


String getDataString(Integer seriesIndex) {
	def dataString = ""
	def dataTable = []
	def dataArray    
	switch (seriesIndex) {
		case 1:
			dataTable = state?.runtimeStatsTable
			break
		case 2:
			dataTable = state?.runtimeAvgWeeklyStatsTable
			break
		case 3:
			dataTable = state?.runtimeAvgMonthlyStatsTable
			break
		case 4:
			dataTable = state?.temperatureTable
			break
		case 5:
			dataTable = state?.heatingSetpointTable
			break
		case 6:
			dataTable = state?.coolingSetpointTable
			break
	}
	dataTable.each() {
		dataString += "[new Date(${it[0]}),"
		if (seriesIndex==1) {
			dataString += "${it[1]},null,null,null,null,null],"
		}
		if (seriesIndex==2) {
			dataString += "null,${it[1]},null,null,null,null],"
		}
		if (seriesIndex==3) {
			dataString += "null,null,${it[1]},null,null,null],"
		}
		if (seriesIndex==4) {
			dataString += "null,null,null,${it[1]},null,null],"
		}
		if (seriesIndex==5) {
			dataString += "null,null,null,null,${it[1]},null],"
		}
		if (seriesIndex==6) {
			dataString += "null,null,null,null,null,${it[1]}],"
		}
        
	}
	        
	if (dataString == "") {
		def todayDate = new Date()
		if (seriesIndex==1) {
			dataString = "[new Date(todayDate.getTime()),0,null,null,null,null,null],"
		}
		if (seriesIndex==2) {
			dataString = "[new Date(todayDate.getTime()),null,0,null,null,null,null],"
		}
		if (seriesIndex==3) {
			dataString = "[new Date(todayDate.getTime()),null,null,0,null,null,null],"
		}
		if (seriesIndex==4) {
			dataString = "[new Date(todayDate.getTime()),null,null,null,0,null,null],"
		}
		if (seriesIndex==5) {
			dataString = "[new Date(todayDate.getTime()),null,null,null,null,0,null],"
		}
		if (seriesIndex==6) {
			dataString = "[new Date(todayDate.getTime()),null,null,null,null,null,0],"
		}
	}
//	traceEvent(settings.logFilter,"seriesIndex= $seriesIndex, dataString=$dataString",settings.trace)
    
	return dataString
}



def getGraphHTML() {
	def mode = (state?.currentMode=='cool')?'Cool1':'Heat1'
  
	String dataRows = "${getDataString(1)}" + "${getDataString(2)}" + "${getDataString(3)}" +
		"${getDataString(4)}" + "${getDataString(5)}" + "${getDataString(6)}"

	Date maxDateTime= new Date()
	Date minDateTime= new Date(getStartTime())
	def minDateStr= "new Date(" +  minDateTime.getTime() + ")"
	def maxDateStr= "new Date(" +  maxDateTime.getTime() + ")"

	Date yesterday=maxDateTime -1
	def yesterdayStr= "new Date(" +  yesterday.getTime() + ")"
   
	def html = """
		<!DOCTYPE html>
			<html>
				<head>
					<meta http-equiv="cache-control" content="max-age=0"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta name="viewport" content="width = device-width, user-scalable=no, initial-scale=1.0">
					<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
					<script type="text/javascript">
   					google.charts.load('current', {'packages':['corechart']});
					google.charts.setOnLoadCallback(drawChart);
                    
					function drawChart() {
						var data = new google.visualization.DataTable();
						data.addColumn('datetime', 'Time of Day')
						data.addColumn('number', 'RunInMin');
						data.addColumn('number', 'AvgRunWeekly');
						data.addColumn('number', 'AvgRunMnthly');
						data.addColumn('number', 'Outdoor');
						data.addColumn('number', 'HSPoint');
						data.addColumn('number', 'CSPoint');
						data.addRows([
							${dataRows}
						]);
						var options = {
							hAxis: {
								viewWindow: {
									min: ${minDateStr},
									max: ${maxDateStr}
								},
  								gridlines: {
									count: -1,
									units: {
										days: {format: ['MMM dd']},
										hours: {format: ['HH:mm', 'ha']}
										}
								},
								minorGridlines: {
									units: {
										hours: {format: ['hh:mm:ss a','ha']},
										minutes: {format: ['HH:mm a Z',':mm']}
									}
								}
							},
							series: {
								0: {targetAxisIndex: 0, color: '#44b621',lineWidth: 2},
								1: {targetAxisIndex: 0, color: '#f1d801'},
								2: {targetAxisIndex: 0, color: '#d04e00'},
								3: {targetAxisIndex: 1, color: '#153591'},
								4: {targetAxisIndex: 1, color: '#FF0000',lineWidth: 1},
								5: {targetAxisIndex: 1, color: '#269bd2',lineWidth: 1}
							},
							vAxes: {
								0: {
									title: '${mode}RunTime(min)',
									format: 'decimal',
									minValue: 0,                                        
									maxValue: 500,                                        
									textStyle: {color: '#44b621'},
									titleTextStyle: {color: '#44b621'}
								},
								1: {
									title: 'Temperature',
									format: 'decimal',
									textStyle: {color: '#FF0000'},
									titleTextStyle: {color: '#FF0000'}
								}
							},
							legend: {
								position: 'bottom',
								textStyle: {color: '#000000'}
							},
							chartArea: {
								left: '12%',
								right: '15%',
								top: '3%',
								bottom: '20%',
								height: '85%',
								width: '100%'
							}
						};
						var chart = new google.visualization.LineChart(document.getElementById('chart_div'));

  						chart.draw(data, options);
						var button = document.getElementById('change');
						var isChanged = false;

						button.onclick = function () {
							if (!isChanged) {
								options.hAxis.viewWindow.min = ${minDateStr};
								options.hAxis.viewWindow.max = ${maxDateStr};
								isChanged = true;
							} else {
								options.hAxis.viewWindow.min = ${yesterdayStr};
								options.hAxis.viewWindow.max =  ${maxDateStr};
								isChanged = false;
							}
							chart.draw(data, options);
						};
					}                        
 			</script>
			</head>
	  		<h3 style="font-size: 20px; font-weight: bold; text-align: center; background: #ffffff; color: #44b621;">RuntimeVsSetpoints</h3>
			<body>
				<button id="change">Change View Window</button>
				<div id="chart_div"></div>
			</body>
		</html>
	"""
	render contentType: "text/html", data: html, status: 200
}

private int get_LOG_ERROR() {return 1}
private int get_LOG_WARN()  {return 2}
private int get_LOG_INFO()  {return 3}
private int get_LOG_DEBUG() {return 4}
private int get_LOG_TRACE() {return 5}

def traceEvent(logFilter,message, displayEvent=false, traceLevel=4, sendMessage=true) {
int LOG_ERROR= get_LOG_ERROR()
int LOG_WARN=  get_LOG_WARN()
int LOG_INFO=  get_LOG_INFO()
int LOG_DEBUG= get_LOG_DEBUG()
int LOG_TRACE= get_LOG_TRACE()
int filterLevel=(logFilter)?logFilter.toInteger():get_LOG_WARN()

	if (displayEvent) {
		def results = [
			name: "verboseTrace",
			value: message,
			displayed: ((displayEvent)?: false)
		]	

		if (filterLevel >= traceLevel) {
			switch (traceLevel) {
				case LOG_ERROR:
					log.error "${message}"
				break
				case LOG_WARN:
					log.warn "${message}"
				break
				case LOG_INFO:
					log.info  "${message}"
				break
				case LOG_TRACE:
					log.trace "${message}"
				break
				case LOG_DEBUG:
				default:
					log.debug "${message}"
				break
			}                
			if (sendMessage) sendEvent (results)
		}
	}			                
}