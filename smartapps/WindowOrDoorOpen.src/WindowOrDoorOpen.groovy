/**
 *  WindowOrDoorOpen!
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
 * Compatible with MyEcobee device available at my store:
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 *
 */
definition(
	name: "WindowOrDoorOpen!",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Choose some contact sensors and get a notification (with voice as an option) when they are left open for too long.  Optionally, turn off the HVAC and set it back to cool/heat when window/door is closed",
	category: "Safety & Security",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("About") {
		paragraph "WindowOrDoorOpen!, the smartapp that warns you if you leave a door or window open (with voice as an option);" +
			"it will turn off your thermostats (optional) after a delay and restore their mode when the contact is closed." +
    		"The smartapp can track up to 30 contacts and can keep track of 6 open contacts at the same time due to ST scheduling limitations"
		paragraph "Version 2.4.3" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.me/ecomatiqhomes",
					title:"Paypal donation..."            
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
 				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}
	section("Notify me when the following door(s) or window contact(s) are left open (maximum 30 contacts)...") {
		input "theSensor", "capability.contactSensor", multiple:true, required: true
	}
	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
		input "phone", "phone", title: "Send a Text Message?", required: false
		input "frequency", "number", title: "Delay between notifications in minutes", description: "", required: false
		input "givenMaxNotif", "number", title: "Max Number of Notifications", description: "Only used if no thermostat is provided as input", required: false
	}
	section("Use Speech capability to warn the residents [optional]") {
		input "theVoice", "capability.speechSynthesis", required: false, multiple: true
		input "powerSwitch", "capability.switch", title: "On/off switch for Voice notifications? [optional]", required: false
	}
	section("And, when contact is left open for more than this delay in minutes [default=5 min.]") {
		input "maxOpenTime", "number", title: "Minutes?", required:false
	}
	section("Turn off the thermostat(s) after the delay;revert this action when closed [optional]") {
		input "tstats", "capability.thermostat", multiple: true, required: false
	}
	section("What do I use as the Master on/off switch to enable/disable other smartapps' processing? [optional,ex.for zoned heating/cooling solutions]") {
		input (name:"masterSwitch", type:"capability.switch", required: false, description: "Optional")
	}

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

def initialize() {	
	def MAX_CONTACT=30
	state?.lastThermostatMode = null
	// subscribe to all contact sensors to check for open/close events
	state?.status=[]    
	state?.count=[]    
	state.lastThermostatMode = ""
    
	int i=0    
	theSensor.each {
		subscribe(theSensor[i], "contact.closed", "sensorTriggered${i}")
		subscribe(theSensor[i], "contact.open", "sensorTriggered${i}")
		state?.status[i] = " "
		state?.count[i] = 0
		i++   
		if (i>=MAX_CONTACT) {
			return       
		}        
	}  

}

def sensorTriggered0(evt) {
	int i=0
	sensorTriggered(evt,i)    
}

def sensorTriggered1(evt) {
	int i=1
	sensorTriggered(evt,i)    
}

def sensorTriggered2(evt) {
	int i=2
	sensorTriggered(evt,i)    
}

def sensorTriggered3(evt) {
	int i=3
	sensorTriggered(evt,i)    
}

def sensorTriggered4(evt) {
	int i=4
	sensorTriggered(evt,i)    
}

def sensorTriggered5(evt) {
	int i=5
	sensorTriggered(evt,i)    
}

def sensorTriggered6(evt) {
	int i=6
	sensorTriggered(evt,i)    
}

def sensorTriggered7(evt) {
	int i=7
	sensorTriggered(evt,i)    
}

def sensorTriggered8(evt) {
	int i=8
	sensorTriggered(evt,i)    
}

def sensorTriggered9(evt) {
	int i=9
	sensorTriggered(evt,i)    
}

def sensorTriggered10(evt) {
	int i=10
	sensorTriggered(evt,i)    
}

def sensorTriggered11(evt) {
	int i=11
	sensorTriggered(evt,i)    
}

def motionEvtHandler12(evt) {
	int i=12
	sensorTriggered(evt,i)    
}

def sensorTriggered13(evt) {
	int i=13
	sensorTriggered(evt,i)    
}

def sensorTriggered14(evt) {
	int i=14
	sensorTriggered(evt,i)    
}

def sensorTriggered15(evt) {
	int i=15
	sensorTriggered(evt,i)    
}

def sensorTriggered16(evt) {
	int i=16
	sensorTriggered(evt,i)    
}

def sensorTriggered17(evt) {
	int i=17
	sensorTriggered(evt,i)    
}

def sensorTriggered18(evt) {
	int i=18
	sensorTriggered(evt,i)    
}

def sensorTriggered19(evt) {
	int i=19
	sensorTriggered(evt,i)    
}

def sensorTriggered20(evt) {
	int i=20
	sensorTriggered(evt,i)    
}

def sensorTriggered21(evt) {
	int i=21
	sensorTriggered(evt,i)    
}

def sensorTriggered22(evt) {
	int i=22
	sensorTriggered(evt,i)    
}

def sensorTriggered23(evt) {
	int i=23
	sensorTriggered(evt,i)    
}

def sensorTriggered24(evt) {
	int i=24
	sensorTriggered(evt,i)    
}

def sensorTriggered25(evt) {
	int i=25
	sensorTriggered(evt,i)    
}

def sensorTriggered26(evt) {
	int i=26
	sensorTriggered(evt,i)    
}

def sensorTriggered27(evt) {
	int i=27
	sensorTriggered(evt,i)    
}

def sensorTriggered28(evt) {
	int i=28
	sensorTriggered(evt,i)    
}


def sensorTriggered29(evt) {
	int i=29
	sensorTriggered(evt,i)    
}


def sensorTriggered(evt, indice=0) {
	def delay = (frequency) ?: 1	
	def freq = delay * 60
	def max_open_time_in_min = maxOpenTime ?: 5 // By default, 5 min. is the max open time

	if (evt.value == "closed") {
		restore_tstats_mode()
		def msg = "your ${theSensor[indice]} is now closed"
		send("WindowOrDoorOpen>${msg}")
		if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
			theVoice.setLevel(30)
			theVoice.speak(msg)
		}
		clearStatus(indice)
	} else if ((evt.value == "open") && (state?.status[indice] != "scheduled")) {
		def takeActionMethod= "takeAction${indice}"       
		runIn(freq, "${takeActionMethod}",[overwrite: false])
		state?.status[indice] = "scheduled"
		log.debug "${theSensor[indice]} is now open and will be checked every ${delay} minute(s) by ${takeActionMethod}"
	}
}


def takeAction0() {
	int i=0
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction1() {
	int i=1
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction2() {
	int i=2
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction3() {
	int i=3
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction4() {
	int i=4
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction5() {
	int i=5
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction6() {
	int i=6
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction7() {
	int i=7
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction8() {
	int i=8
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction9() {
	int i=9
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction10() {
	int i=10
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction11() {
	int i=11
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction12() {
	int i=12
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction13() {
	int i=13
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction14() {
	int i=14
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction15() {
	int i=15
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction16() {
	int i=16
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction17() {
	int i=17
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction18() {
	int i=18
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction19() {
	int i=19
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction20() {
	int i=20
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction21() {
	int i=21
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction22() {
	int i=22
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction23() {
	int i=23
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction24() {
	int i=24
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction25() {
	int i=25
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction26() {
	int i=26
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction27() {
	int i=27
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction28() {
	int i=28
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}

def takeAction29() {
	int i=29
	log.debug ("about to call takeAction() for ${theSensor[i]}")    
	takeAction(i)
}


def takeAction(indice=0) {	
	def takeActionMethod
	def delay = (frequency) ?: 1		    
	def freq =  delay * 60
	def maxNotif = (givenMaxNotif) ?: 5
	def max_open_time_in_min = maxOpenTime ?: 5 // By default, 5 min. is the max open time
	def msg
    
	def contactState = theSensor[indice].currentState("contact")
	log.trace "takeAction>${theSensor[indice]}'s contact status = ${contactState.value}, state.status=${state.status[indice]}, indice=$indice"
	if ((state?.status[indice] == "scheduled") && (contactState.value == "open")) {
		state.count[indice] = state.count[indice] + 1
		log.debug "${theSensor[indice]} was open too long, sending message (count=${state.count[indice]})"
		def openMinutesCount = state.count[indice] * delay
		msg = "your ${theSensor[indice]} has been open for more than ${openMinutesCount} minute(s)!"
		send("WindowOrDoorOpen>${msg}")
		if ((masterSwitch) && (masterSwitch?.currentSwitch=="on")) {
			log.debug "master switch ${masterSwitch} is now off"
			masterSwitch.off() // set the master switch to off as there is an open contact        
		}        
		if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
			theVoice.setLevel(30)
			theVoice.speak(msg)
		}

		if ((tstats) && (openMinutesCount > max_open_time_in_min)) {
						        
			save_tstats_mode()        
			tstats.off()
			            
			msg = "thermostats are now turned off after ${max_open_time_in_min} minutes"
			send("WindowDoorOpen>${msg}")
		}
		if ((!tstats) && (state.count[indice] > maxNotif)) {
			// stop the repeated notifications if there is no thermostats provided and we've reached maxNotif
			clearStatus(indice)
			takeActionMethod= "takeAction${indice}"       
			unschedule("${takeActionMethod}")
			msg = "maximum notifications ($maxNotif) reached for  ${theSensor[indice]}, unscheduled $takeActionMethod"
			log.debug msg            
			return
		}
		takeActionMethod= "takeAction${indice}"       
		msg = "contact still open at ${theSensor[indice]}, about to reschedule $takeActionMethod"
		log.debug msg            
		runIn(freq, "${takeActionMethod}", [overwrite: false])
	} else if (contactState.value == "closed") {
		restore_tstats_mode()
		clearStatus(indice)
		takeActionMethod= "takeAction${indice}"       
		unschedule("${takeActionMethod}")
		msg = "contact closed at ${theSensor[indice]}, unscheduled $takeActionMethod"
		log.debug msg            
	}
}

def clearStatus(indice=0) {
	state?.status[indice] = " "
	state?.count[indice] = 0
}


private void save_tstats_mode() {

	if ((!tstats)  || (state.lastThermostatMode)) { // If state already saved, then keep it
		return    
	} 
	tstats.each {
		it.poll() // to get the latest value at thermostat            
		state.lastThermostatMode = state.lastThermostatMode + "${it.currentThermostatMode}" + ","
	}    
	log.debug "save_tstats_mode>state.lastThermostatMode= $state.lastThermostatMode"

}


private void restore_tstats_mode() {
	def msg
	def MAX_CONTACT=30
    
	log.debug "restore_tstats_mode>checking if all contacts are closed..."
	for (int j = 0;(j < MAX_CONTACT); j++)  {
		if (!theSensor[j]) continue
		def contactState = theSensor[j].currentState("contact")
		log.trace "restore_tstats_mode>For ${theSensor[j]}, Contact's status = ${contactState.value}, indice=$j"
		if (contactState.value == "open") {
			return
		}
	}  
	if ((masterSwitch) && (masterSwitch?.currentSwitch=="off")) {
			log.debug "master switch ${masterSwitch} is back on"
			masterSwitch.on() // set the master switch to on as there is no more any open contacts        
	}        

	if (!tstats) {
		return    
	}    

	if (state.lastThermostatMode) {
		def lastThermostatMode = state.lastThermostatMode.toString().split(',')
		int i = 0
        
		tstats.each {
			def lastSavedMode = lastThermostatMode[i].trim()
			if (lastSavedMode) {
				log.debug "restore_tstats_mode>about to set ${it}, back to saved thermostatMode=${lastSavedMode}"
				if (lastSavedMode == 'cool') {
					it.cool()
				} else if (lastSavedMode.contains('heat')) {
					it.heat()
				} else if (lastSavedMode == 'auto') {
					it.auto()
				} else if (lastSavedMode == 'eco') {
					it.eco()
				} else {
					it.off()
				}
				msg = "thermostat ${it}'s mode is now set back to ${lastSavedMode}"
				send("WindowOrDoorOpen>${theSensor} closed, ${msg}")
				if ((theVoice) && (powerSwitch?.currentSwitch == "on")) { //  Notify by voice only if the powerSwitch is on
					theVoice.speak(msg)
				}
                
			}
			i++
		}
	}        
	state.lastThermostatMode = ""
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
