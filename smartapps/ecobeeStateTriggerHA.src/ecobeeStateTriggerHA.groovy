/**
 * ecobeeStateTriggerHA
 *
 *  Copyright 2017 Yves Racine
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
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 *
 */
definition(
	name: "${get_APP_NAME()}",
	namespace: "yracine",
	author: "Yves Racine",
	description: "ecobeeStateTriggerHA, the ecobee smartapp that can trigger a switch or a routine based on some given operating states (heating, cooling,fan only,idle)", 
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)


def get_APP_VERSION() {return "1.2"}

preferences {

	page(name: "HASettingsPage", title: "Home Automation Settings")
	page(name: "otherSettings", title: "OtherSettings")

}

def HASettingsPage() {
	def phrases = location.helloHome?.getPhrases()*.label

	dynamicPage(name: "HASettingsPage", install: false, uninstall: true, nextPage: "otherSettings") {
		section("About") {
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.me/ecomatiqhomes",
				title:"Paypal donation..."
			paragraph "Version ${get_APP_VERSION()}"
			paragraph "Copyright©2017 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("For this ecobee thermostat") {
			input (name:"thermostat", type: "capability.thermostat", title: "My Ecobee Thermostat")
		}
		section("And, when the following operatingStates are triggered") {
			input "givenEvents", "enum",
				title: "Which Events(s)?",
				multiple: true,
				required: true,
				metadata: [
					values: [
						'cooling',
						'heating',
						'fan only',
						'idle'
					]    
				]    

		}
		section("Turn on/off or Flash the following switch(es) [optional]") {
			input "switches", "capability.switch", required:false, multiple: true, title: "Which switch(es)?"
			input "switchMode", "enum", metadata: [values: ["Flash", "Turn On","Turn Off"]], required: false, defaultValue: "Turn On", title: "Action?"
		}
		section("Select Routine for Execution [optional]") {
			input "phrase", "enum", title: "Routine?", required: false, options: phrases
		}
	} /* end of dynamic page */
}


def otherSettings() {
	dynamicPage(name: "otherSettings", title: "Other Settings", install: true, uninstall: false) {
		section("Notifications") {
			input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required:
				false
			input "phoneNumber", "phone", title: "Send a text message?", required: false
		}
		section("Detailed Notifications") {
			input "detailedNotif", "bool", title: "Detailed Notifications?", required:false
		}
		section("Enable Amazon Echo/Ask Alexa Notifications [optional, default=false]") {
			input (name:"askAlexaFlag", title: "Ask Alexa verbal Notifications?", type:"bool",
				description:"optional",required:false)
			input (name:"listOfMQs",  type:"enum", title: "List of the Ask Alexa Message Queues (default=Primary)", options: state?.askAlexaMQ, multiple: true, required: false,
				description:"optional")            
			input "AskAlexaExpiresInDays", "number", title: "Ask Alexa's messages expiration in days (default=2 days)?", required: false
		}
		section([mobileOnly: true]) {
			label title: "Assign a name for this SmartApp", required: false
		}
	}
}




def installed() {
	initialize()
}

def updated() {
	try {
		unschedule()
	} catch (e) {
		log.debug ("updated>exception $e while trying to call unschedule") 
	}    
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(thermostat, "thermostatOperatingState", thermostatOperatingHandler)
	subscribe(location, "askAlexaMQ", askAlexaMQHandler)
}

def askAlexaMQHandler(evt) {
	if (!evt) return
	switch (evt.value) {
		case "refresh":
		state?.askAlexaMQ = evt.jsonData && evt.jsonData?.queues ? evt.jsonData.queues : []
		log.info("askAlexaMQHandler>refresh value=$state?.askAlexaMQ")        
		break
	}
}

def thermostatOperatingHandler(evt) {
	def msg = "${thermostat} has triggered ${evt.value} event..."
	if (detailedNotif) {
		log.debug msg
		send (msg,settings.askAlexaFlag)    
	}
	check_event(evt.value)
}


private boolean check_event(eventType) {
	def msg
  	boolean foundEvent=false  
    
	if (detailedNotif) {
		log.debug "check_event>eventType=${eventType}, givenEvents list=${givenEvents}"
	}        
	if ((givenEvents.contains(eventType))) {
		foundEvent=true    
		msg = "${thermostat} has triggered ${eventType}, about to ${switchMode} ${switches}"
		if (detailedNotif) {
			log.debug msg
		}
		send (msg,settings.askAlexaFlag)    
		        
		if (switches) {
			if (switchMode?.equals("Turn On")) {
				switches.on()
			} else if (switchMode?.equals("Turn Off")) {
				switches.off()
			} else {
				flashLights()
			}	
		}
		if (phrase) {
			msg = "${thermostat} has triggered ${eventType}, about to trigger $phrase routine"
			if (detailedNotif) {
				log.debug msg
			}
			send (msg,settings.askAlexaFlag)    
			location.helloHome?.execute(phrase)        
		}        
	}
	return foundEvent
}


private flashLights() {
	def doFlash = true
	def onFor = onFor ?: 1000
	def offFor = offFor ?: 1000
	def numFlashes = numFlashes ?: 3

	if (detailedNotif) {
		log.debug "LAST ACTIVATED IS: ${state.lastActivated}"
	}        
	if (state.lastActivated) {
		def elapsed = now() - state.lastActivated
		def sequenceTime = (numFlashes + 1) * (onFor + offFor)
		doFlash = elapsed > sequenceTime
		if (detailedNotif) {
			log.debug "DO FLASH: $doFlash, ELAPSED: $elapsed, LAST ACTIVATED: ${state.lastActivated}"
		}            
	}

	if (doFlash) {
		if (detailedNotif) {
			log.debug "FLASHING $numFlashes times"
		}            
		state.lastActivated = now()
		if (detailedNotif) {
			log.debug "LAST ACTIVATED SET TO: ${state.lastActivated}"
		}            
		def initialActionOn = switches.collect {
			it.currentSwitch != "on"
		}
		int delay = 1 
		numFlashes.times {
			log.trace "Switch on after  $delay msec"
			switches.eachWithIndex {
				s, i ->
					if (initialActionOn[i]) {
						s.on(delay: delay)
					} else {
						s.off(delay: delay)
					}
			}
			delay += onFor
			if (detailedNotif) {
				log.trace "Switch off after $delay msec"
			}                
			switches.eachWithIndex {
				s, i ->
					if (initialActionOn[i]) {
						s.off(delay: delay)
					} else {
						s.on(delay: delay)
					}
			}
			delay += offFor
		}
	}
}




private send(msg, askAlexa=false) {
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

	if (phone) {
		log.debug("sending text message")
		sendSms(phone, message)
	}
    
	log.debug msg
    
}
private def get_APP_NAME() {
	return "ecobeeStateTriggerHA"
}
