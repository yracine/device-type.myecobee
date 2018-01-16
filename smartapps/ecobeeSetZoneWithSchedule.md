ecobeeSetZoneWithSchedule
=========================

Author: Yves Racine

Email: services@maisonsecomatiq.com

linkedIn profile: https://www.linkedin.com/in/yracine/

Installation Setup time: about 5-10 minutes depending on your ST skills.


PREREQUISITES
=====================

- (a) Your ecobee thermostat and slave thermostats (if any) fully operational (and connected to ST)
- (b) My Ecobee device installed & fully operational
- (c) Developer access to SmartThings (http://graph.api.smartthings.com/)
- (d) Location set for your ST account  

Under the ST mobile app, click on the 3-horizontal lines- "hamburger"- menu in the upper left corner, and then the "gear'" icon to review your location and save it.

https://support.smartthings.com/hc/en-us/articles/205956850-How-to-edit-Location-settings

-  (e) <b> Determine your shard, please consult this thread: </b> 


https://community.smartthings.com/t/faq-how-to-find-out-what-shard-cloud-slice-ide-url-your-account-location-is-on/53923

Or the SmartThings documentation here:

http://docs.smartthings.com/en/latest/publishing/index.html#ensure-proper-location

If you are on a different shard, you need to change the links below for your right shard. 
As an example, in North America, 

replace https://graph.api.smartthings.com/ide/devices by https://graph-na02-useast1.api.smartthings.com


Or use  https://consigliere-regional.api.smartthings.com/ to point to the right shard.



INSTALLATION STEPS
=====================


# 1) (Optional) Create a new device Handler (My Virtual Zone)

This is an optional device handler that allows you to more easily control your zones
inside your home. 

- You can see the thermostat's mode & setpoints, the current avg temperature in your zone. 

- Based on the above parameters, you can set a temp delta
for your active zone(s) which is used for controlling your vents and slave thermostats in your zoned rooms. 

- You can also manually make the zone active or inactive according to
your requirements during the day.

a) Go to https://graph.api.smartthings.com/ide/devices   (or whatever your shard is )

b) Hit the "+New Device Handler" at the top right corner

c) Hit the "From Code" tab on the left corner

d) Copy and paste the code  from My Virtual Zone txt file in the zip 

e) Hit the create button at the bottom

f) Hit the "publish/for me" button at the top right corner (in the code window)

N.B. <b> In the step #3 below, for each zone that you want to expose as a virtual zone under ST, you'd need to
indicate it using the appropriate flag in ZonesSetup when you configure ecobeeSetZoneWithSchedule.

If the virtual zones are not responsive to any commands (i.e., set target temp delta, make the zone active/inactive, switch to turn on/off vents in the zone) issued from the new virtual Zone device(s), just go back to ecobeeSetZoneWithSchedule under Automation/Smartapps in the ST mobile app and check your configuration again (rooms->zones->schedules->Notifications) till the final page and press done (to register the new events handler in the smartapp).</b>


# 2) Create a new smartapp (ecobeeSetZoneWithSchedule)


a) Go to https://graph.api.smartthings.com/ide/apps       (or whatever your shard is )

b) Hit the "+New SmartApp" at the top right corner

c) Hit the "From Code" tab on the left corner

d) Copy and paste the code  from the corresponding txt file

e) Hit the create button at the bottom


f) Go back to the code window, and hit the "publish/for me" button at the top right corner 


# 3) Under the ST app, execute ecobeeSetZoneWithSchedule

If you don't see the smartapp, you probably installed it under the wrong shard or it's not been "published" in the IDE. Please refer to the prerequisites above.

- Click on the Smartapps link in the upper section of the following Marketspace screen (last icon at the bottom), and then MyApps (last item in the list).


CONFIGURATION STEPS
=====================

Refer to the ST community wiki, most of the use cases are documented with the input parameters needed.

http://thingsthataresmart.wiki/index.php?title=EcobeeSetZoneWithSchedule#Prerequisites
http://thingsthataresmart.wiki/index.php?title=EcobeeSetZoneWithSchedule#Configuration

TROUBLESHOOTING
=====================

http://thingsthataresmart.wiki/index.php?title=EcobeeSetZoneWithSchedule#Troubleshooting

SUPPORT
========

If needed, there are several support packages at my store.

www.ecomatiqhomes.com/store

Regards.
