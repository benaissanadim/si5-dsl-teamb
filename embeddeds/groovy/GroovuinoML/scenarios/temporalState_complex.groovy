sensor "button" onPin 9
sensor "breakButton" onPin 8
actuator "led" pin 11
actuator "buzzer" pin 10
state "on" means "led" becomes "high" and "buzzer" becomes "low"
state "buzz" means "buzzer" becomes "high" and "led" becomes "high"
state "off" means "led" becomes "low" and "buzzer" becomes "low"

initial "off"

from "off" to "on" when "button" becomes "high"
from "on" to "buzz" after 1000 and "button" becomes "high"
from "on" to "off" after 2000 and "breakButton" becomes "low"
from "buzz" to "off" after 1000ms
from "buzz" to "off" when "breakButton" becomes "high"

export "Switch!"