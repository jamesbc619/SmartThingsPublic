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
    name: "Turn on lights with camera motion and turn off after some minutes",
    namespace: "jamesbc619",
    author: "James Clark",
    description: "Turn off motion camera and light after some amount of time with restriction options",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    section("Cameras armed..."){
		input "armedSwitch", "capability.switch", title: "Switch?", required: true
	}
    section("Camera motion switch..."){
		input "motionSwitch", "capability.switch", title: "Switch?", required: true
	}
    section("Tern off motion switch..."){
		input "delaySeconds", "number", title: "Secounds?", required: true
	}
    section("Control these light..."){
		input "lights", "capability.switch", title: "Switch?", required: true
	}
    section("Turning on when it's dark and motion switch is on..."){
		input "delayMinutes", "number", title: "Minutes?", required: true
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
	subscribe(motionSwitch, "switch", motionHandler)
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

def motionHandler(evt) {
    if(evt.value == "on"){
        log.debug "montion switch on waiting to turn off"
        runIn(delaySeconds, turnOffMontionSwitch, [overwrite: false])
        if (enabled() && armedSwitch.currentSwitch == "on"){
            log.debug "it's after dark and cameras are armed"
            unschedule(turnOffLights)
            if (lights.currentSwitch == "off"){
                log.debug "light off turning on waiting to turn off"
                state.lightOn = "on"
                lights.on()
            }
			runIn(delayMinutes*60, turnOffLights)
        }
	}
}

def turnOffLights() {
    if (state.lightOn == "on"){
        log.debug "light was turned on done waiting to turn off trunig off"
        state.lightOn = "off"
        lights.off()
    }
}

def turnOffMontionSwitch() {
    log.debug "done waiting turn off montion switch"
    motionSwitch.off()
}

def astroCheck() {
	def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)
	state.riseTime = s.sunrise.time
	state.setTime = s.sunset.time
	log.debug "rise: ${new Date(state.riseTime)}($state.riseTime), set: ${new Date(state.setTime)}($state.setTime)"
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