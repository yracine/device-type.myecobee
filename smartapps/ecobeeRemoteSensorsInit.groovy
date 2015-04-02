/**
 *  ecobee3RemoteSensorsInit
 *
 *  Copyright 2015 Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable lax or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
	name: "ecobee3RemoteSensorsInit",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Create individual ST sensors for all selected ecobee3's remote sensors and update them on a regular basis (interval chosen by the user).",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

import groovy.json.JsonSlurper

preferences {

	page(name: "selectThermostat", title: "Ecobee Thermostat", install: false, uninstall: true, nextPage: "selectMotionSensors") {
		section("About") {
			paragraph "ecobeeRemoteSensorsInit, the smartapp that creates individual ST sensors for your ecobee3's remote Sensors and polls them on a regular basis"
			paragraph "Version 1.1\n\n" +
				"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
				"Copyright©2015 Yves Racine"
			href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Select the ecobee thermostat") {
			input "ecobee", "capability.thermostat", title: "Which ecobee thermostat?"

		}
		section("Polling ecobee3's remote3 sensor(s) at which interval in minutes (range=[5..59],default =30 min.)?") {
			input "givenInterval", "number", title: "Interval", required: false
		}

	}
	page(name: "selectMotionSensors", title: "Ecobee Motion Sensors", content: "selectMotionSensors", nextPage: "selectTempSensors")
	page(name: "selectTempSensors", title: "Ecobee Temperature Sensors", content: "selectTempSensors", nextPage: "Notifications")
	page(name: "Notifications", title: "Notifications Options", install: true, uninstall: true) {
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
			input "phone", "phone", title: "Send a Text Message?", required: false
		}
		section([mobileOnly: true]) {
			label title: "Assign a name for this SmartApp", required: false
			mode title: "Set for specific mode(s)", required: false
		}
	}
}




def selectMotionSensors() {

	/* Get the list of remote sensors available 
	 */
	ecobee.generateRemoteSensorEvents("", 'true')
	def ecobeeSensors
    
	String remoteSensorData=ecobee.currentRemoteSensorData.toString()
	if (remoteSensorData != null) {    
		ecobeeSensors = new JsonSlurper().parseText(remoteSensorData)
	} else {
		log.error("selectMotionSensors>sensorRemoteData is empty, exiting")
		return        
	}    
	log.debug "selectMotionSensors>ecobeeSensors data = $ecobeeSensors"
	def sensors = [: ]

	if (ecobeeSensors?.size() > 0) {

		for (i in 0..ecobeeSensors.size() - 1) {


			for (j in 0..ecobeeSensors[i].capability.size() - 1) {

				if (ecobeeSensors[i].capability[j].type == 'occupancy') {

					if (settings.trace) {
						log.debug "selectMotionSensors>>looping at i=${i},found occupancy sensor ${ecobeeSensors[i].capability[j]} at j=${j}"
					}

					def dni = [app.id, ecobeeSensors[i].name, getMotionSensorChildName(), ecobeeSensors[i].id].join('.')
					sensors[dni] = ecobeeSensors[i].name
				}
			}
		}

	}


	log.debug "selectMotionSensors> sensors= $sensors"


	def chosenSensors = dynamicPage(name: "selectMotionSensors", title: "Select Motion Sensors", install: false, uninstall: true) {
		section("Select Motion Sensors") {
			input(name: "motionSensors", title: "", type: "enum", required: false, multiple: true, description: "Tap to choose", metadata: [values: sensors])
		}
	}
	return chosenSensors
}


def selectTempSensors() {
	def ecobeeSensors
    
	String remoteSensorData=ecobee.currentRemoteSensorData.toString()
	if (remoteSensorData != null) {    
		ecobeeSensors = new JsonSlurper().parseText(remoteSensorData)
	} else {
		log.error("selectMotionSensors>sensorRemoteData is empty, exiting")
		return        
	}    
	log.debug "selectTempSensors>ecobeeSensors data = $ecobeeSensors"

	def sensors = [: ]
	if (ecobeeSensors?.size() > 0) {
		for (i in 0..ecobeeSensors.size() - 1) {

			for (j in 0..ecobeeSensors[i].capability.size() - 1) {

				if (ecobeeSensors[i].capability[j].type == 'temperature') {

					if (settings.trace) {
						log.debug "selectTempSensors>>looping at i=${i},found temperature sensor ${ecobeeSensors[i].capability[j]} at j=${j}"
					}

					def dni = [app.id, ecobeeSensors[i].name, getTempSensorChildName(), ecobeeSensors[i].id].join('.')
					sensors[dni] = ecobeeSensors[i].name
				}
			}
		}

	}


	log.debug "selectTempSensors> sensors: $sensors"


	def chosenSensors = dynamicPage(name: "selectTempSensors", title: "Select Temperature Sensors", install: false, uninstall: true) {
		section("Select Temperature Sensors") {
			input(name: "tempSensors", title: "", type: "enum", required: false, multiple: true, description: "Tap to choose", metadata: [values: sensors])
		}
	}
	return chosenSensors
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

private def createMotionSensors() {

	def devices = motionSensors.collect {dni->
		def d = getChildDevice(dni)
		log.debug "initialize>Looping thru motion Sensors, found id $dni"

		if (!d) {
			def sensor_info = dni.tokenize('.')
			def sensorId = sensor_info.last()
			def sensorName = sensor_info[1]
			def sensorType = sensor_info[2]

			def labelName = "${ecobee.currentThermostatName} ${getMotionSensorChildName()} ${sensorName}"
			log.debug "About to create child device with id $dni, sensorId = $sensorId, labelName=  ${labelName}"
			d = addChildDevice(getChildNamespace(), getMotionSensorChildName(), dni, null, [label: "${labelName}", completedSetup: true])
			log.debug "created ${d.displayName} with id $dni"
		} else {
			log.debug "initialize>found ${d.displayName} with id $dni already exists"
		}

	}
	log.trace("ecobeeRemoteSensorsInit>created ${devices.size()} MyEcobee's Motion Sensors" ) 

}

private def createTempSensors() {

	def devices = tempSensors.collect {dni->
		def d = getChildDevice(dni)
		log.debug "initialize>Looping thru temp Sensors, found id $dni"

		if (!d) {
			def sensor_info = dni.tokenize('.')
			def sensorId = sensor_info.last()
			def sensorName = sensor_info[1]
			def sensorType = sensor_info[2]
			def labelName = "${ecobee.currentThermostatName} ${getTempSensorChildName()} ${sensorName}"

			log.debug "About to create child device with id $dni, sensorId = $sensorId, labelName=  ${labelName}"
			d = addChildDevice(getChildNamespace(), getTempSensorChildName(), dni, null, [label: "${labelName}", completedSetup: true])
			log.debug "created ${d.displayName} with id $dni"
		} else {
			log.debug "initialize>found ${d.displayName} with id $dni already exists"
		}            
	}
	log.trace("ecobeeRemoteSensorsInit>created ${devices.size()} MyEcobee's Temp Sensors")

}

private def sendWithDelay() {
	
	if (state.msg) {
		send(state.msg)
	}
}


private def deleteSensors() {

	def delete
	// Delete any that are no longer in settings
	if (!motionSensors) {
		log.debug "delete all Motion Sensors"
		delete = getChildDevices().findAll {
			it.device.deviceNetworkId.contains(getMotionSensorChildName())
		}
	} else {
		delete = getChildDevices().findAll {
			((it.device.deviceNetworkId.contains(getMotionSensorChildName())) && (!motionSensors.contains(it.device.deviceNetworkId)))
		}
	}
	log.trace("ecobeeRemoteSensorsInit>deleting ${delete.size()} MyEcobee's Motion Sensors")
	delete.each {
		deleteChildDevice(it.deviceNetworkId)
	}


	if (!tempSensors) {
		log.debug "delete all Temp Sensors"
		delete = getChildDevices().findAll {
			(it.device.deviceNetworkId.contains(getTempSensorChildName()))
		}
	} else {
		delete = getChildDevices().findAll {
			((it.device.deviceNetworkId.contains(getTempSensorChildName())) && (!tempSensors.contains(it.device.deviceNetworkId)))
		}
	}
	log.trace("ecobeeRemoteSensorsInit>deleting ${delete.size()} MyEcobee's Temp Sensors")
	delete.each {
		deleteChildDevice(it.deviceNetworkId)
	}

}


def initialize() {
	log.debug "initialize>begin"
	state.msg=""
    
	subscribe(ecobee, "remoteSensorOccData", updateMotionSensors)
	subscribe(ecobee, "remoteSensorTmpData", updateTempSensors)

	deleteSensors()
	createMotionSensors()
	createTempSensors()


	takeAction()


	Integer delay = givenInterval ?: 30 // By default, do it every 30 min.
	if ((delay < 5) || (delay > 59)) {
		log.error "Scheduling delay not in range (${delay} min.), exiting"
		runIn(30, "sendNotifDelayNotInRange")
		return
	}
	log.trace("ecobeeRemoteSensorsInit>scheduling takeAction every ${delay} minutes")

	schedule("0 0/${delay} * * * ?", takeAction) 


/* 
	if (delay >= 5 && delay <10) {
		runEvery5Minutes(takeAction)
	} else if (delay >= 10 && delay <15) {
		runEvery10Minutes(takeAction)
	} else if (delay >= 15 && delay <30) {
		runEvery15Minutes(takeAction)
	} else if (delay >= 30 && delay <60) {
		runEvery30Minutes(takeAction)
	} else {        
		runEvery1Hour(takeAction)
	}
*/

	log.debug "initialize>end"
}

def takeAction() {
	log.trace "takeAction>begin"
	ecobee.poll()
	log.trace "takeAction>about to call generateRemoteSensorEvents()"
	ecobee.generateRemoteSensorEvents("", 'true')
	updateTempSensors()
	updateMotionSensors()
	log.trace "takeAction>end"
}


private def sendNotifDelayNotInRange() {

	send "MyEcobeeRemoteSensorsInit>scheduling delay (${givenInterval} min.) not in range, please restart..."

}

private updateMotionSensors(evt) {
	log.debug "updateMotionSensors>evt name=$evt.name, evt.value= $evt.value"

	updateMotionSensors()
}

private updateMotionSensors() {

	def ecobeeSensors = ecobee.currentRemoteSensorOccData.toString().split(",,")
	log.debug "updateTempSensors>ecobeeRemoteSensorOccData= $ecobeeSensors"

	if (ecobeeSensors.size() < 1) {

		log.debug "updateMotionSensors>no values found"
		return
	}
	for (i in 0..ecobeeSensors.size() - 1) {
		def ecobeeSensorDetails = ecobeeSensors[i].split(',')
		def ecobeeSensorId = ecobeeSensorDetails[0]
		def ecobeeSensorName = ecobeeSensorDetails[1]
		def ecobeeSensorType = ecobeeSensorDetails[2]
		String ecobeeSensorValue = ecobeeSensorDetails[3].toString()

		def dni = [app.id, ecobeeSensorName, getMotionSensorChildName(), ecobeeSensorId].join('.')

		log.debug "updateMotionSensors>looking for $dni"

		def device = getChildDevice(dni)

		if (device) {
			log.debug "updateTempSensors>ecobeeSensorId=$ecobeeSensorId"
			log.debug "updateTempSensors>ecobeeSensorName=$ecobeeSensorName"
			log.debug "updateTempSensors>ecobeeSensorType=$ecobeeSensorType"
			log.debug "updateTempSensors>ecobeeSensorValue=$ecobeeSensorValue"

			String status = (ecobeeSensorValue.contains('false')) ? "inactive" : "active"
			def isChange = device.isStateChange(device, "motion", status)
			def isDisplayed = isChange
			log.debug "device $device, found $dni,statusChanged=${isChange}, value= ${status}"

			device.sendEvent(name: "motion", value: status, isStateChange: isChange, displayed: isDisplayed)
		} else {

			log.debug "updateMotionSensors>couldn't find Motion Dectector device $ecobeeSensorName with dni $dni, probably not selected originally"
		}

	}

}

private updateTempSensors(evt) {	
	log.debug "updateTempSensors>evt name=$evt.name, evt.value= $evt.value"
	updateTempSensors()
}

private updateTempSensors() {

	def scale = getTemperatureScale()
    

	def ecobeeSensors = ecobee.currentRemoteSensorTmpData.toString().split(",,")

	log.debug "updateTempSensors>ecobeeRemoteSensorTmpData= $ecobeeSensors"

	if (ecobeeSensors.size() < 1) {

		log.debug "updateTempSensors>no values found"
		return
	}

	for (i in 0..ecobeeSensors.size() - 1) {

		def ecobeeSensorDetails = ecobeeSensors[i].split(',')
		def ecobeeSensorId = ecobeeSensorDetails[0]
		def ecobeeSensorName = ecobeeSensorDetails[1]
		def ecobeeSensorType = ecobeeSensorDetails[2]
		int ecobeeSensorValue = ecobeeSensorDetails[3].toInteger()


		def dni = [app.id, ecobeeSensorName, getTempSensorChildName(), ecobeeSensorId].join('.')

		log.debug "updateTempSensors>looking for $dni"

		def device = getChildDevice(dni)

		if (device) {

			log.debug "updateTempSensors>ecobeeSensorId= $ecobeeSensorId"
			log.debug "updateTempSensors>ecobeeSensorName= $ecobeeSensorName"
			log.debug "updateTempSensors>ecobeeSensorType= $ecobeeSensorType"
			log.debug "updateTempSensors>ecobeeSensorValue= $ecobeeSensorValue"
            
			Double tempValue
			String tempValueString
            
			if (scale == "F") {
				tempValue = getTemperature(ecobeeSensorValue).toDouble().round()
				tempValueString = String.format('%2d', tempValue.intValue())            
			} else {
				tempValue = getTemperature(ecobeeSensorValue).toDouble().round(1)
				tempValueString = String.format('%2.1f', tempValue)
			}                

			def isChange = device.isTemperatureStateChange(device, "temperature", tempValueString)
			def isDisplayed = isChange
			log.debug "device $device, found $dni,statusChanged=${isChange}, value= ${tempValueString}"

			device.sendEvent(name: "temperature", value: tempValueString, unit: scale, isStateChange: isChange, displayed: isDisplayed)
		} else {
			log.debug "updateTempSensors>couldn't find Temperature Sensor device $ecobeeSensorName with dni $dni, probably not selected originally"
		}

	}

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

private def getTemperature(value) {
	def farenheits = value
	if (getTemperatureScale() == "F") {
		return farenheits
	} else {
		return fToC(farenheits)
	}
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

def getChildNamespace() {
	"smartthings"
}
def getMotionSensorChildName() {
	"Motion Detector"
}
def getTempSensorChildName() {
	"Temperature Sensor"
}
