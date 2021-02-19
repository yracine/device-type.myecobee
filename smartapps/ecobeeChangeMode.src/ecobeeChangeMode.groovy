/**
 *  ecobeeChangeMode
 *
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
	"Change the ecobee program manually (by pressing the app's play button) and automatically at the ecobee thermostat(s) based on the location mode(s)",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {


	page(name: "selectThermostats", title: "Thermostats", install: false , uninstall: true, nextPage: "selectProgram") {
		section("About") {
			paragraph "ecobeeChangeMode, the smartapp that sets your ecobee thermostat to a given program/climate ['Away', 'Home', 'Night']" + 
                		" based on the hub's location mode."
			paragraph "Version 2.2" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.me/ecomatiqhomes",
					title:"Paypal donation..."
			paragraph "Copyright©2014-2020 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Change the following ecobee thermostat(s)...") {
			input "thermostats", "capability.thermostat", title: "MyEcobee thermostat(s)", multiple: true
		}
        
	}
	page(name: "selectProgram", title: "Ecobee Programs", content: "selectProgram")
	page(name: "Notifications", title: "Notifications & other Options", install: true, uninstall: true) {
        	if (isST()) {    
	    		section("Notifications") {
		    		input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required:false
		    		input "phone", "phone", title: "Send a Text Message?", required: false
    			}	
                    
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
		section("When hub's location mode changes to (ex. 'Away', 'Home')[optional]") {
			input "newMode", "enum", options: enumModes, multiple:true, required: false
		}
		section("Or the following virtual/physical switch is turned on)[optional]") {
			input "aSwitch", "capability.switch", required: false, description: "Optional"
		}
		section("Enter a delay in minutes [optional, default=immediately after ST hello mode change] ") {
			input "delay", "number", title: "Delay in minutes [default=immediate]", description:"no delay by default",required:false
		}
		section("Or Do the mode change manually only (by pressing the arrow next to its name in Automation/Smartapps in the mobile app)") {
			input "manualFlag", "bool", title: "Manual only [default=false]", description:"optional",required:false
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
		if (newMode) {
			subscribe(location, "mode", changeMode)
		}            
		if (aSwitch) {
			subscribe(aSwitch, "switch.on", onHandler, [filterEvents: false])
		}
	} else {
		takeAction()  
	}    
	subscribe(app, appTouch)
}

def appTouch(evt) {
	log.debug ("changeMode>location.mode= $location.mode, givenClimate=${givenClimate}, about to takeAction")

	takeAction() 
}

def onHandler(evt) {
	log.debug "$evt.name: $evt.value"
	if (delay) {
		try {
			unschedule(takeAction)
		} catch (e) {
			log.debug ("ecobeeChangeMode>exception when trying to unschedule: $e")    
		}    
	}    
       
	if ((!delay) || (delay==null)) {
		log.debug ("onHandler>about to call takeAction() immediately")
		takeAction()    
	} else {
    
		log.debug ("onHandler>about to schedule takeAction() in $delay minutes ")
		runIn((delay*60), "takeAction")   
 	}    
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
		log.debug ("changeMode>about to call takeAction() immediately")
		takeAction()    
	} else {
		log.debug ("changeMode>about to schedule takeAction() in $delay minutes ")
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
