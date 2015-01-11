/**
 *  TED5000 Device Type for SmartThings
 *
 *  Author: badgermanus@gmail.com
 *  Code: https://github.com/jwsf/device-type.ted5000
 *
 * Copyright (C) 2014 Jonathan Wilson  <badgermanus@gmail.com>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions: The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
 
 preferences {
    input("url", "text", title: "URL", description: "The URL (including port number) of your TED5000 device (must be available to the public internet - example http://12.34.56.78:4444)", required: true)
    input("usr", "email", title: "Username", description: "The username configured in Network Settings on your TED5000", required: true)
    input("pass", "password", title: "Password", description: "The password configured in Network Settings on your TED5000", required: true)
}


metadata {
	// Automatically generated. Make future change here.
	definition (name: "TED5000", author: "yracine@yahoo.com") {
		capability "Power Meter"
		capability "Refresh"
		capability "Polling"
		capability "Energy Meter"
        attribute "cost", "string"
	}

	// simulator metadata
	simulator {
    }
	// UI tile definitions
	tiles {

		valueTile(	"power", 
        			"device.power" 
                 ) 
        {
            state(	"power",
                    label:'TED5000', 
                    unit: 'Watts',
                  	backgroundColors: [
                      [value: 1000, color: "green"],
                      [value: 3000, color: "orange"],
                      [value: 5000, color: "red"]
                  	]
                 )
		}
            valueTile(	"energy", 
                        "device.energy", 
                        unit:'Watts'
                     ) 
        {
            state("energy",
                    label:'TED5000',
                   	unit: 'Watts',
                  	backgroundColors: [
                      [value: 1000000, color: "green"],
                      [value: 1500000, color: "orange"],
                      [value: 2000000, color: "red"]
                  	]
                 )
		}
        standardTile("refresh", "device.power") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}


		main(["power", "energy"])
		details(["power", "energy", "refresh"])
	}
}



def poll() {
	doUpdate()
}

def refresh() {
	doUpdate()
}


def doUpdate() {

    // Build auth string
    def auth = settings.usr + ":" + settings.pass
	auth = auth.getBytes()
    auth = auth.encodeBase64()
    String authString = auth

	// Build URL
    def URL = settings.url + "/api/LiveData.xml"
    log.debug "Connecting to " + URL
    
    def params = [
        uri: URL,
        headers: [
            'Authorization': "Basic ${authString}"
        ]
    ]
    
    def powerNow, energy, cost
    
    // This closure is called to parse the XML if it is successfully retrieved
    def successClosure = { response ->
    	log.debug "Request to TED5000 was successful"
        powerNow = response.data.Power.Total.PowerNow
        energy = response.data.Power.Total.PowerMTD
        cost  = response.data.Cost.Total.CostMTD
        log.debug "Power: $powerNow W, month-to-date: $energy, cost=$cost"
        
	}
    
    try {
    
        // Get the XML from the TED5000
    	httpGet(params,  successClosure)
    } catch ( java.net.UnknownHostException e) {
    	log.error "Unknown host - check the URL and PORT for your device"
    	sendEvent name: "power", value: "Unknown host"
   	} catch (java.net.NoRouteToHostException t) {
    	log.error "No route to host - check the URL and PORT for your device " + URL
    	sendEvent name: "power", value: "No route to host"
    } catch (java.io.FileNotFoundException fnf) {
    	log.error "File not found - check the URL and PORT for your device " + URL
        sendEvent name: "power", value: "XML not found"
    } catch (java.io.IOException e) {
    	log.error "Authentication error - check USERNAME and PASSWORD. This can also occur if the TED5000 cannot be reached"
        sendEvent name: "power", value: "Auth error"
    } catch (any) {
    	log.error "General error trying to connect to TED and retrieve data " URL
    	sendEvent name: "power", value: "ERROR"
    }
    float totalCost=0, energyKw=0
    if (energy) {
    	energyKw = (energy.toFloat() / 1000).round(2) 
    	sendEvent (name: "energy", value: energyKw.toString(), unit:"kW")
    }
    if (cost) { 
    	totalCost = (cost.toFloat() / 100).round(2)
    	sendEvent (name: "cost", value: totalCost.toString(), unit:'$')
    }
    sendEvent (name: "power", value: powerNow, unit:"W")

}
