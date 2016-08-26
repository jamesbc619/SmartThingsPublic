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
    name: "Open garage door with presence 3.2",
    namespace: "",
    author: "James Clark",
    description: "Open garage door with presence 3.2",
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
        input "contact1", "capability.contactSensor", title: "Door sensor 1?", required: true
		input "thedoor1", "capability.switch", title: "Door Switch 1?", required: true
	}
    section("Select small garage door and car..") {
		input "car2", "capability.presenceSensor", title: "Car sensor 2?", required: true
        input "contact2", "capability.contactSensor", title: "Door sensor 2?", required: true
		input "thedoor2", "capability.switch", title: "Door Switch 2?", required: true
	}
    section("How long until iPhone and car not present check out..") {
        input "threshold1", "number", title: "Minutes? (ex:5)", required: true
    }
	section("Delay for false check outs..") {
        input "threshold2", "number", title: "Minutes? (ex:1)", required: true
    }
    section("Number of time door is open before it is disable if car as not arrived..") {
        input "disablecount1", "number", title: "Number of Times? (ex:2)", required: true
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
    state.people1car1wait = "arrived"
    state.people2car1wait = "arrived"
    state.people1car2wait = "arrived"
	state.people2car2wait = "arrived"
    state.iphonepresentnocar1 = "present"
    state.iphonepresentnocar2 = "present"
	state.carpresentnocar1 = "present"
    state.carpresentnocar2 = "present"
    state.car1disablecount = 0
    state.car2disablecount = 0
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    installed()
}

def presence1(evt) {
    log.debug "iPhone1 evt.name: $evt.value"
    if (evt.value == "not present") {
        log.debug "People 1 Car 1 Disable Count ($state.car1disablecount)"
    	log.debug "People 1 Car 2 Disable Count ($state.car2disablecount)"
		if ((car1.currentPresence == "not present").and(state.people2car1 == "arrived").and(state.people2car1wait == "arrived").and(state.carpresentnocar1 == "present").and(state.car1disablecount < disablecount1)) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Car 1 departed then person 1 waiting"
			unschedule(carpresentdelay1)
            state.people1car1wait = "departed"
			runIn(offDelay2, people1car1departed)
		}
		else if ((car2.currentPresence == "not present").and(state.people2car2 == "arrived").and(state.people2car2wait == "arrived").and(state.carpresentnocar2 == "present").and(state.car2disablecount < disablecount1)) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Car 2 departed then person 1 waiting"
			unschedule(carpresentdelay2)
            state.people1car2wait = "departed"
			runIn(offDelay2, people1car2departed)
		}
		else {
			log.debug "Person 1 not present no car waiting"
			def offDelay1 = threshold1.toInteger()*60
            log.debug "runIn($offDelay1)"
			runIn(offDelay1, iphonepresentdelay1)
		}
    }
	if (evt.value == "present") {
		unschedule(iphonepresentdelay1)
        unschedule(people1car1departed)
        unschedule(people1car2departed)
		state.iphonepresentnocar1 = "present"
        state.people1car1wait = "arrived"
		state.people1car2wait = "arrived"
    	if ((state.people1car1 == "departed").and(contact1.currentContact == "closed")) {
            log.debug "Person 1 open door 1"
			state.people1car1 = "arrived"
            state.car1disablecount = state.car1disablecount + 1
            thedoor1.on()
		}
    	else if ((state.people1car2 == "departed").and(contact2.currentContact == "closed")) {
			log.debug "Person 1 open door 2"
			state.people1car2 = "arrived"
            state.car2disablecount = state.car2disablecount + 1
            thedoor2.on()
		}
	}
}

def iphonepresentdelay1(evt) {
	log.debug "Done waiting person 1 not present no car waiting"
	state.iphonepresentnocar1 = "not present"
}

def people1car1departed(evt) {
	log.trace "Done waiting car 1 departed then person 1"
	state.people1car1 = "departed"
}

def people1car2departed(evt) {
	log.trace "Done waiting car 2 departed then person 1"
	state.people1car2 = "departed"
}

def carpresence1(evt) {
	log.debug "Car1 evt.name: $evt.value"
    if (evt.value == "not present") {
		if ((person1.currentPresence == "not present").and(state.people1car2 == "arrived").and(state.people1car2wait == "arrived").and(state.iphonepresentnocar1 == "present")) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Person 1 departed then car 1 wating"
			unschedule(iphonepresentdelay1)
			state.people1car1wait = "departed"
            runIn(offDelay2, people1car1departed)
		}
		else if ((person2.currentPresence == "not present").and(state.people2car2 == "arrived").and(state.people2car2wait == "arrived").and(state.iphonepresentnocar2 == "present")) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Person 2 departed then car 1 wating"
			unschedule(iphonepresentdelay2)
			state.people2car1wait = "departed"
            runIn(offDelay2, people2car1departed)
		}
		else {
			log.debug "Car 1 not present no person waiting"
			def offDelay1 = threshold1.toInteger()*60
            log.debug "runIn($offDelay1)"
			runIn(offDelay1, carpresentdelay1)
		}
	}
    if (evt.value == "present") {
		unschedule(people1car1departed)
        unschedule(people2car1departed)
        unschedule(carpresentdelay1)        
		state.car1disablecount = 0
        if ((state.people1car1 == "departed").or(state.people2car1 == "departed").or(state.carpresentnocar1 == "not present")) {
        	log.debug "Car 1 arrived before present or no present departed"
			state.carpresentnocar1 = "present"
            state.people1car1 = "arrived"
        	state.people2car1 = "arrived"
            state.people1car1wait = "arrived"
        	state.people2car1wait = "arrived"
        	if (contact1.currentContact == "closed") {
        		log.debug "Car 1 open door 1"
            	/*thedoor1.on()*/
        	}
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
		log.debug "People 2 Car 2 Disable Count ($state.car2disablecount)"
    	log.debug "People 2 Car 1 Disable Count ($state.car1disablecount)"
        if ((car2.currentPresence == "not present").and(state.people1car2 == "arrived").and(state.people1car2wait == "arrived").and(state.carpresentnocar2 == "present").and(state.car2disablecount < disablecount1)) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Car 2 departed then person 2 waiting"
			unschedule(carpresentdelay2)
			state.people2car2wait = "departed"
            runIn(offDelay2, people2car2departed)
		}
		else if ((car1.currentPresence == "not present").and(state.people1car1 == "arrived").and(state.people1car1wait == "arrived").and(state.carpresentnocar1 == "present").and(state.car1disablecount < disablecount1)) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Car 1 departed then person 2 waiting"
			unschedule(carpresentdelay1)
            state.people2car1wait = "departed"
			runIn(offDelay2, people2car1departed)
		}
		else {
			log.debug "Person 2 not present no car waiting"
			def offDelay1 = threshold1.toInteger()*60
			log.debug "runIn($offDelay1)"
            runIn(offDelay1, iphonepresentdelay2)
		}
    }
	if (evt.value == "present") {
    	unschedule(iphonepresentdelay2)
		state.iphonepresentnocar2 = "present"
		state.people2car2wait = "arrived"
		state.people2car1wait = "arrived"
    	if ((state.people2car2 == "departed").and(contact2.currentContact == "closed")) {
            log.debug "Person 2 open door 2"
			state.people2car2 = "arrived"
            state.car2disablecount = state.car2disablecount + 1
            thedoor2.on()
		}
    	else if ((state.people2car1 == "departed").and(contact1.currentContact == "closed")) {
			log.debug "Person 2 open door 1"
			state.people2car1 = "arrived"
            state.car1disablecount = state.car1disablecount + 1
            thedoor1.on()
		}
	}
}

def iphonepresentdelay2(evt) {
	log.debug "Done Waiting person 2 not present no car waiting"
	state.iphonepresentnocar2 = "not present"
}

def people2car2departed(evt) {
	log.trace "Done Waiting car 2 departed then person 2"
	state.people2car2 = "departed"
}

def people2car1departed(evt) {
	log.trace "Done Waiting car 1 departed then person 2"
	state.people2car1 = "departed"
}

def carpresence2(evt) {
    log.debug "Car2 evt.name: $evt.value"
    if (evt.value == "not present") {
		if ((person2.currentPresence == "not present").and(state.people2car1 == "arrived").and(state.people2car1wait == "arrived").and(state.iphonepresentnocar2 == "present")) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Person 2 departed then car 2 waiting"
			unschedule(iphonepresentdelay2)
            state.people2car2wait = "departed"
			runIn(offDelay2, people2car2departed)
		}
		else if ((person1.currentPresence == "not present").and(state.people1car1 == "arrived").and(state.people1car1wait == "arrived").and(state.iphonepresentnocar1 == "present")) {
			def offDelay2 = threshold2.toInteger()*60
            log.debug "runIn($offDelay2)"
			log.trace "Person 1 departed then car 2 waiting"
			unschedule(iphonepresentdelay1)
			state.people1car2wait = "departed"
            runIn(offDelay2, people1car2departed)
		}
		else {
			log.debug "Car 2 not present no person waiting"
			def offDelay1 = threshold1.toInteger()*60
            log.debug "runIn($offDelay1)"
            runIn(offDelay1, carpresentdelay2)
		}
	}
    if (evt.value == "present") {
        unschedule(people2car2departed)
        unschedule(people1car2departed)
        unschedule(carpresentdelay2)
		state.car2disablecount = 0
        if ((state.people2car2 == "departed").or(state.people1car2 == "departed").or(state.carpresentnocar2 == "not present")) {
        	log.debug "Car 2 arrived before present or no present departed"
			state.carpresentnocar2 = "present"
        	state.people2car2 = "arrived"
        	state.people1car2 = "arrived"
            state.people2car2wait = "arrived"
        	state.people1car2wait = "arrived"
            if (contact2.currentContact == "closed") {
           		log.debug "Car 2 open door 2"
           		/*thedoor2.on()*/
            }
 		} 
	}
}

def carpresentdelay2(evt) {
  	log.debug "Done waiting no pearsons present for car 2"
   	state.carpresentnocar2 = "not present"
}