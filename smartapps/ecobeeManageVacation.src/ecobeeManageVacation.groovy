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
 *  Manage Vacation events at Ecobee Thermostat(s)
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */

// Automatically generated. Make future change here.
definition(
	name: "ecobeeManageVacation",
	namespace: "yracine",
	author: "Yves Racine",
	description: "manages your ecobee vacation settings ['creation', 'update', 'delete']",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "ecobeeManageVacation, the smartapp that manages your ecobee vacation settings ['creation', 'update', 'delete']"
		paragraph "Version 1.9.3" 
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.me/ecomatiqhomes",
				title:"Paypal donation..."
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("For the Ecobee thermostat(s)") {
		input "ecobee", "device.myEcobeeDevice", title: "Ecobee Thermostat", multiple:true
	}
	section("Create this Vacation Name") {
		input "vacationName", "text", title: "Vacation Name"
	}
    
	section("Or delete the vacation [default=false]") {
		input "deleteVacation", "Boolean", title: "delete?", metadata: [values: ["true", "false"]], required: false
	}
	section("Cool Temp for vacation, [default = 80°F/27°C]") {
		input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
	}
	section("Heat Temp for vacation [default= 60°F/14°C]") {
		input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
	}
	section("Start date for the vacation, [format = DD-MM-YYYY]") {
		input "givenStartDate", "text", title: "Beginning Date"
	}
	section("Start time for the vacation [HH:MM 24HR]") {
		input "givenStartTime", "text", title: "Beginning time"
	}
	section("End date for the vacation [format = DD-MM-YYYY]") {
		input "givenEndDate", "text", title: "End Date"
	}
	section("End time for the vacation [HH:MM 24HR]") {
		input "givenEndTime", "text", title: "End time"
	}


}



def installed() {

	subscribe(app, appTouch)
	takeAction()

}


def updated() {

	unsubscribe()
	subscribe(app, appTouch)
	takeAction()


}

def appTouch(evt) {
	takeAction()
}
def takeAction() {    
	log.debug "ecobeeManageVacation> about to take actions"
	def minHeatTemp, minCoolTemp
	def scale = getTemperatureScale()
	if (scale == 'C') {
		minHeatTemp = givenHeatTemp ?: 14 // by default, 14°C is the minimum heat temp
		minCoolTemp = givenCoolTemp ?: 27 // by default, 27°C is the minimum cool temp
	} else {
		minHeatTemp = givenHeatTemp ?: 60 // by default, 60°F is the minimum heat temp
		minCoolTemp = givenCoolTemp ?: 80 // by default, 80°F is the minimum cool temp
	}
	def vacationStartDateTime = null
	String dateTime = null

	dateTime = givenStartDate + " " + givenStartTime
	log.debug("Start datetime= ${datetime}")
	vacationStartDateTime = new Date().parse('d-M-yyyy H:m', dateTime)

	dateTime = givenEndDate + " " + givenEndTime
	log.debug("End datetime= ${datetime}")
	def vacationEndDateTime = new Date().parse('d-M-yyyy H:m', dateTime)

	if (deleteVacation == 'false') {
		// You may want to change to ecobee.createVacation('serial number list',....) if you own EMS thermostat(s)

		log.debug("About to call iterateCreateVacation for ${vacationName}")
		ecobee.each {        
			it.createVacation("", vacationName, minCoolTemp, minHeatTemp, vacationStartDateTime,
			vacationEndDateTime)
		}            
		send("ecobeeManageVacation> vacationName ${vacationName} created at ${ecobee}")
	} else {
		ecobee.each {
			it.deleteVacation("", vacationName)
		}                
		send("ecobeeManageVacation> vacationName ${vacationName} deleted at ${ecobee}")

	}

}


private send(msg) {
	if (sendPushMessage != "No") {
		log.debug("sending push message")
		sendPush(msg)
	}

	if (phoneNumber) {
		log.debug("sending text message")
		sendSms(phoneNumber, msg)
	}

	log.debug msg
}
