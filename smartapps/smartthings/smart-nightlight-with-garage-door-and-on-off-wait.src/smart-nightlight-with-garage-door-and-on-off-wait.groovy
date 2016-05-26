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
    name: "Smart Nightlight with Garage Door and On / Off wait",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Turns on lights when it's dark and motion is detected. Turns lights off when it becomes light or some time after motion ceases.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance@2x.png"
)

preferences {
	section("Control these lights..."){
		input "lights", "capability.switch", required: true
	}
	section("Turning on when it's dark and there's movement..."){
		input "motionSensor", "capability.motionSensor", title: "Where?", required: true
	}
    section("Garage doors...") {
		input "contact1", "capability.contactSensor", title: "Sensor 1?" , required: true
        input "contact2", "capability.contactSensor", title: "Sensor 2?" , required: true
	}
	section("And then off when it's light or there's been no movement for..."){
		input "delayMinutes", "number", title: "Minutes?"
	}
	section("If turn off light with switch, the motion sensor will not turn on the light for the specified minutes"){
        input "offdelayminutes", "number", title: "Minutes" , required: true
    }
    section("If you turn on light with switch, the light will not turn off for the specified minutes"){
        input "ondelayminutes", "number", title: "Minutes" , required: true
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
	initialize()
}

def updated() {
	unsubscribe()
	unschedule()
	initialize()
}

def initialize() {
	subscribe(motionSensor, "motion", motionHandler)
    subscribe(lights, "switch", switchHandler)
    subscribe(contact1, "contact", contactHandler)
    subscribe(contact2, "contact", contactHandler)
	subscribe(location, "position", locationPositionChange)
	subscribe(location, "sunriseTime", sunriseSunsetTimeHandler)
	subscribe(location, "sunsetTime", sunriseSunsetTimeHandler)
	astroCheck()
	state.lightstayoff = false
    state.lightstayon = false
    state.motionevt = "inactive"
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
	log.debug "$evt.name: $evt.value"
	if (evt.value == "active") {
		state.motionevt = "active"
        log.trace "state.lightstayoff = $state.lightstayoff"
        log.trace "state.lightstayon = $state.lightstayon"
        if ((enabled() || ((contact1.currentContact == "closed") && (contact2.currentContact == "closed"))) && !state.lightstayoff && !state.lightstayon) {
			log.debug "turning on lights due to motion"
			lights.on()
			state.lastStatus = "on"
			runIn(3, SetLigthsStayOnStateAfterDelayMotion)
		}
		state.motionStopTime = null
	}
	else {
		state.motionevt = "inactive"
        state.motionStopTime = now()
		if(delayMinutes) {
			runIn(delayMinutes*60, turnOffMotionAfterDelay, [overwrite: false])
		} else {
			turnOffMotionAfterDelay()
		}
	}
}

def SetLigthsStayOnStateAfterDelayMotion() {
	unsubscribe (SetLigthsStayOnStateAfterDelaylightOff)
    log.trace "Setting turn on state after delay - motion"
	state.lightstayon = false
}

def turnOffMotionAfterDelay() {
	log.trace "In turnOffMotionAfterDelay, state.motionStopTime = $state.motionStopTime, state.lastStatus = $state.lastStatus"
	if (state.motionStopTime && state.lastStatus != "off") {
		def elapsed = now() - state.motionStopTime
        log.trace "elapsed = $elapsed"
		log.trace "state.lightstayoff = $state.lightstayoff"
        log.trace "state.lightstayon = $state.lightstayon"
        if ((elapsed >= ((delayMinutes ?: 0) * 60000L) - 2000) && !state.lightstayoff && !state.lightstayon) {
        	log.debug "Turning off lights"
			lights.off()
			state.lastStatus = "off"
            runIn(3, SetLigthsStayOffStateAfterDelayMotion)
		}
	}
}

def SetLigthsStayOffStateAfterDelayMotion() {
	unsubscribe(SetLigthsStayOffStateAfterDelay)
    log.trace "Setting turn off state after delay - motion"
	state.lightstayoff = false
}

def contactHandler(evt) {
	log.debug "$evt.name: $evt.value"
	if ((evt.value == "open") && !enabled() && !state.lightstayoff && !state.lightstayon) {
        SwitchTurnedOffLight()
    }
	if (((contact1.currentContact == "closed") && (contact2.currentContact == "closed")) && (state.motionevt == "active") && !state.lightstayoff && !state.lightstayon) {
        SwitchTurnedOnLight()
    }
}

def scheduleCheck() {
	log.debug "In scheduleCheck - skipping"
	//turnOffMotionAfterDelay()
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

def switchHandler(evt) {
	log.debug "Switch Handler - Evt value: ${evt.value}"
    if(evt.value == "on")  {
    	unsubscribe (SetLigthsStayOffStateAfterDelay)
        state.lightstayon = true
		state.lightstayoff = false	
		runIn(ondelayminutes*60, SetLigthsStayOnStateAfterDelay)		
    }
	if (evt.value == "off") {
    	unsubscribe (SetLigthsStayOnStateAfterDelay)
        state.lightstayoff = true
		state.lightstayon = false
		runIn(offdelayminutes*60, SetLigthsStayOffStateAfterDelay)
    }
}

def SetLigthsStayOnStateAfterDelay() {
	log.trace "Setting turn on state after delay and turn off switch - switch"
	state.lightstayon = false
    if (state.motionevt == "inactive") {
    	SwitchTurnedOffLight()
    }
}

def SetLigthsStayOffStateAfterDelay() {
	log.trace "Setting turn off state after delay - switch"
	state.lightstayoff = false
    if ((enabled() || ((contact1.currentContact == "closed") && (contact2.currentContact == "closed"))) && (state.motionevt == "active")) {
    	SwitchTurnedOnLight()
    }
}

def SwitchTurnedOffLight() {
	log.debug "Switch Turning off lights"
	lights.off()
	state.lastStatus = "off"
    runIn(3, SetLigthsStayOffStateAfterDelayMotion)
}

def SwitchTurnedOnLight() {
	log.debug "Switch Turning on lights"
	lights.on()
	state.lastStatus = "on"
    runIn(3, SetLigthsStayOnStateAfterDelayMotion)
}