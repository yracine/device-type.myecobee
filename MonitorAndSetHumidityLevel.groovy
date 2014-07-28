/***
 *  Monitor and set Humdity with Ecobee Thermostat(s)
 *
 *  Monitor humidity level indoor vs. outdoor at a regular interval (in minutes) and 
 *  set the humidifier/dehumidifier to a target humidity level
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-04-12
*/



// Automatically generated. Make future change here.
definition(
    name: "Monitor And Set Ecobee's humidity",
    namespace: "",
    author: "Yves Racine",
    description: "Monitor And set Ecobee's humidity",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {

    section("Monitor & set the ecobee thermostat's humidifer/dehumidifer devices") {
        input "ecobee", "capability.thermostat", title: "Ecobee?"

    }	  
    section("To this humidity level") {
        input "givenHumidityLevel", "number", title: "humidity level (default=40%)", required:false
    }
    section("At which interval in minutes (default =59 min.)?"){
        input "givenInterval", "number", required: false
    }
    section("Humidity differential for adjustments") {
        input "givenHumidityDiff", "number", title: "Humidity Differential (default=5%)", required:false
    }
    section("Min. Fan Time") {
        input "givenFanMinTime", "number", title: "Minimum fan time per hour in minutes (default=20)", required:false
    }
    
    section("Choose Indoor's humidity sensor to use for better adjustment (optional, default=ecobee sensor)") {
        input "indoorSensor", "capability.relativeHumidityMeasurement", title: "Indoor Humidity Sensor", required:false
        
    }	
    section("Choose Outdoor's humidity sensor to use for better adjustment") {
        input "outdoorSensor", "capability.relativeHumidityMeasurement", title: "Outdoor Humidity Sensor"
        
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
    unsubscribe()
    initialize()
}

def initialize() {
    
    subscribe(ecobee, "heatingSetpoint", ecobeeHeatTempHandler)
    subscribe(ecobee, "coolingSetpoint", ecobeeCoolTempHandler)
    subscribe(ecobee, "humidity", ecobeeHumidityhandler)
    subscribe(ecobee, "thermostatMode", ecobeeModeHandler)
    subscribe(outdoorSensor, "humidity", outdoorSensorHumHandler)
    if ((indoorSensor != null) && (indoorSensor != "")) {
        subscribe(indoorSensor, "humidity", indoorSensorHumHandler)
    }    
    subscribe(sensor, "temperature", sensorTemperatureHandler)
    Integer delay =givenInterval ?: 59   // By default, do it every hour
    log.debug "Scheduling Humidity Monitoring & Change every ${delay}  minutes"
    
    schedule("0 0/${delay} * * * ?", setHumidityLevel)    // monitor the humidity according to delay specified

}

def ecobeeHeatTempHandler(evt) {
    log.debug "ecobee's heating temp: $evt.value"
}

def ecobeeCoolTempHandler(evt) {
    log.debug "ecobee's cooling temp: $evt.value"
}

def ecobeeHumidityHandler(evt) {
    log.debug "ecobee's humidity level: $evt.value"
}

def ecobeeModeHandler(evt) {
    log.debug "ecobee's mode: $evt.value"
}


def outdoorSensorHumHandler(evt) {
    log.debug "outdoor Sensor's humidity level: $evt.value"
}

def indoorSensorHumHandler(evt) {
    log.debug "indoor Sensor's humidity level: $evt.value"
}

def sensorTemperatureHandler(evt) {
    log.debug "outdoor temperature is: $evt.value"
}

def setHumidityLevel() {

    def min_temp_in_Farenheits =givenMinTemp ?: 0                          // Min temp in Farenheits for starting dehumidifier,otherwise too cold
    def min_humidity_diff = givenHumidityDiff ?:5                          //  5% humidity differential by default
    Integer min_fan_time =  givenFanMinTime?:20                            //  20 min. fan time per hour by default
    def target_humidity = givenHumidityLevel ?: 40                         // by default,  40 is the humidity level to check for

    
    log.debug "setHumidity> location.mode = $location.mode"

//  Polling of ecobee to get current metrics

    ecobee.poll()

    def heatTemp = ecobee.currentHeatingSetpoint
    def coolTemp = ecobee.currentCoolingSetpoint
    def ecobeeHumidity = ecobee.currentHumidity
    def indoorHumidity=0 
    
    if ((indoorSensor != null) && (indoorSensor != "")) {
        indoorHumidity = indoorSensor.currentHumidity
    }
    def outdoorHumidity = outdoorSensor.currentHumidity
    float outdoorTemp = outdoorSensor.currentTemperature
    def ecobeeMode = ecobee.currentThermostatMode
    
    
//  If indoorSensor specified, use the indoorHumidity provided instead of ecobeeHumidity

    log.trace("setHumidity>compare: Ecobee's humidity: ${ecobeeHumidity} vs. indoor's humidity ${indoorHumidity}")
    if (((indoorSensor != null) && (indoorSensor != "")) && (indoorHumidity < ecobeeHumidity)) {
        ecobeeHumidity = indoorHumidity
    }
    
    log.trace("setHumidity> evaluate:, Ecobee's humidity: ${ecobeeHumidity} vs. outdoor's humidity ${outdoorHumidity},"  +
        "coolingSetpoint: ${coolTemp} , heatingSetpoint: ${heatTemp}, target humidity=${target_humidity}, fanMinOnTime=${min_fan_time}")

    if ((ecobeeMode == 'cool') && (ecobeeHumidity >= (outdoorHumidity - min_humidity_diff)) && 
         (ecobeeHumidity >= (target_humidity + min_humidity_diff))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, " +
           "need to dehumidify the house and outdoor's humidity is ${outdoorHumidity}"
                        
//     Turn on the dehumidifer, the outdoor's humidity is lower or equivalent than inside
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on', ['dehumidifierMode':'on','dehumidifierLevel':"${target_humidity}",'humidifierMode':'off',
           'dehumidifyWithAC':'false','fanMinOnTime':"${min_fan_time}"]) 

       send "MonitorHumidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode"
    }
    else if ( ((ecobeeMode == 'heat')  ||  (ecobeeMode == 'off')) && (ecobeeHumidity >= (target_humidity + min_humidity_diff)) && 
             (ecobeeHumidity >= outdoorHumidity - min_humidity_diff) && 
             (outdoorTemp > fToC(min_temp_in_Farenheits))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
           "outdoor's humidity is ${outdoorHumidity} & outdoor's temp is ${outdoorTemp},  not too cold"
                        
//     Turn on the dehumidifer, the outdoor's temp is not too cold 
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['dehumidifierMode':'on','dehumidifierLevel':"${target_humidity}",
           'humidifierMode':'off','fanMinOnTime':"${min_fan_time}"]) 

       send "MonitorHumidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode"
    }    
    else if (((ecobeeMode == 'heat')  ||  (ecobeeMode == 'off')) && (ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
             (ecobeeHumidity >= outdoorHumidity - min_humidity_diff) && 
             (outdoorTemp <= fToC(min_temp_in_Farenheits))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
           "outdoor's humidity is ${outdoorHumidity}, but outdoor's temp is ${outdoorTemp}: too cold"
                        
       send "MonitorHumidity>Too cold (${outdoorTemp}) to dehumidify to ${target_humidity}"

//     Turn off the dehumidifer because it's too cold
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, null,['dehumidifierMode':'off','dehumidifierLevel':"${target_humidity}",
           'humidifierMode':'off']) 
    
    }
    else if ((ecobeeMode == 'cool') && (ecobeeHumidity > (target_humidity + min_humidity_diff)) &&
             (outdoorHumidity > (target_humidity + min_humidity_diff))) {   
    
                          
       log.trace("setHumidity> Ecobee's humidity provided is way higher than target humidity level=${target_humidity}, need to dehumidify with AC, because outdoor's humidity is too high=${outdoorHumidity}")

//     If mode is cooling and outdoor's humidity is too high then use the A/C to lower humidity in the house
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['dehumidifyWithAC':'true','dehumidifierLevel':"${target_humidity}",
           'dehumidiferMode':'off','fanMinOnTime':"${min_fan_time}"]) 
          
       send "MonitorHumidity>dehumidifyWithAC in cooling mode, indoor humidity is ${ecobeeHumidity}% and outdoor's humidity (${outdoorHumidity}%) is too high to dehumidify"
             
    }
    else if (((ecobeeMode == 'heat')  ||  (ecobeeMode == 'off')) && (ecobeeHumidity  < (target_humidity - min_humidity_diff))) {    
       log.trace("setHumidity> In ${ecobeeMode} mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house")
                        
//     Need a minimum differential to humidify the house to the target
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['humidifierMode':'auto','humidity':"${target_humidity}",'dehumidifierMode':'off',
           'condensationAvoid':'true','fanMinOnTime':"${min_fan_time}"]) 

       send "MonitorHumidity>humidify to ${target_humidity} in ${ecobeeMode} mode"
    }
    else if ((ecobeeMode == 'cool') && (outdoorTemp < indoorTemp) &&
             (outdoorHumidity <= (ecobeeHumidity + min_humidity_diff))){   
    
                          
       log.trace("setHumidity> Outdoor temp is lower in cooling mode, use the ERV to get fresh air")

//     If mode is cooling and outdoor's temp is lower than inside then use ERV to get fresh air into the house
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['dehumidifierLevel':"${target_humidity}",'dehumidiferMode':'on',
           'fanMinOnTime':"${min_fan_time}"]) 
          
       send "MonitorHumidity>Outdoor temp is lower, getting fresh air with ERV"
             
    }
    else if ((outdoorHumidity > ecobeeHumidity) && (ecobeeHumidity > target_humidity)) {
       log.trace("setHumidity>all off, indoor humidity is ${ecobeeHumidity}% and outdoor's humidity (${outdoorHumidity}%) is too high to dehumidify ")
       send "MonitorHumidity>all off, indoor humidity is ${ecobeeHumidity}% and outdoor's humidity (${outdoorHumidity}%) is too high to dehumidify"
       ecobee.iterateSetHold('registered',coolTemp, heatTemp, null,['dehumidifierMode':'off','humidifierMode':'off']) 
    }
    else {
       log.trace("setHumidity>all off, humidity level within range")
       send "MonitorHumidity>all off, humidity level within range"
       ecobee.iterateSetHold('registered',coolTemp, heatTemp, null,['dehumidifierMode':'off','humidifierMode':'off','dehumidifyWithAC':'false']) 
        
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
	
