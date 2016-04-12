/**
 *  Close my Garage N min after open
 *  Author: Ronald Gouldner
 */
definition(
    name: "Close my Garage N min after open",
    namespace: "gouldner",
    author: "Ronald Gouldner",
    description: "Close your Garage Door N Minutes after it is Opened.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
    section("When the Garage Door opens...") {
	input "door1", "capability.doorControl", title: "Door?"
    }
    //section("Text me at...") {
    //	input "phone1", "phone", title: "Phone number?"
    //}
    section("Close Door after...") {
        input "closeDoorAfter", "number", title: "Minutes?"
    }
}

def installed()
{
	subscribe(door1, "door.open", doorOpenHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contact1, "door.open", doorOpenHandler)
}

def doorOpenHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.debug "${door1.label ?: door1.name} was opened, Closing after $closeDoorAfter min"
	//sendSms(phone1, "Your ${contact1.label ?: contact1.name} was opened")
    def fiveMinuteDelay = 60 * closeDoorAfter
	runIn(fiveMinuteDelay, closeDoor)
}

def closeDoor() {
    log.debug "Closing Door"
    door1.close()
}