/**
 *  Copyright 2015 SmartThings
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
 *  Smart Nightlight
 *
 *  Author: SmartThings
 *
 */
definition(
    name: "Turn on light when door is open and turn off after some minutes",
    namespace: "jamesbc619",
    author: "James Clark",
    description: "Turn on light when door is open and turn off after some minutes",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("When door open...") {
		input "contact", "capability.contactSensor", title: "Door?", required: true
	}
    section("Control this light...") {
		input "light", "capability.switch", title: "Switch?", required: true
	}
    section("Turning light off minutes (optional)...") {
		input "delayMinutes", "number", title: "Minutes?", required: false
	}
	section ("Sunrise offset (optional)...") {
		input "sunriseOffsetValue", "text", title: "HH:MM", required: false
		input "sunriseOffsetDir", "enum", title: "Before or After", required: false, options: ["Before","After"]
	}
	section ("Sunset offset (optional)...") {
		input "sunsetOffsetValue", "text", title: "HH:MM", required: false
		input "sunsetOffsetDir", "enum", title: "Before or After", required: false, options: ["Before","After"]
	}
	section ("Zip code (optional, defaults to location coordinates when location services are enabled)...") {
		input "zipCode", "text", title: "Zip code", required: false
	}
}

def installed() {
    state.lightOn = "off"
    initialize()
}

def updated() {
	unsubscribe()
	unschedule()
	initialize()
}

def initialize() {
    subscribe(contact, "contact", contactHandler)
	subscribe(location, "position", locationPositionChange)
    subscribe(location, "sunriseTime", sunriseSunsetTimeHandler)
    subscribe(location, "sunsetTime", sunriseSunsetTimeHandler)
    astroCheck()
}

def locationPositionChange(evt) {
	log.trace "locationChange()"
	astroCheck()
}

def sunriseSunsetTimeHandler(evt) {
	state.lastSunriseSunsetEvent = now()
	log.debug "SmartNightlight.sunriseSunsetTimeHandler($app.id)"
	astroCheck()
}

def astroCheck() {
	def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)
	state.riseTime = s.sunrise.time
	state.setTime = s.sunset.time
	log.debug "rise: ${new Date(state.riseTime)}($state.riseTime), set: ${new Date(state.setTime)}($state.setTime)"
    def timeSunset = new Date(state.setTime)
    runOnce(timeSunset, turnOn)
    def timeSunrise = new Date(state.riseTime)
    runOnce(timeSunrise, turnOffLights)
}

private enabled() {
    def result
    def t = now()
    result = t < state.riseTime || t > state.setTime
	result
}

private getSunriseOffset() {
	sunriseOffsetValue ? (sunriseOffsetDir == "Before" ? "-$sunriseOffsetValue" : sunriseOffsetValue) : null
}

private getSunsetOffset() {
	sunsetOffsetValue ? (sunsetOffsetDir == "Before" ? "-$sunsetOffsetValue" : sunsetOffsetValue) : null
}

def turnOn() {
    if (light.currentSwitch == "off" && contact.currentContact == "open") {
        log.debug "after dark, door is open, light off turning on"
        state.lightOn = "on"
        light.on()
    }
}

def contactHandler(evt) {
    if (evt.value == "open") {
        log.trace "contactHandler: $evt ${evt.value}"
        if (enabled()) {
            log.debug "it's after dark"
            unschedule(turnOffLights)
            if (light.currentSwitch == "off") {
                log.debug "light off turning on"
                state.lightOn = "on"
                light.on()
            }
        }
 	}
	else {
		log.trace "contactHandler: $evt ${evt.value}"
        if (state.lightOn == "on"){
            if (delayMinutes){
                log.debug "light turning on waiting to turn off"
                runIn(delayMinutes*60, turnOffLights)
            }
            else {
            	log.debug "light turning on turn off"
                state.lightOn = "off"
                light.off()
			}
		}
	}
}

def turnOffLights() {
        log.debug "Done waiting turning off light"
        state.lightOn = "off"
        light.off()
}