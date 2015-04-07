/**
 *  ecobeeManageClimate
 *
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
 */
definition(
	name: "ecobeeManageClimate",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Allows a user to manage ecobee's climates",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)


preferences {
	section("About") {
		paragraph "ecobeeManageClimate, the smartapp that manages your ecobee climates ['creation', 'update', 'delete']" 
		paragraph "Version 1.9\n\n" +
			"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
			"Copyright©2014 Yves Racine"
		href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
		description: "http//github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("For this ecobee thermostat") {
		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
	}
	section("Create (if not present) or update this climate") {
		input "climateName", "text", title: "Climate Name"
	}
	section("Or delete the Climate [default=false]") {
		input "deleteClimate", "Boolean", title: "delete?", metadata: [values: ["true", "false"]], required: false
	}
	section("Substitute Climate name in schedule (used for delete)") {
		input "subClimateName", "text", title: "Climate Name", required: false
	}
	section("Cool Temp [default = 75°F/23°C]") {
		input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
	}
	section("Heat Temp [default=72°F/21°C]") {
		input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
	}
	section("isOptimized [default=false]") {
		input "isOptimizedFlag", "Boolean", title: "isOptimized?", metadata: [values: ["true", "false"]], required: false
	}
	section("isOccupied [default=false]") {
		input "isOccupiedFlag", "Boolean", title: "isOccupied?", metadata: [values: ["true", "false"]], required: false
	}
	section("Cool Fan Mode [default=auto]") {
		input "givenCoolFanMode", "enum", title: "Cool Fan Mode ?", metadata: [values: ["auto", "on"]], required: false
	}
	section("Heat Fan Mode [default=false]") {
		input "givenHeatFanMode", "enum", title: "Heat Fan Mode ?", metadata: [values: ["auto", "on"]], required: false
	}
	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
		input "phoneNumber", "phone", title: "Send a text message?", required: false
	}

}



def installed() {

	ecobee.poll()
	subscribe(app, appTouch)

}


def updated() {


	ecobee.poll()
	subscribe(app, appTouch)


}

def appTouch(evt) {

	def heatTemp, coolTemp
	if (scale == 'C') {
		heatTemp = givenHeatTemp ?: 21 // by default, 21°C is the heat temp
		coolTemp = givenCoolTemp ?: 23 // by default, 23°C is the cool temp
	} else {
		heatTemp = givenHeatTemp ?: 72 // by default, 72°F is the heat temp
		coolTemp = givenCoolTemp ?: 75 // by default, 75°F is the cool temp
	}

	def isOptimized = (isOptimizedFlag != null) ? isOptimizedFlag : false // by default, isOptimized flag is false
	def isOccupied = (isOccupiedFlag != null) ? isOccupiedFlag : false // by default, isOccupied flag is false
	def coolFanMode = givenCoolFanMode ?: 'auto' // By default, fanMode is auto
	def heatFanMode = givenHeatFanMode ?: 'auto' // By default, fanMode is auto
	def deleteClimateFlag = (deleteClimate != null) ? deleteClimate : 'false'

	log.debug "ecobeeManageClimate> about to take actions"


	log.trace "ecobeeManageClimate>climateName =${climateName},deleteClimateFlag =${deleteClimateFlag},subClimateName= ${subClimateName}, isOptimized=${isOptimized}" +
		",isOccupied=${isOccupied},coolTemp = ${coolTemp},heatTemp = ${heatTemp},coolFanMode= ${coolFanMode}, heatFanMode= ${heatFanMode}"

	if (deleteClimateFlag == 'true') {
		send("ecobeeManageClimate>about to delete climateName = ${climateName}")
		ecobee.deleteClimate(null, climateName, subClimateName)

	} else {

		send("ecobeeManageClimate>about to create or update climateName = ${climateName}")
		ecobee.updateClimate(null, climateName, deleteClimateFlag, subClimateName,
			coolTemp, heatTemp, isOptimized, isOccupied, coolFanMode, heatFanMode)
	}

}

private send(msg) {
	if (sendPushMessage != "No") {
		log.debug("sending push message")
		sendPush(msg)
	}

	if (phone) {
		log.debug("sending text message")
		sendSms(phone, msg)
	}

	log.debug msg
}
