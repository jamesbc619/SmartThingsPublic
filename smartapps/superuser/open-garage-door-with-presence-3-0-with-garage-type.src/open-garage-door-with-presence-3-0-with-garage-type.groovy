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
    name: "Open garage door with presence 3.0 with garage type",
    namespace: "",
    author: "James Clark",
    description: "Open garage door with presence 3.0",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Person..") {
        input "person1", "capability.presenceSensor", title: "Person's 1 Phone?", required: true
        input "person2", "capability.presenceSensor", title: "Person's 2 Phone?", required: true
	}
    section("Select large garage door and car..") {
		input "car1", "capability.presenceSensor", title: "Car sensor 1?", required: true
		input "thedoor2", "capability.doorControl", title: "Door 1?", required: true
	}
    section("Select small garage door and car..") {
		input "car2", "capability.presenceSensor", title: "Car sensor 2?", required: true
		input "thedoor2", "capability.doorControl", title: "Door 2?", required: true
	}
    section("How long until iPhone and car not present check out..") {
        input "threshold", "number", title: "Minutes? (ex:5)", required: true
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    subscribe(person1, "presence", presence1)
    subscribe(car1, "presence", carpresence1)
    subscribe(person2, "presence", presence2)
    subscribe(car2, "presence", carpresence2)
	state.people1car1 = "arrived"
	state.people2car1 = "arrived"
    state.people1car2 = "arrived"
	state.people2car2 = "arrived"
    state.iphonepresentnocar1 = "present"
    state.iphonepresentnocar2 = "present"
	state.carpresentnocar1 = "present"
    state.carpresentnocar2 = "present"
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    installed()
}

def presence1(evt) {
    log.debug "iPhone1 evt.name: $evt.value"
    if (evt.value == "not present") {
		if ((car1.currentPresence == "not present").and(state.people2car1 == "arrived").and(state.carpresentnocar1 == "present")) {
			log.trace "Car 1 departed then person 1"
			state.people1car1 = "departed"
			unschedule(carpresentdelay1)
		}
		else if ((car2.currentPresence == "not present").and(state.people2car2 == "arrived").and(state.carpresentnocar2 == "present")) {
			log.trace "Car 2 departedt then person 1"
			state.people1car2 = "departed"
			unschedule(carpresentdelay2)
		}
		else {
			log.debug "Person 1 not present no car waiting"
			def offDelay = threshold.toInteger()*60
            log.debug "runIn($offDelay)"
			runIn(offDelay, iphonepresentdelay1)
		}
    }
	if (evt.value == "present") {
		unschedule(iphonepresentdelay1)
		state.iphonepresentnocar1 = "present"
    	if (state.people1car1 == "departed") {
            log.debug "Person 1 open door 1"
			state.people1car1 = "arrived"
            thedoor1.open()
		}
    	else if (state.people1car2 == "departed") {
			log.debug "Person 1 open door 2"
			state.people1car2 = "arrived"
            thedoor2.open()
		}
	}
}

def iphonepresentdelay1(evt) {
	log.debug "Done waiting person 1 not present no car waiting"
	state.iphonepresentnocar1 = "not present"
}

def carpresence1(evt) {
	log.debug "Car1 evt.name: $evt.value"
    if (evt.value == "not present") {
		if ((person1.currentPresence == "not present").and(state.people1car2 == "arrived").and(state.iphonepresentnocar1 == "present")) {
			log.trace "Person 1 departed then car 1"
			state.people1car1 = "departed"
			unschedule(iphonepresentdelay1)
		}
		else if ((person2.currentPresence == "not present").and(state.people2car2 == "arrived").and(state.iphonepresentnocar2 == "present")) {
			log.trace "Person 2 departed then car 1"
			state.people2car1 = "departed"
			unschedule(iphonepresentdelay2)
		}
		else {
			log.debug "Car 1 not present no person waiting"
			def offDelay = threshold.toInteger()*60
            log.debug "runIn($offDelay)"
			runIn(offDelay, carpresentdelay1)
		}
	}
    if (evt.value == "present") {
        unschedule(carpresentdelay1)        
        if ((state.people1car1 == "departed").or(state.people2car1 == "departed").or(state.carpresentnocar1 == "not present")) {
        	log.debug "Car 1 arrived before present or no present departed open door"
			state.carpresentnocar1 = "present"
            state.people1car1 = "arrived"
        	state.people2car1 = "arrived"
            /*thedoor1.open()*/
        }
	}
}

def carpresentdelay1(evt) {
  	log.debug "Done waiting no pearsons present for car 1"
   	state.carpresentnocar1 = "not present"
}

def presence2(evt) {
    log.debug "iPhone2 evt.name: $evt.value"
    if (evt.value == "not present") {
		if ((car2.currentPresence == "not present").and(state.people1car2 == "arrived").and(state.carpresentnocar2 == "present")) {
			log.trace "Car 2 departed then person 2"
			state.people2car2 = "departed"
			unschedule(carpresentdelay2)
		}
		else if ((car1.currentPresence == "not present").and(state.people1car1 == "arrived").and(state.carpresentnocar1 == "present")) {
			log.trace "Car 1 departed then person 2"
			state.people2car1 = "departed"
			unschedule(carpresentdelay1)
		}
		else {
			log.debug "Person 2 not present no car waiting"
			def offDelay = threshold.toInteger()*60
			log.debug "runIn($offDelay)"
            runIn(offDelay, iphonepresentdelay2)
		}
    }
	if (evt.value == "present") {
    	unschedule(iphonepresentdelay2)
		state.iphonepresentnocar2 = "present"
    	if (state.people2car2 == "departed") {
            log.debug "Person 2 open door 2"
			state.people2car2 = "arrived"
            thedoor2.open()
		}
    	else if (state.people2car1 == "departed") {
			log.debug "Person 2 open door 1"
			state.people2car1 = "arrived"
            thedoor1.open()
		}
	}
}

def iphonepresentdelay2(evt) {
	log.debug "Done waiting person 2 not present no car waiting"
	state.iphonepresentnocar2 = "not present"
}

def carpresence2(evt) {
    log.debug "Car2 evt.name: $evt.value"
    if (evt.value == "not present") {
		if ((person2.currentPresence == "not present").and(state.people2car1 == "arrived").and(state.iphonepresentnocar2 == "present")) {
			log.trace "Person 2 departed then car 2"
			state.people2car2 = "departed"
			unschedule(iphonepresentdelay2)
		}
		else if ((person1.currentPresence == "not present").and(state.people1car1 == "arrived").and(state.iphonepresentnocar1 == "present")) {
			log.trace "Person 1 departed then car 2"
			state.people1car2 = "departed"
			unschedule(iphonepresentdelay1)
		}
		else {
			log.debug "Car 2 not present no person waiting"
			def offDelay = threshold.toInteger()*60
            log.debug "runIn($offDelay)"
            runIn(offDelay, carpresentdelay2)
		}
	}
    if (evt.value == "present") {
        unschedule(carpresentdelay2)
        if ((state.people2car2 == "departed").or(state.people1car2 == "departed").or(state.carpresentnocar2 == "not present")) {
        	log.debug "Car 2 arrived before present or no present departed open door"        	
            state.carpresentnocar2 = "present"
        	state.people2car2 = "arrived"
        	state.people1car2 = "arrived"
            /*thedoor2.open()*/
 		}
	}
}

def carpresentdelay2(evt) {
  	log.debug "Done waiting no pearsons present for car 2"
   	state.carpresentnocar2 = "not present"
}