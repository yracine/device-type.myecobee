/***
 *  ResumeProgram when people arrived at home with Ecobee Thermostat
 *  
 * 
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-04-13
*/

preferences {

        section("When one of these people arrive at home") {
	    input "people", "capability.presenceSensor", multiple: true
        }
        section("Resume Program at this ecobee thermostat") {
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
    subscribe(people, "presence", presence)
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
    unsubscribe()
    subscribe(people, "presence", presence)
}

def presence(evt)
{
    log.debug "evt.name: $evt.value"
    def threshold =  2 * 60 * 1000L // 2 min threshold

    def t0 = new Date(now() - threshold)
    if (evt.value == "present") {

        def person = getPerson(evt)
        def recentNotPresent = person.statesSince("presence", t0).find{it.value == "not present"}
        if (recentNotPresent) {
            log.debug "ResumeProg>skipping notification of arrival of ${person.displayName} because last departure was only ${now() - recentNotPresent.date.time} msec ago"
        }        
	}
	else {
            def message = "ResumeProg>${person.displayName} arrived, do it..."
            log.info message
            send(message)

//     you'd need to change 'registered' to 'managementSet' if you own EMS thermostat(s) in a utility or other management sets
                
            ecobee.iterateResumeProgram('registered')
	}
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
