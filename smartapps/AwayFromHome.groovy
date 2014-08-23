/***
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
 * 
 *
 *  Away from Home with Ecobee Thermostat
 *  Turn off the lights, turn on the security alarm, and lower the settings at ecobee when away from home
*/


// Automatically generated. Make future change here.
definition(
    name: "Away From Home",
    namespace: "yracine",
    author: "Yves Racine",
    description: "Away From Home",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
    section("When all of these people leave home") {
        input "people", "capability.presenceSensor", multiple: true
    }
    
    section("And there is no motion at home on these sensors") {
        input "motions", "capability.motionSensor", title: "Where?",  multiple: true
    }
    section("Turn off these lights") {
        input "switches", "capability.switch", title: "Switch", multiple: true, required: optional
    }
    section("And activate the alarm system") {
        input "alarmSwitch", "capability.switch", title: "Alarm Switch"
    }
    section("Set the ecobee thermostat(s)") {
        input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
    }
    section("Heating set Point for ecobee, default =14C") {
        input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
    }
    section("Cooling set Point for ecobee, default = 27C") {
        input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
    }
 
    section("Or set the ecobee to this Climate Name (ex. Away)") {
        input "givenClimateName", "text", title: "Climate Name", required: false
    }
 
    section("Trigger these actions when home has been quiet for (default 3 minutes)") {
        input "residentsQuietThreshold", "number", title: "Time in minutes", required: false
	}
    
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
        input "phone", "phone", title: "Send a Text Message?", required: false
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
    subscribe(motions, "motion", motionEvtHandler)

}


def alarmSwitchContact(evt) {
    log.info "alarmSwitchContact, $evt.name: $evt.value"
    
    if ((alarmSwitch.currentContact == "closed") && residentsHaveBeenQuiet() && everyoneIsAway()) {
       send("AwayFromHome>alarm system just armed, take actions")
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

	def threshold = residentsQuietThreshold ?: 3   // By default, the delay is 3 minutes
    Integer delay = threshold * 60 
    
    def result = true
    def t0 = new Date(now() - (threshold * 60 *1000))
    for (sensor in motions) {
        def recentStates = sensor.statesSince("motion", t0)
        if (recentStates.find{it.value == "active"}) {
            result = false
            break
        }
    }
    log.debug "residentsHaveBeenQuiet: $result"
    return result
}


def presence(evt) {
	def threshold = residentsQuietThreshold ?: 3   // By default, the delay is 3 minutes
    Integer delay = threshold * 60 

    log.debug "evt.name: $evt.value"
    if (evt.value == "not present") {
        def person = getPerson(evt)
        send("AwayFromHome> ${person.displayName} not present at home")
        log.debug "checking if everyone is away  and quiet at home"
        if (residentsHaveBeenQuiet()){
           
            if (everyoneIsAway()) {
                send("AwayFromHome>Quiet at home...")
                runIn(delay, "takeActions")
            } else {
                log.debug "Not everyone is away, doing nothing"
                send("AwayFromHome>Not everyone is away, doing nothing..")
            }
        } else {
            
            log.debug "Things are not quiet at home, doing nothing"
            send("AwayFromHome>Things are not quiet at home...")
        }     
    } else {
        log.debug "Still present; doing nothing"
    }
}


def takeActions() {
    Integer thresholdMinutes = 2		// check that the security alarm is close in a 2-minute delay
    Integer delay = 60 * thresholdMinutes
    def minHeatTemp = givenHeatTemp ?: 14  // by default, 14C is the minimum heat temp
    def minCoolTemp = givenCoolTemp ?: 27  // by default, 27C is the minimum cool temp

//  Making sure everybody is away and no motion at home

    if (everyoneIsAway() && residentsHaveBeenQuiet()) {
        send("AwayFromHome>Nobody is at home, and it's quiet, about to take actions")
        ecobee.poll() //* Just poll the ecobee thermostat to keep it alive
    
        if ((givenClimateName != null) && (givenClimateName != "")) {
    
            // You may want to change to ecobee.setClimate('serial number list',...) if you own EMS thermostat(s)

                ecobee.iterateSetClimate('registered',givenClimateName)// Set to the climateName
    
    
        }
        else {
    
            // You may want to change to ecobee.setHold('serial number list',...) if you own EMS thermostat(s)
            // Set heating and cooling points at ecobee
            ecobee.iterateSetHold('registered',minCoolTemp, minHeatTemp, null,null,null)
        }
    
        send("AwayFromHome>ecobee's settings are now lower")

        def messageswitch = "AwayFromHome>Switched off all switches"
        send(messageswitch)
        log.info messageswitch
        switches?.off()											 // turn off the lights		

        if (alarmSwitch.currentContact == "open") {
            log.debug "alarm is not set, arm it..."  
            send("AwayFromHome>quiet,alarm system now activated")
            alarmSwitch.on()								     // arm the alarm system
        }
        runIn (delay, "checkAlarmSystem", [overwrite:false])     // check that the alarm system is armed

    }


}

private checkAlarmSystem() {
    if (alarmSwitch.currentContact == "open") {
        send("AwayFromHome>alarm still not activated,repeat..." )
        alarmSwitch.on()								     // try to arm the alarm system again
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

private getPerson(evt)
{
    people.find{evt.deviceId == it.id}
}

private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phone ) {
        log.debug( "sending text message" )
        sendSms( phone, msg )
    }

    log.debug msg
}
