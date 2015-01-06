/**
 *  MonitorDoorOrWIndowOpen
 *
 *  Copyright 2014 Yves Racine
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
    name: "MonitorDoorOrWIndowOpen",
    namespace: "yracine",
    author: "Yves Racine",
    description: "Monitor Door or Window Open to track down waste of energy in the house",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    page(name: "selectAlarm")
    page(name: "selectSensors")
    page(name: "selectTstats")
}

def selectAlarm() {
    dynamicPage(name: "selectAlarm", title: "Configure Alarm", nextPage:"selectSensors", uninstall: true) {
        section("When there is activity on this alarm...") {
            input "theAlarm", "capability.alarm", multiple: false, required: true
        }
    }
}

def selectSensors() {
    dynamicPage(name: "selectSensors", title: "Configure Sensors", uninstall: true, nextPage:"selectTstats") {
        def sensors = theAlarm.supportedAttributes*.name
        if (sensors) {
            section("On these sensors...") {
                input "theSensors", "enum", required: true, multiple:true, metadata:[values:sensors], refreshAfterSelection:true
            }
        }
        section([mobileOnly:true]) {
            label title: "Assign a name", required: false
        }
    }
}

def selectStates() {
    dynamicPage(name: "selectStates", title: "Which states should trigger a notification?", uninstall: true, nextPage:selectTstats ) {
        theSensors.each() {
            def sensor = it
            def states = []
            // TODO: Cannot figure out how to get these possible states, so have to guess them based on the current value
            switch(theAlarm.currentValue("$it")) {
            case "active":
            case "inactive":
                states = ["active", "inactive"]
                break
            case "on":
            case "off":
                states = ["on", "off"]
                break
            case "detected":
            case "clear":
            case "tested":
                states = ["detected", "clear", "tested"]
                break
            case "closed":
            case "open":
                states =  ["closed", "open"]
                break
            default:
                log.debug "value not handled: ${theAlarm.currentValue("$sensor")}"
            }           
            if (states) {
                section() {
                    input "${sensor}States", "enum", title:"For $sensor...", required: true, multiple:true, metadata:[values:states], refreshAfterSelection:true
                }
            }
        }
    }
}
def selectTstats() {
    dynamicPage(name: "selectTstats", title: "Select Thermostat(s)", uninstall: true, install: true) {
        section("Turn off the thermostat(s)") {
            input "tstats", "capability.thermostat", multiple: true, required: false
        }
        section("When Sensor(s) left open for more than...") {
        input "maxOpenTime", "number", title: "Minutes?"
    }

    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}
def updated() {
    log.debug "Updated with settings: ${settings}"

    unsubscribe()
    initialize()
}

def initialize() {
    theSensors.each() {
        def sensor = it
        subscribe(theAlarm, "${sensor}.$it", sensorTriggered)
    }
}
def sensorTriggered(evt) {
    if (evt.value.toUpperCase().contains("CLOSED")) {
        clearStatus()
    }
    else if (evt.value.toUpperCase().contains("OPEN") && state.status != "scheduled") {
        runIn(maxOpenTime * 60, takeAction, [overwrite: false])
        state.status = "scheduled"
    }
}

def takeAction(){
    if (state.status == "scheduled")
    {
        log.debug "${evt.name} has been open for too long, sending message"
        def msg = "MonitorDoorOrWindowOpen>${evt.name} has been open for more than $maxOpenTime minutes!"
        sendPush msg
        if (tstats) {
            msg = "MonitorDoorOrWindowOpen>Turning off thermostat(s): ${tsats}"
            sendPush msg
            tstats?.off()
        }
        clearStatus()
    } else {
        log.trace "Status is no longer scheduled. Not sending text."
    }
}

def clearStatus() {
    state.status = null
}
