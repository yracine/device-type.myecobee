/***
 *  Copyright Yves Racine
 *  LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 *  in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 *  Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 *  to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *
 *  Software Distribution is restricted and shall be done only with Developer's written approval.
 *
 *
 *  Monitor and set Humidity with Ecobee Thermostat(s):
 *      Monitor humidity level indoor vs. outdoor at a regular interval (in minutes) and 
 *      set the humidifier/dehumidifier  to a target humidity level. 
 *      Use also HRV/ERV/dehumidifier to get fresh air (free cooling) when appropriate based on outdoor temperature.
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
// Automatically generated. Make future change here.

definition(
	name: "${get_APP_NAME()}",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Monitor And set Ecobee's humidity via your connected humidifier/dehumidifier/HRV/ERV and your connected Humidifier/Dehumidifier/Fan switch(es)",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

def get_APP_VERSION() {return "3.6.4"}

preferences {
	page(name: "dashboardPage", title: "DashboardPage")
	page(name: "humidifySettings", title: "HumidifySettings")
	page(name: "dehumidifySettings", title: "DehumidifySettings")
	page(name: "ventilatorSettings", title: "ventilatorSettings")
	page(name: "sensorSettings", title: "SensorSettings")
	page(name: "otherSettings", title: "OtherSettings")

}

def dashboardPage() {
	dynamicPage(name: "dashboardPage", title: "MonitorAndSetEcobeeHumidity-Dashboard", uninstall: true, nextPage:sensorSettings, submitOnChange: true) {
		section("Monitor & set the ecobee thermostat's dehumidifer/humidifier/HRV/ERV settings") {
			input "ecobee", "capability.thermostat", title: "MyEcobee?"
		}
		section("To this humidity level") {
			input "givenHumidityLevel", "number", title: "Humidity level (default=calculated based on outside temp)", required: false
		}
		section("At which interval in minutes (range=[10..59],default =59 min.)?") {
			input "givenInterval", "number", title: "Interval",  required: false
		}
		section("Humidity differential for adjustments") {
			input "givenHumidityDiff", "number", title: "Humidity Offset Allowed [default=5%]", required: false
		}
		section("Press Next in the upper section for Initial setup") {
			if (ecobee) {            
				ecobee.refresh()            
				def scale= getTemperatureScale()
				String currentProgName = ecobee?.currentClimateName
				String currentSetClimateName = ecobee?.currentSetClimate
				String currentProgType = ecobee?.currentProgramType
				def scheduleProgramName = ecobee?.currentProgramScheduleName
				def outdoorHumidity,outdoorTemp,corrOutdoorHum, indoorHumidity, indoorTemp                 
                
				String mode =ecobee?.currentThermostatMode.toString()
				def operatingState=ecobee?.currentThermostatOperatingState                
				indoorHumidity = ecobee.currentHumidity
				indoorTemp = ecobee.currentTemperature
				def hasDehumidifier = (ecobee.currentHasDehumidifier) ? ecobee.currentHasDehumidifier : 'false'
				def hasHumidifier = (ecobee.currentHasHumidifier) ? ecobee.currentHasHumidifier : 'false'
				def hasHrv = (ecobee.currentHasHrv) ? ecobee.currentHasHrv : 'false'
				def hasErv = (ecobee.currentHasErv) ? ecobee.currentHasErv : 'false'
				String dehumidifyWithACString=(settings.dehumidifyWithACFlag)? 'true': 'false'                
				String useFanWhenHumidityIsHighString=(settings.useFanWhenHumidityIsHigh)? 'true': 'false'                
				String useFanWithHumidifierSwitchesString=(settings.useFanWithHumidifierSwitches)? 'true': 'false'                
				def heatingSetpoint,coolingSetpoint
                def threshVOC, threshCarbonDioxide, threshRadon, indoorVOC, indoorRadon,indoorCarbonDioxide                

                if (indoorSensor) {
					indoorHumidity = indoorSensor.currentHumidity
					indoorTemp = indoorSensor.currentTemperature
				}
				if (outdoorSensor) {
					outdoorHumidity=outdoorSensor.currentHumidity                
					outdoorTemp=outdoorSensor.currentTemperature                
					corrOutdoorHum = (scale == 'C') ?
						calculate_corr_humidity(outdoorTemp, outdoorHumidity, indoorTemp).round() :
						calculate_corr_humidity(fToC(outdoorTemp), outdoorHumidity, fToC(indoorTemp)).round()
				}
                def idealIndoorHum                
                if (outdoorTemp) {                
                    idealIndoorHum= (scale == 'C')? find_ideal_indoor_humidity(outdoorTemp):
                        find_ideal_indoor_humidity(fToC(outdoorTemp))
                }                                     
                if (indoorVOCSensor) {                
                    threshVOC = (settings.thresholdVOC) ?: 700
                    threshCarbonDioxide = (settings.thresholdCarbonDioxide) ?:500
                    indoorVOC=indoorVOCSensor.currentValue("voc")
                    indoorCarbonDioxide=indoorVOCSensor.currentValue("co2")
                }                                     
                if (indoorRadonSensor) {                
                    threshRadon = (settings.thresholdRadon) ?:100
                    indoorRadon=indoorRadonSensor.currentValue("radonShortTermAvg")
                }                                     
				switch (mode) { 
					case 'cool':
						coolingSetpoint = ecobee?.currentValue('coolingSetpoint')
					break                        
 					case 'auto': 
						coolingSetpoint = ecobee?.currentValue('coolingSetpoint')
					case 'heat':
					case 'emergency heat':
					case 'auto': 
					case 'off': 
 						heatingSetpoint = ecobee?.currentValue('heatingSetpoint')
					break
				}                        
				def detailedNotifFlag = (detailedNotif)? 'true':'false' 
				int min_vent_time = (givenVentMinTime!=null) ? givenVentMinTime : 20 //  20 min. ventilator time per hour by default
				int min_fan_time = (givenFanMinTime!=null) ? givenFanMinTime : 20 //  20 min. fan time per hour by default
				def equipStatus=ecobee?.currentValue("equipmentStatus")                
				def dParagraph = "TstatMode: $mode\n" +
					"TstatOperatingState: $operatingState\n" + 
					"TstatEquipStatus: $equipStatus\n" + 
 					"TstatTemperature: $indoorTemp${scale}\n" 
				if (coolingSetpoint)  { 
					 dParagraph = dParagraph + "CoolingSetpoint: ${coolingSetpoint}$scale\n"
				}     
				if (heatingSetpoint)  { 
					dParagraph = dParagraph + "HeatingSetpoint: ${heatingSetpoint}$scale\n" 
				}
				def min_humidity_diff = givenHumidityDiff ?: 5 //  5% humidity differential by default
				dParagraph = dParagraph +
					"DetailedNotification: ${detailedNotifFlag}\n" +
					"EcobeeCurrentProgram: $currentProgName\n" +
					"EcobeeClimateSet: $currentSetClimateName\n" +
					"EcobeeProgramType: $currentProgType\n" +
					"EcobeeHasHumidifier: $hasHumidifier\n" +
					"EcobeeHasDeHumidifier: $hasDehumidifier\n" +
					"DehumidifyWithAC: $dehumidifyWithACString\n" +
					"DehumidifyWithACOffset: ${settings.dehumidifyWithACOffset}\n" +
					"EcobeeHasHRV: $hasHrv\n" +
					"EcobeeHasERV: $hasErv\n" +
					"MinFanTime: ${min_fan_time} min.\n" +
					"IndoorHumidity: ${indoorHumidity}%\n" +
					"IndoorTemp: ${indoorTemp}$scale\n" +
					"UseFanWhenHumidityIsHigh: $useFanWhenHumidityIsHighString\n" +
					"useFanWithHumidifierSwitch: $useFanWithHumidifierSwitchesString\n" +
					"HumidityOffsetAllowed: +/- $min_humidity_diff%\n"                    
				if (outdoorSensor) {
					dParagraph=  dParagraph +                   
					"OutdoorHumidity: $outdoorHumidity%\n" +
					"OutdoorTemp: ${outdoorTemp}$scale\n" +
					"NormalizedOutHumidity: $corrOutdoorHum%\n" +
					"IdealIndoorHumidity: $idealIndoorHum%\n" 
				}
				if (indoorVOCSensor) {
					dParagraph=  dParagraph +                   
					"\nAir Quality\nVOC: ${indoorVOC} ppb\n" +
					"CarbonDioxide: ${indoorCarbonDioxide}\n" +
					"ThresholdVOC: ${threshVOC} ppb\n" +
					"ThresholdCarbonDioxide: ${threshCarbonDioxide}\n" 
					"ThresholdRadon: ${threshRadon}\n" 
				}
                
				if (indoorRadonSensor) {
					dParagraph=  dParagraph +                   
					"IndoorRadonLevel: ${indoorRadon}\n" 
					"ThresholdRadon: ${threshRadon}\n" 
				}
				paragraph dParagraph 
				if (hasDehumidifier=='true') {
					def min_temp = (givenMinTemp) ? givenMinTemp : ((scale=='C') ? -15 : 10)
					def useDehumidifierAsHRVFlag = (useDehumidifierAsHRV) ? 'true' : 'false'
					dParagraph=  "UseDehumidifierAsHRV: $useDehumidifierAsHRVFlag" +
						"\nMinDehumidifyTemp: $min_temp${scale}"
					if (useDehumidifierAsHRVFlag=='true') {
 						dParagraph= dParagraph + "\nMinVentTime $min_vent_time min." 
					}
					paragraph dParagraph 
				}
				if (hasHumidifier=='true') {
					def frostControlFlag = (frostControl) ? 'true' : 'false'
					dParagraph = "HumidifyFrostControl: $frostControlFlag" 
					paragraph dParagraph 
				}
				if ((hasHrv=='true') || (hasErv=='true')) {
					dParagraph=  "MinVentTime: $min_vent_time min."
					def freeCoolingFlag= (freeCooling) ? 'true' : 'false'
					dParagraph = dParagraph + "\nFreeCooling: $freeCoolingFlag"                     
					paragraph dParagraph 
				}
				if (humidifySwitches) {
					dParagraph=  "HumidifySwitch(es): $humidifySwitches"
					paragraph dParagraph 
				}
				if (dehumidifySwitches) {
					dParagraph=  "DehumidifySwitch(es): $dehumidifySwitches"
					paragraph dParagraph 
				}

				if (ted) {                
					int max_power = givenPowerLevel ?: 3000 // Do not run above 3000w consumption level by default
					dParagraph = "PowerMeter: $ted" + 
						"\nDoNotRunOver: ${max_power}W"
					paragraph dParagraph 
				}
                
			} /* end if ecobee */ 		
		}
		section("Humidifier/Dehumidifier/HRV/ERV Setup") {
			href(name: "toSensorsPage",  title: "Configure your sensors", description: "Tap to Configure...", image: getImagePath() + "HumiditySensor.png", page: "sensorSettings") 
			href(name: "toHumidifyPage",  title: "Configure your humidifier settings", description: "Tap to Configure...", image: getImagePath() + "Humidifier.jpg", page: "humidifySettings") 
			href(name: "toDehumidifyPage", title: "Configure your dehumidifier settings", description: "Tap to Configure...", image: getImagePath() + "dehumidifier.png", page: "dehumidifySettings") 
			href(name: "toVentilatorPage", title: "Configure your HRV/ERV settings", description: "Tap to Configure...", image: getImagePath() + "HRV.jpg", page: "ventilatorSettings") 
			href(name: "toNotificationsPage", title: "Other Options & Notification Setup",  description: "Tap to Configure...", image: getImagePath() + "Fan.png", page: "otherSettings")
		}            
		section("About") {
			paragraph "${get_APP_NAME()}, the smartapp that can control your house's humidity via your connected humidifier/dehumidifier/HRV/ERV"
			paragraph "Version ${get_APP_VERSION()}"
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.me/ecomatiqhomes",
					title:"Paypal donation..."
			paragraph "Copyright©2014-2020 Yves Racine"
				href url:"https://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
 					description: "https://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
       
	} /* end dashboardPage */ 
    
}

def sensorSettings() {
	dynamicPage(name: "sensorSettings", title: "Sensors to be used", install: false, nextPage: humidifySettings) {
		section("Choose Indoor humidity sensor to be used for better adjustment (optional, default=ecobee sensor)") {
			input "indoorSensor", "capability.relativeHumidityMeasurement", title: "Indoor Humidity Sensor", required: false
		}
		section("Choose Outdoor humidity sensor to be used (weatherStation or sensor)") {
			input "outdoorSensor", "capability.relativeHumidityMeasurement", title: "Outdoor Humidity Sensor"
		}
		section("Choose Indoor VOC/CarbonDioxide sensor to be used for better air quality (optional)") {
			input "indoorVOCSensor", "capability.carbonDioxideMeasurement", title: "Indoor Air Quality (CarbonDioxide/VOC) Sensor", required: false
		}
		section("Choose Indoor Radon sensor to be used for better air quality (optional)") {
			input "indoorRadonSensor", "capability.carbonDioxideMeasurement", title: "Indoor Air Quality radon Sensor", required: false
		}
        if (indoorVOCSensor) {
    		section {
                input "thresholdVOC", "number", title: "Threshold VOC for air circulation [default=700 ppb]", description: 'optional',required: false
                input "thresholdCarbonDioxide", "number", title: "Threshold Carbon Dioxide level for air circulation [default=500]", description: 'optional',required: false
		    }
        }
        if (indoorRadonSensor) {
    		section {
                input "thresholdRadon", "number", title: "Threshold Radon level for air circulation [default=100]", description: 'optional',required: false
		    }
        }
		section {
			href(name: "toDashboardPage", title: "Back to Dashboard Page", page: "dashboardPage")
		}
	}
}


def humidifySettings() {
	dynamicPage(name: "humidifySettings", install: false, uninstall: false, nextPage: dehumidifySettings) {
		section("Frost control for humidifier [optional]") {
			input "frostControl", "bool", title: "Frost control [default=false]?",  description: 'optional', required: false
		}
		section("Switch(es) to be turned on when needed for humidifying/ventilating the house[optional]") {
			input "humidifySwitches", "capability.switch", multiple:true, required: false
			input "useFanWithHumidifierSwitches", "bool", title: "Trigger HVAC's fan when humidifier switch is on?", required: false
		}
		section {
			href(name: "toDashboardPage", title: "Back to Dashboard Page", page: "dashboardPage")
		}
	}
}

def dehumidifySettings() {
	dynamicPage(name: "dehumidifySettings", install: false, uninstall: false, nextPage: ventilatorSettings) {
		section("Free cooling using HRV/Dehumidifier [By default=false]") {
			input "freeCooling", "bool", title: "Free Cooling?", required: false
		}
		section("Your dehumidifier as HRV input parameters section  [optional]") {
			input "useDehumidifierAsHRV", "bool", title: "Use Dehumidifier as HRV (By default=false)?", description: 'optional', required: false
			input "givenVentMinTime", "number", title: "Minimum HRV/ERV runtime [default=20]", description: 'optional',required: false
		}
		section("Minimum outdoor threshold for stopping dehumidification (in Farenheits/Celsius) [optional]") {
			input "givenMinTemp", "decimal", title: "Min Outdoor Temp [default=10°F/-15°C]", description: 'optional', required: false
		}
		section("¨Dehumidify With AC - Use your AC to dehumidify when humidity is high and dehumidifier is not available [optional]") {
			input "dehumidifyWithACFlag",  "bool", title: "Use AC as dehumidifier (By default=false)?", description: 'optional', required: false
			input "dehumidifyWithACOffset",  "number", title: "Offset in Farenheit (+delta) to be applied to cooling setpoint for dehumidifying  [range: 0..50, need to be a multiple of 5F]", description: 'optional', required: false
		}
		section("Switch(es) to be turned on when needed for dehumidifying/ventilating the house[optional]") {
			input "dehumidifySwitches", "capability.switch", multiple:true, required: false
			input "useFanWhenHumidityIsHigh", "bool", title: "Trigger HVAC's fan when humidity is high?", required: false
		}
		        
		section {
			href(name: "toDashboardPage", title: "Back to Dashboard Page", page: "dashboardPage")
		}
	}
}

def ventilatorSettings() {
	dynamicPage(name: "ventilatorSettings", install: false, uninstall: false, nextPage: otherSettings) {
		section("Free cooling using HRV [By default=false]") {
			input "freeCooling", "bool", title: "Free Cooling?", required: false
		}
		section("Minimum HRV/ERV runtime in minutes") {
			input "givenVentMinTime", "number", title: "Minimum HRV/ERV [default=20 min.]", description: 'optional',required: false
		}
		section("Minimum outdoor threshold for stopping ventilation (in Farenheits/Celsius) [optional]") {
			input "givenMinTemp", "decimal", title: "Min Outdoor Temp [default=10°F/-15°C]", description: 'optional', required: false
		}
		section("Switch(es) to be turned on when needed for dehumidifying/ventilating the house[optional]") {
			input "dehumidifySwitches", "capability.switch", multiple:true, required: false
			input "useFanWhenHumidityIsHigh", "bool", title: "Trigger HVAC's fan when humidity is high?", required: false
		}
		section {
			href(name: "toDashboardPage", title: "Back to Dashboard Page", page: "dashboardPage")
		}
	}
}



def otherSettings() {
	def enumModes=location.modes.collect{ it.name }

	dynamicPage(name: "otherSettings", title: "Other Settings", install: true, uninstall: false) {
		section("Minimum fan runtime per hour in minutes") {
			input "givenFanMinTime", "number", title: "Minimum fan runtime [default=20]", required: false
		}
		section("Check energy consumption at [optional, to avoid using HRV/ERV/Humidifier/Dehumidifier at peak]") {
			input "ted", "capability.powerMeter", title: "Power meter?", description: 'optional', required: false
			input "givenPowerLevel", "number", title: "power?",  description: 'optional',required: false
		}
		section("What do I use for the Master on/off switch to enable/disable processing? (optional)") {
			input "powerSwitch", "capability.switch", required: false
		}
		if (isST()) {        
    			section("Notifications") {
	    			input "sendPushMessage", "enum", title: "Send a push notification?", options:["Yes", "No"], required:
		    			false
    				input "phoneNumber", "phone", title: "Send a text message?", required: false
	    		}
    			section("Enable Amazon Echo/Ask Alexa Notifications for events logging (optional)") {
	    			input (name:"askAlexaFlag", title: "Ask Alexa verbal Notifications [default=false]?", type:"bool",
			    		description:"optional",required:false)
    				input (name:"listOfMQs",  type:"enum", title: "List of the Ask Alexa Message Queues (default=Primary)", options: state?.askAlexaMQ, multiple: true, required: false,
				    	description:"optional")            
	    			input "AskAlexaExpiresInDays", "number", title: "Ask Alexa's messages expiration in days (optional,default=2 days)?", required: false
	    		}
        	}        
                    
    		section("Logging") {
		    	input "detailedNotif", "bool", title: "Detailed Logging?", required:
		   		false
        	}
		section("Set Humidity Level only for specific mode(s) [default=all]")  {
			input (name:"selectedMode", type:"enum", title: "Choose Mode", options: enumModes, 
				required: false, multiple:true, description: "Optional")
		}
    		section([mobileOnly: true]) {
	    		label title: "Assign a name for this SmartApp", required: false
        	}
		section {
			href(name: "toDashboardPage", title: "Back to Dashboard Page", page: "dashboardPage")
		}
	}
}

boolean isST() { 
    return (getHub() == "SmartThings") 
}

private getHub() {
    def result = "SmartThings"
    if(state?.hub == null) {
        try { [value: "value"]?.encodeAsJson(); } catch (e) { result = "Hubitat" }
        state?.hub = result
    }
    log.debug "hubPlatform: (${state?.hub})"
    return state?.hub
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
	state.currentRevision = null // for further check with thermostatRevision later

	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler)
		subscribe(powerSwitch, "switch.on", onHandler)
	}
	int delay = givenInterval ?: 59 // By default, do it every hour
	if ((delay < 10) || (delay > 59)) {
		log.error "Scheduling delay not in range (${delay} min.), exiting"
		send("error, inputted delay $delay in minutes not in range  [10..59]")
		return
	}
	if (detailedNotif) {   
		log.debug "initialize>scheduling Humidity Monitoring and adjustment every ${delay} minutes"
	}

	state?.poll = [ last: 0, rescheduled: now() ]

	subscribe(location, "askAlexaMQ", askAlexaMQHandler)    

	//Subscribe to different events (ex. sunrise and sunset events) to trigger rescheduling if needed
	subscribe(location, "sunrise", rescheduleIfNeeded)
	subscribe(location, "sunset", rescheduleIfNeeded)
	subscribe(location, "mode", rescheduleIfNeeded)
	subscribe(location, "sunriseTime", rescheduleIfNeeded)
	subscribe(location, "sunsetTime", rescheduleIfNeeded)

	rescheduleIfNeeded()   
}

def askAlexaMQHandler(evt) {
	if (!evt) return
	switch (evt.value) {
		case "refresh":
		state?.askAlexaMQ = evt.jsonData && evt.jsonData?.queues ? evt.jsonData.queues : []
		log.debug ("askAlexaMQHandler>new refresh value=$evt.jsonData?.queues")
		break
	}
}


def appTouch(evt) {
	rescheduleIfNeeded()

}

def rescheduleIfNeeded(evt) {
	if (evt) log.debug("rescheduleIfNeeded>$evt.name=$evt.value")
	int delay = givenInterval ?: 59 // By default, do it every hour
	BigDecimal currentTime = now()    
	BigDecimal lastPollTime = (currentTime - (state?.poll["last"]?:0))  
 
	if (lastPollTime != currentTime) {    
		Double lastPollTimeInMinutes = (lastPollTime/60000).toDouble().round(1)      
		log.info "rescheduleIfNeeded>last poll was  ${lastPollTimeInMinutes.toString()} minutes ago"
	}
	if (((state?.poll["last"]?:0) + (delay * 60000) < currentTime)) {
		log.info "rescheduleIfNeeded>scheduling setHumidityLevel in ${delay} minutes.."
		schedule("0 0/${delay} * * * ?", setHumidityLevel)
		setHumidityLevel()
	}
    
    
	// Update rescheduled state
    
	if (!evt) state.poll["rescheduled"] = now()
}



def offHandler(evt) {
	log.debug "$evt.name: $evt.value"
    
	if (dehumidifySwitches) {
		if (detailedNotif) {    
			log.trace("turning off all dehumidify/fan switches")
		}
		dehumidifySwitches.off()        
	}            
    
	if (humidifySwitches) {
		if (detailedNotif) {    
			log.trace("turning off all humidify/fan switches")
		}
		humidifySwitches.off()        
	}            
    
}

def onHandler(evt) {
	log.debug "$evt.name: $evt.value"
	setHumidityLevel()
}



def setHumidityLevel() {
	int scheduleInterval = givenInterval ?: 59 // By default, do it every hour

	def todayDay = new Date().format("dd",location.timeZone)
	if ((!state?.today) || (todayDay != state?.today)) {
		state?.exceptionCount=0   
		state?.sendExceptionCount=0        
		state?.today=todayDay        
	}   

	state?.poll["last"] = now()
	
    
    
	//schedule the rescheduleIfNeeded() function
	if (((state?.poll["rescheduled"]?:0) + (scheduleInterval * 60000)) < now()) {
		log.info "takeAction>scheduling rescheduleIfNeeded() in ${scheduleInterval} minutes.."
		schedule("0 0/${scheduleInterval} * * * ?", rescheduleIfNeeded)
		// Update rescheduled state
		state?.poll["rescheduled"] = now()
	}

	boolean foundMode=selectedMode.find{it == (location.mode as String)} 
	if ((selectedMode != null) && (!foundMode)) {
		if (detailedNotif) {    
			log.trace("setHumidityLevel does not apply,location.mode= $location.mode, selectedMode=${selectedMode},foundMode=${foundMode}, turning off all equipments")
		}
		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'humidifierMode': 'off', 'dehumidifyWithAC': 'false',
			'vent': 'off', 'ventilatorFreeCooling': 'false'
			])
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("setHumidityLevel does not apply,location.mode= $location.mode, turning off all dehumidify/fan switches")
			}
			dehumidifySwitches.off()        
		}        
		if (humidifySwitches) {
			if (detailedNotif) {    
				log.trace("setHumidityLevel does not apply,location.mode= $location.mode, turning off all humidify/fan switches")
			}
			humidifySwitches.off()        
		}        
		return			
	}    
    
	if (detailedNotif) {
		send("monitoring every ${scheduleInterval} minute(s)")
		log.debug "Scheduling Humidity Monitoring & Change every ${scheduleInterval}  minutes"
	}


	if (powerSwitch ?.currentSwitch == "off") {
		if (detailedNotif) {
			send("Virtual master switch ${powerSwitch.name} is off, processing on hold...")
			log.debug("Virtual master switch ${powerSwitch.name} is off, processing on hold...")
		}
		return
	}
	def min_humidity_diff = givenHumidityDiff ?: 5 //  5% humidity differential by default
	int min_fan_time = (givenFanMinTime!=null) ? givenFanMinTime : 20 //  20 min. fan time per hour by default
	int min_vent_time = (givenVentMinTime!=null) ? givenVentMinTime : 20 //  20 min. ventilator time per hour by default
	def freeCoolingFlag = (freeCooling != null) ? freeCooling : false // Free cooling using the Hrv/Erv/dehumidifier
	def frostControlFlag = (frostControl != null) ? frostControl : false // Frost Control for humdifier, by default=false
	def min_temp // Min temp in Farenheits for using HRV/ERV,otherwise too cold
    float threshVOC = (settings.thresholdVOC) ?: 700
    float threshCarbonDioxide = (settings.thresholdCarbonDioxide) ?:500    
    float threshRadon = (settings.thresholdCarbonDioxide) ?:100    

	def scale = getTemperatureScale()
	if (scale == 'C') {
		min_temp = (givenMinTemp) ? givenMinTemp : -15 // Min. temp in Celcius for using HRV/ERV,otherwise too cold
	} else {

		min_temp = (givenMinTemp) ? givenMinTemp : 10 // Min temp in Farenheits for using HRV/ERV,otherwise too cold
	}
	int max_power = givenPowerLevel ?: 3000 // Do not run above 3000w consumption level by default


	//  Polling of all devices

	def MAX_EXCEPTION_COUNT=10
	String exceptionCheck, msg 
	try {        
		ecobee.poll()
		exceptionCheck= ecobee.currentVerboseTrace.toString()
		if ((exceptionCheck) && ((exceptionCheck.contains("exception") || (exceptionCheck.contains("error")) && 
			(!exceptionCheck.contains("Java.util.concurrent.TimeoutException"))))) {  
		// check if there is any exception or an error reported in the verboseTrace associated to the device (except the ones linked to rate limiting).
			state?.exceptionCount=state.exceptionCount+1    
			log.error "setHumidityLevel>found exception/error after polling, exceptionCount= ${state?.exceptionCount}: $exceptionCheck" 
		} else {             
			// reset exception counter            
			state?.exceptionCount=0       
		}                
	} catch (e) {
		log.error "setHumidityLevel>exception $e while trying to poll the device, exceptionCount= ${state?.exceptionCount}" 
	}
	if ((state?.exceptionCount>=MAX_EXCEPTION_COUNT) || ((exceptionCheck) && (exceptionCheck.contains("Unauthorized")))) {
		// need to authenticate again    
		msg="too many exceptions/errors or unauthorized exception, $exceptionCheck (${state?.exceptionCount} errors), may need to re-authenticate at ecobee..." 
		log.error msg
		send (msg,askAlexaFlag)
		return        
	}    

	if (outdoorSensor && outdoorSensor.hasCapability("Polling")) {
		try {    
			outdoorSensor.poll()
		} catch (e) {
			msg="not able to poll ${outdoorSensor}'s temp value"
			log.warn msg
			if (detailedNotif) {
        		send (msg,askAlexaFlag)
			}
		}
	} else if (outdoorSensor && outdoorSensor.hasCapability("Refresh")) {
		try {    
			outdoorSensor.refresh()
		} catch (e) {
			msg="not able to refresh ${outdoorSensor}'s temp value"
			log.warn msg            
			if (detailedNotif) {
          		send (msg,askAlexaFlag)
			}
		}
	} 
    def indoorCarbonDioxide, indoorVOC, indoorRadon
    

	if (indoorVOCSensor)
        if (indoorVOCSensor.hasCapability("Refresh")) {
		    try {    
			    indoorVOCSensor.refresh()
		    } catch (e) {
			    msg="not able to refresh ${indoorVOCSensor}'s carbonDioxide and voc values"
			    log.warn msg
			    if (detailedNotif) {
        		    send (msg,askAlexaFlag)
			    }
            }
		}
		try {    
            indoorVOC=indoorVOCSensor.currentValue("voc")
			if (detailedNotif) {
			    msg="indoorVOC value is ${indoorVOC}"
			    log.info msg
            }
                            
            indoorCarbonDioxide=indoorVOCSensor.currentValue("co2")
			if (detailedNotif) {
			    msg="indoorCarbonDioxide value is ${indoorCarbonDioxide}"
			    log.info msg
            }            
        } catch (e) {
			    msg="not able to extract ${indoorVOCSensor}'s carbonDioxide or VOC value"
			    log.warn msg
			    if (detailedNotif) {
        		    send (msg,askAlexaFlag)
        }
 	}     
    if (indoorRadonSensor) {                
        if (indoorRadonSensor.hasCapability("Refresh")) {
		    try {    
			    indoorRadonSensor.refresh()
		    } catch (e) {
			    msg="not able to refresh ${indoorRadonSensor}'s Radon values"
			    log.warn msg
			    if (detailedNotif) {
        		    send (msg,askAlexaFlag)
			    }
            }
		}
		try {    
            indoorRadon=indoorRadonSensor.currentValue("radonShortTermAvg")
			if (detailedNotif) {
			    msg="indoorRadon value is ${indoorRadon}"
			    log.info msg
            }
            
        } catch (e) {
			    msg="not able to extract ${indoorRadonSensor}'s  Radon value"
			    log.warn msg
			    if (detailedNotif) {
        		    send (msg,askAlexaFlag)
                    
            }
        }
 	}     
	if (ted) {

		try {
			if (ted.hasCapability("Polling")) ted.poll()
			int powerConsumed = ted.currentPower.toInteger()
			if (powerConsumed > max_power) {

				// peak of energy consumption, turn off all devices

				if (detailedNotif) {
					msg="all off,power usage is too high=${ted.currentPower}"
					log.info msg
					send (msg,askAlexaFlag)
				}

				ecobee.setThermostatSettings("", ['vent': 'off', 'dehumidifierMode': 'off', 'humidifierMode': 'off'])
				if (dehumidifySwitches) {
					if (detailedNotif) {    
						log.trace("all off,power usage is too high, turning off all dehumidify/fan switches")
					}
					dehumidifySwitches.off()        
				}        
				if (humidifySwitches) {
					if (detailedNotif) {    
						log.trace("all off,power usage is too high, turning off all humidify/fan switches")
					}
					humidifySwitches.off()        
				}        
				return

			}
		} catch (e) {
			log.error "Exception $e while trying to get power data "
		}
	}


	def heatTemp = ecobee.currentHeatingSetpoint
	def coolTemp = ecobee.currentCoolingSetpoint
	def ecobeeHumidity = ecobee.currentHumidity
	def indoorHumidity = 0
	def indoorTemp = ecobee.currentTemperature
	def hasDehumidifier = (ecobee.currentHasDehumidifier) ? ecobee.currentHasDehumidifier : 'false'
	def hasHumidifier = (ecobee.currentHasHumidifier) ? ecobee.currentHasHumidifier : 'false'
	def hasHrv = (ecobee.currentHasHrv) ? ecobee.currentHasHrv : 'false'
	def hasErv = (ecobee.currentHasErv) ? ecobee.currentHasErv : 'false'
	def useDehumidifierAsHRVFlag = (useDehumidifierAsHRV) ? useDehumidifierAsHRV : false
	def outdoorHumidity
	// use the readings from another sensor if better precision neeeded
	if (indoorSensor) {
		indoorHumidity = indoorSensor.currentHumidity
		indoorTemp = indoorSensor.currentTemperature
	}
    
	def outdoorSensorHumidity = ( outdoorSensor) ? outdoorSensor.currentHumidity : 50 // if no outdoor sensor, default is 50
	def outdoorTemp =  ( outdoorSensor) ? outdoorSensor.currentTemperature : (scale=='C') ? 20 : 70
	// by default, the humidity level is calculated based on a sliding scale target based on outdoorTemp

	def target_humidity = givenHumidityLevel ?: (scale == 'C')? find_ideal_indoor_humidity(outdoorTemp):
		find_ideal_indoor_humidity(fToC(outdoorTemp))    

	String ecobeeMode = ecobee.currentThermostatMode.toString()
	if (detailedNotif) {   
		log.debug "MonitorAndSetEcobeeHumidity>location.mode = $location.mode"
		log.debug "MonitorAndSetEcobeeHumidity>ecobee Mode = $ecobeeMode"
	}

	outdoorHumidity = (scale == 'C') ?
		calculate_corr_humidity(outdoorTemp, outdoorSensorHumidity, indoorTemp) :
		calculate_corr_humidity(fToC(outdoorTemp), outdoorSensorHumidity, fToC(indoorTemp))


	//  If indoorSensor specified, use the more precise humidity measure instead of ecobeeHumidity

	if ((indoorSensor) && (indoorHumidity < ecobeeHumidity)) {
		ecobeeHumidity = indoorHumidity
	}

	if (detailedNotif) {
		log.trace("Ecobee's humidity: ${ecobeeHumidity} vs. indoor humidity ${indoorHumidity}")
		log.debug "outdoorSensorHumidity = $outdoorSensorHumidity%, normalized outdoorHumidity based on ambient temperature = $outdoorHumidity%"
		send ("normalized outdoor humidity is ${outdoorHumidity}%,sensor outdoor humidity ${outdoorSensorHumidity}%,vs. indoor Humidity ${ecobeeHumidity}%", 
			askAlexaFlag)
		log.trace("Evaluate: Ecobee humidity: ${ecobeeHumidity} vs. outdoor humidity ${outdoorHumidity}, humidity differential allowed= ${min_humidity_diff}, " +
			"coolingSetpoint: ${coolTemp} , heatingSetpoint: ${heatTemp}, target humidity=${target_humidity}, fanMinOnTime=${min_fan_time}")
		log.trace("hasErv=${hasErv}, hasHrv=${hasHrv},hasHumidifier=${hasHumidifier},hasDehumidifier=${hasDehumidifier}, freeCoolingFlag=${freeCoolingFlag}," +
			"useDehumidifierAsHRV=${useDehumidifierAsHRVFlag}, useFanWhenHumidityIsHigh=${useFanWhenHumidityIsHigh}")
	}

	if ((ecobeeMode == 'cool' && (hasHrv == 'true' || hasErv == 'true')) &&
		(ecobeeHumidity >= outdoorHumidity) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff))) {
		if (detailedNotif) {
			log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, " +
			"need to dehumidify the house and normalized outdoor humidity is lower (${outdoorHumidity})"
			send ("dehumidify to ${target_humidity}% in ${ecobeeMode} mode, using ERV/HRV and $dehumidifySwitches switch(es) ", askAlexaFlag)
		}
	
		// Turn on the dehumidifer and HRV/ERV, the outdoor humidity is lower or equivalent than inside

		ecobee.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}", 'vent': 'minontime', 'ventilatorMinOnTime': "${min_vent_time}"])
		if (settings.useFanWhenHumidityIsHigh) {
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
			ecobee.fanOn() // set fan on
		}

		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            
	} else if (((ecobeeMode in ['heat','off', 'auto']) && (hasHrv == 'false' && hasErv == 'false' && hasDehumidifier == 'true')) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
		(ecobeeHumidity >= outdoorHumidity) &&
		(outdoorTemp > min_temp)) {

		if (detailedNotif) {
			log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
				"normalized outdoor humidity is within range (${outdoorHumidity}) & outdoor temp is ${outdoorTemp},not too cold, using connected dehumidifier and $dehumidifySwitches switch(es)"
			send ("dehumidify to ${target_humidity}% in ${ecobeeMode} mode using connected dehumidifier and $dehumidifySwitches switch(es)", askAlexaFlag)
		}

		//      Turn on the dehumidifer, the outdoor temp is not too cold 

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}",
			'humidifierMode': 'off', 'fanMinOnTime': "${min_fan_time}"
			])

		if (settings.useFanWhenHumidityIsHigh) {
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
			ecobee.fanOn() // set fan on
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else if (((ecobeeMode in ['heat','off', 'auto']) && ((hasHrv == 'true' || hasErv == 'true') && hasDehumidifier == 'false')) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
		(ecobeeHumidity >= outdoorHumidity) &&
		(outdoorTemp > min_temp)) {

		if (detailedNotif) {
			log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
				"normalized outdoor humidity is within range (${outdoorHumidity}) & outdoor temp is ${outdoorTemp},not too cold using connected HRV/ERV and $dehumidifySwitches switch(es)"
			send ("using HRV/ERV and $dehumidifySwitches switch(es) to dehumidify ${target_humidity}% in ${ecobeeMode} mode", askAlexaFlag)
		}

		//      Turn on the HRV/ERV, the outdoor temp is not too cold 

		ecobee.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}", 'vent': 'minontime', 'ventilatorMinOnTime': "${min_vent_time}"])
		if (settings.useFanWhenHumidityIsHigh) {
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
			ecobee.fanOn() // set fan on
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else if (((ecobeeMode in ['heat','off', 'auto']) && (hasHrv == 'true' || hasErv == 'true' || hasDehumidifier == 'true')) &&
		(ecobeeHumidity >= (target_humidity + min_humidity_diff)) &&
		(ecobeeHumidity >= outdoorHumidity) &&
		(outdoorTemp <= min_temp)) {


		//      Turn off the dehumidifer and HRV/ERV because it's too cold till the next cycle.

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'dehumidifierLevel': "${target_humidity}",
			'humidifierMode': 'off', 'vent': 'off'
			])

		if (detailedNotif) {
			log.trace "Ecobee is in ${ecobeeMode} mode and its humidity > target humidity level=${target_humidity}, need to dehumidify the house " +
				"normalized outdoor humidity is lower (${outdoorHumidity}), but outdoor temp is ${outdoorTemp}: too cold to dehumidify, using $dehumidifySwitches switch(es) only"
			send ("Too cold (${outdoorTemp}°) to dehumidify to ${target_humidity}, using $dehumidifySwitches switch(es) only", askAlexaFlag)
		}
		if (settings.useFanWhenHumidityIsHigh) {
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
			ecobee.fanOn() // set fan on
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else if ((((ecobeeMode in ['heat','off', 'auto']) && hasHumidifier == 'true')) &&
		(ecobeeHumidity < (target_humidity - min_humidity_diff))) {

		if (detailedNotif) {
			log.trace("In ${ecobeeMode} mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house with connected humidifier and $humidifySwitches switch(es)")
			send ("humidify to ${target_humidity} in ${ecobeeMode} mode using connected humidifier and $humidifySwitches switch(es)", askAlexaFlag)
		}
		//      Need a minimum differential to humidify the house to the target if any humidifier available

		def humidifierMode = (frostControlFlag) ? 'auto' : 'manual'
		ecobee.setThermostatSettings("", ['humidifierMode': "${humidifierMode}", 'humidity': "${target_humidity}", 'dehumidifierMode': 'off'])
		if (humidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and is way lower than target humidity, turning on all humidify/fan switches")
			}
			humidifySwitches.on()        
			if (settings.useFanWithHumidifierSwitches) {
				if (detailedNotif) {
					log.trace("Indoor humidity is ${ecobeeHumidity}% and below the target humidity, triggering the HVAC fan as requested")
					send("Indoor humidity is ${ecobeeHumidity}% and below the target humidity, triggering the HVAC fan as requested")
				}
				ecobee.fanOn() // set fan on
			}
		}            

	} else if ((((ecobeeMode in ['heat','off', 'auto']) && hasHumidifier == 'false')) &&
		(ecobeeHumidity < (target_humidity - min_humidity_diff))) {

		if (detailedNotif) {
			log.trace("In ${ecobeeMode} mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house, but no humidifier is connected to ecobee, using $humidifySwitches switch(es) only")
		}
		//      Need a minimum differential to humidify the house to the target if any humidifier available

		if (humidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and is way lower than target humidity, turning on all humidify/fan switches")
				send ("In ${ecobeeMode} mode, Ecobee's humidity provided is way lower than target humidity level=${target_humidity}, need to humidify the house, using $humidifySwitches switch(es)",
					askAlexaFlag)            
			}
			humidifySwitches.on()        
			if (settings.useFanWithHumidifierSwitches) {
				if (detailedNotif) {
					log.trace("Indoor humidity is ${ecobeeHumidity}% and below the target humidity, triggering the HVAC fan as requested")
					send("Indoor humidity is ${ecobeeHumidity}% and below the target humidity, triggering the HVAC fan as requested")
				}
				ecobee.fanOn() // set fan on
			}
		}            

	} else if (((ecobeeMode == 'cool' && dehumidifyWithACFlag==true) && (hasDehumidifier == 'false') && (hasHrv == 'false' && hasErv == 'false')) &&
		(ecobeeHumidity > (target_humidity + min_humidity_diff)) &&
		(outdoorHumidity > target_humidity)) {


		if (detailedNotif) {
			log.trace("Ecobee humidity provided is way higher than target humidity level=${target_humidity}, need to dehumidify with AC, because normalized outdoor humidity is too high=${outdoorHumidity}")
			send ("dehumidifyWithAC in cooling mode, indoor humidity is ${ecobeeHumidity}% and normalized outdoor humidity (${outdoorHumidity}%) is too high to dehumidify, using $dehumidifySwitches switch(es) only",
					askAlexaFlag)            
		}
		//      If mode is cooling and outdoor humidity is too high then use the A/C to lower humidity in the house if there is no dehumidifier

		if (settings.dehumidifyWithACOffset!=null)   {
        
			ecobee.setThermostatSettings("", ['dehumidifyWithAC': 'true', 'dehumidifyOvercoolOffset':dehumidifyWithACOffset , 'dehumidifierLevel': "${target_humidity}",
				'dehumidiferMode': 'off', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
				])
		
		} else   {       
			ecobee.setThermostatSettings("", ['dehumidifyWithAC': 'true', 'dehumidifierLevel': "${target_humidity}",
				'dehumidiferMode': 'off', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
				])

		}
		if (settings.useFanWhenHumidityIsHigh) {
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
			ecobee.fanOn() // set fan on
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else if (((ecobeeMode == 'cool' && !dehumidifyWithACFlag) && (hasDehumidifier == 'false') && (hasHrv == 'false' && hasErv == 'false')) &&
		(ecobeeHumidity > (target_humidity + min_humidity_diff)) &&
		(outdoorHumidity > target_humidity)) {


		if (detailedNotif) {
			log.trace("Ecobee humidity provided is way higher than target humidity level=${target_humidity}, need to dehumidify with AC, no dehumidifier is available, flag is set to false in the smartapp...")
			send("Ecobee humidity provided is way higher than target humidity level=${target_humidity}, need to dehumidify with AC, no dehumidifier is available, flag is set to false in the smartapp, using $dehumidifySwitches switch(es) only",
				askAlexaFlag)            
		}
		//      If mode is cooling and outdoor humidity is too high then use the A/C to lower humidity in the house if there is no dehumidifier

		ecobee.setThermostatSettings("", ['dehumidifyWithAC': 'false', 'dehumidifierLevel': "${target_humidity}",
			'dehumidiferMode': 'off', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
			])

		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else if ((ecobeeMode == 'cool') && (hasDehumidifier == 'true') && (!useDehumidifierAsHRVFlag) &&
		(ecobeeHumidity > (target_humidity + min_humidity_diff))) {

		//      If mode is cooling and outdoor humidity is too high, then just use dehumidifier if any available

		if (detailedNotif) {
			log.trace "Dehumidify to ${target_humidity} in ${ecobeeMode} mode using the dehumidifier"
			send ("dehumidify to ${target_humidity}% in ${ecobeeMode} mode using the connected dehumidifier and $dehumidifySwitches switch(es)",askAlexaFlag)
		}

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}", 'humidifierMode': 'off',
			'dehumidifyWithAC': 'false', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
			])

		if (settings.useFanWhenHumidityIsHigh) {
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan and $dehumidifySwitches switch(es) as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan and $dehumidifySwitches switch(es)  as requested")
			}
			ecobee.fanOn() // set fan on
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            
        
	} else if ((ecobeeMode == 'cool') && (hasDehumidifier == 'true') && (useDehumidifierAsHRVFlag) &&
		(outdoorHumidity < target_humidity + min_humidity_diff) &&
		(ecobeeHumidity > (target_humidity + min_humidity_diff))) {

		//      If mode is cooling and outdoor humidity is too high, then just use dehumidifier if any available

		if (detailedNotif) {
			log.trace "Dehumidify to ${target_humidity} in ${ecobeeMode} mode using the dehumidifier"
			send ("dehumidify to ${target_humidity}% in ${ecobeeMode} mode using the connected dehumidifier and $dehumidifySwitches switch(es)", askAlexaFlag)
		}
		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}", 'humidifierMode': 'off',
			'dehumidifyWithAC': 'false', 'fanMinOnTime': "${min_fan_time}", 'vent': 'off'
			])


		if (settings.useFanWhenHumidityIsHigh) {
			ecobee.fanOn() // set fan on
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            


	} else if (((ecobeeMode == 'cool') && (hasDehumidifier == 'true' && hasErv == 'false' && hasHrv == 'false')) &&
		(outdoorTemp < indoorTemp) && (freeCoolingFlag)) {

		//      If mode is cooling and outdoor temp is lower than inside, then just use dehumidifier for better cooling if any available

		if (detailedNotif) {
			log.trace "In cooling mode, outdoor temp is lower than inside, using dehumidifier for free cooling"
			send ("Outdoor temp is lower than inside, using connected dehumidifier and $dehumidifySwitches switch(es) for more efficient cooling ", askAlexaFlag)
		}

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}", 'humidifierMode': 'off',
			'dehumidifyWithAC': 'false', 'fanMinOnTime': "${min_fan_time}"
			])
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            


	} else if ((ecobeeMode == 'cool' && (hasHrv == 'true')) && (outdoorTemp < indoorTemp) && (freeCoolingFlag)) {

		if (detailedNotif) {
			log.trace("In cooling mode, outdoor temp is lower than inside, using the HRV to get fresh air")
			send ("Outdoor temp is lower than inside, using the connected HRV and $dehumidifySwitches switch(es) for more efficient cooling", askAlexaFlag)

		}
		//      If mode is cooling and outdoor's temp is lower than inside, then use HRV to get fresh air into the house

		ecobee.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}",
			'vent': 'minontime', 'ventilatorMinOnTime': "${min_vent_time}", 'ventilatorFreeCooling': 'true'
			])

		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("free cooling is on, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else if ((outdoorHumidity > ecobeeHumidity) && (ecobeeHumidity > (target_humidity + min_humidity_diff))) {

		//      If indoor humidity is greater than target, but outdoor humidity is way higher than indoor humidity, 
		//      just wait for the next cycle & do nothing for now.

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'humidifierMode': 'off', 'vent': 'off'])
		if (detailedNotif) {
			log.trace("Indoor humidity is ${ecobeeHumidity}%, but outdoor humidity (${outdoorHumidity}%) is too high to dehumidify")
			send ("indoor humidity is ${ecobeeHumidity}%, but outdoor humidity ${outdoorHumidity}% is too high to dehumidify, using $dehumidifySwitches switch(es) only", askAlexaFlag)
		}

		if (settings.useFanWhenHumidityIsHigh) {
			ecobee.fanOn() // set fan on
			if (detailedNotif) {
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
				send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan as requested")
			}
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            
        
	} else if ((ecobeeHumidity > (target_humidity + min_humidity_diff)) && (settings.useFanWhenHumidityIsHigh)) {
		if (detailedNotif) {
			log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan and $dehumidifySwitches switch(es) as requested")
			send("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, triggering the HVAC fan and $dehumidifySwitches switch(es) as requested")
		}
		ecobee.fanOn() // set fan on
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor humidity is ${ecobeeHumidity}% and above the target humidity, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            

	} else {

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'off', 'humidifierMode': 'off', 'dehumidifyWithAC': 'false',
			'vent': 'off', 'ventilatorFreeCooling': 'false'
			])
		if (detailedNotif) {
			log.trace("All off, humidity level (${ecobeeHumidity}%) within range")
			send ("all off, humidity level (${ecobeeHumidity}%) within range", askAlexaFlag)
		}
		if (settings.useFanWhenHumidityIsHigh) {
			ecobee.fanAuto() // set fan to auto
		}
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("humidity level (${ecobeeHumidity}%) within range, turning off all dehumidify/fan switches")
			}
			dehumidifySwitches.off()        
		}            
		if (humidifySwitches) {
			if (detailedNotif) {    
				log.trace("humidity level (${ecobeeHumidity}%) within range, turning off all humidify/fan switches")
			}
			humidifySwitches.off()        
			if (settings.useFanWithHumidifierSwitches) {
				if (detailedNotif) {
					log.trace("Indoor humidity is ${ecobeeHumidity}% and close to target humidity, setting the HVAC's fan to auto")
					send("Indoor humidity is ${ecobeeHumidity}% and close to target humidity, setting the HVAC's fan to auto")
				}
				ecobee.fanAuto() // set fan to auto
			}
		}            
        
	}
	if ((indoorVOCSensor) && ((indoorVOC) && (indoorVOC.toFloat() > (threshVOC.toFloat()))) || (((indoorCarbonDioxide) && (indoorCarbonDioxide.toFloat() > threshCarbonDioxide.toFloat()))) || ((indoorRadon) && (indoorRadon.toFloat() > (threshRadon.toFloat()))))   {
		if (detailedNotif) {
			if (indoorVOC) log.trace("indoor VOC ${indoorVOC} is above the target VOC of ${threshVOC}, triggering the HVAC fan and $dehumidifySwitches switch(es) as requested")
			if (indoorCarbonDioxide) log.trace("or indoor CarbonDioxide ${indoorCarbonDioxide} is above the target CarbonDioxide level of ${threshCarbonDioxide}, triggering the dehumidifier, HVAC fan and $dehumidifySwitches switch(es) as requested")
			if (indoorRadon) log.trace("or indoor Radon ${indoorRadon} is above the target Radon level of ${threshRadon}, triggering the dehumidifier, HVAC fan and $dehumidifySwitches switch(es) as requested")
			send("Indoor quality air is poor, triggering the dehumidifier, HVAC fan and $dehumidifySwitches switch(es) as requested")
		}
		ecobee.fanOn() // set fan on
		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': "${target_humidity}", 'humidifierMode': 'off',
			'dehumidifyWithAC': 'false'
			])
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("Indoor Air quality is poor, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            
	}            
    
	if (useDehumidifierAsHRVFlag) {
		use_dehumidifer_as_HRV()    
	} // end if useDehumidifierAsHRVFlag '

	log.debug "End of Fcn"
}

private void use_dehumidifer_as_HRV() {
	int scheduleInterval = givenInterval ?: 59 // By default, do it every hour
	Date now = new Date()
	String nowInLocalTime = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
	Calendar oneHourAgoCal = new GregorianCalendar()
	oneHourAgoCal.add(Calendar.HOUR, -1)
	Date oneHourAgo = oneHourAgoCal.getTime()
	if (detailedNotif) {
		log.debug("local date/time= ${nowInLocalTime}, date/time now in UTC = ${String.format('%tF %<tT',now)}," +
			"oneHourAgo's date/time in UTC= ${String.format('%tF %<tT',oneHourAgo)}")
	}


	// Get the dehumidifier's runtime 
	ecobee.getReportData("", oneHourAgo, now, null, null, "dehumidifier", 'false', 'true')
	ecobee.generateReportRuntimeEvents("dehumidifier", oneHourAgo, now, 0, null, 'lastHour')
	def dehumidifierRunInMinString=ecobee.currentDehumidifierRuntimeInPeriod    
	float dehumidifierRunInMin = (dehumidifierRunInMinString)? dehumidifierRunInMinString.toFloat().round():0
	int min_vent_time = (givenVentMinTime!=null) ? givenVentMinTime : 20 //  20 min. ventilator time per hour by default
	int min_fan_time = (givenFanMinTime!=null) ? givenFanMinTime : 20 //  20 min. fan time per hour by default
	if (detailedNotif) {
		log.debug "use_dehumidifer_as_HRV>dehumidifierRunInMin=$dehumidifierRunInMin, min_vent_time=$min_vent_time"    
	}        
	float diffVentTimeInMin = min_vent_time - (dehumidifierRunInMin as Float)
	def equipStatus = ecobee.currentEquipmentStatus
	if (detailedNotif) {
		send ("dehumidifier runtime in the last hour is ${dehumidifierRunInMin.toString()} min. vs. desired ventilatorMinOnTime =${min_vent_time.toString()} minutes",
			askAlexaFlag)        
		log.debug "equipStatus = $equipStatus"
	}
	if (equipStatus.contains("dehumidifier")) {
		if (detailedNotif) {
			log.trace("dehumidifier should be running (${equipStatus}), time left to run = ${diffVentTimeInMin.toString()} min. within the current cycle")
			send ("dehumidifier (used as HRV) already running,time left to run = ${diffVentTimeInMin.toString()} min.", askAlexaFlag)
		}
	}

	if ((diffVentTimeInMin > 0) && (!equipStatus.contains("dehumidifier"))) {
		if (detailedNotif) {
			send ("About to turn the dehumidifier on for ${diffVentTimeInMin.toString()} min. within the next hour...", askAlexaFlag)
		}

		ecobee.setThermostatSettings("", ['dehumidifierMode': 'on', 'dehumidifierLevel': '25',
				'fanMinOnTime': "${min_fan_time}"
			])
		// calculate the delay to turn off the dehumidifier according to the scheduled monitoring cycle
		if (dehumidifySwitches) {
			if (detailedNotif) {    
				log.trace("use dehumidifier as HRV, turning on all dehumidify/fan switches")
			}
			dehumidifySwitches.on()        
		}            
  
		float delay = ((min_vent_time.toFloat() / 60) * scheduleInterval.toFloat()).round()
		int delayInt = delay.toInteger()
		delayInt = (delayInt > 1) ? delayInt : 1 // Min. delay should be at least 1 minute, otherwise, the dehumidifier won't stop.
		send ("turning off the dehumidifier (used as HRV) in ${delayInt} minute(s)...", askAlexaFlag)
			// save the current setpoints before scheduling the dehumidifier to be turned off
		runIn((delayInt * 60), "turn_off_dehumidifier") // turn off the dehumidifier after delay
	} else if (diffVentTimeInMin <= 0) {
		if (detailedNotif) {
			send ("dehumidifier has run for at least ${min_vent_time} min. within the last hour, waiting for the next cycle", askAlexaFlag)
			log.trace("dehumidifier has run for at least ${min_vent_time} min. within the last hour, waiting for the next cycle")
		}

	} else if (equipStatus.contains("dehumidifier")) {
		turn_off_dehumidifier()
	}
}


private void turn_off_dehumidifier() {


	if (detailedNotif) {
		send("about to turn off dehumidifier used as HRV and $dehumidifySwitches switch(es)....", askAlexaFlag)
	}
	log.trace("About to turn off the dehumidifier used as HRV and the fan after timeout")


	ecobee.setThermostatSettings("", ['dehumidifierMode': 'off'])
	if (dehumidifySwitches) {
		if (detailedNotif) {    
			log.trace("use dehumidifier as HRV, turning off all dehumidify/fan switches")
		}
		dehumidifySwitches.off()        
	}            
 
}


private def bolton(t) {

	//  Estimates the saturation vapour pressure in hPa at a given temperature,  T, in Celcius
	//  return saturation vapour pressure at a given temperature in Celcius


	Double es = 6.112 * Math.exp(17.67 * t / (t + 243.5))
	return es

}


private def calculate_corr_humidity(t1, rh1, t2) {


	log.debug("calculate_corr_humidity t1= $t1, rh1=$rh1, t2=$t2")

	Double es = bolton(t1)
	Double es2 = bolton(t2)
	Double vapor = rh1 / 100.0 * es
	Double rh2 = ((vapor / es2) * 100.0).round(2)

	log.debug("calculate_corr_humidity rh2= $rh2")

	return rh2
}


private send(String msg, askAlexa=false) {
	int MAX_EXCEPTION_MSG_SEND=5

	// will not send exception msg when the maximum number of send notifications has been reached
	if (msg.contains("exception")) {
		atomicState?.sendExceptionCount=atomicState?.sendExceptionCount+1         
		log.debug ("checking sendExceptionCount=${atomicState?.sendExceptionCount} vs. max=${MAX_EXCEPTION_MSG_SEND}")
		if (atomicState?.sendExceptionCount >= MAX_EXCEPTION_MSG_SEND) {
			log.warn ("send>reached $MAX_EXCEPTION_MSG_SEND exceptions, exiting")
			return        
		}        
	}    
	def message = "${get_APP_NAME()}>${msg}"


	if (sendPushMessage == "Yes") {
		sendPush(message)
	}
	if (askAlexa) {
		def expiresInDays=(AskAlexaExpiresInDays)?:2    
		sendLocationEvent(
			name: "AskAlexaMsgQueue", 
			value: "${get_APP_NAME()}", 
			isStateChange: true, 
			descriptionText: msg, 
			data:[
				queues: listOfMQs,
		        expires: (expiresInDays*24*60*60)  /* Expires after 2 days by default */
		    ]
		)
	} /* End if Ask Alexa notifications*/
	
	if (phoneNumber) {
		sendSms(phoneNumber, message)
	}
}



private int find_ideal_indoor_humidity(outsideTemp) {

	// -30C => 30%, at 0C => 45%

	int targetHum = 45 + (0.5 * outsideTemp)
	return (Math.max(Math.min(targetHum, 60), 30))
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

def getImagePath() {
	return "https://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/"
} 

def get_APP_NAME() {
	return "MonitorAndSetEcobeeHumidity"
} 
