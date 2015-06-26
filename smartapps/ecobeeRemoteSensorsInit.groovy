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

preferences {

	page(name: "selectThermostat", title: "Ecobee Thermostat", install: false, uninstall: true, nextPage: "selectMotionSensors") {
		section("About") {
			paragraph "ecobeeRemoteSensorsInit, the smartapp that creates individual ST sensors for your ecobee3's remote Sensors and polls them on a regular basis"
			paragraph "Version 1.1.4\n\n" +
				"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
				"Copyright©2015 Yves Racine"
			href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Select the ecobee thermostat") {
			input "ecobee", "capability.thermostat", title: "Which ecobee thermostat?"

		}
		section("Polling ecobee3's remote3 sensor(s) at which interval in minutes (range=[15..59],default =30 min.)?") {
			input "givenInterval", "number", title: "Interval", required: false
		}

	}
	page(name: "selectMotionSensors", title: "Ecobee Motion Sensors", content: "selectMotionSensors", nextPage: "selectTempSensors")
	page(name: "selectTempSensors", title: "Ecobee Temperature/Humidity Sensors", content: "selectTempSensors", nextPage: "Notifications")
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

	/* Generate the list of all remote sensors available 
	*/
	ecobee.generateRemoteSensorEvents("", 'false')
    
	def sensors = [: ]

	/* Get only the list of all occupancy remote sensors available 
	*/

	def ecobeeSensors = ecobee.currentRemoteSensorOccData.toString().split(",,")

	log.debug "selectMotionSensors>ecobeeSensors= $ecobeeSensors"

	if ((!ecobeeSensors)  || (ecobeeSensors.size() < 1)) {

		log.debug "selectMotionSensors>no values found"
		return sensors

	}

	for (i in 0..ecobeeSensors.size() - 1) {

		def ecobeeSensorDetails = ecobeeSensors[i].split(',')
		def ecobeeSensorId = ecobeeSensorDetails[0]
		def ecobeeSensorName = ecobeeSensorDetails[1]
		def ecobeeSensorType = ecobeeSensorDetails[2]

		def dni = [app.id, ecobeeSensorName, getMotionSensorChildName(), ecobeeSensorId].join('.')

		sensors[dni] = ecobeeSensorName

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
    
	def sensors = [: ]
	/* Get only the list of all temperature remote sensors available 
	 */
	def ecobeeSensors = ecobee.currentRemoteSensorTmpData.toString().split(",,")

	log.debug "selectTempSensors>ecobeeSensors= $ecobeeSensors"

	if ((!ecobeeSensors)  || (ecobeeSensors.size() < 1)) {

		log.debug "selectTempSensors>no values found"
		return sensors

	}

	for (i in 0..ecobeeSensors.size() - 1) {

		def ecobeeSensorDetails = ecobeeSensors[i].split(',')
		def ecobeeSensorId = ecobeeSensorDetails[0]
		def ecobeeSensorName = ecobeeSensorDetails[1]
		def ecobeeSensorType = ecobeeSensorDetails[2]


		def dni = [app.id, ecobeeSensorName, getTempSensorChildName(), ecobeeSensorId].join('.')

		sensors[dni] = ecobeeSensorName

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
	unschedule()
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
		log.debug "delete all Temp/Humidity Sensors"
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
	state?.exceptionCount=0       
	state?.runtimeRevision=null
    
	subscribe(ecobee, "remoteSensorOccData", updateMotionSensors)
	subscribe(ecobee, "remoteSensorTmpData", updateTempSensors)
	subscribe(ecobee, "remoteSensorHumData", updateHumiditySensors)

	deleteSensors()
	createMotionSensors()
	createTempSensors()


	takeAction()


	Integer delay = givenInterval ?: 30 // By default, do it every 30 min.
	if ((delay < 15) || (delay > 59)) {
		log.error "Scheduling delay not in range (${delay} min.), exiting"
		runIn(30, "sendNotifDelayNotInRange")
		return
	}
	log.trace("ecobeeRemoteSensorsInit>scheduling takeAction every ${delay} minutes")

	schedule("0 0/${delay} * * * ?", takeAction) 



	log.debug "initialize>end"
}

def takeAction() {
	def MAX_EXCEPTION_COUNT=10	
	def msg
    
	log.trace "takeAction>begin"
	try {
		ecobee.poll()
		// reset exception counter            
		state?.exceptionCount=0       
	} catch (e) {
		state.exceptionCount=state.exceptionCount+1    
		log.error "ecobee3RemoteSensorInit>exception $e while trying to poll $ecobee, exceptionCount= ${state?.exceptionCount}" 
	}
	if (state?.exceptionCount>=MAX_EXCEPTION_COUNT) {
		msg="warning: too many exceptions (${state?.exceptionCount}), may need to re-authenticate at ecobee, please check logs..." 
		send "ecobee3RemoteSensorInit> ${msg}"
		log.error msg
	}    
    
	def newRuntimeRevision = ecobee.currentRuntimeRevision
	log.debug ("ecobee3RemoteSensorInit>state.runtimeRevision=${state?.runtimeRevision},newRuntimeRevision=${newRuntimeRevision}")
	if ((state?.runtimeRevision) && (state?.runtimeRevision == newRuntimeRevision)) { 
		log.debug ("ecobee3RemoteSensorInit>no runtime revision change since last poll(), no update action required...")
		return
	}
	state?.runtimeRevision = newRuntimeRevision
    
	log.trace "takeAction>about to call generateRemoteSensorEvents()"
	ecobee.generateRemoteSensorEvents("", 'false')
	    
	updateMotionSensors()
	updateTempSensors()    
/*	
	updateHumiditySensors()
*/
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
		def ecobeeSensorValue = ecobeeSensorDetails[3].toDouble()


		def dni = [app.id, ecobeeSensorName, getTempSensorChildName(), ecobeeSensorId].join('.')

		log.debug "updateTempSensors>looking for $dni"

		def device = getChildDevice(dni)

		if (device) {

			log.debug "updateTempSensors>ecobeeSensorId= $ecobeeSensorId"
			log.debug "updateTempSensors>ecobeeSensorName= $ecobeeSensorName"
			log.debug "updateTempSensors>ecobeeSensorType= $ecobeeSensorType"
			log.debug "updateTempSensors>ecobeeSensorValue= $ecobeeSensorValue"
            
            
			Double tempValue = getTemperature(ecobeeSensorValue).toDouble().round(1)
			String tempValueString = String.format('%2.1f', tempValue)

			def isChange = device.isTemperatureStateChange(device, "temperature", tempValueString)
			def isDisplayed = isChange
			log.debug "device $device, found $dni,statusChanged=${isChange}, value= ${tempValueString}"

			device.sendEvent(name: "temperature", value: tempValueString, unit: scale, isStateChange: isChange, displayed: isDisplayed)
		} else {
			log.debug "updateTempSensors>couldn't find Temperature Sensor device $ecobeeSensorName with dni $dni, probably not selected originally"
		}

	}

}


private updateHumiditySensors(evt) {	
	log.debug "updateHumiditySensors>evt name=$evt.name, evt.value= $evt.value"
	updateHumiditySensors()
}

private updateHumiditySensors() {


	def ecobeeSensors = ecobee.currentRemoteSensorHumData.toString().split(",,")

	log.debug "updateHumiditySensors>ecobeeRemoteSensorHumData= $ecobeeSensors"

	if (ecobeeSensors.size() < 1) {

		log.debug "updateHumiditySensors>no values found"
		return
	}

	for (i in 0..ecobeeSensors.size() - 1) {

		def ecobeeSensorDetails = ecobeeSensors[i].split(',')
		def ecobeeSensorId = ecobeeSensorDetails[0]
		def ecobeeSensorName = ecobeeSensorDetails[1]
		def ecobeeSensorType = ecobeeSensorDetails[2]
		def ecobeeSensorValue = ecobeeSensorDetails[3]


		def dni = [app.id, ecobeeSensorName, getTempSensorChildName(), ecobeeSensorId].join('.')

		log.debug "updateHumiditySensors>looking for $dni"

		def device = getChildDevice(dni)

		if (device) {

			log.debug "updateHumiditySensors>ecobeeSensorId= $ecobeeSensorId"
			log.debug "updateHumiditySensors>ecobeeSensorName= $ecobeeSensorName"
			log.debug "updateHumiditySensors>ecobeeSensorType= $ecobeeSensorType"
			log.debug "updateHumiditySensors>ecobeeSensorValue= $ecobeeSensorValue"
            
            
			Double humValue = ecobeeSensorValue.toDouble().round()
			String humValueString = String.format('%2d', humValue.intValue())

			def isChange = device.isStateChange(device, "humidity", humValueString)
			def isDisplayed = isChange
			log.debug "device $device, found $dni,statusChanged=${isChange}, value= ${humValue}"

			device.sendEvent(name: "humidity", value: humValueString, unit: '%', isStateChange: isChange, displayed: isDisplayed)
		} else {
			log.debug "updateHumiditySensors>couldn't find Humidity Sensor device $ecobeeSensorName with dni $dni, probably not selected originally"
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
