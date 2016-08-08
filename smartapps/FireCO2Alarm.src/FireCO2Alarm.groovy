/**
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
 *  Take a series of actions in case of smoke or CO2 alert, i.e. turn on/flash the lights, turn on the siren, unlock the doors, turn
 *  off the thermostat(s), turn off the alarm system, etc.
 *
 *  Software Distribution is restricted and shall be done only with Developer's written approval.
 *
 *  N.B. Compatible with MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
 
// Automatically generated. Make future change here.
definition(
	name: "FireCO2Alarm",
	namespace: "yracine",
	author: "yracine@yahoo.com",
	description: "In case of a fire/CO2 alarm,turn on all the lights/turn off all thermostats, unlock the doors, disarm the alarm system & open the garage door when CO2 is detected ",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
	page(name: "actionsSettings", title: "actionsSettings")
	page(name: "otherSettings", title: "OtherSettings")


}

def actionsSettings() {
	dynamicPage(name: "actionsSettings", install: false, uninstall: true, nextPage: "otherSettings") {
		section("About") {
			paragraph "FireCO2Alarm, the smartapp that executes a series of actions when a Fire or CO2 alarm is triggerred"
			paragraph "Version 1.3" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
					title:"Paypal donation..."
			paragraph "Copyright©2014 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
					description: "http://github.com/yracine"
		}
		section("When these smoke/CO2 detector trigger, the following actions will be taken...") {
			input "smoke_detectors", "capability.smokeDetector", title: "Which Smoke/CO2 detector(s)?", multiple: true
		}
		section("Unlock the doors [optional]") {
			input "locks", "capability.lock", multiple: true, required: false
		}
		section("Open this Garage Door in case of CO2...") {
			input "garageSwitch", "capability.switch", title: "Which Garage Door Switch", required: false
		}
		section("Only If this Garage's Contact is closed") {
			input "garageMulti", "capability.contactSensor", title: "Which Garage Door Contact", required: false
		}
		section("Turn off the thermostat(s) [optional]") {
			input "tstat", "capability.thermostat", title: "Thermostat(s)", multiple: true, required: false
		}
		section("Disarm the alarm system if armed [optional]") {
			input "alarmSwitch", "capability.contactSensor", title: "Alarm System", required: false
		}
		section("Flash/turn on the lights...") {
			input "switches", "capability.switch", title: "These lights", multiple: true
			input "numFlashes", "number", title: "This number of times (default 20)", required: false
		}
		section("Time settings in milliseconds [optional]") {
			input "givenOnFor", "number", title: "On for (default 1000)", required: false
			input "givenOffFor", "number", title: "Off for (default 1000)", required: false
		}
		section("And activate the siren [optional]") {
			input "securityAlert", "capability.alarm", title: "Security Alert", required: false, multiple:true
		}
		section("Clear alarm threshold (default = 1 min) to revert actions[optional]") {
			input "clearAlarmThreshold", "decimal", title: "Number of minutes after clear alarm", required: false
		}
	}
}


def otherSettings() {
	dynamicPage(name: "otherSettings", title: "Other Settings", install: true, uninstall: false) {
		section("Detectors' low battery warning") {
			input "lowBattThreshold", "number", title: "Low Batt Threshold % (default 10%)", required: false
		}
		section("Use Speech capability to warn the residents (optional) ") {
			input "theVoice", "capability.speechSynthesis", required: false, multiple: true
		}
		section("What do I use for the Master on/off switch to enable/disable voice notifications? (optional)") {
			input "powerSwitch", "capability.switch", required: false
		}
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
			input "phone", "phone", title: "Send a Text Message?", required: false
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
	unsubscribe()
	initialize()
}

private initialize() {
	subscribe(smoke_detectors, "smoke", smokeHandler)
	subscribe(smoke_detectors, "carbonMonoxide", carbonMonoxideHandler)
	subscribe(smoke_detectors, "battery", batteryHandler)
	subscribe(locks, "lock", doorUnlockedHandler)
	subscribe(garageMulti, "contact", garageDoorContact)
	subscribe(alarmSwitch, "contact", alarmSwitchContact)
	if (tstat) {
		subscribe(tstat, "thermostatMode", thermostatModeHandler)
	}

	reset_state_variables()
}


private def reset_state_variables() {

	state.lastActivated = null
	state.lastThermostatMode = null

	if (tstat) {
		state.lastThermostatMode = " "
		tstat.each {
			log.debug "reset_state_variables>thermostat mode reset for $it"
			state.lastThermostatMode = state.lastThermostatMode + "${it.currentThermostatMode}" + ","
		}
	}
	log.debug "reset_state_variables>state.lastThermostatMode= $state.lastThermostatMode"

}


def thermostatModeHandler(evt) {
	log.debug "thermostat mode: $evt.value"
}

def garageDoorContact(evt) {
	log.info "garageDoorContact, $evt.name: $evt.value"
}

def doorUnlockedHandler(evt) {
	log.debug "Lock ${locks} was: ${evt.value}"

}


def smokeHandler(evt) {
	def SMOKE_ALERT = 'detected_SMOKE'
	def CLEAR_ALERT = 'clear'
	def CLEAR_SMOKE_ALERT = 'clear_SMOKE'
	def DETECTED_ALERT = 'detected'
	def TESTED_ALERT = 'tested'

	log.trace "$evt.value: $evt, $settings"

	String theMessage

	if (evt.value == TESTED_ALERT) {
		theMessage = "${evt.displayName} was tested for smoke."
		send("FireCO2Alarm>${theMessage}")
		takeActions(evt.value)

	} else if (evt.value == CLEAR_ALERT) {
		theMessage = "${evt.displayName} is clear of smoke."
		send("FireCO2Alarm>${theMessage}")
		takeActions(CLEAR_SMOKE_ALERT)

	} else if (evt.value == DETECTED_ALERT) {
		theMessage = "${evt.displayName} detected smoke!"
		send("FireCO2Alarm>${theMessage}")
		takeActions(SMOKE_ALERT)
	} else {
		theMessage = ("Unknown event received from ${evt.name}")
		send("FireCO2Alarm>${theMessage}")
	}

}


def carbonMonoxideHandler(evt) {
	def CO2_ALERT = 'detected_CO2'
	def CLEAR_CO2_ALERT = 'clear_CO2'
	def CLEAR_ALERT = 'clear'
	def DETECTED_ALERT = 'detected'
	def TESTED_ALERT = 'tested'

	log.trace "$evt.value: $evt, $settings"

	String theMessage

	if (evt.value == TESTED_ALERT) {
		theMessage = "${evt.displayName} was tested for carbon monoxide."
		send("FireCO2Alarm>${theMessage}")
	} else if (evt.value == CLEAR_ALERT) {
		theMessage = "${evt.displayName} is clear of carbon monoxide."
		send("FireCO2Alarm>${theMessage}")
		takeActions(CLEAR_CO2_ALERT)
	} else if (evt.value == DETECTED_ALERT) {
		theMessage = "${evt.displayName} detected carbon monoxide!"
		send("FireCO2Alarm>${theMessage}")
		takeActions(CO2_ALERT)
	} else {
		theMessage = "Unknown event received from ${evt.name}"
		send("FireCO2Alarm>${theMessage}")
	}

}

def batteryHandler(evt) {
	log.trace "$evt.value: $evt, $settings"
	String theMessage
	int battLevel = evt.integerValue

	log.debug "${evt.displayName} has battery of ${battLevel}"

	if (battLevel < lowBattThreshold ?: 10) {
		theMessage = "${evt.displayName} has battery of ${battLevel}"
		send("FireCO2Alarm>${theMessage}")
	}
}


def alarmSwitchContact(evt)

{
	log.info "alarmSwitchContact, $evt.name: $evt.value"
}

def clearAlert() {
	def msg

	if (securityAlert) {
		securityAlert.off() // Turned off the security alert
		msg = "turned security alert off"
		send("FireCO2Alarm>now clear, ${msg}")
		if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
			theVoice.setLevel(40)
			theVoice.speak(msg)
		}
	}
	/*    
	    if (locks) {
	        locks.lock()                                      // Lock the locks 
	        send("FireCO2Alarm>Cleared, locked all locks...")
	    }
	*/
	if (tstat) {
		if (state.lastThermostatMode) {
			def lastThermostatMode = state.lastThermostatMode.toString().split(',')
			int i = 0
			tstat.each {
				def lastSavedMode = lastThermostatMode[i].trim()

				if (lastSavedMode) {
					log.debug "About to set ${it}, back to saved thermostatMode=${lastSavedMode}"
					if (lastSavedMode == 'cool') {
						it.cool()
					} else if (lastSavedMode.contains('heat')) {
						it.heat()
					} else if (lastSavedMode == 'auto') {
						it.auto()
					}
					msg = "thermostat ${it}'s mode is now set back to ${lastThermostatMode[i]}"
					send("FireCO2Alarm>Cleared, ${msg}")
					if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
						theVoice.speak(msg)
					}
				}
				i++
			}
		} else {
			tstat.auto()
			msg = "thermostats set to auto"
			send("FireCO2Alarm>Cleared, ${msg}")
			if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
				theVoice.speak(msg)
			}
		}
	}
}


private takeActions(String alert) {
	def msg
	def CO2_ALERT = 'detected_CO2'
	def SMOKE_ALERT = 'detected_SMOKE'
	def CLEAR_ALERT = 'clear'
	def CLEAR_SMOKE_ALERT = 'clear_SMOKE'
	def CLEAR_CO2_ALERT = 'clear_CO2'
	def DETECTED_ALERT = 'detected'
	def TESTED_ALERT = 'tested'

	// Proceed with the following actions when clear alert

	if ((alert == CLEAR_SMOKE_ALERT) || (alert == CLEAR_CO2_ALERT)) {

		def delay = (clearAlarmThreshold ?: 1) * 60 // default is 1 minute
			//  Wait a certain delay before clearing the alert


		send("FireCO2Alarm>Cleared, wait for ${delay} seconds...")
		runIn(delay, "clearAlert", [overwrite: false])

		if ((alert == CLEAR_CO2_ALERT) && (garageMulti?.currentContact == "open")) {
			log.debug "garage door is open,about to close it following cleared CO2 alert..."
			garageSwitch?.on() // Open the garage door if it is closed
			msg = "closed the garage door following cleared CO2 alert"
			send("FireCO2Alarm>${msg}...")
			if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
				theVoice.setLevel(50)
				theVoice.speak(msg)
			}

		}

		return
	}

	if ((alert != TESTED_ALERT) && (alert != SMOKE_ALERT) && (alert != CO2_ALERT)) {
		log.debug "Not in test mode nor smoke/CO2 detected, exiting..."
		return
	}

	// Proceed with the following actions in case of SMOKE or CO2 alert


	//  Reset state variables 

	reset_state_variables()

	if (securityAlert) {
		securityAlert.on() // Turned on the security alert
		msg = "security Alert on"
		send("FireCO2Alarm>${msg}...")
		if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
			theVoice.setLevel(75)
			theVoice.speak(msg)
		}
	}
	if (alarmSwitch) {
		if (alarmSwitch.currentContact == "closed") {
			log.debug "alarm system is on, about to disarm it..."
			alarmSwitch.off() // disarm the alarm system
			msg = "alarm system disarmed"
			send("FireCO2Alarm>${msg}...")
			if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
				theVoice.setLevel(75)
				theVoice.speak(msg)
			}

		}
	}
	if (tstat) {
		tstat.off() // Turn off the thermostats
		msg = "turning off all thermostats"
		send("FireCO2Alarm>${msg}...")
		if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
			theVoice.speak(msg)
		}
	}
	if (!location.mode.contains('Away')) {
		if (locks) {
			locks.unlock() // Unlock the locks if mode is not 'Away'
			msg = "unlocked the doors"
			send("FireCO2Alarm>${msg}...")
			if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
				theVoice.speak(msg)
			}
		}
		if ((alert == CO2_ALERT) && (garageSwitch)) {
			if (garageMulti?.currentContact == "closed") {
				log.debug "garage door is closed,about to open it following CO2 alert..."
				garageSwitch.on() // Open the garage door if it is closed
				msg = "opened the garage door following CO2 alert"
				send("FireCO2Alarm>${msg}...")
				if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
					theVoice.speak(msg)
				}
			}
		}

	}

	flashLights() // Flash the lights
	msg = "flashed the lights"
	send("FireCO2Alarm>${msg}...")

	if (theVoice) {
		theVoice.speak(msg)
	}

	def now = new Date().getTime() // Turn the switches on at night
	astroCheck()
	if (now > state.setTime) {
		switches?.on()

		msg = "turned on the lights"
		send("FireCO2Alarm>${msg}")
		if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
			theVoice.speak(msg)
		}

	}



}

def astroCheck() {
	def s = getSunriseAndSunset(zipCode: zipCode)

	state.riseTime = s.sunrise.time
	state.setTime = s.sunset.time
	log.debug "rise: ${new Date(state.riseTime)}($state.riseTime), set: ${new Date(state.setTime)}($state.setTime)"
}



private flashLights() {
	def doFlash = true
	def onFor = givenOnFor ?: 1000
	def offFor = givenOffFor ?: 1000
	def numFlashes = numFlashes ?: 20

	log.debug "LAST ACTIVATED IS: ${state.lastActivated}"
	if (state.lastActivated) {
		def elapsed = now() - state.lastActivated
		def sequenceTime = (numFlashes + 1) * (onFor + offFor)
		doFlash = elapsed > sequenceTime
		log.debug "DO FLASH: $doFlash, ELAPSED: $elapsed, LAST ACTIVATED: ${state.lastActivated}"
	}

	if (doFlash) {
		log.debug "FLASHING $numFlashes times"
		state.lastActivated = now()
		log.debug "LAST ACTIVATED SET TO: ${state.lastActivated}"
		def initialActionOn = switches.collect {
			it.currentSwitch != "on"
		}
		def delay = 1 L
		numFlashes.times {
			log.trace "Switch on after  $delay msec"
			switches.eachWithIndex {
				s, i ->
					if (initialActionOn[i]) {
						s.on(delay: delay)

					} else {
						s.off(delay: delay)
					}
			}
			delay += onFor
			log.trace "Switch off after $delay msec"
			switches.eachWithIndex {
				s, i ->
					if (initialActionOn[i]) {
						s.off(delay: delay)

					} else {
						s.on(delay: delay)
					}
			}
			delay += offFor
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
