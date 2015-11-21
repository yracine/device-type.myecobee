/**
 *  ScheduleTsatZones
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
	name: "ScheduleTstatZones",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Enable Heating/Cooling Zoned Solutions for thermostats coupled with smart vents (optional) for better temp settings control throughout your home",
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
			paragraph "ScheduleTstatZones, the smartapp that enables Heating/Cooling zoned settings at selected thermostat(s) coupled with smart vents (optional) for better temp settings control throughout your home"
			paragraph "Version 5.2" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest",
					title:"Paypal donation..."
			paragraph "Copyright©2015 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
 					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Main thermostat at home (used for temp/vent adjustment)") {
			input (name:"thermostat", type: "capability.thermostat", title: "Which main thermostat?")
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
			input (name:"setAwayOrPresentFlag", title: "Set Main thermostat to [Away,Present]?", type:"bool",required:false)
		}
		section("Outdoor temp Sensor used for adjustment [optional]") {
			input (name:"outTempSensor", type:"capability.temperatureMeasurement", required: false,
				description:"Optional")
		}
		section("Enable vent settings [optional, default=false]") {
			input (name:"setVentSettingsFlag", title: "Set Vent Settings?", type:"bool",required:false)
		}
		section("Enable temp adjustment based on outdoor temp sensor [optional, default=false]") {
			input (name:"setAdjustmentOutdoorTempFlag", title: "Enable temp adjustment based on outdoor sensor?", type:"bool",required:false)
		}
		section("Enable temp adjustment based on indoor temp/motion sensor(s) [optional, default=false]") {
			input (name:"setAdjustmentTempFlag", title: "Enable temp adjustment based on avg temp collected at indoor sensor(s)?", type:"bool",required:false)
		}
		section("Enable fan adjustment based on indoor/outdoor temp sensors [optional, default=false]") {
			input (name:"setAdjustmentFanFlag", title: "Enable fan adjustment set in schedules based on sensors?", type:"bool",required:false)
		}
		section("What do I use for the Master on/off switch to enable/disable smartapp processing? [optional]") {
			input (name:"powerSwitch", type:"capability.switch", required: false,description: "Optional")
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
	} catch (any) {
		log.debug("Not able to get the list of climates (ecobee)")    	
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

	dynamicPage(name: "schedulesSetup", title: "Schedule Setup") {
		section("Schedule ${indiceSchedule} Setup") {
			input (name:"scheduleName${indiceSchedule}", title: "Schedule Name", type: "text",
				defaultValue:settings."scheduleName${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Included zones") {
			input (name:"includedZones${indiceSchedule}", title: "Zones included in this schedule", type: "enum",
				defaultValue:settings."includedZones${indiceSchedule}",
				options: zones,
 				multiple: true)
		}
		section("Schedule ${indiceSchedule}- Day & Time of the desired Heating/Cooling settings for the selected zone(s)") {
			input (name:"dayOfWeek${indiceSchedule}", type: "enum",
				title: "Which day of the week to trigger the zoned heating/cooling settings?",
				defaultValue:settings."dayOfWeek${indiceSchedule}",                 
				multiple: false,
				metadata: [
					values: [
						'All Week',
						'Monday to Friday',
						'Saturday & Sunday',
						'Monday',
						'Tuesday',
						'Wednesday',
						'Thursday',
						'Friday',
						'Saturday',
						'Sunday'
					]
				])
			input (name:"begintime${indiceSchedule}", type: "time", title: "Beginning time to trigger the zoned heating/cooling settings",
				defaultValue:settings."begintime${indiceSchedule}")
			input (name:"endtime${indiceSchedule}", type: "time", title: "End time",
				defaultValue:settings."endtime${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Switch thermostat mode (auto/cool/heat) based on this outdoor temp range [optional]") {
			input (name:"heatModeThreshold${indiceSchedule}", type:"decimal", title: "Heat mode threshold", 
				required: false, defaultValue:settings."heatModeThreshold${indiceSchedule}", description: "Optional")			                

			input (name:"coolModeThreshold${indiceSchedule}", type:"decimal", title: "Cool mode threshold", 
				required: false, defaultValue:settings."coolModeThreshold${indiceSchedule}", description: "Optional")			               
		}			
		section("Schedule ${indiceSchedule}-Select the program/climate at ecobee thermostat to be applied [optional,for ecobee]") {
			input (name:"givenClimate${indiceSchedule}", type:"enum", title: "Which ecobee program? ", options: ecobeePrograms, 
				required: false, defaultValue:settings."givenClimate${indiceSchedule}", description: "Optional")
		}
		section("Schedule ${indiceSchedule}-Set Thermostat's Cooling setpoint in the selected zone(s) [optional,when no ecobee program/climate available]") {
			input (name:"desiredCoolTemp${indiceSchedule}", type:"decimal", title: "Cooling setpoint, default = 75°F/23°C", 
				required: false,defaultValue:settings."desiredCoolTemp${indiceSchedule}", description: "Optional")			                
		}
		section("Schedule ${indiceSchedule}-Set Thermostat's Heating setpoint [optional,when no ecobee program/climate available]") {
			input (name:"desiredHeatTemp${indiceSchedule}", type:"decimal", title: "Heating setpoint, default=72°F/21°C", 
				required: false, defaultValue:settings."desiredHeatTemp${indiceSchedule}", description: "Optional")			                
		}
		section("Schedule ${indiceSchedule}-More Heat/Cool Threshold in the selected zone(s) based on outdoor temp Sensor [optional]") {
			input (name:"moreHeatThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for more heating", 
				required: false, defaultValue:settings."moreHeatThreshold${indiceSchedule}", description: "Optional")			                
			input (name:"moreCoolThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for more cooling",
				required: false,defaultValue:settings."moreCoolThreshold${indiceSchedule}", description: "Optional")
		}
		section("Schedule ${indiceSchedule}-Max Temp Adjustment/Differential Allowed for the zone(s)") {
			input (name:"givenMaxTempDiff${indiceSchedule}", type:"decimal",  title: "Max Temp adjustment/Differential", required: false,
				defaultValue:settings."givenMaxTempDiff${indiceSchedule}", description: " [default= +/-5°F/2°C]")
		}        
		section("Schedule ${indiceSchedule}-Set Fan Mode [optional]") {
			input (name:"fanMode${indiceSchedule}", type:"enum", title: "Set Fan Mode ['on', 'auto', 'circulate']", metadata: [values: ["on", "auto", "circulate"]], required: false,
				defaultValue:settings."fanMode${indiceSchedule}", description: "Optional")
			input (name:"moreFanThreshold${indiceSchedule}", type:"decimal", title: "Outdoor temp's threshold for Fan Mode", required: false,
				defaultValue:settings."moreFanThreshold${indiceSchedule}", description: "Optional")			                
			input (name:"fanModeForThresholdOnlyFlag${indiceSchedule}", type:"bool",  title: "Override Fan Mode only when Threshold or Indoor Temp differential is reached(default=false)", 
				required: false, defaultValue:settings."fanModeForThresholdOnlyFlag${indiceSchedule}")
		}	
		section("Schedule ${indiceSchedule}-Set Zone/Room Thermostats Only Indicator [optional]") {
			input (name:"setRoomThermostatsOnlyFlag${indiceSchedule}", type:"bool", title: "Set room thermostats only [default=false,main & room thermostats setpoints are set]", 
				required: false, defaultValue:settings."setRoomThermostatsOnlyFlag${indiceSchedule}")
		}
		section("Schedule ${indiceSchedule}-Set for specific mode(s) [default=all]")  {
			input (name:"selectedMode${indiceSchedule}", type:"enum", title: "Choose Mode", options: enumModes, 
				required: false, multiple:true,defaultValue:settings."selectedMode${indiceSchedule}", description: "Optional")
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
		send("ScheduleTstatZones>current HVAC mode is ${currentHVACMode}, found one of the vents' value too hot (${evt.value}°), opening all vents to avoid any damage")
	} /* if too hot */           
	if (((currentHVACMode=='cool') || (currentHVACMode == 'auto')) && (ventTemp <= MIN_TEMP_VENT_SWITCH)) {
		// Open all vents just to be safe
		open_all_vents()
		send("ScheduleTstatZones>current HVAC mode is ${currentHVACMode}, found one of the vents' value too cold (${evt.value}°), opening all vents to avoid any damage")
	} /* if too cold */ 
}

def thermostatOperatingHandler(evt) {
	log.debug "Thermostat Operating now: $evt.value"
	state?.operatingState=evt.value    
	setZoneSettings()      
}

def heatingSetpointHandler(evt) {
	log.debug "heating Setpoint now: $evt.value"
}
def coolingSetpointHandler(evt) {
	log.debug "cooling Setpoint now: $evt.value"
}

def changeModeHandler(evt) {
	log.debug "Changed mode, $evt.name: $evt.value"
	setZoneSettings()  
}

def motionEvtHandler(evt) {
	if (evt.value == "active") {
		log.debug "Motion at home..."

		if (state?.setPresentOrAway == 'Away') {
			set_main_tstat_to_AwayOrPresent('present')
		}        
	}
}

def initialize() {

	if (powerSwitch) {
		subscribe(powerSwitch, "switch.off", offHandler, [filterEvents: false])
		subscribe(powerSwitch, "switch.on", onHandler, [filterEvents: false])
	}
	subscribe(thermostat, "heatingSetpoint", heatingSetpointHandler)    
	subscribe(thermostat, "coolingSetpoint", coolingSetpointHandler)
	subscribe(thermostat, "thermostatOperatingState", thermostatOperatingHandler)
    
	subscribe(location, changeModeHandler)

	// Initialize state variables
	state.lastScheduleLastName=""
	state.lastStartTime=null 
	state.scheduleHeatSetpoint=0  
	state.scheduleCoolSetpoint=0    
	state.setPresentOrAway=''
	state.programSetTime = ""
	state.programSetTimestamp = null
	state.operatingState=""
    
	subscribe(app, appTouch)

	// subscribe all vents to check their temperature on a regular basis
    
	for (indiceRoom in 1..roomsCount) {
		for (int j = 1;(j <= 5); j++)  {
			def key = "ventSwitch${j}$indiceRoom"
			def vent = settings[key]
			if (vent) {
				subscribe(vent, "temperature", ventTemperatureHandler)
			} /* end if vent != null */
		} /* end for vent switches */
	} /* end for rooms */

	// subscribe all motion sensors to check for active motion in rooms
    
	def motionSensors =[]   	 
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
		log.info "recheduleIfNeeded>scheduling setZoneSettings in ${delay} minutes.."
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
		log.info "setZoneSettings>Scheduling rescheduleIfNeeded() in ${delay} minutes.."
		schedule("0 0/${delay} * * * ?", rescheduleIfNeeded)
		// Update rescheduled state
		state?.poll["rescheduled"] = now()
	}
	if (powerSwitch?.currentSwitch == "off") {
		if (detailedNotif) {
			send("ScheduleTstatZones>${powerSwitch.name} is off, schedule processing on hold...")
		}
		return
	}

	def currTime = now()
	boolean initialScheduleSetup=false        
	boolean foundSchedule=false

	/* Poll or refresh the thermostat to get latest values */
	if  (thermostat.hasCapability("Polling")) {
		try {        
			thermostat.poll()
		} catch (e) {
			log.debug("setZoneSettings>not able to do a poll() on ${thermostat}, exception ${e}")
		}                    
	}  else if  (thermostat.hasCapability("Refresh")) {
		try {        
			thermostat.refresh()
		} catch (e) {
			log.debug("setZoneSettings>not able to do a refresh() on ${thermostat}, exception ${e}")
		}                    
	}                    

	if ((outTempSensor) && ((outTempSensor.hasCapability("Refresh")) || (outTempSensor.hasCapability("Polling")))) {

		// do a refresh to get latest temp value
		try {        
			outTempSensor.refresh()
		} catch (e) {
			log.debug("setZoneSettings>not able to do a refresh() on ${outTempSensor}, exception ${e}")
		}                    
	}
	def ventSwitchesOn = []

	def setVentSettings = (setVentSettingsFlag) ?: false
	def adjustmentOutdoorTempFlag = (setAdjustmentOutdoorTempFlag)?: false
	def adjustmentTempFlag = (setAdjustmentTempFlag)?: false
	def adjustmentFanFlag = (setAdjustmentFanFlag)?: false
    
	String nowInLocalTime = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
	for (int i = 1;((i <= settings.schedulesCount) && (i <= 12)); i++) {
        
		def key = "selectedMode$i"
		def selectedModes = settings[key]
		key = "scheduleName$i"
		def scheduleName = settings[key]

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
		key = "begintime$i"
		def startTime = settings[key]
		if (startTime == null) {
        		continue
		}
		def startTimeToday = timeToday(startTime,location.timeZone)
		key = "endtime$i"
		def endTime = settings[key]
		def endTimeToday = timeToday(endTime,location.timeZone)
		if ((currTime < endTimeToday.time) && (endTimeToday.time < startTimeToday.time)) {
			startTimeToday = startTimeToday -1        
			log.debug "setZoneSettings>schedule ${scheduleName}, subtracted - 1 day, new startTime=${startTimeToday.time}"
		}            
		if ((currTime > endTimeToday.time) && (endTimeToday.time < startTimeToday.time)) {
			endTimeToday = endTimeToday +1        
			log.debug "setZoneSettings>schedule ${scheduleName} added + 1 day, new endTime=${endTimeToday.time}"
		}        
		String startInLocalTime = startTimeToday.format("yyyy-MM-dd HH:mm", location.timeZone)
		String endInLocalTime = endTimeToday.format("yyyy-MM-dd HH:mm", location.timeZone)

		log.debug "setZoneSettings>found schedule ${scheduleName},original startTime=$startTime,original endTime=$endTime,nowInLocalTime= ${nowInLocalTime},startInLocalTime=${startInLocalTime},endInLocalTime=${endInLocalTime}," +
        		"currTime=${currTime},begintime=${startTimeToday.time},endTime=${endTimeToday.time},lastScheduleName=$state.lastScheduleName, lastStartTime=$state.lastStartTime"
        
		def ventSwitchesZoneSet = []        
		if ((currTime >= startTimeToday.time) && (currTime <= endTimeToday.time) && (state.lastStartTime != startTimeToday.time) && (IsRightDayForChange(i))) {
        
			// let's set the given schedule
			initialScheduleSetup=true
			foundSchedule=true

			log.debug "setZoneSettings>schedule ${scheduleName},currTime= ${currTime}, current date & time OK for execution"
			if (detailedNotif) {
				send("ScheduleTstatZones>now running schedule ${scheduleName},about to set zone settings as requested")
			}
			if (adjustmentFanFlag) {                
				set_fan_mode(i)
			}   
			adjust_thermostat_setpoint_in_zone(i)
			if (setVentSettings) {
			// set the zoned vent switches to 'on' and adjust them according to the ambient temperature
               
/*				ventSwitchesZoneSet= control_vent_switches_in_zone(i)
*/
				ventSwitchesZoneSet= adjust_vent_settings_in_zone(i)
				log.debug "setZoneSettings>schedule ${scheduleName},list of Vents turned 'on'= ${ventSwitchesZoneSet}"

			}
 			ventSwitchesOn = ventSwitchesOn + ventSwitchesZoneSet              
			state.lastScheduleName = scheduleName
			state?.lastStartTime = startTimeToday.time
		}
		else if ((state.lastScheduleName == scheduleName) && (currTime >= startTimeToday.time) && (currTime <= endTimeToday.time) && (IsRightDayForChange)) {
			// We're in the middle of a schedule run
        
			log.debug "setZoneSettings>schedule ${scheduleName},currTime= ${currTime}, current time is OK for execution, we're in the middle of a schedule run"
			foundSchedule=true
			def setAwayOrPresent = (setAwayOrPresentFlag)?:false
			boolean isResidentPresent=true
            
			if (setAwayOrPresent) {
	            
				isResidentPresent=verify_presence_based_on_motion_in_rooms()
				if (isResidentPresent) {            

					if (state.setPresentOrAway != 'present') {
						set_main_tstat_to_AwayOrPresent('present')
					}
				} else {
					if (state.setPresentOrAway != 'away') {
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
					// adjust the temperature at the thermostat(s) based on indoor temp sensors
					adjust_thermostat_setpoint_in_zone(i)
				}                    
				if (adjustmentOutdoorTempFlag) {            	
					// check the thermsostat mode based on outdoor temp's thresholds (heat, cool) if any set                
					switch_thermostatMode(i) 
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
		} else {
			if (detailedNotif) {
				send("ScheduleTstatZones>schedule: ${scheduleName},change not scheduled at this time ${nowInLocalTime}...")
			}
		}

	} /* end for */
    
	if ((setVentSettings) && ((ventSwitchesOn !=[]) || (initialScheduleSetup))) {
		log.debug "setZoneSettings>list of Vents turned on= ${ventSwitchesOn}"
		turn_off_all_other_vents(ventSwitchesOn)
	}
	if (!foundSchedule) {
		if (detailedNotif) {
			send "ScheduleTstatZones>No schedule applicable at this time ${nowInLocalTime}"
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

private def set_main_tstat_to_AwayOrPresent(mode) {

	try {
		if  (mode == 'away') {
			thermostat.away()
		} else if (mode == 'present') {	
			thermostat.present()
		}
            
		send("ScheduleTstatZones>set main thermostat ${thermostat} to ${mode} mode based on motion in all rooms")
		state?.setPresentOrAway=mode    // set a state for further checking later
	 	state?.programSetTime = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
 		state?.programSetTimestamp = now()
	}    
	catch (e) {
		log.error("set_tstat_to_AwayOrPresent>not able to set thermostat ${thermostat} to ${mode} mode (exception $e)")
	}

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
		if (tempSensor.hasCapability("Refresh")) {
			// do a refresh to get the latest temp value
			try {        
				tempSensor.refresh()
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

	log.debug("ScheduleTstaZones>schedule ${scheduleName}, in room ${roomName},about to apply zone's temp settings at ${roomTstat}")
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
			log.debug("ScheduleTstatZones>schedule ${scheduleName}, in room ${roomName},about to apply zone's temp settings")
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
				send("ScheduleTstatZones>schedule ${scheduleName}, in room ${roomName}, ${roomTstat}'s heating setPoint now =${desiredHeat}°")
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
				send("ScheduleTstatZones>schedule ${scheduleName},in room ${roomName}, ${roomTstat}'s cooling setPoint now =${desiredCool}°")
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

	def key = "fanMode$indiceSchedule"
	def fanMode = settings[key]
	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

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
		log.debug("setFanMode>fanModeForThresholdOnly=$fanModForThresholdOnly,morefanThreshold=$moreFanThreshold")
		if (moreFanThreshold == null) {
			return     
		}
		float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)
        
		if (outdoorTemp < moreFanThreshold.toFloat()) {
			fanMode='off'	// fan mode should be set then at 'off'			
		}
	}    

	def currentFanMode=thermostat.latestValue("thermostatFanMode")
	if ((fanMode == currentFanMode) || ((fanMode=='off') && (currentFanMode=='auto'))) {
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
        
		if (detailedNotif) {
			send("ScheduleTstatZones>schedule ${scheduleName},set fan mode to ${fanMode} at thermostat ${thermostat} as requested")
		}
	} catch (e) {
		log.debug("set_fan_mode>schedule ${scheduleName},not able to set fan mode to ${fanMode} (exception $e) at thermostat ${thermostat}")
	}
}



private def switch_thermostatMode(indiceSchedule) {

	def key = "outTempSensor$indiceSchedule"
	def outTempSensor = settings[key]
    
	if (outTempSensor == null) {
		return     
	}
    
	float outdoorTemp = outTempSensor.currentTemperature.toFloat().round(1)

	key = "heatModeThreshold$indiceSchedule"
	def heatModeThreshold = settings[key]
	key = "coolModeThreshold$indiceSchedule"
	def coolModeThreshold = settings[key]
    
	if ((heatModeThreshold == null) && (coolModeThreshold ==null)) {
		log.debug "switch_thermostatMode>no adjustment variables set, exiting"
		return
	}        
	String currentMode = thermostat.currentThermostatMode.toString()
	def currentHeatPoint = thermostat.currentHeatingSetpoint
	def currentCoolPoint = thermostat.currentCoolingSetpoint
	log.debug "switch_thermostatMode>currentMode=$currentMode, outdoor temperature=$outdoorTemp, coolTempThreshold=$coolTempThreshold, heatTempThreshold=$heatTempThreshold"

	if ((heatModeThreshold != null) && (outdoorTemp < heatModeThreshold?.toFloat())) {
		if (currentMode != "heat") {
			def newMode = "heat"
			thermostat.setThermostatMode(newMode)
			log.debug "switch_thermostatMode>thermostat mode set to $newMode"
			state.scheduleHeatSetpoint=currentHeatPoint      // Set for later processing in adjust_more_less_heat_cool()     
		}
	} else if ((coolModeThreshold != null) && (outdoorTemp > coolModeThreshold?.toFloat())) {
		if (currentMode != "cool") {
			def newMode = "cool"
			thermostat.setThermostatMode(newMode)
			log.debug "switch_thermostatMode>thermostat mode set to $newMode"
			state.scheduleCoolSetpoint=currentCoolPoint      // Set for later processing in adjust_more_less_heat_cool()      
		}
	} else if ((currentMode != "auto") && (currentMode != "off")) {
			def newMode = "auto"
			thermostat.setThermostatMode(newMode)
			log.debug "switch_thermostatMode>thermostat mode set to $newMode"
    
	}    

}

private def adjust_tstat_for_more_less_heat_cool(indiceSchedule) {
	def scale = getTemperatureScale()
	def key = "setRoomThermostatsOnlyFlag$indiceSchedule"
	def setRoomThermostatsOnlyFlag = settings[key]
	def setRoomThermostatsOnly = (setRoomThermostatsOnlyFlag) ?: false
	key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	if (setRoomThermostatsOnly) {
		log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName},all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    

	key = "outTempSensor$indiceSchedule"
	def outTempSensor = settings[key]
	if (outTempSensor == null) {
		log.debug "adjust_tstat_for_more_less_heat_cool>no outdoor temp sensor set, exiting"
		return     
	}
	
	key = "moreHeatThreshold$indiceSchedule"
	def moreHeatThreshold = settings[key]
	key = "moreCoolThreshold$indiceSchedule"
	def moreCoolThreshold = settings[key]
	key = "heatModeThreshold$indiceSchedule"
	def heatModeThreshold = settings[key]
	key = "coolModeThreshold$indiceSchedule"
	def coolModeThreshold = settings[key]


	if ((moreHeatThreshold == null) && (moreCoolThreshold ==null) && 
		(heatModeThreshold == null) && (coolModeThreshold ==null)) {
		log.debug "adjust_tstat_for_more_less_heat_cool>no adjustment variables set, exiting"
		return
	}
	
	float outdoorTemp = outTempSensor?.currentTemperature.toFloat().round(1)
	String currentMode = thermostat.currentThermostatMode.toString()
	float currentHeatPoint = thermostat.currentHeatingSetpoint.toFloat().round(1)
	float currentCoolPoint = thermostat.currentCoolingSetpoint.toFloat().round(1)
	float targetTstatTemp    
	log.debug "adjust_tstat_for_more_less_heat_cool>currentMode=$currentMode,outdoorTemp=$outdoorTemp,moreCoolThreshold=$moreCoolThreshold,  moreHeatThreshold=$moreHeatThreshold," +
		"coolModeThreshold=$coolModeThreshold,heatModeThreshold=$heatModeThreshold,currentHeatSetpoint=$currentHeatPoint,currentCoolSetpoint=$currentCoolPoint"

	key = "givenMaxTempDiff$indiceSchedule"
	def givenMaxTempDiff = settings[key]
	def input_max_temp_diff = givenMaxTempDiff ?: (scale=='C')? 2: 5 // 2°C/5°F temp differential is applied by default

	float max_temp_diff = input_max_temp_diff.toFloat().round(1)
	if (currentMode== 'heat') {
		if ((moreHeatThreshold != null) && (outdoorTemp <= moreHeatThreshold?.toFloat()))  {
			targetTstatTemp = (currentHeatPoint + max_temp_diff).round(1)
			float temp_diff = (state?.scheduleHeatSetpoint - targetTstatTemp).toFloat().round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for more heat")
				targetTstatTemp = (state?.scheduleHeatSetpoint  + max_temp_diff).round(1)
			}
			send("ScheduleTstatZones>heating setPoint now= ${targetTstatTemp}°, outdoorTemp <=${moreHeatThreshold}°")
			thermostat.setHeatingSetpoint(targetTstatTemp)
		} else if ((heatModeThreshold != null) && (outdoorTemp >= heatModeThreshold?.toFloat())) {
        	
			targetTstatTemp = (currentHeatPoint - max_temp_diff).round(1)
			float temp_diff = (state?.scheduleHeatSetpoint - targetTstatTemp).toFloat().round(1)
			log.debug "adjust_tstat_for_more_less_heat_cool>temp_diff=$temp_diff, max_temp_diff=$max_temp_diff for heat mode" 
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for heat mode")
				targetTstatTemp = (state?.scheduleHeatSetpoint  - max_temp_diff).round(1)
			}
			thermostat.setHeatingSetpoint(targetTstatTemp)
			send("ScheduleTstatZones>heating setPoint now= ${targetTstatTemp}°, outdoorTemp >=${heatModeThreshold}°")
        
		} else {
			switch_thermostatMode(indiceSchedule)        
		}        
	}
	if (currentMode== 'cool') {
    
		if ((moreCoolThreshold != null) && (outdoorTemp >= moreCoolThreshold?.toFloat())) {
			targetTstatTemp = (currentCoolPoint - max_temp_diff).round(1)
			float temp_diff = (state?.scheduleCoolSetpoint - targetTstatTemp).toFloat().round(1)
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for more cool")
				targetTstatTemp = (state?.scheduleCoolSetpoint  - max_temp_diff).round(1)
			}
			thermostat.setCoolingSetpoint(targetTstatTemp)
			send("ScheduleTstatZones>cooling setPoint now= ${targetTstatTemp}°, outdoorTemp >=${moreCoolThreshold}°")
		} else if ((coolModeThreshold!=null) && (outdoorTemp <= coolModeThreshold?.toFloat())) {
			targetTstatTemp = (currentCoolPoint + max_temp_diff).round(1)
			float temp_diff = (state?.scheduleCoolSetpoint - targetTstatTemp).toFloat().round(1)
			if (temp_diff.abs() > max_temp_diff) {
				log.debug("adjust_tstat_for_more_less_heat_cool>schedule ${scheduleName}:max_temp_diff= ${max_temp_diff},temp_diff=${temp_diff},too much adjustment for cool mode")
				targetTstatTemp = (state?.scheduleCoolSetpoint  + max_temp_diff).round(1)
			}
			thermostat.setCoolingSetpoint(targetTstatTemp)
			send("ScheduleTstatZones>cooling setPoint now= ${targetTstatTemp}°, outdoorTemp <=${coolModeThreshold}°")
		} else {
        
			switch_thermostatMode(indiceSchedule)        
		}        
        
	} 
    // Check if auto mode needs to be switched to 'heat' or 'cool' based on thresholds
	if (currentMode== 'auto') {
		switch_thermostatMode(indiceSchedule)        
	}
}


private def adjust_thermostat_setpoint_in_zone(indiceSchedule) {
	float MIN_SETPOINT_ADJUSTMENT_IN_CELSIUS=0.5
	float MIN_SETPOINT_ADJUSTMENT_IN_FARENHEITS=1
	float desiredHeat, desiredCool, avg_indoor_temp
	def scale = getTemperatureScale()

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
		setAllRoomTstatsSettings(indiceSchedule,indiceZone) 
		if (setRoomThermostatsOnly) { // Does not want to set the main thermostat, only the room ones
			if (detailedNotif) {
				send("ScheduleTstatZones>schedule ${scheduleName},zone ${zoneName}: all room Tstats set and setRoomThermostatsOnlyFlag= true, continue...")
			}
		} else if (adjustmentTempFlag) {

			def indoorTemps = getAllTempsForAverage(indiceZone)
			indoor_all_zones_temps = indoor_all_zones_temps + indoorTemps
		}
	}
	if (setRoomThermostatsOnly) {
		log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}:all room Tstats set and setRoomThermostatsOnlyFlag= true,exiting")
		return				    
	}    
	//	Now will do an avg temp calculation based on all temp sensors to apply the desired temp settings at the main Tstat correctly

	float currentTemp = thermostat?.currentTemperature.toFloat().round(1)
	String mode = thermostat?.currentThermostatMode.toString()
	//	This is the avg indoor temp based on indoor temp sensors in all rooms in the zone
	log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},all temps collected from sensors=${indoor_all_zones_temps}")

	if (indoor_all_zones_temps != [] ) {
		avg_indoor_temp = (indoor_all_zones_temps.sum() / indoor_all_zones_temps.size()).round(1)
	} else {
		avg_indoor_temp = currentTemp
	}

	float temp_diff = (avg_indoor_temp - currentTemp).round(1)
	if (detailedNotif) {
		send("ScheduleTstatZones>schedule ${scheduleName}:avg temp= ${avg_indoor_temp},main Tstat's currentTemp= ${currentTemp},temp adjustment=${temp_diff.abs()}")
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
				send("ScheduleTstatZones>schedule ${scheduleName},avg_temp_diff=${temp_diff.abs()} > ${max_temp_diff} :adjusting fan mode as temp differential in zone is too big")				
				// set fan mode with overrideThreshold=true
				set_fan_mode(indiceSchedule, true)          
                
			}   
		}   
	}
     
	float min_setpoint_adjustment = (scale=='C') ? MIN_SETPOINT_ADJUSTMENT_IN_CELSIUS:MIN_SETPOINT_ADJUSTMENT_IN_FARENHEITS
	if ((scheduleName == state.lastScheduleLastName) && (temp_diff.abs() < min_setpoint_adjustment)) {  // adjust the temp only if temp diff is significant
		log.debug("adjust_thermostat_setpoint_in_zone>temperature adjustment (${temp_diff}°) between sensors is small, skipping it and exiting")
		if (detailedNotif) {
			send("ScheduleTsatZones>running ongoing schedule ${scheduleName}, temperature adjustment (${temp_diff}°) between sensors is not significant, skipping adjustment in zone")
		}
		return
	}                

	key = "givenClimate$indiceSchedule"
	def climateName = settings[key]
	if (mode == 'heat') {
	
		if ((climateName) && (thermostat.hasCommand("setClimate"))) {
			try {
				thermostat.setClimate("", climateName)
				thermostat.poll() // to get the latest setpoints               
			} catch (any) {
				if (detailedNotif) {
					send("ScheduleTstatZones>schedule ${scheduleName}:not able to set climate ${climateName} for heating at the thermostat ${thermostat}")
				}
				log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}:not able to set climate  ${climateName} for heating at the thermostat ${thermostat}")
			}                
			desiredHeat = thermostat.currentHeatingSetpoint
			log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},according to climateName ${climateName}, desiredHeat=${desiredHeat}")
		} else {
			log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}:no climate to be applied for heatingSetpoint")
			key = "desiredHeatTemp$indiceSchedule"
			def heatTemp = settings[key]
			if (!heatTemp) {
				log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}:about to apply default heat settings")
				desiredHeat = (scale=='C') ? 21:72 					// by default, 21°C/72°F is the target heat temp
			} else {
				desiredHeat = heatTemp.toFloat()
			}
			log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},desiredHeat=${desiredHeat}")
		} 
		temp_diff = (temp_diff < (0-max_temp_diff)) ? -(max_temp_diff):(temp_diff >max_temp_diff) ?max_temp_diff:temp_diff // determine the temp_diff based on max_temp_diff
		float targetTstatTemp = (desiredHeat - temp_diff).round(1)
		thermostat?.setHeatingSetpoint(targetTstatTemp)
		if (detailedNotif) {
			send("ScheduleTstatZones>schedule ${scheduleName},in zones=${zones},heating setPoint now =${targetTstatTemp}°,adjusted by avg temp diff (${temp_diff.abs()}°) between all temp sensors in zone")
		}
		if (scheduleName != state.lastScheduleLastName) {
			state.scheduleHeatSetpoint=desiredHeat // save the value for later processing in adjust_more_less_heat_cool()
		}        
        
	} else if (mode == 'cool') {

		if ((climateName) && (thermostat.hasCommand("setClimate"))) {
			try {
				thermostat?.setClimate("", climateName)
				thermostat.poll() // to get the latest setpoints               
			} catch (any) {
				if (detailedNotif) {
					send("ScheduleTstatZones>schedule ${scheduleName},not able to set climate ${climateName} for cooling at the thermostat(s) ${thermostat}")
				}
				log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},not able to set climate ${climateName} associated for cooling at the thermostat ${thermostat}")
			}                
			desiredCool = thermostat.currentCoolingSetpoint
			log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},according to climateName ${climateName}, desiredCool=${desiredCool}")
		} else {
			log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName}:no climate to be applied for coolingSetpoint")
			key = "desiredCoolTemp$indiceSchedule"
			def coolTemp = settings[key]
			if (!coolTemp) {
				log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},about to apply default cool settings")
				desiredCool = (scale=='C') ? 23:75					// by default, 23°C/75°F is the target cool temp
			} else {
            
				desiredCool = coolTemp.toFloat()
			}
            
			log.debug("adjust_thermostat_setpoint_in_zone>schedule ${scheduleName},desiredCool=${desiredCool}")
		} 
		temp_diff = (temp_diff < (0-max_temp_diff)) ? -(max_temp_diff):(temp_diff >max_temp_diff) ?max_temp_diff:temp_diff // determine the temp_diff based on max_temp_diff
		float targetTstatTemp = (desiredCool - temp_diff).round(1)
		thermostat?.setCoolingSetpoint(targetTstatTemp)
		if (detailedNotif) {
			send("ScheduleTstatZones>schedule ${scheduleName}, in zones=${zones},cooling setPoint now =${targetTstatTemp}°,adjusted by avg temp diff (${temp_diff}°) between all temp sensors in zone")
		}            
		if (scheduleName != state.lastScheduleLastName) {
			state.scheduleCoolSetpoint=desiredCool // save the value for later processing in adjust_more_less_heat_cool()
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
						switchLevel =MIN_OPEN_LEVEL_SMALL // setLevel at a minimum as the room is not occupied.
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
			send("ScheduleTstatZones>schedule ${scheduleName},set all ventSwitches at ${switchLevel}% to avoid closing all of them")
		}
	}    
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

/* Y.R. Now the check is done by an event handler
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
					log.debug("turn_off_all_other_vents>about to turn off ${ventSwitch} as requested to create the desired zone(s)")
				} else {
					try {                
						ventSwitch.refresh() 
					} catch (e) {
						log.debug("turn_off_all_other_vents>exception ${e} while trying to call refresh() on ${ventSwitch}")
					}                    
					def setLevel = ventSwitch.latestValue("level")
					if (setlevel < MIN_OPEN_LEVEL) {                    
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
		if (detailedNotif) {
			send("ScheduleTstatZones>ratio of closed vents is too high (${ratioClosedVents.round()}%), opening ${closedVentsSet} at minimum level of ${MIN_OPEN_LEVEL}%")
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
			send("ScheduleTstatZones> ** IMMEDIATE ATTENTION: current HVAC mode is ${currentHVACMode}, and inside temperature of vent ${ventSwitch} too hot (${tempSwitch}°), opening all vents and turning off the HVAC to avoid any damage **")
			return true            
		} /* if too hot */           
		if (((currentHVACMode=='cool') || (currentHVACMode == 'auto')) && (tempSwitch <= MIN_TEMP_VENT_SWITCH)) {
			// Turn the HVAC off, open all vents, and deactivate any further smartapp processing
			thermostat.off()            
			open_all_vents()
			powerSwitch?.off()            
			send("ScheduleTstatZones> ** IMMEDIATE ATTENTION: current HVAC mode is ${currentHVACMode}, and inside temperature of vent ${ventSwitch} too cold (${tempSwitch}°), opening all vents and turning off the HVAC to avoid any damage **")
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
				send("ScheduleTstatZones>set ${ventSwitch} at level ${switchLevel} in room ${roomName} to reach desired temperature")
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
	def key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key = "includedZones$indiceSchedule"
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


def IsRightDayForChange(indiceSchedule) {

	def key = "scheduleName$indiceSchedule"
	def scheduleName = settings[key]

	key ="dayOfWeek$indiceSchedule"
	def dayOfWeek = settings[key]
    
	def makeChange = false
	Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
	int currentDayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK);

	// Check the condition under which we want this to run now
	// This set allows the most flexibility.
	if (dayOfWeek == 'All Week') {
		makeChange = true
	} else if ((dayOfWeek == 'Monday' || dayOfWeek == 'Monday to Friday') && currentDayOfWeek == Calendar.instance.MONDAY) {
		makeChange = true
	} else if ((dayOfWeek == 'Tuesday' || dayOfWeek == 'Monday to Friday') && currentDayOfWeek == Calendar.instance.TUESDAY) {
		makeChange = true
	} else if ((dayOfWeek == 'Wednesday' || dayOfWeek == 'Monday to Friday') && currentDayOfWeek == Calendar.instance.WEDNESDAY) {
		makeChange = true
	} else if ((dayOfWeek == 'Thursday' || dayOfWeek == 'Monday to Friday') && currentDayOfWeek == Calendar.instance.THURSDAY) {
		makeChange = true
	} else if ((dayOfWeek == 'Friday' || dayOfWeek == 'Monday to Friday') && currentDayOfWeek == Calendar.instance.FRIDAY) {
		makeChange = true
	} else if ((dayOfWeek == 'Saturday' || dayOfWeek == 'Saturday & Sunday') && currentDayOfWeek == Calendar.instance.SATURDAY) {
		makeChange = true
	} else if ((dayOfWeek == 'Sunday' || dayOfWeek == 'Saturday & Sunday') && currentDayOfWeek == Calendar.instance.SUNDAY) {
		makeChange = true
	}

	log.debug "IsRightDayforChange>schedule ${scheduleName}, makeChange=${makeChange},Calendar DOW= ${currentDayOfWeek}, dayOfWeek=${dayOfWeek}"

	return makeChange
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

