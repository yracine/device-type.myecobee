device-type.myecobee
Ecobee-Smartthings integration

/***

My Ecobee Device:  Custom ecobee device to enable more smart thermostat's capabilities within Smartthings 

Author:             Yves Racine

linkedIn profile:   ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/

Date:               2014-03-31

Code: https://github.com/yracine/device-type.myecobee



INSTALLATION STEPS
=====================

1) Create a new device type (My Ecobee Device)


Go to https://graph.api.smartthings.com/ide/devices

Name: My Ecobee Device

Author: Yves Racine

Copy and paste the code from ecobee.devicetype.groovy


2) Create a new smartapp (My ecobee Init)

Go to https://graph.api.smartthings.com/ide/apps


Name:           My ecobee Init

Description:    My ecobee Init

Categoy:        My Apps

Enable Oauth in Smartapp

Click on Create at the bottom of the page


Copy and paste the code from My ecobee Init

At https://github.com/yracine/device-type.myecobee/tree/master/smartapps



3) Use SmartSetup (+) from your phone or table within the smarttings app, scroll right to to My Apps, execute My ecobee Init


4) Go through the authentication process as the stock Smartthings ecobee device


See ecobee video at http://blog.smartthings.com/news/smartthings-updates/new-additions-to-smartthings-labs


5) Update device's preferences (optional)

    (a) <trace> when needed, set to true to get more tracing (no spaces)
    (b) <holdType> set to nextTransition or indefinite (by default, no spaces) 
    see https://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml for more details 
    

6) Your device should now be ready to process your commands


7) To populate the UI fields for your newly created device, please hit the 'refresh' button several times as the smartthings UI is not always responsive. You may want to stop and restart your smartthings app if needed.


Optional (Smartapps available)


8) You can also use some of my smartapps that I've developed and adapt them according to your needs

https://github.com/yracine/device-type.myecobee/tree/master/smartapps

Amongst others:

********************************
a) Monitor And Set Ecobee Temp
********************************

In brief, the smartapp allows automatic adjustments of the cooling/heating setpoints according to outdoor conditions. This is particularly useful in the Winter/Summer where outdoor temperature and humidity can vary throughout the day.

You can enable/disable the smartapp with a button on/off tile (ex.virtual switch).

The smartapp can use an outdoor sensor or a virtual weather station, such as

https://github.com/yracine/device-type.weatherstation8

to get the oudoor temperature and humidity.

***********************************
b) Monitor And Set Ecobee Humidity
***********************************

Monitor humidity level indoor vs. outdoor at a regular intervals (in minutes) and set the humidifier/dehumidifier/HRV/ERV to a target humidity level.

*******************
c) ecobeeChangeMode
*******************
Change your ecobee climate (Away,Home) according to your hello home mode.

*************************************
d) AwayFromHome and ecobeeResumeProg
*************************************
Use presence sensors to set a target climate or heatin/cooling setpoints based on your presence/absence.
