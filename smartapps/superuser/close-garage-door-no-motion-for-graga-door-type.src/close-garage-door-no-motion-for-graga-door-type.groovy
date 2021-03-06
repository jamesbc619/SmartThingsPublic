/**
 *  My First SmartApp
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
    name: "Close garage door no motion for graga door type",
    namespace: "",
    author: "James Clark",
    description: "Close if garage door is open with no montion for # minutes",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("When motion stops..."){
		input "motion1", "capability.motionSensor", title: "Which motion sensor?"
	}
    section("Close if large garage door is open...") {
		input "door1", "capability.doorControl", title: "Which Sensor?"
		input "thedoor1", "capability.doorControl", title: "Which Door?"
	}
    section("Close if small garage door is open...") {
		input "contact2", "capability.contactSensor", title: "Which Sensor?"
		input "thedoor2", "capability.doorControl", title: "Which Door?"
	}
    section("Timeout Delay Before Garage Door Close") {
        input "threshold", "number", title: "Minutes? (ex:30)"
    }
    section("Delay and number of times to try and close garage...") {
        input "numberoftimes", "number", title: "Number of times? (ex:3)", required: true
        input "threshold2", "number", title: "Minutes per time? (ex:5)", required: true
    }
}

def installed()
{
    subscribe(motion1, "motion", NoMotioneventHandler)
    subscribe(contact1, "contact", ContactHandler1)
    subscribe(contact2, "contact", ContactHandler2)
    state.counter1 = 1
    state.counter2 = 1
}

def updated()
{
	unsubscribe()
    subscribe(motion1, "motion", NoMotioneventHandler)
    subscribe(contact1, "contact", ContactHandler1)
    subscribe(contact2, "contact", ContactHandler2)
    state.counter1 = 1
    state.counter2 = 1
}

def NoMotioneventHandler(evt) {
	log.debug "$evt.value: $evt, $settings"
	log.trace "No motion detected"
        if (evt.value == "active") {
        	log.debug "Cancelling previous close door task..."
       		unschedule(TurnOnSwitch1)
            unschedule(TurnOnSwitch2)
    	}
        if ((motion1.currentMotion == "inactive").and(contact1.currentContact == "open")) {
        	log.trace "Waiting to close garage door 1 with montion."
    		def offDelay = threshold.toInteger() * 60
        	log.debug "runIn($offDelay)"
        	runIn(offDelay, TurnOnSwitch1)
		}
        if ((motion1.currentMotion == "inactive").and(contact2.currentContact == "open")) {
        	log.trace "Waiting to close garage door 2 with montion."
    		def offDelay = threshold.toInteger() * 60
        	log.debug "runIn($offDelay)"
        	runIn(offDelay, TurnOnSwitch2)
		}
}

def ContactHandler1(evt) {
	log.debug "$evt.value: $evt, $settings"
    if (contact1.currentContact == "closed") {
    	log.trace "Garge Door 1 Contact Was Closed"
    	unschedule(TurnOnSwitch1)
    }
    if (contact1.currentContact == "open") {
    	log.trace "Garge Door 1 Contact Was Open"
        log.trace "Waiting to close garage door 1 with contact."
    	def offDelay = threshold.toInteger() * 60
        log.debug "runIn($offDelay)"
        runIn(offDelay, TurnOnSwitch1)
	}
}

def ContactHandler2(evt) {
	log.debug "$evt.value: $evt, $settings"
    if (contact2.currentContact == "closed") {
    	log.trace "Garge Door 2 Contact Was Closed"
    	unschedule(TurnOnSwitch2)
    }
    if (contact2.currentContact == "open") {
    	log.trace "Garge Door 2 Contact Was Open"
        log.trace "Waiting to close garage door 2 with contact."
    	def offDelay = threshold.toInteger() * 60
        log.debug "runIn($offDelay)"
        runIn(offDelay, TurnOnSwitch2)
	}
}  

def TurnOnSwitch1(evt) {
	log.debug "state.counter1 = ${state.counter1}"
    if (contact1.currentContact == "closed") {
    	log.trace "Garge Door 2 closed"
		state.counter1 = 1
        unschedule(TurnOnSwitch1)
    }
    else if ((contact1.currentContact == "open").and(state.counter1 <= numberoftimes.toInteger())) {
    	log.trace "Wait done, closing large garage door."
		thedoor1.close()
        state.counter1 = state.counter1 + 1
        def offDelay2 = threshold2.toInteger() * 60
        log.debug "runIn($offDelay2)"
        runIn(offDelay2, TurnOnSwitch1)
	}
	else if ((contact1.currentContact == "open").and(state.counter1 > numberoftimes.toInteger())) {
        state.counter1 = 1
        log.trace "Garge Door 1 did not close"
	}
}

def TurnOnSwitch2(evt) {
	log.debug "state.counter2 = ${state.counter2}"
    if (contact2.currentContact == "closed") {
    	log.trace "Garge Door 2 closed"
        state.counter2 = 1
        unschedule(TurnOnSwitch2)
    }
    else if ((contact2.currentContact == "open").and(state.counter2 <= numberoftimes.toInteger())) {
    	log.trace "Wait done, closing small garage door."
		thedoor2.close()
        state.counter2 = state.counter2 + 1
        def offDelay2 = threshold2.toInteger() * 60
        log.debug "runIn($offDelay2)"
        runIn(offDelay2, TurnOnSwitch2)
	}
	else if ((contact2.currentContact == "open").and(state.counter2 > numberoftimes.toInteger())) {
        state.counter2 = 1
        log.trace "Garge Door 2 did not close"
	}
}