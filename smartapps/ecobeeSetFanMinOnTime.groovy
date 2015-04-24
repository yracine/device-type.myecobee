/**
 *  ecobeeSetFanMinOnTime
 *
 *  Copyright 2015 Yves Racine
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
    name: "ecobeeSetFanMinOnTime",
    namespace: "yracine",
    author: "Yves Racine",
    description: "ecobeeSetFanMinOnTime, the smartapp that sets your ecobee's fan to circulate for a minimum time (in minutes) per hour",
    category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)
preferences {


	page(name: "selectThermostats", title: "Thermostats", install: false , uninstall: true, nextPage: "selectProgram") {
		section("About") {
			paragraph "ecobeeSetFanMinOnTime, the smartapp that sets your ecobee's fan to circulate for a minimum time (in minutes) per hour." 
			paragraph "Version 0.9\n\n" +
				"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
				"Copyright©2015 Yves Racine"
			href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
			description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Change the following ecobee thermostat(s)...") {
			input "thermostats", "capability.thermostat", title: "Which thermostat(s)", multiple: true
		}
	}
	page(name: "selectProgram", title: "Ecobee Programs", content: "selectProgram")
	page(name: "Notifications", title: "Notifications Options", install: true, uninstall: true) {
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required:
				false
			input "phone", "phone", title: "Send a Text Message?", required: false
		}
        	section([mobileOnly:true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
	}
}


def selectProgram() {
    def ecobeePrograms = thermostats[0].currentClimateList.toString().minus('[').minus(']').tokenize(',')
	log.debug "programs: $ecobeePrograms"

	return dynamicPage(name: "selectProgram", title: "Select Ecobee Program", install: false, uninstall: true, nextPage:
			"Notifications") {
		section("Select Program") {
			input "givenClimate", "enum", title: "When change to this ecobee program?", options: ecobeePrograms, required: true
		}
		section("Set the minimum fan runtime per hour for this program [default: 10 min. per hour]") {
			input "givenFanMinTime", "number", title: "Minimum fan runtime in minutes", required: false
		}

        
	}
}


def installed() {
	subscribe(thermostats,"climateName",changeFanMinOnTime)    
	subscribe(app, changeFanMinOnTime)
}

def updated() {
	unsubscribe()
	subscribe(thermostats,"climateName",changeFanMinOnTime)    
	subscribe(app, changeFanMinOnTime)
}


def changeFanMinOnTime(evt) {


	if ((evt.value != givenClimate) && (evt.value != 'touch')) {
		log.debug ("changeFanMinOnTime>not right climate (${evt.value}), doing nothing...")
		return
	}    
	Integer min_fan_time = (givenFanMinTime != null) ? givenFanMinTime : 10 //  10 min. fan time per hour by default
    
	def message = "ecobeeSetFanMinOnTime>changing fanMinOnTime to ${min_fan_time}.."
	send(message)

	thermostats?.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}"])

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
