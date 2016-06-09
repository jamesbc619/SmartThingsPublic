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
    name: "Close garage door with no presence or mode",
    namespace: "",
    author: "James Clark",
    description: "Close garage door is phrase ",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Garage Door 1...") {
		input "car1", "capability.presenceSensor", title: "Presence senser car?", required: true
		input "contact1", "capability.contactSensor", title: "Which door sensor?", required: true
		input "thedoor1", "capability.switch", title: "Which door switch?", required: true
    }
	section("Garage door 2...") {
		input "car2", "capability.presenceSensor", title: "Presence senser car?", required: true
        input "contact2", "capability.contactSensor", title: "Which door sensor?", required: true
		input "thedoor2", "capability.switch", title: "Which door switch?", required: true
    }
	section("When mode changes close garage door...") {
        input "modes", "mode", title: "Mode?"
    }
    section("Select motion senser...") {
		input "motion", "capability.motionSensor", title: "Motion sensor?", required: true
    }
    section("Delay time before door closes after arriving when no montion...") {
    	input "arrivingnumber", "number", title: "Minutes? (ex:10)", required: true
  	}
    section("Delay time before canceling close door open after arriving...") {
    	input "endarrivingnumber", "number", title: "Minutes? (ex:15)", required: true
  	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    subscribe(car1, "presence", carpresence1)
    subscribe(car2, "presence", carpresence2)
    subscribe(contact1, "contact", contacthandler1)
	subscribe(contact2, "contact", contacthandler2)
	subscribe(motion, "motion", motionhandler)
    subscribe(location, "mode", modechangehandler)
    state.motiontimer = "inactive"
	state.arrivedclosing1 = false
    state.arrivedclosing2 = false
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	installed()
}

def modechangehandler(evt) {
    log.debug "mode changed to ${evt.value}"
    if (location.mode == modes) {
    	if ((contact1.currentContact == "open") && !state.arrivedclosing1) {
        	log.trace "Mode change to ${evt.value} closing door 1."
            turnonswitchcar1()
		}            
    	if ((contact2.currentContact == "open") && !state.arrivedclosing2) {
        	log.trace "Mode change to ${evt.value} closing door 2."
            turnonswitchcar2()
		}            
    }
}

def carpresence1(evt) {
    log.debug "carpresence1: $evt.value"            
    if (evt.value == "present") {
		state.arrivedclosing1 = true
       log.trace "Car 1 arrived waiting to end close door 1."
    	def endarrivingnumberdelay = endarrivingnumber.toInteger() * 60
    	log.debug "runIn($endarrivingnumberdelay)"
        runIn(endarrivingnumberdelay, endarrivingcar1)
    }
   	else {
    	state.arrivedclosing1 = false
		log.trace "Car 1 not present closing garage door."
       	turnonswitchcar1()
	}
}

def endarrivingcar1() {
	log.trace "Car 1 arrived done waiting ended close door 1."
    state.arrivedclosing1 = false
}

def carpresence2(evt) {
    log.debug "carpresence1: $evt.value"          
    if (evt.value == "present")  {
    	state.arrivedclosing2 = true
        log.trace "Car 2 arrived waiting to end close door 2."
    	def endarrivingnumberdelay = endarrivingnumber.toInteger() * 60
    	log.debug "runIn($endarrivingnumberdelay)"
        runIn(endarrivingnumberdelay, endarrivingcar2)
    }
   	else {
    	state.arrivedclosing2 = false
		log.trace "Car 2 no present closing garage door 2."
	}  
}

def endarrivingcar2() {
	log.trace "Car 2 arrived done waiting ended close door 2."
    state.arrivedclosing2 = false
}

def contacthandler1(evt){
	log.debug "contacthandler1: $evt.value"
    if ((evt.value == "closed") && (state.arrivedclosing1)) {
		state.arrivedclosing1 = false
		unschedule(turnonswitchcar1)
		log.trace "Cancel close door 1 arrived because door 1 closed."
	}
}

def contacthandler2(evt){
	log.debug "contacthandler2: $evt.value"
    if ((evt.value == "closed") && (state.arrivedclosing2)) {
		state.arrivedclosing2 = false
		unschedule(turnonswitchcar2)
		log.trace "Cancel close 2 door arrived because door 2 closed."
	}
}

def motionhandler(evt) {
    log.debug "motionhandler: $evt.value"
    if (evt.value == "active") {
    	log.trace "Motion active"
        state.motiontimer = "active"
        unschedule(motionHandlerinactive)
        unschedule(turnonswitchcar1)
        unschedule(turnonswitchcar2)
    }
    else {
    	log.trace "Motion timer wait inactive"
		runIn(5, motionHandlerinactive)
        if (state.arrivedclosing1) {
            log.trace "Car 1 arrived waiting to close door 1."
    		def arrivingnumberdelay = arrivingnumber.toInteger() * 60
    		log.debug "runIn($arrivingnumberdelay)"
        	runIn(arrivingnumberdelay, turnonswitchcar1)
        }
        if (state.arrivedclosing2) {
            log.trace "Car 2 arrived waiting to close door 2."
    		def arrivingnumberdelay = arrivingnumber.toInteger() * 60
    		log.debug "runIn($arrivingnumberdelay)"
        	runIn(arrivingnumberdelay, turnonswitchcar2)
        }
    }
}

def motionHandlerinactive() {
	state.motiontimer = "inactive"
	log.trace "Motion timer done inactive"
}

def turnonswitchcar1() {
    if ((contact1.currentContact == "open") && (state.motiontimer == "inactive")) {
    	log.trace "Closing door 1."
		state.arrivedclosing1 = false
        thedoor1.on()
	}
}

def turnonswitchcar2() {
    if ((contact2.currentContact == "open") && (state.motiontimer == "inactive")) {
    	log.trace "Closing door 2."
		state.arrivedclosing2 = false
        thedoor2.on()
	}
}