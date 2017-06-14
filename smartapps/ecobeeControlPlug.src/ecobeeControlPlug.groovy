/**
 *  ecobeeControlPlug
 *
 *  Copyright 2014 Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 *  in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 *  Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 *  to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *
 * Software Distribution is restricted and shall be done only with Developer's written approval.
 * 
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
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
		paragraph "ecobeeControlPlug, the smartapp that controls your ecobee connected sensor or plug"
		paragraph "Version 1.1.4" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.me/ecomatiqhomes",
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
