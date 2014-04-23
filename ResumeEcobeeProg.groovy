/***
 *  Resume Ecobee's Program when people arrive at home 
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
        section("False alarm threshold (defaults to 3 min)") {
            input "falseAlarmThreshold", "decimal", title: "Number of minutes", required: false
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
	def threshold = (falseAlarmThreshold != null && falseAlarmThreshold != "") ? (falseAlarmThreshold * 60 * 1000) as Long : 3 * 60 * 1000L
    def message=null
    
    def t0 = new Date(now() - threshold)
    if (evt.value == "present") {
		
        def person = getPerson(evt)
        def recentNotPresent = person.statesSince("presence", t0).find{it.value == "not present"}
        if (recentNotPresent) {
            log.debug "skipping notification of arrival of ${person.displayName} because last departure was only ${now() - recentNotPresent.date.time} msec ago"
            message = "EcobeeResumeProg> ${person.displayName}: too recent arrival..."
            log.info message
            send(message)
        }
        else {
            message = "EcobeeResumeProg> ${person.displayName} finally arrived,do it.."
            log.info message
            send(message)

//          You may want to change to ecobee.resumeProgram('serial number list') if you own EMS thermostat(s)                
            ecobee.iterateResumeProgram('registered')
        }
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
