ecobeeSetZoneWithSchedule
=========================

Author: Yves Racine

Email: services@maisonsecomatiq.com

linkedIn profile: https://www.linkedin.com/in/yracine/

Installation Setup time: about 5-10 minutes depending on your Hubitat skills.


PREREQUISITES
=====================

- (a) My Ecobee device installed & fully operational
- (b) Any other connected tstats (zigbee or zwave) fully connected and operational

INSTALLATION STEPS
=====================


# 1a) Create a new device driver (optional) For MyVirtualZone

go to http://192.168.xx.xx/driver/list (insert your own hub's IP address)

b) Hit the "+New Driver" at the top right corner

c) Copy and paste the code from the corresponding txt file in the zip (MyVirtualZoneForHubitat) 

d) Hit the save button on the right inside of the screen


# 1b) Create a new smartapp (ecobeeSetZoneWithSchedule)

go to http://192.168.xx.xx/app/list (insert your own hub's IP address)

a) Hit the "+New App" at the top right corner

b) Copy and paste the code from the corresponding txt file in the zip 

c) Hit the save button on the right inside of the screen


If the instructions above are not clear enough, you can refer to the troubleshooting section below with some pictures:

http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Init#Issue_.231:_I_don.27t_know_how_to_create_a_custom_smartapp



# 3) Go to the apps section of hubitats, execute ecobeeSetZoneWithSchedule

http://192.168.xx.xx/installedapp/list (insert your own hub's IP address)

Click on "Add User app" in the right corner of the window and choose ecobeeSetZoneWithSchedule


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


