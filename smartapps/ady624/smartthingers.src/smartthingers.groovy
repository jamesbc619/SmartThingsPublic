/**
 *  SmartThingers
 *
 *  Copyright 2016 Adrian Caramaliu
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Version history
 *   5/06/2016 >>> v0.0.009.20160506 - Alpha test version - Partial support for Location Mode, Smart Home Monitor and Date & Time - work in progress
 *   5/06/2016 >>> v0.0.008.20160506 - Alpha test version - Minor improvements
 *   5/05/2016 >>> v0.0.007.20160505 - Alpha test version - All conditions implemented, simple triggers implemented. History-based triggers ("...stays...") not working yet
 *   5/05/2016 >>> v0.0.006.20160505 - Alpha test version - Simple conditions implemented. All "is" type conditions should work
 *   5/04/2016 >>> v0.0.005.20160504 - Alpha test version - added full list of standard capabilities, attributes and commands, improved condition UI
 *   5/02/2016 >>> v0.0.004.20160502 - Alpha test version - changed license from Apache to GPLv3
 *   5/02/2016 >>> v0.0.003.20160502 - Alpha test version - added mode - simple, latching or else-if
 *   5/02/2016 >>> v0.0.002.20160502 - Alpha test version - added latching rules
 *   4/29/2016 >>> v0.0.001.20160429 - Alpha test version - added condition naming
 *   4/29/2016 >>> v0.0.000.20160429 - Alpha test version
 *
 */
definition(
    name: "SmartThingers",
    namespace: "ady624",
    author: "Adrian Caramaliu",
    description: "Executes conditional actions",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page(name: "pageMain")
    page(name: "pageIf")
    page(name: "pageIfOther")
    page(name: "pageThen")
    page(name: "pageElse")
    page(name: "pageCondition")
    page(name: "pageConditionGroupL1")
    page(name: "pageConditionGroupL2")
    page(name: "pageConditionGroupL3")
    page(name: "pageConditionVsTrigger")
    page(name: "pageActionGroup")
}


/* constant definitions */
def capabilities() {
	return [
    	[ name: "accelerationSensor",				display: "Acceleration Sensor",				attribute: "acceleration",				commands: null,																		multiple: true,			],
    	[ name: "alarm",							display: "Alarm",							attribute: "alarm",						commands: ["off", "strobe", "siren", "both"],										multiple: true,			],
    	[ name: "doorControl",						display: "Automatic Door",					attribute: "door",						commands: ["open", "close"],														multiple: true,			],
    	[ name: "garageDoorControl",				display: "Automatic Garage Door",			attribute: "door",						commands: ["open", "close"],														multiple: true,			],
        [ name: "battery",							display: "Battery",							attribute: "battery",					commands: null,																		multiple: true,			],
    	[ name: "beacon",							display: "Beacon",							attribute: "presence",					commands: null,																		multiple: true,			],
    	[ name: "switchLevel",						display: "Bulb",							attribute: "level",						commands: ["setLevel"],															multiple: true,			],
        [ name: "button",							display: "Button",							attribute: "button",					commands: null,																		multiple: true,			],
    	[ name: "carbonDioxideMeasurement",			display: "Carbon Dioxide Measurement",		attribute: "carbonDioxide",				commands: null,																		multiple: true,			],
        [ name: "carbonMonoxideDetector",			display: "Carbon Monoxide Detector",		attribute: "carbonMonoxide",			commands: null,																		multiple: true,			],
    	[ name: "colorControl",						display: "Color Control",					attribute: "color",						commands: ["setColor", "setHue", "setSaturation"],									multiple: true,			],
        [ name: "colorTemperature",					display: "Color Temperature",				attribute: "colorTemperature",			commands: ["setColorTemperature"],													multiple: true,			],
    	[ name: "configure",						display: "Configure",						attribute: null,						commands: ["configure"],															multiple: true,			],
    	[ name: "consumable",						display: "Consumable",						attribute: "consumable",				commands: ["setConsumableStatus"],													multiple: true,			],
		[ name: "contactSensor",					display: "Contact Sensor",					attribute: "contact",					commands: null,																		multiple: true,			],
    	[ name: "dateAndTime",						display: "Date & Time",						attribute: "time",						commands: null, /* wish we could control time */									multiple: true,			, virtualDevice: [id: "dt"],		virtualDeviceName: "Date & Time"	],
    	[ name: "switchLevel",						display: "Dimmer",							attribute: "level",						commands: ["setLevel"],																multiple: true,			],
    	[ name: "energyMeter",						display: "Energy Meter",					attribute: "energy",					commands: null,																		multiple: true,			],
        [ name: "illuminanceMeasurement",			display: "Illuminance Measurement",			attribute: "illuminance",				commands: null,																		multiple: true,			],
        [ name: "imageCapture",						display: "Image Capture",					attribute: "image",						commands: ["take"],																	multiple: true,			],
    	[ name: "waterSensor",						display: "Leak Sensor",						attribute: "water",						commands: null,																		multiple: true,			],
        [ name: "locationMode",						display: "Location Mode",					attribute: "mode",						commands: ["setMode"],																multiple: false,		, virtualDevice: location	],
        [ name: "lock",								display: "Lock",							attribute: "lock",						commands: ["lock", "unlock"],														multiple: true,			],
    	[ name: "mediaController",					display: "Media Controller",				attribute: "currentActivity",			commands: ["startActivity", "getAllActivities", "getCurrentActivity"],				multiple: true,			],
    	[ name: "momentary",						display: "Momentary",						attribute: null,						commands: ["push"],																	multiple: true,			],
    	[ name: "motionSensor",						display: "Motion Sensor",					attribute: "motion",					commands: null,																		multiple: true,			],
    	[ name: "musicPlayer",						display: "Music Player",					attribute: "status",					commands: ["play", "pause", "stop", "nextTrack", "playTrack", "setLevel", "playText", "mute", "previousTrack", "unmute", "setTrack", "resumeTrack", "restoreTrack"],	multiple: true,			],
    	[ name: "notification",						display: "Notification",					attribute: null,						commands: ["deviceNotification"],													multiple: true,			],
    	[ name: "pHMeasurement",					display: "pH Measurement",					attribute: "pH",						commands: null,																		multiple: true,			],
    	[ name: "polling",							display: "Polling",							attribute: null,						commands: ["poll"],																	multiple: true,			],
        [ name: "powerMeter",						display: "Power Meter",						attribute: "power",						commands: null,																		multiple: true,			],
        [ name: "powerSource",						display: "Power Source",					attribute: "powerSource",				commands: null,																		multiple: true,			],
    	[ name: "presenceSensor",					display: "Presence Sensor",					attribute: "presence",					commands: null,																		multiple: true,			],
    	[ name: "refresh",							display: "Refresh",							attribute: null,						commands: ["refresh"],																multiple: true,			],
    	[ name: "relativeHumidityMeasurement",		display: "Relative Humidity Measurement",	attribute: "humidity",					commands: null,																		multiple: true,			],
    	[ name: "relaySwitch",						display: "Relay Switch",					attribute: "switch",					commands: ["on", "off"],															multiple: true,			],
    	[ name: "shockSensor",						display: "Shock Sensor",					attribute: "shock",						commands: null,																		multiple: true,			],
    	[ name: "signalStrength",					display: "Signal Strength",					attribute: "lqi",						commands: null,																		multiple: true,			],
    	[ name: "alarm",							display: "Siren",							attribute: "alarm",						commands: ["off", "strobe", "siren", "both"],										multiple: true,			],
    	[ name: "sleepSensor",						display: "Sleep Sensor",					attribute: "sleeping",					commands: null,																		multiple: true,			],
    	[ name: "smartHomeMonitor",					display: "Smart Home Monitor",				attribute: "alarmSystemStatus",			commands: null,																		multiple: true,			, virtualDevice: location,	virtualDeviceName: "Smart Home Monitor"	],
    	[ name: "smokeDetector",					display: "Smoke Detector",					attribute: "smoke",						commands: null,																		multiple: true,			],
        [ name: "soundSensor",						display: "Sound Sensor",					attribute: "sound",						commands: null,																		multiple: true,			],
    	[ name: "speechSynthesis",					display: "Speech Synthesis",				attribute: null,						commands: ["speak"],																multiple: true,			],
        [ name: "stepSensor",						display: "Step Sensor",						attribute: "steps",						commands: null,																		multiple: true,			],
    	[ name: "switch",							display: "Switch",							attribute: "switch",					commands: ["on", "off"],															multiple: true,			],
    	[ name: "switchLevel",						display: "Switch Level",					attribute: "level",						commands: ["setLevel"],																multiple: true,			],
        [ name: "soundPressureLevel",				display: "Sound Pressure Level",			attribute: "soundPressureLevel",		commands: null,																		multiple: true,			],
    	[ name: "consumable",						display: "Stock Management",				attribute: "consumable",				commands: null,																		multiple: true,			],
    	[ name: "tamperAlert",						display: "Tamper Alert",					attribute: "tamper",					commands: null,																		multiple: true,			],
    	[ name: "temperatureMeasurement",			display: "Temperature Measurement",			attribute: "temperature",				commands: null,																		multiple: true,			],
        [ name: "thermostat",						display: "Thermostat",						attribute: "temperature",				commands: ["setHeatingSetpoint", "setCoolingSetpoint", "off", "heat", "emergencyHeat", "cool", "setThermostatMode", "fanOn", "fanAuto", "fanCirculate", "setThermostatFanMode", "auto"],	multiple: true,			],
        [ name: "thermostatCoolingSetpoint",		display: "Thermostat Cooling Setpoint",		attribute: "coolingSetpoint",			commands: ["setCoolingSetpoint"],													multiple: true,			],
    	[ name: "thermostatFanMode",				display: "Thermostat Fan Mode",				attribute: "thermostatFanMode",			commands: ["fanOn", "fanAuto", "fanCirculate", "setThermostatFanMode"],				multiple: true,			],
    	[ name: "thermostatHeatingSetpoint",		display: "Thermostat Heating Setpoint",		attribute: "heatingSetpoint",			commands: ["setHeatingSetpoint"],													multiple: true,			],
    	[ name: "thermostatMode",					display: "Thermostat Mode",					attribute: "thermostatMode",			commands: ["off", "heat", "emergencyHeat", "cool", "auto", "setThermostatMode"],	multiple: true,			],
    	[ name: "thermostatOperatingState",			display: "Thermostat Operating State",		attribute: "thermostatOperatingState",	commands: null,																		multiple: true,			],
    	[ name: "thermostatSetpoint",				display: "Thermostat Setpoint",				attribute: "thermostatSetpoint",		commands: null,																		multiple: true,			],
    	[ name: "threeAxis",						display: "Three Axis Sensor",				attribute: "threeAxis",					commands: null,																		multiple: true,			],
    	[ name: "timedSession",						display: "Timed Session",					attribute: "sessionStatus",				commands: ["setTimeRemaining", "start", "stop", "pause", "cancel"],					multiple: true,			],
    	[ name: "tone",								display: "Tone Generator",					attribute: null,						commands: ["tone"],																	multiple: true,			],
    	[ name: "touchSensor",						display: "Touch Sensor",					attribute: "touch",						commands: null,																		multiple: true,			],
    	[ name: "valve",							display: "Valve",							attribute: "contact",					commands: ["open", "close"],														multiple: true,			],
    	[ name: "voltageMeasurement",				display: "Voltage Measurement",				attribute: "voltage",					commands: null,																		multiple: true,			],
    	[ name: "waterSensor",						display: "Water Sensor",					attribute: "water",						commands: null,																		multiple: true,			],
        [ name: "windowShade",						display: "Window Shade",					attribute: "windowShade",				commands: ["open", "close", "presetPosition"],										multiple: true,			],
    ]
}

def attributes() {
	def tempUnit = "°" + location.temperatureScale
	return [
    	[ name: "acceleration",				type: "enum",				range: null,			unit: null,		options: ["active", "inactive"],																			],
    	[ name: "alarm",					type: "enum",				range: null,			unit: null,		options: ["off", "strobe", "siren", "both"],																],
    	[ name: "battery",					type: "number",				range: "0..100",		unit: "%",		options: null,																								],
    	[ name: "beacon",					type: "enum",				range: null,			unit: null,		options: ["present", "not present"],																		],
        [ name: "button",					type: "enum",				range: null,			unit: null,		options: ["held", "pushed"],																				],
    	[ name: "carbonDioxide",			type: "decimal",			range: "0..*",			unit: null,		options: null,																								],
    	[ name: "carbonMonoxide",			type: "enum",				range: null,			unit: null,		options: ["clear", "detected", "tested"],																	],
    	[ name: "color",					type: "color",				range: null,			unit: null,		options: null,																								],
    	[ name: "hue",						type: "number",				range: "0..100",		unit: "%",		options: null,																								],
    	[ name: "saturation",				type: "number",				range: "0..100",		unit: "%",		options: null,																								],
    	[ name: "hex",						type: "hexcolor",			range: null,			unit: null,		options: null,																								],
    	[ name: "saturation",				type: "number",				range: "0..100",		unit: "%",		options: null,																								],
    	[ name: "level",					type: "number",				range: "0..100",		unit: "%",		options: null,																								],
    	[ name: "switch",					type: "enum",				range: null,			unit: null,		options: ["on", "off"],																						],
    	[ name: "colorTemperature",			type: "number",				range: "2000..7000",	unit: "°K",		options: null,																								],
    	[ name: "consumable",				type: "enum",				range: null,			unit: null,		options: ["missing", "good", "replace", "maintenance_required", "order"],									],
    	[ name: "contact",					type: "enum",				range: null,			unit: null,		options: ["open", "closed"],																				],
    	[ name: "door",						type: "enum",				range: null,			unit: null,		options: ["unknown", "closed", "open", "closing", "opening"],												],
    	[ name: "energy",					type: "decimal",			range: "0..*",			unit: "kWh",	options: null,																								],
    	[ name: "illuminance",				type: "number",				range: "0..*",			unit: "lux",	options: null,																								],
    	[ name: "image",					type: "image",				range: null,			unit: null,		options: null,																								],
    	[ name: "lock",						type: "enum",				range: null,			unit: null,		options: ["locked", "unlocked"],																			],
    	[ name: "activities",				type: "string",				range: null,			unit: null,		options: null,																								],
    	[ name: "currentActivity",			type: "string",				range: null,			unit: null,		options: null,																								],
    	[ name: "motion",					type: "enum",				range: null,			unit: null,		options: ["active", "inactive"],																			],
    	[ name: "status",					type: "string",				range: null,			unit: null,		options: null,																								],								
    	[ name: "mute",						type: "enum",				range: null,			unit: null,		options: ["muted", "unmuted"],																				],
    	[ name: "pH",						type: "decimal",			range: "0..14",			unit: null,		options: null,																								],
    	[ name: "power",					type: "decimal",			range: "0..*",			unit: "W",		options: null,																								],
    	[ name: "presence",					type: "enum",				range: null,			unit: null,		options: ["present", "not present"],																		],
    	[ name: "humidity",					type: "number",				range: "0..100",		unit: "%",		options: null,																								],
        [ name: "shock",					type: "enum",				range: null,			unit: null,		options: ["detected", "clear"],																				],
    	[ name: "lqi",						type: "number",				range: "0..255",		unit: null,		options: null,																								],
    	[ name: "rssi",						type: "number",				range: "0..100",		unit: "%",		options: null,																								],
    	[ name: "sleeping",					type: "enum",				range: null,			unit: null,		options: ["sleeping", "not sleeping"],																		],
    	[ name: "smoke",					type: "enum",				range: null,			unit: null,		options: ["clear", "detected", "tested"],																	],
    	[ name: "sound",					type: "enum",				range: null,			unit: null,		options: ["detected", "not detected"],																		],
        [ name: "steps",					type: "number",				range: "0..*",			unit: null,		options: null,																								],
    	[ name: "goal",						type: "number",				range: "0..*",			unit: null,		options: null,																								],
    	[ name: "soundPressureLevel",		type: "number",				range: "0..*",			unit: null,		options: null,																								],
    	[ name: "tamper",					type: "enum",				range: null,			unit: null,		options: ["clear", "detected"],																				],
    	[ name: "temperature",				type: "decimal",			range: "*..*",			unit: tempUnit,	options: null,																								],
    	[ name: "thermostatMode",			type: "enum",				range: null,			unit: null,		options: ["off", "auto", "cool", "heat", "emergency heat"],													],
    	[ name: "thermostatFanMode",		type: "enum",				range: null,			unit: null,		options: ["auto", "on", "circulate"],																		],
    	[ name: "thermostatOperatingState",	type: "enum",				range: null,			unit: null,		options: ["idle", "pending cool", "cooling", "pending heat", "heating", "fan only", "vent economizer"],		],
        [ name: "coolingSetpoint",			type: "decimal",			range: "-127..127",		unit: tempUnit,	options: null,																								],
        [ name: "heatingSetpoint",			type: "decimal",			range: "-127..127",		unit: tempUnit,	options: null,																								],
        [ name: "thermostatSetpoint",		type: "decimal",			range: "-127..127",		unit: tempUnit,	options: null,																								],
        [ name: "sessionStatus",			type: "enum",				range: null,			unit: null,		options: ["paused", "stopped", "running", "canceled"],														],
    	[ name: "threeAxis",				type: "threeAxis",			range: "0..1024",		unit: null,		options: null,																								],
    	[ name: "touch",					type: "enum",				range: null,			unit: null,		options: ["touched"],																						],
    	[ name: "valve",					type: "enum",				range: null,			unit: null,		options: ["open", "closed"],																				],
        [ name: "voltage",					type: "decimal",			range: "*..*",			unit: "V",		options: null,																								],
    	[ name: "water",					type: "enum",				range: null,			unit: null,		options: ["dry", "wet"],																					],
    	[ name: "windowShade",				type: "enum",				range: null,			unit: null,		options: ["unknown", "open", "closed", "opening", "closing", "partially open"],								],
    	[ name: "mode",						type: "mode",				range: null,			unit: null,		options: location.modes,																					],
    	[ name: "alarmSystemStatus",		type: "enum",				range: null,			unit: null,		options: ["Disarmed", "Armed/Stay", "Armed/Away"],															],
    	[ name: "time",						type: "time",				range: null,			unit: null,		options: null,																								],
    ]
}

def comparisons() {
	def optionsEnum = [
        [ condition: "is", trigger: "changes to", parameters: 1, timed: false],
        [ condition: "is not", trigger: "changes away from", parameters: 1, timed: false],
        [ condition: "is one of", trigger: "changes to one of", parameters: 1, timed: false, multiple: true, minOptions: 3],
        [ condition: "was", trigger: "stays", parameters: 1, timed: true],
        [ condition: "was not", parameters: 1, timed: true],
        [ trigger: "changes", parameters: 0, timed: false],
    ]
    def optionsNumber = [
        [ condition: "is equal to", trigger: "changes to", parameters: 1, timed: false],
        [ condition: "is not equal to", trigger: "changes away from", parameters: 1, timed: false],
        [ condition: "is less than", trigger: "drops below", parameters: 1, timed: false],
        [ condition: "is less than or equal to", trigger: "drops to or below", parameters: 1, timed: false],
        [ condition: "is greater than", trigger: "raises above", parameters: 1, timed: false],
        [ condition: "is greater than or equal to", trigger: "raises to or above", parameters: 1, timed: false],
        [ condition: "is inside range", trigger: "enters range", parameters: 2, timed: false],
        [ condition: "is outside of range", trigger: "exits range", parameters: 2, timed: false],
        [ condition: "is even", trigger: "changes to an even value", parameters: 0, timed: false],
        [ condition: "is odd", trigger: "changes to an odd value", parameters: 0, timed: false],
        [ condition: "was equal to", trigger: "stays equal to", parameters: 1, timed: true],
        [ condition: "was not equal to", trigger: "stays not equal to", parameters: 1, timed: true],
        [ condition: "was less than", trigger: "stays less than", parameters: 1, timed: true],
        [ condition: "was less than or equal to", trigger: "stays less than or equal to", parameters: 1, timed: true],
        [ condition: "was greater than", trigger: "stays greater than", parameters: 1, timed: true],
        [ condition: "was greater than or equal to", trigger: "stays greater than or equal to", parameters: 1, timed: true],
        [ condition: "was inside range",trigger: "stays inside range",  parameters: 2, timed: true],
        [ condition: "was outside range", trigger: "stays outside range", parameters: 2, timed: true],
        [ condition: "was even", trigger: "stays even", parameters: 0, timed: true],
        [ condition: "was odd", trigger: "stays odd", parameters: 0, timed: true],
        [ trigger: "changes", parameters: 0, timed: false],
    ]
    def optionsTime = [
        [ trigger: "reaches", parameters: 1],
        [ condition: "is before", parameters: 1],
        [ condition: "is after", parameters: 1],
        [ condition: "is between", parameters: 2],
        [ condition: "is not between", parameters: 2],
	]
	return [
    	[ type: "string",				options: optionsEnum,	],
    	[ type: "enum",					options: optionsEnum,	],
    	[ type: "mode",					options: optionsEnum,	],
    	[ type: "alarmSystemStatus",	options: optionsEnum,	],
    	[ type: "number",				options: optionsNumber,	],
    	[ type: "decimal",				options: optionsNumber	],
    	[ type: "time",					options: optionsTime,	],        
    ]
}

def timeComparisonOptionValues(trigger) {
   	return ["custom time", "midnight", "around midnight", "sunrise", "around sunrise", "noon", "around noon", "sunset", "around sunset"] + (trigger ? ["every number of minutes", "every number of hours"] : [])
}

def timeOptions() {
	def result = ["1 minute"]
    for (def i =2; i <= 60; i++) {
    	result.push("$i minutes")
    }
	return result
}

def timeRepeatOptions() {
	return ["every day", "every number of days"]
}

def timeDayOfMonthOptions() {
	def result = ["on the first...", "on the second...", "on the third...", "on the fourth...", "on the fifth...", "on the last...", "on the second-last..."]
    for (def i =1; i <= 31; i++) {
    	result.push("on day $i")
    }
	return result + ["on the last day"]
}

def timeDayOfWeekOptions() {
	return ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
}

def timeMonthOfYearOptions() {
	return ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
}

def timeToMinutes(time) {
	def value = time.replace(" minutes", "").replace(" minute", "")
    if (value.isInteger()) {
    	return value.toInteger()
    }
    return 0
}

def triggerPrefix() {
    return "● "
}

def conditionPrefix() {
	return "◦ "
}

def customAttributePrefix() {
	return "⌂ "
}


def pageMain() {
	configApp()
    cleanUpConditions(true)
	dynamicPage(name: "pageMain", title: "SmartThinger Application", uninstall: true, install: true) {
    	section() {
        	label title: "Name", required: true, state: null
        	input "description", "string", title: "Description", required: false, state: null, defaultValue: "test"
            input "mode", "enum", title: "SmartThinger Mode", required: true, options: ["Simple", "Latching", "Else-If"], defaultValue: "Simple", submitOnChange: true
            switch (settings.mode) {
            	case "Latching":
            		paragraph "A latching SmartThinger - also known as a bi-stable SmartThinger - uses one set of conditions to achieve a 'true' state and a second set of conditions to revert back to its 'false' state"
                    break
            	case "Else-If":
            		paragraph "An Else-If SmartThinger executes a set of actions if an initial condition set evaluates to true, otherwise executes a second set of actions if a second condition set evaluates to true"
                    break
            }
        }
        
        section() {
			href "pageSimulate", title: "Simulate", description: "Allows you to test the actions manually", state: complete
        }
        
        section() {
			href "pageIf", title: "If...", description: (state.config.app.conditions.children.size() ? "Tap here to edit the main If group or tap on any individual conditions below to edit them directly" : "Tap to select conditions")
            buildIfContent()
        }
        
        section() {
			href "pageActionGroup", params:["conditionId": -1], title: "Then...", description: "Choose what should happen then", state: null, submitOnChange: false
        }
        
        if (settings.mode == "Latching") {
            section() {
                href "pageIfOther", title: "But if...", description: (state.config.app.otherConditions.children.size() ? "" : "Tap to select conditions")
                buildIfOtherContent()
            }
        }

        if (settings.mode == "Else-If") {
            section() {
                href "pageIfOther", title: "Else if...", description: (state.config.app.otherConditions.children.size() ? "" : "Tap to select conditions")
                buildIfOtherContent()
            }
        }

        section() {
			href "pageActionGroup", params:["conditionId": -1], title: ((settings.mode == "Latching") || (settings.mode == "Else-If") ? "Then..." : "Else..."), description: "Choose what should happen otherwise", state: null, submitOnChange: false
		}
        
		section(title: "Advanced options", hideable: true, hidden: true) {
        	input "expert", "bool", title: "Expert Mode", defaultValue: false, submitOnChange: true
		}
        
        section() {
        	paragraph "Triggers in use: ${getTriggerCount(state.config.app)}"
        }
	}
}


def pageIf(params) {
    cleanUpConditions(false)
	def condition = state.config.app.conditions
    dynamicPage(name: "pageIf", title: "Main Condition Group", uninstall: false, install: false) {
    	getConditionGroupPageContent(params, condition)
    }
}

def pageIfOther(params) {
    cleanUpConditions(false)
	def condition = state.config.app.otherConditions
    dynamicPage(name: "pageIfOther", title: "Main Group", uninstall: false, install: false) {
    	getConditionGroupPageContent(params, condition)
    }
}
def pageConditionGroupL1(params) {
	pageConditionGroup(params, 1)
}

def pageConditionGroupL2(params) {
	pageConditionGroup(params, 2)
}

def pageConditionGroupL3(params) {
	pageConditionGroup(params, 3)
}

//helper function for condition group paging
def pageConditionGroup(params, level) {
    cleanUpConditions(false)
    def condition = null    
    if (params?.command == "add") {
        condition = createCondition(params?.parentConditionId, true)
    } else {
		condition = getCondition(params?.conditionId ? params?.conditionId : state.config["conditionGroupIdL$level"])
    }
    if (condition) {
    	def id = condition.id
        state.config["conditionGroupIdL$level"] = id
        def pid = condition.parentId
    	dynamicPage(name: "pageConditionGroupL$level", title: "Group $id (level $level)", uninstall: false, install: false) {
	    	getConditionGroupPageContent(params, condition)
	    }
    }
}

def getConditionGroupPageContent(params, condition) {	
	if (condition) {
        def id = condition.id
        def pid = condition.parentId ? condition.parentId : condition.id
        def nextLevel = (condition.level ? condition.level : 0) + 1
        def cnt = 0
        section() {
        	if (settings["condNegate$id"]) {
                paragraph "NOT ("
            }
            for (c in condition.children) {
            	if (cnt > 0) {
                	if (cnt == 1) {
			           	input "condGrouping$id", "enum", title: "", description: "Choose the logical operation to be applied between all conditions in this group", options: ["AND", "OR", "XOR"], defaultValue: "AND", required: true, submitOnChange: true
                    } else {
                		paragraph settings["condGrouping$id"], state: "complete"
                    }
                }
                def cid = c?.id
		        def conditionType = (c.trg ? "trigger" : "condition")
                if (c.children != null) {
                    href "pageConditionGroupL${nextLevel}", /*image: "https://raw.githubusercontent.com/ady624/SmartThingers/master/resources/images/folder.png",*/ params: ["conditionId": cid], title: "Group #$cid", description: getConditionDescription(cid), state: "complete", required: false, submitOnChange: false
                } else {
                    href "pageCondition", /*image: "https://raw.githubusercontent.com/ady624/SmartThingers/master/resources/images/${conditionType}.png",*/ params: ["conditionId": cid], title: (c.trg ? "Trigger" : "Condition") + " #$cid", description: getConditionDescription(cid), state: "complete", required: false, submitOnChange: false
                }
                cnt++
            }
        	if (settings["condNegate$id"]) {
                paragraph ")", state: "complete"
            }
        }
        section() {
            href "pageCondition", params:["command": "add", "parentConditionId": id], title: "Add a condition", description: "A condition watches the state of one or multiple similar devices", state: "complete", submitOnChange: true
            //href "pageCondition", params:["command": "add", "parentConditionId": id], title: "Add a trigger", description: "A trigger is a specialised condition that watches for state changes of one or multiple similar devices", state: "complete", submitOnChange: true
            if (nextLevel <= 3) {
            	href "pageConditionGroupL${nextLevel}", params:["command": "add", "parentConditionId": id], title: "Add a group", description: "A group is a container for multiple conditions and/or triggers, allowing for more complex logical operations, such as evaluating [A AND (B OR C)]", state: "complete", submitOnChange: true
            }
        }
        
        section(title: (condition.trg ? "Trigger" : "Condition") + " Overview") {
            paragraph getConditionDescription(id)
            href "pageActionGroup", params:["conditionId": id], title: "Individual actions", description: "Tap to set individual actions for this condition", state: complete, submitOnChange: true
        }       

//        section() {
//            href "pageConditionVsTrigger", title: "Uh oh, which should I use?", description: "Find out when to use a condition and when to use a trigger", state: complete, submitOnChange: false
//        }

		section(title: "Advanced options", hideable: true, hidden: true) {
           	//input "condGrouping$id", "enum", title: "Grouping Method", description: "Choose the logical operation to be applied between all conditions in this group", options: ["AND", "OR", "XOR"], defaultValue: "AND", required: true, submitOnChange: true
           	input "condNegate$id", "bool", title: "Negate Group", description: "Apply a logical NOT to the whole group", defaultValue: false, required: true, submitOnChange: true
        	if (id) {
            	//add a hidden parameter for any condition other than the main container - this is to maintain the correct grouping
    	        input "condParent$id", "number", title: "Parent ID", range: "$pid..$pid", defaultValue: pid
        	}
        }
    }
}

def pageCondition(params) {
	//get the current edited condition
    def condition = null
    if (params?.command == "add") {
        condition = createCondition(params?.parentConditionId, false)
    } else {   	
		condition = getCondition(params?.conditionId ? params?.conditionId : state.config.conditionId)
    }
    if (condition) {
    	updateCondition(condition)
    	def id = condition.id
        state.config.conditionId = id
        def pid = condition.parentId
    	dynamicPage(name: "pageCondition", title: (condition.trg? "Trigger" : "Condition") + " #$id", uninstall: false, install: false) {
			section() {
            	if (!settings["condDevices$id"] || (settings["condDevices$id"].size() == 0)) {
                	//only display capability selection if no devices already selected
	            	input "condCap$id", "enum", title: "Capability", options: listCapabilities(true, false), submitOnChange: true
                }
                if (settings["condCap$id"]) {
                	def capability = getCapabilityByDisplay(settings["condCap$id"])
                    if (capability) {
                    	if (capability.virtualDevice) {
                        	def attribute = capability.attribute
                            def attr = getAttributeByName(attribute)
                            log.trace attr
                            if (attribute == "time") {
                            	//Date & Time support
                                def comparison = cleanUpComparison(settings["condComp$id"])
                                log.trace comparison
                                input "condComp$id", "enum", title: "Comparison", options: listComparisonOptions(attribute, true), required: true, multiple: false, submitOnChange: true
                                if (comparison) {
									def comp = getComparisonOption(attribute, comparison)
                                    if (attr && comp) {
                                        //we have a valid comparison object
                                        def trigger = (comp.trigger == comparison)
                                        def recurring = false
                                        for (def i = 1; i <= comp.parameters; i++) {
                                            input "condValue$id#$i", "enum", title: (comp.parameters == 1 ? "Value" : (i == 1 ? "Time" : "And")), options: timeComparisonOptionValues(trigger), required: true, multiple: false, submitOnChange: true
                                            def value = settings["condValue$id#$i"]
                                            if (value) {
                                                if (value.contains("custom")) {
                                                    //using a time offset
                                                    input "condTime$id#$i", "time", title: "Custom time", required: true, multiple: false, submitOnChange: true
                                                }
                                                if (value.contains("around")) {
                                                    //using a time offset
                                                    input "condOffset$id#$i", "number", title: "Offset (minutes)", range: "-180..180", required: true, multiple: false, defaultValue: 0, submitOnChange: true
                                                }
                                                if (value.contains("number")) {
                                                    //using a time offset
                                                    input "condEvery$id", "number", title: value.replace("every n", "N"), range: "1..*", required: true, multiple: false, defaultValue: 1, submitOnChange: true
                                                    recurring = true
                                                    break
                                                }
                                            }
                                        }
                                        if (trigger && !recurring) {
											input "condRepeat$id", "enum", title: "Repeat", options: ["every day", "every number of days", "every month", "every number of months", "every year", "every number of years"], required: true, multiple: false, defaultValue: "every day", submitOnChange: true
                                            def repeat = settings["condRepeat$id"]
                                            if (repeat) {
                                            	def incremental = repeat.contains("number")
                                                if (incremental) {
                                                    //using a time offset
                                                    input "condRepeatEvery$id", "number", title: repeat.replace("every n", "N"), range: "2..*", required: true, multiple: false, defaultValue: 2, submitOnChange: true
                                                    recurring = true
                                                }
                                                def monthOfYear = null
                                                if (repeat.contains("month") || repeat.contains("year")) {
                                                	//oh-oh, monthly
                                                    input "condRepeatDay$id", "enum", title: "On", options: timeDayOfMonthOptions(), required: true, multiple: false, submitOnChange: true
                                                    def dayOfMonth = settings["condRepeatDay$id"]
                                                    def certainDay = false
                                                    def dayOfWeek = null
                                                    if (dayOfMonth) {
                                                    	if (!dayOfMonth.contains("day")) {
                                                        	certainDay = true
															input "condDOWOM$id", "enum", title: "Day", options: timeDayOfWeekOptions(), required: true, multiple: false, submitOnChange: true
                                                            dayOfWeek = settings["condDOWOM$id"]
                                                        }
                                                    }
													if (repeat.contains("year") && (dayOfMonth) && (!certainDay || dayOfWeek)) {
                                                		//oh-oh, yearly
                                                    	input "condRepeatMonth$id", "enum", title: "Of", options: timeMonthOfYearOptions(), required: true, multiple: false, submitOnChange: true
                                                    	monthOfYear = settings["condRepeatMonth$id"]
                                                	}
                                                }
                                            }
                                        	
                                        }
                                    }
                                }
                            
                            } else {
                            	//Location Mode, Smart Home Monitor support
                                def comparison = cleanUpComparison(settings["condComp$id"])
                                input "condComp$id", "enum", title: "Comparison", options: listComparisonOptions(attribute, true), required: true, multiple: false, submitOnChange: true                                
                                if (comparison) {                                	
                                    //Value
                                    def comp = getComparisonOption(attribute, comparison)
                                    if (attr && comp) {
                                        if (comp.parameters >= 1) {
                                            input "condValue$id#1", attr.type, title: "Value", options: attr.options, range: attr.range, required: true, multiple: comp.multiple, submitOnChange: true
                                        }
                                    }
                                }
							}
                        } else {                        
                        	//physical device support
                            def devices = settings["condDevices$id"]
                            input "condDevices$id", "capability.${capability.name}", title: "${capability.display} list", required: false, state: (devices ? "complete" : null), multiple: capability.multiple, submitOnChange: true
                            if (devices && devices.size()) {
                                if (devices.size() > 1) {
                                    input "condMode$id", "enum", title: "Evaluation mode", options: ["Any", "All"], required: true, multiple: false, defaultValue: "All", submitOnChange: true
                                }
                                def evalMode = settings["condMode$id"] == "All" ? "All" : "Any"

                                //Attribute
                                def attribute = cleanUpAttribute(settings["condAttr$id"])
                                if (attribute == null) {
                                    attribute = capability.attribute
                                }
                                //display the Attribute only in expert mode or in basic mode if it differs from the default capability attribute
                                if ((attribute != capability.attribute) || settings["expert"]) {
                                    input "condAttr$id", "enum", title: "Attribute", options: listCommonDeviceAttributes(devices), required: true, multiple: false, defaultValue: capability.attribute, submitOnChange: true
                                }
                                if (attribute) {                              
                                    //Condition
                                    def attr = getAttributeByName(attribute)
                                    def comparison = cleanUpComparison(settings["condComp$id"])
                                    input "condComp$id", "enum", title: "Comparison", options: listComparisonOptions(attribute, true), required: true, multiple: false, submitOnChange: true                                
                                    if (comparison) {                                	
                                        //Value
                                        def comp = getComparisonOption(attribute, comparison)
                                        if (attr && comp) {
                                            if (comp.parameters >= 1) {
                                                def value1 = settings["condValue$id#1"]
                                                def device1 = settings["condDev$id#1"]
                                                if (device1 == null) {
                                                    input "condValue$id#1", attr.type, title: (comp.parameters == 1 ? "Value" : "From value"), options: attr.options, range: attr.range, required: true, multiple: comp.multiple, submitOnChange: true
                                                }
                                                if (value1 == null) {
                                                    input "condDev$id#1", "capability.${capability.name}", title: (device1 == null ? "... or choose a device to compare ..." : (comp.parameters == 1 ? "Device" : "From")), required: true, multiple: comp.multiple, submitOnChange: true
                                                }
                                            }
                                            if (comp.parameters >= 2) {
                                                def value2 = settings["condValue$id#2"]
                                                def device2 = settings["condDev$id#2"]
                                                if (device2 == null) {
                                                    input "condValue$id#2", attr.type, title: "Through value", options: attr.options, range: attr.range, required: true, multiple: false, submitOnChange: true
                                                }
                                                if (value2 == null) {
                                                    input "condDev$id#2", "capability.${capability.name}", title: (device2 == null ? "... or choose a device to compare ..." : "Through"), required: true, multiple: false, submitOnChange: true
                                                }
                                            }

                                            if (comp.timed) {
                                                input "condFor$id", "enum", title: "Time restriction", options: ["for at least", "for less than"], required: true, multiple: false, submitOnChange: true
                                                input "condTime$id", "enum", title: "Interval", options: timeOptions(), required: true, multiple: false, submitOnChange: true
                                            }
                                        }
                                    }
                                    //input "condDevice$id", "", title: title, required: true, multiple: false, submitOnChange: true
                                }
                            }
                        }
                    }
                }
			}

			section(title: (condition.trg ? "Trigger" : "Condition") + " Overview") {
                paragraph getConditionDescription(id)
	            href "pageActionGroup", params:["conditionId": id], title: "Individual actions", description: "Tap to set individual actions for this condition", state: complete, submitOnChange: true
			}

			section() {
				paragraph "NOTE: To delete this condition, simply remove all devices from the list above and tap Done"
            }
            
            section(title: "Advanced options", hideable: true, hidden: true) {
                input "condParent$id", "number", title: "Parent ID", range: "$pid..$pid", defaultValue: pid
			}
	    }
    }
}


def pageConditionVsTrigger() {
	dynamicPage(name: "pageConditionVsTrigger", title: "Conditions versus Trigers", uninstall: false, install: false) {
    	section() {
			paragraph "All SmartThingers are event-driven. This means that an action is taken whenever something happens while the SmartThinger is watching over. To do so, the SmartThinger subscribes to events from all the devices you use while building your 'If...' and - in case of latching SmartThingers - your 'But if...' statements as well. Since a SmartThinger subscribes to multiple device events, it is evaluated every time such an event occurs. Depending on your conditions, a device event may not necessarily make any change to the evaluated state of the SmartThinger (think OR), but the SmartThinger is evaluated either way, making it possible to execute actions even if the SmartThinger's status didn't change. More about this under the 'Then...' or 'Else...' sections of the SmartThinger." 
	        paragraph "Events tell SmartThingers something has changed. Depending on the logic you are trying to implement, sometimes you need to check that the state of a device is within a certain range, and sometimes you need to react to a device state reaching a certain value, list or range.\n\nLet's start with an example. Say you have a temperature sensor and you want to monitor its temperature. You want to be alerted if the temperature is over 100°F. Now, assume the temperature starts at 99°F and increases steadily at a rate of one degree Fahrenheit per minute.", title: "State vs. State Change"
            paragraph "If you use a condition, the SmartThinger will be evaluated every one minute, as the temperature changes. The first evaluation will result in a false condition as the temperature reaches 100°F. Remember, our condition is for the temperature to be OVER 100°F. The next minute, your temperature is reported at 101°F which will cause the SmartThinger to evaluate true this time. Your 'Then...' actions will now have a chance at execution. The next minute, as the temperature reaches 102°F, the SmartThinger will again evaluate true and proceed to executing your 'Then...' actions. This will happen for as long as the temperature remains over 100°F and will possibly execute your actions every time a new temperature is read that matches that condition. You could use this to pass the information along to another service (think IFTTT) or display it on some sort of screen. But not for turning on a thermostat - you don't neet to turn the thermostat on every one minute, it's very likely already on from your last execution.", title: "Using a Condition"
            paragraph "If you use a trigger, the SmartThinger will now be on the lookout for a certain state change that 'triggers' our evaluation to become true. You will no longer look for a temperature over 100°F, but instead you will be looking for when the temperature exceeds 100°F. This means your actions will only be executed when the temperature actually transitioned from below or equal to 100°F to over 100°F. This means your actions will only execute once and for the SmartThinger to fire your actions again, the temperature would have to first drop at or below 100°F and then raise again to exceed your set threshold of 100°F. Now, this you could use to control a thermostat, right?", title: "Using a Trigger"
		}
    }
}

def pageActionGroup() {
	def conditionId = params?.conditionId
	def value = (conditionId < 0) && (settings.mode == "Simple") ? "false" : "true"
	dynamicPage(name: "pageActionGroup", title: "Actions", uninstall: false, install: false) {
    	section() {
        	paragraph "Add actions below to be executed once, whenever the evaluation of your condition(s) changes to '$value'", title: "Do..."
            href "pageAction", params:["command": "add", "conditionId": id, "branch": "do"], title: "Add an action", description: "Actions allow control of various devices in your ecosystem", required: true, state: "complete", submitOnChange: true
        }
    	section() {
        	paragraph "Add actions below to be executed every time the evaluation of your condition(s) is '$value'", title: "Do While..."
            paragraph "CAUTION: Only use this section if you know what you are doing. Because evaluations may happen whenever various attributes of various devices involved in your condition(s) change, actions in this list may be executed very often and may therefore yield unexpected results\n\nYE BE WARNED!", required: true, state: null
            href "pageAction", params:["command": "add", "conditionId": id, "branch": "doWhile"], title: "Add an action", description: "Actions allow control of various devices in your ecosystem", required: true, state: "complete", submitOnChange: true
        }
        
    }
}

/* prepare configuration version of app */
def configApp() {
	//TODO: rebuild (object-oriented) app object from settings
	//state.config = null
	if (!state.config) {
    	//initiate config app, since we have no running version yet (not yet installed)
        state.config = [:]
        state.config.conditionId = 0
    	state.config.app = [:]
    	state.config.app.description = "cough"
        //create the root condition
    	state.config.app.conditions = createCondition(true)
        state.config.app.conditions.id = 0
    	state.config.app.otherConditions = createCondition(true)
        state.config.app.otherConditions.id = -1
    	state.config.app.actions = [:]
    	state.config.app.actions.ot = []
    	state.config.app.actions.wt = []
    	state.config.app.actions.of = []
    	state.config.app.actions.wf = []
    }
}

//returns a list of all available capabilities
def listCapabilities(requireAttributes, requireCommands) {
    def result = []
    for (capability in capabilities()) {
    	if ((requireAttributes && capability.attribute) || (requireCommands && capability.commands) || !(requireAttributes || requireCommands)) {
	    	result.push(capability.display)
		}
    }
    return result
}

//returns a list of all available attributes
def listAttributes() {
    def result = []
    for (attribute in attributes()) {
    	result.push(attribute.name)
    }
    return result.sort()
}

//returns a list of possible comparison options for a selected attribute
def listComparisonOptions(attributeName, allowTriggers) {
    def conditions = []
    def triggers = []
    def attribute = getAttributeByName(attributeName)
    if (attribute) {
    	def optionCount = attribute.options ? attribute.options.size() : 0
        def attributeType = attribute.type
        for (comparison in comparisons()) {
            if (comparison.type == attributeType) {
                for (option in comparison.options) {
                    if (option.condition && (!option.minOptions || option.minOptions <= optionCount)) {
                        conditions.push(conditionPrefix() + option.condition)
                    }
                    if (allowTriggers && option.trigger && (!option.minOptions || option.minOptions <= optionCount)) {
                        triggers.push(triggerPrefix() + option.trigger)
                    }
                }
            }
        }
    }
    return conditions.sort() + triggers.sort()
}

//returns the comparison option object for the given attribute and selected comparison
def getComparisonOption(attributeName, comparisonOption) {	
    def attribute = getAttributeByName(attributeName)
    if (attribute) {
		def attributeType = attribute.type
        for (comparison in comparisons()) {
            if (comparison.type == attributeType) {
                for (option in comparison.options) {
                    if (option.condition == comparisonOption) {
                        return option
                    }
                    if (option.trigger == comparisonOption) {
                    	return option
                    }
                }
            }
        }
    }
    return null	
}

//returns true if the comparisonOption selected for the given attribute is a trigger-type condition
def isComparisonOptionTrigger(attributeName, comparisonOption) {
    def attribute = getAttributeByName(attributeName)
    if (attribute) {
		def attributeType = attribute.type
        for (comparison in comparisons()) {
            if (comparison.type == attributeType) {
                for (option in comparison.options) {
                    if (option.condition == comparisonOption) {
                        return false
                    }
                    if (option.trigger == comparisonOption) {
                    	return true
                    }
                }
            }
        }
    }
    return null	
}

//returns the list of attributes that exist for all devices in the provided list
def listCommonDeviceAttributes(devices) {
	def list = [:]
    def customList = [:]
    //build the list of standard attributes
	for (attribute in attributes()) {
    	list[attribute.name] = 0
    }
	//get supported attributes
    for (device in devices) {
    	def attrs = device.supportedAttributes
        for (attr in attrs) {        	
        	if (list.containsKey(attr.name)) {
            	//if attribute exists in standard list, increment its usage count
	       		list[attr.name] = list[attr.name] + 1
            } else {
            	//otherwise increment the usage count in the custom list
	       		customList[attr.name] = customList[attr.name] ? customList[attr.name] + 1 : 1
            }
        }
    }
    def result = []
    //get all common attributes from the standard list
    for (item in list) {
    	//ZWave Lock reports lock twice - others may do the same, so let's allow multiple instances
    	if (item.value >= devices.size()) {
        	result.push(item.key)
        }
    }
    //get all common attributes from the custom list
    for (item in customList) {
    	//ZWave Lock reports lock twice - others may do the same, so let's allow multiple instances
    	if (item.value >= devices.size()) {
        	result.push(customAttributePrefix() + item.key)
        }
    }
    //return the sorted list
    return result.sort()
}

def getCapabilityByName(name) {
    for (capability in capabilities()) {
    	if (capability.name == name) {
        	return capability
        }
    }
    return null
}

def getCapabilityByDisplay(display) {
    for (capability in capabilities()) {
    	if (capability.display == display) {
        	return capability
        }
    }
    return null
}

def getAttributeByName(name) {
    for (attribute in attributes()) {
    	if (attribute.name == name) {
        	return attribute
        }
    }
    return [ name: name, type: "string", range: null, unit: null, options: null]
}

def buildDeviceNameList(devices, suffix) {
	def cnt = 1
    def result = ""
	for (device in devices) {
        result += device?.label + (cnt < devices.size() ? (cnt == devices.size() - 1 ? " $suffix " : ", ") : "")
        cnt++
    }
    return result;
}


def buildIfContent() {
	buildIfContent(state.config.app.conditions.id, 0)
}

def buildIfOtherContent() {
	buildIfContent(state.config.app.otherConditions.id, 0)
}

def buildIfContent(id, level) {
	def condition = getCondition(id)
    if (!condition) {
    	return null
    }
    def conditionGroup = (condition.children != null)
    def conditionType = (condition.trg ? "trigger" : "condition")
    level = (level ? level : 0)
    def pre = ""
    def preNot = ""
    def tab = ""
    def aft = ""
    switch (level) {
    	case 1:
        	pre = " ┌ ("
        	preNot = " ┌ NOT ("
        	tab = " │   "
        	aft = " └ )"
	        break;
        case 2:
    	    pre = " │ ┌ ["
    	    preNot = " │ ┌ NOT ["
        	tab = " │ │   "
       		aft = " │ └ ]"
        	break;
        case 3:
	        pre = " │ │ ┌ <"
	        preNot = " │ │ ┌ NOT {"
        	tab = " │ │ │   "
    	    aft = " │ │ └ >"
        	break;
    }
	if (!conditionGroup) {
		href "pageCondition", /*image: "https://raw.githubusercontent.com/ady624/SmartThingers/master/resources/images/${conditionType}.png",*/ params: ["conditionId": id], title: "", description: tab + getConditionDescription(id).trim(), state: "complete", required: false, submitOnChange: false
    } else {
    
        def grouping = settings["condGrouping$id"]
        def negate = settings["condNegate$id"]
    
    	if (pre) {
			href "pageConditionGroupL${level}", /*image: "https://raw.githubusercontent.com/ady624/SmartThingers/master/resources/images/folder.png",*/ params: ["conditionId": id], title: "", description: (negate? preNot : pre), state: "complete", required: true, submitOnChange: false
        }
        
        def cnt = 0
        for (child in condition.children) {
        	buildIfContent(child.id, level + (child.children == null ? 0 : 1))
            cnt++            
            if (cnt < condition.children.size()) {
            	def page = (level ? "pageConditionGroupL${level}" : (id == 0 ? "pageIf" : "pageIfOther"))
            	href page, /*image: "https://raw.githubusercontent.com/ady624/SmartThingers/master/resources/images/" + (level ? "folder.png" : "blank.png"),*/ params: ["conditionId": id], title: "", description: tab + grouping, state: "complete", required: true, submitOnChange: false
            }
        }
        
        if (aft) {
			href "pageConditionGroupL${level}", /*image: "https://raw.githubusercontent.com/ady624/SmartThingers/master/resources/images/folder.png",*/ params: ["conditionId": id], title: "", description: aft, state: "complete", required: true, submitOnChange: false
        }
    }
}

def cleanUpAttribute(attribute) {
	if (attribute) {
		return attribute.replace(customAttributePrefix(), "")
    }
    return null
}

def cleanUpComparison(comparison) {
	if (comparison) {
		return comparison.replace(triggerPrefix(), "").replace(conditionPrefix(), "")
    }
    return null
}

def getConditionDescription(id) {
	return getConditionDescription(id, 0)
}

def getConditionDescription(id, level) {
	def condition = getCondition(id)
    def pre = ""
    def preNot = ""
    def tab = ""
    def aft = ""
    def conditionGroup = (condition.children != null)
    switch (level) {
    	case 1:
        	pre = " ┌ ("
        	preNot = " ┌ NOT ("
        	tab = " │   "
        	aft = " └ )"
	        break;
        case 2:
    	    pre = " │ ┌ ["
    	    preNot = " │ ┌ NOT ["
        	tab = " │ │   "
       		aft = " │ └ ]"
        	break;
        case 3:
	        pre = " │ │ ┌ <"
	        preNot = " │ │ ┌ NOT {"
        	tab = " │ │ │   "
    	    aft = " │ │ └ >"
        	break;
    }
    if (!conditionGroup) {
    	//single condition
        def devices = settings["condDevices$id"]
        def capability = getCapabilityByDisplay(condition.cap)
        def virtualDevice = capability ? capability.virtualDevice : null
        if (virtualDevice || (devices && devices.size())) {
            def evaluation = (virtualDevice ? "" : (devices.size() > 1 ? (condition.mode == "All" ? "Each of " : "Any of ") : ""))
            def deviceList = (virtualDevice ? (capability.virtualDeviceName ? capability.virtualDeviceName : virtualDevice.name) : buildDeviceNameList(devices, "or")) + " "
	        def attribute = condition.attr + " "
            def attr = getAttributeByName(condition.attr)
            def unit = (attr && attr.unit ? attr.unit : "")
            def comparison = cleanUpComparison(condition.comp)
            def comp = getComparisonOption(condition.attr, comparison)
            def values = " [ERROR]"
            def time = ""
            if (comp) {
            	switch (comp.parameters) {
                	case 0:
                    	values = ""
                        break
                    case 1:
                    	values = " ${(condition.dev1 ? condition.dev1 + "'s $attribute" : condition.val1)}${unit}"
                        break
                    case 2:
                    	values = " ${(condition.dev1 ? condition.dev1 + "'s $attribute" : condition.val1 + unit) + " - " + (condition.dev2 ? condition.dev2 + "'s $attribute" : condition.val2 + unit)}"
                        break                       
            	}
                if (comp.timed) {
                	if (condition.for && condition.time) {
                		time = " " + condition.for + " " + condition.time
                    } else {
                    	time = " for [ERROR]"
                    }
                }
            }
            if (virtualDevice) {
            	attribute = ""
            }
            return tab + (condition.trg ? triggerPrefix() : conditionPrefix()) + evaluation + deviceList + attribute + comparison + values + time
        }
        return "Sorry, incomplete rule"
	} else {
    	//condition group
        def grouping = settings["condGrouping$id"]
        def negate = settings["condNegate$id"]
        def result = (negate ? preNot : pre) + "\n"
        def cnt = 1
        for (child in condition.children) {
        	result += getConditionDescription(child.id, level + (child.children == null ? 0 : 1)) + "\n" + (cnt < condition.children.size() ? tab + grouping + "\n" : "")
            cnt++
        }
        result += aft
        return result
    }
}


//used to get the next id for a condition, action, etc - looks into settings to make sure we're not reusing a previously used id
def getNextId(collection, prefix) {
	def nextId = _getLastId(collection) + 1
    while (settings.findAll { it.key == prefix + "Parent" + nextId }) {
    	nextId++
    }
    return nextId
}

//helper function for getNextId
private _getLastId(parent) {
	if (!parent) {
    	return -1
    }
	def lastId = parent?.id    
    for (child in parent.children) {
        def childLastId = _getLastId(child)
        lastId = lastId > childLastId ? lastId : childLastId
    }
    return lastId
}


//creates a condition (grouped or not)
def createCondition(group) {
    def condition = [:]
    //give the new condition an id
    condition.id = getNextId(state.config.app.conditions, 'cond')
    //initiate the condition type
    if (group) {
    	//initiate children
        condition.children = []
        condition.actions = []
    } else {
    	condition.type = null
    }
    return condition
}

//creates a condition and adds it to a parent
def createCondition(parentConditionId, group) {	
    def parent = getCondition(parentConditionId)
    if (parent) {
		def condition = createCondition(group)
    	//preserve the parentId so we can rebuild the app from settings
    	condition.parentId = parent ? parent.id : null
        //calculate depth for new condition
        condition.level = (parent.level ? parent.level : 0) + 1
   		//add the new condition to its parent, if any
        //set the parent for upwards traversal
   		parent.children.push(condition)
    	//return the newly created condition
    	return condition
	}
    return null
}

//deletes a condition
def deleteCondition(conditionId) {
	def condition = getCondition(conditionId)
    if (condition) {
    	def parent = getCondition(condition.parentId)
        if (parent) {
			parent.children.remove(condition);
        }
    }
}

//helper function for _cleanUpConditions
def _cleanUpCondition(condition, deleteGroups) {
	def result = false

	if (condition.children) {
    	//we cannot use a for each due to concurrent modifications
        //we're using a while instead
        def deleted = true
        while (deleted) {
        	deleted = false
			for (def child in condition.children) {
            	deleted = _cleanUpCondition(child, deleteGroups)
	    		result = result || deleted
                if (deleted) {
                	break
                }
            }
		}
    }

	//if non-root condition
	if (condition.id > 0) {
    	if (condition.children == null) {
        	//if regular condition
        	if ((condition.cap != "Location Mode") && (condition.cap != "Smart Home Monitor") && (condition.cap != "Date & Time") && settings["condDevices${condition.id}"] == null) {
	        	deleteCondition(condition.id);
	            return true
	        //} else {
            //	updateCondition(condition)
            }
        } else {
        	//if condition group
        	if (deleteGroups && (condition.children.size() == 0)) {
	        	deleteCondition(condition.id);
	            return true
	        }
        }
    }
    updateCondition(condition)
    return result
}

def updateCondition(condition) {
	condition.cap = settings["condCap${condition.id}"]
	condition.dev = []
    for (device in settings["condDevices${condition.id}"])
    {
    	//save the list of device IDs - we can't have the actual device objects in the state
    	condition.dev.push(device.id)
    }
	condition.mode = settings["condMode${condition.id}"]
    condition.attr = cleanUpAttribute(settings["condAttr${condition.id}"])
    if (!condition.attr) {
	    def cap = getCapabilityByDisplay(condition.cap)
        if (cap && cap.attribute) {
    		condition.attr = cap.attribute
        }
    }
    condition.comp = cleanUpComparison(settings["condComp${condition.id}"])
    condition.trg = isComparisonOptionTrigger(condition.attr, condition.comp)
    condition.val1 = settings["condValue${condition.id}#1"]
    condition.dev1 = settings["condDev${condition.id}#1"] ? settings["condDev${condition.id}#1"].label : null
    condition.val2 = settings["condValue${condition.id}#2"]
    condition.dev2 = settings["condDev${condition.id}#2"] ? settings["condDev${condition.id}#2"].label : null
    condition.for = settings["condFor${condition.id}"]
    condition.time = settings["condTime${condition.id}"]
    condition.grp = settings["condGrouping${condition.id}"]
    condition.grp = condition.grp && condition.grp.size() ? condition.grp : "AND"
    condition.not = !!settings["condNegate${condition.id}"]
    return null
}

//cleans up conditions - this may be replaced by a complete rebuild of the app object from the settings
def cleanUpConditions(deleteGroups) {
	//go through each condition in the state config and delete it if no associated settings exist
    _cleanUpCondition(state.config.app.conditions, deleteGroups)
    _cleanUpCondition(state.config.app.otherConditions, deleteGroups)
}

//finds and returns the condition object for the given condition Id
private _traverseConditions(parent, conditionId) {
    if (parent.id == conditionId) {
        return parent
    }
    for (condition in parent.children) {
        def result = _traverseConditions(condition, conditionId)
        if (result) {
            return result
        }
    }
    return null
}

//returns a condition based on its ID
def getCondition(conditionId) {
	def result = null
    if (state.config.app.conditions) {
    	result =_traverseConditions(state.config.app.conditions, conditionId)
    }
    if (!result && state.config.app.otherConditions) {
    	result = _traverseConditions(state.config.app.otherConditions, conditionId)
    }
	return result
}

//optimized version that returns true if any trigger is detected
def getConditionHasTriggers(condition) {
	def result = 0
    if (condition) {
        if (condition.children != null) {
            //we're dealing with a group
            for (child in condition.children) {
                if (getConditionHasTriggers(child)) {
                	//if we detect a trigger we exit immediately
                	return true
                }
            }
        } else {
        	return !!condition.trg
        }
    }
    return false
}

def getConditionTriggerCount(condition) {
	def result = 0
    if (condition) {
        if (condition.children != null) {
            //we're dealing with a group
            for (child in condition.children) {
                result += getConditionTriggerCount(child)
            }
        } else {
        	if (condition.trg) {
            	def devices = settings["condDevices${condition.id}"]
                if (devices) {
                	return devices.size()
                } else {
                	return 0
                }
            }
        }
    }
    return result
}

def getTriggerCount(app) {
	return getConditionTriggerCount(app.conditions) + (settings.mode == "Latching" ? getConditionTriggerCount(app.otherConditions) : 0)
}

def subscribeToDevices(condition, triggersOnly, handler, subscriptions, onlySubscriptions, excludeSubscriptions) {
	if (subscriptions == null) {
    	subscriptions = [:]
    }
	def result = 0
    if (condition) {
        if (condition.children != null) {
            //we're dealing with a group
            for (child in condition.children) {
                subscribeToDevices(child, triggersOnly, handler, subscriptions, onlySubscriptions, excludeSubscriptions)
            }
        } else {
        	if (condition.trg || !triggersOnly) {
            	//get the details
                def capability = getCapabilityByDisplay(condition.cap)
            	def devices = capability.virtualDevice ? (capability.attribute == "time" ? [] : [capability.virtualDevice]) : settings["condDevices${condition.id}"]
                def attribute = capability.virtualDevice ? capability.attribute : cleanUpAttribute(settings["condAttr${condition.id}"])
                if (devices) {
                	for (device in devices) {
                    	def subscription = "${device.id}-${attribute}"
                        if ((excludeSubscriptions == null) || !(excludeSubscriptions[subscription])) {
                        	//if we're provided with an exclusion list, we don't subscribe to those devices/attributes events
                            if ((onlySubscriptions == null) || onlySubscriptions[subscription]) {
                            	//if we're provided with a restriction list, we use it
                                if (!subscriptions[subscription]) {
                                    subscriptions[subscription] = true //[deviceId: device.id, attribute: attribute]
                                    if (handler) {
	                                    //we only subscribe to the device if we're provided a handler (not simulating)
                                        log.trace "Subscribing to events from $device for attribute $attribute, handler is $handler"
                                        subscribe(device, attribute, handler)
                                        //initialize the cache for the device - this will allow the triggers to work properly on first firing
                                        state.cache[device.id + "-" + attribute] = [v: device.currentValue(attribute), t: now()]
                                        
                                    }
                                }
                            }
						}
                    }
                } else {
                	return
                }
            }
        }
    }
    return subscriptions
}

def subscribeToAll(app) {
	log.trace "Initializing subscriptions..."
	//we have to maintain two separate logic threads for the latching mode
    //to do so, we first simulate 
	def hasTriggers = getConditionHasTriggers(app.conditions)
   	def hasLatchingTriggers = false
    
   	if (settings.mode == "Latching") {
    	//we really get the count
    	hasLatchingTriggers = getConditionHasTriggers(app.otherConditions)
		//simulate subscribing to both lists
		def subscriptions = subscribeToDevices(app.conditions, hasTriggers, null, null, null, null)
		def latchingSubscriptions = subscribeToDevices(app.otherConditions, hasLatchingTriggers, null, null, null, null)
        //we now have the two lists that we'd be subscribing to, let's figure out the common elements
        def commonSubscriptions = [:]
        for (subscription in subscriptions) {
        	if (latchingSubscriptions.containsKey(subscription.key)) {
            	//found a common subscription, save it
                commonSubscriptions[subscription.key] = true
            }
        }
        //perform subscriptions
		subscribeToDevices(app.conditions, false, bothDeviceHandler, null, commonSubscriptions, null)
		subscribeToDevices(app.conditions, hasTriggers, deviceHandler, null, null, commonSubscriptions)
		subscribeToDevices(app.otherConditions, hasLatchingTriggers, latchingDeviceHandler, null, null, commonSubscriptions)       
    } else {
    	//simple IF case, no worries here
    	subscribeToDevices(app.conditions, hasTriggers, deviceHandler, null, null, null)
    }
	log.trace "Finished subscribing"
}

def checkEventEligibility(condition, evt) {
	//we have a quad-state result
    // -2 means we're using triggers and the event does not match any of the used triggers
    // -1 means we're using conditions only and the event does not match any of the used conditions
    // 1 means we're using conditions only and the event does match at least one of the used conditions
    // 2 means we're using triggers and the event does match at least one of the used triggers
    // any positive value means the event is eligible for evaluation
	def result = -1 //assuming conditions only, no match
    if (condition) {
        if (condition.children != null) {
            //we're dealing with a group
            for (child in condition.children) {
                def v = checkEventEligibility(child, evt)
                switch (v) {
                	case -2:
                    	result = v
                    	break
                    case -1:
                    	break
                    case  1:
                    	if (result == -1) {
                        	result = v
                        }
                    	break
                    case  2:
	                	//if we already found a matching trigger, we're out
    	            	return v
                }
            }
        } else {
        	if (condition.trg) {
            	if (result < 2) {
                	//if we haven't already found a trigger
                	result = -2 // we are using triggers
                }
            }
            for (deviceId in condition.dev) {
                if ((evt.deviceId == deviceId) && (evt.name == condition.attr)) {
                	if (condition.trg) {
                    	//we found a trigger that matches the event, exit immediately
                    	return 2
                    } else {
                    	if (result == -1) {
                        	//we found a condition that matches the event, still looking for triggers though
                        	result = 1
                        }
                    }
                }
            }
        }
    }
    return result	
}

//blah blah functions that come with ST
def installed() {
	//log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	//log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    //move app to production
    state.app = state.config.app
    state.cache = [:]
    subscribeToAll(state.app)
}

def debug(message) {
	debug message, null
}

def debug(message, mode) {
	if (!settings.debugging) {
    	return
    }
    
    //mode is
    // 0 - initialize level, level set to 1
    // 1 - start of routine, level up
    // -1 - end of routine, level down
    // anything else - nothing happens
    
    def level = state.debugLevel ? state.debugLevel : 0
    def levelDelta = 0
    def prefix = "║"
    def pad = "░"
    switch (mode) {
    	case 0:
        	level = 0
            prefix = ""
            break
        case 1: 
        	level += 1
            prefix = "╚"
            pad = "═"
            break
        case -1:
        	levelDelta = -(level > 0 ? 1 : 0)
            pad = "═"
            prefix = "╔"
        break
    }
    
    if (level > 0) {
    	prefix = prefix.padLeft(level, "║").padRight(8, pad)
    }

    level += levelDelta
    state.debugLevel = level

	log.debug "$prefix $message"
}

def deviceHandler(evt) {
	//executes whenever a device in the primary if block has an event
	//starting primary IF block evaluation
    def perf = now()
    debug "", 0
	debug "Entering deviceHandler()", 1
    broadcastDeviceEvent(evt, true, false)
    perf = now() - perf
    debug "Exiting deviceHandler() after ${perf}ms", -1
}

def latchingDeviceHandler(evt) {
	//executes whenever a device in the primary if block has an event
	//starting primary IF block evaluation
    def perf = now()
    debug "", 0
	debug "Entering latchingDeviceHandler()", 1
    broadcastDeviceEvent(evt, false, true)
    perf = now() - perf
    debug "Exiting latchingDeviceHandler() after ${perf}ms", -1
}

def bothDeviceHandler(evt) {
	//executes whenever a common use device has an event
	//broadcast to both IF blocks
    def perf = now()
    debug "", 0
	debug "Entering bothDeviceHandler()", 1
    broadcastDeviceEvent(evt, true, true)
    perf = now() - perf
    debug "Exiting bothDeviceHandler() after ${perf}ms", -1

}

def broadcastDeviceEvent(evt, primary, secondary) {
	//filter duplicate events and broadcast event to proper IF blocks
    def perf = now()
	debug "Entering broadcastDeviceEvent()", 1
	debug "Event was generated on ${evt.date} (${evt.date.getTime()}), about ${now() - evt.date.getTime()}ms ago"  
    log.trace "Event ${evt.name} for device ${evt.device} with id ${evt.deviceId} and value ${evt.value}"
    def cachedValue = atomicState.cache[evt.deviceId + '-' + evt.name]
    def eventTime = evt.date.getTime()
	state.cache[evt.deviceId + '-' + evt.name] = [o: cachedValue ? cachedValue.v : null, v: evt.value, t: eventTime ]
    //we apparently can't change subelements of atomicState - we save the whole cache then?
    atomicState.cache = state.cache
	if (cachedValue) {
    	if ((cachedValue.v == evt.value) && ((cachedValue.v instanceof String) || (eventTime < cachedValue.t) || (cachedValue.t + 1000 > eventTime))) {
        	//duplicate event
    		debug "WARNING: Received duplicate event for device ${evt.device}, attribute ${evt.name}='${evt.value}', ignoring..."
            evt = null
        }
    }
    if (evt) {    
	    debug "Event is a valid, non-duplicate, storing it in the cache"
    	//broadcast to primary IF block
    	if (primary) evaluateConditionSet(evt, true)
    	//broadcast to secondary IF block
    	if (secondary) evaluateConditionSet(evt, false)
	}
    perf = now() - perf
    debug "Exiting broadcastDeviceEvent() after ${perf}ms", -1
    if (evt) log.trace "Event processing took ${perf}ms"
}

def evaluateConditionSet(evt, primary) {
	//executes whenever a device in the primary or secondary if block has an event
    def perf = now()
    def pushNote = null
    
	debug "Entering evaluateConditionSet()", 1
    debug "Event received by the ${primary ? "primary" : "secondary"} IF block evaluation for device ${evt.device}, attribute ${evt.name}='${evt.value}', isStateChange=${evt.isStateChange()}, currentValue=${evt.device.currentValue(evt.name)}, determining eligibility"
    //check for triggers - if the primary IF block has triggers and the event is not related to any trigger
    //then we don't want to evaluate anything, as only triggers should be executed
    //this check ensures that an event that is used in both blocks, but as different types, one as a trigger
    //and one as a condition do not interfere with each other
    def eligibilityStatus = checkEventEligibility(primary ? state.app.conditions: state.app.otherConditions , evt)
    debug "Eligibility status is $eligibilityStatus (" + (eligibilityStatus == 2 ? "triggers required, event is a trigger" : (eligibilityStatus == 1 ? "triggers not required, event is a condition" : (eligibilityStatus == -2 ? "triggers required, but event is a condition" : "something is messed up"))) + ")"
    if (eligibilityStatus > 0) {
        debug "Event is eligible for evaluation, proceeding..."
        def evaluation = evaluateCondition(primary ? state.app.conditions: state.app.otherConditions, evt)
        log.info "${primary ? "PRIMARY" : "SECONDARY"} EVALUATION IS $evaluation\n${getConditionDescription(primary ? 0 : -1)}\n"
        pushNote = "${evt.device}.${evt.name} >>> ${evt.value}\n${primary ? "primary" : "secondary"} evaluation result: $evaluation\n\n${getConditionDescription(primary ? 0 : -1)}\n\nEvent received after ${perf - evt.date.getTime()}ms\n"
    } else {
        debug "Event is ineligible for evaluation, ignoring..."
    }
    perf = now() - perf
    debug "Exiting evaluateConditionSet() after ${perf}ms", -1

	if (pushNote) {
    	sendPush(pushNote + "Event processed in ${perf}ms")
    }

    return evaluation
}

def evaluateCondition(condition, evt) {
	//evaluates a condition
    def perf = now()
	debug "Entering evaluateCondition()", 1
    
    def result = false
    
    if (condition.children == null) {
    	//we evaluate a real condition here
    	debug "Evaluating condition #${condition.id}"
        //several types of conditions, device, mode, SMH, time, etc.
        if (condition.dev && condition.dev.size()) {
        	result = evaluateDeviceCondition(condition, evt)
        }
    } else {
    	debug "Evaluating group #${condition.id} with grouping function ${condition.grp}"
    	//we evaluate a group
        result = (condition.grp == "AND") //we need to start with a true when doing AND or with a false when doing OR/XOR
        for (child in condition.children) {
        	//evaluate the child
           	def subResult = evaluateCondition(child, evt)
            //apply it to the composite result
        	switch (condition.grp) {
            	case "AND":
                	result = result && subResult
                    break
            	case "OR":
                	result = result || subResult
                    break
            	case "XOR":
                	result = result ^ subResult
                    break
            }
        }
    }
    
    perf = now() - perf
    debug "Exiting evaluateCondition() after ${perf}ms", -1

	return result
}

def evaluateDeviceCondition(condition, evt) {
	//evaluates a condition
    def perf = now()
	debug "Entering evaluateDeviceCondition()", 1
    
    //we need true when dealing with All
    def mode = condition.mode == "All" ? "All" : "Any"
    def result =  mode == "All" ? true : false
    
    //get list of devices
    def devices = settings["condDevices${condition.id}"]
    if (!devices) {
    	//something went wrong
        debug "Something went wrong, we cannot find any devices for condition #${condition.id}"
    	
    } else {
    	//the real deal goes here
        
        for (device in devices) {

			//update cache for device
//            def key = device.id + '-' + condition.attr
//            def cachedValue = state.cache[key]
//            if (cachedValue && cachedValue.o == null) {
//				state.cache[key] = [o: cachedValue.v, t: now(), v: device.currentValue(condition.attr)]
//            }

			def comp = getComparisonOption(condition.attr, condition.comp)
            if (comp) {
            	//if event is about the same device/attribute, use the event's value as the current value, otherwise, fetch the current value from the device
                def deviceResult = false
                def ownsEvent = (evt.deviceId == device.id) && (evt.name == condition.attr)
                def oldValue = null
                def cachedValue = state.cache[device.id + "-" + condition.attr]
                if (cachedValue) oldValue = cachedValue.o
            	def currentValue = ownsEvent ? evt.value : device.currentValue(condition.attr)
                def value1 = condition.dev1 && settings["condDev${condition.id}#1"] ? settings["condDev${condition.id}#1"].currentValue(condition.attr) : condition.val1
                def value2 = condition.dev2 && settings["condDev${condition.id}#2"] ? settings["condDev${condition.id}#2"].currentValue(condition.attr) : condition.val2
                //casting
				def attr = getAttributeByName(condition.attr)
                if (attr) {
                	switch (attr.type) {
                    	case "number":
                        	if (oldValue instanceof String) oldValue = oldValue.isInteger() ? oldValue.toInteger() : 0
                        	if (currentValue instanceof String) currentValue = currentValue.isInteger() ? currentValue.toInteger() : 0
							if (value1 instanceof String) value1 = value1.isInteger() ? value1.toInteger() : 0
							if (value2 instanceof String) value2 = value2.isInteger() ? value2.toInteger() : 0
                        case "decimal":
                        	if (oldValue instanceof String) oldValue = oldValue.isFloat() ? oldValue.isFloat() : 0
							if (currentValue instanceof String) currentValue = currentValue.isFloat() ? currentValue.toFloat() : 0
							if (value1 instanceof String) value1 = value1.isFloat() ? value1.toFloat() : 0
							if (value2 instanceof String) value2 = value2.isFloat() ? value1.toFloat() : 0
                        	break
                    }
                }
                if (condition.trg && !ownsEvent) {
                    //all triggers should own the event, otherwise be false
                    deviceResult = false
                } else {          
                	def function = "eval_" + (condition.trg ? "trg" : "cond") + "_" + condition.comp.replace(" ", "_")
					deviceResult = "$function"(condition, device, condition.attr, oldValue, currentValue, value1, value2, ownsEvent ? evt : null, evt)
                    log.info "Function $function for $device [$oldValue >>> $currentValue] ${condition.comp} $value1 - $value2 returned $deviceResult"
                }
                
        		//compound the result, depending on mode
                def finalResult = false
        		switch (mode) {
	            	case "All":
	                	result = result && deviceResult
                        finalResult = !result
	                	break
	                case "Any":
	                	result = result || deviceResult
                        finalResult = result
	                	break
	            }
                //optimize the loop to exit when we find a result that's going to be the final one (AND encountered a false, or OR encountered a true)
                if (finalResult) break
            }
        }
        
    }
    perf = now() - perf
    debug "Exiting evaluateDeviceCondition() after ${perf}ms", -1
	
    return condition.not ? !result : result
}




/* low-level evaluation functions */
def eval_cond_is(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return eval_cond_is_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt)
}

def eval_cond_is_not(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return eval_cond_is_not_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt)
}

def eval_cond_is_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return currentValue == value1
}

def eval_cond_is_not_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return currentValue != value1
}

def eval_cond_is_less_than(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return currentValue < value1
}

def eval_cond_is_less_than_or_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return currentValue <= value1
}

def eval_cond_is_greater_than(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return currentValue > value1
}

def eval_cond_is_greater_than_or_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return currentValue >= value1
}

def eval_cond_is_even(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	if (currentValue.isInteger()) {
    	return currentValue.toInteger().mod(2) == 0
    }
	return false
}

def eval_cond_is_odd(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	if (currentValue.isInteger()) {
    	return currentValue.toInteger().mod(2) == 0
    }
	return false
}

def eval_cond_is_inside_range(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	if (value1 < value2) {
		return (currentValue >= value1) && (currentValue <= value2)
    } else {
		return (currentValue >= value2) && (currentValue <= value1)
    }
}

def eval_cond_is_outside_of_range(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	if (value1 < value2) {
		return (currentValue < value1) || (currentValue > value2)
	} else {
		return (currentValue < value2) || (currentValue > value1)
    }
}

def listPreviousStates(device, attribute, currentValue, minutes, excludeLast) {
//	def events = device.eventsSince(new Date(now() - minutes * 60000));
	def events = device.events([all: true, max: 100]).findAll{it.name == attribute}
    def result = []
    //if we got any events, let's go through them       
	//if we need to exclude last event, we start at the second event, as the first one is the event that triggered this function. The attribute's value has to be different from the current one to qualify for quiet
    def value = currentValue
    def thresholdTime = now() - minutes * 60000
    def endTime = now()
    for(def i = 0; i < events.size(); i++) {
    	def startTime = events[i].date.getTime()
    	def duration = endTime - startTime
        if ((duration >= 1000) && ((i > 0) || !excludeLast)) {
	        result.push([value: events[i].value, startTime: startTime, duration: duration])
        }
        if (startTime < thresholdTime)
	        break
        endTime = startTime
    }
    return result
}


def eval_cond_was(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return eval_cond_was_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt)
}

def eval_cond_was_not(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	eval_cond_was_not_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt)
}

def eval_cond_was_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value == value1) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_not_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value != value1) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_less_than(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value < value1) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_less_than_or_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value <= value1) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_greater_than(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value > value1) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_greater_than_or_equal_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value >= value1) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_even(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value.isInteger() ? state.value.toInteger().mod(2) == 0 : false) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_odd(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (state.value.isInteger() ? state.value.toInteger().mod(2) == 1 : false) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_inside_range(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (value1 < value2 ? (state.value >= value1) && (state.value <= value2) : (state.value >= value2) && (state.value <= value1)) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

def eval_cond_was_outside_of_range(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
    def time = timeToMinutes(condition.time)
	def states = listPreviousStates(device, attribute, currentValue, time, evt ? 1 : 0)
    def thresholdTime = time * 60000
    def stableTime = 0
    for (state in states) {
    	if (value1 < value2 ? (state.value < value1) || (state.value > value2) : (state.value < value2) || (state.value > value1)) {
        	stableTime += state.duration
        } else {
        	break
        }
    }
    return (stableTime > 0) && (condition.for == "for at least" ? stableTime >= thresholdTime : stableTime < thresholdTime)
}

/* triggers */
def eval_trg_changes(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return oldValue != currentValue
}

def eval_trg_changes_to(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_equal_to(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_equal_to(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_changes_away_from(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_not_equal_to(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_not_equal_to(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_drops_below(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_less_than(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_less_than(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_drops_to_or_below(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_less_than_or_equal_to(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_less_than_or_equal_to(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_raises_above(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_greater_than(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_greater_than(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_raises_to_or_above(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_greater_than_or_equal_to(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_greater_than_or_equal_to(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_changes_to_even(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_even(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_even(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_changes_to_odd(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_odd(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_odd(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_enters_range(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_inside_range(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_inside_range(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def eval_trg_exits_range(condition, device, attribute, oldValue, currentValue, value1, value2, evt, sourceEvt) {
	return !eval_cond_is_outside_of_range(condition, device, attribute, null, oldValue, value1, value2, evt, sourceEvt) &&
    		eval_cond_is_outside_of_range(condition, device, attribute, null, currentValue, value1, value2, evt, sourceEvt)
}

def dummy() {
	//evaluates a condition
    def perf = now()
	debug "Entering dummy()", 1
    
    
    perf = now() - perf
    debug "Exiting dummy() after ${perf}ms", -1
	
}