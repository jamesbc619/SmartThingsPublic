/**
 *  Close garage door
 *
 *  Copyright 2016 James Clark
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
    name: "Close garage door with mode",
    namespace: "",
    author: "James Clark",
    description: "Close garage door with mode change",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Garage Door 1...") {
		input "thedoor1", "capability.switch", title: "Which door switch?", required: true
    }
	section("When mode changes close garage door...") {
        input "modes", "mode", title: "Mode?"
    }
    section("Select motion senser...") {
		input "motion", "capability.motionSensor", title: "Motion sensor?", required: true
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(motion, "motion", motionhandler)
    subscribe(location, "mode", modechangehandler)
    state.motiontimer = "inactive"
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	installed()
}

def modechangehandler(evt) {
    log.debug "mode changed to ${evt.value}"
    if (evt.value == modes)  {
    	if (state.motiontimer == "inactive") {
        	log.trace "Mode change to ${evt.value} closing door 1."
            thedoor1.on()
		}                     
    }
}


def motionhandler(evt) {
    log.debug "motionhandler: $evt.value"
    if (evt.value == "active") {
    	log.trace "Motion active"
        state.motiontimer = "active"
        unschedule(motionHandlerinactive)
    }
    else {
    	log.trace "Motion timer wait inactive"
		runIn(5, motionHandlerinactive)
    }
}

def motionHandlerinactive() {
	state.motiontimer = "inactive"
	log.trace "Motion timer done inactive"
}