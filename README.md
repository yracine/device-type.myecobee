device-type.myecobee
====================

Ecobee-Smartthings integration

/***
 *  My Ecobee Device
 *
 *  Author: Yves Racine
 *  linkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/
 *  Date: 2014-03-31
 *  Code: https://github.com/yracine/device-type.myecobee
 
INSTALLATION STEPS
==================
 
1) Connect to the ecobee portal (www.ecobee.com) and (as a developer) create an application key with an application name (such as    ecobeeTstat) and indicate the PIN method authentication (at the bottom of the window).


For signing up as a ecobee developer, just click on the link under

http://www.ecobee.com/solutions/api/

It says "Want to be an ecobee developer? Sign up here".

Then, after signing up, you'll see a new 'developer' tab at the ecobee portal where you can register your key.

2) Create a new device type (https://graph.api.smartthings.com/ide/devices)

      Name: MyEcobee Device
      Author: Yves Racine
      copy and paste the code from ecobee.devicetype.groovy
3) Create a new device (https://graph.api.smartthings.com/device/list)

      Name: Your Choice
      Device Network Id: Your Choice
      Type: My Ecobee Device (should be the last option)
      Location: Choose the correct location
      Hub/Group: (optional) leave blank or set it up to your liking
 
4) Update device's preferences


        (a) <appKey> provided at the ecobee web portal in step 1 (no spaces)
        (b) <serial number> of your ecobee thermostat (no spaces)
        (c) <trace> when needed, set to true to get more tracing (no spaces)
        (d) <holdType> set to nextTransition or indefinite (by default, no spaces) 
        see https://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml for more details 
        (e) <ecobeeType> set to registered (by default, for SmartSI and SMART thermostats) or managementSet (EMS, no spaces)

5) To get an ecobee PIN (double authentication), create a smartapp with the following code and install it.

To do so, go to  https://graph.api.smartthings.com/ide/apps and click the 'new SmartApp' button at the top-right of your screen.



preferences {
    
	section("Initialize this ecobee thermostat") {
		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
	}
            
}



def installed() {
   
    log.debug "installed> calling getEcobeePinAuth... "
    ecobee.getEcobeePinAndAuth()
    
}

6) Click on your ecobee device again to get the 4-alphanumeric PIN  from the List Events under https://graph.api.smartthings.com/device/list. It should appear immediately under the verboseTrace attribute.


7) Go to the ecobee web portal within the next 9 minutes and enter your pin number under settings/my apps

 
8) Your device should now be ready to process your commands

9) To populate the UI fields for your newly created device, please hit the 'refresh' button several times as the smartthings UI is not always responsive.  You may want to stop and restart your smartthings app if needed.

10) You can also use the smartapps that I've developed and adapt them according to your needs (see smartapps folder)
