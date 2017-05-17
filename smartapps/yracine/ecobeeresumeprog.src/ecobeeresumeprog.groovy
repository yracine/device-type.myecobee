/***
 *  Copyright 2014 Yves Racine
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
 *  Resume Ecobee's Program when people arrive or there is been recent motion at home
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
 
// Automatically generated. Make future change here.
definition(
	name: "ecobeeResumeProg",
	namespace: "yracine",
	author: "Yves Racine",
	description: "resumes your ecobee's scheduled program when a presence is back home or when motion is detected or when a ST hello mode is changed",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {

	page(name: "About", title: "About", install: false , uninstall: true, nextPage: "selectThermostats") {
		section("About") {
			paragraph "ecobeeResumeProg, the smartapp that resumes your ecobee's scheduled program when a presence is back home,or when motion is detected or when a ST hello mode is changed"
			paragraph "Version 2.1.4" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.me/ecomatiqhomes",
					title:"Paypal donation..."
			paragraph "Copyright©2014 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
	}        
	page(name: "selectThermostats", title: "Thermostats", install: false , uninstall: false, nextPage: "selectModes") {
		section("Resume Program at the ecobee thermostat(s)") {
			input "ecobee", "device.myEcobeeDevice", title: "Ecobee Thermostat(s)", multiple: true
		}
		section("When one of these people arrive at home") {
			input "people", "capability.presenceSensor", multiple: true, required:false
		}
		section("Or there is motion at home on these sensors [optional]") {
			input "motions", "capability.motionSensor", title: "Where?", multiple: true, required: false
		}
		section("False alarm threshold [defaults = 3 minutes]") {
			input "falseAlarmThreshold", "decimal", title: "Number of minutes", required: false
		}
	}        
	page(name: "selectModes", title: "Select Hello ST modes", content: "selectModes")
	page(name: "Notifications", title: "Notifications Options", install: true, uninstall: false) {
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required:
				false
			input "phone", "phone", title: "Send a Text Message?", required: false
		}
        section([mobileOnly:true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
	}

}



def selectModes() {
	def enumModes=[]
	location.modes.each {
		enumModes << it.name
	}    

	return dynamicPage(name: "selectModes", title: "Select Modes", install: false, uninstall: false, nextPage:
			"Notifications") {
		section("Or when SmartThings' hello home mode changes to (ex.'Home')[optional]") {
			input "newMode", "enum", options: enumModes, multiple:true, required: false
		}
	}
}


def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
	subscribe(location, changeMode)
	subscribe(app, changeMode)
	subscribe(people, "presence", presence)
	subscribe(motions, "motion", motionEvtHandler)

}

def motionEvtHandler(evt) {
	if ((evt.value == "active") && residentsHaveJustBeenActive()) {
		message = "EcobeeResumeProg>Recent motion just detected at home, do it"
		log.info message
		send(message)
		takeActions()
	}
}

private residentsHaveJustBeenActive() {
	def threshold = (falseAlarmThreshold != null && falseAlarmThreshold != "") ? (falseAlarmThreshold*60*1000) as Long : 3*60*1000L
	def result = true
	def t0 = new Date(now() - threshold)
	for (sensor in motions) {
		def recentStates = sensor.statesSince("motion", t0)
		if (recentStates.find {it.value == "active"}) {
			result = false
			break
		}
	}
	log.debug "residentsHaveJustBeenActive: $result"
	return result
}

def changeMode(evt) {

	Boolean foundMode=false        
	newMode.each {
        
		if (it==location.mode) {
			foundMode=true            
		}            
	}        
        
	if (!foundMode) {
		log.debug "changeMode>location.mode= $location.mode, newMode=${newMode},foundMode=${foundMode}, not resuming program"
		return			
	}
	takeActions()
}

def presence(evt) {
	log.debug "evt.name: $evt.value"
	def threshold = (falseAlarmThreshold != null && falseAlarmThreshold != "") ? (falseAlarmThreshold*60*1000) as Long : 3*60*1000L
	def message = null

	if ((location.mode == newMode) || (newMode == null) || (newMode.trim() == '')) {
		def t0 = new Date(now() - threshold)
		if (evt.value == "present") {

			def person = getPerson(evt)
			if (person != null) {
				def recentNotPresent = person.statesSince("presence", t0).find {
					it.value == "not present"
				}
				if (!recentNotPresent) {
					message = "EcobeeResumeProg> ${person.displayName} just arrived,take actions.."
					log.info message
					send(message)
					takeActions()
				}
			} else {
				message = "EcobeeResumeProg> Somebody just arrived,take actions.."
				log.info message
				send(message)
				takeActions()

			}
		}
	} else {
		log.debug "mode is not the same, not evaluating"
	}

}

def takeActions() {
	def message = "EcobeeResumeProg>resumed program at ecobee..."
	ecobee.resumeThisTstat()
	send(message)
}


private getPerson(evt) {
	people.find {
		evt.deviceId == it.id
	}
}

private send(msg) {
	if (sendPushMessage != "No") {
		log.debug("sending push message")
		sendPush(msg)
	}
	if (phone) {
		log.debug("sending text message")
		sendSms(phone, msg)
	}

	log.debug msg
}