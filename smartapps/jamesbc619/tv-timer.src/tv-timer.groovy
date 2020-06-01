/**
 *  Game Time
 *
 *  Copyright 2020 James Clark
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
    name: "TV Timer",
    namespace: "jamesbc619",
    author: "James Clark",
    description: "Turns off TV after X minutes in a day",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	input "remoteSwitchs", "capability.switch", title: "Remote switch?", multiple: true , required: true
    input "resetTime", "time", title: "Time reset timer?", required: true
    input "onMinutes", "number", title: "Minutes per day? (default 120)", required: false
    input "remoteOffAfterOff", "number", title: "Seconds off if remote should be off? (default 20)", required: false
    section("Warning..."){
		input "warningSwitch", "capability.switch", title: "Warning switch?", required: true
		input "warningMinutes", "number", title: "Warning minutes before off? (default 5)", required: false
        input "numFlashes", "number", title: "This number of times? (default 5)", required: false
	}
	section("Warning time settings in milliseconds..."){
		input "onFor", "number", title: "On for? (default 1000)", required: false
		input "offFor", "number", title: "Off for? (default 1000)", required: false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    resetTimerHandler()
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
    def onMinutes = onMinutes ?: 120
    state.onMinutesSec = onMinutes*60
    subscribe(remoteSwitchs, "switch", remoteSwitchHandler)
    schedule(resetTime, resetTimerHandler)
    state.warningSwitchTurnedOn = false
    adjustTime()
    //resetTimerHandler()
}

def adjustTime() {
    def remoteOn = remoteSwitchs.find{it.currentValue("switch") == "on"}
    if (remoteOn == null) {
    	log.debug "Adjust time: remote off"
        state.remoteTurnedOff = true
    }
    else {
        if (state.warningSwitchTurnedOn) {
            log.debug "Warnig Switchs off"
            warningSwitch.off()
            state.warningSwitchTurnedOn = false
		}
        log.debug "Adjust time: remote on"
        //state.startTime = new Date().getTime()
        log.debug "Adjust time: state.startTime($state.startTime)"
        def diffTimeUsed = new Date().getTime() - state.startTime
        def diffTimeUsedSec = Math.round(diffTimeUsed / 1000)
        log.debug "Switch off: diffTimeUsedSec($diffTimeUsedSec)"
        state.offTimer = state.offTimer + diffTimeUsedSec
        log.debug "Switch off: offTimer($state.offTimer)"
        def turnOffTime = state.onMinutesSec - state.offTimer
        log.debug "Adjust time on: Waiting to turn off switch($turnOffTime)"
        def warningMinutes = warningMinutes ?: 5
        def secWarningMinutes = warningMinutes * 60
        def warningOnTime = turnOffTime - secWarningMinutes
        if (turnOffTime > secWarningMinutes) {
            log.debug "Adjust time on: Waiting to flash warning lights($warningOnTime)"
            runIn(warningOnTime, flashLights)
        }
        else {
            if (state.warningSwitchTurnedOn == false) {
                log.debug "Adjust time on: Flash warning lights now($warningOnTime)"
                flashLights()
			}                
        }
        runIn(turnOffTime, turnOffRemote)
    }
}

def remoteSwitchHandler(evt) {
    log.debug "Switch evt: ($evt.value)"
    if (evt.value == "on" && state.remoteTurnedOff) {
       	state.remoteTurnedOff = false
        log.debug "Switch on: offTimer($state.offTimer)"
        if (state.offTimer < state.onMinutesSec) {
            state.startTime = new Date().getTime()
            def turnOffTime = state.onMinutesSec - state.offTimer
            log.debug "Switch on: Waiting to turn off switch($turnOffTime)"
            def warningMinutes = warningMinutes ?: 5
            def secWarningMinutes = warningMinutes * 60
            def warningOnTime = turnOffTime - secWarningMinutes
            if (turnOffTime > secWarningMinutes) {
				log.debug "Switch on: Waiting to flash warning lights($warningOnTime)"
                runIn(warningOnTime, flashLights)
            }
            else {
                log.debug "Switch on: Flash warning lights now($warningOnTime)"
                flashLights()
            }
            runIn(turnOffTime, turnOffRemote)
		}
        else {
            def remoteOffAfterOff = remoteOffAfterOff ?: 20
            log.debug "Switch on: Waiting to turn of remote remoteOffAfterOff($remoteOffAfterOff)"
            runIn(remoteOffAfterOff, turnOffRemote)
        }
    }
    else {
        def remoteOn = remoteSwitchs.find{it.currentValue("switch") == "on"}
        if (remoteOn == null) {
            log.debug "Switch off: unschedule"
            unschedule(flashLights)
            unschedule(turnOffRemote)
            if (state.warningSwitchTurnedOn) {
            	log.debug "Switch off: warningSwitchTurnedOn"
                state.warningSwitchTurnedOn = false
                warningSwitch.off()
            }
            if (state.startTime && state.offTimer < state.onMinutesSec) {
                state.remoteTurnedOff = true
                def diffTimeUsed = new Date().getTime() - state.startTime
                def diffTimeUsedSec = Math.round(diffTimeUsed / 1000)
                log.debug "Switch off: diffTimeUsedSec($diffTimeUsedSec)"
                state.offTimer = state.offTimer + diffTimeUsedSec
                log.debug "Switch off: offTimer($state.offTimer)"
            }
		}
    }
}

def resetTimerHandler() {
    state.offTimer = 0
    state.startTime = null
    log.debug "New day: offTimer($state.offTimer)"
}

def turnOffRemote() {
    log.debug "Remote Switchs off"
    remoteSwitchs.off()
    state.remoteTurnedOff = true
    if (state.warningSwitchTurnedOn) {
        log.debug "Warnig Switchs off"
        warningSwitch.off()
        state.warningSwitchTurnedOn = false
	}
}

def flashLights() {
    def doFlash = true
    def onFor = onFor ?: 1000
    def offFor = offFor ?: 1000
    def numFlashes = numFlashes ?: 5
    
    
    log.debug "LAST ACTIVATED IS: ${state.lastActivated}"
    if (state.lastActivated) {
        def elapsed = now() - state.lastActivated
        def sequenceTime = (numFlashes + 1) * (onFor + offFor)
        doFlash = elapsed > sequenceTime
        log.debug "DO FLASH: $doFlash, ELAPSED: $elapsed, LAST ACTIVATED: ${state.lastActivated}"
    }
    
    if (warningSwitch.currentSwitch == "off"){
        log.debug "Warning switch is off"
        state.warningSwitchTurnedOn = true

         if (doFlash) {
            log.debug "FLASHING $numFlashes times"
            state.lastActivated = now()
            log.debug "LAST ACTIVATED SET TO: ${state.lastActivated}"
            def initialActionOn = warningSwitch.collect{it.currentSwitch != "on"}
            def delay = 0L
            numFlashes.times {
                log.trace "Switch off after $delay msec"
                warningSwitch.eachWithIndex {s, i ->
                    if (initialActionOn[i]) {
                        s.off(delay: delay)
                    }
                    else {
                        s.on(delay:delay)
                    }
                }
                delay += offFor
                
                log.trace "Switch on after  $delay msec"
                warningSwitch.eachWithIndex {s, i ->
                    if (initialActionOn[i]) {
                        s.on(delay: delay)
                    }
                    else {
                        s.off(delay:delay)
                    }
                }
                delay += onFor
            }
        }
    }
    else {
		if (doFlash) {
            log.debug "Warning switch is on"
            log.debug "FLASHING $numFlashes times"
            state.lastActivated = now()
            log.debug "LAST ACTIVATED SET TO: ${state.lastActivated}"
            def initialActionOn = warningSwitch.collect{it.currentSwitch != "on"}
            def delay = 0L
            numFlashes.times {
                log.trace "Switch on after  $delay msec"
                warningSwitch.eachWithIndex {s, i ->
                    if (initialActionOn[i]) {
                        s.on(delay: delay)
                    }
                    else {
                        s.off(delay:delay)
                    }
                }
                delay += onFor

                log.trace "Switch off after $delay msec"
                warningSwitch.eachWithIndex {s, i ->
                    if (initialActionOn[i]) {
                        s.off(delay: delay)
                    }
                    else {
                        s.on(delay:delay)
                    }
                }
                delay += offFor
            }
        }
    }
}