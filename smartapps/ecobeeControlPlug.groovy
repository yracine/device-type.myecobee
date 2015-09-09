/**
 *  ecobeeControlPlug
 *
 *  Copyright 2014 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
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
	name: "ecobeeControlPlug",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Control a plug attached to an ecobee device",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)


preferences {
	section("About") {
		paragraph "ecobeeControlPlug, the smartapp that control your ecobee connected sensor or plug"
		paragraph "Version 1.1.3" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
				title:"Paypal donation..."
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("For this Ecobee thermostat") {
		input "ecobee", "device.myEcobeeDevice", title: "Ecobee Thermostat"
	}
	section("Control this SmartPlug Name") {
		input "plugName", "text", title: "SmartPlug Name"
	}
	section("Target control State") {
		input "plugState", "enum", title: "Control State?", metadata: [values: ["on", "off", "resume", ]]
	}
	section("Hold Type") {
		input "givenHoldType", "enum", title: "Hold Type?", metadata: [values: ["dateTime", "nextTransition", "indefinite"]]
	}
	section("For 'dateTime' holdType, Start date for the hold (format = DD-MM-YYYY)") {
		input "givenStartDate", "text", title: "Beginning Date", required: false
	}
	section("For 'dateTime' holdType, Start time for the hold (HH:MM,24HR)") {
		input "givenStartTime", "text", title: "Beginning time", required: false
	}
	section("For 'dateTime' holdType, End date for the hold (format = DD-MM-YYYY)") {
		input "givenEndDate", "text", title: "End Date", required: false
	}
	section("For 'dateTime' holdType, End time for the hold (HH:MM,24HR)") {
		input "givenEndTime", "text", title: "End time", required: false
	}


}


def installed() {

	initialize()
}


def updated() {

	unsubscribe()	
	initialize()


}

def initialize() {
	ecobee.poll()
	subscribe(app, appTouch)
}

private void sendMsgWithDelay() {

	if (state?.msg) {
		send state.msg
	}
}

def appTouch(evt) {
	log.debug "ecobeeControlPlug> about to take actions"
	def plugSettings = [holdType: "${givenHoldType}"]


	if (givenHoldType == "dateTime") {

		if ((!givenStartDate) || (!givenEndDate) || (!givenStartTime) || (!givenEndTime)) {
			state?.msg="ecobeeControlPlug>holdType=dateTime and dates/times are not valid for controlling plugName ${plugName}"
			log.error state.msg
			runIn(30, "sendMsgWithDelay")
			return
		}
		plugSettings = [holdType: "dateTime", startDate: "${givenStartDate}", startTime: "${givenStartTime}", endDate: "${givenEndDate}", endTime: "${givenEndTime}"]
	}
	log.debug("About to call controlPlug for thermostatId=${thermostatId}, plugName=${plugName}")
	ecobee.controlPlug("", plugName, plugState, plugSettings)
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
