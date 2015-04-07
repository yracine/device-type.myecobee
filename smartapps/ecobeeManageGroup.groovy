/**
 *  EcobeeManageGroup
 *
 *  Copyright 2014 Yves Racine
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
		paragraph "Version 1.9\n\n" +
			"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
			"Copyright©2014 Yves Racine"
		href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
		description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}
	section("For this ecobee thermostat") {
		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
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

}


def updated() {


	ecobee.poll()
	subscribe(app, appTouch)


}

def appTouch(evt) {
	log.debug "EcobeeManageGroup> about to take actions"

	// by default,  all flags are set to false

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
