/**
 *  ecobeeChangeMode
 *
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
 * Change the mode manually (by pressing the app's play button) and automatically at the ecobee thermostat(s)
 * If you need to set it for both Away and Home modes, you'd need to save them as 2 distinct apps
 * Don't forget to set the app to run only for the target mode.
 *
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
definition(
	name: "ecobeeChangeMode",
	namespace: "yracine",
	author: "Yves Racine",
	description:
	"Change the ecobee program manually (by pressing the app's play button) and automatically at the ecobee thermostat(s) based on the ST hello mode(s)",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {


	page(name: "selectThermostats", title: "Thermostats", install: false , uninstall: true, nextPage: "selectProgram") {
		section("About") {
			paragraph "ecobeeChangeMode, the smartapp that sets your ecobee thermostat to a given program/climate ['Away', 'Home', 'Night']" + 
                		" based on ST hello mode."
			paragraph "Version 1.9.9a" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.me/ecomatiqhomes",
					title:"Paypal donation..."
			paragraph "Copyright©2014 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Change the following ecobee thermostat(s)...") {
			input "thermostats", "device.myEcobeeDevice", title: "Which thermostat(s)", multiple: true
		}
		section("Do the mode change manually only (by pressing the arrow to execute the smartapp)") {
			input "manualFlag", "bool", title: "Manual only [default=false]", description:"optional",required:false
		}
        
	}
	page(name: "selectProgram", title: "Ecobee Programs", content: "selectProgram")
	page(name: "Notifications", title: "Notifications Options", install: true, uninstall: true) {
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


def selectProgram() {
    def ecobeePrograms = thermostats[0].currentClimateList.toString().minus('[').minus(']').tokenize(',')
	log.debug "programs: $ecobeePrograms"
	def enumModes=[]
	location.modes.each {
		enumModes << it.name
	}    

	return dynamicPage(name: "selectProgram", title: "Select Ecobee Program", install: false, uninstall: true, nextPage:
			"Notifications") {
		section("Select Program") {
			input "givenClimate", "enum", title: "Change to this program?", options: ecobeePrograms, required: true
		}
		section("When SmartThings' hello home mode changes to (ex. 'Away', 'Home')[optional]") {
			input "newMode", "enum", options: enumModes, multiple:true, required: false
		}
		section("Enter a delay in minutes [optional, default=immediately after ST hello mode change] ") {
			input "delay", "number", title: "Delay in minutes [default=immediate]", description:"no delay by default",required:false
		}

        
	}
}


def installed() {
	initialize()	
}

def updated() {
	unsubscribe()
	initialize()	
}

private def initialize() {

	if (!manualFlag) {
		subscribe(location, "mode", changeMode)
	} else {
		takeAction()  
	}    
	subscribe(app, appTouch)
}

def appTouch(evt) {
	log.debug ("changeMode>location.mode= $location.mode, givenClimate=${givenClimate}, about to takeAction")

	takeAction() 
}


def changeMode(evt) {
	def message


	if (delay) {
		try {
			unschedule(takeAction)
		} catch (e) {
			log.debug ("ecobeeChangeMode>exception when trying to unschedule: $e")    
		}    
	}    
    
	Boolean foundMode=false        
	newMode.each {
        
		if (it==location.mode) {
			foundMode=true            
		}            
	}        
	log.debug ("changeMode>location.mode= $location.mode, newMode=${newMode}")
        
	if ((newMode != null) && (!foundMode)) {
        
		log.debug "changeMode>location.mode= $location.mode, newMode=${newMode},foundMode=${foundMode}, not doing anything"
		return			
	}

	if ((!delay) || (delay==null)) {
		log.debug ("changeMode>about to call takeAction()")
		takeAction()    
	} else {
		runIn((delay*60), "takeAction")   
 	}    
}

private void takeAction() {
	def message = "ecobeeChangeMode>setting ${thermostats} to ${givenClimate}.."
	send(message)
	log.debug (message)
    
	thermostats.each {
		it?.setThisTstatClimate(givenClimate)
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
