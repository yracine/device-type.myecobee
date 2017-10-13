/**
 *  ecobeeSetFanMinOnTime
 *
 *  Copyright 2015 Yves Racine
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
 */
definition(
    name: "ecobeeSetFanMinOnTime",
    namespace: "yracine",
    author: "Yves Racine",
    description: "ecobeeSetFanMinOnTime, the smartapp that sets your ecobee's fan to circulate for a minimum time (in minutes) per hour",
    category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)
preferences {


	page(name: "selectThermostats", title: "Thermostats", install: false , uninstall: true, nextPage: "selectProgram") {
		section("About") {
			paragraph "ecobeeSetFanMinOnTime, the smartapp that sets your ecobee's fan to circulate for a minimum time (in minutes) per hour." 
			paragraph "Version 1.4" 
			paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
				href url: "https://www.paypal.me/ecomatiqhomes"
					title:"Paypal donation..."
			paragraph "Copyright©2015 Yves Racine"
				href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
					description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
		}
		section("Change the following ecobee thermostat(s)...") {
			input "thermostats", "device.myEcobeeDevice", title: "Which thermostat(s)", multiple: true
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

	return dynamicPage(name: "selectProgram", title: "Select Ecobee Program", install: false, uninstall: true, nextPage:
			"Notifications") {
		section("Select Program") {
			input "givenClimate", "enum", title: "When change to this ecobee program?", options: ecobeePrograms, required: true
		}
		section("Set the minimum fan runtime per hour for this program [default: 10 min. per hour]") {
			input "givenFanMinTime", "number", title: "Minimum fan runtime in minutes", required: false
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

def initialize()  {
	subscribe(thermostats,"setClimate",changeFanMinOnTime)    
	subscribe(thermostats,"climateName",changeFanMinOnTime)    
	subscribe(app, changeFanMinOnTime)


}



def changeFanMinOnTime(evt) {


	if ((evt.value != givenClimate) && (evt.value != 'touch')) {
		log.debug ("changeFanMinOnTime>not right climate (${evt.value}), doing nothing...")
		return
	}    
	Integer min_fan_time = (givenFanMinTime != null) ? givenFanMinTime : 10 //  10 min. fan time per hour by default
    
	def message = "ecobeeSetFanMinOnTime>changing fanMinOnTime to ${min_fan_time} at ${thermostats}.."
	send(message)

	thermostats.each {
		it?.setThermostatSettings("", ['fanMinOnTime': "${min_fan_time}"])
	}
}

private send(msg) {
	if (sendPushMessage == "Yes") {
		log.debug("sending push message")
		sendPush(msg)
	}
	if (phone) {
		log.debug("sending text message")
		sendSms(phone, msg)
	}

	log.debug msg
}
