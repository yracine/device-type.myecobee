ScheduleRoomTempControl
========================

Author: Yves Racine

Email: services@maisonsecomatiq.com

linkedIn profile: https://www.linkedin.com/in/yracine/

\Installation Setup time: about 5-10 minutes depending on your ST skills.


PREREQUISITES
=====================
- (a) Your ecobee thermostat and slave thermostats (if any) fully operational (and connected to ST)
- (b) My Ecobee device installed & fully operational
- (c) Developer access to SmartThings (http://graph.api.smartthings.com/) or Hubitat

- (d) <b> For ST users, Determine your shard, please consult this thread: 
  
https://community.smartthings.com/t/faq-how-to-find-out-what-shard-cloud-slice-ide-url-your-account-location-is-on/53923
  
Or the SmartThings documentation here:

http://docs.smartthings.com/en/latest/publishing/index.html#ensure-proper-location

If you are on a different shard, you need to change the links below for your right shard. 
As an example, in North America, 

replace https://graph.api.smartthings.com/ide/devices by https://graph-na02-useast1.api.smartthings.com


Or use   https://account.smartthings.com to point to the right shard (automatically)



INSTALLATION STEPS
=====================


# 1a) Create a new device driver (optional) For MyVirtualZone

go to http://192.168.xx.xx/driver/list (insert your own hub's IP address)

b) Hit the "+New Driver" at the top right corner

c) Copy and paste the code from the corresponding txt file in the zip (MyVirtualZoneForHubitat) 

d) Hit the save button on the right inside of the screen


# 1b) Create a new smartapp (ScheduleRoomTempControl)

go to http://192.168.xx.xx/app/list (insert your own hub's IP address)

a) Hit the "+New App" at the top right corner

b) Copy and paste the code from the corresponding txt file in the zip 

c) Hit the save button on the right inside of the screen


If the instructions above are not clear enough, you can refer to the troubleshooting section below with some pictures:

http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Init#Issue_.231:_I_don.27t_know_how_to_create_a_custom_smartapp


# 3) Go to the apps section of hubitats, execute ScheduleRoomTempControl 

http://192.168.xx.xx/installedapp/list (insert your own hub's IP address)

Click on "Add User app" in the right corner of the window and choose ScheduleRoomTempControl




CONFIGURATION STEPS
=====================

Refer to the ST community wiki, most of the use cases are documented with the input parameters needed.


http://thingsthataresmart.wiki/index.php?title=ScheduleRoomTempControl#Prerequisites
http://thingsthataresmart.wiki/index.php?title=ScheduleRoomTempControl#Configuration


TROUBLESHOOTING
=====================

http://thingsthataresmart.wiki/index.php?title=ScheduleRoomTempControl#Troubleshooting


SUPPORT
========

If needed, there are several support packages at my store.

www.ecomatiqhomes.com/store


