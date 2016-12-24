/**
 *  ecobeeManageClimate
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
 *  N.B. Requires MyEcobee device available at 
 *          http://www.ecomatiqhomes.com/#!store/tc3yr 
 */
definition(
	name: "ecobeeManageClimate",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Allows a user to manage ecobee's climates",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)


preferences {
	section("About") {
		paragraph "ecobeeManageClimate, the smartapp that manages your ecobee climates ['creation', 'update', 'delete']" 
		paragraph "Version 1.9.5"
		paragraph "If you like this smartapp, please support the developer via PayPal and click on the Paypal link below " 
			href url: "https://www.paypal.me/ecomatiqhomes",
				title:"Paypal donation..."
		paragraph "Copyright©2014 Yves Racine"
			href url:"http://github.com/yracine/device-type.myecobee", style:"embedded", required:false, title:"More information..."  
				description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("For this ecobee thermostat") {
		input "ecobee", "device.myEcobeeDevice", title: "Ecobee Thermostat"
	}
	section("Create (if not present) or update this climate") {
		input "climateName", "text", title: "Climate Name"
	}
	section("Or delete the Climate [default=false]") {
		input "deleteClimate", "Boolean", title: "delete?", metadata: [values: ["true", "false"]], required: false
	}
	section("Substitute Climate name in schedule (used for delete)") {
		input "subClimateName", "text", title: "Climate Name", required: false
	}
	section("Cool Temp [default = 75°F/23°C]") {
		input "givenCoolTemp", "decimal", title: "Cool Temp", required: false
	}
	section("Heat Temp [default=72°F/21°C]") {
		input "givenHeatTemp", "decimal", title: "Heat Temp", required: false
	}
	section("isOptimized [default=false]") {
		input "isOptimizedFlag", "Boolean", title: "isOptimized?", metadata: [values: ["true", "false"]], required: false
	}
	section("isOccupied [default=false]") {
		input "isOccupiedFlag", "Boolean", title: "isOccupied?", metadata: [values: ["true", "false"]], required: false
	}
	section("Cool Fan Mode [default=auto]") {
		input "givenCoolFanMode", "enum", title: "Cool Fan Mode ?", metadata: [values: ["auto", "on"]], required: false
	}
	section("Heat Fan Mode [default=auto]") {
		input "givenHeatFanMode", "enum", title: "Heat Fan Mode ?", metadata: [values: ["auto", "on"]], required: false
	}
	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
		input "phoneNumber", "phone", title: "Send a text message?", required: false
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
	def scale = getTemperatureScale()
	def heatTemp, coolTemp
	if (scale == 'C') {
		heatTemp = givenHeatTemp ?: 21 // by default, 21°C is the heat temp
		coolTemp = givenCoolTemp ?: 23 // by default, 23°C is the cool temp
	} else {
		heatTemp = givenHeatTemp ?: 72 // by default, 72°F is the heat temp
		coolTemp = givenCoolTemp ?: 75 // by default, 75°F is the cool temp
	}

	def isOptimized = (isOptimizedFlag != null) ? isOptimizedFlag : false // by default, isOptimized flag is false
	def isOccupied = (isOccupiedFlag != null) ? isOccupiedFlag : false // by default, isOccupied flag is false
	def coolFanMode = givenCoolFanMode ?: 'auto' // By default, fanMode is auto
	def heatFanMode = givenHeatFanMode ?: 'auto' // By default, fanMode is auto
	def deleteClimateFlag = (deleteClimate != null) ? deleteClimate : 'false'

	log.debug "ecobeeManageClimate> about to take actions"


	log.trace "ecobeeManageClimate>climateName =${climateName},deleteClimateFlag =${deleteClimateFlag},subClimateName= ${subClimateName}, isOptimized=${isOptimized}" +
		",isOccupied=${isOccupied},coolTemp = ${coolTemp},heatTemp = ${heatTemp},coolFanMode= ${coolFanMode}, heatFanMode= ${heatFanMode}"

	if (deleteClimateFlag == 'true') {
		send("ecobeeManageClimate>about to delete climateName = ${climateName}")
		ecobee.deleteClimate("", climateName, subClimateName)

	} else {

		send("ecobeeManageClimate>about to create or update climateName = ${climateName}")
		ecobee.updateClimate("", climateName, deleteClimateFlag, subClimateName,
			coolTemp, heatTemp, isOptimized, isOccupied, coolFanMode, heatFanMode)
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
