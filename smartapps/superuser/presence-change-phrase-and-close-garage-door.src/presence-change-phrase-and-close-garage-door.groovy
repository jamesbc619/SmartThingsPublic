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
    name: "Presence change phrase and close garage door",
    namespace: "",
    author: "James Clark",
    description: "If you have multiple presence devices per person and if at least one of the devices per person has left the house and one device has returned. This app will change the phrase when leaving and returning home. It will also close the garage doors if there is no motion.",
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
		section("Close if large garage door is open...") {
			input "car1", "capability.presenceSensor", title: "Presence senser car?", required: true
            input "contact1", "capability.contactSensor", title: "Which door sensor?", required: true
			input "thedoor1", "capability.switch", title: "Which door switch?", required: true
		}
		section("Close if small garage door is open...") {
			input "car2", "capability.presenceSensor", title: "Presence senser car?", required: true
            input "contact2", "capability.contactSensor", title: "Which door sensor?", required: true
			input "thedoor2", "capability.switch", title: "Which door switch?", required: true
		}
		section("Select motion senser...") {
			input "motion1", "capability.motionSensor", title: "Motion sensor?", required: true
		}
    	section("Delay time to change phrase and close garage door after everyone has left...") {
    		input "threshold1", "number", title: "Minutes", required: true
        }
    	section("Delay time to close garage door if car is not present...") {
    		input "threshold2", "number", title: "Minutes", required: true
  		}
    	section("Delay time before door closes after arriving when no montion...") {
    		input "arrivingnumber", "number", title: "Minutes", required: true
  		}
	}
}   

def installed() {
    subscribe(person1, "presence", presence)
    subscribe(person2, "presence", presence)
    subscribe(car1, "presence", carpresence1)
    subscribe(car2, "presence", carpresence2)
    subscribe(motion1, "motion", motionHandler1)
    subscribe(contact1, "contact", contactHandler)
	subscribe(contact2, "contact", contactHandler)
	state.present = true
    state.motiontimer = "inactive"
	state.arrivedclosing1 = false
    state.arrivedclosing2 = false
}

def updated() {
    unsubscribe()
    subscribe(person1, "presence", presence)
    subscribe(person2, "presence", presence)
    subscribe(car1, "presence", carpresence1)
    subscribe(car2, "presence", carpresence2)
    subscribe(motion1, "motion", motionHandler1)
    subscribe(contact1, "contact", contactHandler)
	subscribe(contact2, "contact", contactHandler)
    state.present = true
    state.motiontimer = "inactive"
	state.arrivedclosing1 = false
    state.arrivedclosing2 = false
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

def motionHandler1(evt) {
    if (evt.value == "active") {
    	log.trace "Motion active"
        state.motiontimer = "active"
        unschedule(motionHandlerinactive1)
        unschedule(TurnOnSwitchCarArrivedBoth)
        unschedule(TurnOnSwitchCarArrived1)
        unschedule(TurnOnSwitchCarArrived2)
    }
    else {
    	log.trace "Motion timer wait inactive"
		runIn(5, motionHandlerinactive1)
        if ((state.arrivedclosing1).and(state.arrivedclosing2)) {
            log.trace "Both Cars arrived waiting to close large garage door ariving."
    		def offDelay = arrivingnumber.toInteger() * 60
    		log.debug "runIn($offDelay)"
        	unschedule(TurnOnSwitchCarDeparted1)
        	runIn(offDelay, TurnOnSwitchBothCarsArrived)
        }    
        else if (state.arrivedclosing1) {
            log.trace "Car 1 arrived waiting to close large garage door ariving."
    		def offDelay = arrivingnumber.toInteger() * 60
    		log.debug "runIn($offDelay)"
        	unschedule(TurnOnSwitchCarDeparted1)
        	runIn(offDelay, TurnOnSwitchCarArrived1)
        }
        else if (state.arrivedclosing2) {
            log.trace "Car 2 arrived waiting to close small garage door ariving."
    		def offDelay = arrivingnumber.toInteger() * 60
    		log.debug "runIn($offDelay)"
        	unschedule(TurnOnSwitchDepartedCar2)
        	runIn(offDelay, TurnOnSwitchCarArrived2)
        }
    }
}

def contactHandler(evt){
	log.debug "evt.name: $evt.value"
    if (evt.value == "closed"){
    	if ((state.arrivedclosing1).and(state.arrivedclosing2)){
			state.arrivedclosing1 = false
        	state.arrivedclosing2 = false
			unschedule(TurnOnSwitchCarArrived1)
            unschedule(TurnOnSwitchCarArrived2)
        	unschedule(TurnOnSwitchBothCarsArrived)
			log.trace "Cancel close large and small door arrived because door closed."
		}
    	else if (state.arrivedclosing1){
			state.arrivedclosing1 = false
			unschedule(TurnOnSwitchCarArrived1)
			log.trace "Cancel close small door arrived because door 1 closed."
		}
		else if (state.arrivedclosing2){
			state.arrivedclosing2 = false
			unschedule(TurnOnSwitchCarArrived2)
			log.trace "Cancel close small door arrived because door 2 closed."
		}
	}
}

def motionHandlerinactive1(evt) {
	state.motiontimer = "inactive"
	log.trace "Motion timer done inactive"
}

def presence(evt) {
	if ((!everyoneIsAway1() || !everyoneIsAway2()) && !state.present) {
    	log.trace "Present."
        state.present = true
        location.helloHome.execute(settings.phrase1)
        log.debug "Phrase:${settings.phrase2}"
        unschedule(notpresent)
        unschedule(TurnOnSwitchPerson1)
        unschedule(TurnOnSwitchPerson2)
        
    }
    else if (everyoneIsAway1() && everyoneIsAway2() && state.present) {
		log.trace "Not present."
        state.present = false
		def offDelay = threshold1.toInteger() * 60
		log.debug "runIn($offDelay)"
		runIn(offDelay, notpresent)
    }
}

def notpresent(evt) {
	location.helloHome.execute(settings.phrase2)
    log.debug "Phrase:${settings.phrase2}"
    if ((contact1.currentContact == "open").and(car1.currentPresence == "present")) {
    	log.debug "Everyone left and car 1 is present closing large garage door"
        TurnOnSwitchPerson1()
    }
    if ((contact2.currentContact == "open").and(car2.currentPresence == "present")) {
    	log.debug "Everyone left and car 2 is present closing small garage door"
        TurnOnSwitchPerson2()
    }
    if ((contact1.currentContact == "open").and(car1.currentPresence == "not present")) {
		log.debug "Everyone left and car 1 is not present closing large garage door"
        def offDelay = threshold2.toInteger() * 60
		log.debug "runIn($offDelay)"
        runIn(offDelay, TurnOnSwitchPerson1)
	}
	if ((contact2.currentContact == "open").and(car2.currentPresence == "not present")) {
		log.debug "Everyone left and car 2 is not present closing small garage door"
        def offDelay = threshold2.toInteger() * 60
		log.debug "runIn($offDelay)"
        runIn(offDelay, TurnOnSwitchPerson2)
	}
}


def carpresence1(evt) {
    log.debug "evt.name: $evt.value"
   	if ((car1.currentPresence == "not present").and(contact1.currentContact == "open").and(state.present)) {
		log.trace "Car 1 not present closing garage door."
        unschedule(TurnOnSwitchCarArrived1)
        TurnOnSwitchCarDeparted1()
	}
    else if (car1.currentPresence == "present") {
		state.arrivedclosing1 = true
    }
}

def carpresence2(evt) {
    log.debug "evt.name: $evt.value"
   	if ((car2.currentPresence == "not present").and(contact2.currentContact == "open").and(state.present)) {
		log.trace "Car 2 no present closing garage door."
        unschedule(TurnOnSwitchCarArrived2)
        TurnOnSwitchCarDeparted2()
	}
    else if (car2.currentPresence == "present") {
    	state.arrivedclosing2 = true
    }
}

def TurnOnSwitchCarDeparted1(evt) {
	if ((state.motiontimer == "inactive").and(contact1.currentContact == "open")) {
    	log.trace "Closing large garage door Departed."
		thedoor1.on()
        pause(2000)
		thedoor1.off()
	}
}

def TurnOnSwitchCarDeparted2(evt) {
	if ((state.motiontimer == "inactive").and(contact2.currentContact == "open")) {
    	log.trace "Closing small garage door Departed."
		thedoor2.on()
        pause(2000)
		thedoor2.off()
	}
}

def TurnOnSwitchBothCarsArrived(evt) {
	state.arrivedclosing1 = false
    state.arrivedclosing2 = false
    if ((contact1.currentContact == "open").and(contact2.currentContact == "open")) {
    	log.trace "Closing large and small garage door both cars arrived."
		thedoor1.on()
        pause(2000)
		thedoor1.off()
        thedoor2.on()
        pause(2000)
		thedoor2.off()
	}
}

def TurnOnSwitchCarArrived1(evt) {
	state.arrivedclosing1 = false
    if (contact1.currentContact == "open") {
    	log.trace "Closing large garage door Arrived."
		thedoor1.on()
        pause(2000)
		thedoor1.off()
	}
}

def TurnOnSwitchCarArrived2(evt) {
	state.arrivedclosing2 = false
    if (contact2.currentContact == "open") {
    	log.trace "Closing small garage door Arrived."
		thedoor2.on()
        pause(2000)
		thedoor2.off()
	}
}

def TurnOnSwitchPerson1(evt) {
	if ((state.motiontimer == "inactive").and(contact1.currentContact == "open")) {
    	log.trace "Closing large garage door Person."
		thedoor1.on()
        pause(2000)
		thedoor1.off()
	}
}
def TurnOnSwitchPerson2(evt) {
	if ((state.motiontimer == "inactive").and(contact2.currentContact == "open")) {
    	log.trace "Closing small garage door Person."
		thedoor2.on()
        pause(2000)
		thedoor2.off()
	}
}