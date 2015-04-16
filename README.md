# device-type.myecobee           Ecobee-Smartthings integration

My Ecobee Device:  Custom ecobee device to enable more smart thermostat's capabilities within Smartthings 

Author:             Yves Racine

linkedIn profile:   ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/

Date:               2014-03-31

Code: http://github.com/yracine/device-type.myecobee

**************************************************************************************************
If you like My Ecobee Device and related smartapps, please support the developer:


<br/> [![PayPal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](
https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=yracine%40yahoo%2ecom&lc=US&item_name=Maisons%20ecomatiq&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest)

**************************************************************************************************

=====================
INSTALLATION STEPS
=====================

/*********************************************************************************************

1) Create a new device type (My Ecobee Device)
/*********************************************************************************************

a) Go to https://graph.api.smartthings.com/ide/devices

b) Hit the "+New SmartDevice" at the top right corner

c) Hit the "From Code" tab on the left corner

d) Copy and paste the code from ecobee.devicetype.groovy
under http://github.com/yracine/device-type.myecobee

e) Hit the create button at the bottom

f) Hit the "publish/for me" button at the top right corner (in the code window)

/*********************************************************************************************

2) Create a new smartapp (My ecobee Init)
/*********************************************************************************************

a) Go to https://graph.api.smartthings.com/ide/apps

b) Hit the "+New SmartApp" at the top right corner

c) Hit the "From Code" tab on the left corner

d) Copy and paste the code from My ecobee Init
under http://github.com/yracine/device-type.myecobee/tree/master/smartapps

e) Hit the create button at the bottom

f) Make sure that enable OAuth in Smartapp is active 

* Goto app settings (top right corner), 
* Click on Oauth (middle of the page), and enable OAuth in Smart app
* Hit "Update" at the bottom

g) Go back to the code window, and hit the "publish/for me" button at the top right corner 

/*********************************************************************************************

3) Use SmartSetup and execute My ecobee Init
/*********************************************************************************************

From your phone or tablet, within the smarttings app and on the main screen, 
click on '+' at the bottom, scroll right to to My Apps, execute My ecobee Init

/*********************************************************************************************

4) Connect Smartthings to the Ecobee portal
/*********************************************************************************************

You should already have an ecobee username and password, if not go to https://www.ecobee.com/home/ecobeeLogin.jsp

Go through the authentication process using My ecobee Init.

If needed, watch "how to setup ecobee" video (but use My ecobee Init instead of the Smarttings labs script) as
the authentication process is similar.

http://blog.smartthings.com/news/smartthings-updates/new-additions-to-smartthings-labs

After being connected, click 'Next' and select your ecobee thermostat(s) (SMART, SMART-SI, ecobee3, EMS) 
that you want to control from Smartthings.

/*********************************************************************************************

5) Your device(s) should now be ready to process your commands
/*********************************************************************************************

/*********************************************************************************************

6) To populate the UI fields for your newly created device(s)
/*********************************************************************************************

Hit the 'refresh' button several times as the smartthings UI is not always responsive. 
You may want to stop and restart your smartthings app if needed.


/*********************************************************************************************

7) Update device's preferences (optional, input parameters)
/*********************************************************************************************
Go to https://graph.api.smartthings.com/device/list

Click on the My ecobee device that you just created

Click on Preferences (edit)

You only need to edit the following parameters


    (a) <trace> when needed, set to true to get more tracing (no spaces)
    (b) <holdType> set to nextTransition or indefinite (by default, no spaces) 
    see http://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml 
    for more details 

/*********************************************************************************************

8) Use some of the Smartapps available (optional)
/*********************************************************************************************

You can also use some of my smartapps that I've developed and adapt them according to your needs

http://github.com/yracine/device-type.myecobee/tree/master/smartapps

Amongst others:

/****************************************************

a) ecobee3RemoteSensorInit

/****************************************************

This smartapp will expose your ecobee3's remote sensors as Motion and Temperature Sensors in SmartThings, so
that you can use them in your automation scenarios.

See the following readme file for instructions 

http://github.com/yracine/device-type.myecobee/blob/master/smartapps/readme.ecobee3RemoteSensor


/****************************************************

b) Monitor And Set Ecobee Temp

/****************************************************


In brief, the smartapp allows automatic adjustments of your programmed cooling/heating setpoints according to indoor/outdoor conditions. This is particularly useful in Winter/Summer where outdoor temperature and humidity can vary throughout the day.  It can also set your thermostat to 'Away' or 'Home' based on your indoor motion sensors.  It will ajust your thermostat's programmed or scheduled setpoints based on occupied rooms (similar to ecobee3, but with ST connected sensors).

You can enable/disable the smartapp with a button on/off tile (ex.virtual switch).

The smartapp can use an outdoor sensor or a virtual weather station, such as

https://github.com/yracine/device-type.weatherstation

to get the oudoor temperature and humidity.

/****************************************************

c) Monitor And Set Ecobee Humidity

/****************************************************

Monitor humidity level indoor vs. outdoor at regular intervals (in minutes) and set the humidifier/dehumidifier/HRV/ERV to a target humidity level.

/****************************************************

d) ecobeeChangeMode

/****************************************************

Change your ecobee climate (Away,Home) according to your hello home mode.

/****************************************************

e) AwayFromHome and ecobeeResumeProg

/****************************************************

Use presence sensors to set a target climate or heatin/cooling setpoints based on your presence/absence.

/****************************************************

f) ecobeeSetClimate

/****************************************************

This smartapp allows a ST user to set the ecobee thermostat(s) to a given climate (Away,Home,Awake,Sleep, other custom programs) at a given day&time.

And many others...
