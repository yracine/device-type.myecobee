device-type.myecobee           Ecobee-Smartthings integration

My Ecobee Device:  Custom ecobee device to enable more smart thermostat's capabilities within Smartthings 

Author:             Yves Racine

linkedIn profile:   ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/

Date:               2014-03-31

Code: https://github.com/yracine/device-type.myecobee



=====================
INSTALLATION STEPS
=====================

*************************************************
1) Create a new device type (My Ecobee Device)
*************************************************


a) Go to https://graph.api.smartthings.com/ide/devices
b) Hit the "+New SmartDevice" at the top right corner
c) Hit the "From Code" tab on the left corner
d) Copy and paste the code from ecobee.devicetype.groovy
under https://github.com/yracine/device-type.myecobee
e) Hit the create button at the bottom


*************************************************
2) Create a new smartapp (My ecobee Init)
*************************************************

a) Go to https://graph.api.smartthings.com/ide/apps
b) Hit the "+New SmartApp" at the top right corner
c) Hit the "From Code" tab on the left corner
d) Copy and paste the code from My ecobee Init
under https://github.com/yracine/device-type.myecobee/tree/master/smartapps
e) Hit the create button at the bottom


*************************************************
3) Use SmartSetup and execute My ecobee Init
*************************************************
From your phone or tablet, within the smarttings app and on the main screen, 
click on '+' at the bottom, scroll right to to My Apps, execute My ecobee Init

***********************************************************************************
4) Connect Smartthings to the Ecobee portal
***********************************************************************************

You should already have an ecobee username and password, if not go to https://www.ecobee.com/home/ecobeeLogin.jsp

Go through the authentication process using My ecobee Init.

If needed, watch "how to setup ecobee" video (but use My ecobee Init instead of the Smarttings labs script) as
the authentication process is similar.

http://blog.smartthings.com/news/smartthings-updates/new-additions-to-smartthings-labs

After being connected, click 'Next' and select your ecobee thermostat(s) (SMART, SMART-SI, ecobee3, EMS) 
that you want to control from Smartthings.

***********************************************************
5) Your device(s) should now be ready to process your commands
***********************************************************

**************************************************************
6) To populate the UI fields for your newly created device(s)
**************************************************************

Hit the 'refresh' button several times as the smartthings UI is not always responsive. 
You may want to stop and restart your smartthings app if needed.


*************************************************************
7) Update device's preferences (optional, input parameters)
*************************************************************
Go to https://graph.api.smartthings.com/device/list

Click on the My ecobee device that you just created

Click on Preferences (edit)

You only need to edit the following parameters


    (a) <trace> when needed, set to true to get more tracing (no spaces)
    (b) <holdType> set to nextTransition or indefinite (by default, no spaces) 
    see https://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml 
    for more details 

***********************************
8) Use some of the Smartapps available
***********************************

You can also use some of my smartapps that I've developed and adapt them according to your needs

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
