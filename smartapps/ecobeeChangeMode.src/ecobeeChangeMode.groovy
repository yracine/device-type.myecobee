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
  "Change the ecobee program manually, by pressing the app's play button, or automatically based on Home Mode(s), switches, sensors, etc.",
  category: "My Apps",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

preferences {
  page name: "pageSetup"
  page name: "pageThermostats"
  page name: "pageProgram"
  page name: "pageNotifications"
  page name: "pageRestrictions"
}

/**
 * PAGE METHODS
 **/

/**
 * "Setup" page
 * Present the starting page with app info and page index.
 *
 * @return a dynamically created "Setup" page
 */
def pageSetup() {
  LOG("pageSetup()")

  def hrefPaypal = [
    url:            "https://www.paypal.me/ecomatiqhomes",
    title:          "If you like this smartapp, please support the developer via PayPal using the link below"
  ]

  def hrefGithub = [
    url:            "http://github.com/yracine/device-type.myecobee",
    style:          "embedded",
    required:       false,
    title:          "More information...",
    description:    "http://github.com/yracine/device-type.myecobee/blob/master/README.md"
  ]

  def hrefThermostats = [
    page:           "pageThermostats",
    title:          "Thermostats(s)",
    description:    "Tap to open"
  ]

  def hrefProgram = [
    page:           "pageProgram",
    title:          "Program and Options",
    description:    "Tap to open"
  ]

  def hrefNotifications = [
    page:           "pageNotifications",
    title:          "Notification Options",
    description:    "Tap to open"
  ]

  def hrefRestrictions = [
    page:           "pageRestrictions",
    title:          "Notification Restrictions",
    description:    "Tap to open"
  ]

  def inputLabel = [
    title:          "Assign a name",
    required:       false
  ]

  def pageProperties = [
    name:           "pageSetup",
    nextPage:       "pageThermostats",
    title:          "Setup",
    install:        false,
    uninstall:      state.installed
  ]

  return dynamicPage(pageProperties) {
    section("About") {
      paragraph "${app.getName()}, the SmartApp that sets your ecobee thermostat " +
                "to a given program/climate, e.g. 'Away', 'Home', 'Night', based " +
                "on the Home Mode in SmartThings."
      paragraph "Version ${getVersion()}"
      paragraph "${textCopyright()}"
      href hrefGithub
    }
    section("Paypal Donation") {
      href hrefPaypal
    }
    if (state.installed) {
      section("Setup Menu") {
        href hrefThermostats
        href hrefProgram
        href hrefNotifications
        href hrefRestrictions
      }
    }
    section([mobileOnly:true], "Rename SmartApp") {
      label inputLabel
    }
  }
}

/**
 * "Thermostats" page
 * Define which ecobee thermostat(s) to manage.
 *
 * @return a dynamically created "Thermostats" page
 */
def pageThermostats() {
  LOG("pageThermostats()")

  def inputThermostats = [
    name:           "thermostats",
    type:           "device.myEcobeeDevice",
    title:          "Manage these ecobee thermostat(s)",
    multiple:       true,
    required:       true
  ]

  def pageProperties = [
    name:           "pageThermostats",
    nextPage:       "pageProgram",
    title:          "Thermostats",
    install:        false,
    uninstall:      state.installed
  ]

  return dynamicPage(pageProperties) {
    section("Thermostats") {
      input inputThermostats
    }
  }
}

/**
 * "Program Selection" page
 * Define what ecobee program to set based on the Home Mode,
 * contact sensors, and/or switches.
 *
 * @return a dynamically created "Notification Restrictions" page
 */
def pageProgram() {
  LOG("pageProgram()")

  def listPrograms = thermostats[0].currentClimateList.toString().minus('[').minus(']').tokenize(',')
  LOG("programs: $listPrograms")
  def listModes=[]
  location.modes.each {
    listModes << it.name
  }

  def inputEcobeeProgram = [
    name:           "ecobeeProgram",
    type:           "enum",
    title:          "Use this program",
    options:        listPrograms,
    required:       true
  ]

  def inputModes = [
    name:           "modes",
    type:           "enum",
    title:          "Home Mode changes to",
    options:        listModes,
    multiple:       true,
    required:       false
  ]

  def inputContactSensorCardinality = [
    name:           "cardinalityContactSensor",
    type:           "enum",
    metadata:       [values:["Any","All"]],
    title:          "When",
    defaultValue:   "All",
    multiple:       false,
    required:       true
  ]

  def inputContactSensors = [
    name:           "contactSensors",
    title:          "Of these doors or windows",
    type:           "capability.contactSensor",
    multiple:       true,
    required:       false,
    submitOnChange: true
  ]

  def inputContactSensorState = [
    name:           "stateContactSensor",
    type:           "enum",
    metadata:       [values:["open","closed"]],
    title:          "Are",
    defaultValue:   "closed",
    multiple:       false,
    required:       true
  ]

  def inputSwitchCardinality = [
    name:           "cardinalitySwitch",
    type:           "enum",
    metadata:       [values:["Any","All"]],
    title:          "When",
    defaultValue:   "All",
    multiple:       false,
    required:       true
  ]

  def inputSwitches = [
    name:           "switches",
    type:           "capability.switch",
    title:          "Of these switches",
    multiple:       true,
    required:       false,
    submitOnChange: true
  ]

  def inputSwitchState = [
    name:           "stateSwitch",
    type:           "enum",
    metadata:       [values:["on","off"]],
    title:          "Are",
    defaultValue:   "on",
    multiple:       false,
    required:       true
  ]

  def inputDelay = [
    name:           "delay",
    type:           "number",
    title:          "Delay in minutes",
    required:       false
  ]

  def pageProperties = [
    name:           "pageProgram",
    nextPage:       "pageNotifications",
    title:          "Programs",
    install:        false,
    uninstall:      state.installed
  ]

  return dynamicPage(pageProperties) {
    section("Choose the ecobee program to run based on the Home Mode, contact sensors or switches.") {
      input inputEcobeeProgram
    }
    section("When [optional]") {
      input inputModes
    }
    section("Or [optional]") {
      if (settings.contactSensors) {
        input inputContactSensorCardinality
      }
      input inputContactSensors
      if (settings.contactSensors) {
        input inputContactSensorState
      }
    }
    section("Or [optional]") {
      if (settings.switches) {
        input inputSwitchCardinality
      }
      input inputSwitches
      if (settings.switches) {
        input inputSwitchState
      }
    }
    section("Delay after condition is met [optional, default=immediate]") {
      input inputDelay
    }
  }
}

/**
 * "Notification Options" page
 * Define what happens when a new program is implemented.
 * Switches can be turned on, and, optionally dimmed. A message
 * can be sent via push, text, PushBullet, or read aloud.
 *
 * @return a dynamically created "Notifications Options" page
 */
def pageNotifications() {
  LOG("pageNotifications()")

  def helpAbout =
    "How do you want to be notified of a program " +
    "change? Turn on a switch, a chime, or dim a light. " +
    "Send a push or SMS message. Use PushBullet " +
    "or an audio announcement. Amazon Echo " +
    "requires Echo Speaks."

  def inputNotifySwitches = [
    name:           "notifySwitches",
    type:           "capability.switch",
    title:          "Set these switches",
    multiple:       true,
    required:       false,
    submitOnChange: true
  ]

  def inputNotifyDimmerLevel = [
    name:           "notifyDimmerLevel",
    type:           "enum",
    metadata:       [values:["10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"]],
    title:          "Dimmer Level",
    defaultValue:   "40%",
    required:       false
  ]

  def inputMessageText = [
    name:           "messageText",
    type:           "text",
    title:          "Message Phrase",
    defaultValue:   "Changing to %program mode.",
    required:       false
  ]

  def inputSendPush = [
    name:           "sendPush",
    type:           "bool",
    title:          "Send Push on program change",
    defaultValue:   true
  ]

  def inputContacts = [
    name:           "contacts",
    type:           "contact",
    title:          "Send notifications to",
    multiple:       true,
    required:       false
  ]

  def inputPhone = [
    name:           "phone",
    type:           "phone",
    title:          "Send to this number",
    required:       false
  ]

  def inputPushbulletDevice = [
    name:           "pushbullet",
    type:           "device.pushbullet",
    title:          "Which Pushbullet devices?",
    multiple:       true,
    required:       false
  ]

  def inputAudioPlayers = [
    name:           "audioPlayers",
    type:           "capability.musicPlayer",
    title:          "Which audio players?",
    multiple:       true,
    required:       false
  ]

  def inputSpeechText = [
    name:           "speechText",
    type:           "text",
    title:          "Audio Phrase",
    defaultValue:   "Changing to %program mode",
    required:       false
  ]

  def inputEchoDevice = [
    name:           "echoSpeaks",
    type:           "capability.musicPlayer",
    title:          "Select an Amazon Echo Device",
    multiple:       false,
    required:       false,
    submitOnChange: true
  ]

  def inputEchoAll = [
    name:           "echoAll",
    type:           "bool",
    title:          "Announce on all Echo devices?",
    defaultValue:   true,
    required:       false
  ]

  def pageProperties = [
    name:           "pageNotifications",
    nextPage:       "pageRestrictions",
    title:          "Notification Options",
    uninstall:      state.installed
  ]

  return dynamicPage(pageProperties) {
    section("Instructions") {
      paragraph helpAbout
    }
    section("Turn On Switches") {
      input inputNotifySwitches
      if (settings.notifySwitches) {
        input inputNotifyDimmerLevel
      }
    }
    section("Push & SMS Notifications") {
      input inputMessageText
      input("contacts", "contact", title: "Send notification to") {
        input inputSendPush
        input inputPhone
      }
    }
    section("Pushbullet Notifications") {
      input inputPushbulletDevice
    }
    section("Audio Notifications") {
      input inputSpeechText
      input inputAudioPlayers
      input inputEchoDevice
      if (settings.echoSpeaks) {
        input inputEchoAll
      }
    }
  }
}

/**
 * "Notification Restrictions" page
 * Define when to allow notifications to be sent. Based on time
 * of day, day of week, modes or switches in a defined state.
 *
 * @return a dynamically created "Notification Restrictions" page
 */
def pageRestrictions() {
  LOG("pageRestrictions()")

  def helpAbout =
    "Restrict when you will receive program notifications by " +
    "time of day, day of week, home mode, or when one or more " +
    "switches are on or off."

  def inputStartTime = [
    name:           "startTime",
    type:           "time",
    title:          "Starting time",
    required:       false
  ]

  def inputStopTime = [
    name:           "stopTime",
    type:           "time",
    title:          "Ending time",
    required:       false,
    submitOnChange: true
  ]

  def inputWeekDays = [
    name:           "weekDays",
    type:           "enum",
    options:        ["Sunday": "Sunday","Monday": "Monday","Tuesday": "Tuesday","Wednesday": "Wednesday","Thursday": "Thursday","Friday": "Friday","Saturday": "Saturday"],
    title:          "These days of the week",
    multiple:       true,
    required:       false
  ]

  def inputNotifyModes = [
    name:           "notifyModes",
    type:           "mode",
    title:          "These modes",
    multiple:       true,
    required:       false
  ]

  def inputControlSwitches = [
    name:           "controlSwitches",
    type:           "capability.switch",
    title:          "These switches",
    multiple:       true,
    required:       false,
    submitOnChange: true
  ]

  def inputControlSwitchState = [
    name:           "notifyControlSwitchState",
    type:           "enum",
    metadata:       [values:["on","off"]],
    title:          "Are",
    defaultValue:   "on",
    multiple:       false,
    required:       true,
    submitOnChange: true
  ]

  def inputSetSwitchState = [
    name:           "setSwitchState",
    type:           "bool",
    title:          "Turn switches ${settings.notifySwitchState} afterward?",
    defaultValue:   false,
    required:       false
  ]

  def pageProperties = [
    name:           "pageRestrictions",
    nextPage:       "pageSetup",
    title:          "Notification Restrictions",
    install:        true,
    uninstall:      state.installed
  ]

  return dynamicPage(pageProperties) {
    section("Instructions") {
      paragraph helpAbout
    }
    section("Notify between") {
      input inputStartTime
      if (settings.startTime) {
        input inputStopTime
      }
    }
    section("Notify on") {
      input inputWeekDays
    }
    section("Notify when the house is in") {
      input inputNotifyModes
    }
    section("Notify when") {
      input inputControlSwitches
      if (settings.controlSwitches) {
        input inputControlSwitchState
        input inputSetSwitchState
      }
    }
  }
}

/**
 * APP INIT
 **/

def installed() {
  LOG("installed()")

  initialize()
  state.installed = true
}

def updated() {
  LOG("updated()")

  unsubscribe()
  initialize()
}

private def initialize() {
  LOG("initialize()")

  def appName  = app.getName()
  def appLabel = app.getLabel()
  if (appName == appLabel) {
    log.info "${appName}> Version ${getVersion()}. ${textCopyright()}"
  } else {
    log.info "${appLabel}(${appName})> Version ${getVersion()}. ${textCopyright()}"
  }
  LOG("settings: ${settings}")

  if (settings.modes || settings.switches || settings.contactSensors) {
    if (settings.modes && (settings.modes != null)) {
      subscribe(location, "mode", onModeOrContact)
    }
    if (settings.switches && (settings.switches != null)) {
      def txtState = "switch." + settings.stateSwitch
      settings.switches.each {
        subscribe(it, txtState, onSwitch, [filterEvents: false])
      }
    }
    if (settings.contactSensors && (settings.contactSensors != null)) {
      def txtState = "contact." + settings.stateContactSensor
      settings.contactSensors.each {
        subscribe(it, txtState, onModeOrContact, [filterEvents: false])
      }
    }
  } else {
    takeAction()
  }
  subscribe(app, onTouch)

  STATE()
}

/**
 * EVENT HANDLERS
 **/

/**
 * Check contact sensors for specified criteria. Only 'All' cardinality needs
 * to be checked. 'Any' is satisfied by fact that we were called.
 */
private Boolean checkContactSensors() {
  if (settings.contactSensors) {
    if (settings.cardinalityContactSensor == "All") {
      if (settings.contactSensors.any { it.latestValue("contact") != settings.stateContactSensor }) {
        LOG("Not all contacts are ${settings.stateContactSensor}")
        return false
      }
    } else {
      if (settings.contactSensors.any { it.latestValue("contact") == settings.stateContactSensor }) {
        LOG("One or more contacts are ${settings.stateContactSensor}")
        return true
      } else {
        LOG("No contacts are ${settings.stateContactSensor}")
        return false
      }
    }
  }
  LOG("No contact sensors defined")
  return false
}

/**
 * Check that current mode is one of our specified criteria.
 */
private Boolean checkMode() {
  if (settings.modes) {
    return settings.modes.contains(location.currentMode)
  }
  LOG("No modes defined")
  return false
}

/**
 * Check switches for specified criteria. Only 'All' cardinality needs to be
 * checked. 'Any' is satisfied by fact that we were called.
 */
private Boolean checkSwitches() {
  if (settings.switches) {
    if (settings.cardinalitySwitch == "All") {
      if (settings.switches.any { it.latestValue("switch") != settings.stateSwitch }) {
        LOG("Not all switches are ${settings.stateSwitch}")
        return false
      }
    } else {
      if (settings.switches.any { it.latestValue("switch") == settings.stateSwitch }) {
        LOG("One or more switches are ${settings.stateSwitch}")
        return true
      } else {
        LOG("No switches are ${settings.stateSwitch}")
        return false
      }
    }
  }
  LOG("No switches defined")
  return false
}

/**
 * Manual app invocation
 *
 * @param an app event object
 */
def onTouch(evt) {
  LOG("appTouch(${evt.name}: ${evt.value})")
  LOG("location.mode= $location.currentMode, ecobeeProgram=${ecobeeProgram}, about to takeAction")
  takeAction()
}

/**
 * Contact Event Handler
 * If mode and sensors are set, schedules mode change
 *
 * @param a contact or mode event object
 */
def onModeOrContact(evt) {
  LOG("onContact(${evt.name}: ${evt.value})")

  stopAction()
  if (!checkMode() && !checkContactSensors()) {
    LOG("Mode or Contact Sensors failed; doing nothing")
    return
  }
  scheduleAction()
}

/**
 * Switch Event Handler
 * If switches are set, schedule mode change
 *
 * @param a switch event object
 */
def onSwitch(evt) {
  LOG("onSwitch(${evt.name}: ${evt.value})")

  stopAction()
  if (!checkSwitches()) {
    LOG("Switch check failed, doing nothing")
    return
  }
  scheduleAction()
}

/**
 * ACTION HANDLERS
 **/

/**
 * Unschedule action if delay is specified.
 */
private void stopAction() {
  if (settings.delay) {
    try {
      unschedule(takeAction)
    } catch (e) {
      LOG("exception when trying to unschedule: $e")
    }
  }
}

/**
 * Schedule action for either immediate or delayed execution.
 */
private void scheduleAction() {
  if (settings.delay && (settings.delay!=null)) {
    LOG("scheduling takeAction() in $delay minutes ")
    runIn((settings.delay*60), "takeAction")
  } else {
    LOG("about to call takeAction() immediately")
    takeAction()
  }
}

/**
 * Verify that modes, contacts and/or switches are all set as specified,
 * then perform action.
 */
private void takeAction() {
  LOG("takeAction()")

  if (checkMode() || checkContactSensors() || checkSwitches()) {
    notify(settings.ecobeeProgram)
    thermostats.each {
      it?.setThisTstatClimate(ecobeeProgram)
    }
  }
}

/**
 * NOTIFICATION HANDLERS
 **/

/**
 * Main notification processor
 * Turns on and dims switches, calls additional notification methods.
 *
 * @param the name of the ecobee program.
 */
private notify(name) {
  LOG("notify(${name})")

  // Determine if conditions permit notification
  def restricted = notifyRestrictions()
  if (!restricted) {
    def msg = textMessage(name)

    // Only turn on those switches that are currently off
    def switchesOn = settings.notifySwitches?.findAll { it?.currentSwitch == "off" }
    LOG("switchesOn: ${switchesOn}")
    if (switchesOn) {
      switchesOn*.on()
    }

    if (contacts) {
      notifyContacts(msg)
    } else {
      notifyPush(msg)
      notifyText(msg)
    }
    notifyPushBullet(msg)
    notifyEcho(name)
    notifyVoice(name)
  } else {
    LOG("notification restricted")
  }
}

/**
 * Check restrictions to notification
 */
private def notifyRestrictions() {
  LOG("notifyRestrictions()")

  // Create and ensure the data object is set to local time zone
  def df = new java.text.SimpleDateFormat("EEEE")
  df.setTimeZone(location.timeZone)

  // Is today a selected day of the week?
  if (settings.weekDays) {
    def day = df.format(new Date())
    def dayCheck = settings.weekDays.contains(day)
    if (!dayCheck) {
      LOG("Not an allowed weekday")
      return true
    }
  }

  // Is the time within the specified interval?
  if (settings.startTime && settings.stopTime) {
    def timeCheck = timeOfDayIsBetween(settings.startTime, settings.stopTime, new Date(), location.timeZone)
    if (!timeCheck) {
      LOG("Outside time of day")
      return true
    }
  }

  // Is the house in a selected mode?
  if (settings.notifyModes) {
    def modeCheck = settings.notifyModes.contains(location.currentMode)
    if (!modeCheck) {
      LOG("Not allowed in ${location.currentMode} mode")
      return true
    }
  }

  // Are any switches set to disable notifications?
  def switchCheck = settings.controlSwitches?.findAll { it?.currentSwitch != settings.controlSwitchState }
  if (switchCheck) {
    LOG("Switches not ${settings.controlSwitchState}: ${switchCheck}")
    if (settings.setSwitchState) {
      LOG("Setting to ${settings.controlSwitchState}: ${switchCheck}")
      if (settings.controlSwitchState == "on") {
        switchCheck*.on()
      } else {
        switchCheck*.off()
      }
    }
    return true
  }

  return false
}

/**
 * Process message to Contact Book
 *
 * @param the message to send
 */
private def notifyContacts(msg) {
  LOG("notifyContacts(${msg})")

  sendNotificationToContacts(msg, contacts)
}

/**
 * Process a push message
 *
 * @param the message to send
 */
private def notifyPush(msg) {
  LOG("notifyPush(${msg})")

  if (settings.sendPush) {
    // sendPush can throw an exception
    try {
      sendPush(msg)
    } catch (e) {
      log.error e
    }
  } else {
    sendNotificationEvent(msg)
  }
}

/**
 * Process a text message
 *
 * @param the message to send
 */
private def notifyText(msg) {
  LOG("notifyText(${msg})")

  if (settings.phone) {
    sendSms(phone, msg)
  }
}

/**
 * Process a PushBullet message
 *
 * @param the message to send
 */
private def notifyPushBullet(msg) {
  if (settings.pushbullet) {
    settings.pushbullet*.push(location.name, msg)
  }
}

/**
 * Process a text-to-speech message. Note that the string
 * '%door' in the message text will be replaced with the
 * name of the acceleration sensor that detected the knock.
 *
 * @param the name of the acceleration sensor that detected the knock.
 */
private def notifyVoice(name) {
  LOG("notifyVoice(${name})")

  if (!settings.audioPlayers) {
    return
  }

  // Replace %door with name
  def phrase = textSpeech(name)

  if (phrase) {
    settings.audioPlayers*.playText(phrase)
  }
}

private def notifyEcho(name) {
  LOG("notifyEcho(${name})")

  if (!settings.echoSpeaks) {
    return
  }

  // Replace %door with name
  def phrase = textSpeech(name)

  if (phrase) {
    if (settings.echoAll) {
      settings.echoSpeaks*.playAnnouncementAll(phrase,app.getName())
    } else {
      settings.echoSpeaks*.playAnnouncement(phrase,app.getName())
    }
  }
}

private def textMessage(name) {
  def text = settings.messageText.replaceAll('%program', name)
}

private def textSpeech(name) {
  def text = settings.speechText.replaceAll('%program', name)
}

/**
 * OUTPUT FORMATTERS
 **/

private def LOG(message) {
  def appID = app.getLabel()
  if (appID == null) {
    appID = app.getName()
  }
  log.trace "${appID}> " + message
}

private def STATE() {
  LOG("app state: ${state}")
}

/**
 * APP METADATA
 **/

private def textCopyright() {
  def text = "Copyright©2014 Yves Racine"
}

private def getVersion() {
  return "2.1.0"
}