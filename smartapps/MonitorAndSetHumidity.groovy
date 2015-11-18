/***
 *  Copyright 2014 Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
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
 * 
 *
 *  Monitor and set Humidity with Ecobee Thermostat(s):
 *      Monitor humidity level indoor vs. outdoor at a regular interval (in minutes) and 
 *      set the humidifier/dehumidifier  to a target humidity level. 
 *      Use also HRV/ERV/dehumidifier to get fresh air (free cooling) when appropriate based on outdoor temperature.
 *
 */
// Automatically generated. Make future change here.
definition(
	name: "MonitorAndSetEcobeeHumidity",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Monitor And set Ecobee's humidity via your connected humidifier/dehumidifier/HRV/ERV",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	page(name: "humiditySettings", title: "HumiditySettings")
	page(name: "sensorSettings", title: "SensorSettings")
	page(name: "otherSettings", title: "OtherSettings")

}

def humiditySettings() {
	dynamicPage(name: "humiditySettings", install: false, uninstall: true, nextPage: "sensorSettings") {
		section("About") {
			paragraph "MonitorAndSetEcobeeHumdity, the smartapp that can control your house's humidity via your connected humidifier/dehumidifier/HRV/ERV"
			paragraph "Version 2.0.6"
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
					title:"Paypal donation..."
			paragraph "Copyright©2014 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
 					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Monitor & set the ecobee thermostat's dehumidifer/humidifier/HRV/ERV") {
			input "ecobee", "capability.thermostat", title: "Ecobee?"
		}
		section("To this humidity level") {
			input "givenHumidityLevel", "number", title: "Humidity level (default=calculated based on outside temp)", required: false
		}
		section("At which interval in minutes (range=[10..59],default =59 min.)?") {
			input "givenInterval", "number", title: "Interval", required: false
		}
		section("Humidity differential for adjustments") {
			input "givenHumidityDiff", "number", title: "Humidity Differential [default=5%]", required: false
		}
		section("Frost control for humidifier") {
			input "frostControl", "bool", title: "Frost control [default=false]?", required: false
		}
		section("Minimum fan runtime per hour in minutes") {
			input "givenFanMinTime", "number", title: "Minimum fan runtime [default=20]", required: false
		}
		section("Your dehumidifier as HRV input parameters section") {
			input "useDehumidifierAsHRV", "bool", title: "Use Dehumidifier as HRV (By default=false)?", required: false
			input "givenVentMinTime", "number", title: "Minimum HRV/ERV runtime [default=20]", required: false
		}
		section("Minimum temperature for dehumidification (in Farenheits/Celsius)") {
			input "givenMinTemp", "decimal", title: "Min Temp [default=10°F/-15°C]", required: false
		}
		section("Free cooling using HRV/Dehumidifier [By default=false]") {
			input "freeCooling", "bool", title: "Free Cooling?", required: false
		}
		section("Check energy consumption at [optional]") {
			input "ted", "capability.powerMeter", title: "Power meter?", required: false
		}
		section("Do not run any devices above this power consumption level at a given time [default=3000W]") {
			input "givenPowerLevel", "number", title: "power?", required: false
		}
	}
}

def sensorSettings() {
	dynamicPage(name: "sensorSettings", title: "Sensors to be used", install: false, nextPage: otherSettings) {
		section("Choose Indoor humidity sensor to be used for better adjustment (optional, default=ecobee sensor)") {
			input "indoorSensor", "capability.relativeHumidityMeasurement", title: "Indoor Humidity Sensor", required: false
		}
		section("Choose Outdoor humidity sensor to be used") {
			input "outdoorSensor", "capability.relativeHumidityMeasurement", title: "Outdoor Humidity Sensor"
		}
	}
}


def otherSettings() {
	dynamicPage(name: "otherSettings", title: "Other Settings", install: true, uninstall: false) {

		section("What do I use for the Master on/off switch to enable/disable processing? (optional)") {
			input "powerSwitch", "capability.switch", required: false
		}
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required:
				false
			input "phoneNumber", "phone", title: "Send a text message?", required: false
		}
		section("Detailed Notifications") {
			input "detailedNotif", "bool", title: "Detailed Notifications?", required:
				false
		}
		section([mobileOnly: true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
	}
}



def installed() {
	initialize()
}

def updated() {
	// we have had an update
	// remove everything and reinstall
	unschedule()
	unsubscribe()
	initialize()
}

def initialize() {
	state.currentRevision = null // for further check with thermostatRevision later

	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler)
		subscribe(powerSwitch, "switch.on", onHandler)
	}
	Integer delay = givenInterval ?: 59 // By default, do it every hour
	if ((delay < 10) || (delay > 59)) {
		log.error "Scheduling delay not in range (${delay} min.), exiting"
		runIn(30, "sendNotifDelayNotInRange")
		return
	}
	log.debug "initialize>scheduling Humidity Monitoring and adjustment every ${delay} minutes"

	state?.poll = [ last: 0, rescheduled: now() ]

	//Subscribe to different events (ex. sunrise and sunset events) to trigger rescheduling if needed
	subscribe(location, "sunrise", rescheduleIfNeeded)
	subscribe(location, "sunset", rescheduleIfNeeded)
	subscribe(location, "mode", rescheduleIfNeeded)
	subscribe(location, "sunriseTime", rescheduleIfNeeded)
	subscribe(location, "sunsetTime", rescheduleIfNeeded)

	rescheduleIfNeeded()   
}


def rescheduleIfNeeded(evt) {
	if (evt) log.debug("rescheduleIfNeeded>$evt.name=$evt.value")
	Integer delay = givenInterval ?: 59 // By default, do it every hour
	BigDecimal currentTime = now()    
	BigDecimal lastPollTime = (currentTime - (state?.poll["last"]?:0))  
 
	if (lastPollTime != currentTime) {    
		Double lastPollTimeInMinutes = (lastPollTime/60000).toDouble().round(1)      
		log.info "rescheduleIfNeeded>last poll was  ${lastPollTimeInMinutes.toString()} minutes ago"
	}
	if (((state?.poll["last"]?:0) + (delay * 60000) < currentTime) && canSchedule()) {
		log.info "rescheduleIfNeeded>scheduling setHumidityLevel in ${delay} minutes.."
		schedule("0 0/${delay} * * * ?", setHumidityLevel)
	}
    
	setHumidityLevel()
    
	// Update rescheduled state
    
	if (!evt) state.poll["rescheduled"] = now()
}



private def sendNotifDelayNotInRange() {

	send "MonitorEcobeeHumidity>scheduling delay (${givenInterval} min.) not in range, please restart..."

}


def offHandler(evt) {
	log.debug "$evt.name: $evt.value"
}

def onHandler(evt) {
	log.debug "$evt.name: $evt.value"
	setHumidityLevel()
}


def appTouch(evt) {
	setHumidityLevel()
}


def setHumidityLevel() {
	Integer scheduleInterval = givenInterval ?: 59 // By default, do it every hour
	state?.poll["last"] = now()
		
	//schedule the rescheduleIfNeeded() function
    
	if (((state?.poll["rescheduled"]?:0) + (scheduleInterval * 60000)) < now()) {
		log.info "takeAction>scheduling rescheduleIfNeeded() in ${scheduleInterval} minutes.."
		schedule("0 0/${scheduleInterval} * * * ?", rescheduleIfNeeded)
		// Update rescheduled state
		state?.poll["rescheduled"] = now()
	}

	if (detailedNotif) {
		send("MonitorEcobeeHumidity>monitoring every ${scheduleInterval} minute(s)")
		log.debug "Scheduling Humidity Monitoring & Change every ${scheduleInterval}  minutes"
	}


	if (powerSwitch ?.currentSwitch == "off") {
		if (detailedNotif) {
			send("MonitorEcobeeHumidity>Virtual master switch ${powerSwitch.name} is off, processing on hold...")
		}
		return
	}
	def min_humidity_diff = givenHumidityDiff ?: 5 //  5% humidity differential by default
	Integer min_fan_time = (givenFanMinTime != null) ? givenFanMinTime : 20 //  20 min. fan time per hour by default
	Integer min_vent_time = (givenVentMinTime != null) ? givenVentMinTime : 20 //  20 min. ventilator time per hour by default
	def freeCoolingFlag = (freeCooling != null) ? freeCooling : false // Free cooling using the Hrv/Erv/dehumidifier
	def frostControlFlag = (frostControl != null) ? frostControl : false // Frost Control for humdifier, by default=false
	def min_temp // Min temp in Farenheits for using HRV/ERV,otherwise too cold

	def scale = getTemperatureScale()
	if (scale == 'C') {
		min_temp = (givenMinTemp != null) ? givenMinTemp : -15 // Min. temp in Celcius for using HRV/ERV,otherwise too cold
	} else {

		min_temp = (givenMinTemp != null) ? givenMinTemp : 10 // Min temp in Farenheits for using HRV/ERV,otherwise too cold
	}
	Integer max_power = givenPowerLevel ?: 3000 // Do not run above 3000w consumption level by default


	log.debug "location.mode = $location.mode"

	//  Polling of all devices

	def MAX_EXCEPTION_COUNT=5
	String exceptionCheck, msg 
	try {        
		ecobee.poll()
		exceptionCheck= ecobee.currentVerboseTrace.toString()
		if ((exceptionCheck.contains("exception") || (exceptionCheck.contains("error")) && 
			(!exceptionCheck.contains("Java.util.concurrent.TimeoutException")))) {  
		// check if there is any exception or an error reported in the verboseTrace associated to the device (except the ones linked to rate limiting).
			state?.exceptionCount=state.exceptionCount+1    
			log.error "setHumidityLevel>found exception/error after polling, exceptionCount= ${state?.exceptionCount}: $exceptionCheck" 
		} else {             
			// reset exception counter            
			state?.exceptionCount=0       
		}                
	} catch (e) {
		state.exceptionCount=state.exceptionCount+1    
		log.error "setHumidityLevel>exception $e while trying to poll the device $d, exceptionCount= ${state?.exceptionCount}" 
	}
	if ((state?.exceptionCount>=MAX_EXCEPTION_COUNT) || (exceptionCheck.contains("Unauthorized"))) {
		// need to authenticate again    
		msg="too many exceptions/errors or unauthorized exception, $exceptionCheck (${state?.exceptionCount} errors), need to re-authenticate at ecobee..." 
		send "MonitorEcobeeHumidity> ${msg}"
		log.error msg
		return        
	}    

	if ((outdoorSensor.hasCapability("Refresh")) || (outdoorSensor.hasCapability("Polling"))) {
		try {    
			outdoorSensor.poll()
		} catch (e) {
			log.debug("MonitorEcobeeHumdity>not able to refresh ${outdoorSensor}'s temp value")
		}
	}        
	if (ted) {

		try {
			ted.poll()
			Integer powerConsumed = ted.currentPower.toInteger()
			if (powerConsumed > max_power) {

				//              peak of energy consumption, turn off all devices

				if (detailedNotif) {
					send "MonitorEcobeeHumidity>all off,power usage is too high=${ted.currentPower}"
				}

				ecobee.setThermostatSettings("", ['vent': 'off', 'dehumidifierMode': 'off', 'humidifierMode': 'off',
					'dehumidifyWithAC': 'false'
				])
				return

			}
		} catch (e) {
			log.error "Exception $e while trying to get power data "
		}
	}


	def heatTemp = ecobee.currentHeatingSetpoint
	def coolTemp = ecobee.currentCoolingSetpoint
	def ecobeeHumidity = ecobee.currentHumidity
	def indoorHumidity = 0
	def indoorTemp = ecobee.currentTemperature
	def hasDehumidifier = (ecobee.currentHasDehumidifier) ? ecobee.currentHasDehumidifier : 'false'
	def hasHumidifier = (ecobee.currentHasHumidifier) ? ecobee.currentHasHumidifier : 'false'
	def hasHrv = (ecobee.currentHasHrv) ? ecobee.currentHasHrv : 'false'
	def hasErv = (ecobee.currentHasErv) ? ecobee.currentHasErv : 'false'
	def useDehumidifierAsHRVFlag = (useDehumidifierAsHRV) ? useDehumidifierAsHRV : 'false'
	def outdoorHumidity

	// use the readings from another sensor if better precision neeeded
	if (indoorSensor) {
		indoorHumidity = indoorSensor.currentHumidity
		indoorTemp = indoorSensor.currentTemperature
	}
	outdoorSensor.refresh()
	def outdoorSensorHumidity = outdoorSensor.currentHumidity
	def outdoorTemp = outdoorSensor.currentTemperature
		// by default, the humidity level is calculated based on a sliding scale target based on outdoorTemp

	def target_humidity = givenHumidityLevel ?: find_ideal_indoor_humidity(outdoorTemp)

	String ecobeeMode = ecobee.currentThermostatMode
	log.debug "ecobee Mode = $ecobeeMode"

	outdoorHumidity = (scale == 'C') ?
		calculate_corr_humidity(outdoorTemp, outdoorSensorHumidity, indoorTemp) :
		calculate_corr_humidity(fToC(outdoorTemp), outdoorSensorHumidity, fToC(indoorTemp))


	//  If indoorSensor specified, use the more precise humidity measure instead of ecobeeHumidity

	log.trace("Ecobee's humidity: ${ecobeeHumidity} vs. indoor humidity ${indoorHumidity}")
	if ((indoorSensor) && (indoorHumidity < ecobeeHumidity)) {
		ecobeeHumidity = indoorHumidity
	}

	log.debug "outdoorSensorHumidity = $outdoorSensorHumidity%, normalized outdoorHumidity based on ambient temperature = $outdoorHumidity%"
	if (detailedNotif) {
		send "MonitorEcobeeHumidity>normalized outdoor humidity is ${outdoorHumidity}%,sensor outdoor humidity ${outdoorSensorHumidity}%,vs. indoor Humidity ${ecobeeHumidity}%"
	}



	log.trace("Evaluate: Ecobee humidity: ${ecobeeHumidity} vs. outdoor humidity ${outdoorHumidity}," +
		"coolingSetpoint: ${coolTemp} , heatingSetpoint: ${heatTemp}, target humidity=${target_humidity}, fanMinOnTime=${min_fan_time}")
	log.trace("hasErv=${hasErv}, hasHrv=${hasHrv},hasHumidifier=${hasHumidifier},hasDehumidifier=${hasDehumidifier}, freeCoolingFlag=${freeCoolingFlag}," +
		"useDehumidifierAsHRV=${useDehumidifierAsHRVFlag}")

	if ((ecobeeMode == 'cool' && (hasHrv == 'true' || hasErv == 'true')) &&
		(ecobeeHumidity >= (outdoorHumidity - min_humidity_diff)) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff))) {
		log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, " +
			"need to dehumidify the house and normalized outdoor humidity is lower (${outdoorHumidity})"

		// Turn on the dehumidifer and HRV/ERV, the outdoor humidity is lower or equivalent than inside

		ecobee.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}", 'vent': 'minontime', 'ventilatorMinOnTime': "${min_vent_time}"])

		send "MonitorEcobeeHumidity>dehumidify to ${target_humidity}% in ${ecobeeMode} mode, using ERV/HRV"

	} else if (((ecobeeMode == 'heat') || (ecobeeMode == 'off') && (hasHrv == 'false' && hasErv == 'false' && hasDehumidifier == 'true')) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
		(ecobeeHumidity >= outdoorHumidity - min_humidity_diff) &&
		(outdoorTemp > min_temp)) {

		log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
			"normalized outdoor humidity is within range (${outdoorHumidity}) & outdoor temp is ${outdoorTemp},not too cold"

		//      Turn on the dehumidifer, the outdoor temp is not too cold 

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}",
			'humidifierMode': 'off', 'fanMinOnTime': "${min_fan_time}"
			])

		send "MonitorEcobeeHumidity>dehumidify to ${target_humidity}% in ${ecobeeMode} mode"
	} else if (((ecobeeMode == 'heat') || (ecobeeMode == 'off') && ((hasHrv == 'true' || hasErv == 'true') && hasDehumidifier == 'false')) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
		(ecobeeHumidity >= outdoorHumidity - min_humidity_diff) &&
		(outdoorTemp > min_temp)) {

		log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
			"normalized outdoor humidity is within range (${outdoorHumidity}) & outdoor temp is ${outdoorTemp},not too cold"

		//      Turn on the HRV/ERV, the outdoor temp is not too cold 

		ecobee.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}", 'vent': 'minontime', 'ventilatorMinOnTime': "${min_vent_time}"])

		send "MonitorEcobeeHumidity>use HRV/ERV to dehumidify ${target_humidity}% in ${ecobeeMode} mode"
	} else if (((ecobeeMode == 'heat') || (ecobeeMode == 'off') && (hasHrv == 'true' || hasErv == 'true' || hasDehumidifier == 'true')) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
		(ecobeeHumidity >= outdoorHumidity - min_humidity_diff) &&
		(outdoorTemp <= min_temp)) {

		log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
			"normalized outdoor humidity is lower (${outdoorHumidity}), but outdoor temp is ${outdoorTemp}: too cold to dehumidify"


		//      Turn off the dehumidifer and HRV/ERV because it's too cold till the next cycle.

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'dehumidifierLevel': "${target_humidity}",
			'humidifierMode': 'off', 'vent': 'off'
			])

		if (detailedNotif) {
			send "MonitorEcobeeHumidity>Too cold (${outdoorTemp}°) to dehumidify to ${target_humidity}"
		}
	} else if ((((ecobeeMode == 'heat' || ecobeeMode == 'off') && hasHumidifier == 'true')) &&
		(ecobeeHumidity < (target_humidity - min_humidity_diff))) {

		log.trace("In ${ecobeeMode} mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house")

		//      Need a minimum differential to humidify the house to the target if any humidifier available

		def humidifierMode = (frostControlFlag) ? 'auto' : 'manual'
		ecobee.setThermostatSettings("", ['humidifierMode': "${humidifierMode}", 'humidity': "${target_humidity}", 'dehumidifierMode': 'off'])

		send "MonitorEcobeeHumidity> humidify to ${target_humidity} in ${ecobeeMode} mode"
	} else if (((ecobeeMode == 'cool') && (hasDehumidifier == 'false') && (hasHrv == 'true' || hasErv == 'true')) &&
		(ecobeeHumidity > (target_humidity + min_humidity_diff)) &&
		(outdoorHumidity > target_humidity)) {


		log.trace("Ecobee humidity provided is way higher than target humidity level=${target_humidity}, need to dehumidify with AC, because normalized outdoor humidity is too high=${outdoorHumidity}")

		//      If mode is cooling and outdoor humidity is too high then use the A/C to lower humidity in the house if there is no dehumidifier

		ecobee.setThermostatSettings("", ['dehumidifyWithAC': 'true', 'dehumidifierLevel': "${target_humidity}",
			'dehumidiferMode': 'off', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
			])

		send "MonitorEcobeeHumidity>dehumidifyWithAC in cooling mode, indoor humidity is ${ecobeeHumidity}% and normalized outdoor humidity (${outdoorHumidity}%) is too high to dehumidify"

	} else if ((ecobeeMode == 'cool') && (hasDehumidifier == 'true') &&
		(ecobeeHumidity > (target_humidity + min_humidity_diff))) {

		//      If mode is cooling and outdoor humidity is too high, then just use dehumidifier if any available

		log.trace "Dehumidify to ${target_humidity} in ${ecobeeMode} mode using the dehumidifier"

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}", 'humidifierMode': 'off',
			'dehumidifyWithAC': 'false', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
			])

		send "MonitorEcobeeHumidity>dehumidify to ${target_humidity}% in ${ecobeeMode} mode using the dehumidifier only"


	} else if (((ecobeeMode == 'cool') && (hasDehumidifier == 'true' && hasErv == 'false' && hasHrv == 'false')) &&
		(outdoorTemp < indoorTemp) && (freeCoolingFlag == 'true')) {

		//      If mode is cooling and outdoor temp is lower than inside, then just use dehumidifier for better cooling if any available

		log.trace "In cooling mode, outdoor temp is lower than inside, using dehumidifier for free cooling"

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}", 'humidifierMode': 'off',
			'dehumidifyWithAC': 'false', 'fanMinOnTime': "${min_fan_time}"
			])
		send "MonitorEcobeeHumidity>Outdoor temp is lower than inside, using dehumidifier for more efficient cooling"


	} else if ((ecobeeMode == 'cool' && (hasHrv == 'true')) && (outdoorTemp < indoorTemp) &&
		(freeCoolingFlag == 'true')) {

		log.trace("In cooling mode, outdoor temp is lower than inside, using the HRV to get fresh air")

		//      If mode is cooling and outdoor's temp is lower than inside, then use HRV to get fresh air into the house

		ecobee.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}",
			'vent': 'minontime', 'ventilatorMinOnTime': "${min_vent_time}"
			])

		send "MonitorEcobeeHumidity>Outdoor temp is lower than inside, using the HRV for more efficient cooling"

	} else if ((outdoorHumidity > ecobeeHumidity) && (ecobeeHumidity > target_humidity)) {

		//      If indoor humidity is greater than target, but outdoor humidity is way higher than indoor humidity, 
		//      just wait for the next cycle & do nothing for now.

		log.trace("Indoor humidity is ${ecobeeHumidity}%, but outdoor humidity (${outdoorHumidity}%) is too high to dehumidify")
		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'humidifierMode': 'off', 'vent': 'off'])
		if (detailedNotif) {
			send "MonitorEcobeeHumidity>indoor humidity is ${ecobeeHumidity}%, but outdoor humidity ${outdoorHumidity}% is too high to dehumidify"
		}

	} else {

		log.trace("All off, humidity level (${ecobeeHumidity}%) within range")
		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'humidifierMode': 'off', 'dehumidifyWithAC': 'false',
			'vent': 'off'
			])
		if (detailedNotif) {
			send "MonitorEcobeeHumidity>all off, humidity level (${ecobeeHumidity}%) within range"
		}
	}

	if (useDehumidifierAsHRVFlag) {
		Date now = new Date()
		String nowInLocalTime = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
		Calendar oneHourAgoCal = new GregorianCalendar()
		oneHourAgoCal.add(Calendar.HOUR, -1)
		Date oneHourAgo = oneHourAgoCal.getTime()
		log.debug("local date/time= ${nowInLocalTime}, date/time now in UTC = ${String.format('%tF %<tT',now)}," +
			"oneHourAgo's date/time in UTC= ${String.format('%tF %<tT',oneHourAgo)}")


		// Get the dehumidifier's runtime 
		ecobee.getReportData("", oneHourAgo, now, null, null, "dehumidifier", 'false', 'true')
		ecobee.generateReportRuntimeEvents("dehumidifier", oneHourAgo, now, 0, null, 'lastHour')
		float dehumidifierRunInMin = ecobee.currentDehumidifierRuntimeInPeriod.toFloat().round()
		if (detailedNotif) {
			send "MonitorEcobeeHumidity>dehumidifier runtime in the last hour is ${dehumidifierRunInMin.toString()} min. vs. desired ventilatorMinOnTime =${min_vent_time.toString()} minutes"
		}
		float diffVentTimeInMin = min_vent_time - dehumidifierRunInMin as Float
		def equipStatus = ecobee.currentEquipmentStatus
		log.debug "equipStatus = $equipStatus"
		if (equipStatus.contains("dehumidifier")) {
			log.trace("dehumidifier should be running (${equipStatus}), time left to run = ${diffVentTimeInMin.toString()} min. within the current cycle")
			if (detailedNotif) {
				send "MonitorEcobeeHumidity>dehumidifier (used as HRV) already running,time left to run = ${diffVentTimeInMin.toString()} min."
			}
		}

		if ((diffVentTimeInMin > 0) && (!equipStatus.contains("dehumidifier"))) {
			if (detailedNotif) {
				send "MonitorEcobeeHumidity>About to turn the dehumidifier on for ${diffVentTimeInMin.toString()} min. within the next hour..."
			}

			ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': '25',
					'fanMinOnTime': "${min_fan_time}"
				])
				// calculate the delay to turn off the dehumidifier according to the scheduled monitoring cycle

			float delay = ((min_vent_time.toFloat() / 60) * scheduleInterval.toFloat()).round()
			int delayInt = delay.toInteger()
			delayInt = (delayInt > 1) ? delayInt : 1 // Min. delay should be at least 1 minute, otherwise, the dehumidifier won't stop.
			send "MonitorEcobeeHumidity>turning off the dehumidifier (used as HRV) in ${delayInt} minute(s)..."
				// save the current setpoints before scheduling the dehumidifier to be turned off
			runIn((delayInt * 60), "turn_off_dehumidifier") // turn off the dehumidifier after delay
		} else if (diffVentTimeInMin <= 0) {
			if (detailedNotif) {
				send "MonitorEcobeeHumidity>dehumidifier has run for at least ${min_vent_time} min. within the last hour, waiting for the next cycle"
			}
			log.trace("dehumidifier has run for at least ${min_vent_time} min. within the last hour, waiting for the next cycle")

		} else if (equipStatus.contains("dehumidifier")) {
			turn_off_dehumidifier()
		}
	} // end if useDehumidifierAsHRVFlag '

	log.debug "End of Fcn"
}

private void turn_off_dehumidifier() {


	if (detailedNotif) {
		send("MonitorEcobeeHumidity>about to turn off dehumidifier used as HRV....")
	}
	log.trace("About to turn off the dehumidifier used as HRV and the fan after timeout")


	ecobee.setThermostatSettings("", ['dehumidifierMode': 'off'])

}


private def bolton(t) {

	//  Estimates the saturation vapour pressure in hPa at a given temperature,  T, in Celcius
	//  return saturation vapour pressure at a given temperature in Celcius


	Double es = 6.112 * Math.exp(17.67 * t / (t + 243.5))
	return es

}


private def calculate_corr_humidity(t1, rh1, t2) {


	log.debug("calculate_corr_humidity t1= $t1, rh1=$rh1, t2=$t2")

	Double es = bolton(t1)
	Double es2 = bolton(t2)
	Double vapor = rh1 / 100.0 * es
	Double rh2 = ((vapor / es2) * 100.0).round(2)

	log.debug("calculate_corr_humidity rh2= $rh2")

	return rh2
}


private send(msg) {
	if (sendPushMessage != "No") {
		log.debug("sending push message")
		sendPush(msg)

	}

	if (phoneNumber) {
		log.debug("sending text message")
		sendSms(phoneNumber, msg)
	}

	log.debug msg
}

private int find_ideal_indoor_humidity(outsideTemp) {

	// -30C => 30%, at 0C => 45%

	int targetHum = 45 + (0.5 * outsideTemp)
	return (Math.max(Math.min(targetHum, 60), 30))
}


// catchall
def event(evt) {
	log.debug "value: $evt.value, event: $evt, settings: $settings, handlerName: ${evt.handlerName}"
}

def cToF(temp) {
	return (temp * 1.8 + 32)
}

def fToC(temp) {
		return (temp - 32) / 1.8
}
