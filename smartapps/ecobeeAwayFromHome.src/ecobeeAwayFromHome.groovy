/***
 *
 *  Copyright 2014 Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 *  in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 *  Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 *  to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 *
 *  Away from Home with Ecobee Thermostat
 *  Turn off the lights, turn on the security alarm, and lower the settings at ecobee when away from home
 *
 *  Software Distribution is restricted and shall be done only with Developer's written approval.
 *
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
 
// Automatically generated. Make future change here.
definition(
	name: "ecobeeAwayFromHome",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Away From Home",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "ecobeeAwayFromHome, the smartapp that sets your ecobee thermostat to 'Away' or to some specific settings when all presences leave your home"
		paragraph "Version 1.9.5" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.me/ecomatiqhomes",
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}
	section("When all of these people leave home") {
		input "people", "capability.presenceSensor", multiple: true
	}
	section("And there is no motion at home on these sensors [optional]") {
		input "motions", "capability.motionSensor", title: "Where?", multiple: true, required: false
	}
	section("Turn off these lights") {
		input "switches", "capability.switch", title: "Switch", multiple: true, required: optional
	}
	section("And activate the alarm system [optional]") {
		input "alarmSwitch", "capability.contactSensor", title: "Alarm Switch", required: false
	}
	section("Set the ecobee thermostat(s)") {
		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat(s)", multiple: true
	}
	section("Heating set Point for the thermostat [default = 60°F/14°C]") {
		input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
	}
	section("Cooling set Point for the thermostat [default = 80°F/27°C]") {
		input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
	}
	section("Or set the ecobee to this Climate Name (ex. Away)") {
		input "givenClimateName", "text", title: "Climate Name", required: false
	}
	section("Lock these locks [optional]") {
		input "locks", "capability.lock", title: "Locks?", required: false, multiple: true
	}
	section("Arm this(ese) camera(s) [optional]") {
		input "cameras", "capability.imageCapture", title: "Cameras", multiple: true, required: false
	}
	section("Trigger these actions when home has been quiet for [default=3 minutes]") {
		input "residentsQuietThreshold", "number", title: "Time in minutes", required: false
	}
	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
		input "phone", "phone", title: "Send a Text Message?", required: false
	}
	section("Detailed Notifications") {
		input "detailedNotif", "bool", title: "Detailed Notifications?", required: false
	}

}


def installed() {
	log.debug "Installed with settings: ${settings}"
	log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
	initialize()

}



def updated() {
	log.debug "Updated with settings: ${settings}"
	log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
	unsubscribe()
	initialize()
}

private initialize() {
	subscribe(people, "presence", presence)
	subscribe(alarmSwitch, "contact", alarmSwitchContact)
	if (motions != null && motions != "") {
		subscribe(motions, "motion", motionEvtHandler)
	}
	subscribe(app, appTouch)    
}


def alarmSwitchContact(evt) {
	log.info "alarmSwitchContact, $evt.name: $evt.value"

	if ((alarmSwitch.currentContact == "closed") && residentsHaveBeenQuiet() && everyoneIsAway()) {
		if (detailedNotif) {
			send("AwayFromHome>alarm system just armed, take actions")
		}
		log.debug "alarm is armed, nobody at home"
		takeActions()
	}
}


def motionEvtHandler(evt) {
	if (evt.value == "active") {
		state.lastIntroductionMotion = now()
		log.debug "Motion at home..."
	}
}


private residentsHaveBeenQuiet() {

	def threshold = residentsQuietThreshold ?: 3 // By default, the delay is 3 minutes
	Integer delay = threshold * 60

	def result = true
	def t0 = new Date(now() - (threshold * 60 * 1000))
	for (sensor in motions) {
		def recentStates = sensor.statesSince("motion", t0)
		if (recentStates.find {it.value == "active"}) {
			result = false
			break
		}
	}
	log.debug "residentsHaveBeenQuiet: $result"
	return result
}


def presence(evt) {
	def threshold = residentsQuietThreshold ?: 3 // By default, the delay is 3 minutes
	Integer delay = threshold * 60

	log.debug "$evt.name: $evt.value"
	if (evt.value == "not present") {
		def person = getPerson(evt)
		if (detailedNotif) {
			send("AwayFromHome> ${person.displayName} not present at home")
		}
		log.debug "checking if everyone is away  and quiet at home"
		if (residentsHaveBeenQuiet()) {

			if (everyoneIsAway()) {
				if (detailedNotif) {
					send("AwayFromHome>Quiet at home...")
				}
				runIn(delay, "takeActions")
			} else {
				log.debug "Not everyone is away, doing nothing"
				if (detailedNotif) {
					send("AwayFromHome>Not everyone is away, doing nothing..")
				}
			}
		} else {

			log.debug "Things are not quiet at home, doing nothing"
			if (detailedNotif) {
				send("AwayFromHome>Things are not quiet at home...")
			}
		}
	} else {
		log.debug "Still present; doing nothing"
	}
}

def appTouch(evt) {
	log.debug ("ecobeeAwayFromHome>location.mode= $location.mode, givenClimate=${givenClimateName}, about to takeAction")

	takeActions() 
}


def takeActions() {
	Integer thresholdMinutes = 2 // check that the security alarm is close in a 2-minute delay
	Integer delay = 60 * thresholdMinutes
	def msg, minHeatTemp, minCoolTemp

	def scale = getTemperatureScale()
	if (scale == 'C') {
		minHeatTemp = givenHeatTemp ?: 14 // by default, 14°C is the minimum heat temp
		minCoolTemp = givenCoolTemp ?: 27 // by default, 27°C is the minimum cool temp
	} else {
		minHeatTemp = givenHeatTemp ?: 60 // by default, 60°F is the minimum heat temp
		minCoolTemp = givenCoolTemp ?: 80 // by default, 80°F is the minimum cool temp
	}
	//  Making sure everybody is away and no motion at home

	if (everyoneIsAway() && residentsHaveBeenQuiet()) {
		send("AwayFromHome>Nobody is at home, and it's quiet, about to take actions")
		if (alarmSwitch?.currentContact == "open") {
			alarmSwitch.on() // arm the alarm system
			if (detailedNotif) {
				log.debug "alarm is not set, arm it..."
				send(msg)
			}
		}
		if ((givenClimateName != null) && (givenClimateName != "")) {
			ecobee.each {
				it.setClimate('', givenClimateName) // Set to the climateName
			}                
		} else {

			// Set heating and cooling points at ecobee
			ecobee.each {
				it.setHold('', minCoolTemp, minHeatTemp, null, null)
			}               
		}

		msg = "AwayFromHome>${ecobee} thermostats' settings are now lower"
		if (detailedNotif ) {
			log.info msg
			send(msg)
		}

		locks?.lock() // lock the locks 		
		msg = "AwayFromHome>Locked the locks"
		if ((locks) && (detailedNotif)) {
			log.info msg
			send(msg)
		}

		switches?.off() // turn off the lights		
		msg = "AwayFromHome>Switched off all switches"
		if ((switches) && (detailedNotif)) {
			log.info msg
			send(msg)
		}


		cameras?.alarmOn() // arm the cameras
		msg = "AwayFromHome>cameras are now armed"
		if ((cameras) && (detailedNotif)) {
			log.info msg
			send(msg)
		}
		if (alarmSwitch) {
			runIn(delay, "checkAlarmSystem", [overwrite: false]) // check that the alarm system is armed
		}
	}


}

private checkAlarmSystem() {
	if (alarmSwitch.currentContact == "open") {
		if (detailedNotif) {
			send("AwayFromHome>alarm still not activated,repeat...")
		}
		alarmSwitch.on() // try to arm the alarm system again
	}


}

private everyoneIsAway() {
	def result = true
	for (person in people) {
		if (person.currentPresence == "present") {
			result = false
			break
		}
	}
	log.debug "everyoneIsAway: $result"
	return result
}

private getPerson(evt) {
	people.find {
		evt.deviceId == it.id
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
