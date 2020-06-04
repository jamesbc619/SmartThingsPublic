/**
 *  Water the Garder
 *
 *  Copyright 2019 James Clark
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
    name: "Water the Garden",
    namespace: "jamesbc619",
    author: "James Clark",
    description: "Turn on the water for the garden specified number of minutes in an hour. Between sunRise / sunSet with offset.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	input "switches", "capability.switch", title: "Which lights to turn on?" , multiple: true, required: true
	
	input "minutesHour", "number", title: "Turn on this many minutes in a hour?" , required: true
    
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
    initialize()
}

def updated() {
    unsubscribe()
//    unschedule()
    initialize()
}

def initialize() {
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
    state.dayTime = true
    unschedule(turnOn)
    unschedule(turnOff)
	turnOn()
}

def sunsetHandler() {
    log.info "Executing sunset handler"
	state.dayTime = false
    unschedule(turnOn)
    unschedule(turnOff)
    turnOff()
}

def turnOn() {	
    if (state.dayTime == true) { 
		log.info "Switch On"
        switches.on()
        log.info "Waiting 15 Sec to Turn Switch On Again"
		runIn(15, turnOnAgain)
	}
}

def turnOnAgain() {	 
    log.info "Switch On"
    switches.on()
    //caluculate time till off
    def timeOff = minutesHour.toInteger() * 60
    log.info "Waiting $timeOff Sec Switch Off"
    runIn(timeOff, turnOff)
}

def turnOff() {
    log.info "Switch Off"
    switches.off()
    log.info "Waiting 15 Sec to Turn Switch Off Again"
    runIn(15, turnOffAgain)
}

def turnOffAgain() {
    log.info "Switch Off"
    switches.off()
	if (state.dayTime == true) {
		//caluculate time till on 
		def timeOn = (60 - minutesHour.toInteger()) * 60
		log.info "Waiting $timeOn Sec Switch On"
        runIn(timeOn, turnOn)
	}
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