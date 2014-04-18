/***
 *  Monitor and set Humdity with Ecobee Thermostat(s)
 *
 *  Monitor humidity level indoor vs. outdoor every hour  and set the humidifier/dehumidifier accordingly
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-04-12
*/




preferences {

    section("Set the ecobee thermostat's humidifer/dehumidifer") {
        input "ecobee", "capability.thermostat", title: "Ecobee?"

    }
    	  
    section("To this humidity level") {
        input "givenHumidityLevel", "number", title: "humidity level (default=40%)", required:false
    }
    
    section("Humidity differential for adjustments") {
        input "givenHumidityDiff", "number", title: "Humidity Differential (default=5%)", required:false
    }
    section("Min. Fan Time") {
        input "givenMinFanTime", "number", title: "Minimum fan time per hour in minutes (default=10)", required:false
    }
    
    section("Choose Outdoor's humidity sensor to use for better adjustment") {
        input "sensor", "capability.relativeHumidityMeasurement", title: "Outdoor Humidity Sensor"
        
    }	
    section("Min temperature for dehumidification (in Farenheits)") {
        input "givenMinTemp", "number", title: "Min Temp (default=0)", required:false
    }

    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes", "No"]], required: false
        input "phoneNumber", "phone", title: "Send a text message?", required: false
    }
    

}



def installed() {
    initialize()
}

def updated() {
    // we have had an update
    // remove everything and reinstall
    unschedule()
    initialize()
}

def initialize() {
    
    subscribe(ecobee, "heatingSetpoint", ecobeeHeatTempHandler)
    subscribe(ecobee, "coolingSetpoint", ecobeeCoolTempHandler)
    subscribe(ecobee, "humidity", ecobeeHumidityhandler)
    subscribe(ecobee, "thermostatMode", ecobeeModeHandler)
    subscribe(sensor, "humidity", sensorHumidityHandler)
    subscribe(sensor, "temperature", sensorTemperatureHandler)
    log.debug "Scheduling Humidity Monitoring & Change every 60 minutes"
    schedule("0 59 * * * ?", setHumidityLevel)    // monitor the humidity every hour

}

def ecobeeHeatTempHandler(evt) {
    log.debug "ecobee's heating temp: $evt"
}

def ecobeeCoolTempHandler(evt) {
    log.debug "ecobee's cooling temp: $evt"
}

def ecobeeHumidityHandler(evt) {
    log.debug "ecobee's humidity level: $evt"
}

def ecobeeModeHandler(evt) {
    log.debug "ecobee's mode: $evt"
}


def sensorHumidityHandler(evt) {
    log.debug "outdoor sensor's humidity level: $evt"
}

def sensorTemperatureHandler(evt) {
    log.debug "outdoor's temperature is': $evt"
}

def setHumidityLevel() {

    def min_temp_in_Farenheits =givenMinTemp ?: 0        // Min temp in Farenheits for starting dehumidifier,otherwise too cold
    def min_humidity_diff = givenHumidityDiff ?: 5       //  5% humidity differential by default
    def min_fan_time = givenFanMinTime ?: 10           //  10 min. fan time per hour by default
    
    def target_humidity = givenHumidityLevel ?: 40  // by default,  40 is the humidity level to check for
    
    log.debug "setHumidity> location.mode = $location.mode"

//  Polling of ecobee

    ecobee.poll()


    def heatTemp = ecobee.currentHeatingSetpoint
    def coolTemp = ecobee.currentCoolingSetpoint
    def ecobeeHumidity = ecobee.currentHumidity
    def outdoorHumidity = sensor.currentHumidity
    float outdoorTemp = sensor.currentTemperature
    def ecobeeMode = ecobee.currentThermostatMode
    
    log.trace("setHumidity> evaluate:, Ecobee's humidity: ${ecobeeHumidity} vs. outdoor's humidity ${outdoorHumidity},"  +
        "coolingSetpoint: ${coolTemp} , heatingSetpoint: ${heatTemp}, target humidity=${target_humidity}")

    if ((ecobeeMode == 'cool') &&
        ((ecobeeHumidity < (outdoorHumidity + min_humidity_diff)) && (ecobeeHumidity >= (outdoorHumidity - min_humidity_diff))) && 
        ((ecobeeHumidity < (target_humidity + min_humidity_diff)) && (ecobeeHumidity >= (target_humidity - min_humidity_diff)))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}" +
           "need to dehumidify the house and outdoor's humidity is ${outdoorHumidity}"
                        
//     Turn on the dehumidifer, the outdoor's humidity is lower or equivalent than inside
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, ['dehumidifierMode':'auto','dehumidifierLevel':"${target_humidity}",'humidifierMode':'off',
           'dehumidifyWithAC':'false', 'vent':'minontime','ventilatorMinOnTime':"${min_fan_time}", 'holdType':'nextTransition']) 

       send "Monitor humidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode"
    }
    else if ((ecobeeMode == 'heat') &&
             ((ecobeeHumidity < (target_humidity + min_humidity_diff)) && (ecobeeHumidity >= (target_humidity - min_humidity_diff))) && 
             ((ecobeeHumidity < (outdoorHumidity + min_humidity_diff)) && (ecobeeHumidity >= (outdoorHumidity - min_humidity_diff))) && 
             (outdoorTemp > fToC(min_temp_in_Farenheits))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity},need to dehumidify the house " +
           "outdoor's humidity is ${outdoorHumidity} & outdoor's temp is ${outdoorTemp},  not too cold"
                        
//     Turn on the dehumidifer, the outdoor's temp is not too cold 
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, ['dehumidifierMode':'auto','dehumidifierLevel':"${target_humidity}",
           'humidifierMode':'off', 'vent':'minontime','ventilatorMinOnTime':"${min_fan_time}", 'holdType':'nextTransition']) 

       send "Monitor humidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode"
    }    
    else if ((ecobeeMode == 'heat') && 
             ((ecobeeHumidity < (target_humidity + min_humidity_diff)) && (ecobeeHumidity >= (target_humidity - min_humidity_diff))) &&
             ((ecobeeHumidity < (outdoorHumidity + min_humidity_diff)) && (ecobeeHumidity >= (outdoorHumidity - min_humidity_diff))) && 
             (outdoorTemp <= fToC(min_temp_in_Farenheits))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
           "outdoor's humidity is ${outdoorHumidity}, but outdoor's temp is ${outdoorTemp}: too cold"
                        
       send "Monitor humidity>Too cold (${outdoorTemp}) to dehumidify to ${target_humidity}"

//     Turn off the dehumidifer because it's too cold
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, ['dehumidifierMode':'off','dehumidifierLevel':"${target_humidity}",'humidifierMode':'off',
           'holdType':'nextTransition']) 
    
    }
    else if ((ecobeeMode == 'cool') && (ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
             (outdoorHumidity >= (ecobeeHumidity + min_humidity_diff))){   
    
                          
       log.trace("setHumidity> Ecobee's humidity provided is higher than target humidity level=${target_humidity}, need to dehumidify with AC, because outdoor's humidity is too high=${outdoorHumidity}")

//     If mode is cooling and outdoor's humidity is too high then use the A/C to lower humidity in the house
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, ['dehumidifyWithAC':'true','dehumidifierLevel':"${target_humidity}",
          'dehumidiferMode':'off','vent':'minontime','ventilatorMinOnTime':"${min_fan_time}",'holdType':'nextTransition']) 
          
       send "Monitor humidity>dehumidifyWithAC in cooling mode"
             
    }
    else if ((ecobeeMode == 'heat') && (ecobeeHumidity  < (target_humidity - min_humidity_diff))) {    
       log.trace("setHumidity> In heat mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house")
                        
//     Need a minimum differential to humidify the house to the target
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, ['humidifierMode':'auto','humidity':"${target_humidity}",'dehumidifierMode':'off',
           'condensationAvoid':'true','vent':'minontime','ventilatorMinOnTime':"${min_fan_time}",'holdType':'nextTransition']) 

       send "Monitor humidity>humidfy to ${target_humidity} in heating mode"
    }
    else {
	   log.trace("setHumidity> Turning off all devices due to actual conditions ")
       send "Monitor humidity>all off due to actual conditions"
       ecobee.iterateSetHold('registered',coolTemp, heatTemp, ['dehumidifierMode':'off','humidifierMode':'off',
           'holdType':'nextTransition']) 
        
    }
            
    log.debug "End of Fcn"
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


// catchall
def event(evt) {
     log.debug "value: $evt.value, event: $evt, settings: $settings, handlerName: ${evt.handlerName}"
}

def cToF(temp) {
    return (temp * 1.8 + 32)
}
 
def fToC(temp) {
    return (temp - 32) / 1.8
}
	
