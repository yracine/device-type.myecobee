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

2) Create a new device type (https://graph.api.smartthings.com/ide/devices)

      Name: MyEcobee Device
      Author: Yves Racine
3) Create a new device (https://graph.api.smartthings.com/device/list)

      Name: Your Choice
      Device Network Id: Your Choice
      Type: My Ecobee Device (should be the last option)
      Location: Choose the correct location
      Hub/Group: (optional) leave blank or set it up to your liking
 
4) Update device's preferences

      Click on the new device 
      Click the edit button next to Preferences
      Fill in your device 
         (a) "appKey" provided at the ecobee web portal in step 1
         (b) "serial number" of your ecobee thermostat
         (c) "trace" when needed, set to true to get more tracing
 
5) To get an ecobee PIN (double authentication), create a small app with the following code and install it.

preferences {
    
	section("Initialize this ecobee thermostat") {
		input "ecobee", "capability.thermostat", title: "Ecobee Thermostat"
	}
            
}



def installed() {
   
    log.debug "installed> calling getEcobeePinAuth... "
    ecobee.getEcobeePinAndAuth()
    
}

6) Click on your ecobee device again to get the 4-alphanumeric PIN  from the list ((https://graph.api.smartthings.com/device/list). It should appear immediately under the verboseTrace attribute.


7) Go to the ecobee web portal within the next 9 minutes and enter your pin number under settings/my apps

 
8) Your device should now be ready to process your commands

