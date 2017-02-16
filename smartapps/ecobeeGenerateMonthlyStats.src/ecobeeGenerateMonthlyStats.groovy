private send(msg, askAlexa=false) {
	def message = "${get_APP_NAME()}>${msg}"
	def sendNotification= (sendPushMessage==null) ?: "No"
	if (sendNotification != "No") {
		
		if (askAlexa) {
			sendLocationEvent(name: "AskAlexaMsgQueue", value: "${get_APP_NAME()}", isStateChange: true, descriptionText: msg)        
		} else {        
			sendPush(message)
		}            
	}
	
	if (phone) {
		log.debug("sending text message")
		sendSms(phone, message)
	}
    
	log.debug msg
    
}
