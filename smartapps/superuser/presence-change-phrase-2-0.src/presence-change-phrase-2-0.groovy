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
    name: "Presence change phrase 2.0",
    namespace: "",
    author: "James Clark",
    description: "If you have multiple presence devices per person and if at least one of the devices per person is person or not person. This app will change the phrase.",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "selectPhrases")
}

def selectPhrases() {
	dynamicPage(name: "selectPhrases", title: "Configure", uninstall: true,install:true) {		
		section("Select Person...") {
			input "person1", "capability.presenceSensor", title: "Preson sensers person 1?", multiple: true, required: true
    		input "person2", "capability.presenceSensor", title: "Preson sensers person 2?", multiple: true, required: true
        }
		def phrases = location.helloHome?.getPhrases()*.label
		if (phrases) {
       		phrases.sort()
       		section("What phrases...") {
				log.trace phrases
				input "phrase1", "enum", title: "Present phrase?", options: phrases, required: true
				input "phrase2", "enum", title: "Not Present phrase?", options: phrases, required: true
				}
		}
        section("Phrase does not change in this mode") {
        	input "Mode", "mode", title: "Mode?"
    	}
    	section("Delay time to change phrase...") {
    		input "threshold1", "number", title: "Minutes", required: true
        }
	}
}   

def installed() {
    subscribe(person1, "presence", presence)
    subscribe(person2, "presence", presence)
	state.present = true
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
	if (location.mode != Mode) {
    	if ((evt.value == "present") && !state.present) {
    		log.trace "Present."
        	state.present = true
        	location.helloHome.execute(settings.phrase1)
        	log.debug "Phrase:${settings.phrase1}"
        	unschedule(notpresent)        
    	}
        else if (everyoneIsAway1() && everyoneIsAway2() && state.present) {
            log.trace "Not present."
            state.present = false
            def threshold1offdelay = threshold1.toInteger() * 60
            log.debug "runIn($threshold1offdelay)"
            runIn(threshold1offdelay, notpresent)
        }
	}
}

def notpresent(evt) {
	location.helloHome.execute(settings.phrase2)
    log.debug "Phrase:${settings.phrase2}"
}