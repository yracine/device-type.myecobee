/**
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
 *  Take a series of actions in case of smoke or CO2 alert, i.e. turn on/flash the lights, turn on the siren, unlock the doors, turn
 *  off the thermostat(s), turn off the alarm system, etc.
 *
 */


// Automatically generated. Make future change here.
definition(
    name: "FireCO2Alarm",
    namespace: "yracine",
    author: "yracine@yahoo.com",
    description: "Turn on all the lights/thermostat in case of a fire/CO2 alarm, unlock the doors, disarm the alarm system & open the garage door ",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {


    section("Select smoke detector(s)..."){
        input "smoke_detectors", "capability.smokeDetector", title: "Which one(s)...?", multiple: true
    }
    section( "Low battery warning" ){
        input "lowBattThreshold", "number", title: "Low Batt Threshold % (default 10%)", required: false
    }
    section("Unlock the doors") {
        input "locks", "capability.lock", multiple: true,required: false
    }
    section("Open this Garage Door in case of CO2...") {
        input "garageSwitch", "capability.switch",title: "Which Garage Door Switch"
    }
    section("Only If this Garage's Contact is closed") {  
        input "garageMulti", "capability.contactSensor",title: "Which Garage Door Contact"
    }
    section("Turn off the thermostat(s)") {
        input "tstat", "capability.thermostat", title: "Thermostat(s)", multiple:true, required: false
    }
    section("Dismarm the alarm system if armed") {
        input "alarmSwitch", "capability.contactSensor", title: "Alarm System"
    }
    section("Flash/turn on the ligths..."){
        input "switches", "capability.switch", title: "These lights", multiple: true
        input "numFlashes", "number", title: "This number of times (default 20)", required: false
    }
    section("Time settings in milliseconds (optional)"){
        input "givenOnFor", "number", title: "On for (default 1000)", required: false
        input "givenOffFor", "number", title: "Off for (default 1000)", required: false
    }
    section("And activate the siren") {
        input "securityAlert", "capability.alarm", title: "Security Alert"
    }
    section("Clear alarm threshold (defaults to 1 min)") {
        input "clearAlarmThreshold", "decimal", title: "Number of minutes", required: false
    }
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone", "phone", title: "Send a Text Message?", required: false
    }
}


def installed()
{
    initialize()
}
 
def updated()
{
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
    if ((tstat != null) && (tstat != "")) {
        subscribe(tstat, "thermostatMode", thermostatModeHandler)
    }    
}

def thermostatModeHandler(evt) {
    log.debug "thermostat mode: $evt.value"
}

def garageDoorContact(evt)
{
    log.info "garageDoorContact, $evt.name: $evt.value"
}

def doorUnlockedHandler(evt) {
    log.debug "Lock ${locks} was: ${evt.value}"

}

 
def smokeHandler(evt) {
    def SMOKE_ALERT = 'detected_SMOKE'
    def CLEAR_ALERT = 'clear'
    def DETECTED_ALERT = 'detected'
    def TESTED_ALERT = 'tested'
    
    log.trace "$evt.value: $evt, $settings"
    
    String theMessage
    
    if (evt.value == TESTED_ALERT) {
    	theMessage = "${evt.displayName} was tested for smoke."
        sendMsg("FireCO2Alarm>${theMessage}")
        takeActions(evt.value)

    } else if (evt.value == CLEAR_ALERT) {
    	theMessage = "${evt.displayName} is clear of smoke."
        sendMsg("FireCO2Alarm>${theMessage}")
        takeActions(CLEAR_ALERT)
        
    } else if (evt.value == DETECTED_ALERT) {
    	theMessage = "${evt.displayName} detected smoke!"
        sendMsg("FireCO2Alarm>${theMessage}")
        takeActions(SMOKE_ALERT)
    } else {
    	theMessage = ("Unknown event received from ${evt.name}")
        sendMsg("FireCO2Alarm>${theMessage}")
    }
    
}
 
 
def carbonMonoxideHandler(evt) {
    def CO2_ALERT = 'detected_CO2'
    def CLEAR_ALERT = 'clear'
    def DETECTED_ALERT = 'detected'
    def TESTED_ALERT = 'tested'

    log.trace "$evt.value: $evt, $settings"
    
    String theMessage
    
    if (evt.value == TESTED_ALERT) {
    	theMessage = "${evt.displayName} was tested for carbon monoxide."
        sendMsg("FireCO2Alarm>${theMessage}")
    } else if (evt.value == CLEAR_ALERT) {
    	theMessage = "${evt.displayName} is clear of carbon monoxide."
        sendMsg("FireCO2Alarm>${theMessage}")
        takeActions(CLEAR_ALERT)
    } else if (evt.value == DETECTED_ALERT) {
    	theMessage = "${evt.displayName} detected carbon monoxide!"
        sendMsg("FireCO2Alarm>${theMessage}")
        takeActions(CO2_ALERT)
    } else {
    	theMessage = "Unknown event received from ${evt.name}"
        sendMsg("FireCO2Alarm>${theMessage}")
    }
    
}
 
def batteryHandler(evt) {
    log.trace "$evt.value: $evt, $settings"
    String theMessage
    int battLevel = evt.integerValue
    
    log.debug "${evt.displayName} has battery of ${battLevel}"
    
    if (battLevel < lowBattThreshold ?: 10) {
    	theMessage = "${evt.displayName} has battery of ${battLevel}"
        sendMsg("FireCO2Alarm>${theMessage}")
    }
}
 

def alarmSwitchContact(evt)

{
    log.info "alarmSwitchContact, $evt.name: $evt.value"
}

def clearAlert() { 
    securityAlert.off()                                // Turned off the security alert
    
    sendMsg("FireCO2Alarm>Cleared, set the security alert off...")
     
    tstat?.auto()
    sendMsg("FireCO2Alarm>Cleared, thermostat(s) mode is now set to auto")
}


private takeActions(String alert) {

    def CO2_ALERT = 'detected_CO2'
    def SMOKE_ALERT = 'detected_SMOKE'
    def CLEAR_ALERT = 'clear'
    def DETECTED_ALERT = 'detected'
    def TESTED_ALERT = 'tested'

// Proceed with the following actions when clear alert

    if (alert == CLEAR_ALERT) {

        def delay = (clearAlarmThreshold ?: 1) * 60               // default is 3 minutes
        //  Wait a certain delay before clearing the alert

        sendMsg("FireCO2Alarm>Cleared, wait for ${delay} seconds...")
        runIn ( delay, "clearAlert", [overwrite:false])
        
        return
    }
   
    if ((alert != TESTED_ALERT) && (alert != SMOKE_ALERT) && (alert != CO2_ALERT)) {
        log.debug "Not in test mode nor smoke/CO2 detected, exiting..."  
        return
    }
   
// Proceed with the following actions in case of SMOKE or CO2 alert


    securityAlert.on()                                     // Turned on the security alert
    sendMsg("FireCO2Alarm>Security Alert on...")

    if (alarmSwitch.currentContact == "closed") {
        log.debug "alarm system is on, about to disarm it..."  
        alarmSwitch.off()                                  // disarm the alarm system
        sendMsg("FireCO2Alarm>Alarm system disarmed")
    }
    tstat?.off()                                           // Turn off the thermostats
    if (location.mode != 'Away') {
       locks?.unlock()                                     // Unlock the locks if mode is not 'Away'
	   sendMsg("FireCO2Alarm>Unlocked the doors...")
       if (((alert == CO2_ALERT) || (alert == TESTED_ALERT)) && (garageMulti.currentContact == "closed")) {
           log.debug "garage door is closed,about to open it following CO2 alert..."  
           garageSwitch.on()                               // Open the garage door if it is closed
           sendMsg("FireCO2Alarm>Opened the garage door following CO2 alert...")
       }

    }

    flashLights()                                          // Flash the lights
    sendMsg("FireCO2Alarm>Flashed the lights...")

    def now = new Date().getTime()                         // Turn the switches on at night
    astroCheck()
    if (now > state.setTime) {                                
        switches?.on()
        sendMsg("FireCO2Alarm>Turned on the lights at night")

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
        def initialActionOn = switches.collect{it.currentSwitch != "on"}
        def delay = 1L
        numFlashes.times {
            log.trace "Switch on after  $delay msec"
            switches.eachWithIndex {s, i ->
                if (initialActionOn[i]) {
                    s.on(delay: delay)
                
                }
                else {
                    s.off(delay:delay)
                }
            }
            delay += onFor
            log.trace "Switch off after $delay msec"
            switches.eachWithIndex {s, i ->
                if (initialActionOn[i]) {
                    s.off(delay: delay)

                }
                else {
                    s.on(delay:delay)
                }
            }
            delay += offFor
        }
    }
}


private sendMsg(theMessage) {
    log.debug "Sending message: ${theMessage}"
    if (phoneNumber) {
        sendSms(phoneNumber, theMessage)
    }
 
    if (sendPushMessage == "Yes") {
        sendPush(theMessage)
    }
}
