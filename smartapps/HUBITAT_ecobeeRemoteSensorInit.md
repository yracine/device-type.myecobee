 
 Readme.ecobeeRemoteSensorsInit
 ================================
 
 Copyright 2015-2020 Yves Racine
 LinkedIn profile: https://www.linkedin.com/in/yracine/

 Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 Developer's written consent.
 
 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 

Setup time: about 5-15 minutes depending on your ST skills

Please take note of the following:

You can now download the code at 

http://www.maisonsecomatiq.com/store

P.S. Technical support packages are also available.

PREREQUISITES
=====================

- Your ecobee thermostat fully operational (and connected to wifi), exposed to SmartThings via My Ecobee device (doesn't work with any other ecobee implementation)
- Your ecobee's proprietary sensors connected to your thermostat and fully operationnal under the ecobee portal
  
  Note: This smartapp is used to expose the ecobee's proprietary remote sensors. 
        For any other ST connnected sensors, refer to MonitorAndSetEcobeeTemp at my github or even better             
        ecobeeSetZoneWithSchedule which allows you to control which ST connected sensors are used at a given period of the day   
        according to the ecobee's schedules (Home, Away, Sleep, Awake, custom).
        
- Developer access to SmartThings (http://graph.api.smartthings.com/)
- <b>Location set for your ST account</b> 



INSTALLATION STEPS
=====================

# 1) Create a new device driver For MyRemoteSensor

go to http://192.168.xx.xx/driver/list (insert your own hub's IP address)

b) Hit the "+New Driver" at the top right corner

c) Copy and paste the code from the corresponding txt file in the zip (MyRemoteSensor.txt) 

d) Hit the save button on the right inside of the screen


# 2) Create a new smartapp (ecobeeRemoteSensorInit)

go to http://192.168.xx.xx/app/list (insert your own hub's IP address)

a) Hit the "+New App" at the top right corner

b) Copy and paste the code from the corresponding txt file in the zip 

c) Hit the save button on the right inside of the screen


If the instructions above are not clear enough, you can refer to the troubleshooting section below with some pictures:

http://thingsthataresmart.wiki/index.php?title=My_Ecobee_Init#Issue_.231:_I_don.27t_know_how_to_create_a_custom_smartapp


# 3) Go to the apps section of hubitat, execute ScheduleRoomTempControl 

http://192.168.xx.xx/installedapp/list (insert your own hub's IP address)

Click on "Add User app" in the right corner of the window and choose ecobeeRemoteSensorInit


# 4) Enable tracing, activate live Logging & Execute ecobeeRemoteSensorInit

b>a)For better support, just active the live logging and get more tracing
in the IDE by following these steps:</b>

http://192.168.xx.xx/logs


<b>b) Watch for any errors in the logs for My Ecobee Device and ecobeeRemoteSensorInit</b>

If you have a blank page in ecobeeRemoteSensorInit or "a unexpected error occurred" while
executing the smartapp:

<b>c) Make sure that all your remote sensors are actually reporting to the physical thermostat...
In some cases, remote sensors may have some connection issues with your physical thermostat, and my code is not able 
to detect them.
 
<b>d) If you have the following error msg in the logs:</b>

UnknownDeviceTypeException: Device type 'My RemoteSensor' in namespace 'yracine' not found

Then, it means that somehow, step 3 was not completed entirely.

<b>e) If you have the following error msg in the logs:</b>

'generateRemoteSensorEvents' is not supported. Supported commands: [poll, refresh, setHeatingSetpoint, ....'

This could mean that you're using the wrong Device driver (stock ecobee device)..


After execution, you should be able to see the new Sensor objects under your list of devices

http://192.168.xx.xx/device/list

# 5) Motion and temp Updates 

The temp and motion values will be updated in MyRemote Sensor according to the polling interval specified as input in the smartapp.

# 6) Repeat step 3 for each ecobee thermostat

If you have many ecobee thermostats, you'd need to execute ecobeeRemoteSensorInit for each one, but change the instance
name each time at the last page (Notifications And Other Settings):

"Assign a name for this SmartApp"



