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
 *  Monitor and set Humidity with Ecobee Thermostat(s):
 *      Monitor humidity level indoor vs. outdoor at a regular interval (in minutes) and 
 *      set the humidifier/dehumidifier  to a target humidity level. 
 *      Use also HRV/ERV/dehumidifier to get fresh air (free cooling) when appropriate based on outdoor temperature.
 *
*/



// Automatically generated. Make future change here.
definition(
    name: "Monitor And Set Ecobee's humidity",
    namespace: "yracine",
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
    section("Min. ERV/HRV Time") {
        input "givenVentMinTime", "number", title: "Minimum ERV/HRV time per hour in minutes (default=20)", required:false
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
    section("Use free cooling using HRV/ERV/Dehumidifier (By default=false)") {
        input "freeCooling", "Boolean", title: "Free Cooling?",metadata:[values:["true", "false"]], required:false
    }

    section("Check TED energy consumption at") {
        input "ted", "capability.powerMeter", title: "TED5000?"
    }
    section("Do not run above this power consumption level (default=3000W") {
        input "givenPowerLevel", "number", title: "power?", required:false
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
    
    subscribe(ted, "power", tedPowerHandler)
    subscribe(ecobee, "heatingSetpoint", ecobeeHeatTempHandler)
    subscribe(ecobee, "coolingSetpoint", ecobeeCoolTempHandler)
    subscribe(ecobee, "humidity", ecobeeHumidityHandler)
    subscribe(ecobee, "temperature", ecobeeTempHandler)
    subscribe(ecobee, "thermostatMode", ecobeeModeHandler)
    subscribe(outdoorSensor, "humidity", outdoorSensorHumHandler)
    if ((indoorSensor != null) && (indoorSensor != "")) {
        subscribe(indoorSensor, "humidity", indoorSensorHumHandler)
        subscribe(indoorSensor, "temperature", indoorTempHandler)
        
    }    
    subscribe(outdoorSensor, "temperature", outdoorTempHandler)
    Integer delay =givenInterval ?: 59   // By default, do it every hour
    log.debug "Scheduling Humidity Monitoring & Change every ${delay}  minutes"
    
    schedule("0 0/${delay} * * * ?", setHumidityLevel)    // monitor the humidity according to delay specified

}
def tedPowerHandler(evt) {
    log.debug "ted power: $evt.value"
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

def ecobeeTempHandler(evt) {
    log.debug "ecobee's temperature level: $evt.value"
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

def indoorTempHandler(evt) {
    log.debug "Indoor Temperature is: $evt.value"
}

def outdoorTempHandler(evt) {
    log.debug "Outdoor temperature is: $evt.value"
}

def setHumidityLevel() {

    def min_temp_in_Farenheits =givenMinTemp ?: 0                          // Min temp in Farenheits for starting dehumidifier,otherwise too cold
    def min_humidity_diff = givenHumidityDiff ?:5                          //  5% humidity differential by default
    Integer min_fan_time =  givenFanMinTime?:20                            //  20 min. fan time per hour by default
    Integer min_vent_time =  givenVentMinTime?:20                          //  20 min. ventilator time per hour by default
    def target_humidity = givenHumidityLevel ?: 40                         // by default,  40 is the humidity level to check for
    def freeCoolingFlag = (freeCooling != null) ? freeCooling: 'false'     // Free cooling using the Hrv/Erv/dehumidifier

    Integer max_power = givenPowerLevel ?:3000                             // Do not run above 3000w consumption level by default
    
    
    log.debug "setHumidity> location.mode = $location.mode"

//  Polling of all devices

    ecobee.poll()
    ted.poll()    

    Integer powerConsumed = ted.currentPower.toInteger()
    
    def heatTemp = ecobee.currentHeatingSetpoint
    def coolTemp = ecobee.currentCoolingSetpoint
    def ecobeeHumidity = ecobee.currentHumidity
    def indoorHumidity=0 
    def indoorTemp = ecobee.currentTemperature
    def hasDehumidifier = (ecobee.currentHasDehumidifier!=null) ? ecobee.currentHasDehumidifier : 'false' 
    def hasHumidifier = (ecobee.currentHasHumidifier!=null) ? ecobee.currentHasHumidifier : 'false' 
    def hasHrv = (ecobee.currentHasHrv !=null)? ecobee.currentHasHrv : 'false' 
    def hasErv = (ecobee.currentHasErv !=null)? ecobee.currentHasErv : 'false' 
    
    // use the readings from another sensor if better precision neeeded
    if ((indoorSensor != null) && (indoorSensor != "")) {
        indoorHumidity = indoorSensor.currentHumidity
        indoorTemp = indoorSensor.currentTemperature
    }
    
    def outdoorHumidity = outdoorSensor.currentHumidity
    def outdoorTemp = outdoorSensor.currentTemperature
    def ecobeeMode = ecobee.currentThermostatMode
    
    if (powerConsumed > max_power){

//  peak of energy consumption, turn off all devices

       send "MonitorHumidity>all off,power is too high=${ted.currentPower}"
       ecobee.iterateSetHold('registered',coolTemp, heatTemp,'auto', ['vent':'off','dehumidifierMode':'off','humidifierMode':'off','dehumidifyWithAC':'false']) 
       return
           

    }
    
//  If indoorSensor specified, use the more precise humidity measure instead of ecobeeHumidity

    log.trace("setHumidity>compare: Ecobee's humidity: ${ecobeeHumidity} vs. indoor's humidity ${indoorHumidity}")
    if (((indoorSensor != null) && (indoorSensor != "")) && (indoorHumidity < ecobeeHumidity)) {
        ecobeeHumidity = indoorHumidity
    }
    
    log.trace("setHumidity> evaluate:, Ecobee's humidity: ${ecobeeHumidity} vs. outdoor's humidity ${outdoorHumidity},"  +
        "coolingSetpoint: ${coolTemp} , heatingSetpoint: ${heatTemp}, target humidity=${target_humidity}, fanMinOnTime=${min_fan_time}")
    log.trace("setHumidity> evaluate: Indoor Temp: ${indoorTemp} vs. outdoor Temp ${outdoorTemp}" )
    log.trace("setHumidity> hasErv=${hasErv}, hasHrv=${hasHrv},hasHumidifier=${hasHumidifier},hasDehumidifier=${hasDehumidifier}, freeCoolingFlag=${freeCoolingFlag}") 

    if ((ecobeeMode == 'cool' && (hasHrv=='true' || hasErv=='true')) && 
        (ecobeeHumidity >= (outdoorHumidity - min_humidity_diff)) && 
        (ecobeeHumidity >= (target_humidity + min_humidity_diff))) {
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, " +
           "need to dehumidify the house and outdoor's humidity is lower (${outdoorHumidity})"
                        
//     Turn on the dehumidifer and HRV/ERV, the outdoor's humidity is lower or equivalent than inside
//     You may want to change ecobee.iterateSetHold to ecobee.setHold('list of serial # separated by commas',...) if you own EMS thermostat(s)

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on', ['dehumidifierMode':'on','dehumidifierLevel':"${target_humidity}",'humidifierMode':'off',
           'dehumidifyWithAC':'false','fanMinOnTime':"${min_fan_time}"],['vent':'minontime','ventilatorMinOnTime': "${min_vent_time}"]) 

       send "MonitorHumidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode, using ERV/HRV and dehumidifier if available"
    }
    else if (((ecobeeMode == 'heat') || (ecobeeMode == 'off') && (hasHrv=='true' || hasErv=='true')) && 
             (ecobeeHumidity >= (target_humidity + min_humidity_diff)) && 
             (ecobeeHumidity >= outdoorHumidity - min_humidity_diff) && 
             (outdoorTemp > fToC(min_temp_in_Farenheits))) {
             
       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
           "outdoor's humidity is lower (${outdoorHumidity}) & outdoor's temp is ${outdoorTemp},  not too cold"
                        
//     Turn on the dehumidifer and HRV/ERV, the outdoor's temp is not too cold 

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['dehumidifierMode':'on','dehumidifierLevel':"${target_humidity}",
            'humidifierMode':'off','fanMinOnTime':"${min_fan_time}"],['vent':'minontime','ventilatorMinOnTime': "${min_vent_time}"]) 

       send "MonitorHumidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode"
    }    
    else if (((ecobeeMode == 'heat') ||(ecobeeMode == 'off') && (hasHrv=='true' || hasErv=='true')) && 
             (ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
             (ecobeeHumidity >= outdoorHumidity - min_humidity_diff) && 
             (outdoorTemp <= fToC(min_temp_in_Farenheits))) {

       log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
           "outdoor's humidity is lower (${outdoorHumidity}), but outdoor's temp is ${outdoorTemp}; too cold"
                        
       send "MonitorHumidity>Too cold (${outdoorTemp}) to dehumidify to ${target_humidity}"

//     Turn off the dehumidifer and HRV/ERV because it's too cold till the next cycle.

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, null,['dehumidifierMode':'off','dehumidifierLevel':"${target_humidity}",
           'humidifierMode':'off'],['vent':'off'])
    
    }
    else if ((((ecobeeMode == 'heat' ||  ecobeeMode == 'off') && hasHumidifier=='true')) && 
             (ecobeeHumidity  < (target_humidity - min_humidity_diff))) {    

       log.trace("setHumidity> In ${ecobeeMode} mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house")
                        
//     Need a minimum differential to humidify the house to the target if any humidifier available

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['humidifierMode':'auto','humidity':"${target_humidity}",'dehumidifierMode':'off',
           'condensationAvoid':'true','fanMinOnTime':"${min_fan_time}"],null)

       send "MonitorHumidity>humidify to ${target_humidity} in ${ecobeeMode} mode"
    }
    else if (((ecobeeMode == 'cool') && (hasDehumidifier =='false') && (hasHrv=='true' || hasErv=='true')) && 
             (ecobeeHumidity > (target_humidity + min_humidity_diff)) &&
             (outdoorHumidity > target_humidity)) {   
    
                          
       log.trace("setHumidity> Ecobee's humidity provided is way higher than target humidity level=${target_humidity}, need to dehumidify with AC, because outdoor's humidity is too high=${outdoorHumidity}")

//     If mode is cooling and outdoor's humidity is too high then use the A/C to lower humidity in the house if there is no dehumidifier

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['dehumidifyWithAC':'true','dehumidifierLevel':"${target_humidity}",
           'dehumidiferMode':'off','fanMinOnTime':"${min_fan_time}"], ['vent':'off'])
          
       send "MonitorHumidity>dehumidifyWithAC in cooling mode, indoor humidity is ${ecobeeHumidity}% and outdoor's humidity (${outdoorHumidity}%) is too high to dehumidify"
             
    }
    else if (((ecobeeMode == 'cool') && (hasDehumidifier=='true')) && 
             (ecobeeHumidity >= (target_humidity + min_humidity_diff))) {
    
    
//     If mode is cooling and outdoor's humidity is too high, then just use dehumidifier if any availabl

       log.trace "MonitorHumidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode using the dehumidifier only"

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on', ['dehumidifierMode':'on','dehumidifierLevel':"${target_humidity}",'humidifierMode':'off',
           'dehumidifyWithAC':'false','fanMinOnTime':"${min_fan_time}"], ['vent':'off'])

       send "MonitorHumidity>dehumidify to ${target_humidity} in ${ecobeeMode} mode using the dehumidifier only"
          
    
    }
    else if (((ecobeeMode == 'cool') && (hasDehumidifier=='true' && hasErv=='false' && hasHrv=='false')) && 
              (outdoorTemp < indoorTemp) && (freeCoolingFlag=='true') ) {
    
//     If mode is cooling and outdoor's temp is lower than inside, then just use dehumidifier/hrv if any available

       log.trace "MonitorHumidity>In cooling mode, outdoor temp is lower than inside, using dehumidifier for free cooling"

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on', ['dehumidifierMode':'on','dehumidifierLevel':"${target_humidity}",'humidifierMode':'off',
           'dehumidifyWithAC':'false','fanMinOnTime':"${min_fan_time}"],null) 

       send "MonitorHumidity>Outdoor temp is lower than inside, using dehumidifier for more efficient cooling"
          
    
    }
    else if ((ecobeeMode == 'cool' && (hasErv=='true' || hasHrv=='true')) && (outdoorTemp < indoorTemp) && 
             (freeCoolingFlag=='true')) {
    
       log.trace("setHumidity>In cooling mode, outdoor temp is lower than inside, using the HRV/ERV to get fresh air")

//     If mode is cooling and outdoor's temp is lower than inside, then use ERV/HRV to get fresh air into the house

       ecobee.iterateSetHold('registered',coolTemp, heatTemp, 'on',['fanMinOnTime':"${min_fan_time}"],
           ['vent':'minontime','ventilatorMinOnTime': "${min_vent_time}"])
          
       send "MonitorHumidity>Outdoor temp is lower than inside, using the HRV/ERV for more efficient cooling"
             
    }
    else if ((hasDehumidifier=='false') && (outdoorHumidity > ecobeeHumidity) && (ecobeeHumidity > target_humidity)) {
    
//     If indoor humidity is greater than target, but outdoor humidity is way higher than indoor humidity, 
//     just wait for the next cycle & do nothing for now.

       log.trace("setHumidity>indoor humidity is ${ecobeeHumidity}%, but outdoor's humidity (${outdoorHumidity}%) is too high to dehumidify")
       ecobee.iterateSetHold('registered',coolTemp, heatTemp, null,['dehumidifierMode':'off',
           'humidifierMode':'off'],['vent':'off'])
       send "MonitorHumidity>indoor humidity is ${ecobeeHumidity}%, but outdoor humidity ${outdoorHumidity}% is too high to dehumidify"

     } else {

       log.trace("setHumidity>all off, humidity level (${ecobeeHumidity}%) within range")
       send "MonitorHumidity>all off, humidity level (${ecobeeHumidity}%) within range"
       ecobee.iterateSetHold('registered',coolTemp, heatTemp, null,['dehumidifierMode':'off',
           'humidifierMode':'off','dehumidifyWithAC':'false'], ['vent':'off']) 
        
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
