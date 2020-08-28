/**
 *  Close garage door if everyone has left and there is no motion
 *
 *  Copyright 2015 James Clark
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
    name: "Presence change modes",
    namespace: "",
    author: "James Clark",
    description: "If you have multiple presence devices per person and if at least one of the devices per person is person or not presence. This app will change the mode if the selected mode is not set.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Select Person Sensors...") {
        input "person1", "capability.presenceSensor", title: "Preson sensers person 1?", multiple: true, required: true
        input "person2", "capability.presenceSensor", title: "Preson sensers person 2?", multiple: true, required: true
    }
    section("Select Modes...") {
        input "PresentMode", "mode", title: "Mode when Home?", required: true
        input "NotPresentMode", "mode", title: "Mode when Away?", required: true
        input "VisitorMode", "mode", title: "Disable Does not Change Away Mode?", required: true
        input "EntertainMode", "mode", title: "Entertain Does not Change Arrived Mode?", required: true
    }
}   

def installed() {
    subscribe(person1, "presence", presence)
    subscribe(person2, "presence", presence)
}

def updated() {
    unsubscribe()
	installed()
}

private everyoneIsAway1() {
    def result = false
    for (person in person1) {
        if (person.currentPresence == "not present") {
            result = true
            break
        }
    }
    log.debug "everyoneIsAway1: $result"
    return result
}

private everyoneIsAway2() {
    def result = false
    for (person in person2) {
        if (person.currentPresence == "not present") {
            result = true
            break
        }
    }
    log.debug "everyoneIsAway2: $result"
    return result
}

def presence(evt) {
    	if (evt.value == "present"){
			log.trace "Present"
			if (location.mode != PresentMode && location.mode == VisitorMode) {
				log.trace "Changing Mode from Visitor to Arrived"
				setLocationMode(PresentMode)
			}
			else if (location.mode != PresentMode && location.mode != EntertainMode) {
				log.trace "Present."
                setLocationMode(PresentMode)
			}
    	}
        else if (everyoneIsAway1() && everyoneIsAway2() && location.mode != NotPresentMode && location.mode != VisitorMode) {
            setLocationMode(NotPresentMode)
        }
}