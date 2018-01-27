
 
 readme.ecobeeRemoteSensorsInit
 ================================
 
 Copyright 2015 Yves Racine
 LinkedIn profile: ca.linkedin.com/pub/yves-racine-m-sc-a/0/406/4b/

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

http://www.maisonsecomatiq.com/#!store/tc3yr 

P.S. Technical support packages are also available.

PREREQUISITES
=====================

- Your ecobee thermostat fully operational (and connected to wifi)
- Developer access to SmartThings (http://graph.api.smartthings.com/)
- <b>Location set for your ST account</b> 

Under the ST mobile app, click on the 3-horizontal lines- "hamburger"- menu in the upper right corner, and then the "gear'" icon to review your location.

-  <b> Determine your shard, please consult this thread: </b>

https://community.smartthings.com/t/faq-how-to-find-out-what-shard-cloud-slice-ide-url-your-account-location-is-on/53923

<b>If you are on a different shard, you need to change the links below for your right shard. 
As an example, in North America,</b>

replace https://graph.api.smartthings.com/ide/devices by https://graph-na02-useast1.api.smartthings.com


INSTALLATION STEPS
=====================


# 1) If needed, you may want to update the My ecobee device, save & publish in the IDE

<b>The code will be sent to you via your paypal verified email address.</b>

If not already created, read the instructions at

https://github.com/yracine/device-type.myecobee

# 2) Create a new smartapp called ecobeeRemoteSensorInit, grab the code from the related txt file, save & publish
<b>a) Go to </b> 

https://graph.api.smartthings.com/ide/apps    (or whatever your shard is)

<b>b) Create a new Smartapp, click on new Smartapp on the right</b>

<b>c) From there, use the "From code" tab, and copy & paste it from the source file</b>

<b>d) Click "Save" & Publish (upper right)"</b>
# 3) Prior to calling ecobeeRemoteSensorInit, you need to create My Remote Sensor device handler in your IDE:

<b>a) Go to https://graph.api.smartthings.com/ide/devices</b>    (or whatever your shard is)

<b>b) Click on new Device Handler on the right</b>

<b>c) On the new Device Handler page, click on the "From Code" tab</b>

<b>d) Copy and paste the code from My Remote Sensor from the source file</b>

<b>e) Click save and publish</b>
# 4) Enable tracing, activate live Logging & Execute ecobeeRemoteSensorInit

for better support, just active the live logging and get more tracing
in the IDE by following these steps:

<b>a) Go to https://graph.api.smartthings.com/ide/logs</b>     (or whatever your shard is)

<b>b) Under the ST app, run ecobeeRemoteSensorInit on your smartphone/tablet</b>

<b>Click on the Smartapps link in the upper section of the following Marketspace screen (last icon at the bottom), and then MyApps (last item in the list).</b>

Scroll down to ecobeeRemoteSensorInit and click on it to execute it.

The smartapp will ask you to select the remote temp sensors and motion sensors to expose to SmartThings.


<b>c) Watch for any errors in the logs for My Ecobee Device and ecobeeRemoteSensorInit</b>



If you have a blank page in ecobeeRemoteSensorInit or "a unexpected error occurred" while
executing the smartapp:

<b>d) Make sure that you have the latest ecobee firmware (at least v3.6) at the tstat as motion sensors
are not detected prior to that firmware version. </b> 

And, of course, make sure that all your remote sensors are actually reporting to the physical thermostat...
In some cases, remote sensors may have some connection issues with your physical thermostat, and my code is not able 
to detect them.


<b>e) If you have the following error msg in the logs:</b>

physicalgraph.app.exception.UnknownDeviceTypeException: Device type 'My RemoteSensor' in namespace 'yracine' not found

Then, it means that somehow, step 3 was not completed entirely.

<b>f) If you have the following error msg in the logs:</b>

'generateRemoteSensorEvents' is not supported. Supported commands: [poll, refresh, setHeatingSetpoint, ....'

This could mean that you're using the wrong Device Type Handler (stock ecobee device or old version)..

- Kill your smartThings app, clean the cache under your OS (android or iOS) and reload 
the app (as the old device is cached)

- For iOS devices, in order to clear the cache, you'd need to uninstall & reinstall the smartThings app,
otherwise, you may encounter the blank page issue within the ecobeeRemoteInitSensor smartapp.


After execution, you should be able to see the new Sensor objects under  (or whatever your shard is)

https://graph.api.smartthings.com/device/list  

# 5) Motion and temp Updates 

The temp and motion values will be updated in MyRemote Sensor according to the polling interval specified as input in the smartapp.

# 6) Repeat step 4b for each ecobee thermostat

If you have many ecobee thermostats, you'd need to execute ecobeeRemoteSensorInit for each one, but change the instance
name each time at the last page (Notifications And Other Settings):

"Assign a name for this SmartApp"



