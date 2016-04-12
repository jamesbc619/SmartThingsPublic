/**
 *  Open garage doors
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
    name: "Open garage door with presence 2.0",
    namespace: "",
    author: "James Clark",
    description: "Open garage door with presence 2.0",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Person...") {
        input "person1", "capability.presenceSensor", title: "Person's 1 iPhone?", required: true
        input "person2", "capability.presenceSensor", title: "Person's 2 iPhone?", required: true
    }
    section("Select large garage door and car...") {
		input "car1", "capability.presenceSensor", title: "Car sensor 1?", required: true
        input "contact1", "capability.contactSensor", title: "Door sensor 1?", required: true
		input "thedoor1", "capability.switch", title: "Door Switch 1?", required: true
	}
    section("Select small garage door and car...") {
		input "car2", "capability.presenceSensor", title: "Car sensor 2?", required: true
        input "contact2", "capability.contactSensor", title: "Door sensor 2?", required: true
		input "thedoor2", "capability.switch", title: "Door Switch 2?", required: true
	}
    section("Delay and number of times to check pearsons presece sensor...") {
        input "numberoftimes", "number", title: "Number of times? (ex:75)", required: true
        input "threshold", "number", title: "Secounds per time? (ex:5)", required: true
    }
    section("How long until iPhone not present and not in a car...") {
        input "threshold2", "number", title: "Secounds? (ex:300)", required: true
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    subscribe(person1, "presence", presence1)
    subscribe(car1, "presence", carpresence1)
    subscribe(person2, "presence", presence2)
    subscribe(car2, "presence", carpresence2)
    state.people1car1 = "Arrived"
	state.people2car1 = "Arrived"
    state.people1car2 = "Arrived"
	state.people2car2 = "Arrived"
    state.counter1 = 1
    state.counter2 = 1
    state.iphonepresentnocar1 = "present"
    state.iphonepresentnocar2 = "present"
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    installed()
}

def presence1(evt) {
    log.debug "iPhone1 evt.name: $evt.value"
    if (evt.value == "present") {
    	state.iphonepresentnocar1 = "present"
   		unschedule(iphonepresentdelay1)
    	if ((state.people1car1 == "Departed").and(contact1.currentContact == "closed")) {
            log.debug "Person 1 open door 1."
			state.people1car1 = "Arrived"
            thedoor1.on()
		}
    	else if ((state.people1car2 == "Departed").and(contact2.currentContact == "closed")) {
			log.debug "Person 1 open door 2."
			state.people1car2 = "Arrived"
            thedoor2.on()
		}
	}
    if (evt.value == "not present") {
    	log.debug "Person 1 not present no car waiting"
		def offDelay2 = threshold2.toInteger()
        log.debug "runIn($offDelay2)"
		runIn(offDelay2, iphonepresentdelay1)
    }
}

def iphonepresentdelay1(evt) {
	if ((state.people1car1 == "Arrived").and(state.people1car2 == "Arrived")) {
  		log.debug "Person 1 not present no car done waiting"
   		state.iphonepresentnocar1 = "not present"
	}        
}

def carpresence1(evt) {
	log.debug "Car1 evt.name: $evt.value"
    if (evt.value == "not present") {
		presentcheck1()
	}
    if (evt.value == "present") {
    	unschedule(presentcheck1)
		state.counter1 = 1
        if ((state.people1car1 == "Departed").or(state.people2car1 == "Departed")) {
        	log.debug "Car 1 arrived before person or door 1 was open."
        	state.people1car1 = "Arrived"
            state.people2car1 = "Arrived"
            if (contact1.currentContact == "closed") {
            	log.debug "Car 1 open 1."
            	/*thedoor1.on() */
 			} 
		}
        else {
        	log.debug "Car 1 arrived else."
			if (contact1.currentContact == "closed") {
            	log.debug "Car 1 open 1."
            	/*thedoor1.on() */
 			} 
		}
	}
}

def presentcheck1(evt) {
    log.debug "state.iphonepresentnocar1 = ${state.iphonepresentnocar1}"
	log.debug "state.iphonepresentnocar2 = ${state.iphonepresentnocar2}"
	log.debug "state.people1car1 = ${state.people1car1}"
	log.debug "state.people2car1 = ${state.people2car1}"
	log.debug "state.people1car2 = ${state.people1car2}"
	log.debug "state.people2car2 = ${state.people2car2}"
	log.debug "person1 = ${person1.currentPresence}"
	log.debug "person2 = ${person2.currentPresence}"
    log.debug "state.counter1 = ${state.counter1}"
    if ((person1.currentPresence == "not present").and(state.people1car2 == "Arrived").and(state.iphonepresentnocar1 == "present")) {
    	log.trace "Pearson 1 not present car 1 not present."
    	state.people1car1 = "Departed"
        unschedule(iphonepresentdelay1)
	}
    else if ((person2.currentPresence == "not present").and(state.people2car2 == "Arrived").and(state.iphonepresentnocar2 == "present")) {
    	log.trace "Pearson 2 not present car 1 not present."
    	state.people2car1 = "Departed"
        unschedule(iphonepresentdelay2)
	}
	else if (state.counter1 <= numberoftimes.toInteger()) {
        state.counter1 = state.counter1 + 1
        log.trace "Waiting pearsons present for car 1."
        def offDelay = threshold.toInteger()
		log.debug "runIn($offDelay)"
        runIn(offDelay, presentcheck1)
    }
	else {
    	log.trace "Done waiting no pearsons present for car 1."
    }
}

def presence2(evt){
    log.debug "iPhone2 evt.name: $evt.value"
    if (evt.value == "present") {
    state.iphonepresentnocar2 = "present"
    unschedule(iphonepresentdelay2)
    	if ((state.people2car2 == "Departed").and(contact2.currentContact == "closed")) {
			log.debug "Person 2 open door 2."
			state.people2car2 = "Arrived"
            thedoor2.on()
		}
    	else if ((state.people2car1 == "Departed").and(contact2.currentContact == "closed")) {
			log.debug "Person 2 open Door 1."
			state.people2car1 = "Arrived"
            thedoor1.on()
		}
    }       
    if (evt.value == "not present") {
    	log.debug "Person 2 not present no car waiting."
		def offDelay2 = threshold2.toInteger()
        log.debug "runIn($offDelay2)"
		runIn(offDelay2, iphonepresentdelay2)
    }
}

def iphonepresentdelay2(evt) {
	if ((state.people2car1 == "Arrived").and(state.people2car2 == "Arrived")) {
		log.debug "Person 2 not present no car done waiting."
    	state.iphonepresentnocar2 = "not present"
	}        
}

def carpresence2(evt) {
	log.debug "Car 2 evt.name: $evt.value"
    if (evt.value == "not present") {
		presentcheck2()
    }
    if (evt.value == "present") {
		unschedule(presentcheck2)
        state.counter2 = 1
		if ((state.people2car2 == "Departed").or(state.people1car2 == "Departed")) {
        	log.debug "Car 2 arrived before person or door 2 was open."
        	state.people1car2 = "Arrived"
            state.people2car2 = "Arrived"
            if (contact2.currentContact == "closed") {
            	log.debug "Car 2 open 2."
            	/*thedoor2.on() */
 			}               
		}
        else {
        	log.debug "Car 2 arrived else."
			if (contact2.currentContact == "closed") {
            	log.debug "Car 2 open 2."
            	/*thedoor2.on() */
 			}
		}
	}
}

def presentcheck2(evt) {
    log.debug "state.iphonepresentnocar1 = ${state.iphonepresentnocar1}"
	log.debug "state.iphonepresentnocar2 = ${state.iphonepresentnocar2}"
	log.debug "state.people1car1 = ${state.people1car1}"
	log.debug "state.people2car1 = ${state.people2car1}"
	log.debug "state.people1car2 = ${state.people1car2}"
	log.debug "state.people2car2 = ${state.people2car2}"
	log.debug "person1 = ${person1.currentPresence}"
	log.debug "person2 = ${person2.currentPresence}"
    log.debug "state.counter2 = ${state.counter2}"
	if ((person2.currentPresence == "not present").and(state.people2car1 == "Arrived").and(state.iphonepresentnocar2 == "present")) {
    	log.trace "Pearson 2 not present car 2 not present."
    	state.people2car2 = "Departed"
        unschedule(iphonepresentdelay2)
	}
    else if ((person1.currentPresence == "not present").and(state.people1car1 == "Arrived").and(state.iphonepresentnocar1 == "present")) {
    	log.trace "Pearson 1 not present car 2 not present."
    	state.people1car2 = "Departed"
        unschedule(iphonepresentdelay1)
	}
    else if (state.counter2 <= numberoftimes.toInteger()) {
        state.counter2 = state.counter2 + 1
        log.trace "Waiting pearsons present for car 2."
        def offDelay = threshold.toInteger()
		log.debug "runIn($offDelay)"
        runIn(offDelay, presentcheck2)
    }
	else {
    	log.trace "Done waiting no present for car 2."
    }
}