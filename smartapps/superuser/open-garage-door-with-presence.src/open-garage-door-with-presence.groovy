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
    name: "Open garage door with presence",
    namespace: "",
    author: "James Clark",
    description: "Open garage door with presence",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Person...") {
        input "people1", "capability.presenceSensor", title: "Person's 1 iPhone?", required: true
        input "people2", "capability.presenceSensor", title: "Person's 2 iPhone?", required: true
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
        input "threshold2", "number", title: "Secounds? (ex:400)", required: true
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    subscribe(people1, "presence", person1)
    subscribe(car1, "presence", carperson1)
    subscribe(people2, "presence", person2)
    subscribe(car2, "presence", carperson2)
    state.people1car1 = "Arrived"
    state.people2car1 = "Arrived"
    state.people1car2 = "Arrived"
    state.people2car2 = "Arrived"
    state.counter1 = 1
    state.counter2 = 1
    state.openwithiphone1car1 = "car"
	state.openwithiphone2car1 = "car"
    state.openwithiphone1car2 = "car"
	state.openwithiphone2car2 = "car"
    state.openwithcar1 = "Arrived"
    state.openwithcar2 = "Arrived"
    state.iphonepresentnocar1 = "present"
    state.iphonepresentnocar2 = "present"
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    subscribe(people1, "presence", person1)
	subscribe(car1, "presence", carperson1)
    subscribe(people2, "presence", person2)
    subscribe(car2, "presence", carperson2)
    state.people1car1 = "Arrived"
    state.people2car1 = "Arrived"
    state.people1car2 = "Arrived"
    state.people2car2 = "Arrived"
    state.counter1 = 1
    state.counter2 = 1
    state.openwithiphone1car1 = "car"
	state.openwithiphone2car1 = "car"
    state.openwithiphone1car2 = "car"
	state.openwithiphone2car2 = "car"
    state.openwithcar1 = "Arrived"
    state.openwithcar2 = "Arrived"
    state.iphonepresentnocar1 = "present"
    state.iphonepresentnocar2 = "present"
}

def motionHandlerinactive1(evt) {
	state.motiontimer = "inactive"
	log.trace "Motion timer done inactive"
}

def person1(evt) {
    log.debug "iPhone1 evt.name: $evt.value"
   	if (people1.currentPresence == "present"){
       	state.iphonepresentnocar1 = "present"
        unschedule(iphonepresentdelay1)
        if ((state.people1car1 == "Departed").and(contact1.currentContact == "closed")) {
    		state.openwithiphone1car1 = "iphone"
            log.debug "iPhone1 open door1"
            OpenDoor1() 
		}
   		else if ((state.people1car2 == "Departed").and(contact2.currentContact == "closed")) {
    		state.openwithiphone1car2 = "iphone"
            log.debug "iPhone1 open door2"
            OpenDoor2()
   		}
        else {
        	log.debug "iPhone 1 present door was already open or something"
		}
	}
    if (people1.currentPresence == "not present") {
    	log.debug "Person 1 not present no car waiting"
        def offDelay2 = threshold2.toInteger()
		log.debug "runIn($offDelay2)"
		runIn(offDelay2, iphonepresentdelay1)
    }
}    

def iphonepresentdelay1(evt) {
	if ((people1.currentPresence == "not present").and(state.people1car1 == "Arrived").and(state.people1car2 == "Arrived")) {
    	log.debug "Person 1 not present no car done waiting"
    	state.iphonepresentnocar1 = "not present"
    }
}

def carperson1(evt) {
    if (car1.currentPresence == "not present"){
		presentcheck1()
    }
    if (car1.currentPresence == "present") {
    	unschedule(presentcheck1)
        if ((state.openwithiphone1car1 == "iphone").or(state.openwithiphone2car1 == "iphone"))  {
        	state.people1car1 = "Arrived"
        	state.people2car1 = "Arrived"
        	state.openwithiphone1car1 = "car"
            state.openwithiphone2car1 = "car"
        	state.counter1 = 1
        	log.debug "iPhone Open Door1 for Car1"
		}
    	else if ((contact1.currentContact == "closed").and((state.openwithiphone1car1 == "car").or(state.openwithiphone2car1 == "car")).and((state.people1car1 == "Departed").or(state.people2car1 == "Departed")))  {
        	state.people1car1 = "Arrived"
        	state.people2car1 = "Arrived"
       		state.counter1 = 1
        	log.debug "Car1 open door1 defore iPhone could"
			OpenDoor1()
		}
    	else if ((contact1.currentContact == "closed").and(state.openwithcar1 == "Departed")) {
       		state.counter1 = 1
            state.openwithcar1 = "Arrived"
        	log.debug "Car1 open door1 no iPhone presence"
			OpenDoor1()
        }
		else {
        	state.people1car1 = "Arrived"
        	state.people2car1 = "Arrived"
        	state.openwithiphone1car1 = "car"
            state.openwithiphone2car1 = "car"
        	state.counter1 = 1
        	log.debug "Door1 was already open or something when car1 arravied"
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
    log.debug "state.people1 = ${people1.currentPresence}"
    log.debug "state.people2 = ${people2.currentPresence}"
    log.debug "state.counter1 = ${state.counter1}"
    if ((people1.currentPresence == "not present").and(state.people1car2 != "Departed").and(state.iphonepresentnocar1 == "present")) {
		log.trace "Pearson 1 not present car 1 not present."
        state.people1car1 = "Departed"
        unschedule(iphonepresentdelay1)
		log.debug "state.people1car1 = ${state.people1car1}"
    	log.debug "state.people2car1 = ${state.people2car1}"
    	log.debug "state.people1car2 = ${state.people1car2}"
    	log.debug "state.people2car2 = ${state.people2car2}"
    }
   	else if ((people2.currentPresence == "not present").and(state.people2car2 != "Departed").and(state.iphonepresentnocar2 == "present")) {
		log.trace "Pearson 2 not present car 1 not present."
        state.people2car1 = "Departed"
        unschedule(iphonepresentdelay2)
		log.debug "state.people1car1 = ${state.people1car1}"
    	log.debug "state.people2car1 = ${state.people2car1}"
    	log.debug "state.people1car2 = ${state.people1car2}"
    	log.debug "state.people2car2 = ${state.people2car2}"
   	}
    else if ((state.people1car1 == "Arrived").and(state.people2car1 == "Arrived").and(state.counter1 <= numberoftimes.toInteger())) {
        state.counter1 = state.counter1 + 1
        log.trace "Waiting pearsons present for car 1."
        def offDelay = threshold.toInteger()
		log.debug "runIn($offDelay)"
        runIn(5, presentcheck1)
    }
	else if ((state.people1car1 == "Arrived").and(state.people2car1 == "Arrived").and(state.counter1 > numberoftimes.toInteger())) {
    	log.trace "Done waiting pearsons present for car 1."
        state.openwithcar1 = "Departed"
    }
}

def person2(evt) {
    log.debug "iPhone 2 evt.name: $evt.value"
    if (people2.currentPresence == "present") {
       	state.iphonepresentnocar2 = "present"
        unschedule(iphonepresentdelay2)
        if ((state.people2car2 == "Departed").and(contact2.currentContact == "closed")) {
    		state.openwithiphone2car2 = "iphone"
            log.debug "iPhone2 open door2"
            OpenDoor2()
		}
   		else if ((state.people2car1 == "Departed").and(contact1.currentContact == "closed")) {
    		state.openwithiphone2car1 = "iphone"
            log.debug "iPhone2 open door1"
            OpenDoor1()
   		}
        else {
        	log.debug "iPhone 2 present door was already open or something"
		}
	}
    if (people2.currentPresence == "not present") {
    	log.debug "Person 2 not present no car waiting"
        def offDelay2 = threshold2.toInteger()
		log.debug "runIn($offDelay2)"
        runIn(offDelay2, iphonepresentdelay2)
    }
}    

def iphonepresentdelay2(evt) {
	if ((people2.currentPresence == "not present").and(state.people2car1 == "Arrived").and(state.people2car2 == "Arrived")) {
    	log.debug "Person 2 not present no car done waiting"
    	state.iphonepresentnocar2 = "not present"
	}
}   
    
def carperson2(evt) {
    if (car2.currentPresence == "not present") {
		presentcheck2()
    }
    if (car2.currentPresence == "present") {
    	unschedule(presentcheck2)
    	if ((state.openwithiphone1car2 == "iphone").or(state.openwithiphone2car2 == "iphone"))  {
        	state.people1car2 = "Arrived"
        	state.people2car2 = "Arrived"
        	state.openwithiphone1car2 = "car"
            state.openwithiphone2car2 = "car"
        	state.counter2 = 1
        	log.debug "iPhone open door2 for car2"
		}
    	else if ((contact2.currentContact == "closed").and((state.openwithiphone1car2 == "car").or(state.openwithiphone2car2 == "car")).and((state.people1car2 == "Departed").or(state.people2car2 == "Departed")))  {
        	state.people1car2 = "Arrived"
        	state.people2car2 = "Arrived"
            state.counter2 = 1
			log.debug "Car2 open door2 defore iPhone could"
/*        	OpenDoor2()*/
		}
    	else if ((contact2.currentContact == "closed").and(state.openwithcar2 == "Departed")) {
        	state.counter2 = 1
            state.openwithcar2 == "Arrived"
        	log.debug "Car2 open door2 no iPhone presence"
/*          OpenDoor2()*/
		}
		else {
		    state.people1car2 = "Arrived"
        	state.people2car2 = "Arrived"
        	state.openwithiphone1car2 = "car"
            state.openwithiphone2car2 = "car"
        	state.counter2 = 1
        	log.debug "Door2 was already open or something when car2 arravied"
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
    log.debug "state.people1 = ${people1.currentPresence}"
    log.debug "state.people2 = ${people2.currentPresence}"
    log.debug "state.counter2 = ${state.counter2}"
    if ((people2.currentPresence == "not present").and(state.people2car1 != "Departed").and(state.iphonepresentnocar2 == "present")) {
		log.trace "Pearson 2 not present car 2 not present."
        state.people2car2 = "Departed"
        unschedule(iphonepresentdelay2)
   		log.debug "state.people1car1 = ${state.people1car1}"
    	log.debug "state.people2car1 = ${state.people2car1}"
    	log.debug "state.people1car2 = ${state.people1car2}"
    	log.debug "state.people2car2 = ${state.people2car2}"
    }
   	else if ((people1.currentPresence == "not present").and(state.people1car1 != "Departed").and(state.iphonepresentnocar1 == "present")) {
		log.trace "Pearson 1 not present car 2 not present."
        state.people1car2 = "Departed"
        unschedule(iphonepresentdelay1)
   		log.debug "state.people1car1 = ${state.people1car1}"
    	log.debug "state.people2car1 = ${state.people2car1}"
    	log.debug "state.people1car2 = ${state.people1car2}"
    	log.debug "state.people2car2 = ${state.people2car2}"
   	}
    else if ((state.people1car2 == "Arrived").and(state.people2car2 == "Arrived").and(state.counter2 <= numberoftimes.toInteger())) {
        state.counter2 = state.counter2 + 1
        log.trace "Waiting pearsons present for car 2."
        def offDelay = threshold.toInteger()
		log.debug "runIn($offDelay)"
        runIn(offDelay, presentcheck2)
    }
	else if ((state.people1car2 == "Arrived").and(state.people2car2 == "Arrived").and(state.counter2 > numberoftimes.toInteger())) {
    	log.trace "Done waiting pearsons present for car 2."
        state.openwithcar2 = "Departed"
    }
}

def OpenDoor1(evt) {
	log.trace "Opening large garage door."
	thedoor1.on()
	pause(2000)
	thedoor1.off()
}

def OpenDoor2(evt) {
	log.trace "Opening small garage door."
    thedoor2.on()
	pause(2000)
	thedoor2.off()
}