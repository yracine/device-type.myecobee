/**
 *  Monitor And Set Ecobee Temp
 *
 *  Copyright 2014 Yves Racine
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
 * The MonitorAndSetEcobeeTemp monitors the outdoor temp and adjusts the heating and cooling set points
 * at regular intervals (input parameter in minutes) according to heat/cool thresholds that you set (input parameters).
 */
definition(
	name: "Monitor And Set Ecobee Temp",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Monitor And Set Ecobee Temperature according to outdoor temperature and humidity.",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {

	section("Monitor outdoor temp & set the ecobee thermostat setpoints") {
		input "ecobee", "capability.thermostat", title: "Ecobee?"
	}
	section("For more heating in cold season, outdoor temp's threshold (default <= 10°F/-15°C)") {
		input "givenMoreHeatThreshold", "decimal", title: "Outdoor temp's threshold for more heating", required: false
	}
	section("For less heating in cold season, outdoor temp's threshold (default >= 50°F/10°C)") {
		input "givenLessHeatThreshold", "decimal", title: "Outdoor temp's threshold for less heating", required: false
	}
	section("For more cooling in hot season, outdoor temp's threshold (default >= 85°F/30°C)") {
		input "givenMoreCoolThreshold", "decimal", title: "Outdoor temp's threshold for more cooling", required: false
	}
	section("For less cooling in hot season, outdoor temp's threshold (default <= 75°F/22°C)") {
		input "givenLessCoolThreshold", "decimal", title: "Outdoor temp's threshold for less cooling", required: false
	}
	section("For more cooling/heating, outdoor humidity's threshold (default >= 85%)") {
		input "givenHumThreshold", "number", title: "Outdoor Relative humidity's threshold for more cooling/heating",
			required: false
	}
	section("At which interval in minutes (default =59 min.)?") {
		input "givenInterval", "number", required: false
	}
	section("Temp differential for adjustments in Farenheits/Celcius") {
		input "givenTempDiff", "decimal", title: "Temperature adjustment (default= +/-5°F/2°C)", required: false
	}
	section("Choose outdoor Temperature & Humidity sensor to be used for monitoring") {
		input "outdoorSensor", "capability.temperatureMeasurement", title: "Outdoor Temperature Sensor"

	}
	section("Choose indoor motion sensors for climate settings [Away, Home] (optional)") {
        	input "motions", "capability.motionSensor", title: "Where?",  multiple: true, required: false
    	}
	section("Trigger the climate update when home has been quiet for (default=15 minutes)") {
        	input "residentsQuietThreshold", "number", title: "Time in minutes", required: false
	}
	section("What do I use for the Master on/off switch to enable/disable processing? (optional)") {
		input "powerSwitch", "capability.switch", required: false
	}
	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required:
			false
		input "phoneNumber", "phone", title: "Send a text message?", required: false
	}
	section("Detailed Notifications") {
		input "detailedNotif", "Boolean", title: "Detailed Notifications?", metadata: [values: ["true", "false"]], required:
			false
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

 	reset_state_values()
	log.debug("initialize state=$state")
	if (motions) {
		subscribe(motions, "motion", motionEvtHandler)
	}
	subscribe(ecobee, "heatingSetpoint", ecobeeHeatTempHandler)
	subscribe(ecobee, "coolingSetpoint", ecobeeCoolTempHandler)
	subscribe(ecobee, "humidity", ecobeeHumidityHandler)
	subscribe(ecobee, "temperature", ecobeeTempHandler)
	subscribe(ecobee, "thermostatMode", ecobeeModeHandler)
	subscribe(outdoorSensor, "humidity", outdoorSensorHumHandler)
	subscribe(outdoorSensor, "temperature", outdoorTempHandler)
	subscribe(ecobee, "programType", ecobeeProgramTypeHandler)
	subscribe(ecobee, "programCoolTemp", ecobeeProgramCoolHandler)
	subscribe(ecobee, "programHeatTemp", ecobeeProgramHeatHandler)
	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler)
		subscribe(powerSwitch, "switch.on", onHandler)
	}
	Integer delay = givenInterval ?: 59 // By default, do it every hour
	log.debug "Scheduling Outdoor temp Monitoring and adjustment every ${delay}  minutes"

	schedule("0 0/${delay} * * * ?", monitorAdjustTemp) // monitor & set indoor temp according to delay specified

}


def motionEvtHandler(evt) {
 	if (evt.value == "active") {
        	state.lastIntroductionMotion = now()
        	log.debug "Motion at home..."
		if (state?.programHoldSet == 'Away')  {
			check_if_hold_justified()
		}
	}        
}



private residentsHaveBeenQuiet() {

	def threshold = residentsQuietThreshold ?: 15   // By default, the delay is 15 minutes
    	def result = true
    	def t0 = new Date(now() - (threshold * 60 *1000))
    	for (sensor in motions) {
        	def recentStates = sensor.statesSince("motion", t0)
        	if (recentStates.find{it.value == "active"}) {
			result = false
			break
		}	
            
	}
    	log.debug "residentsHaveBeenQuiet: $result"
    	return result
}


private isProgramScheduleSet(climateName, threshold) {
	def result = false
    	def t0 = new Date(now() - (threshold * 60 *1000))
	def recentStates = ecobee.statesSince("programScheduleName", t0)
	if (recentStates.find{it.value == climateName}) {
		result = true
	}
	log.debug "isProgramScheduleSet: $result"
    	return result
}

def ecobeeHeatTempHandler(evt) {
	log.debug "ecobee's heating temp: $evt.value"
}

def ecobeeProgramTCoolHandler(evt) {
	log.debug "ecobee's heating temp: $evt.value"
}
def ecobeeProgramHeatHandler(evt) {
	log.debug "ecobee's heating temp: $evt.value"
}

def ecobeeCoolTempHandler(evt) {
	log.debug "ecobee's cooling temp: $evt.value"
}

def ecobeeHumidityHandler(evt) {
	log.debug "ecobee's humidity level: $evt.value"
}

def ecobeeTempHandler(evt) {
	log.debug "ecobee's temperature level: $evt.value"
}

def ecobeeModeHandler(evt) {
	log.debug "ecobee's mode: $evt.value"
}

def outdoorSensorHumHandler(evt) {
	log.debug "outdoor Sensor's humidity level: $evt.value"
}


def outdoorTempHandler(evt) {
	log.debug "outdoor temperature is: $evt.value"
}

def ecobeeProgramTypeHandler(evt) {
	log.debug "ecobee's program type: $evt.value"
}
def offHandler(evt) {
	log.debug "$evt.name: $evt.value"
}

def onHandler(evt) {
	log.debug "$evt.name: $evt.value"
	monitorAdjustTemp()
}

def monitorAdjustTemp() {
	if (powerSwitch?.currentSwitch == "off") {
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>Virtual master switch ${powerSwitch.name} is off, processing on hold...")
		}
		return
	}

	if (detailedNotif == 'true') {
		Integer delay = givenInterval ?: 59 // By default, do it every hour
		send("MonitorEcobeeTemp>monitoring every ${delay} minute(s)")
	}

	//  Polling of the latest values at the thermostat
	ecobee.poll()

	String currentProgType = ecobee.currentProgramType
	log.trace("MonitorEcobeeTemp> program Type= ${currentProgType}")
	if (currentProgType.contains("hold")) { 						// don't make any further adustments 
		log.trace("MonitorEcobeeTemp>about to call check_if_hold_justified....")
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>Hold set, checking if still justified")
		}
		check_if_hold_justified()
	}
        
	if (!currentProgType.contains("vacation")) {				// don't make adjustment if on vacation mode
		log.trace("MonitorEcobeeTemp>about to call check_if_needs_hold....")
		check_if_hold_needed()
	}
}

private def reset_state_values() {

 	state.programSetTime = null
 	state.programSetTimestamp = ""
 	state.programHoldSet = ""
}

private def check_if_hold_needed() {
	log.debug "Begin of Fcn check_if_hold_needed"
	float temp_diff
	Integer humidity_threshold = givenHumThreshold ?: 85 // by default, 85% is the outdoor Humidity's threshold for more cooling
	float more_heat_threshold, more_cool_threshold
	float less_heat_threshold, less_cool_threshold

	def scale = getTemperatureScale()
	if (scale == 'C') {
		temp_diff = givenTempDiff ?: 2 // 2°C temp differential is applied by default
		more_heat_threshold = (givenMoreHeatThreshold != null) ? givenMoreHeatThreshold : (-15) // by default, -15°C is the outdoor temp's threshold for more heating
		more_cool_threshold = (givenMoreCoolThreshold != null) ? givenMoreCoolThreshold : 30 // by default, 30°C is the outdoor temp's threshold for more cooling
		less_heat_threshold = (givenLessHeatThreshold != null) ? givenLessHeatThreshold : 10 // by default, 10°C is the outdoor temp's threshold for less heating
		less_cool_threshold = (givenLessCoolThreshold != null) ? givenLessCoolThreshold : 22 // by default, 22°C is the outdoor temp's threshold for less cooling

	} else {
		more_heat_threshold = (givenMoreHeatThreshold != null) ? givenMoreHeatThreshold : 10 // by default, 10°F is the outdoor temp's threshold for more heating
		more_cool_threshold = (givenMoreCoolThreshold != null) ? givenMoreCoolThreshold : 85 // by default, 85°F is the outdoor temp's threshold for more cooling
		less_heat_threshold = (givenLessHeatThreshold != null) ? givenLessHeatThreshold : 50 // by default, 50°F is the outdoor temp's threshold for less heating
		less_cool_threshold = (givenLessCoolThreshold != null) ? givenLessCoolThreshold : 75 // by default, 75°F is the outdoor temp's threshold for less cooling
	}

	String currentProgName = ecobee.currentProgramScheduleName

	Integer outdoorHumidity = outdoorSensor.currentHumidity
	float outdoorTemp = outdoorSensor.currentTemperature.toFloat()
	String ecobeeMode = ecobee.currentThermostatMode
	float heatTemp = ecobee.currentHeatingSetpoint.toFloat()
	float coolTemp = ecobee.currentCoolingSetpoint.toFloat()
	float programHeatTemp = ecobee.currentProgramHeatTemp.toFloat()
	float programCoolTemp = ecobee.currentProgramCoolTemp.toFloat()
	Integer ecobeeHumidity = ecobee.currentHumidity
	float ecobeeTemp = ecobee.currentTemperature.toFloat()

	log.trace "check_if_hold_justified> location.mode = $location.mode"
	log.trace "check_if_hold_justified> ecobee Mode = $ecobeeMode"
	log.trace "check_if_hold_justified> currentProgName = $currentProgName"
	log.trace "check_if_hold_justified> outdoorTemp = $outdoorTemp°"
	log.trace "check_if_hold_justified> temp_diff = $temp_diff°"
	log.trace "check_if_hold_justified> moreHeatThreshold = $more_heat_threshold°"
	log.trace "check_if_hold_justified> moreHCoolThreshold = $more_cool_threshold°"
	log.trace "check_if_hold_justified> lessHeatThreshold = $less_heat_threshold°"
	log.trace "check_if_hold_justified> lessCoolThreshold = $less_cool_threshold°"
	log.trace "check_if_hold_justified> heatTemp = $heatTemp°"
	log.trace "check_if_hold_justified> coolTemp = $coolTemp°"
	log.trace "check_if_hold_justified> programHeatTemp = $programHeatTemp°"
	log.trace "check_if_hold_justified> programCoolTemp = $programCoolTemp°"
	log.trace "check_if_hold_justified>state=${state}"
	float targetTstatTemp

	if (detailedNotif == 'true') {
		send("MonitorEcobeeTemp>needs Hold? currentProgName ${currentProgName},indoorTemp ${ecobeeTemp}°,progHeatSetPoint ${programHeatTemp}°,progCoolSetPoint ${programCoolTemp}°")
		send("MonitorEcobeeTemp>needs Hold? currentProgName ${currentProgName},indoorTemp ${ecobeeTemp}°,heatingSetPoint ${heatTemp}°,coolingSetPoint ${coolTemp}°")
	}
	if ((currentProgName.toUpperCase()=='AWAY') && (!residentsHaveBeenQuiet())) {
                
		ecobee.present()            
		send("MonitorEcobeeTemp>Program now set to Home, motion detected")
 		state.programSetTime = now()
 		state.programSetTimestamp = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
 		state.programHoldSet = 'Home'
		log.debug "MonitorEcobeeTemp>Program now set to Home at ${state.programSetTimestamp}, motion detected"
		 /* Get latest heat and cool setting points after climate adjustment */
		programHeatTemp = ecobee.currentHeatingSetpoint.toFloat() // This is the heat temp associated to the current program
		programCoolTemp = ecobee.currentCoolingSetpoint.toFloat() // This is the cool temp associated to the current program
        
	} else if ((currentProgName.toUpperCase()!='SLEEP')  && (residentsHaveBeenQuiet())) { // Do not adjust the program when asleep
                
		ecobee.away()          
		send("MonitorEcobeeTemp>Program now set to Away,no motion detected")
 		state.programSetTime = now()
 		state.programSetTimestamp = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
 		state.programHoldSet = 'Away'
		log.debug "MonitorEcobeeTemp>Program now set to Away at: ${state.programSetTimestamp}, motion detected"
        
		 /* Get latest heat and cool setting points after climate adjustment */
		programHeatTemp = ecobee.currentHeatingSetpoint.toFloat() // This is the heat temp associated to the current program
		programCoolTemp = ecobee.currentCoolingSetpoint.toFloat() // This is the cool temp associated to the current program
        
	}
	log.trace "MonitorEcobeeTemp>state=${state}"
	if (ecobeeMode == 'cool') {

		if (location.mode.toUpperCase() != 'AWAY') { // increase cooling settings only if mode != Away
			log.trace(
				"check_if_hold_needed>evaluate: moreCoolThreshold= ${more_cool_threshold}° vs. outdoorTemp ${outdoorTemp}°")
			log.trace(
				"check_if_hold_needed>evaluate: moreCoolThresholdHumidity= ${humidity_threshold}% vs. outdoorHum ${outdoorHumidity}%")
			if (detailedNotif == 'true') {
				send("MonitorEcobeeTemp>eval:  moreCoolThreshold ${more_cool_threshold}° vs.outdoorTemp ${outdoorTemp}°")
				send("MonitorEcobeeTemp>eval:  moreCoolThresholdHumidty ${humidity_threshold}% vs. outdoorHum ${outdoorHumidity}%")
			}
			if (outdoorTemp >= more_cool_threshold) {
				targetTstatTemp = (programCoolTemp - temp_diff).round(1)
				ecobee.setCoolingSetpoint(targetTstatTemp)
				send("MonitorEcobeeTemp>cooling setPoint now =${targetTstatTemp.toString()}°,outdooTemp >=${more_cool_threshold}°")
			} else if (outdoorHumidity >= humidity_threshold) {
				float median_temp = (less_cool_threshold + more_cool_threshold / 2).round(1) // Increase cooling settings based on median temp
				if (detailedNotif == 'true') {
					String medianTempFormat = String.format('%2.1f', median_temp)
					send("MonitorEcobeeTemp>eval: cool median temp ${medianTempFormat}° vs.outdoorTemp ${outdoorTemp}°")
				}
				if (outdoorTemp > median_temp) { // Only increase cooling settings when outdoorTemp < median_temp
					targetTstatTemp = (programCoolTemp - temp_diff).round(1)
					ecobee.setCoolingSetpoint(targetTstatTemp)
					send("MonitorEcobeeTemp>cooling setPoint now=${targetTstatTemp.toString()}°,outdoorHum >=${humidity_threshold}%")

				}
			}
		}
		log.trace("check_if_hold_needed>evaluate: lessCoolThreshold= ${less_cool_threshold} vs.outdoorTemp ${outdoorTemp}°")
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>evaluate: lessCoolThreshold ${less_cool_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		}
		if (outdoorTemp <= less_cool_threshold) {
			targetTstatTemp = (programCoolTemp + temp_diff).round(1)
			ecobee.setCoolingSetpoint(targetTstatTemp)
			send(
				"Monitor&SetEcobeeTemp>cooling setPoint now=${targetTstatTemp.toString()}°,outdoor temp <=${less_cool_threshold}°"
			)

		}
	} else if (ecobeeMode == 'heat') {
		if (location.mode.toUpperCase() != 'AWAY') { // increase heating settings only if mode != Away
			log.trace("check_if_hold_needed>evaluate: moreHeatThreshold ${more_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			log.trace(
				"check_if_hold_needed>evaluate: moreHeatThresholdHumidity= ${humidity_threshold}% vs.outdoorHumidity ${outdoorHumidity}%")
			if (detailedNotif == 'true') {
				send("MonitorEcobeeTemp>eval:  moreHeatThreshold ${more_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
				send("MonitorEcobeeTemp>eval:  moreHeatThresholdHumidty=${humidity_threshold}% vs.outdoorHumidity ${outdoorHumidity}%")
			}
			if (outdoorTemp <= more_heat_threshold) {
				targetTstatTemp = (programHeatTemp + temp_diff).round(1)
				ecobee.setHeatingSetpoint(targetTstatTemp)
				send(
					"MonitorEcobeeTemp>heating setPoint now= ${targetTstatTemp.toString()}°,outdoorTemp <=${more_heat_threshold}°")
			} else if (outdoorHumidity >= humidity_threshold) {

				float median_temp = (less_heat_threshold + more_heat_threshold / 2).round(1) // Increase heating settings based on median temp
				if (detailedNotif == 'true') {
					String medianTempFormat = String.format('%2.1f', median_temp)
					send("MonitorEcobeeTemp>eval: heat median temp ${medianTempFormat}° vs.outdoorTemp ${outdoorTemp}°")
				}
				if (outdoorTemp < median_temp) { // Only increase heating settings when outdoorTemp < median_temp
					targetTstatTemp = (programHeatTemp + temp_diff).round(1)
					ecobee.setHeatingSetpoint(targetTstatTemp)
					send("MonitorEcobeeTemp>heating setPoint now=${targetTstatTemp.toString()}°,outdoorHum >=${humidity_threshold}%")
				}
			}
		}
		log.trace("MonitorEcobeeTemp>eval:lessHeatThreshold=${less_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>eval:  lessHeatThreshold ${less_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		}
		if (outdoorTemp >= less_heat_threshold) {
			targetTstatTemp = (programHeatTemp - temp_diff).round(1)
			ecobee.setHeatingSetpoint(targetTstatTemp)
			send("MonitorEcobeeTemp>heating setPoint now=${targetTstatTemp.toString()}°,outdoor temp>= ${less_heat_threshold}°")
		}
	}

	log.debug "End of Fcn check_if_hold_needed"
}

private def check_if_hold_justified() {
	log.debug "Begin of Fcn check_if_hold_justified"
	Integer humidity_threshold = givenHumThreshold ?: 85 // by default, 85% is the outdoor Humidity's threshold for more cooling
	float more_heat_threshold, more_cool_threshold
	float less_heat_threshold, less_cool_threshold
	float temp_diff
	Integer delay = givenInterval ?: 59 // By default, do it every hour

	def scale = getTemperatureScale()
	if (scale == 'C') {
		temp_diff = givenTempDiff ?: 2 // 2°C temp differential is applied by default
		more_heat_threshold = (givenMoreHeatThreshold != null) ? givenMoreHeatThreshold : (-15) // by default, -15°C is the outdoor temp's threshold for more heating
		more_cool_threshold = (givenMoreCoolThreshold != null) ? givenMoreCoolThreshold : 30 // by default, 30°C is the outdoor temp's threshold for more cooling
		less_heat_threshold = (givenLessHeatThreshold != null) ? givenLessHeatThreshold : 10 // by default, 10°C is the outdoor temp's threshold for less heating
		less_cool_threshold = (givenLessCoolThreshold != null) ? givenLessCoolThreshold : 22 // by default, 22°C is the outdoor temp's threshold for less cooling

	} else {
		temp_diff = givenTempDiff ?: 5 // 5°F temp differential is applied by default
		more_heat_threshold = (givenMoreHeatThreshold != null) ? givenMoreHeatThreshold : 10 // by default, 10°F is the outdoor temp's threshold for more heating
		more_cool_threshold = (givenMoreCoolThreshold != null) ? givenMoreCoolThreshold : 85 // by default, 85°F is the outdoor temp's threshold for more cooling
		less_heat_threshold = (givenLessHeatThreshold != null) ? givenLessHeatThreshold : 50 // by default, 50°F is the outdoor temp's threshold for less heating
		less_cool_threshold = (givenLessCoolThreshold != null) ? givenLessCoolThreshold : 75 // by default, 75°F is the outdoor temp's threshold for less cooling
	}
	String currentProgName = ecobee.currentProgramScheduleName
	float heatTemp = ecobee.currentHeatingSetpoint.toFloat()
	float coolTemp = ecobee.currentCoolingSetpoint.toFloat()
	float programHeatTemp = ecobee.currentProgramHeatTemp.toFloat()
	float programCoolTemp = ecobee.currentProgramCoolTemp.toFloat()
	Integer ecobeeHumidity = ecobee.currentHumidity
	float ecobeeTemp = ecobee.currentTemperature.toFloat()

	Integer outdoorHumidity = outdoorSensor.currentHumidity
	float outdoorTemp = outdoorSensor.currentTemperature.toFloat()
	String ecobeeMode = ecobee.currentThermostatMode
	log.trace "check_if_hold_justified> location.mode = $location.mode"
	log.trace "check_if_hold_justified> ecobee Mode = $ecobeeMode"
	log.trace "check_if_hold_justified> currentProgName = $currentProgName"
	log.trace "check_if_hold_justified> outdoorTemp = $outdoorTemp°"
	log.trace "check_if_hold_justified> temp_diff = $temp_diff°"
	log.trace "check_if_hold_justified> moreHeatThreshold = $more_heat_threshold°"
	log.trace "check_if_hold_justified> moreHCoolThreshold = $more_cool_threshold°"
	log.trace "check_if_hold_justified> lessHeatThreshold = $less_heat_threshold°"
	log.trace "check_if_hold_justified> lessCoolThreshold = $less_cool_threshold°"
	log.trace "check_if_hold_justified> heatTemp = $heatTemp°"
	log.trace "check_if_hold_justified> coolTemp = $coolTemp°"
	log.trace "check_if_hold_justified> programHeatTemp = $programHeatTemp°"
	log.trace "check_if_hold_justified> programCoolTemp = $programCoolTemp°"
	log.trace "check_if_hold_justified>state=${state}"

	if (detailedNotif == 'true') {
		send("MonitorEcobeeTemp>Hold justified? currentProgName ${currentProgName},indoorTemp ${ecobeeTemp}°,progHeatSetPoint ${programHeatTemp}°,progCoolSetPoint ${programCoolTemp}°")
		send("MonitorEcobeeTemp>Hold justified? currentProgName ${currentProgName},indoorTemp ${ecobeeTemp}°,heatingSetPoint ${heatTemp}°,coolingSetPoint ${coolTemp}°")
	}
	if ((state?.programHoldSet == 'Away') && (!residentsHaveBeenQuiet())) {
		log.trace("check_if_hold_justified>it's not been quiet since ${state.programSetTimestamp},resume program")
		ecobee.resumeProgram()
		send("MonitorEcobeeTemp>resumed current program, motion detected")
        
        /* Get latest heat and cool setting points after climate adjustment */
		heatTemp = ecobee.currentHeatingSetpoint.toFloat() // This is the heat temp associated to the current program
		coolTemp = ecobee.currentCoolingSetpoint.toFloat() // This is the cool temp associated to the current program
		reset_state_values()        
	} else if (state?.programHoldSet == 'Away') {
		log.trace("check_if_hold_justified>quiet since ${state.programSetTimestamp}, 'Away' hold justified")
		send("MonitorEcobeeTemp>quiet since ${state.programSetTimestamp}, 'Away' hold justified")
 		return // hold justified, no more adjustments
    
	}    
	if ((state?.programHoldSet == 'Home') && (residentsHaveBeenQuiet())) {
		log.trace("check_if_hold_justified>it's been quiet since ${state.programSetTimestamp},resume program")
		ecobee.resumeProgram()
		send("MonitorEcobeeTemp>resumed program, no motion detected")
        
        	/* Get latest heat and cool setting points after climate adjustment */
		heatTemp = ecobee.currentHeatingSetpoint.toFloat() // This is the heat temp associated to the current program
		coolTemp = ecobee.currentCoolingSetpoint.toFloat() // This is the cool temp associated to the current program
		reset_state_values()
	} else if (state?.programHoldSet == 'Home') { 
		log.trace("MonitorEcobeeTemp>not quiet since ${state.programSetTimestamp}, 'Home' hold justified")
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>not quiet since ${state.programSetTimestamp}, 'Home' hold justified")
		}
		return // hold justified, no more adjustments
        
 	}    
	if (ecobeeMode == 'cool') {
		log.trace("check_if_hold_justified>evaluate: moreCoolThreshold=${more_cool_threshold} vs. outdoorTemp ${outdoorTemp}°")
		log.trace(
			"check_if_hold_justified>evaluate: moreCoolThresholdHumidity= ${humidity_threshold}% vs. outdoorHumidity ${outdoorHumidity}%")
		log.trace("check_if_hold_justified>evaluate: lessCoolThreshold= ${less_cool_threshold} vs.outdoorTemp ${outdoorTemp}°")
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>eval:  moreCoolThreshold ${more_cool_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			send("MonitorEcobeeTemp>eval:  lessCoolThreshold ${less_cool_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			send("MonitorEcobeeTemp>eval:  moreCoolThresholdHumidity ${humidity_threshold}% vs. outdoorHumidity ${outdoorHumidity}%")
		}
		if ((outdoorTemp > less_cool_threshold) && (outdoorTemp < more_cool_threshold) &&
        		(outdoorHumidity < humidity_threshold)) {
			send("MonitorEcobeeTemp>resuming program, ${less_cool_threshold}° < outdoorTemp <${more_cool_threshold}°")
			ecobee.resumeProgram()
		} else {
			if (detailedNotif == 'true') {
				send("MonitorEcobeeTemp>Hold justified, cooling setPoint=${coolTemp}°")
			}
			float actual_temp_diff = (programCoolTemp - coolTemp).round(1)
			actual_temp_diff = (actual_temp_diff > 0) ? actual_temp_diff : (actual_temp_diff * -1)
			if (detailedNotif == 'true') {
				send("MonitorEcobeeTemp>eval: actual_temp_diff ${actual_temp_diff.toString()}° vs.temp_diff ${temp_diff}°")
			}
			if (actual_temp_diff > temp_diff) {
				if (detailedNotif == 'true') {
					send("MonitorEcobeeTemp>Hold differential too big (${actual_temp_diff.toString()}), needs adjustment")
				}
				check_if_hold_needed() // call it to adjust cool temp
			}
		}
	} else if (ecobeeMode == 'heat') {
		log.trace("MonitorEcobeeTemp>eval: moreHeatingThreshold ${more_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		log.trace(
			"check_if_hold_justified>evaluate: moreHeatingThresholdHum= ${humidity_threshold}% vs. outdoorHum ${outdoorHumidity}%")
		log.trace("MonitorEcobeeTemp>eval:lessHeatThreshold=${less_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		if (detailedNotif == 'true') {
			send("MonitorEcobeeTemp>eval: moreHeatThreshold ${more_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			send("MonitorEcobeeTemp>eval: lessHeatThreshold ${less_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			send("MonitorEcobeeTemp>eval: moreHeatThresholdHum ${humidity_threshold}% vs. outdoorHum ${outdoorHumidity}%")
		}
        if ((outdoorTemp > more_heat_threshold) && (outdoorTemp < less_heat_threshold) && 
			(outdoorHumidity < humidity_threshold)) {
			send("MonitorEcobeeTemp>resuming program, ${less_heat_threshold}° < outdoorTemp > ${more_heat_threshold}°")
			ecobee.resumeProgram()
		} else {
			if (detailedNotif == 'true') {
				send("MonitorEcobeeTemp>Hold justified, heating setPoint=${heatTemp}°")
			}
			float actual_temp_diff = (heatTemp - programHeatTemp).round(1)
			actual_temp_diff = (actual_temp_diff > 0) ? actual_temp_diff : (actual_temp_diff * -1)
			if (detailedNotif == 'true') {
				send("MonitorEcobeeTemp>eval: actualTempDiff ${actual_temp_diff.toString()}° vs.givenTempDiff ${temp_diff}°")
			}
			if (actual_temp_diff > temp_diff) {
				if (detailedNotif == 'true') {
					send("MonitorEcobeeTemp>Hold differential too big ${actual_temp_diff.toString()}, needs adjustment")
				}
				check_if_hold_needed() // call it to adjust heat temp
			}
		}
	}
	log.debug "End of Fcn check_if_hold_justified"
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
Status 
