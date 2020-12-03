# device-type.myecobee           Hubitat-Ecobee integration

My Ecobee Device:  Custom ecobee device to enable more smart thermostat's capabilities within Hubitat

Author:             Yves Racine

linkedIn profile:   ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/


_______________________________________________________________________________________

For more details on the extensive features & capabilities of MyEcobee device, please refer to this ST community thread:

https://community.smartthings.com/t/release-my-ecobee-device-v5-the-smartest-thermostat-under-st-with-new-comfort-tips-features-and-unique-integration-with-echo-ask-alexa/56534

<i>It's the most comprehensive ecobee integration under any automation platform.</i>

please take note of the following statement:

**http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Device#Notes_to_MyEcobee_Device_Users_-_Contribution_is_now_required**

_______________________________________________________________________________________

You can now download the code at 

<b>
http://www.maisonsecomatiq.com/hubitatstore
</b>

P.S. Technical support packages are also available.
**************************************************************************************************


Setup time: about 5-10 minutes depending on your Hubitat skills.


PREREQUISITES
=====================

- (a) Your ecobee thermostat fully operational (and connected to wifi)
- (b) Your ecobee credentials (username/password)
- (c) Hubitat hub operational

INSTALLATION STEPS
=====================


# 1a) Create a new device Handler (My Ecobee Device)

go to http://192.168.xx.xx/driver/list (insert your own hub's IP address)

b) Hit the "+New Driver" at the top right corner

c) Copy and paste the code from the corresponding txt file in the zip 

d) Hit the save button on the right inside of the screen

# 1b) (Optional) Create a new device Handler (My Ecobee Switch, If you have such switches at home)

 N.B. The code for the new EcobeeSwitch+ is included in My Ecobee bundle only (available at my store).
 
go to http://192.168.xx.xx/driver/list (insert your own hub's IP address)

a) Hit the "+New Driver" at the top right corner

b) Copy and paste the code from the corresponding txt file in the zip 

c) Hit the save button on the right inside of the screen


# 2) Create a new smartapp (My ecobee Init)

go to http://192.168.xx.xx/app/list (insert your own hub's IP address)

a) Hit the "+New App" at the top right corner

b) Copy and paste the code from the corresponding txt file in the zip 

c) Hit the save button on the right inside of the screen

d) Make sure that enable OAuth in Smartapp is active (click oAuth in the upper right corner)
* Hit "Update" at the bottom

If the instructions above are not clear enough, you can refer to the troubleshooting section below with some pictures:

http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Init#Issue_.231:_I_don.27t_know_how_to_create_a_custom_smartapp


# 3) Go to the apps section of hubitats, execute MyEcobeeInit (under + in the upper right corner/Smartapp)

http://192.168.xx.xx/installedapp/list (insert your own hub's IP address)

Click on "Add User app" in the right corner of the window and choose MyEcobeeInit

# 4) Connect Smartthings to the Ecobee portal


You should already have an ecobee username and password, if not go to https://www.ecobee.com/home/ecobeeLogin.jsp

Go through the authentication process using MyEcobeeInit. To do so, please press on the "ecobee Connect> Required" button in the middle of the screen.

After signing-in, you need to accept the permission needed to connect your ecobee devices to SmartThings by pressing the accept button at the bottom of the page.

After being connected, click 'Next' and select your ecobee thermostat(s) (SmartThermostat, ecobee lite, ecobee3, SMART, SMART-SI) 
that you want to control from Smartthings and, then press 'Next' for the 'Other Settings &Notification' page, 
and then 'Done' or 'Save' when finished.


If you have My Ecobee bundle, you can also select your ecobee switch(es) to be exposed to ST.

*************************************************************************************************************************************
N.B. If you have any errors:

If you get a blank screen after pressing 'Next or you get the following error: "Error - bad state' or 'Java.lang.NullPointerException: Cannot get property 'accessToken' on null object" in the IDE', you'd need to enable oAuth as specified in step 2f) above.

<b> At the end of the authorization flow,  if you have the following error message: "Unexpected error" even if you press several times, this probably means that you have not "saved & published" one of the Device Handler Types (MyEcobeeDevice,MyEcobeeSwitch) under the right shard.  Refer to the prerequisites & step 1 for more details.
 
*************************************************************************************************************************************


# 5) Your device(s) should now be ready to process your commands


Afer about 1 minute, You should see your device populated under:

http://192.168.xx.xx/device/list (insert your own hub's IP address)



# 6) (Optional) Set device's preferences 

a) Click on the My ecobee device that you just created


http://192.168.xx.xx/device/edit/"device number"  (Device number can vary from one location to the next)


b) Edit the preferences in the middle section of the screen) 

You only need to edit the following parameters

    (a) <trace> when needed, set to true to get more tracing (no spaces)
    (b) <holdType> set to nextTransition or indefinite (by default, no spaces) 
   
    (c) <logFilter:1..5> Values=[Level 1=ERROR only,2=<Level 1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>]
    
    P.S. Don't enter any values for the thermostat's serial number or for the appKey as the values are only
    used for the PIN authentication method (not with the Service Manager).  If you do it, you may
    experience authentication issues when used with MyEcobeeInit smartapp.
 
 For more details on the holdType input parameter, refer to the community wiki:
 
 http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Device#Item_3b.29_Set_up_the_holdType_input_parameter

c) Save the preferences by clicking on the corresponding button.

# 8) Use some of the  Smartapps available (optional)

Some complimentary smartapps at my github, refer to:

https://github.com/yracine/device-type.myecobee/tree/master/smartapps

Amongst others:

/****************************************************

<b>a) ecobee3RemoteSensorInit</b>

/****************************************************

This smartapp will expose your ecobee3's remote sensors as Motion and Temperature Sensors in Hubitat, so
that you can use them in your automation scenarios.

See the following readme file for instructions 

https://github.com/yracine/device-type.myecobee/blob/master/smartapps/ecobeeRemoteSensor.md


/****************************************************

<b>b) Monitor And Set Ecobee Temp</b>

/****************************************************


In brief, the smartapp allows automatic adjustments of your programmed cooling/heating setpoints according to indoor/outdoor conditions. This is particularly useful in Winter/Summer where outdoor temperature and humidity can vary throughout the day. It can also set your thermostat to 'Away' or 'Home' based on your indoor motion sensors.  It will ajust your thermostat's programmed or scheduled setpoints based on occupied rooms (similar to ecobee3, but with ST connected sensors).

You can enable/disable the smartapp with a button on/off tile (ex.virtual switch).

The smartapp can use an outdoor sensor or a virtual weather station, such as

https://github.com/yracine/device-type.weatherstation

to get the oudoor temperature and humidity.

/****************************************************

<b>c) Monitor And Set Ecobee Humidity</b>

/****************************************************

Monitor humidity level indoor vs. outdoor at regular intervals (in minutes) and set the humidifier/dehumidifier/HRV/ERV to a target humidity level.

P.S. Your humidifier/dehumidifier/HRV/ERV needs to be physically connected to ecobee.

/****************************************************

<b>d) ecobeeChangeMode</b>

/****************************************************

Change your ecobee climate (Away,Home) according to your hello home mode.


/****************************************************

<b>e) ecobeeGetTips and EcobeeGenerateXXXstats/b>

/****************************************************

The smartapps allow the user to get comfort & energy tips based on his/her indoor/outdoor conditions at home.
The ecobeeGenerateXXXstats smartapps allow to collect runtime stats for better tips generation.


/****************************************************

<b>f) AwayFromHome and ecobeeResumeProg</b>

/****************************************************

Use presence/motion sensors or location modes to set a target climate or heating/cooling setpoints based on your presence/absence.

/****************************************************

<b>g) ecobeeSetClimate</b>

/****************************************************

This smartapp allows a Hubitat user to set the ecobee thermostat(s) to a given climate (Away,Home,Awake,Sleep, other custom programs) at a given day&time.


/****************************************************

<b>h) ecobeeStateTriggerHA</b>

/****************************************************

The above smartapp allows a Hubitat user to trigger some switch(es) (turn on/off or flash) and/or trigger a hello phrase routine when the thermostat is cooling/heating/running the fan/or idle.

/****************************************************

<b>i) ecobeeSetZoneWithSchedule</b>

/****************************************************


The smartapp that enables Multi Zoned Heating/Cooling Solutions based on your ecobee schedule(s)- coupled with smart vents (optional) for better temp settings control throughout your home"


And many others...
