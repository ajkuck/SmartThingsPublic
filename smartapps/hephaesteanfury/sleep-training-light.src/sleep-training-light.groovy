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
 *  Sleep Training Light
 *
 *  Author: Andrew Kuck
 *
 *  Change the color of light to indicate to children when it's time to go to bed or get up.
 */

definition(
    name: "Sleep Training Light",
    namespace: "hephaesteanfury",
    author: "Andrew Kuck",
    description: "Change the color of light to indicate to children when it's time to go to bed or get up.",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

/** Constants for Hue Colors */
Map getHueColors() {
    return [Red: 0, Green: 39, Blue: 70, Yellow: 25, Orange: 10, Purple: 75, Pink: 83, White:0]
}

Map getSaturationColors() {
    return [Red: 255, Green: 255, Blue: 255, Yellow: 255, Orange: 255, Purple: 255, Pink: 255, White:0]
}

Map getValueColors() {
    return [Red: 255, Green: 255, Blue: 255, Yellow: 255, Orange: 255, Purple: 255, Pink: 255, White:255]
}

preferences {
	section("Select lights to control...") {
		input name: "lights", type: "capability.colorControl", multiple: true
	}
	section("Time to get ready for bed...") {
		input name: "bedPrepTime", title: "Bed Prep Time?", type: "time"
		input name: "bedPrepColor", title: "Bed Prep Color?", type: "enum", 
        			required: true, multiple: false, default: 4, 
                    options: getHueColors().keySet() as String[]
	}
    section("Time to go to bed...") {
		input name: "bedTime", title: "Bed Time?", type: "time"
		input name: "bedColor", title: "Bed Time Color?", type: "enum",
        	  required: true, 
              multiple: false,
              default: "Blue",
              options: getHueColors().keySet() as String[]
	}

	section("Okay to wake up...") {
		input name: "wakeTime", title: "Wake Up Time?", type: "time"
		input name: "wakeColor", title: "Wake Up Color?", type: "enum",
        	  required: true, 
              multiple: false,
              default: "Green",
              options: getHueColors().keySet() as String[]
	}
    section("Get out of bed...") {
		input name: "getUpTime", title: "Get Up Time?", type: "time"
		input name: "getUpColor", title: "Get Up Color?", type: "enum",
        	  required: true, 
              multiple: false,
              default: "White",
              options: getHueColors().keySet() as String[]
	}

    section("Ignore when mode is...") {
		input name: "activeModes", title: "Mode?", type: "mode", multiple: false, required: false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	schedule(bedPrepTime, "bedPrepTimerCallback")
	schedule(bedTime, "bedTimerCallback")
	schedule(wakeTime, "wakeTimerCallback")
	schedule(getUpTime, "getUpTimerCallback")
}

def updated(settings) {
	unschedule()
	schedule(bedPrepTime, "bedPrepTimerCallback")
	schedule(bedTime, "bedTimerCallback")
	schedule(wakeTime, "wakeTimerCallback")
	schedule(getUpTime, "getUpTimerCallback")
}

def bedPrepTimerCallback() {
	log.debug "Setting BedPrepTimer"
    log.warn "Setting to target color: '${bedPrepColor}'"
    setIfActive(bedPrepColor)
}

def bedTimerCallback() {
	log.debug "Setting Bed-Timer"
    setIfActive(bedColor)
}

def wakeTimerCallback() {
	log.debug "Setting Wake-Up-Timer"
    log.warn "Setting to target color: '${bedPrepColor}'"
    setIfActive(wakeColor)
}

def getUpTimerCallback() {
	log.debug "Setting Get-Up-Timer"
    setIfActive(getUpColor)
}

def setIfActive(targetColor)
{    
	def currentMode = location.mode
    if (activeModes && (activeModes == currentMode)) {
    	log.warn "Skipping because mode '${currentMode}'"
	}
    else
    {
    	log.warn "Acting because mode '${currentMode}'"
        log.warn "Setting to target color: '${targetColor}'"
        lights.setLevel(getValueColors()[targetColor])
        lights.setSaturation(getSaturationColors()[targetColor])
        lights.setHue(getHueColors()[targetColor])
        lights.on()
    }
}
