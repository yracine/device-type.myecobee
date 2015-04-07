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
 *  Manage Vacation events at Ecobee Thermostat(s)
 * 
 */
// Automatically generated. Make future change here.
definition(
	name: "ecobeeManageVacation",
	namespace: "yracine",
	author: "Yves Racine",
	description: "manages your ecobee vacation settings ['creation', 'update', 'delete']",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "ecobeeManageVacation, the smartapp that manages your ecobee vacation settings ['creation', 'update', 'delete']"
		paragraph "Version 1.9\n\n" +
			"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
			"Copyright©2014 Yves Racine"
		href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
		description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("For the Ecobee thermostat(s)") {
		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
	}
	section("Create this Vacation Name") {
		input "vacationName", "text", title: "Vacation Name"
	}
    
	section("Or delete the vacation [default=false]") {
		input "deleteVacation", "Boolean", title: "delete?", metadata: [values: ["true", "false"]], required: false
	}
	section("Cool Temp for vacation, [default = 80°F/27°C]") {
		input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
	}
	section("Heat Temp for vacation [default= 60°F/14°C]") {
		input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
	}
	section("Start date for the vacation, [format = DD-MM-YYYY]") {
		input "givenStartDate", "text", title: "Beginning Date"
	}
	section("Start time for the vacation [HH:MM 24HR]") {
		input "givenStartTime", "text", title: "Beginning time"
	}
	section("End date for the vacation [format = DD-MM-YYYY]") {
		input "givenEndDate", "text", title: "End Date"
	}
	section("End time for the vacation [HH:MM 24HR]") {
		input "givenEndTime", "text", title: "End time"
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
	log.debug "ecobeeManageVacation> about to take actions"
	def minHeatTemp, minCoolTemp
	def scale = getTemperatureScale()
	if (scale == 'C') {
		minHeatTemp = givenHeatTemp ?: 14 // by default, 14°C is the minimum heat temp
		minCoolTemp = givenCoolTemp ?: 27 // by default, 27°C is the minimum cool temp
	} else {
		minHeatTemp = givenHeatTemp ?: 60 // by default, 60°F is the minimum heat temp
		minCoolTemp = givenCoolTemp ?: 80 // by default, 80°F is the minimum cool temp
	}
	def vacationStartDateTime = null
	String dateTime = null

	dateTime = givenStartDate + " " + givenStartTime
	log.debug("Start datetime= ${datetime}")
	vacationStartDateTime = new Date().parse('d-M-yyyy H:m', dateTime)

	dateTime = givenEndDate + " " + givenEndTime
	log.debug("End datetime= ${datetime}")
	def vacationEndDateTime = new Date().parse('d-M-yyyy H:m', dateTime)

	if (deleteVacation == 'false') {
		// You may want to change to ecobee.createVacation('serial number list',....) if you own EMS thermostat(s)

		log.debug("About to call iterateCreateVacation for ${vacationName}")
		ecobee.iterateCreateVacation('', vacationName, minCoolTemp, minHeatTemp, vacationStartDateTime,
			vacationEndDateTime)
		send("ecobeeManageVacation> vacationName ${vacationName} created")
	} else {

		ecobee.iterateDeleteVacation('', vacationName)
		send("ecobeeManageVacation> vacationName ${vacationName} deleted")

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
