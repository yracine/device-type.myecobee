# device-type.myecobee           Ecobee-SmartThings integration

My Ecobee Device:  Custom ecobee device to enable more smart thermostat's capabilities within SmartThings 

Author:             Yves Racine

linkedIn profile:   ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/

Date:               2014-03-31

/*********************************************************************************************

please take note of the following statement:**

**http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Device#Notes_to_MyEcobee_Device_Users_-_Contribution_is_now_required**

/*********************************************************************************************

You can now download the code at 

<b>
http://www.maisonsecomatiq.com/#!store/tc3yr 
</b>

P.S. Technical support packages are also available.
**************************************************************************************************


Setup time: about 5-10 minutes depending on your ST skills.


PREREQUISITES
=====================

- Your ecobee thermostat fully operational (and connected to wifi)
- Your ecobee credentials (username/password)
- Developer access to SmartThings (http://graph.api.smartthings.com/)
- Location set for your ST account 

Under the ST mobile app, click on the 3-horizontal lines- "hamburger"- menu in the upper left corner, and then the "gear'" icon to review your location and save it.

https://support.smartthings.com/hc/en-us/articles/205956850-How-to-edit-Location-settings

- Determine your shard, please consult this thread:


https://community.smartthings.com/t/faq-how-to-find-out-what-shard-cloud-slice-ide-url-your-account-location-is-on/53923

Or the SmartThings documentation here:

http://docs.smartthings.com/en/latest/publishing/index.html#ensure-proper-location

If you are on a different shard, you need to change the links below for your right shard. 
As an example, in North America,

replace https://graph.api.smartthings.com/ide/devices by https://graph-na02-useast1.api.smartthings.com


INSTALLATION STEPS
=====================



# 1) Create a new device Handler (My Ecobee Device)


a) Go to https://graph.api.smartthings.com/ide/devices

b) Hit the "+New Device Handler" at the top right corner

c) Hit the "From Code" tab on the left corner

d) Copy and paste the code  

<b>The code will be sent to you via your paypal verified email address.</b>

e) Hit the create button at the bottom

f) Hit the "publish/for me" button at the top right corner (in the code window)





# 2) Create a new smartapp (My ecobee Init)


a) Go to https://graph.api.smartthings.com/ide/apps

b) Hit the "+New SmartApp" at the top right corner

c) Hit the "From Code" tab on the left corner

d) Copy and paste the code  

<b>The code will be sent to you via your paypal verified email address.</b>

e) Hit the create button at the bottom

f) Make sure that enable OAuth in Smartapp is active 

* Goto app settings (top right corner, click on it)
* Click on Oauth (middle of the page), and enable OAuth in Smart app
* Hit "Update" at the bottom

g) Go back to the code window, and hit the "publish/for me" button at the top right corner 



# 3) Under the ST app, execute My ecobee Init



<b>Click on the Smartapps link in the upper section of the following Marketspace screen (last icon at the bottom), and then MyApps (last item in the list).</b>


# 4) Connect Smartthings to the Ecobee portal


You should already have an ecobee username and password, if not go to https://www.ecobee.com/home/ecobeeLogin.jsp

Go through the authentication process using My ecobee Init.

If needed, watch "how to setup ecobee" video (but use My ecobee Init instead of the Smarttings labs script) as the authentication process is similar.

http://blog.smartthings.com/news/smartthings-updates/new-additions-to-smartthings-labs

After being connected, click 'Next' and select your ecobee thermostat(s) (ecobee lite, ecobee3, SMART, SMART-SI, EMS) 
that you want to control from Smartthings and, then press 'Next' for the 'Other Settings &Notification' page, 
and then 'Done' when finished.

If you get a blank screen after pressing 'Next or you get the following error: " Error - bad state. Unable to complete page configuration", you'd need to enable oAuth as specified in step 2f) above.



# 5) Your device(s) should now be ready to process your commands


You should see your device under

https://graph.api.smartthings.com/device/list

And

In the ST app, under myHome/Things.


# 6) To populate the UI fields for your newly created device(s)


You may have to hit the My Ecobee device 'refresh' button several times as the smartThings app is not always responsive. 
You may want to stop and restart your smartthings app if needed.



# 7) Update device's preferences (optional, input parameters)


Go to https://graph.api.smartthings.com/device/list

Click on the My ecobee device that you just created

Click on Preferences (edit)

N.B. You can also edit the preferences under Things/Your Ecobee device/Edit Device using the ST mobile app.

You only need to edit the following parameters


    (a) <trace> when needed, set to true to get more tracing (no spaces)
    (b) <holdType> set to nextTransition or indefinite (by default, no spaces) 
    see http://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml 
    for more details 
    (c) <logFilter:1..5> Values=[Level 1=ERROR only,2=<Level 1+WARNING>,3=<2+INFO>,4=<3+DEBUG>,5=<4+TRACE>]
    
    P.S. Don't enter any values for the thermostat's serial number or for the appKey as the values are only
    used for the PIN authentication method (not with the Service Manager).  If you do it, you may
    experience authentication issues when used with MyEcobeeInit smartapp.
    

# 8) Use some of the Smartapps available (optional)


Amongst others:

/****************************************************

<b>a) ecobee3RemoteSensorInit</b>

/****************************************************

This smartapp will expose your ecobee3's remote sensors as Motion and Temperature Sensors in SmartThings, so
that you can use them in your automation scenarios.

See the following readme file for instructions 

http://github.com/yracine/device-type.myecobee/blob/master/smartapps/readme.ecobee3RemoteSensor


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

<b>e) AwayFromHome and ecobeeResumeProg</b>

/****************************************************

Use presence/motion sensors or ST hello modes to set a target climate or heating/cooling setpoints based on your presence/absence.

/****************************************************

<b>f) ecobeeSetClimate</b>

/****************************************************

This smartapp allows a ST user to set the ecobee thermostat(s) to a given climate (Away,Home,Awake,Sleep, other custom programs) at a given day&time.


/****************************************************

<b>g) ecobeeSetZoneWithSchedule</b>

/****************************************************


The smartapp that enables Multi Zoned Heating/Cooling Solutions based on your ecobee schedule(s)- coupled with smart vents (optional) for better temp settings control throughout your home"


And many others...
