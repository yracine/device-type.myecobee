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
 *  Resume Ecobee's Program when people arrive or there is been recent motion at home
*/


// Automatically generated. Make future change here.
definition(
    name: "EcobeeResumeProg",
    namespace: "yracine",
    author: "Yves Racine",
    description: "EcobeeResumeProg",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {

    section("When one of these people arrive at home") {
	    input "people", "capability.presenceSensor", multiple: true
    }
    section("Or there is motion at home on these sensors") {
        input "motions", "capability.motionSensor", title: "Where?",  multiple: true, required: false
    }

    section("False alarm threshold (defaults to 3 min)") {
        input "falseAlarmThreshold", "decimal", title: "Number of minutes", required: false
    }
    section("Resume Program for the ecobee thermostat(s)") {
        input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
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

def initialize() {
    subscribe(people, "presence", presence)
    subscribe(motions, "motion", motionEvtHandler)

}

def motionEvtHandler(evt) {
    if ((evt.value == "active") && residentsHaveJustBeenActive()) {
        message = "EcobeeResumeProg>Recent motion just detected at home, do it"
        log.info message
        send(message)
        takeActions()
    }
}

private residentsHaveJustBeenActive() {
    def threshold = (falseAlarmThreshold != null && falseAlarmThreshold != "") ? (falseAlarmThreshold * 60 * 1000) as Long : 3 * 60 * 1000L
    def result = true
    def t0 = new Date(now() - threshold)
    for (sensor in motions) {
        def recentStates = sensor.statesSince("motion", t0)
        if (recentStates.find{it.value == "active"}) {
            result = false
            break
        }
    }
    log.debug "residentsHaveJustBeenActive: $result"
    return result
}


def presence(evt) {
    log.debug "evt.name: $evt.value"
	def threshold = (falseAlarmThreshold != null && falseAlarmThreshold != "") ? (falseAlarmThreshold * 60 * 1000) as Long : 3 * 60 * 1000L
    def message=null
    
    def t0 = new Date(now() - threshold)
    if (evt.value == "present") {
		
        def person = getPerson(evt)
        def recentNotPresent = person.statesSince("presence", t0).find{it.value == "not present"}
        if (!recentNotPresent) {
            message = "EcobeeResumeProg> ${person.displayName} finally arrived,do it.."
            log.info message
            send(message)
            takeActions()
        }
    }
        
}

def takeActions() {

//  For EMS thermostats, please replace registered by managementSet
    ecobee.iterateResumeProgram('registered') 

}


private getPerson(evt){
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
