	if ((state?.programHoldSet == 'Away') && (verify_presence_based_on_motion_in_rooms())) {
		if ((currentSetClimate.toUpperCase() == 'AWAY') && (currentProgName.toUpperCase()!='AWAY')) {       
			log.trace("check_if_hold_justified>it's not been quiet since ${state.programSetTimestamp},resume program...")
			thermostat.resumeProgram("")
			send("ecobeeSetZoneWithSchedule>resumed current program, motion detected")
			reset_state_program_values()
		}                
 		else {	/* Climate was changed since the last climate set, just reset state program values */
			reset_state_program_values()
		}
	} else if (state?.programHoldSet == 'Away') {
		if (currentProgName.toUpperCase() == 'AWAY') {
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>hold no longer needed, program already in Away mode")
			}
			thermostat.resumeProgram("")
			reset_state_program_values()
                
		} else {
			log.trace("check_if_hold_justified>quiet since ${state.programSetTimestamp}, current program= ${currentProgName},'Away' hold justified")
			send("ecobeeSetZoneWithSchedule>quiet since ${state.programSetTimestamp}, current program= ${currentProgName}, 'Away' hold justified")
		}    
	}
	if ((state?.programHoldSet == 'Home') && (!verify_presence_based_on_motion_in_rooms())) {
		if ((currentSetClimate.toUpperCase() == 'AWAY') && (currentProgName.toUpperCase()!='HOME')) {       
			log.trace("check_if_hold_justified>it's been quiet since ${state.programSetTimestamp},resume program...")
			thermostat.resumeProgram("")
			send("ecobeeSetZoneWithSchedule>resumed program, no motion detected")
			reset_state_program_values()
		}                	
		else {	/* Climate was changed since the last climate set, just reset state program values */
			reset_state_program_values()
		}
	} else if (state?.programHoldSet == 'Home')  { 
		if (currentProgName.toUpperCase() == 'HOME') {
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>hold no longer needed, program already in Home mode")
			}
			reset_state_program_values()
			thermostat.resumeProgram("")
		} else {
			log.trace("check_if_hold_justified>not quiet since ${state.programSetTimestamp}, current program= ${currentProgName}, 'Home' hold justified")
			if (detailedNotif == 'true') {
				send("ecobeeSetZoneWithSchedule>not quiet since ${state.programSetTimestamp}, current program= ${currentProgName}, 'Home' hold justified")
			}
		}
	}   
