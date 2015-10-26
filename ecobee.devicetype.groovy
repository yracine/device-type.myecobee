 iterateSetThermostatSettings(tstatType, tstatSettings = []) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
			"iterateSetThermostatSettings>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateSetThermostatSettings>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateSetThermostatSettings>about to call setThermostatSettings for ${tstatlist}"
					log.debug "iterateSetThermostatSettings> about to call setThermostatSettings for ${tstatlist}"
				}
				setThermostatSettings("${tstatlist}",tstatSettings)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
void setThermostatSettings(thermostatId,tstatSettings = []) {
   	thermostatId= determine_tstat_id(thermostatId) 	    
	if (settings.trace) {
		log.debug
			"setThermostatSettings>called with values ${tstatSettings} for ${thermostatId}"
		sendEvent name: "verboseTrace", value:
			"setThermostatSettings>called with values ${tstatSettings} for ${thermostatId}"
	}
	def bodyReq = build_body_request('setThermostatSettings',null,thermostatId,null,tstatSettings)
	def statusCode=true
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		api('setThermostatSettings', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				sendEvent name: "verboseTrace", value:
						"setThermostatSettings>done for ${thermostatId}"
			} else {
				log.error 
					"setThermostatSettings> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				sendEvent name: "verboseTrace", value: 
					"setThermostatSettings> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		} /* end api call */                
	} /* end for */
}

// iterateSetHold: iterate thru all the thermostats under a specific account and create the hold event
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// settings can be anything supported by ecobee 
//		at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// extraHoldParams may be any other sethold or events properties  
//		see https://www.ecobee.com/home/developer/api/documentation/v1/objects/Event.shtml for more details
void iterateSetHold(tstatType, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = [], extraHoldParams=[]) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
			"iterateSetHold>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateSetHold>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateSetHold>about to call setHold for ${tstatlist}"
					log.debug "iterateSethold> about to call setHold for ${tstatlist}"
				}
				setHoldExtraParams("${tstatlist}", coolingSetPoint, heatingSetPoint, fanMode,
					tstatSettings, extraHoldParams)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '"123456789012","123456789013"') 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// This setHold function is kept for better compatibility with previous releases
void setHold(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = []) {
    
    // Call the setHoldExtraParams function with null value as extraHoldParams 
	setHoldExtraParams(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
		tstatSettings, null) 
}

// New version of setHold with access to extra setHold params when needed (ex. to set ventilator event's properties).
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// settings can be anything supported by ecobee at https://www.ecobee.com/home/developer/api/documentation/v1/objects/Settings.shtml
// extraHoldParams may be any other sethold or events properties  
//		see https://www.ecobee.com/home/developer/api/documentation/v1/objects/Event.shtml
void setHoldExtraParams(thermostatId, coolingSetPoint, heatingSetPoint, fanMode,
	tstatSettings = [], extraHoldParams=[]) {    
	Integer targetCoolTemp = null,targetHeatTemp = null
	def tstatParams = null
	thermostatId = determine_tstat_id(thermostatId)
	if (settings.trace) {
		log.debug
			"sethold>called with values ${coolingSetPoint}, ${heatingSetPoint}, ${fanMode}, ${tstatSettings} for ${thermostatId}"
		sendEvent name: "verboseTrace", value:
			"sethold>called with values ${coolingSetPoint}, ${heatingSetPoint}, ${fanMode}, ${tstatSettings} for ${thermostatId}"
	}
	def scale = getTemperatureScale()
	if (scale == 'C') {
		targetCoolTemp = (cToF(coolingSetPoint) * 10) // need to send temperature in F multiply by 10
		targetHeatTemp = (cToF(heatingSetPoint) * 10)
	} else {
		targetCoolTemp = coolingSetPoint * 10 // need to send temperature in F multiply by 10
		targetHeatTemp = heatingSetPoint * 10
	}
	/* if settings.holdType has a value, include it in the list of params
	 */
	if ((settings.holdType != null) && (settings.holdType.trim() != "")) {
		tstatParams = ((fanMode != null) & (fanMode != "")) ? 
          		[coolHoldTemp:targetCoolTemp, heatHoldTemp: targetHeatTemp, fan: fanMode, 
             			holdType:"${settings.holdType.trim()}"
             		] : 
        		[coolHoldTemp: targetCoolTemp, heatHoldTemp: targetHeatTemp, 
             			holdType:"${settings.holdType.trim()}"
			]
	} else {
		tstatParams = ((fanMode != null) & (fanMode != "")) ? 
			[coolHoldTemp:targetCoolTemp, heatHoldTemp: targetHeatTemp, fan: fanMode] : 
        	[coolHoldTemp: targetCoolTemp, heatHoldTemp: targetHeatTemp]

	}
	// Add the extraHoldParams if any
 	if ((extraHoldParams != null) && (extraHoldParams != [])) {
		tstatParams = tstatParams + extraHoldParams
    		
	}
	def bodyReq = build_body_request('setHold',null,thermostatId,tstatParams,tstatSettings)
	def statusCode=true
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		api('setHold', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				sendEvent name: "verboseTrace", value:
						"setHold>done for ${thermostatId}"
			} else {
				log.error 
					"setHold> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"setThermostatSettings> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		} /* end api call */
        
	}/* end while */
}

// iterateCreateVacation: iterate thru all the thermostats under a specific account and create the vacation
// tstatType =managementSet or registered (no spaces).  
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void iterateCreateVacation(tstatType, vacationName, targetCoolTemp,
	targetHeatTemp, targetStartDateTime, targetEndDateTime) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
			"iterateCreateVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateCreateVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateCreateVacation>about to call createVacation for ${tstatlist}"
					log.debug "iterateCreateVacation>about to call createVacation for ${tstatlist}"
				}
				createVacation("${tstatlist}", vacationName, targetCoolTemp, targetHeatTemp,
					targetStartDateTime, targetEndDateTime)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void createVacation(thermostatId, vacationName, targetCoolTemp, targetHeatTemp,
	targetStartDateTime, targetEndDateTime) {    
	thermostatId = determine_tstat_id(thermostatId)
	def vacationStartDate = String.format('%tY-%<tm-%<td', targetStartDateTime)
	def vacationStartTime = String.format('%tH:%<tM:%<tS', targetStartDateTime)
	def vacationEndDate = String.format('%tY-%<tm-%<td', targetEndDateTime)
	def vacationEndTime = String.format('%tH:%<tM:%<tS', targetEndDateTime)
	Integer targetCool = null,targetHeat = null
	def scale = getTemperatureScale()
	if (scale == 'C') {
		targetCool = (cToF(targetCoolTemp) * 10) as Integer // need to send temperature in F multiply by 10
		targetHeat = (cToF(targetHeatTemp) * 10) as Integer
	} else {
		targetCool = targetCoolTemp * 10 as Integer // need to send temperature in F multiply by 10
		targetHeat = targetHeatTemp * 10 as Integer
	}
	def vacationParams = [
		name: vacationName.trim(),
		coolHoldTemp: targetCool,
		heatHoldTemp: targetHeat,
		startDate: vacationStartDate,
		startTime: vacationStartTime,
		endDate: vacationEndDate,
		endTime: vacationEndTime
	]
	def bodyReq = build_body_request('createVacation',null,thermostatId,vacationParams)
	if (settings.trace) {
		log.debug "createVacation> about to call api with body = ${bodyReq} for ${thermostatId} "
	}
	api('createVacation', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			sendEvent name: "verboseTrace", value:
					"createVacation>done for ${thermostatId}"
		} else {
			log.error 
				"createVacation>error=${statusCode.toString()},message=${message} for ${thermostatId}"
			sendEvent name: "verboseTrace", value: 
				"createVacation>error=${statusCode.toString()},message=${message} for ${thermostatId}"
			// introduce a 1 second delay before re-attempting any other command                    
			delay(1000)                    
		}
	}
}

// iterateDeleteVacation: iterate thru all the thermostats under a specific account and delete the vacation
// tstatType =managementSet or registered (no spaces).  
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void iterateDeleteVacation(tstatType, vacationName) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0
    
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug
        	"iterateDeleteVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateDeleteVacation>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateDeleteVacation> about to call deleteVacation for ${tstatlist}"
					log.debug "iterateDeleteVacation> about to call deleteVacation for ${tstatlist}"
				}
				deleteVacation("${tstatlist}", vacationName)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void deleteVacation(thermostatId, vacationName) {
  
	thermostatId = determine_tstat_id(thermostatId)
	def vacationParams = [name: vacationName.trim()]
	def bodyReq = build_body_request('deleteVacation',null,thermostatId,vacationParams)
	api('deleteVacation', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			sendEvent name: "verboseTrace", value:
					"deleteVacation>done for ${thermostatId}"
		} else {
			log.error "deleteVacation> error= ${statusCode.toString()},message=${message} for ${thermostatId}"
			sendEvent name: "verboseTrace", value:
				"deleteVacation>error ${statusCode.toString()},message=${message} for ${thermostatId}"
			// introduce a 1 second delay before re-attempting any other command                    
			delay(1000)                    
		}
	}
}

// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// iterateResumeProgram: iterate thru all the thermostats under a specific account and resume their program
void iterateResumeProgram(tstatType) {
	Integer MAX_TSTAT_BATCH = get_MAX_TSTAT_BATCH()
	def tstatlist = null
	Integer nTstats = 0

	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		sendEvent name: "verboseTrace", value:
			"iterateResumeProgram>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { // process a batch of maximum 25 thermostats according to API doc
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"iterateResumeProgram> about to call resumeProgram for ${tstatlist}"
					log.debug "iterateResumeProgram> about to call resumeProgram for ${tstatlist}"
				}
				resumeProgram("${tstatlist}")
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}

// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// resumeAllFlag, if true then a resume all will be sent, otherwise, just a partial resume will be done.
void resumeProgram(thermostatId=settings.thermostatId, resumeAllFlag=true) {  
	thermostatId = determine_tstat_id(thermostatId)

	def resumeParams=null
    
 	if (resumeAllFlag==true) {
		resumeParams = [resumeAll: 'true']
	}
    
	def bodyReq = build_body_request('resumeProgram',null,thermostatId,resumeParams)
	if (settings.trace) {
		log.debug "resumeProgram> about to call api with body = ${bodyReq} for ${thermostatId}"
	}
	api('resumeProgram', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			sendEvent name: "verboseTrace", value:
					"resumeProgram>resume done for ${thermostatId}"
		} else {
			log.error 
				"resumeProgram>error=${statusCode.toString()},message=${message} for ${thermostatId}"
			sendEvent name: "verboseTrace", value:
				"resumeProgram>error=${statusCode.toString()},message=${message} for ${thermostatId}"
			// introduce a 1 second delay before re-attempting any other command                    
			delay(1000)                    
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// Get all groups related to a thermostatId or all groups
// thermostatId may only be 1 thermostat (not a list) or null (for all groups)
def getGroups(thermostatId) {
	thermostatId = determine_tstat_id(thermostatId)
	def ecobeeType = determine_ecobee_type_or_location("")
	if (settings.trace) {
		log.debug "getGroups>ecobee Type = $ecobeeType for thermostatId = $thermostatId"
	}
	if (ecobeeType.toUpperCase() != 'REGISTERED') {
		if (settings.trace) {
			log.debug "getGroups>managementSet is not a valid settings.ecobeeType for getGroups"
			sendEvent name: "verboseTrace", value:
				"getGroups>managementSet is not a valid settings.ecobeeType for getGroups"
		}
		data.groups = null
		return
	}
	def bodyReq = '{"selection":{"selectionType":"registered"}}'
	api('getGroups', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			data.groups = resp.data.groups
			if (settings.trace) {
				log.debug "getGroups>done for ${thermostatId}," +
					"group size = ${data.groups.size()}, groups data = ${data.groups}"
				sendEvent name: "verboseTrace", value:
					"getGroups>done for ${thermostatId}," +
					"group size = ${data.groups.size()}, groups data = ${data.groups}"
			}
			if (data.groups.size() == 0) {
				return
			}
			if ((thermostatId != null) && (thermostatId != "")) {
				if (data.groups.thermostats.size() > 0) {
					for (i in 0..data.groups.size() - 1) {
						def foundTstat = false
						for (j in 0..data.groups[i].thermostats.size() - 1) {
							if (data.groups[i].thermostats[j] == thermostatId) {
								if (settings.trace) {
									log.debug "getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}"
									sendEvent name: "verboseTrace", value:
										"getGroups>found group ${data.groups[i]} for thermostatId= ${thermostatId}"
								}
								foundTstat=true
							}
						}   
						if (!foundTstat) {
							data.groups[i].groupName = '' // Not the right group for this thermostat, set it to blanks
						}    
					}
				}
			}
		} else {
			log.error 
				"getGroups>> error=${statusCode.toString()},message=${message} for ${thermostatId}" 
			sendEvent name: "verboseTrace", value:
				"getGroups>error ${statusCode.toString()},message=${message} for ${thermostatId}"
			// introduce a 1 second delay before re-attempting any other command                    
			delay(1000)                    
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// iterateUpdateGroup: iterate thru all the Groups under a specific account and update their settings
// Get all groups related to a thermostatId and update them with the groupSettings
// thermostatId may only be 1 thermostat (not a list), if null or empty, then defaulted to this thermostatId (settings)
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// groupSettings may be a map of settings separated by ",", no spaces; 
// 	For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void iterateUpdateGroup(thermostatId, groupSettings = []) {
	thermostatId = determine_tstat_id(thermostatId)
	getGroups(thermostatId)
	if (settings.trace) {
		log.debug "iterateUpdateGroup>about to loop ${data.groups.size()}"
		sendEvent name: "verboseTrace", value:
			"iterateUpdateGroup> about to loop ${data.groups.size()}"
	}
	if (data.groups.size() == 0) {
		return
	}
	for (i in 0..data.groups.size() - 1) {
		def groupName = data.groups[i].groupName
		def groupRef = data.groups[i].groupRef
		def synchronizeSchedule = data.groups[i].synchronizeSchedule
		def synchronizeVacation = data.groups[i].synchronizeVacation
		def synchronizeSystemMode = data.groups[i].synchronizeSystemMode
		if (settings.trace) {
			sendEvent name: "verboseTrace", value:
				"iterateUpdateGroup> about to call updateGroup for ${groupName}"
			log.debug "iterateUpdateGroup> about to call updateGroup for ${groupName}, groupRef= ${groupRef}," +
				"synchronizeSystemMode=${synchronizeSystemMode}, synchronizeVacation=${synchronizeVacation}" +
				"synchronizeSchedule=${synchronizeSchedule}"
		}
		updateGroup(groupRef, groupName, thermostatId, groupSettings)
		if (settings.trace) {
			log.debug "iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}"
			sendEvent name: "verboseTrace", value:
				"iterateUpdateGroup>done for groupName = ${groupName}, groupRef= ${groupRef}"
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// If groupRef is not provided, it is assumed that a group creation needs to be done
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// groupSettings could be a map of settings separated by ",", no spaces; 
//	For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void updateGroup(groupRef, groupName, thermostatId, groupSettings = []) {
	String updateGroupParams
	def groupSettingsJson = new groovy.json.JsonBuilder(groupSettings)
	def groupSet = groupSettingsJson.toString().minus('{').minus('}')

	thermostatId = determine_tstat_id(thermostatId)
	if (settings.trace) {
		log.debug "updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}..."
		sendEvent name: "verboseTrace", value:
			"updateGroup> about to assemble bodyReq for groupName =${groupName}, thermostatId = ${thermostatId}, groupSettings=${groupSet}"
	}
	if ((groupRef != null) && (groupRef.trim() != "")) {

		updateGroupParams = '"groupRef":"' + groupRef.trim() + '","groupName":"' +
			groupName.trim() +
			'",' + groupSet + ',"thermostats":["' + thermostatId + '"]'
	} else {
		updateGroupParams = '"groupName":"' + groupName.trim() + '",' + groupSet +
			',"thermostats":["' + thermostatId + '"]'
	}
	def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' +
		updateGroupParams + '}]}'
	if (settings.trace) {
		log.debug "updateGroup> about to call api with body = ${bodyReq} for groupName =${groupName},thermostatId = ${thermostatId}..."
	}
	def statusCode=true        
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		api('updateGroup', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				sendEvent name: "verboseTrace", value:
						"updateGroup>done for groupName =${groupName}, ${thermostatId}"
			} else {
				log.error 
					"updateGroup> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"updateGroup>error ${statusCode.toString()},message=${message} for ${thermostatId}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		} /* end api call */                
                        
	} /* end while */
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void deleteGroup(groupRef, groupName) {
	String updateGroupParams

	if ((groupRef != null) && (groupRef.trim() != "")) {
		updateGroupParams = '"groupRef":"' + groupRef.trim() + '","groupName":"' +
			groupName.trim() + '"'
	} else {
		updateGroupParams = '"groupName":"' + groupName.trim() + '"'
	}
	if (settings.trace) {
		log.debug "deleteGroup> updateGroupParams=${updateGroupParams}"
		sendEvent name: "verboseTrace", value:
			"deleteGroup> updateGroupParams=${updateGroupParams}"
	}
	def bodyReq = '{"selection":{"selectionType":"registered"},"groups":[{' +
		updateGroupParams + '}]}'
	if (settings.trace) {
		log.debug "deleteGroup> about to call api with body = ${bodyReq} for groupName =${groupName}, groupRef = ${groupRef}"
	}
	api('updateGroup', bodyReq) {resp ->
		def statusCode = resp.data.status.code
		def message = resp.data.status.message
		if (!statusCode) {
			sendEvent name: "verboseTrace", value:
					"deleteGroup>done for groupName =${groupName},groupRef = ${groupRef}"
		} else {
			log.error 
				"deleteGroup> error= ${statusCode.toString()},message= ${message} for ${groupName},groupRef= ${groupRef}"
			sendEvent name: "verboseTrace", value:
				"deteteGroup>error ${statusCode.toString()},message= ${message} for ${groupName},groupRef= ${groupRef}"
			// introduce a 1 second delay before re-attempting any other command                    
			delay(1000)                    
		}
	}
}

// Only valid for Smart and ecobee3 thermostats (not for EMS)
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// groupSettings could be a map of settings separated by ",", no spaces; 
// For more details, see https://beta.ecobee.com/home/developer/api/documentation/v1/objects/Group.shtml
void createGroup(groupName, thermostatId, groupSettings = []) {
	updateGroup(null, groupName, thermostatId, groupSettings)
}

// thermostatId may only be 1 thermostat (not a list) 
// climate name is the name of the climate to be created (ex. "Bedtime").
// isOptimized is 'true' or 'false'
// isOccupied is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void createClimate(thermostatId, climateName, coolTemp, heatTemp, isOptimized, isOccupied,
	coolFan, heatFan) {
    
	updateClimate(thermostatId, climateName, 'false', null, coolTemp, heatTemp,
		isOptimized, isOccupied, coolFan, heatFan)
}

// thermostatId can be only 1 thermostat (not a list) 
// climate name is the name of the climate to be deleted (ex. "Bedtime").
void deleteClimate(thermostatId, climateName, substituteClimateName) {
	updateClimate(thermostatId, climateName, 'true', substituteClimateName, null,
		null, null, null, null, null)
}
// iterateSetClimate: iterate thru all the thermostats under a specific account and set their Climate
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// climate name is the name of the climate bet set to(ex. "Home", "Away").

void iterateSetClimate(tstatType, climateName) {
	Integer MAX_TSTAT_BATCH = 25
	def tstatlist = null
	Integer nTstats = 0
	def ecobeeType = determine_ecobee_type(tsatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug "iterateSetClimate> ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateSetClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (nTstats == 0) {
				tstatlist = Id
				nTstats = 1
			}
			if ((nTstats > MAX_TSTAT_BATCH) || (i == (data.thermostatCount - 1))) { 
				// process a batch of maximum 25 thermostats according to API doc
				setClimate("${tstatlist}", climateName, sensors)
				tstatlist = Id
				nTstats = 1
			} else {
				tstatlist = tstatlist + "," + Id
				nTstats++ 
			}
		}
	}
}
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
//	The settings.thermostatId (input) is the default one
// climate name is the name of the climate to be set to (ex. "Home", "Away").
// paramsMap: parameters Map, ex. [holdType:'nextTransition']  
//	see https://www.ecobee.com/home/developer/api/documentation/v1/functions/SetHold.shtml 

void setClimate(thermostatId, climateName, paramsMap=[]) {
	def climateRef = null
	def tstatParams

	if (thermostatId) {
//		call getThermostatInfo if a value for thermostatId is provided to make sure to have the right thermostat information

		getThermostatInfo(thermostatId) 
	}        
	thermostatId = determine_tstat_id(thermostatId)
	for (i in 0..data.thermostatList.size() - 1) {
		def foundClimate = data.thermostatList[i].program.climates.find{ it.name.toUpperCase() == climateName.toUpperCase() }
		if (foundClimate) {
			climateRef = foundClimate.climateRef
			if (settings.trace) {
				log.debug "setClimate>climateRef ${climateRef} found for thermostatId =${data.thermostatList[i].identifier}"
				sendEvent name: "verboseTrace", value:
					"setClimate>Climate ${climateRef} found for thermostatId =${data.thermostatList[i].identifier}"
			}        
		} else {
			if (settings.trace) {
				log.debug "setClimate>Climate ${climateName} not found for thermostatId =${data.thermostatList[i].identifier}"
				sendEvent name: "verboseTrace", value:
					"setClimate>Climate ${climateName} not found for thermostatId =${data.thermostatList[i].identifier}"
			}        
			continue
		}
        
		tstatParams = ((settings.holdType != null) && (settings.holdType.trim() != "")) ?
				[holdClimateRef:"${climateRef}", holdType:"${settings.holdType.trim()}"
				] :
				[holdClimateRef:"${climateRef}"
				]
           	
		tstatParams = tstatParams + paramsMap            
		def bodyReq = build_body_request('setHold',null, data.thermostatList[i].identifier,tstatParams)	
		def statusCode=true
		int j=0        
		while ((statusCode) && (j++ <2)) { // retries once if api call fails
            
			api('setHold', bodyReq) {resp ->
				statusCode = resp.data.status.code
				def message = resp.data.status.message
				if (!statusCode) {
					/* Post the setClimate value */    
					sendEvent(name: 'setClimate', value: climateName)
					sendEvent name: "verboseTrace", value:
							"setClimate>done for thermostatId =${data.thermostatList[i].identifier},climateName =${climateName}"
				} else {
					log.error 
						"setClimate>error ${statusCode.toString()},message=${message} while setting climate ${climateName} for thermostatId =${data.thermostatList[i].identifier}"
					sendEvent name: "verboseTrace", value:
						"setClimate>error ${statusCode.toString()},message=${message} while setting climate ${climateName} for thermostatId =${data.thermostatList[i].identifier}"
					// introduce a 1 second delay before re-attempting any other command                    
					delay(1000)                    
				} /* end if statusCode */
			} /* end api call */                   
		} /* end while */               
	} /* end for */    
}
// iterateUpdateClimate: iterate thru all the thermostats under a specific account and update their Climate
// tstatType =managementSet or registered (no spaces).  May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// climate name is the name of the climate to be updated (ex. "Home", "Away").
// deleteClimateFlag is set to 'true' if the climate needs to be deleted (should not be part of any schedule beforehand)
// subClimateName is the climateName that will replace the original climateName in the schedule (can be null when not needed)
// isOptimized is 'true' or 'false'
// isOccupied is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void iterateUpdateClimate(tstatType, climateName, deleteClimateFlag,
	subClimateName, coolTemp, heatTemp, isOptimized, isOccupied, coolFan, heatFan) {
    
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	if (settings.trace) {
		log.debug "iterateUpdateClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
		sendEvent name: "verboseTrace", value:
			"iterateUpdateClimate>ecobeeType=${ecobeeType},about to loop ${data.thermostatCount} thermostat(s)"
	}
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def Id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		if (connected == 'true') {
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:
					"iterateUpdateClimate> about to call updateClimate for thermostatId =${id}"
				log.debug "iterateUpdateClimate> about to call updateClimate for thermostatId =${id}"
			}
			updateClimate(Id, climateName, deleteClimateFlag, subClimateName, coolTemp,
				heatTemp, isOptimized, isOccupied, coolFan, heatFan)
		}
	}
}

// thermostatId may only be 1 thermostat (not a list) 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// climate name is the name of the climate to be updated (ex. "Home", "Away").
// deleteClimateFlag is set to 'true' if the climate needs to be deleted (should not be part of any schedule beforehand)
// substituteClimateName is the climateName that will replace the original climateName in the schedule (can be null when not needed)
// isOptimized is 'true' or 'false'
// coolFan & heatFan's mode is 'auto' or 'on'
void updateClimate(thermostatId, climateName, deleteClimateFlag,
		substituteClimateName, coolTemp, heatTemp, isOptimized, isOccupied, coolFan, heatFan) {
	Integer targetCoolTemp,targetHeatTemp
	def foundClimate = null, foundSubstituteClimate =null
	String scheduleAsString
	def substituteClimateRef = null
	def climateRefToBeReplaced = null

	if (thermostatId) {
//		call getThermostatInfo if a value for thermostatId is provided to make sure to have the right thermostat information

		getThermostatInfo(thermostatId) 
	}        
	thermostatId = determine_tstat_id(thermostatId)
	if ((coolTemp != null) && (heatTemp != null)) {
		if (settings.trace) {
			log.debug "updateClimate>thermostatId =${thermostatId} coolTemp=${coolTemp}, heatTemp= ${heatTemp}"
			sendEvent name: "verboseTrace", value:
				"updateClimate>thermostatId =${thermostatId} coolTemp=${coolTemp}, heatTemp= ${heatTemp}"
		}
		def scale = getTemperatureScale()
		if (scale == 'C') {
			targetCoolTemp = (cToF(coolTemp) * 10) as Integer // need to send temperature in F multiply by 10
			targetHeatTemp = (cToF(heatTemp) * 10) as Integer
		} else {
			targetCoolTemp = (coolTemp * 10) as Integer // need to send temperature in F multiply by 10
			targetHeatTemp = (heatTemp * 10) as Integer
		}
	}
	if ((substituteClimateName != null) && (substituteClimateName != "")) { // find the subsituteClimateRef for the subsitute Climate Name if not null

		foundClimate = data.thermostatList[0].program.climates.find{ it.name.toUpperCase() == climateName.toUpperCase() }
		foundSubstituteClimate = data.thermostatList[0].program.climates.find { it.name.toUpperCase() == substituteClimateName.toUpperCase() }
		if (foundClimate) {
			climateRefToBeReplaced = foundClimate.climateRef       
			if (settings.trace) {
				log.debug "updateClimate>climateRef ${climateRefToBeReplaced} found for climateName ${climateName}"
				sendEvent name: "verboseTrace", value:
					"updateClimate>climateRef ${climateRefToBeReplaced} found for climateName ${climateName}"
			}                    
		} else {
			if (settings.trace) {
				log.debug "updateClimate>climateName ${substituteClimateName} for substitution not found"
				sendEvent name: "verboseTrace", value:
					"updateClimate>substituteClimateName ${substituteClimateName} for substitution not found"
			}
			return
		}
		if (foundSubstituteClimate) {
 			substituteClimateRef = foundSubstituteClimate.climateRef       
			if (settings.trace) {
				log.debug "updateClimate>substituteClimateRef ${substituteClimateRef} found for ${substituteClimateName}"
				sendEvent name: "verboseTrace", value:
					"updateClimate>substituteClimateRef ${substituteClimateRef} found for ${substituteClimateName} "
			}
		} else {
			if (settings.trace) {
				log.debug "updateClimate>substituteClimateName ${substituteClimateName} for substitution not found"
				sendEvent name: "verboseTrace", value:
					"updateClimate>substituteClimateName ${substituteClimateName} for substitution not found"
			}
			return
		}
	}
	def bodyReq =
		'{"selection":{"selectionType":"thermostats","selectionMatch":"' +
		thermostatId + '"}'
	bodyReq = bodyReq + ',"thermostat":{"program":{"schedule":['
	for (i in 0..data.thermostatList[0].program.schedule.size() - 1) {
		bodyReq = bodyReq + '['
		// loop thru all the schedule items to create the json structure
		// replace the climateRef with right substituteClimateRef if needed
		for (j in 0..data.thermostatList[0].program.schedule[i].size() - 1) {
			if (substituteClimateRef != null) {
				scheduleAsString = (data.thermostatList[0].program.schedule[i][j] ==
					climateRefToBeReplaced) ?
					'"' + substituteClimateRef + '"' :
					'"' + data.thermostatList[0].program.schedule[i][j].toString() + '"'
			} else {
				scheduleAsString = '"' + data.thermostatList[0].program.schedule[i][j].toString() +
						'"'
			}
			bodyReq = (j == 0) ? bodyReq + scheduleAsString : bodyReq + ',' +
				scheduleAsString
		}
		bodyReq = (i == (data.thermostatList[0].program.schedule.size() - 1)) ?
			bodyReq + ']' : bodyReq + '],'
	}
	bodyReq = bodyReq + '],"climates":['
	foundClimate = false
	for (i in 0..data.thermostatList[0].program.climates.size() - 1) {
		if ((i != 0) && (i < data.thermostatList[0].program.climates.size())) {
			bodyReq = bodyReq + ','
		}
		if ((deleteClimateFlag == 'true') && (climateName.trim().toUpperCase() ==
			data.thermostatList[0].program.climates[i].name.toUpperCase())) {
			foundClimate = true
			if (settings.trace) {
				log.debug "updateClimate>thermostatId =${thermostatId}, deleteClimateFlag=${deleteClimateFlag},Climate ${climateName} to be deleted..."
				sendEvent name: "verboseTrace", value:
					"updateClimate>thermostatId =${thermostatId}, deleteClimateFlag=${deleteClimateFlag},Climate ${climateName} to be deleted..."
			}
			bodyReq = bodyReq.substring(0, (bodyReq.length() - 1)) // trim the last ','
		} else if ((deleteClimateFlag == 'false') && (climateName.trim().toUpperCase() ==
			data.thermostatList[0].program.climates[i].name.toUpperCase())) {
			// update the Climate Object
			foundClimate = true
			bodyReq = bodyReq + '{"name":"' + data.thermostatList[0].program.climates[i]
				.name + '","climateRef":"' +
				data.thermostatList[0].program.climates[i].climateRef + '","coolTemp":"' +
				targetCoolTemp + '","heatTemp":"' + targetHeatTemp + '","isOptimized":"' + isOptimized +
					'","isOccupied":"' + isOccupied +                
					'","coolFan":"' + coolFan + '","heatFan":"' + heatFan + '"}'
		} else {
			bodyReq = bodyReq + '{"name":"' + data.thermostatList[0].program.climates[i]
				.name + '","climateRef":"' +
				data.thermostatList[0].program.climates[i].climateRef + '"}'
		}
	}
	if (!foundClimate) { // this is a new Climate object to create
		if (settings.trace) {
			log.debug "updateClimate>thermostatId =${thermostatId},Climate ${climateName} to be created"
			sendEvent name: "verboseTrace", value:
					"updateClimate>thermostatId =${thermostatId},Climate ${climateName} to be created"
		}

		bodyReq = bodyReq + ',{"name":"' + climateName.capitalize().trim() +
			'","coolTemp":"' + targetCoolTemp +
			'","heatTemp":"' + targetHeatTemp + '","isOptimized":"' + isOptimized +
			'","isOccupied":"' + isOccupied  + '","coolFan":"' + coolFan + '","heatFan":"' + heatFan + '"' +
			',"ventilatorMinOnTime":"5"}' // workaround due to ecobee create Climate bug, to be removed
	}
	bodyReq = bodyReq + ']}}}'

	def statusCode=true
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		api('updateClimate', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				sendEvent name: "verboseTrace", value:
						"updateClimate>done for thermostatId =${thermostatId},climateName =${climateName}"
			} else {
				log.error 
					"updateClimate>error=${statusCode.toString()},message=${message} for thermostatId =${thermostatId},climateName =${climateName}"
				sendEvent name: "verboseTrace", value:
					"updateClimate>error ${statusCode.toString()},message=${message} for thermostatId =${thermostatId},climateName =${climateName}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		} /* end api call */               
	} /* end for */
}
// controlPlug() only works with Smart-02 thermostats as smart plugs are now obsolete
// thermostatId may only be 1 thermostat (not a list) 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// plugName is the name of the plug name to be controlled 
// plugState is the state to be set
// plugSettings are the different settings at https://www.ecobee.com/home/developer/api/documentation/v1/functions/ControlPlug.shtml
void controlPlug(thermostatId, plugName, plugState, plugSettings = []) {
	def plugSettingsJson = new groovy.json.JsonBuilder(plugSettings)
	def plugSet = plugSettingsJson.toString().minus('{').minus('}')

	thermostatId = determine_tstat_id(thermostatId)
	if (settings.trace) {
		log.debug "controlPlug> about to assemble bodyReq for plugName =${plugName}, thermostatId = ${thermostatId}, groupSettings=${plugSet}..."
		sendEvent name: "verboseTrace", value:
			"updateGroup> about to assemble bodyReq for plugName =${plugName}, thermostatId = ${thermostatId}, plugSettings=${plugSet}"
	}
	def bodyReq =
		'{"selection":{"selectionType":"thermostats","selectionMatch":"' +
		thermostatId + '"}'
	bodyReq = bodyReq +
		',"functions":[{"type":"controlPlug","params":{"plugName":"' + plugName +
		'","plugState":"' + plugState + '"'

	// add the plugSettings if any
	if (plugSettings) {
		bodyReq = bodyReq + ',' + plugSet
	}
	bodyReq = bodyReq + '}}]}'
	def statusCode=true
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
        api('controlPlug', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				sendEvent name: "verboseTrace", value:
						"controlPlug>done for thermostatId =${thermostatId},plugName =${plugName}"
				// post plug values 
 				def plugEvents = [
					plugName: plugName, 
 					plugState: plugState
 				]
                
				if (plugSettings) {
					plugEvents = plugEvents + [plugSettings: plugSet]
				}
				generateEvent(plugEvents)
			} else {
				log.error 
					"controlPlug>error=${statusCode.toString()},message=${message} for thermostatId =${thermostatId},plugName =${plugName}"
				sendEvent name: "verboseTrace", value:
					"controlPlug>error ${statusCode.toString()},message=${message} for thermostatId =${thermostatId},plugName =${plugName}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		} /* end api call */               
	} /* end while */
}
// thermostatId shall refer to a single thermostat to avoid processing too much data
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// reportColumn shall be 1 component only (ex. auxHeat1, coolComp1, fan, ventilator, humidifier, dehumidifier)
// startDateTime and endDateTime should be in UTC timezone format
// startInterval & endInterval may be null. In that case, the intervals are calculated using the startDateTime and endDateTime
// includeSensorData may be 'true' or 'false'
// postData may be 'true' or 'false', by default the latter

// See https://www.ecobee.com/home/developer/api/documentation/v1/operations/get-runtime-report.shtml for more details
// If you want to retrieve the data, then set postData to 'true', by default, reportdata and sensorData are not posted
//
// 	One can loop on the reportData or sensorData with the following logic 
//		ecobee.getReportData("", oneHourAgo, now, null, null, "dehumidifier",'false','true')
//		def reportData = ecobee.currentReportData.toString().split(",,")
//            
//		for (i in 0..reportData.size()-1) {
//			def rowDetails
//			try {
//				rowDetails = reportData[i].split(',')
//				def dateReportData = rowDetails[0]
//				def timeReportData = rowDetails[1]
//				def valueReportData = rowDetails[2]
//			
//			} catch (any)
//				log.error "no values ($rowDetails) for $component at $i"  // values are not always provided
//				continue
//			} 
//		}

void getReportData(thermostatId, startDateTime, endDateTime, startInterval, endInterval, reportColumn, includeSensorData, postData='false') {
	Double TOTAL_MILLISECONDS_PER_DAY=(24*60*60*1000)	
	def REPORT_TIME_INTERVAL=5
	def REPORT_MAX_INTERVALS_PER_DAY=287
	int beginInt, endInt

	thermostatId = determine_tstat_id(thermostatId)
	Double nbDaysInPeriod = ((endDateTime.getTime() - startDateTime.getTime()) /TOTAL_MILLISECONDS_PER_DAY).round(2)
        
	if (nbDaysInPeriod > 2) {  // Report period should not be bigger than 2 days to avoid summarizing too much data.
		if (settings.trace) {
			sendEvent name: "verboseTrace", value:"getReportData> report's period too big (${nbDaysInPeriod.toString()} > 2)"
			log.error "getReportData> report's period too big (${nbDaysInPeriod.toString()} >1)"
			return
		}
	} 
	if (thermostatId.contains(',')) {  // Report should run on a single thermostat only
		if (settings.trace) {
			sendEvent name: "verboseTrace", value:"getReportData> report should run on a single thermostatId only (${thermostatId})"
			log.error "getReportData> report should run on a single thermostatId only (${thermostatId})"
		}
		return
	}
    
	if (reportColumn.contains(',')) {  // Report should run on a single component only
		if (settings.trace) {
			sendEvent name: "verboseTrace", value:"getReportData> report should run on a single component only (${reportColumn})"
			log.error "getReportData> report should run on a single component only (${reportColumn})"
		}
		return
	}
	if (settings.trace) {
		log.debug "getReportData> startDate in UTC timezone =${String.format('%tF %<tT',startDateTime)}," +
        	"endDate in UTC timezone =${String.format('%tF %<tT',endDateTime)}"
	}
    


	// Check the interval Revision in order to avoid polling ecobee servers unnecessarily 
    
	def hasRevisionChanged=getThermostatRevision("", thermostatId)
	def newReportRevision = device.currentValue('intervalRevision')
	if (settings.trace) {
		log.debug ("getReportData>state.reportRevision=${state?.reportRevision},newReportRevision=${newReportRevision}, component=$reportColumn...")
	}
	if ((state?.reportRevision == newReportRevision) && (state?.componentReport==reportColumn)) {
		// Trying to produce report data on same component with same interval Revision
		if (settings.trace) {
			log.debug ("getReportData>called in a very short time with same component=${state.componentReport}, exiting...")
		}
		return
	
	}
	state?.reportRevision = newReportRevision
	state?.componentReport = reportColumn
    
	beginInt = (startInterval == null)? get_interval(startDateTime): startInterval.toInteger()
	endInt = (endInterval == null)? get_interval(endDateTime): endInterval.toInteger()
	Calendar startCalendar = startDateTime.toCalendar()
	Calendar endCalendar = endDateTime.toCalendar()
	if (settings.trace) {
		log.debug "getReportData> startInterval = ${beginInt}, endInterval = ${endInt}"
	}
	def bodyReq = '{"startInterval":"' + beginInt.toString() + '","endInterval":"' + endInt.toString() + 
    				'","startDate":"' + String.format('%tY-%<tm-%<td',startDateTime) + '",' + '"endDate":"' +
					String.format('%tY-%<tm-%<td',endDateTime) + '",' +
					'"columns":"' +  reportColumn + '","includeSensors":"' + includeSensorData + '",' +
					'"selection":{"selectionType":"thermostats","selectionMatch":"' + thermostatId + '"}}'
	if (settings.trace) {
		log.debug "getReportData> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}..."
	}
	def statusCode=true
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		api('runtimeReport', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				data.reportList = resp.data.reportList
				data.startDate = resp.data.startDate
				data.endDate = resp.data.endDate
				data.startInterval = resp.data.startInterval
				data.endInterval = resp.data.endInterval
				data.columns = resp.data.columns
				if (includeSensorData=='true') {
					data.sensorList =  resp.data.sensorList
				}  
				def reportData = ""
				def reportSensorMetadata=""
				def reportSensorData =""
                
				if (postData=='true') {
					if (settings.trace) {
						log.debug "getReportData>about to post reportData = $data.reportList.rowList[0].toString()"
					}
					reportData = data.reportList?.rowList[0].toString().minus('[').minus(']')
					if (includeSensorData=='true') {
						reportSensorMetadata = new groovy.json.JsonBuilder(data.sensorList?.sensors[0])  // metadata is in Json format
						reportSensorData = data.sensorList?.data[0].toString().minus('[').minus(']')
					}   
				}   
				generateEvent(['reportData':reportData,'reportSensorMetadata':reportSensorMetadata,
					'reportSensorData':reportSensorData])
				if (settings.trace) {
					log.debug "getReportData> startDate= ${data.startDate}"
					log.debug "getReportData> endDate= ${data.endDate}"
					log.debug "getReportData> startInterval= ${data.startInterval}"
					log.debug "getReportData> endInterval= ${data.endInterval}"
					log.debug "getReportData> columns= ${data.columns}"
					log.debug "getReportData> reportList= ${data.reportList}"
					log.debug "getReportData> sensorList= ${data.sensorList}"
					log.debug "getReportData> postData= ${postData}"
				}
				sendEvent name: "verboseTrace", value:"getReportData>done for thermostatId ${thermostatId}"
        	        
			} else {
				log.error 
					"getReportData> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"getReportData>error=${statusCode},message= ${message} for ${thermostatId}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		} /* end api call */                
	} /* end while */
}

private int get_interval(Date dateTime) {
	def REPORT_TIME_INTERVAL=5
	Calendar c = dateTime.toCalendar()
	int intervalHr = (c.get(Calendar.HOUR_OF_DAY)>0) ? 
		(c.get(Calendar.HOUR_OF_DAY) * 60) / REPORT_TIME_INTERVAL :0 
	int intervalMin = (c.get(Calendar.MINUTE)>= REPORT_TIME_INTERVAL) ? 
		c.get(Calendar.MINUTE) / REPORT_TIME_INTERVAL :0 
	if (settings.trace) {
		log.debug "get_interval> Calendar hour= ${c.get(Calendar.HOUR_OF_DAY)}"
		log.debug "get_interval> Calendar minute= ${c.get(Calendar.MINUTE)}"
		log.debug "get_interval> intervalHr ${intervalHr}"
		log.debug "get_interval> intervalMin= ${intervalMin}"
	}
	return (intervalHr + intervalMin)
}

// getReportData() must be called prior to calling generateReportRuntimeEvents() function
// component may be auxHeat1, compCool1, fan, ventilator, humidifier, dehumidifier, etc.
// startInterval & endInterval may be null. 
//	Intervals will be then defaulted to the ones used to generate the report
// typeEvent may be 'daily' or others

void generateReportRuntimeEvents(component, startDateTime, endDateTime, startInterval, endInterval, typeEvent ='daily') {
	Double TOTAL_MILLISECONDS_PER_DAY=(24*60*60*1000)	
	def REPORT_TIME_INTERVAL=5
	def REPORT_MAX_INTERVALS_PER_DAY=287
	int beginInt, endInt
    
	Double nbDaysInPeriod = ((endDateTime.getTime() - startDateTime.getTime()) /TOTAL_MILLISECONDS_PER_DAY).round(2)
        
	if (nbDaysInPeriod > 2) {  // Report period should not be bigger than 2 days to avoid summarizing too much data.
		if (settings.trace) {
			sendEvent name: "verboseTrace", value:"generateReportRuntimeEvents> report's period too big (${nbDaysInPeriod.toString()} > 2)"
			log.error "generateReportRuntimeEvents> report's period too big (${nbDaysInPeriod.toString()} >1)"
			return
		}
	} 

	float totalRuntime
	float runtimeInMin   // Calculations are done in minutes
    
	beginInt = (startInterval == null)? get_interval(startDateTime): startInterval.toInteger()
	endInt = (endInterval == null)? get_interval(endDateTime): endInterval.toInteger()
	Calendar startCalendar = startDateTime.toCalendar()
	Calendar endCalendar = endDateTime.toCalendar()
	if (endCalendar.get(Calendar.DATE) != startCalendar.get(Calendar.DATE)) {
		endInt += nbDaysInPeriod.intValue() * REPORT_MAX_INTERVALS_PER_DAY 
	}
	if (settings.trace) {
		log.debug "generateReportRuntimeEvents> startInterval = ${beginInt}, endInterval = ${endInt}"
	}
	if (component.contains('auxHeat1')) {
		totalRuntime = calculate_report_stats('auxHeat1', beginInt, endInt, 'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
 			sendEvent name: "auxHeat1RuntimeDaily", value: runtimeInMin.toString() 
		} else {	            
 			sendEvent name: "auxHeat1RuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('auxHeat2')) {
		totalRuntime = calculate_report_stats('auxHeat2', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "auxHeat2RuntimeDaily", value: runtimeInMin.toString()
 		} else {           
			sendEvent name: "auxHeat2RuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('auxHeat3')) {
		totalRuntime = calculate_report_stats('auxHeat3', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "auxHeat3RuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "auxHeat3RuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('compCool1')) {
		totalRuntime = calculate_report_stats('coolComp1', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "compCool1RuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "compCool1RuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('compCool2')) {
		totalRuntime = calculate_report_stats('coolComp2', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "coolComp2RuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "coolComp2RuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('fan')) {
		totalRuntime = calculate_report_stats('fan', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "fanRuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "fanRuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('ventilator')) {
 		totalRuntime = calculate_report_stats('ventilator', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "ventilatorRuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "ventilatorRuntimeInPeriod", value: runtimeInMin.toString()
		}
	}
	if (component.contains('dehumidifier')) {
		totalRuntime = calculate_report_stats('dehumidifier', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "dehumidifierRuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "dehumidifierRuntimeInPeriod", value: runtimeInMin.toString()
		}                
	} else if (component.contains('humidifier')) {
		totalRuntime = calculate_report_stats('humidifier', beginInt, endInt,'report')
		runtimeInMin = (totalRuntime >60) ? (totalRuntime / 60).round(2) :0
		if (typeEvent== 'daily') {
			sendEvent name: "humidifierRuntimeDaily", value: runtimeInMin.toString()
		} else {            
			sendEvent name: "humidifierRuntimeInPeriod", value: runtimeInMin.toString()
                
		}
	}
}
// getReportData() must be called prior to calling the generateReportSensorStatsEvents
// sensorId may be null or a specific sensorId as specified in the sensor metadata
//	see https://www.ecobee.com/home/developer/api/documentation/v1/objects/RuntimeSensorMetadata.shtml
// startInterval & endInterval may be null. 
//	Intervals will be then defaulted to the ones used to generate the report
// operation may be one or several stats separated by comma (ex. avg, min, max, total)

void generateReportSensorStatsEvents(sensorId, startDateTime, endDateTime, startInterval, endInterval, operation) {
	Double TOTAL_MILLISECONDS_PER_DAY=(24*60*60*1000)	
	def REPORT_TIME_INTERVAL=5
	def REPORT_MAX_INTERVALS_PER_DAY=287
	int beginInt, endInt
	boolean foundSensor=false
    
	Double nbDaysInPeriod = ((endDateTime.getTime() - startDateTime.getTime()) /TOTAL_MILLISECONDS_PER_DAY).round(2)

	float runtimeSensorStat
    
	beginInt = (startInterval == null)? get_interval(startDateTime): startInterval.toInteger()
	endInt = (endInterval == null)? get_interval(endDateTime): endInterval.toInteger()
	Calendar startCalendar = startDateTime.toCalendar()
	Calendar endCalendar = endDateTime.toCalendar()
	if (endCalendar.get(Calendar.DATE) != startCalendar.get(Calendar.DATE)) {
		endInt += nbDaysInPeriod.intValue() * REPORT_MAX_INTERVALS_PER_DAY 
	}
	if (settings.trace) {
		log.debug "generateSensorRuntimeEvents> startInterval = ${beginInt}, endInterval = ${endInt}"
	}
	if (sensorId != null) {
		foundSensor = data.sensorList.sensors[0].find{ it.sensorId == sensorId }
	}
	if (!foundSensor) {
		if (settings.trace) {
			log.error "generateReportSensorStatsEvents> sensor ${sensorId} not found in last sensor data from getReportData()"
		}
		return
	}    
	if (operation.contains('avg')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor', 'avg')
 		sendEvent name: "reportSensorAvgInPeriod", value: runtimeSensorStat.round(2).toString() 
	}
	if (operation.contains('min')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor', 'min')
 		sendEvent name: "reportSensorMinInPeriod", value: runtimeSensorStat.round(2).toString()
	}
	if (operation.contains('max')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor', 'max')
 		sendEvent name: "reportSensorMaxInPeriod", value: runtimeSensorStat.round(2).toString()
	}
	if (operation.contains('total')) {
		runtimeSensorStat = calculate_report_stats(sensorId, beginInt, endInt, 'sensor')
 		sendEvent name: "reportSensorTotalInPeriod", value: runtimeSensorStat.round(2).toString()
	}
}

private float calculate_report_stats(component, startInterval, endInterval, typeData, operation='total') {
	int total=0	
	int nbRows=0
	int max=0
	def min = null
	int rowValue
    
	int startRow = (startInterval != null) ? startInterval: data.startInterval.toInteger()
	int rowCount = (typeData=='sensor')? data.sensorList.data[0].size(): data.reportList.rowList[0].size()
	int lastRow =  Math.min(endInterval,rowCount)

	if (settings.trace) {
		log.debug "calculate_report_stats> about to process rowCount= ${rowCount},startRow=${startRow},lastRow=${lastRow}"
	}
	if (lastRow <= startRow) {    
		if (settings.trace) {
			log.error "calculate_report_stats>lastRow=${lastRow} is not greater than startRow=${startRow}"
		}
		return null
	}
	for (i in startRow..lastRow -1) {
		def rowDetails = (typeData=='sensor')? data.sensorList.data[0][i].split(","): data.reportList.rowList[0][i].split(",")
		try {
			rowValue = rowDetails[2]?.toInteger()
			total += rowValue
			nbRows++
			max = Math.max(rowValue,max)
			min = (min==null) ? rowValue : Math.min(rowValue,min)
		} catch (any) {
			if (settings.trace) {
				log.debug "calculate_report_stats> no values ($rowDetails) for $component at $i"
			}
			continue
		}	
	}
    
	int avg = ((nbRows >1) ? total/nbRows:total)
	if (settings.trace) {
		log.debug "calculate_report_stats> total= ${total} for $component component"
		log.debug "calculate_report_stats> nbRows with value= ${nbRows} for $component component"
		log.debug "calculate_report_stats> avg= ${avg} for $component component"
		log.debug "calculate_report_stats> max= ${max} for $component component"
		log.debug "calculate_report_stats> min= ${min} for $component component"
	}
	if (operation == 'avg') {
		return avg    
	}
	if (operation == 'max') {	
		return max    	
	}
	if (operation == 'min') {	
		return min    	
	}
	return total
}

// getThermostatInfo() should be called prior to calling generateRemoteSensorEvents() method to get the latest data
// thermostatId shall refer to a single thermostat to avoid processing too much data
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
// postData may be 'true' or 'false', by default the latter

void generateRemoteSensorEvents(thermostatId,postData='false') {
	def REMOTE_SENSOR_TYPE="ecobee3_remote_sensor"
	def REMOTE_THERMOSTAT_TYPE="thermostat"
	def REMOTE_SENSOR_OCCUPANCY='occupancy'
	def REMOTE_SENSOR_TEMPERATURE='temperature'
	def REMOTE_SENSOR_HUMIDITY='humidity'
    
	int nbTempSensorInUse=0
	int nbHumSensorInUse=0
	float value,totalTemp=0,totalHum=0, avgTemp=0, avgHum=0
	float maxTemp=0, maxHum=0
	def minTemp=null
	def minHum=null
    
	if (thermostatId) {
		if (thermostatId.contains(",")) {
        
			if (settings.trace) {
				sendEvent name: "verboseTrace", value:"generateRemoteSensorEvents>thermostatId ${thermostatId} is not valid"
				log.error "generateRemoteSensorEvents>thermostatId ${thermostatId} is not valid"
			}                
			return
		}
		getThermostatInfo(thermostatId)    
	}
	thermostatId = determine_tstat_id(thermostatId)

/* Reset all remote sensor data values */
	def remoteData = []
	def remoteTempData = ""
	def remoteHumData = ""
	def remoteOccData = ""
    
	if (data.thermostatList[0].remoteSensors) {
		for (i in 0..data.thermostatList[0].remoteSensors.size() - 1) {
			if (settings.trace) {
				log.debug "generateRemoteSensorEvents>found sensor ${data.thermostatList[0].remoteSensors[i]} at (${i})"
			}
			if ((data.thermostatList[0].remoteSensors[i]?.type != REMOTE_SENSOR_TYPE) &&
			 (data.thermostatList[0].remoteSensors[i]?.type != REMOTE_THERMOSTAT_TYPE)) {
				if (settings.trace) {
					log.debug "generateRemoteSensorEvents>found sensor type ${data.thermostatList[0].remoteSensors[i].type} at (${i}, skipping it)"
				}
 				// not a remote sensor
 				continue
			}
			if (!data.thermostatList[0].remoteSensors[i].capability) {
				if (settings.trace) {
					log.debug "generateRemoteSensorEvents>looping i=${i}, no capability values found..."
				}
				continue            
			}            
			if (postData == 'true') {
				if (settings.trace) {
					log.debug "generateRemoteSensorEvents>adding ${data.thermostatList[0].remoteSensors[i]} to remoteData"
				}
				remoteData << data.thermostatList[0].remoteSensors[i]  // to be transformed into Json later
			} 
			for (j in 0..data.thermostatList[0].remoteSensors[i].capability.size()-1) {
				if (settings.trace) {
					log.debug "generateRemoteSensorEvents>looping i=${i},found ${data.thermostatList[0].remoteSensors[i].capability[j]} at j=${j}"
				}
				if (data.thermostatList[0].remoteSensors[i].capability[j].type == REMOTE_SENSOR_TEMPERATURE) {
					// Divide the sensor temperature by 10 
					value =(data.thermostatList[0].remoteSensors[i].capability[j].value.toFloat()/10).round(1)
 					remoteTempData = remoteTempData + data.thermostatList[0].remoteSensors[i].id + "," +
						data.thermostatList[0].remoteSensors[i].name + "," +
						data.thermostatList[0].remoteSensors[i].capability[j].type + "," + value.toString() + ",,"
					totalTemp = totalTemp + value
					maxTemp = Math.max(value,maxTemp)
					minTemp = (minTemp==null)? value: Math.min(value,minTemp)
					nbTempSensorInUse++
				} else if (data.thermostatList[0].remoteSensors[i].capability[j].type == REMOTE_SENSOR_HUMIDITY) {
					remoteHumData = remoteHumData + data.thermostatList[0].remoteSensors[i].id + "," + 
						data.thermostatList[0].remoteSensors[i].name + "," +
						data.thermostatList[0].remoteSensors[i].capability[j].type + "," + data.thermostatList[0].remoteSensors[i].capability[j].value + ",,"
					value =data.thermostatList[0].remoteSensors[i].capability[j].value.toFloat()
					totalHum = totalHum + value
					maxHum = Math.max(value,maxHum)
					minHum = (minHum==null)? value: Math.min(value,minHum)
					nbHumSensorInUse++
				} else if (data.thermostatList[0].remoteSensors[i].capability[j].type == REMOTE_SENSOR_OCCUPANCY) {
					remoteOccData = remoteOccData + data.thermostatList[0].remoteSensors[i].id + "," + 
						data.thermostatList[0].remoteSensors[i].name + "," +
						data.thermostatList[0].remoteSensors[i].capability[j].type + "," + data.thermostatList[0].remoteSensors[i].capability[j].value + ",,"
				} 
				                        
			} /* end for remoteSensor Capabilites */
		} /* end for remoteSensor data */
	}                        
	if (nbTempSensorInUse >0) {
		avgTemp = (totalTemp / nbTempSensorInUse).round(1)
		if (settings.trace) {
			log.debug "generateRemoteSensorEvents>avgTemp for remote sensors= ${avgTemp},totalTemp=${totalTemp},nbTempSensors=${nbTempSensorInUse}"
		}
	}                        
	def remoteSensorEvents = [
		remoteSensorOccData: "${remoteOccData.toString()}",
		remoteSensorAvgTemp: avgTemp,
 		remoteSensorTmpData: "${remoteTempData.toString()}",
		remoteSensorMinTemp: ((minTemp!=null)?minTemp:0),
		remoteSensorMaxTemp: maxTemp
	]    
	if (nbHumSensorInUse >0) {
		avgHum = (totalHum / nbHumSensorInUse).round()
		if (settings.trace) {
			log.debug "generateRemoteSensorEvents>avgHum for remote sensors= ${avgHum},totalHum=${totalHum},nbHumSensors=${nbHumSensorInUse}"
		}
		remoteSensorEvents = remoteSensorEvents + [remoteSensorHumData: "${remoteHumData.toString()}",remoteSensorAvgHumidity: avgHum,	
			remoteSensorMinHumidity: ((minHum!=null)?minHum:0),	remoteSensorMaxHumidity: maxHum]        
	}                        
	def remoteDataJson=""
 	if (remoteData != []) {
		remoteDataJson = new groovy.json.JsonBuilder(remoteData)
		remoteSensorEvents = remoteSensorEvents + [remoteSensorData: "${remoteDataJson.toString()}"]
	}
	if (settings.trace) {
		log.debug "generateRemoteSensorEvents>remoteDataJson=${remoteDataJson}"
		log.debug "generateRemoteSensorEvents>remoteSensorEvents to be sent= ${remoteSensorEvents}"
	}
	generateEvent(remoteSensorEvents)
}
    
// thermostatId may be a list of serial# separated by ",", no spaces (ex. '123456789012,123456789013') 
//	if no thermostatId is provided, it is defaulted to the current thermostatId 
void getThermostatInfo(thermostatId=settings.thermostatId) {
	if (settings.trace) {
		log.debug "getThermostatInfo> about to call build_body_request for thermostatId = ${thermostatId}..."
	}
	def bodyReq = build_body_request('thermostatInfo',null,thermostatId,null)
	if (settings.trace) {
		log.debug "getThermostatInfo> about to call api with body = ${bodyReq} for thermostatId = ${thermostatId}..."
	}
	def statusCode=true
	int j=0    
	while ((statusCode) && (j++ <2)) { // retries once if api call fails
		api('thermostatInfo', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				data?.thermostatList = resp.data.thermostatList
				def thermostatName = data.thermostatList[0].name
				// divide the temperature by 10 before for display or calculations later.
				data.thermostatList[0].runtime.actualTemperature = data.thermostatList[0].runtime
					.actualTemperature / 10
				data.thermostatList[0].runtime.desiredCool = data.thermostatList[0].runtime
					.desiredCool / 10
				data.thermostatList[0].runtime.desiredHeat = data.thermostatList[0].runtime
					.desiredHeat / 10
				data.thermostatList[0].weather.forecasts[0].temperature = data.thermostatList[
					0].weather.forecasts[0].temperature / 10
				data.thermostatList[0].weather.forecasts[0].tempLow = data.thermostatList[
					0].weather.forecasts[0].tempLow / 10
				data.thermostatList[0].weather.forecasts[0].tempHigh = data.thermostatList[
					0].weather.forecasts[0].tempHigh / 10
				data.thermostatList[0].settings.quickSaveSetBack = data.thermostatList[0].settings
					.quickSaveSetBack / 10
				data.thermostatList[0].settings.quickSaveSetForward = data.thermostatList[
					0].settings.quickSaveSetForward / 10
				if (data.thermostatList[0].events.size() > 0) {
					for (i in 0..data.thermostatList[0].events.size() - 1) {
						if (data.thermostatList[0].events[i].running) {
							// Divide the running events' temps by 10 for display later
							data.thermostatList[0].events[i].coolHoldTemp = data.thermostatList[0].events[
								i].coolHoldTemp / 10
							data.thermostatList[0].events[i].heatHoldTemp = data.thermostatList[0].events[
								i].heatHoldTemp / 10
						}                                    
					}
				}                        
				def runtimeSettings = data.thermostatList[0].runtime
				def thermostatSettings = data.thermostatList[0].settings
				if (settings.trace) {
					sendEvent name: "verboseTrace", value:
						"getTstatInfo> thermostatId=${thermostatId},name=${thermostatName},hvacMode=${thermostatSettings.hvacMode}," +
						"fan=${runtimeSettings.desiredFanMode},fanMinOnTime=${thermostatSettings.fanMinOnTime},desiredHeat=${runtimeSettings.desiredHeat},desiredCool=${runtimeSettings.desiredCool}," +
						"current Humidity= ${runtimeSettings.actualHumidity},desiredHumidity=${runtimeSettings.desiredHumidity},humidifierMode=${thermostatSettings.humidifierMode}," +
						"desiredDehumidity= ${runtimeSettings.desiredDehumidity},dehumidifierMode=${thermostatSettings.dehumidifierMode}"
					log.debug "getTstatInfo> thermostatId=${thermostatId},name=${thermostatName},hvacMode=${thermostatSettings.hvacMode}," +
						"fan=${runtimeSettings.desiredFanMode},fanMinOnTime=${thermostatSettings.fanMinOnTime},desiredHeat=${runtimeSettings.desiredHeat},desiredCool=${runtimeSettings.desiredCool}," +
						"current Humidity=${runtimeSettings.actualHumidity} desiredHumidity = ${runtimeSettings.desiredHumidity},humidifierMode=${thermostatSettings.humidifierMode}," +
						"desiredDehumidity=${runtimeSettings.desiredDehumidity},dehumidifierMode=${thermostatSettings.dehumidifierMode}"
				}
				sendEvent name: "verboseTrace", value:
					"getTstatInfo>done for ${thermostatId}"
			} else {
				log.error 
					"getThermostatInfo> error=${statusCode.toString()},message=${message} for ${thermostatId}"
				sendEvent name: "verboseTrace", value:
					"getTstatInfo>error=${statusCode},message=${message} for ${thermostatId}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */                 
		} /* end api call */
	} /* end while */
}

// tstatType =managementSet or registered (no spaces). 
// May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
// thermostatId may be a single thermostat only
// returns true if intervalRevision, thermostatRevision or runtimeRevision has changed or false otherwise.

def getThermostatRevision(tstatType, thermostatId) {
	def runtimeRevision,intervalRevision,thermostatRevision
    
	thermostatId = determine_tstat_id(thermostatId)
	def ecobeeType = determine_ecobee_type_or_location(tstatType)
	getThermostatSummary(ecobeeType)
	for (i in 0..data.thermostatCount - 1) {
		def thermostatDetails = data.revisionList[i].split(':')
		def id = thermostatDetails[0]
		def thermostatName = thermostatDetails[1]
		def connected = thermostatDetails[2]
		thermostatRevision = thermostatDetails[3]
		runtimeRevision = thermostatDetails[5]
		intervalRevision = thermostatDetails[6]
		if ((thermostatId == id) && (connected=='true')) {
			sendEvent name: "runtimeRevision", value: runtimeRevision
			sendEvent name: "intervalRevision", value: intervalRevision
			sendEvent name: "thermostatRevision", value: thermostatRevision
			if (settings.trace) {	
				log.debug "getThermostatRevision>done for ${thermostatId},intervalRevision=$intervalRevision,runtimeRevision=$runtimeRevision,thermostatRevision=$thermostatRevision," +
					"state.intervalRevision=${state?.intervalRevision},state.runtimeRevision=${state?.runtimeRevision},state.thermostatRevision=${state?.thermostatRevision}"
			}
			if ((state?.runtimeRevision != runtimeRevision) || 
				(state?.intervalRevision != intervalRevision) ||
				(state?.thermostatRevision != thermostatRevision)) {
				state?.intervalRevision=intervalRevision
				state?.runtimeRevision=runtimeRevision
				state?.thermostatRevision=thermostatRevision
				return true
			} else {
				return false
			}            
		} else if (connected=='false') {
			if (settings.trace) {	
				log.error "getThermostatRevision>thermostatId ${id} not connected" 
			}
		}        
	}    
	return false
}


// tstatType =managementSet or registered (no spaces). 
// May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
void getThermostatSummary(tstatType) {

	def bodyReq = build_body_request('thermostatSummary',tstatType,null,null)
	if (settings.trace) {
		log.debug "getThermostatSummary> about to call api with body = ${bodyReq}"
	}
	def statusCode=true
	int j=0        
	while ((statusCode) && (j++ <2)) { // retries once if api call fails

		api('thermostatSummary', bodyReq) {resp ->
			statusCode = resp.data.status.code
			def message = resp.data.status.message
			if (!statusCode) {
				data?.revisionList = resp.data.revisionList
				data?.statusList = resp.data.statusList
				data?.thermostatCount = data.revisionList.size()
				for (i in 0..data.thermostatCount - 1) {
					def thermostatDetails = data.revisionList[i].split(':')
					def thermostatId = thermostatDetails[0]
					def thermostatName = thermostatDetails[1]
					def connected = thermostatDetails[2]
					def thermostatRevision = thermostatDetails[3]
					def alertRevision = thermostatDetails[4]
					def runtimeRevision = thermostatDetails[5]
					if (settings.trace) {
						log.debug "getThermostatSummary>thermostatId=${thermostatId},name=${thermostatName},connected =${connected}"
						sendEvent name: "verboseTrace", value:
							"getTstatSummary> found ${thermostatId},name=${thermostatName},connected=${connected}"
					}
				} /* end for */                        
				sendEvent name: "verboseTrace", value:
					"getTstatSummary>done"
			} else {
				log.error
					"getThermostatSummary> error=${statusCode.toString()},message=${message}"
				sendEvent name: "verboseTrace", value:
					"getTstatSummary> error= ${statusCode.toString()},message=${message}"
				// introduce a 1 second delay before re-attempting any other command                    
				delay(1000)                    
			} /* end if statusCode */
		}  /* end api call */              
	} /* end while */
}
// poll() or getThermostatInfo() must be called prior to calling the getModelNumber() method 
// Return thermostat's current Model Number */
def getModelNumber() {

	if (settings.trace) {
		log.debug "getModelNumber>thermostatId=${data.thermostatList[0]?.identifier}," +
        	"modelNumber=${data.thermostatList[0]?.modelNumber}"
	}
	return ((data.thermostatList[0].identifier)? data.thermostatList[0].modelNumber: "")
}

private def refresh_tokens() {
    
	def method = 
	[
		headers: [
			'Content-Type': "application/json",
			'charset': "UTF-8"
			],
		uri: "${get_URI_ROOT()}/token?" +
		"grant_type=refresh_token&" +
		"code=${data.auth.refresh_token}&" +
		"client_id=${get_appKey()}"
	]
	if (settings.trace) {
		log.debug "refresh_tokens> uri = ${method.uri}"
	}
	def successRefreshTokens = {resp ->
		if (settings.trace) {
			log.debug "refresh_tokens> response = ${resp.data}"
		}
		data.auth.access_token = resp.data.access_token
		data.auth.refresh_token = resp.data.refresh_token
		data.auth.expires_in = resp.data.expires_in
		data.auth.token_type = resp.data.token_type
		data.auth.scope = resp.data.scope
	}
	try {
		httpPostJson(method, successRefreshTokens)
	} catch (java.net.UnknownHostException e) {
		log.error "refresh_tokens> Unknown host - check the URL " + method.uri
		sendEvent name: "verboseTrace", value: "refresh_tokens> exception $e, Unknown host ${method.uri}"
		state.exceptionCount = state.exceptionCount +1     
		return false
	} catch (java.net.NoRouteToHostException e) {
		log.error "refresh_tokens> No route to host - check the URL " + method.uri
		sendEvent name: "verboseTrace", value: "refresh_tokens> exception $e, no route to host ${method.uri}"
		state.exceptionCount = state.exceptionCount +1     
		return false
	} catch (e) {
		log.debug "refresh_tokens>exception $e at " + method.uri
		sendEvent name: "verboseTrace", value:
			"refresh_tokens>exception $e at " + method.uri
		state.exceptionCount = state.exceptionCount +1     
		if (!(e.toString().contains("TimeoutException"))) {
			// introduce a 1 second delay before re-attempting any other command                    
			delay(1000)                    
		}
		return false
	}
	// determine token's expire time
	def authexptime = new Date((now() + (data.auth.expires_in * 60 * 1000))).getTime()
	data.auth.authexptime = authexptime

	// If this thermostat was created by initialSetup, go and refresh the parent and all other children
	if (data.auth.thermostatId) {		// Created by initalSetup, need to refresh Parent tokens and other children
		refreshParentTokens()
	}        
	if (settings.trace) {
		log.debug "refresh_tokens> expires in ${data.auth.expires_in} minutes"
		log.debug "refresh_tokens> data_auth.expires_in in time = ${authexptime}"
		sendEvent name: "verboseTrace", value:
			"refresh_tokens>expire in ${data.auth.expires_in} minutes"
	}
	return true
}
void refreshChildTokens(auth) {
	if (settings.trace) {
		log.debug "refreshChildTokens>begin token auth= $auth"
	}
	data.auth.access_token = auth.authToken
	data.auth.refresh_token = auth.refreshToken
	data.auth.expires_in = auth.expiresIn
	data.auth.token_type = auth.tokenType
	data.auth.scope = auth.scope
	data.auth.authexptime = auth.authexptime
	if (settings.trace) {
		log.debug "refreshChildTokens>end data.auth=$data.auth"
	}
}

private void refreshParentTokens() {
	if (settings.trace) {
		log.debug "refreshParentTokens>begin data.auth = ${data.auth}"
	}
	if (settings.trace) {
		log.debug "refreshParentTokens>about to call parent.setParentAuthTokens"
	}         
	parent.setParentAuthTokens(data.auth)
	if (settings.trace) {
		log.debug "refreshParentTokens>end"
	}         
}

private void login() {
	if (settings.trace) {
		log.debug "login> about to call setAuthTokens"
	}
	if (data?.auth?.thermostatId) {
    	// Created by initalSetup
		if (settings.trace) {
			log.debug "login> about to call refreshThisChildAuthTokens"
		}
		parent.refreshThisChildAuthTokens(this)
	} else { 
		setAuthTokens()
	}    
	if (!isLoggedIn()) {
		if (settings.trace) {
			log.error "login> no auth_token..., failed"
		}
		return
	}
}

void getEcobeePinAndAuth() {
	String SCOPE = "smartWrite,ems"
	def method = 
	[
		headers: [
			'Content-Type': "application/json",
			'charset': "UTF-8"
			],
		uri: "${get_URI_ROOT()}/authorize?" +
		"response_type=ecobeePin&" +
		"client_id=${get_appKey()}&" +
		"scope=${SCOPE}"
	]
	def successEcobeePin = {resp ->
		if (settings.trace) {
			log.debug "getEcobeePinAndAuth> response = ${resp.data}"
		}
		data?.auth = resp.data
		data.auth.code = resp.data.code
		data.auth.expires_in = resp.data.expires_in
		data.auth.interval = resp.data.interval
		data.auth.ecobeePin = resp.data.ecobeePin
		sendEvent name: "verboseTrace", value:"getEcobeePin>${data.auth.ecobeePin}"
		data.auth.access_token = null // for isLoggedIn() later
		data.thermostatCount = null // for iterate functions later
		data.groups = null // for iterateUpdateGroups later
	}
	try {
		httpGet(method, successEcobeePin)
	} catch (java.net.UnknownHostException e) {
		log.error "getEcobeePinAndAuth> Unknown host - check the URL " + method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> Unknown host " +
			method.uri
		return
	} catch (java.net.NoRouteToHostException t) {
		log.error "getEcobeePinAndAuth> No route to host - check the URL " + method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> No route to host " +
			method.uri
		return
	} catch (java.io.IOException e) {
		log.error "getEcobeePinAndAuth> Authentication error, bad URL or settings missing " +
			method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> Auth error " + method.uri
		return
	} catch (any) {
		log.error "getEcobeePinAndAuth> general error " + method.uri
		sendEvent name: "verboseTrace", value: "getEcobeePin> general error " +
			method.uri
		return
	}
	if (settings.trace) {
		log.debug "getEcobeePinAndAuth> ecobeePin= ${data.auth.ecobeePin}, authorizationCode=${data.auth.code},scope=${data.auth.scope} expires_in=${data.auth.expires_in} done"
	}
}

void setAuthTokens() {
	def method = 
	[
		headers: [
			'X-nl-protocol-version': 1,
			'Content-Type': "application/json",
			'charset': "UTF-8"
			],
		uri: "${get_URI_ROOT()}/token?" +
		"grant_type=ecobeePin&" +
		"code=${data.auth.code}&" +
		"client_id=${get_appKey()}"
	]
	if (data?.auth?.access_token == null) {
		def successTokens = {resp ->
			data.auth.access_token = resp.data.access_token
			data.auth.refresh_token = resp.data.refresh_token
			data.auth.expires_in = resp.data.expires_in
			data.auth.token_type = resp.data.token_type
			data.auth.scope = resp.data.scope
			if (settings.trace) {
				log.debug "setAuthTokens> accessToken= ${data.auth.access_token}, refreshToken=${data.auth.refresh_token}," +
					"tokenType=${data.auth.token_type},scope=${data.auth.scope}"
			}
		}
		try {
			log.debug "setAuthTokens> about to call httpPost with code= ${data.auth.code}"
			httpPostJson(method, successTokens)

		} catch (java.net.UnknownHostException e) {
			log.error "setAuthTokens> Unknown host - check the URL " + method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens> Unknown host " +
				method.uri
			return
		} catch (java.net.NoRouteToHostException t) {
			log.error "setAuthTokens> No route to host - check the URL " + method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens> No route to host" +
				method.uri
			return
		} catch (java.io.IOException e) {
			log.error "setAuthTokens> Auth error, ecobee site cannot be reached " +
				method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens> Auth error " +
				method.uri
			return
		} catch (any) {
			log.error "setAuthTokens> general error " + method.uri
			sendEvent name: "verboseTrace", value: "setAuthTokens>general error " +
				method.uri
			return
		}
		// determine token's expire time
		def authexptime = new Date((now() + (data.auth.expires_in * 60 * 1000))).getTime()
		data.auth.authexptime = authexptime
		if (settings.trace) {
			log.debug "setAuthTokens> expires in ${data.auth.expires_in} minutes"
			log.debug "setAuthTokens> data_auth.expires_in in time = ${authexptime}"
			sendEvent name: "verboseTrace", value:
				"setAuthTokens>expire in ${data.auth.expires_in} minutes"
		}
	}
}
private def isLoggedIn() {
	if (data.auth == null) {
		if (settings.trace) {
			log.debug "isLoggedIn> no data auth"
		}
		return false
	} else {
		if (data.auth.access_token == null) {
			if (settings.trace) {
				log.debug "isLoggedIn> no access token"
				return false
			}
		}
	}
	return true
}
private def isTokenExpired() {
	def buffer_time_expiration=5   // set a 5 min. buffer time before token expiration to avoid auth_err 
	def time_check_for_exp = now() + (buffer_time_expiration * 60 * 1000);
	if (settings.trace) {
		log.debug "isTokenExpired> check expires_in: ${data.auth.authexptime} > time check for exp: ${time_check_for_exp}"
	}
	if (data.auth.authexptime > time_check_for_exp) {
		if (settings.trace) {
			log.debug "isTokenExpired> not expired"
		}
		return false
	}
	if (settings.trace) {
		log.debug "isTokenExpired> expired"
	}
	return true
}


// Determine ecobee type from tstatType or settings
// tstatType =managementSet or registered (no spaces). 
//	May also be set to a specific locationSet (ex./Toronto/Campus/BuildingA)
private def determine_ecobee_type_or_location(tstatType) {
	def ecobeeType
	def modelNumber = getModelNumber()
    
	if (settings.trace) {
		log.debug "determine_ecobee_type>begin ecobeeType = ${tstatType}, modelNumber=${modelNumber}"
	}
	if ((tstatType != null) && (tstatType != "")) {
		ecobeeType = tstatType.trim()
	} else if ((settings?.ecobeeType != null) && (settings?.ecobeeType != "")) {
		ecobeeType = settings.ecobeeType.trim()
		
	} else if (modelNumber.toUpperCase().contains("EMS")) {
    
		ecobeeType = 'managementSet'
		settings.ecobeeType='managementSet'
    
	} else {
		// by default, the ecobee type is 'registered'
		ecobeeType = 'registered'
		settings.ecobeeType='registered'
	}
	if (settings.trace) {
		log.debug "determine_ecobee_type>end ecobeeType = ${ecobeeType}"
	}
	return ecobeeType
}

// Determine id from settings or initalSetup
def determine_tstat_id(tstat_id) {
	def tstatId
    
	if ((tstat_id != null) && (tstat_id != "")) {
		tstatId = tstat_id.trim()
	} else if ((settings.thermostatId != null) && (settings.thermostatId  != "")) {
		tstatId = settings.thermostatId.trim()
		if (settings.trace) {
			log.debug "determine_tstat_id> tstatId = ${settings.thermostatId}"
		}
	} else if ((settings.thermostatId == null) || (settings.thermostatId  == "")) {
		settings.appKey = data.auth.appKey
		settings.thermostatId = data.auth.thermostatId
		tstatId=data.auth.thermostatId
		if (settings.trace) {
			log.debug "determine_tstat_id> tstatId from data= ${data.auth.thermostatId}"
		}
	}
	return tstatId
}

// Get the appKey for authentication
private def get_appKey() {
	return settings.appKey
}    

// Called by My ecobee Init for initial creation of a child Device
void initialSetup(device_client_id, auth_data, device_tstat_id) {
/*
	settings.trace='true'
*/	
	if (settings.trace) {
		log.debug "initialSetup>begin"
		log.debug "initialSetup> device_tstat_Id = ${device_tstat_id}"
		log.debug "initialSetup> device_client_id = ${device_client_id}"
	}	
	settings.appKey= device_client_id
	settings.thermostatId = device_tstat_id

	data?.auth = settings    
	data.auth.access_token = auth_data.authToken
	data.auth.refresh_token = auth_data.refreshToken
	data.auth.expires_in = auth_data.expiresIn
	data.auth.token_type = auth_data.tokenType
	data.auth.authexptime= auth_data?.authexptime
	if (settings.trace) {
		log.debug "initialSetup> settings = $settings"
		log.debug "initialSetup> data_auth = $data.auth"
		log.debug "initialSetup>end"
	}
	getThermostatInfo(thermostatId)
	def ecobeeType=determine_ecobee_type_or_location("")
	data.auth.ecobeeType = ecobeeType
	state?.exceptionCount=0    
	state?.lastPollTimestamp = now()
}

def toQueryString(Map m) {
	return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}

private def cToF(temp) {
	return (temp * 1.8 + 32)
}
private def fToC(temp) {
	return (temp - 32) / 1.8
}
private def milesToKm(distance) {
	return (distance * 1.609344) 
}
private def get_URI_ROOT() {
	return "https://api.ecobee.com"
}
// Maximum tstat batch size (25 thermostats max may be processed in batch)
private def get_MAX_TSTAT_BATCH() {
	return 25
}
