/**
 *  Arme cameras when dooors are open and mode is set
 *
 *  Copyright 2020 James Clark
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Arme cameras when door is closed and mode is set",
    namespace: "jamesbc619",
    author: "James Clark",
    description: "This app armed the cameras if a door is closed and mode is set.",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When any of these are open...") {
		input "contact1", "capability.contactSensor", title: "Doors?", multiple: true, required: true
	}
	section("Disarmed cameras switch...") {
		input "switch1", "capability.switch", title: "Switch?", required: true
	}
    section("When mode is set to...") {
        input "mode1", "mode", title: "Mode?", required: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    initialize()
}

def initialize() {
    subscribe(contact1, "contact", contactHandler)
    subscribe(location, "mode", modeHandler)
}

def contactHandler(evt) {
	log.trace "contactHandler: $evt ${evt.value}"
    
    def openSensor = contact1.find{it.currentValue("contact") == "open"}
    if (location.mode == mode1) {
        log.debug "Mode is set to ${mode1}, enable app"
        if (openSensor != null) {
            log.debug "At least one sensor (${openSensor.label}) is open, switch off"
            switch1.off()
        } else {
            log.debug "No sensors are open, switch on"
            switch1.on()
        }
	}
    else {
    	log.debug "Mode is not set to ${mode1}, disable app"
    }
}

def modeHandler(evt) {
    log.debug "modeHandler: $evt ${evt.value}"
    if (location.mode == mode1) {
        if(openSensor != null) {
            log.debug "mode changed to ${evt.value}, at least one sensor (${openSensor.label}) is open, switch off"
            switch1.off()
        }
        else {
            log.debug "mode changed to ${evt.value}, no sensors are open, switch on"
            switch1.on()
        }
    }
}