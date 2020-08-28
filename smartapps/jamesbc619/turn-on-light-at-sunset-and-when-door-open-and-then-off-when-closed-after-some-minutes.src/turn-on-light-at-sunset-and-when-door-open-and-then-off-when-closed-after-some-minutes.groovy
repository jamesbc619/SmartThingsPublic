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
    name: "Turn on light at sunset and when door open and then off when closed after some minutes",
    namespace: "jamesbc619",
    author: "James Clark",
    description: "Turn on light at sunset and when door is open and turn off after some minutes",
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
    subscribe(location, "sunriseTime", sunriseTimeHandler)
    subscribe(location, "sunsetTime", sunsetTimeHandler)
    
    //Run today too
    scheduleWithOffset(location.currentValue("sunsetTime"), sunsetOffsetValue, sunsetOffsetDir, "sunsetHandler")
    scheduleWithOffset(location.currentValue("sunriseTime"), sunriseOffsetValue, sunriseOffsetDir, "sunriseHandler")
    
    startUp()
}
def startUp() {
	def now = new Date()
    def sunriseOffset = getOffsetMinuts(sunriseOffsetValue, sunriseOffsetDir)
	def sunsetOffset = getOffsetMinuts(sunsetOffsetValue, sunsetOffsetDir)
    def SunriseAndSunse = getSunriseAndSunset(sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)
    if(SunriseAndSunse.sunrise.before(now) && SunriseAndSunse.sunset.after(now)) {   
		//before midnight/after sunset or after midnight/before sunset
	  	log.info "Sun is Up"
        sunriseHandler()
	}
    else {
    	log.info "Sun is Down"
        sunsetHandler()
    }
}

private getOffsetMinuts(String offsetValue, String offsetDir) {
	def timeOffsetMinuts = offsetValue
	if (offsetDir == "Before") {
		return "-" + timeOffsetMinuts
	}
	return timeOffsetMinuts
}

def locationPositionChange(evt) {
    log.trace "locationChange()"
    updated()
}

def sunsetTimeHandler(evt) {
    log.trace "sunsetTimeHandler()"
    scheduleWithOffset(evt.value, sunsetOffsetValue, sunsetOffsetDir, "sunsetHandler")
}

def sunriseTimeHandler(evt) {
    log.trace "sunriseTimeHandler()"
    scheduleWithOffset(evt.value, sunriseOffsetValue, sunriseOffsetDir, "sunriseHandler")
}

def scheduleWithOffset(nextSunriseSunsetTime, offset, offsetDir, handlerName) {
    def nextSunriseSunsetTimeDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", nextSunriseSunsetTime)
    def offsetTime = new Date(nextSunriseSunsetTimeDate.time + getOffset(offset, offsetDir))

    log.debug "scheduling Sunrise/Sunset for $offsetTime"
    runOnce(offsetTime, handlerName, [overwrite: false])
}

def sunriseHandler() {
    log.info "Executing sunrise handler"
    state.nightTime = false
}

def sunsetHandler() {
    log.info "Executing sunset handler"
	state.nightTime = true
    turnOnSunset()
}

private getOffset(String offsetValue, String offsetDir) {
    def timeOffsetMillis = calculateTimeOffsetMillis(offsetValue)
    if (offsetDir == "Before") {
        return -timeOffsetMillis
    }
    return timeOffsetMillis
}

private calculateTimeOffsetMillis(String offset) {
    def result = 0
    if (!offset) {
        return result
    }

    def before = offset.startsWith('-')
    if (before || offset.startsWith('+')) {
        offset = offset[1..-1]
    }

    if (offset.isNumber()) {
        result = Math.round((offset as Double) * 60000L)
    } else if (offset.contains(":")) {
        def segs = offset.split(":")
        result = (segs[0].toLong() * 3600000L) + (segs[1].toLong() * 60000L)
    }

    if (before) {
        result = -result
    }

    result
}

def turnOnSunset() {
    if (light.currentSwitch == "off" && contact.currentContact == "open") {
        log.debug "after dark, door is open, light off turning on"
        state.lightOn = "on"
        light.on()
    }
}

def contactHandler(evt) {
    if (evt.value == "open") {
        log.trace "contactHandler: $evt ${evt.value}"
        if (state.nightTime) {
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