/***
 *  Away from Home with Ecobee Thermostat
 *  Turn off the lights, turn on the security alarm, and set the temps at ecobee when away from home
 * 
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-03-31
*/

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
    section("Set this ecobee thermostat") {
        input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
    }
    section("Heating set Point for ecobee, default =14C") {
        input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
    }
    section("Cooling set Point for ecobee, default = 27C") {
        input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
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


def alarmSwitchContact(evt)

{
    log.info "alarmSwitchContact, $evt.name: $evt.value"
    
    if ((alarmSwitch.currentContact == "closed") && residentsHaveBeenQuiet()) {
       send("AwayFromHome>alarm system armed, no motion at home")
       log.debug "alarm is armed, nobody at home"  
       takeActions()								    
    }
}


def motionEvtHandler(evt)
{
    if (evt.value == "active") {
        state.lastIntroductionMotion = now()
        log.debug "Motion at home..."
    }
}

private residentsHaveBeenQuiet()
{
    def threshold = (residentsQuietThreshold == null ? 3: residentsQuietThreshold) * 60 * 1000
    def result = true
    def t0 = new Date(now() - threshold)
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
    log.debug "evt.name: $evt.value"
    ecobee.poll() //* Just poll the ecobee thermostat to keep it alive
    if (evt.value == "not present") {
        def person = getPerson(evt)
        send("AwayFromHome> ${person.displayName} not present at home")
        log.debug "checking if everyone is away  and quiet at home"
        if (everyoneIsAway()) {
            send("AwayFromHome>Nobody is at home now")
            if (residentsHaveBeenQuiet()){
           
                send("AwayFromHome>Quiet at home...")
                takeActions()
            }     
            else {
            
                log.debug "Things are not quiet at home, doing nothing"
                send("AwayFromHome>Things are not quiet at home...")
            }     
            
        }
        else {
            log.debug "not everyone is away, doing nothing"
            send("AwayFromHome>Not everyone is away, doing nothing..")
        }
    }
	else {
        log.debug "Still present; doing nothing"
    }
}



def takeActions() {
    send("AwayFromHome>about to take actions")
    Integer thresholdMinutes = 2		// check that the security alarm is close in a 2-minute delay
    Integer delay = 60 * thresholdMinutes
    def minHeatTemp = givenHeatTemp ?: 14  // by default, 14C is the minimum heat temp
    def minCoolTemp = givenCoolTemp ?: 27  // by default, 27C is the minimum cool temp
    
    // You may want to change to ecobee.setHold('serial number list',...) if you own EMS thermostat(s)
    
    ecobee.iterateSetHold('registered',minCoolTemp, minHeatTemp, null)// Set heating and cooling points at ecobee
    send("AwayFromHome>ecobee's temps are now lower")

    def messageswitch = "AwayFromHome>Switched off switches"
    send(messageswitch)
    log.info messageswitch
    switches?.off()											 // turn off the lights		

    if (alarmSwitch.currentContact == "open") {
        log.debug "alarm is not set, arm it..."  
        alarmSwitch.on()								     // arm the alarm system
    }
    send("AwayFromHome>quiet,Alarm system activated")
    runIn (delay, "checkAlarmSystem")
    

}

private checkAlarmSystem() {
    if (alarmSwitch.currentContact == "open") {
        send("AwayFromHome>alarm not activated,repeat..." )
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
