/**
 *  ecobeeSetZonesWithSchedule
 *
 *  Copyright 2015 Yves Racine
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
 */
definition(
	name: "ecobeeSetZoneWithSchedule",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Enables Heating/Cooling Zoned Solutions based on your ecobee schedule(s)- coupled with smart vents (optional) for better temp settings control throughout your home",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)



preferences {

	page(name: "generalSetupPage")
	page(name: "roomsSetupPage")
	page(name: "zonesSetupPage")
	page(name: "schedulesSetupPage")
	page(name: "NotificationsPage")
	page(name: "roomsSetup")
	page(name: "zonesSetup")
	page(name: "schedulesSetup")
}

def generalSetupPage() {

	dynamicPage(name: "generalSetupPage", uninstall: true, nextPage: roomsSetupPage) {
		section("About") {
			paragraph "ecobeeSetZoneWithSchedule, the smartapp that enables Heating/Cooling Zoned Solutions based on your ecobee schedule(s)- coupled with smart vents (optional) for better temp settings control throughout your home"
			paragraph "Version 5.2" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
					title:"Paypal donation..."
			paragraph "Copyright©2015 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
 		}
		section("Main ecobee thermostat at home (used for temp/vent adjustment)") {
			input (name:"thermostat", type: "capability.thermostat", title: "Which main ecobee thermostat?")
		}
		section("Rooms count") {
			input (name:"roomsCount", title: "Rooms count (max=16)?", type: "number",refreshAfterSelection: true)
		}
		section("Zones count") {
			input (name:"zonesCount", title: "Zones count (max=8)?", type:"number",refreshAfterSelection: true)
		}
		section("Schedules count") {
			input (name:"schedulesCount", title: "Schedules count (max=12)?", type: "number",refreshAfterSelection: true)
		}
		section("Set your main thermostat to [Away,Present] based on all Room Motion Sensors [default=false] ") {
			input (name:"setAwayOrPresentFlag", title: "Set Main thermostat to [Away,Present]?", type:"bool",
				description:"optional",required:false)
		}
		section("Outdoor temp Sensor used for adjustment [optional]") {
			input (name:"outTempSensor", type:"capability.temperatureMeasurement", required: false,
					description:"Optional")				            
		}
		section("Enable vent settings [optional, default=false]") {
			input (name:"setVentSettingsFlag", title: "Set Vent Settings?", type:"bool",
				description:"optional",required:false)
		}
		section("Enable temp adjustment at main thermostat based on outdoor temp sensor [optional, default=false]") {
			input (name:"setAdjustmentOutdoorTempFlag", title: "Enable temp adjustment set in schedules based on outdoor sensor?", type:"bool",
				description:"optional",required:false)
		}
		section("Enable temp adjustment at main thermostat based on indoor temp/motion sensor(s) [optional, default=false]") {
			input (name:"setAdjustmentTempFlag", title: "Enable temp adjustment based on avg temp collected at indoor sensor(s)?", type:"bool",
				description:"optional",required:false)
		}
		section("Enable fan adjustment at main thermostat based on indoor/outdoor temp sensors [optional, default=false]") {
			input (name:"setAdjustmentFanFlag", title: "Enable fan adjustment set in schedules based on sensors?", type:"bool",
				description:"optional",required:false)
		}
		section("What do I use for the Master on/off switch to enable/disable smartapp processing? [optional]") {
			input (name:"powerSwitch", type:"capability.switch", required: false, description: "Optional")
		}
		if (thermostat) {
			section {
				href(name: "toRoomPage", title: "Room Setup", page: "roomsSetupPage")
				href(name: "toZonePage", title: "Zone Setup", page: "zonesSetupPage")
				href(name: "toSchedulePage", title: "Schedule Setup", page: "schedulesSetupPage")
				href(name: "toNotificationsPage", title: "Notifications Setup", page: "NotificationsPage")
			}                
		}
	}
}



def roomsSetupPage() {

	dynamicPage(name: "roomsSetup", title: "Rooms Setup", uninstall: true, nextPage: zonesSetupPage) {

		for (int indiceRoom = 1;
			((indiceRoom <= settings.roomsCount) && (indiceRoom <= 16)); indiceRoom++) {
            			section("Room ${indiceRoom} Setup") {
				input "roomName${indiceRoom}", title: "Room Name", "string"
			}
			section("Room ${indiceRoom}-Thermostat [optional]") {
				input "roomTstat${indiceRoom}", title: "Zone or Room thermostat to be set", "capability.thermostat", 
	          			required: false, description: "Optional"

			}
			section("Room ${indiceRoom}-TempSensor [optional]") {
				input "tempSensor${indiceRoom}", title: "Temp sensor for better temp adjustment", "capability.temperatureMeasurement", 
                			required: false, description: "Optional"

			}
			section("Room ${indiceRoom}-Vents Setup [optional]")  {
				for (int j = 1;(j <= 5); j++)  {
					input "ventSwitch${j}${indiceRoom}", title: "Vent switch no ${j} in room", "capability.switch", 
                    			required: false, description: "Optional"
				}           
			}           
			section("Room ${indiceRoom}-MotionSensor [optional]") {
				input "motionSensor${indiceRoom}", title: "Motion sensor (if any) to detect if room is occupied", "capability.motionSensor", 
                			required: false, description: "Optional"

			}
			section("Room ${indiceRoom}-Do temp/vent adjustment when occupied room only [optional]") {
				input "needOccupiedFlag${indiceRoom}", title: "Will do temp/vent adjustement only when Occupied [default=false]", "bool",
                			required: false, description: "Optional"

			}
			section("Room ${indiceRoom}-Do temp/vent adjustment with this occupied's threshold [optional]") {
				input "residentsQuietThreshold${indiceRoom}", title: "Threshold in minutes for motion detection [default=15 min]", "number", 
               				required: false, description: "Optional"

			}
			section() {
				paragraph "**** DONE FOR ROOM ${indiceRoom} **** "

			}                
		} /* end for */
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}

	}

}



def zoneHrefDescription(i) {
	def description ="Zone no ${i} "

	if (settings."zoneName${i}" !=null) {
		description += settings."zoneName${i}"		    	
	}
	return description
}

def zonePageState(i) {

	if (settings."zoneName${i}" != null) {
		return 'complete'
	} else {
		return 'incomplete'
	}
  
}

def zoneHrefTitle(i) {
	def title = "Zone ${i}"
	return title
}

def zonesSetupPage() {

	dynamicPage(name: "zonesSetupPage", title: "Zones Setup", nextPage: schedulesSetupPage) {
		section("Zones") {
			for (int i = 1; i <= settings.zonesCount; i++) {
				href(name: "toZonePage$i", page: "zonesSetup", params: [indiceZone: i], required:false, description: zoneHrefDescription(i), title: zoneHrefTitle(i), state: zonePageState(i) )
			}
		}            
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
	}
}        

def zonesSetup(params) {

	def rooms = []
	for (i in 1..settings.roomsCount) {
		def key = "roomName$i"
		def room = "${i}:${settings[key]}"
		rooms = rooms + room
	}
	log.debug "rooms: $rooms"

	def indiceZone=0   

	// Assign params to indiceZone.  Sometimes parameters are double nested.
	if (params?.indiceZone || params?.params?.indiceZone) {

		if (params.indiceZone) {
			indiceZone = params.indiceZone
		} else {
			indiceZone = params.params.indiceZone
		}
	}    
	indiceZone=indiceZone.intValue()
	log.debug "zonesSetup> indiceZone=${indiceZone}"
	dynamicPage(name: "zonesSetup", title: "Zones Setup") {
		section("Zone ${indiceZone} Setup") {
			input (name:"zoneName${indiceZone}", title: "Zone Name", type: "text",
				defaultValue:settings."zoneName${indiceZone}")
		}
		section("Zone ${indiceZone}-Included rooms") {
			input (name:"includedRooms${indiceZone}", title: "Rooms included in the zone", type: "enum",
				options: rooms,
				multiple: true,
				defaultValue:settings."includedRooms${indiceZone}")
		}
		section {
			href(name: "toZonesSetupPage", title: "Back to Zones Setup Page", page: "zonesSetupPage")
		}
	}            
}

def scheduleHrefDescription(i) {
	def description ="Schedule no ${i} " 
	if (settings."scheduleName${i}" !=null) {
		description += settings."scheduleName${i}"		    
    }
	return description
}

def schedulePageState(i) {

	if (settings."scheduleName${i}"  != null) {		    
		return 'complete'
	} else {
		return 'incomplete'
	}	
    
}

def scheduleHrefTitle(i) {
	def title = "Schedule ${i}"
	return title
}

def schedulesSetupPage() {
	dynamicPage(name: "schedulesSetupPage", title: "Schedule Setup", nextPage: NotificationsPage) {
		section("Schedules") {
			for (int i = 1; i <= settings.schedulesCount; i++) {
				href(name: "toSchedulePage$i", page: "schedulesSetup", params: [indiceSchedule: i],required:false, description: scheduleHrefDescription(i), title: scheduleHrefTitle(i), state: schedulePageState(i) )
			}
		}            
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
	}
}        

def schedulesSetup(params) {
    
	def ecobeePrograms=[]
	// try to get the thermostat programs list (ecobee)
	try {
		ecobeePrograms = thermostat?.currentClimateList.toString().minus('[').minus(']').tokenize(',')
		ecobeePrograms.sort()        
	} catch (e) {
		log.debug("Not able to get the list of climates (ecobee), exception $e")    	
	}    
    
    
	log.debug "programs: $ecobeePrograms"

	def zones = []
    
	for (i in 1..settings.zonesCount) {
		def key = "zoneName$i"
		def zoneName =  "${i}:${settings[key]}"   
		zones = zones + zoneName
	}
	log.debug "zones: $zones"

	
	def enumModes=[]
	location.modes.each {
		enumModes << it.name
	}    
    
	def indiceSchedule=1
	// Assign params to indiceSchedule.  Sometimes parameters are double nested.
	if (params?.indiceSchedule || params?.params?.indiceSchedule) {

		if (params.indiceSchedule) {
			indiceSchedule = params.indiceSchedule
		} else {
			indiceSchedule = params.params.indiceSchedule
		}
	}    
	indiceSchedule=indiceSchedule.intValue()
	log.debug "scheduleSetup> indiceSchedule=${indiceSchedule}"

	dynamicPage(name: "schedulesSetup", title: "Schedule Setup", submitOnChange: true) {
		section("Schedule ${indiceSchedule} Setup") {
			input (name:"scheduleName${indiceSchedule}", title: "Schedule Name", type: "text",
				defaultValue:settings."scheduleName${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Select the climate/program scheduled at ecobee thermostat to be set for the included zone(s)") {
			input (name:"givenClimate${indiceSchedule}", type:"enum", title: "Which ecobee climate/program? ", options: ecobeePrograms,  
				defaultValue:settings."givenClimate${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Included zones") {
			input (name:"includedZones${indiceSchedule}", title: "Zones included in this schedule", type: "enum",
				defaultValue:settings."includedZones${indiceSchedule}",
				options: zones,
 				multiple: true)
		}
		section("Schedule ${indiceSchedule}-More or Less Heat/Cool Threshold in the selected zone(s) based on outdoor temp Sensor [optional]") {
			input (name:"moreHeatThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for more heating", required: false,
				defaultValue:settings."moreHeatThreshold${indiceSchedule}", description: "[default <= 10°F/-17°C]")			                
			input (name:"moreCoolThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for more cooling",required: false,
				,defaultValue:settings."moreCoolThreshold${indiceSchedule}", description: "[default >= 85°F/30°C]")
			input (name:"lessHeatThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for less heating", required: false,
				defaultValue:settings."lessHeatThreshold${indiceSchedule}", description: "[default >= 50°F/10°C]")			                
			input (name:"lessCoolThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for less cooling",required: false,
				,defaultValue:settings."lessCoolThreshold${indiceSchedule}", description: "[default <= 75°F/22°C]")
		}
		section("Schedule ${indiceSchedule}-Max Temp Adjustment/Differential Allowed for the zone(s)") {
			input (name:"givenMaxTempDiff${indiceSchedule}", type:"decimal",  title: "Max Temp adjustment/Differential", required: false,
				defaultValue:settings."givenMaxTempDiff${indiceSchedule}", description: " [default= +/-5°F/2°C]")
		}
		section("Schedule ${indiceSchedule}-Override Fan settings at ecobee based on indoor/outdoor sensors [optional]") {
			input (name:"fanMode${indiceSchedule}", type:"enum", title: "Set Fan Mode ['on', 'auto']", metadata: [values: ["on", "auto"]], 
				required: false, defaultValue:settings."fanMode${indiceSchedule}", description: "Optional")
			input (name:"moreFanThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for Fan Mode", required: false,
				defaultValue:settings."moreFanThreshold${indiceSchedule}", description: "Optional")			                
			input (name:"fanModeForThresholdOnlyFlag${indiceSchedule}", type:"bool",  title: "Override Fan Mode only when Threshold or Indoor Temp differential is reached(default=false)", 
				required: false, defaultValue:settings."fanModeForThresholdOnlyFlag${indiceSchedule}")
		}
/*		Y.R. Commented out as issue with Schedule page        
		section("Schedule ${indiceSchedule}-Minimum Fan Time during the Schedule [optional]") {
			input (name: "givenFanMinTime${indiceSchedule}", "number", title: "Minimum fan runtime for this schedule",
				required: false, defaultValue:settings."givenFanMinTime${indiceSchedule}", description: "Optional")
		}                        
*/

		section("Schedule ${indiceSchedule}-Set Zone/Room Thermostats Only Indicator [optional]") {
			input (name:"setRoomThermostatsOnlyFlag${indiceSchedule}", type:"bool", title: "Set room thermostats only [default=false,main & room thermostats setpoints are set]", 
				required: false, defaultValue:settings."setRoomThermostatsOnlyFlag${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Desired Heat/Cool Temp for Room Thermostats [optional]") {
			input (name:"desiredCoolTemp${indiceSchedule}", type:"decimal", title: "Cool Temp, default = 75°F/23°C", 
				required: false,defaultValue:settings."desiredCoolTemp${indiceSchedule}", description: "Optional")			                
			input (name:"desiredHeatTemp${indiceSchedule}", type:"decimal", title: "Heat Temp, default=72°F/21°C", 
				required: false, defaultValue:settings."desiredHeatTemp${indiceSchedule}", description: "Optional")			                
		}
		section("Schedule ${indiceSchedule}-Set for specific mode(s) [default=all]")  {
			input (name:"selectedMode${indiceSchedule}", type:"enum", title: "Choose Mode", options: enumModes, required: false, multiple:true,
				defaultValue:settings."selectedMode${indiceSchedule}")
		}
		section {
			href(name: "toSchedulesSetupPage", title: "Back to Schedules Setup Page", page: "schedulesSetupPage")
		}
	}        
}

def NotificationsPage() {
	dynamicPage(name: "NotificationsPage", title: "Other Options", install: true) {
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
			input "phone", "phone", title: "Send a Text Message?", required: false
		}
		section("Detailed Notifications") {
			input "detailedNotif", "bool", title: "Detailed Notifications?", required:false
		}
		section([mobileOnly: true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
		section {
			href(name: "toGeneralSetupPage", title: "Back to General Setup Page", page: "generalSetupPage")
		}
	}
}



def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	unschedule()
	initialize()
}

def offHandler(evt) {
	log.debug "$evt.name: $evt.value"
}

def onHandler(evt) {
	log.debug "$evt.name: $evt.value"
	setZoneSettings()
}

def motionEvtHandler(evt) {
	if (evt.value == "active") {
		log.debug "Motion at home..."

		if (state?.programHoldSet == 'Away') {
			check_if_hold_justified()
		}        
	}
}


def thermostatOperatingHandler(evt) {
	log.debug "Thermostat Operating now: $evt.value"
	state?.operatingState=evt.value    
	setZoneSettings()      
}


def ventTemperatureHandler(evt) {
	log.debug "vent temperature: $evt.value"
	float ventTemp = evt.value.toFloat()
	def scale = getTemperatureScale()
	def MAX_TEMP_VENT_SWITCH = (scale=='C')?49:121 //Max temperature inside a ventSwitch
	def MIN_TEMP_VENT_SWITCH = (scale=='C')?7:45 //Min temperature inside a ventSwitch
	String currentHVACMode = thermostat.currentThermostatMode.toString()
    
	if (((currentHVACMode=='heat') || (currentHVACMode == 'auto')) && (ventTemp >= MAX_TEMP_VENT_SWITCH)) {
		// Open all vents just to be safe
		open_all_vents()
		send("ecobeeSetZoneWithSchedule>current HVAC mode is ${currentHVACMode}, found one of the vents' value too hot (${evt.value}°), opening all vents to avoid any damage")
	} /* if too hot */           
	if (((currentHVACMode=='cool') || (currentHVACMode == 'auto')) && (ventTemp <= MIN_TEMP_VENT_SWITCH)) {
		// Open all vents just to be safe
		open_all_vents()
		send("ecobeeSetZoneWithSchedule>current HVAC mode is ${currentHVACMode}, found one of the vents' value too cold (${evt.value}°), opening all vents to avoid any damage")
	} /* if too cold */ 
}


def changeModeHandler(evt) {
	log.debug "Changed mode, $evt.name: $evt.value"
	thermostat.resumeProgram("")    
	setZoneSettings()  
}

def setClimateHandler(evt) {
	log.debug "SetClimate, $evt.name: $evt.value"
    
	setZoneSettings()  
    
}

def initialize() {

	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler, [filterEvents: false])
		subscribe(powerSwitch, "switch.on", onHandler, [filterEvents: false])
	}
	subscribe(thermostat, "climateName", setClimateHandler)
	subscribe(thermostat, "thermostatMode", changeModeHandler)
	subscribe(location, changeModeHandler)
	subscribe(thermostat, "thermostatOperatingState", thermostatOperatingHandler)

	// Initialize state variables
    
	state.lastScheduleLastName=""
	state.scheduleHeatSetpoint=0  
	state.scheduleCoolSetpoint=0    
	state.operatingState=""
	reset_state_program_values()  
	state?.exceptionCount=0	

	// Resume program every time an install/update is done to remote any holds at thermostat (reset).
    
	thermostat.resumeProgram("")
    
	subscribe(app, appTouch)
	def motionSensors =[]   	 

	// subscribe all vents to check their temperature on a regular basis
    
	for (indiceRoom in 1..roomsCount) {
		for (int j = 1;(j <= 5); j++)  {
			def key = "ventSwitch${j}$indiceRoom"
			def vent = settings[key]
				if (vent != null) {
					subscribe(vent, "temperature", ventTemperatureHandler)
				} /* end if vent != null */
		} /* end for vent switches */
	} /* end for rooms */

	for (int i = 1;
		((i <= settings.roomsCount) && (i <= 16)); i++) {
		def key = "motionSensor${i}"
		def motionSensor = settings[key]
        
		if (motionSensor) {
			motionSensors.add(motionSensor)    
		}            
	}        

	// associate the motionHandler to the list of motionSensors in rooms   	 
	subscribe(motionSensors, "motion", motionEvtHandler, [filterEvents: false])

	state?.poll = [ last: 0, rescheduled: now() ]


	Integer delay =5 				// wake up every 5 minutes to apply zone settings if any
	log.debug "initialize>scheduling setZoneSettings every ${delay} minutes to check for zone settings to be applied"

	//Subscribe to different events (ex. sunrise and sunset events) to trigger rescheduling if needed
	subscribe(location, "sunrise", rescheduleIfNeeded)
	subscribe(location, "sunset", rescheduleIfNeeded)
	subscribe(location, "mode", rescheduleIfNeeded)
	subscribe(location, "sunriseTime", rescheduleIfNeeded)
	subscribe(location, "sunsetTime", rescheduleIfNeeded)

	rescheduleIfNeeded()   
}



def rescheduleIfNeeded(evt) {
	if (evt) log.debug("rescheduleIfNeeded>$evt.name=$evt.value")
	Integer delay = 5 // By default, schedule SetZoneSettings() every 5 min.
	BigDecimal currentTime = now()    
	BigDecimal lastPollTime = (currentTime - (state?.poll["last"]?:0))  
	if (lastPollTime != currentTime) {    
		Double lastPollTimeInMinutes = (lastPollTime/60000).toDouble().round(1)      
		log.info "rescheduleIfNeeded>last poll was  ${lastPollTimeInMinutes.toString()} minutes ago"
	}
	if (((state?.poll["last"]?:0) + (delay * 60000) < currentTime) && canSchedule()) {
		log.info "rescheduleIfNeeded>scheduling takeAction in ${delay} minutes.."
		runEvery5Minutes(setZoneSettings)
	}
    
	setZoneSettings()
    
	// Update rescheduled state
    
	if (!evt) state.poll["rescheduled"] = now()
}




def appTouch(evt) {
	setZoneSettings()
}


def setZoneSettings() {
	log.debug "Begin of setZoneSettings Fcn"
	log.debug "setZoneSettings>setVentSettingsFlag=$setVentSettingsFlag,setAdjustmentTempFlag=$setAdjustmentTempFlag" +
		",setAdjustmentOutdoorTempFlag=$setAdjustmentOutdoorTempFlag,setAdjustmentFanFlag=$setAdjustmentFanFlag"
	Integer delay = 5 // By default, schedule SetZoneSettings() every 5 min.

	//schedule the rescheduleIfNeeded() function
	state?.poll["last"] = now()
    
	if (((state?.poll["rescheduled"]?:0) + (delay * 60000)) < now()) {
		log.info "setZoneSettings>scheduling rescheduleIfNeeded() in ${delay} minutes.."
		schedule("0 0/${delay} * * * ?", rescheduleIfNeeded)
		// Update rescheduled state
		state?.poll["rescheduled"] = now()
	}
    
	if (powerSwitch?.currentSwitch == "off") {
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>${powerSwitch.name} is off, schedule processing on hold...")
		}
		return
	}
	def MAX_EXCEPTION_COUNT=5
	def exceptionCheck, msg 
	try {        
		thermostat.poll()
		exceptionCheck= thermostat.currentVerboseTrace?.toString()
		if ((exceptionCheck.contains("exception") || (exceptionCheck.contains("error")) && 
			(!exceptionCheck.contains("Java.util.concurrent.TimeoutException")))) {  
			// check if there is any exception or an error reported in the verboseTrace associated to the device (except the ones linked to rate limiting).
			state?.exceptionCount=state.exceptionCount+1    
			log.error "setZoneSettings>found exception/error after polling, exceptionCount= ${state?.exceptionCount}: $exceptionCheck" 
		} else {             
			// reset exception counter            
			state?.exceptionCount=0       
		}                
	} catch (e) {
		state.exceptionCount=state.exceptionCount+1    
		log.error "setZoneSettings>exception $e while trying to poll the device $d, exceptionCount= ${state?.exceptionCount}" 
	}
	if ((state?.exceptionCount>=MAX_EXCEPTION_COUNT) || (exceptionCheck.contains("Unauthorized"))) {
		// need to authenticate again    
		msg="too many exceptions/errors or unauthorized exception, $exceptionCheck (${state?.exceptionCount} errors), need to re-authenticate at ecobee..." 
		send "ecobeeSetZoneWithSchedule> ${msg}"
		log.error msg
		return        
	}    
	if ((outTempSensor) && ((outTempSensor.hasCapability("Refresh")) || (outTempSensor.hasCapability("Polling")))) {

		// do a refresh to get latest temp value
		try {        
			outTempSensor.poll()
		} catch (e) {
			log.debug("setZoneSettings>not able to do a refresh() on $outTempSensor")
		}                    
	}
	def scheduleProgramName = thermostat.currentClimateName

	boolean foundSchedule=false
	boolean initialScheduleSetup=false        
	String nowInLocalTime = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
	def ventSwitchesOn = []

	def setVentSettings = (setVentSettingsFlag) ?: false
	def adjustmentTempFlag = (setAdjustmentTempFlag)?: false
	def adjustmentOutdoorTempFlag = (setAdjustmentOutdoorTempFlag)?: false
	def adjustmentFanFlag = (setAdjustmentFanFlag)?: false
    
	for (int i = 1;((i <= settings.schedulesCount) && (i <= 12)); i++) {
		def key = "scheduleName$i"
		def scheduleName = settings[key]
		log.debug "setZoneSettings>found schedule=${scheduleName}, scheduled program at ecobee=$scheduleProgramName..."
		key = "selectedMode$i"
		def selectedModes = settings[key]

		boolean foundMode=false        
		selectedModes.each {
        
			if (it==location.mode) {
				foundMode=true            
			}            
		}        
        
		if ((selectedModes != null) && (!foundMode)) {
        
			log.debug "setZoneSettings>schedule=${scheduleName} does not apply,location.mode= $location.mode, selectedModes=${selectedModes},foundMode=${foundMode}, continue"
			continue			
		}
		key = "givenClimate$i"
		def selectedClimate=settings[key]
		def ventSwitchesZoneSet = []        
		if ((selectedClimate==scheduleProgramName) && (scheduleName != state.lastScheduleName)) {
        
			// let's set the given zone(s) for this program schedule
            
			log.debug "setZoneSettings>now applying ${scheduleName}, scheduled ecobee program is now ${scheduleProgramName}"
			foundSchedule=true   
			initialScheduleSetup=true

			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>running schedule ${scheduleName},about to set zone settings as requested")
			}
			if (setVentSettings) {            
				// set the zoned vent switches to 'on' and adjust them according to the ambient temperature
                
/*				ventSwitchesZoneSet= control_vent_switches_in_zone(i)
*/
				ventSwitchesZoneSet= adjust_vent_settings_in_zone(i)
			}				
			if (adjustmentFanFlag) {
				set_fan_mode(i)
			}
			if (adjustmentTempFlag) {
				// adjust the temperature at the thermostat(s) based on avg temp calculated from indoor temp sensors if any
				adjust_thermostat_setpoint_in_zone(i)
			}                
			ventSwitchesOn = ventSwitchesOn + ventSwitchesZoneSet              
			state?.lastScheduleName = scheduleName
		} else if ((selectedClimate==scheduleProgramName) && (state?.lastScheduleName == scheduleName)) {
			// We're in the middle of a schedule run

			log.debug "setZoneSettings>${scheduleName} is running again, scheduled ecobee program is still ${scheduleProgramName}"
			foundSchedule=true   
			def setAwayOrPresent = (setAwayOrPresentFlag)?:false
			boolean isResidentPresent=true

			if (setAwayOrPresent) {
	            
				// Check if current Hold (if any) is justified
				check_if_hold_justified()
                
				isResidentPresent=verify_presence_based_on_motion_in_rooms()
				if (isResidentPresent) {            

					if (state?.programHoldSet != 'Home') {
						set_main_tstat_to_AwayOrPresent('present')
					}
				} else {
					if (state?.programHoldSet != 'Away') {
						set_main_tstat_to_AwayOrPresent('away')
					}                
				}
			}   
            
			if (adjustmentFanFlag) {
				// will override the fan settings if required (ex. more Fan Threshold is set)
				set_fan_mode(i)
			}
            
			if (isResidentPresent) {
				if (adjustmentTempFlag) {
					// adjust the temperature at the thermostat(s) based on avg temp calculated from indoor temp sensors if any
					adjust_thermostat_setpoint_in_zone(i) 
				}                    
				if (adjustmentOutdoorTempFlag) {
					// let's adjust the thermostat's temp & mode settings according to outdoor temperature
					adjust_tstat_for_more_less_heat_cool(i)
				}                    
            
			}        
			// Check the operating State before adjusting the vents again.
			String operatingState = thermostat.currentThermostatOperatingState           
			// let's adjust the vent settings according to desired Temp only if thermostat is not idle or was not idle at the last run
            
			if ((setVentSettings) && ((operatingState.toUpperCase() !='IDLE') ||
				((state?.operatingState.toUpperCase() =='HEATING') || (state?.operatingState.toUpperCase() =='COOLING'))))
			{            
				log.debug "setZoneSettings>thermostat ${thermostat}'s Operating State is ${operatingState} or was just recently " +
					"${state?.operatingState}, adjusting the vents for schedule ${scheduleName}"
				ventSwitchesZoneSet=adjust_vent_settings_in_zone(i)
				ventSwitchesOn = ventSwitchesOn + ventSwitchesZoneSet     
                
			}   
			state?.operatingState =operatingState            
		}

	} /* end for */ 	
    		
	if ((setVentSettings) && ((ventSwitchesOn !=[]) || (initialScheduleSetup))) {
		log.debug "setZoneSettings>list of Vents turned on= ${ventSwitchesOn}"
		turn_off_all_other_vents(ventSwitchesOn)
	}		    
	if (!foundSchedule) {
		if (detailedNotif) {
			send "ecobeeSetZoneWithSchedule>No schedule applicable at this time ${nowInLocalTime}"
		}
		log.debug "setZoneSettings>No schedule applicable at this time ${nowInLocalTime}"
	} 
	log.debug "End of setZoneSettings Fcn"
}

private def isRoomOccupied(sensor, indiceRoom) {
	def key = "residentsQuietThreshold$indiceRoom"
	def threshold = (settings[key]) ?: 15 // By default, the delay is 15 minutes 

	key = "roomName$indiceRoom"
	def roomName = settings[key]

	def result = false
	def t0 = new Date(now() - (threshold * 60 * 1000))
	def recentStates = sensor.statesSince("motion", t0)
	if (recentStates.find {it.value == "active"}) {
		log.debug "isRoomOccupied>room ${roomName} has been occupied, motion was detected at sensor ${sensor} in the last ${threshold} minutes"
		result = true
	}
	return result
}

private def verify_presence_based_on_motion_in_rooms() {

	def result=false
	for (i in 1..roomsCount) {

		def key = "roomName$i"
		def roomName = settings[key]
		key = "motionSensor$i"
		def motionSensor = settings[key]
		if (motionSensor != null) {

			if (isRoomOccupied(motionSensor,i)) {
				log.debug("verify_presence_based_on_motion>in ${roomName},presence detected, return true")
				return true                
			}                
		}
	} /* end for */        
	return result
}

private void reset_state_program_values() {

 	state.programSetTime = null
 	state.programSetTimestamp = ""
 	state.programHoldSet = ""

}


private def set_main_tstat_to_AwayOrPresent(mode) {

	String currentProgName = thermostat.currentClimateName
	String currentSetClimate = thermostat.currentSetClimate
	String currentProgType = thermostat.currentProgramType
    
	if (currentProgType.toUpperCase()=='VACATION') {
		log.debug("set_tstat_to_AwayOrPresent>not setting the thermostat ${thermostat} to ${mode} mode;the current program type is ${currentProgType}")
		return    
	}    
    
	if (currentProgName.toUpperCase().contains('SLEEP'))  {
		log.debug("set_tstat_to_AwayOrPresent>not setting the thermostat ${thermostat} to ${mode} mode;the default program mode is ${currentProgName}")
		return    
	}
	if ((mode == 'away') && (currentProgName.toUpperCase().contains('AWAY')) ||
		((mode == 'present') && (!currentProgName.toUpperCase().contains('AWAY')))) {
		log.debug("set_tstat_to_AwayOrPresent>not setting the thermostat ${thermostat} to ${mode} mode;the default program mode is ${currentProgName}")
		return    
	}    
    
	if (((state?.programHoldSet == 'Away') && (currentSetClimate.toUpperCase() == 'AWAY'))  ||
		((state?.programHoldSet == 'Home') && (currentSetClimate.toUpperCase() == 'HOME'))) {
    
		log.debug("set_tstat_to_AwayOrPresent>not setting the thermostat ${thermostat} to ${mode} mode; ${currentSetClimate} 'Hold' already set")
		return    
 	}    
    
	try {
		if  (mode == 'away') {
        
			thermostat.away()
		} else if (mode == 'present') {	
			thermostat.present()
		}
		send("ecobeeSetZoneWithSchedule>set main thermostat ${thermostat} to ${mode} mode based on motion in all rooms")
		state?.programHoldSet=(mode=='present')?'Home': 'Away'    // set a state for further checking later
 		state?.programSetTime = now()
 		state?.programSetTimestamp = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
	}    
	catch (e) {
		log.error("set_tstat_to_AwayOrPresent>not able to set thermostat ${thermostat} to ${mode} mode (exception $e)")
	}

}

private void check_if_hold_justified() {
	log.debug "Begin of Fcn check_if_hold_justified"

	String currentProgName = thermostat.currentClimateName
	String currentSetClimate = thermostat.currentSetClimate

	String ecobeeMode = thermostat.currentThermostatMode.toString()
	log.trace "check_if_hold_justified> location.mode = $location.mode"
	log.trace "check_if_hold_justified> ecobee Mode = $ecobeeMode"
	log.trace "check_if_hold_justified> currentProgName = $currentProgName"
	log.trace "check_if_hold_justified> currentSetClimate = $currentSetClimate"
	log.trace "check_if_hold_justified>state=${state}"
	
	boolean residentPresent= verify_presence_based_on_motion_in_rooms()   
	if ((currentSetClimate.toUpperCase()=='AWAY')  && (residentPresent)) {
		if ((state?.programHoldSet == 'Away') && (!currentProgName.toUpperCase().contains('AWAY'))) {       
			log.trace("check_if_hold_justified>it's not been quiet since ${state.programSetTimestamp},resume ecobee program...")
			thermostat.resumeProgram("")
			reset_state_program_values()
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>resumed program to ecobee ${currentProgName} schedule as motion has been detected")
			}
		}  else if (state?.programHoldSet == 'Away') {	/* Climate was changed since the last climate set, just reset state program values */
			reset_state_program_values()
		}
	} else if ((currentSetClimate.toUpperCase()=='AWAY') && (!residentPresent)) {
		if ((state?.programHoldSet == 'Away') && ((currentProgName.toUpperCase().contains('AWAY')) ||
			(currentProgName.toUpperCase().contains('SLEEP')))) {       
			thermostat.resumeProgram("")
			reset_state_program_values()
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>'Away' hold no longer needed, resumed program to ecobee ${currentProgName} schedule")
			}
                
		} else if (state?.programHoldSet == 'Away') {
			log.trace("check_if_hold_justified>quiet since ${state.programSetTimestamp}, current ecobee schedule= ${currentProgName},'Away' hold justified")
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>quiet since ${state.programSetTimestamp}, current ecobee schedule= ${currentProgName}, 'Away' hold justified")
			}    
			thermostat.away()
		}    
	}
	if ((currentSetClimate.toUpperCase()=='HOME') && (!residentPresent)) {
		if ((state?.programHoldSet == 'Home')  && (currentProgName.toUpperCase().contains('AWAY'))) {       
			log.trace("check_if_hold_justified>it's been quiet since ${state.programSetTimestamp},resume program...")
			thermostat.resumeProgram("")
			reset_state_program_values()
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>'Home' hold no longer needed, resumed ecobee program to ${currentProgName} schedule, no motion detected")
			}                
		} else if (state?.programHoldSet == 'Home') {	/* Climate was changed since the last climate set, just reset state program values */
			reset_state_program_values()
		}
	} else if ((currentSetClimate.toUpperCase()=='HOME') && (residentPresent)) { 
		if ((state?.programHoldSet == 'Home')  && (!currentProgName.toUpperCase().contains('AWAY'))) {       
			thermostat.resumeProgram("")
			reset_state_program_values()
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>'Home' hold no longer needed, resumed ecobee program to ${currentProgName} schedule as motion has been detected")
			}
		} else if (state?.programHoldSet == 'Home') {
			log.trace("check_if_hold_justified>not quiet since ${state.programSetTimestamp}, current ecobee schedule= ${currentProgName}, 'Home' hold justified")
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>not quiet since ${state.programSetTimestamp}, current ecobee schedule= ${currentProgName}, 'Home' hold justified")
			}
			thermostat.present()
		}
	}   
    
	def adjustmentOutdoorTempFlag = (setAdjustmentOutdoorTempFlag)?: false
	if ((outTempSensor == null) || (!adjustmentOutdoorTempFlag)) {
		return    
	}            


	def key = "moreHeatThreshold$indiceSchedule"
	def moreHeatThreshold = settings[key]
	key = "moreCoolThreshold$indiceSchedule"
	def moreCoolThreshold = settings[key]
	key = "lessHeatThreshold$indiceSchedule"
	def lessHeatThreshold = settings[key]
	key = "lessCoolThreshold$indiceSchedule"
	def lessCoolThreshold = settings[key]
	
	def scale = getTemperatureScale()
	float more_heat_threshold, more_cool_threshold
	float less_heat_threshold, less_cool_threshold

	key = "givenMaxTempDiff$indiceSchedule"
	def givenMaxTempDiff = settings[key]
	def input_max_temp_diff = givenMaxTempDiff ?: (scale=='C')? 2: 5 // 2°C/5°F temp differential is applied by default

	float max_temp_diff = input_max_temp_diff.toFloat().round(1)
    
	if (scale == 'C') {
		more_heat_threshold = (moreHeatThreshold) ?(moreHeatThreshold.toFloat().round(1)): (-17) // by default, -17°C is the outdoor temp's threshold for more heating
		more_cool_threshold = (moreCoolThreshold) ?(moreCoolThreshold.toFloat().round(1)): 30 // by default, 30°C is the outdoor temp's threshold for more cooling
		less_heat_threshold = (lessHeatThreshold) ?(lessHeatThreshold.toFloat().round(1)): 10 // by default, 10°C is the outdoor temp's threshold for less heating
		less_cool_threshold = (lessCoolThreshold) ?(lessCoolThreshold.toFloat().round(1)): 22 // by default, 22°C is the outdoor temp's threshold for less cooling

	} else {
		more_heat_threshold = (moreHeatThreshold) ?(moreHeatThreshold.toFloat().round(1)): 10 // by default, 10°F is the outdoor temp's threshold for more heating
		more_cool_threshold = (moreCoolThreshold) ?(moreCoolThreshold.toFloat().round(1)): 85 // by default, 85°F is the outdoor temp's threshold for more cooling
		less_heat_threshold = (lessHeatThreshold) ?(lessHeatThreshold.toFloat().round(1)): 50 // by default, 50°F is the outdoor temp's threshold for less heating
		less_cool_threshold = (lessCoolThreshold) ?(lessCoolThreshold.toFloat().round(1)): 75 // by default, 75°F is the outdoor temp's threshold for less cooling
	}

	float heatTemp = thermostat.currentHeatingSetpoint.toFloat()
	float coolTemp = thermostat.currentCoolingSetpoint.toFloat()
	float programHeatTemp = thermostat.currentProgramHeatTemp.toFloat()
	float programCoolTemp = thermostat.currentProgramCoolTemp.toFloat()
	float ecobeeTemp = thermostat.currentTemperature.toFloat()
	float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)

	if (ecobeeMode == 'cool') {
		log.trace("check_if_hold_justified>evaluate: moreCoolThreshold=${more_cool_threshold} vs. outdoorTemp ${outdoorTemp}°")
		log.trace("check_if_hold_justified>evaluate: lessCoolThreshold= ${less_cool_threshold} vs.outdoorTemp ${outdoorTemp}°")
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>eval:  moreCoolThreshold ${more_cool_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			send("ecobeeSetZoneWithSchedule>eval:  lessCoolThreshold ${less_cool_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		}
		if ((outdoorTemp > less_cool_threshold) && (outdoorTemp < more_cool_threshold)) {
			send("ecobeeSetZoneWithSchedule>resuming program, ${less_cool_threshold}° < outdoorTemp <${more_cool_threshold}°")
			thermostat.resumeProgram("")
		} else {
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>Hold justified, cooling setPoint=${coolTemp}°")
			}
			float actual_temp_diff = (programCoolTemp - coolTemp).round(1).abs()
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>eval: actual_temp_diff ${actual_temp_diff}° between program cooling setpoint & hold setpoint vs. max temp diff ${max_temp_diff}°")
			}
			if ((actual_temp_diff > max_temp_diff) && (!state?.programHoldSet)) {
				if (detailedNotif) {
					send("ecobeeSetZoneWithSchedule>Hold differential too big (${actual_temp_diff}), needs adjustment")
				}
				thermostat.resumeProgram("")
			}
		}
	} else if (ecobeeMode == 'heat') {
		log.trace("check_if_hold_justified>eval: moreHeatingThreshold ${more_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		log.trace("check_if_hold_justified>eval:lessHeatThreshold=${less_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		log.trace(
			"check_if_hold_justified>evaluate: programHeatTemp= ${programHeatTemp}° vs.avgIndoorTemp= ${avg_indoor_temp}°")
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>eval: moreHeatThreshold ${more_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
			send("ecobeeSetZoneWithSchedule>eval: lessHeatThreshold ${less_heat_threshold}° vs.outdoorTemp ${outdoorTemp}°")
		}
		if ((outdoorTemp > more_heat_threshold) && (outdoorTemp < less_heat_threshold)) { 
			send("ecobeeSetZoneWithSchedule>resuming program, ${less_heat_threshold}° < outdoorTemp > ${more_heat_threshold}°")
			thermostat.resumeProgram("")
		} else {
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>Hold justified, heating setPoint=${heatTemp}°")
			}
			float actual_temp_diff = (heatTemp - programHeatTemp).round(1).abs()
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>eval: actual_temp_diff ${actual_temp_diff}° between program heating setpoint & hold setpoint vs. max temp diff ${max_temp_diff}°")
			}
			if ((actual_temp_diff > max_temp_diff) && (!state?.programHoldSet)) {
				if (detailedNotif) {
					send("ecobeeSetZoneWithSchedule>Hold differential too big ${actual_temp_diff}, needs adjustment")
				}
				thermostat.resumeProgram("")
			}
		}
	}
	log.debug "End of Fcn check_if_hold_justified"
}


private def getSensorTempForAverage(indiceRoom, typeSensor='tempSensor') {
	def key 
	def currentTemp=null
    	    
	if (typeSensor == 'tempSensor') {
		key = "tempSensor$indiceRoom"
	} else {
		key = "roomTstat$indiceRoom"
	}
	def tempSensor = settings[key]
	if (tempSensor != null) {
		log.debug("getTempSensorForAverage>found sensor ${tempSensor}")
		if ((tempSensor.hasCapability("Polling")) || (tempSensor.hasCapability("Refresh")) ) {
			// do a refresh to get the latest temp value
			try {        
				tempSensor.poll()
			} catch (e) {
				log.debug("getSensorTempForAverage>not able to do a refresh() on $tempSensor")
			}                
		}        
		currentTemp = tempSensor.currentTemperature.toFloat().round(1)
	}
	return currentTemp
}

private def setRoomTstatSettings(indiceSchedule,indiceZone, indiceRoom) {

	def scale = getTemperatureScale()
	float desiredHeat, desiredCool
	boolean setClimate = false
	def key = "zoneName$indiceZone"
	def zoneName = settings[key]

	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "givenClimate$indiceSchedule"
	def climateName = settings[key]

	key = "roomTstat$indiceRoom"
	def roomTstat = settings[key]

	key = "roomName$indiceRoom"
	def roomName = settings[key]

	log.debug("ecobeeSetZoneWithSchedule>>schedule ${scheduleName}, in room ${roomName},about to apply zone's temp settings at ${roomTstat}")
	String mode = thermostat?.currentThermostatMode.toString() // get the mode at the main thermostat
	if (mode == 'heat') {
		roomTstat.heat()
		if ((climateName) && (roomTstat?.hasCommand("setClimate"))) {
			try {
				roomTstat?.setClimate("", climateName)
				setClimate = true
			} catch (any) {
				log.debug("setRoomTstatSettings>schedule ${scheduleName}, in room ${roomName},not able to set climate ${climateName} for heating at the thermostat ${roomTstat}")

			}
		}
		if (!setClimate) {
			log.debug("ecobeeSetZoneWithSchedule>>schedule ${scheduleName}, in room ${roomName},about to apply zone's temp settings")
			key = "desiredHeatTemp$indiceSchedule"
			def heatTemp = settings[key]
			if (!heatTemp) {
				log.debug("setRoomTstatSettings>schedule ${scheduleName}, in room ${roomName},about to apply default heat settings")
				desiredHeat = (scale=='C') ? 21:72				// by default, 21°C/72°F is the target heat temp
			} else {
				desiredHeat = heatTemp.toFloat()
			}
			log.debug("setRoomTstatSettings>schedule ${scheduleName},in room ${roomName},${roomTstat}'s desiredHeat=${desiredHeat}")
			roomTstat.setHeatingSetpoint(desiredHeat)
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>schedule ${scheduleName}, in room ${roomName}, ${roomTstat}'s heating setPoint now =${desiredHeat}°")
			}                
		}
	} else if (mode == 'cool') {
		roomTstat.cool()
		if ((climateName) && (roomTstat?.hasCommand("setClimate"))) {
			try {
				roomTstat?.setClimate("", climateName)
				setClimate = true
			} catch (any) {
				log.debug("setRoomTstatSettings>schedule ${scheduleName},in room ${roomName},not able to set climate ${climateName} for cooling at the thermostat ${roomTstat}")

			}
		}
		if (!setClimate) {
			log.debug("ScheduleTstatZones>schedule ${scheduleName}, in room ${roomName},about to apply zone's temp settings")
			key = "desiredCoolTemp$indiceSchedule"
			def coolTemp = settings[key]
			if (!coolTemp) {
				log.debug("setRoomTstatSettings>schedule ${scheduleName}, in room ${roomName},about to apply default cool settings")
				desiredCool = (scale=='C') ? 23:75				// by default, 23°C/75°F is the target cool temp
			} else {
            
				desiredCool = coolTemp.toFloat()
			}
			log.debug("setRoomTstatSettings>schedule ${scheduleName}, in room ${roomName}, ${roomTstat}'s desiredCool=${desiredCool}")
			roomTstat.setCoolingSetpoint(desiredCool)
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},in room ${roomName}, ${roomTstat}'s cooling setPoint now =${desiredCool}°")
			}                
		}
	}
}

private def setAllRoomTstatsSettings(indiceSchedule,indiceZone) {
	boolean foundRoomTstat = false
	def	key= "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "includedRooms$indiceZone"
	def rooms = settings[key]
	for (room in rooms) {

		def roomDetails=room.split(':')
		def indiceRoom = roomDetails[0]
		def roomName = roomDetails[1]
		key = "needOccupiedFlag$indiceRoom"
		def needOccupied = (settings[key]) ?: false
		key = "roomTstat$indiceRoom"
		def roomTstat = settings[key]

		if (!roomTstat) {
			continue
		}
		log.debug("setAllRoomTstatsSettings>schedule ${scheduleName},found a room Tstat ${roomTstat}, needOccupied=${needOccupied} in room ${roomName}, indiceRoom=${indiceRoom}")
		foundRoomTstat = true
		if (needOccupied) {

			key = "motionSensor$indiceRoom"
			def motionSensor = settings[key]
			if (motionSensor != null) {

				if (isRoomOccupied(motionSensor, indiceRoom)) {
					log.debug("setAllRoomTstatsSettings>schedule ${scheduleName},for occupied room ${roomName},about to call setRoomTstatSettings ")
					setRoomTstatSettings(indiceSchedule,indiceZone, indiceRoom)
				} else {
                
					log.debug("setAllRoomTstatsSettings>schedule ${scheduleName},room ${roomName} not occupied,skipping it")
                
				}
			}
		} else {

			log.debug("setAllRoomTstatsSettings>schedule ${scheduleName},for room ${roomName},about to call setRoomTstatSettings ")
			setRoomTstatSettings(indiceSchedule,indiceZone, indiceRoom)
		}
	}
	return foundRoomTstat
}

private def getAllTempsForAverage(indiceZone) {
	def tempAtSensor

	def indoorTemps = []
	def key = "includedRooms$indiceZone"
	def rooms = settings[key]
	for (room in rooms) {

		def roomDetails=room.split(':')
		def indiceRoom = roomDetails[0]
		def roomName = roomDetails[1]

		key = "needOccupiedFlag$indiceRoom"
		def needOccupied = (settings[key]) ?: false
		log.debug("getAllTempsForAverage>looping thru all rooms,now room=${roomName},indiceRoom=${indiceRoom}, needOccupied=${needOccupied}")

		if (needOccupied) {

			key = "motionSensor$indiceRoom"
			def motionSensor = settings[key]
			if (motionSensor != null) {

				if (isRoomOccupied(motionSensor, indiceRoom)) {

					tempAtSensor = getSensorTempForAverage(indiceRoom)
					if (tempAtSensor != null) {
						indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
						log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} due to occupied room ${roomName} based on ${motionSensor}")
					}
					tempAtSensor = getSensorTempForAverage(indiceRoom,'roomTstat')
					if (tempAtSensor != null) {
						indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
						log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} due to occupied room ${roomName} based on ${motionSensor}")
					}
				}
			}

		} else {

			tempAtSensor = getSensorTempForAverage(indiceRoom)
			if (tempAtSensor != null) {
				log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} in room ${roomName}")
				indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
			}
			tempAtSensor = getSensorTempForAverage(indiceRoom,'roomTstat')
			if (tempAtSensor != null) {
				indoorTemps = indoorTemps + tempAtSensor.toFloat().round(1)
 				log.debug("getAllTempsForAverage>added ${tempAtSensor.toString()} in room ${roomName}")
			}

		}
	} /* end for */
	return indoorTemps

}

private def set_fan_mode(indiceSchedule, overrideThreshold=false) {
	def	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

/* YR commented out as issue with Schedule page (ST constraint?)
	key = "givenFanMinTime${indiceSchedule}"
	def fanMinTime=settings[key]

	if (fanMinTime) {
		log.debug("set_fan_mode>minimum Fan Time for $scheduleName schedule is $fanMinTime")
		def currentFanMinOnTime=thermostat.currentFanMinOnTime
		if (fanMinTime !=currentFanMinOnTime) {        
			// set FanMinTime for this schedule    
			thermostat.setThermostatSettings("", ['fanMinOnTime': "${fanMinTime}"])		    
			send ("ecobeeSetZoneWithSchedule>minimum Fan Time set for this $scheduleName schedule is now $fanMinTime minutes")             
		}            
	}    
*/
    
	key = "fanMode$indiceSchedule"
	def fanMode = settings[key]
        
	if (fanMode == null) {
		return     
	}

	key = "fanModeForThresholdOnlyFlag${indiceSchedule}"
	def fanModeForThresholdOnlyFlag = settings[key]

	def fanModeForThresholdOnly = (fanModeForThresholdOnlyFlag) ?: false
	if ((fanModeForThresholdOnly) && (!overrideThreshold)) {
    
		if (outTempSensor == null) {
			return     
		}

		key = "moreFanThreshold$indiceSchedule"
		def moreFanThreshold = settings[key]
		log.debug("setFanMode>fanModeForThresholdOnly=$fanModeForThresholdOnly,morefanThreshold=$moreFanThreshold")
		if (moreFanThreshold == null) {
			return     
		}
		float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)
        
		if (outdoorTemp < moreFanThreshold.toFloat()) {
			fanMode='auto'	// fan mode should be set then at 'auto'			
		}
	}    

	def currentFanMode=thermostat.latestValue("thermostatFanMode")
	if (fanMode == currentFanMode) {
		log.debug("set_fan_mode>schedule ${scheduleName},fan already in $fanMode at thermostat ${thermostat}, exiting...")
		return
	}    
	try {
		if (fanMode=='auto') {
			thermostat.fanAuto()        
 		}
		if (fanMode=='off') {
			thermostat.fanOff()        
 		}
 		if (fanMode=='on') {
			thermostat.fanOn()        
 		}
 		if (fanMode=='circulate') {
			thermostat.fanCirculate()        
 		}
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},set fan mode to ${fanMode} at thermostat ${thermostat} as requested")
		}
	} catch (e) {
		log.debug("set_fan_mode>schedule ${scheduleName},not able to set fan mode to ${fanMode} (exception $e) at thermostat ${thermostat}")
	}
}


private def adjust_tstat_for_more_less_heat_cool(indiceSchedule) {
	def scale = getTemperatureScale()
	def key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: false

	String currentProgType = thermostat.currentProgramType
	if (currentProgType.contains("vacation")) {				// don't make adjustment if on vacation mode
		log.debug("thermostat ${thermostat} is in vacation mode, exiting")
		return
	}
	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	if (setRoomThermostatsOnly) {
		log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName},all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    

	if (outTempSensor == null) {
		log.debug "adjust_tstat_for_more_less_heat_cool>no outdoor temp sensor set, exiting"
		return     
	}
	
	key = "moreHeatThreshold$indiceSchedule"
	def moreHeatThreshold = settings[key]
	key = "moreCoolThreshold$indiceSchedule"
	def moreCoolThreshold = settings[key]
	key = "lessHeatThreshold$indiceSchedule"
	def lessHeatThreshold = settings[key]
	key = "lessCoolThreshold$indiceSchedule"
	def lessCoolThreshold = settings[key]
	
	if ((moreHeatThreshold == null) && (moreCoolThreshold ==null) && 
		(lessHeatThreshold  == null) && (lessCoolThreshold ==null)) {
		log.debug "adjust_tstat_for_more_less_heat_cool>no adjustment variables set, exiting"
		return
	}
	
	float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)
    
	String currentMode = thermostat.currentThermostatMode.toString()
	float currentHeatPoint = thermostat.currentHeatingSetpoint.toFloat().round(1)
	float currentCoolPoint = thermostat.currentCoolingSetpoint.toFloat().round(1)
	float currentScheduleHeat = thermostat.currentProgramHeatTemp.toFloat().round(1)
	float currentScheduleCool = thermostat.currentProgramCoolTemp.toFloat().round(1)
	float targetTstatTemp 
    
	log.debug "adjust_tstat_for_more_less_heat_cool>currentMode=$currentMode,outdoorTemp=$outdoorTemp,moreCoolThreshold=$moreCoolThreshold,  moreHeatThreshold=$moreHeatThreshold," +
		"coolModeThreshold=$coolModeThreshold,heatModeThreshold=$heatModeThreshold,currentHeatSetpoint=$currentHeatPoint,currentCoolSetpoint=$currentCoolPoint"

	key = "givenMaxTempDiff$indiceSchedule"
	def givenMaxTempDiff = settings[key]
	def input_max_temp_diff = givenMaxTempDiff ?: (scale=='C')? 2: 5 // 2°C/5°F temp differential is applied by default

	float max_temp_diff = input_max_temp_diff.toFloat().round(1)
    
	if (currentMode== 'heat') {
		if ((moreHeatThreshold != null) & (outdoorTemp <= moreHeatThreshold?.toFloat()))  {
			targetTstatTemp = (currentHeatPoint + max_temp_diff).round(1)
			float temp_diff = (currentScheduleHeat - targetTstatTemp).round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for more heat" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for more heat")
				targetTstatTemp = (currentScheduleHeat + max_temp_diff).round(1)
			}
			thermostat.setHeatingSetpoint(targetTstatTemp)
			send("ecobeeSetZoneWithSchedule>heating setPoint now= ${targetTstatTemp}°, outdoorTemp <=${moreHeatThreshold}°")
            
		} else if ((lessHeatThreshold != null) && (outdoorTemp > lessHeatThreshold?.toFloat()))  {
			targetTstatTemp = (currentHeatPoint - max_temp_diff).round(1)
			float temp_diff = (currentScheduleHeat - targetTstatTemp).round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for less leat" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for less heat")
				targetTstatTemp = (currentScheduleHeat - max_temp_diff).round(1)
			}
			thermostat.setHeatingSetpoint(targetTstatTemp)
			send("ecobeeSetZoneWithSchedule>heating setPoint now= ${targetTstatTemp}°, outdoorTemp > ${lessHeatThreshold}°")
		}            
	}
	if (currentMode== 'cool') {
    
		if ((moreCoolThreshold!= null) && (outdoorTemp >= moreCoolThreshold?.toFloat())) {
			targetTstatTemp = (currentCoolPoint - max_temp_diff).round(1)
			float temp_diff =  (currentScheduleCool - targetTstatTemp).round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for more cool" 
			if (temp_diff.abs()  > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for more cool")
				targetTstatTemp = (currentScheduleCool - max_temp_diff).round(1)
			}
			thermostat.setCoolingSetpoint(targetTstatTemp)
			send("ecobeeSetZoneWithSchedule>cooling setPoint now= ${targetTstatTemp}°, outdoorTemp >=${moreCoolThreshold}°")
		} else if ((lessCoolThreshold != null) && (outdoorTemp < lessCoolThreshold?.toFloat())) {
			targetTstatTemp = (currentCoolPoint + max_temp_diff).round(1)
			float temp_diff = (currentScheduleCool - targetTstatTemp).round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for less cool" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for less cool")
				targetTstatTemp = (currentScheduleCool + max_temp_diff).round(1)
			}
			send("ecobeeSetZoneWithSchedule>cooling setPoint now= ${targetTstatTemp}°, outdoorTemp <${lessCoolThreshold}°")
			thermostat.setCoolingSetpoint(targetTstatTemp)
		}            
	} 
}


private def adjust_thermostat_setpoint_in_zone(indiceSchedule) {
	float desiredHeat, desiredCool, avg_indoor_temp
	float MIN_SETPOINT_ADJUSTMENT_IN_CELSIUS=0.5
	float MIN_SETPOINT_ADJUSTMENT_IN_FARENHEITS=1
	def scale = getTemperatureScale()

	String currentProgType = thermostat.currentProgramType
	if (currentProgType.contains("vacation")) {				// don't make adjustment if on vacation mode
		log.debug("thermostat ${thermostat} is in vacation mode, exiting")
		return
	}

	def key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "includedZones$indiceSchedule"
	def zones = settings[key]
	key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: false
	def indoor_all_zones_temps=[]

	log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}: zones= ${zones}")

	def adjustmentTempFlag = (setAdjustmentTempFlag)?: false
	for (zone in zones) {

		def zoneDetails=zone.split(':')
		log.debug("adjust_thermostat_setpoint_in_zone>zone=${zone}: zoneDetails= ${zoneDetails}")
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
        
		setAllRoomTstatsSettings(indiceSchedule, indiceZone) 

		if (setRoomThermostatsOnly) { // Does not want to set the main thermostat, only the room ones

			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},zone ${zoneName}: all room Tstats set and setRoomThermostatsOnlyFlag= true, continue...")
			}
            
		} else {
			if (adjustmentTempFlag) { 
				def indoorTemps = getAllTempsForAverage(indiceZone)
				indoor_all_zones_temps = indoor_all_zones_temps + indoorTemps
			}
		}
	}
	if (setRoomThermostatsOnly) {
		log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    
    
    
	if (state?.programHoldSet != "")  {
    
		log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}, ${state?.programHoldSet} hold set,exiting...")
		return				    
    
	}    
	//	Now will do an avg temp calculation based on all temp sensors to apply the desired temp settings at the main Tstat correctly

	float currentTemp = thermostat?.currentTemperature.toFloat().round(1)
	String mode = thermostat?.currentThermostatMode.toString()
	// This is the avg indoor temp based on indoor temp sensors in all rooms in the zone
	log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},all temps collected from sensors=${indoor_all_zones_temps}")
	if (indoor_all_zones_temps != [] ) {
		avg_indoor_temp = (indoor_all_zones_temps.sum() / indoor_all_zones_temps.size()).round(1)
	} else {
		avg_indoor_temp = currentTemp
	}

	float temp_diff = (avg_indoor_temp - currentTemp).round(1)
	if (detailedNotif == 'true') {
		send("ecobeeSetZoneWithSchedule>schedule ${scheduleName}:avg temp= ${avg_indoor_temp},main Tstat's currentTemp= ${currentTemp},temp adjustment=${temp_diff.abs()}")
	}

	key = "givenMaxTempDiff$indiceSchedule"
	def givenMaxTempDiff = settings[key]
	def input_max_temp_diff = givenMaxTempDiff ?: (scale=='C')? 2: 5 // 2°C/5°F temp differential is applied by default
	float max_temp_diff = input_max_temp_diff.toFloat().round(1)
  
	def adjustmentFanFlag = (setAdjustmentFanFlag)?: false
	if (adjustmentFanFlag) {
		// Adjust the fan mode if avg temp differential in zone is greater than max_temp_diff set in schedule
		if (temp_diff.abs() > max_temp_diff) {
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},avg_temp_diff=${temp_diff.abs()} > ${max_temp_diff} :adjusting fan mode as temp differential in zone is too big")				
				// set fan mode with overrideThreshold=true
				set_fan_mode(indiceSchedule, true)          
                
			}   
		}   
	}

	float min_setpoint_adjustment = (scale=='C') ? MIN_SETPOINT_ADJUSTMENT_IN_CELSIUS:MIN_SETPOINT_ADJUSTMENT_IN_FARENHEITS
	if (temp_diff.abs() < min_setpoint_adjustment) {  // adjust the temp only if temp diff is significant
		log.debug("adjust_thermostat_setpoint_in_zone>temperature adjustment (${temp_diff}°) between sensors is small, skipping it and exiting")
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>temperature adjustment (${temp_diff}°) between sensors is not significant, exiting")
		}
		return
	}                
	key = "givenClimate$indiceSchedule"
	def climateName = settings[key]
	if (mode == 'heat') {
	
		desiredHeat = thermostat.currentProgramHeatTemp.toFloat().round(1)
        
		temp_diff = (temp_diff < (0-max_temp_diff)) ? -(max_temp_diff):(temp_diff >max_temp_diff) ?max_temp_diff:temp_diff // determine the temp_diff based on max_temp_diff
		log.debug("ecobeeSetZoneWithSchedule>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff} for heating")

		float targetTstatTemp = (desiredHeat - temp_diff).round(1)
		thermostat?.setHeatingSetpoint(targetTstatTemp)
		if (detailedNotif) {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},in zones=${zones},heating setPoint now =${targetTstatTemp}°,adjusted by avg temp diff (${temp_diff.abs()}°) between all temp sensors in zone")
		}
		if (scheduleName != state.lastScheduleLastName) {
			state?.scheduleHeatSetpoint=desiredHeat  // save the desiredHeat in state variable
		}        
        
	} else if (mode == 'cool') {

		desiredCool = thermostat.currentProgramCoolTemp.toFloat().round(1)
		temp_diff = (temp_diff < (0-max_temp_diff)) ? -(max_temp_diff):(temp_diff >max_temp_diff) ?max_temp_diff:temp_diff // determine the temp_diff based on max_temp_diff
		log.debug("ecobeeSetZoneWithSchedule>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff} for cooling")
		float targetTstatTemp = (desiredCool - temp_diff).round(1)
		thermostat?.setCoolingSetpoint(targetTstatTemp)
		if (detailedNotif) {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName}, in zones=${zones},cooling setPoint now =${targetTstatTemp}°,adjusted by avg temp diff (${temp_diff}°) between all temp sensors in zone")
		}   
		if (scheduleName != state.lastScheduleLastName) {
			state?.scheduleCoolSetpoint=desiredCool  // save the desiredCool in state variable
		}        
        
	}
}


private def adjust_vent_settings_in_zone(indiceSchedule) {
	def MIN_OPEN_LEVEL_SMALL=25
	def MIN_OPEN_LEVEL_BIG=35
	float desiredTemp, avg_indoor_temp, avg_temp_diff
	def indiceRoom
	boolean closedAllVentsInZone=true
	int nbVents=0
	def switchLevel    
	def ventSwitchesOnSet=[]

	def key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "includedZones$indiceSchedule"
	def zones = settings[key]
	def indoor_all_zones_temps=[]
  
	log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}: zones= ${zones}")

	key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: false
	if (setRoomThermostatsOnly) {
		log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}:all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    
    
	String mode = thermostat?.currentThermostatMode.toString()
	float currentTempAtTstat = thermostat?.currentTemperature.toFloat().round(1)
	if (mode=='heat') {
		desiredTemp = thermostat.currentHeatingSetpoint.toFloat().round(1)
	} else if (mode=='cool') {    
		desiredTemp = thermostat.currentCoolingSetpoint.toFloat().round(1)
	} else {
		desiredTemp = thermostat.currentThermostatSetpoint.toFloat().round(1)
	}    
	log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, desiredTemp=${desiredTemp}")

	float currentTemp = thermostat?.currentTemperature.toFloat().round(1)
	indoor_all_zones_temps.add(currentTemp)
	for (zone in zones) {

		def zoneDetails=zone.split(':')
		log.debug("adjust_vent_settings_in_zone>zone=${zone}: zoneDetails= ${zoneDetails}")
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
		def indoorTemps = getAllTempsForAverage(indiceZone)

		if (indoorTemps != [] ) {
			indoor_all_zones_temps = indoor_all_zones_temps + indoorTemps
			            
		} else {
			log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, no data from temp sensors, exiting")
		}        
		log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, all temps collected from sensors=${indoorTemps}")
	} /* end for zones */

	avg_indoor_temp = (indoor_all_zones_temps.sum() / indoor_all_zones_temps.size()).round(1)
	avg_temp_diff = (avg_indoor_temp - desiredTemp).round(1)
	log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in all zones, all temps collected from sensors=${indoor_all_zones_temps}, avg_indoor_temp=${avg_indoor_temp}, avg_temp_diff=${avg_temp_diff}")

	for (zone in zones) {
		def zoneDetails=zone.split(':')
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
		key = "includedRooms$indiceZone"
		def rooms = settings[key]
		for (room in rooms) {
        
			switchLevel =0	// initially set at zero for check later
			def roomDetails=room.split(':')
			indiceRoom = roomDetails[0]
			def roomName = roomDetails[1]
/*            
			if (!roomName) {
				continue
			}
*/            
			key = "needOccupiedFlag$indiceRoom"
			def needOccupied = (settings[key]) ?: false
			log.debug("adjust_vent_settings_in_zone>looping thru all rooms,now room=${roomName},indiceRoom=${indiceRoom}, needOccupied=${needOccupied}")

			if (needOccupied) {
				key = "motionSensor$indiceRoom"
				def motionSensor = settings[key]
				if (motionSensor != null) {
					if (!isRoomOccupied(motionSensor, indiceRoom)) {
						switchLevel =MIN_OPEN_LEVEL_SMALL // setLevel at MIN_OPEN_LEVEL_SMALL as the room is not occupied.
						log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, room = ${roomName},not occupied,vents set to mininum level=${switchLevel}")
					}
				}
			} 
			if (!switchLevel) {
				def tempAtSensor =getSensorTempForAverage(indiceRoom)			
				if (tempAtSensor == null) {
					tempAtSensor= currentTempAtTstat				            
				}
				float temp_diff_at_sensor = tempAtSensor.toFloat().round(1) - desiredTemp 
				log.debug("adjust_vent_settings_in_zone>thermostat mode = ${mode}, schedule ${scheduleName}, in zone ${zoneName}, room ${roomName}, temp_diff_at_sensor=${temp_diff_at_sensor}, avg_temp_diff=${avg_temp_diff}")
				if (mode=='heat') {
					avg_temp_diff = (avg_temp_diff !=0) ? avg_temp_diff : (-0.1)  // to avoid divided by zero exception
					switchLevel = ((temp_diff_at_sensor / avg_temp_diff) * 100).round()
					switchLevel =( switchLevel >=0)?((switchLevel<100)? switchLevel: 100):0
					switchLevel=(temp_diff_at_sensor >=0)? MIN_OPEN_LEVEL_SMALL: ((temp_diff_at_sensor <0) && (avg_temp_diff>0))?100:switchLevel
				} else if (mode =='cool') {
					avg_temp_diff = (avg_temp_diff !=0) ? avg_temp_diff : (0.1)  // to avoid divided by zero exception
					switchLevel = ((temp_diff_at_sensor / avg_temp_diff) * 100).round()
					switchLevel =( switchLevel >=0)?((switchLevel<100)? switchLevel: 100):0
					switchLevel=(temp_diff_at_sensor <=0)? MIN_OPEN_LEVEL_SMALL: ((temp_diff_at_sensor >0) && (avg_temp_diff<0))?100:switchLevel
				}                
			} 
			if (switchLevel >=10) {	
				closedAllVentsInZone=false
			}              
			log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName}, in zone ${zoneName}, room ${roomName},switchLevel to be set=${switchLevel}")
			for (int j = 1;(j <= 5); j++)  {
				key = "ventSwitch${j}$indiceRoom"
				def ventSwitch = settings[key]
				if (ventSwitch != null) {
					setVentSwitchLevel(indiceRoom, ventSwitch, switchLevel)                
					log.debug "adjust_vent_settings_in_zone>in zone=${zoneName},room ${roomName},set ${ventSwitch} at switchLevel =${switchLevel}%"
					ventSwitchesOnSet.add(ventSwitch)
					nbVents++                    
				}
			} /* end for ventSwitch */                             
		} /* end for rooms */
	} /* end for zones */

	if ((closedAllVentsInZone) && (nbVents)) {
		    	
		switchLevel=(nbVents>2)? MIN_OPEN_LEVEL_SMALL:MIN_OPEN_LEVEL_BIG        
		ventSwitchesOnSet=control_vent_switches_in_zone(indiceSchedule, switchLevel)		    
		log.debug "adjust_vent_settings_in_zone>schedule ${scheduleName}, set all ventSwitches at ${switchLevel}% to avoid closing all of them"
		if (detailedNotif) {
			send("ecobeeSetZoneWithSchedule>schedule ${scheduleName},set all ventSwitches at ${switchLevel}% to avoid closing all of them")
		}
	}    
	log.debug("adjust_vent_settings_in_zone>schedule ${scheduleName},ventSwitchesOnSet=${ventSwitchesOnSet}")
	return ventSwitchesOnSet    
}

private def turn_off_all_other_vents(ventSwitchesOnSet) {
	def foundVentSwitch
	int nbClosedVents=0, totalVents=0
	float MAX_RATIO_CLOSED_VENTS=50 // not more than 50% of the smart vents should be closed at once
	def MIN_OPEN_LEVEL=25  
	def closedVentsSet=[]
    
	for (indiceRoom in 1..roomsCount) {
		for (int j = 1;(j <= 5); j++)  {
			def key = "ventSwitch${j}$indiceRoom"
			def ventSwitch = settings[key]
			if (ventSwitch != null) {
				totalVents++
                
/* Y.R. Check is now done in an event handler
				// Prior to any processing, check temperature in each vent to avoid any HVAC damage
				if (is_temperature_too_hot_or_too_cold(ventSwitch)) {
					log.debug("turn_off_all_other_vents>temperature too hot or too cold in ${ventSwitch}, exiting...")
					return                        
				}                    
*/                
				foundVentSwitch = ventSwitchesOnSet.find{it == ventSwitch}
				if (foundVentSwitch ==null) {
					nbClosedVents++ 
					closedVentsSet.add(ventSwitch)                        
					log.debug("turn_off_all_other_vents>turned off ${ventSwitch} as requested to create the desired zone(s)")
				} else {
					try {                
						ventSwitch.refresh() 
					} catch (e) {
						log.debug("turn_off_all_other_vents>exception ${e} while trying to call refresh() on ${ventSwitch}")
					}                    
					def setLevel = ventSwitch.latestValue("level")
					if (setlevel < MIN_OPEN_LEVEL_SMALL) {                    
						nbClosedVents++ 
						closedVentsSet.add(ventSwitch)                        
					}                    
				}                    
			}   /* end if ventSwitch */                  
		}  /* end for ventSwitch */         
	} /* end for rooms */
	float ratioClosedVents=(nbClosedVents/totalVents*100)
    
	if (ratioClosedVents > MAX_RATIO_CLOSED_VENTS) {
		log.debug("turn_off_all_other_vents>ratio of closed vents is too high (${ratioClosedVents.round()}%), opening ${closedVentsSet} at minimum level of ${MIN_OPEN_LEVEL}%")
		if (detailedNotif == 'true') {
			send("ecobeeSetZoneWithSchedule>ratio of closed vents is too high (${ratioClosedVents.round()}%), opening ${closedVentsSet} at minimum level of ${MIN_OPEN_LEVEL}%")
		}
		closedVentsSet.each {
			setVentSwitchLevel(null, it, MIN_OPEN_LEVEL)
		}        
	} else {
		closedVentsSet.each {
			it.off()
		}        
    
	}        
    
}

private boolean is_temperature_too_hot_or_too_cold(ventSwitch) {
	def scale = getTemperatureScale()
	def MAX_TEMP_VENT_SWITCH = (scale=='C')?50:122 //Max temperature inside a ventSwitch
	def MIN_TEMP_VENT_SWITCH = (scale=='C')?7:45 //Min temperature inside a ventSwitch
	String currentHVACMode = thermostat.currentThermostatMode.toString()
    
	def tempSwitch=getTemperatureInVent(ventSwitch)                    
	log.debug("is_temperature_in_vent_too_hot_or_too_cold>checking current Temperature of ${ventSwitch}= ${tempSwitch}° vs. HVAC Max Temp of ${MAX_TEMP_VENT_SWITCH}°")
	log.debug("is_temperature_in_vent_too_hot_or_too_cold>checking current Temperature of ${ventSwitch}= ${tempSwitch}° vs. HVAC Min Temp of ${MIN_TEMP_VENT_SWITCH}°")
	if (tempSwitch) {    
		if (((currentHVACMode=='heat') || (currentHVACMode == 'auto')) && (tempSwitch >= MAX_TEMP_VENT_SWITCH)) {
			// Turn the HVAC off, open all vents, and deactivate any further smartapp processing
			thermostat.off()            
			open_all_vents()
			powerSwitch?.off()            
			send("ecobeeSetZoneWithSchedule> ** IMMEDIATE ATTENTION: current HVAC mode is ${currentHVACMode}, and inside temperature of vent ${ventSwitch} too hot (${tempSwitch}°), opening all vents and turning off the HVAC to avoid any damage **")
			return true            
		} /* if too hot */           
		if (((currentHVACMode=='cool') || (currentHVACMode == 'auto')) && (tempSwitch <= MIN_TEMP_VENT_SWITCH)) {
			// Turn the HVAC off, open all vents, and deactivate any further smartapp processing
			thermostat.off()            
			open_all_vents()
			powerSwitch?.off()            
			send("ecobeeSetZoneWithSchedule> ** IMMEDIATE ATTENTION: current HVAC mode is ${currentHVACMode}, and inside temperature of vent ${ventSwitch} too cold (${tempSwitch}°), opening all vents and turning off the HVAC to avoid any damage **")
			return true            
		} /* if too cold */ 
	} /* if tempSwitch != null */         
	return false
}

private def open_all_vents() {
	// Turn on all vents        
	for (indiceRoom in 1..roomsCount) {
		for (int j = 1;(j <= 5); j++)  {
			def key = "ventSwitch${j}$indiceRoom"
			def vent = settings[key]
				if (vent != null) {
					vent.setLevel(100)	
				} /* end if vent != null */
		} /* end for vent switches */
	} /* end for rooms */
}
private def getTemperatureInVent(ventSwitch) {
	def temp=null
	try {
		temp = ventSwitch.currentTemperature
	} catch (any) {
		log.debug("getTemperatureInVent>Not able to current Temperature from ${ventSwitch}")
	}    
	return temp    
}

private def setVentSwitchLevel(indiceRoom, ventSwitch, switchLevel=100) {
	def roomName
    
	if (indiceRoom) {
		def key = "roomName$indiceRoom"
		roomName = settings[key]
	}
	try {
		ventSwitch.setLevel(switchLevel)
		if (roomName) {       
			log.debug("setVentSwitchLevel>set ${ventSwitch} at level ${switchLevel} in room ${roomName} to reach desired temperature")
			if (detailedNotif) {
				send("ecobeeSetZoneWithSchedule>set ${ventSwitch} at level ${switchLevel} in room ${roomName} to reach desired temperature")
			}
		}            
	} catch (e) {
		if (switchLevel >0) {
			ventSwitch.on()        
			log.error "setVentSwitchLevel>not able to set ${ventSwitch} at ${switchLevel} (exception $e), trying to turn it on"
		} else {
			ventSwitch.off()        
			log.error "setVentSwitchLevel>not able to set ${ventSwitch} at ${switchLevel} (exception $e), trying to turn it off"
		}
	}
    
}



private def control_vent_switches_in_zone(indiceSchedule, switchLevel=100) {

	def key = "includedZones$indiceSchedule"
	def zones = settings[key]
	def ventSwitchesOnSet=[]
    
	for (zone in zones) {

		def zoneDetails=zone.split(':')
		def indiceZone = zoneDetails[0]
		def zoneName = zoneDetails[1]
		key = "includedRooms$indiceZone"
		def rooms = settings[key]
    
		for (room in rooms) {
			def roomDetails=room.split(':')
			def indiceRoom = roomDetails[0]
			def roomName = roomDetails[1]


			for (int j = 1;(j <= 5); j++)  {
	                
				key = "ventSwitch${j}$indiceRoom"
				def ventSwitch = settings[key]
				if (ventSwitch != null) {
					ventSwitchesOnSet.add(ventSwitch)
					setVentSwitchLevel(indiceRoom, ventSwitch, switchLevel)
				}
			} /* end for ventSwitch */
		} /* end for rooms */
	} /* end for zones */
	return ventSwitchesOnSet
}



private send(msg) {
	if (sendPushMessage != "No") {
		sendPush(msg)
	}
	
	if (phone) {
		log.debug("sending text message")
		sendSms(phone, msg)
	}
	log.debug msg
}
