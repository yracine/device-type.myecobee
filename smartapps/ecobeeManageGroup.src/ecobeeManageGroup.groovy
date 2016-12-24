/**
 *  EcobeeManageGroup
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
	name: "ecobeeManageGroup",
	namespace: "yracine",
	author: "Yves Racine",
	description: "Allows a user to create,update, and delete an ecobee group",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "ecobeeManageGroup, the smartapp that manages your ecobee groups ['creation', 'update', 'delete']"
		paragraph "Version 1.9.4" 
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
	section("Create this group or update all groups [empty for all group]") {
		input "groupName", "text", title: "Group Name", required: false
	}
	section("Or delete the group [default=false]") {
		input "deleteGroup", "Boolean", title: "delete?", metadata: [values: ["true", "false"]], required: false
	}
	section("Synchronize Vacation") {
		input "synchronizeVacation", "Boolean", title: "synchronize Vacation [default=false]?", metadata: [values: ["true", "false"]], required: false
	}
	section("Synchronize Schedule") {
		input "synchronizeSchedule", "Boolean", title: "synchronize Schedule [default=false]?", metadata: [values: ["true", "false"]], required: false
	}
	section("Synchronize SystemMode") {
		input "synchronizeSystemMode", "Boolean", title: "synchronize SystemMode [default=false]?", metadata: [values: ["true", "false"]], required: false
	}
	section("Synchronize Alerts") {
		input "synchronizeAlerts", "Boolean", title: "synchronize Alerts [default=false]?", metadata: [values: ["true", "false"]], required: false
	}
	section("Synchronize QuickSave") {
		input "synchronizeQuickSave", "Boolean", title: "synchronize QuickSave [default=false]?", metadata: [values: ["true", "false"]], required: false
	}
	section("Synchronize User Preferences") {
		input "synchronizeUserPreferences", "Boolean", title: "synchronize User Preferences [default=false]?", metadata: [values: ["true", "false"]], required: false
	}

	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata: [values: ["Yes", "No"]], required: false
		input "phoneNumber", "phone", title: "Send a text message?", required: false
	}




}



def installed() {

	ecobee.poll()
	subscribe(app, appTouch)
	takeAction()
}


def updated() {

	unsubscribe()

	ecobee.poll()
	subscribe(app, appTouch)
	takeAction()    


}

def appTouch(evt) {
	takeAction()
}
    

def takeAction() {
	// by default,  all flags are set to false

	log.debug "EcobeeManageGroup> about to take actions"
	def syncVacationFlag = (synchronizeVacation != null) ? synchronizeVacation : 'false'
	def syncScheduleFlag = (synchronizeSchedule != null) ? synchronizeSchedule : 'false'
	def syncSystemModeFlag = (synchronizeSystemMode != null) ? synchronizeSystemMode : 'false'
	def syncAlertsFlag = (synchronizeAlerts != null) ? synchronizeAlerts : 'false'
	def syncQuickSaveFlag = (synchronizeQuickSave != null) ? synchronizeQuickSave : 'false'
	def syncUserPreferencesFlag = (synchronizeUserPreferences != null) ? synchronizeUserPreferences : 'false'
	def deleteGroupFlag = (deleteGroup != null) ? deleteGroup : 'false'

	def groupSettings = [synchronizeVacation: "${syncVacationFlag}", synchronizeSchedule: "${syncScheduleFlag}",
		synchronizeSystemMode: "${syncSystemModeFlag}", synchronizeAlerts: "${syncAlertsFlag}",
		synchronizeQuickSave: "${syncQuickSaveFlag}", synchronizeUserPreferences: "${syncUserPreferencesFlag}"
	]

	log.trace "ecobeeManageGroup>deleteGroupFlag=${deleteGroupFlag},  groupName = ${groupName}, groupSettings= ${groupSettings}"

	if (deleteGroupFlag == 'true') {
		ecobee.deleteGroup(null, groupName)
		send("ecobeeManageGroup>groupName=${groupName} deleted")

	} else if ((groupName != "") && (groupName != null)) {

		ecobee.createGroup(groupName, null, groupSettings)
		send("ecobeeManageGroup>groupName=${groupName} created")
	} else {
		ecobee.iterateUpdateGroup(null, groupSettings)
		send("ecobeeManageGroup>all groups associated with thermostat updated with ${groupSettings}")
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
