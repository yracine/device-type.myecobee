/**
 *  ecobeeGenerateStats
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
import java.text.SimpleDateFormat 
definition(
    name: "ecobeeGenerateStats",
    namespace: "yracine",
    author: "Yves Racine",
    description: "This smartapp allows a ST user to generate runtime stats (daily by scheduling or based on custom dates) on their devices controlled by ecobee such as a heating & cooling component,fan, dehumidifier/humidifier/HRV/ERV. ",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
	section("About") {
		paragraph "ecobeeGenerateStats, the smartapp that generates daily runtime reports about your ecobee components"
		paragraph "Version 1.9.3\n\n" +
			"If you like this app, please support the developer via PayPal:\n\nyracine@yahoo.com\n\n" +
			"Copyright©2014 Yves Racine"
		href url: "http://github.com/yracine", style: "embedded", required: false, title: "More information...",
		description: "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
	}

	section("Generate daily stats for this ecobee thermostat") {
		input "ecobee", "device.myEcobeeDevice", title: "Ecobee?"

	}
	section("Start date for the initial run, format = YYYY-MM-DD") {
		input "givenStartDate", "text", title: "Beginning Date"
	}        
	section("Start time for initial run HH:MM (24HR)") {
		input "givenStartTime", "text", title: "Beginning time"	
	}        
	section("End date for the initial run = YYYY-MM-DD") {
		input "givenEndDate", "text", title: "End Date"
	}        
	section("End time for the initial run (24HR)" ) {
		input "givenEndTime", "text", title: "End time"
	}        
	section( "Notifications" ) {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes", "No"]], required: false
		input "phoneNumber", "phone", title: "Send a text message?", required: false
    }
	section("Detailed Notifications") {
		input "detailedNotif", "Boolean", title: "Detailed Notifications?",metadata:[values:["true", "false"]], required:false
    }
    
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	unschedule()    
	initialize()
}

def initialize() {
	// generate the stats every day at 1:00 (am)

	schedule("0 0 1 * * ?", dailyRun)    
	runIn((1*60),	"generateStats") // run 1 minute later as it requires notification.     
	subscribe(app, appTouch)
    
}

def appTouch(evt) {
	generateStats()
}

void dailyRun() {
	settings.givenStartDate=null
	settings.givenStartTime=null
	settings.givenEndDate=null
	settings.givenEndTime=null
	generateStats()
    
}


private String formatDateInLocalTime(dateInString, timezone='') {
	def myTimezone=(timezone)?TimeZone.getTimeZone(timezone):location.timeZone 
	if ((dateInString==null) || (dateInString.trim()=="")) {
		return (new Date().format("yyyy-MM-dd HH:mm:ss", myTimezone))
	}    
	SimpleDateFormat ISODateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
	Date ISODate = ISODateFormat.parse(dateInString)
	String dateInLocalTime =new Date(ISODate.getTime()).format("yyyy-MM-dd HH:mm:ss", myTimezone)
	log.debug("formatDateInLocalTime>dateInString=$dateInString, dateInLocalTime=$dateInLocalTime")    
	return dateInLocalTime
}


private def formatDate(dateString) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm zzz")
	Date aDate = sdf.parse(dateString)
	return aDate
}


void generateStats() {
    
	String dateInLocalTime = new Date().format("yyyy-MM-dd", location.timeZone) 
	String timezone = new Date().format("zzz", location.timeZone)
	String dateAtMidnight = dateInLocalTime + " 00:00 " + timezone    
	if (settings.trace) {
		log.debug("get_all_detailed_trips_info>date at Midnight in UTC= ${dateAtMidnight}")
	}
	Date endDate = formatDate(dateAtMidnight) 
	Date startDate = endDate -1
    
	if (settings.givenStartDate != null) { 
		String dateTime = givenStartDate + " " + givenStartTime + " " + timezone
		log.debug( "Start datetime= ${dateTime}" )
 		startDate = formatDate(dateTime)
		dateTime = givenEndDate  + " " + givenEndTime + " " + timezone
		log.debug( "End datetime= ${dateTime}" )
		endDate = formatDate(dateTime)
	}
	String nowInLocalTime = new Date().format("yyyy-MM-dd HH:mm", location.timeZone)
	log.debug("local date/time= ${nowInLocalTime}, date/time startDate in UTC = ${String.format('%tF %<tT',startDate)}," +
		"date/time endDate in UTC= ${String.format('%tF %<tT', endDate)}")
        
 	// Get the auxHeat1's runtime for startDate-endDate period
	def component = "auxHeat1"
	generateRuntimeReport(component,startDate, endDate)
	float runtimeTotalDaily = ecobee.currentAuxHeat1RuntimeDaily.toFloat().round(2)
	if (detailedNotif == 'true') {
		send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
	}     

	int heatStages = ecobee.currentHeatStages.toInteger()
    
    
	if (heatStages >1) { 
    
//	Get the auxHeat2's runtime for startDate-endDate period
 	
		component = "auxHeat2"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = ecobee.currentAuxHeat2RuntimeDaily.toFloat().round(2)
		if (detailedNotif == 'true') {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     
	}     

	if (heatStages >2) { 
    
//	Get the auxHeat3's runtime for startDate-endDate period
 	
		component = "auxHeat3"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = ecobee.currentAuxHeat3RuntimeDaily.toFloat().round(2)
		if (detailedNotif == 'true') {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     
	}     

// Get the compCool1's runtime for startDate-endDate period

	int coolStages = ecobee.currentCoolStages.toInteger()

	component = "compCool1"
	generateRuntimeReport(component,startDate, endDate)
	runtimeTotalDaily = ecobee.currentCompCool1RuntimeDaily.toFloat().round(2)
	if (detailedNotif == 'true') {
		send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
	}     
    
//	Get the compCool2's runtime for startDate-endDate period

	if (coolStages >1) { 
		component = "compCool2"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = ecobee.currentCompCool2RuntimeDaily.toFloat().round(2)
		if (detailedNotif == 'true') {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		} 
	} 


// 	Get the fan's runtime for startDate-endDate period
 	component = "fan"
	generateRuntimeReport(component,startDate, endDate)
	runtimeTotalDaily = ecobee.currentFanRuntimeDaily.toFloat().round(2)
	if (detailedNotif == 'true') {
		send "ecobeeGenerateStats>generated $component's runtime Daily=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
	}     

	def hasDehumidifier = (ecobee.currentHasDehumidifier) ? ecobee.currentHasDehumidifier : 'false' 
	def hasHumidifier = (ecobee.currentHasHumidifier) ? ecobee.currentHasHumidifier : 'false' 
	def hasHrv = (ecobee.currentHasHrv)? ecobee.currentHasHrv : 'false' 
	def hasErv = (ecobee.currentHasErv)? ecobee.currentHasErv : 'false' 

	if (hasHumidifier) {
	 	// Get the humidifier's runtime for startDate-endDate period
		component = "humidifier"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = ecobee.currentHumidifierRuntimeDaily.toFloat().round(2)
		if (detailedNotif == 'true') {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     
    
	}

	if (hasDehumidifier) {
	// Get the dehumidifier's for startDate-endDate period
		component = "dehumidifier"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = ecobee.currentDehumidifierRuntimeDaily.toFloat().round(2)
		if (detailedNotif == 'true') {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     

	}
	if (hasHrv || hasErv) {
 	// Get the ventilator's runtime for  startDate-endDate period
		component = "ventilator"
		generateRuntimeReport(component,startDate, endDate)
		runtimeTotalDaily = ecobee.currentVentilatorRuntimeDaily.toFloat().round(2)
		if (detailedNotif == 'true') {
			send "ecobeeGenerateStats>generated $component's runtime stats=${runtimeTotalDaily} min. for ${String.format('%tF', startDate)}"
		}     

	}
	send "ecobeeGenerateStats>generated stats done for ${String.format('%tF', startDate)} - ${String.format('%tF', endDate)} period"

}

private void generateRuntimeReport(component, startDate, endDate) {

	ecobee.getReportData("", startDate, endDate, 0, null, component,false)
	ecobee.generateReportRuntimeEvents(component, startDate,endDate, 0, null,'daily')

}

private send(msg) {
	if ( sendPushMessage != "No" ) {
		log.debug( "sending push message" )
		sendPush( msg )
       
	}
	if ( phoneNumber ) {
		log.debug( "sending text message" )
 		sendSms( phoneNumber, msg )
	}

	log.debug msg
}

