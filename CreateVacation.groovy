/***
 *  CreateVacation with Ecobee Thermostat
 *  create a vacation on the Ecobee thermostat
 * 
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-03-31
*/


preferences {
    
    section("Initialize this ecobee thermostat") {
        input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
    }
    section("Vacation Name") { 
        input "vacationName", "text", title: "Vacation Name"
    }        
    section("Cool Temp for vacation, default = 27C") {
        input "givenCoolTemp", "number", title: "Cool Temp", required: false
    }        
    section("Heat Temp for vacation, default=14C") {
        input "givenHeatTemp", "number", title: "Heat Temp", required: false
    }        
    section("Start date for the vacation, format = DD-MM-YYYY, default=today") {
        input "givenStartDate", "text", title: "Beginning Date", required: false
    }        
    section("Start time for the vacation HH:MM (24HR)") {
        input "givenStartTime", "text", title: "Beginning time"
    }        
	section("End date for the vacation format = DD-MM-YYYY") {
        input "givenEndDate", "text", title: "End Date"
    }        
    section("End time for the vacation HH:MM (24HR)" ) {
        input "givenEndTime", "text", title: "End time"
    }        
    

}



def installed() {
    
    initialize()
     
}




def updated() {
  
    
    initialize()

}


def initialize() {
    def minHeatTemp = givenHeatTemp ?: 14  // by default, 14C is the minimum heat temp
    def minCoolTemp = givenCoolTemp ?: 27  // by default, 27C is the minimum cool temp
    def vacationStartDateTime=null
    String dateTime=null
    
    if ((givenStartDate !=null) && (givenStartDate != "")) {
        dateTime = givenStartDate + givenStartTime
        vacationStartDateTime = new Date().parse('d-M-yyyy H:m', dateTime)
    } 
    else {
        vacationStartDateTime = timeToday(givenStartTime, location.timeZone).getTime()
    }
    dateTime = givenEndDate + givenEndTime
    def vacationEndDateTime = new Date().parse('d-M-yyyy H:m', dateTime)

// If you have EMS thermostat(s) in a utility or other management sets, change from 'registered' to 'managementSet'

    ecobee.iterateCreateVacation('registered', vacationName, minCoolTemp, minHeatTemp, vacationStartDateTime, 
        vacationEndDateTime)
    send("CreateVacation> vacationName ${vacationName} created")
}


private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phoneNumber ) {
        log.debug( "sending text message" )
        sendSms( phoneNumber, msg )
    }

    log.debug msg
}
