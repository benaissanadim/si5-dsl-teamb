sensor "button" onPin 9
actuator "led" pin 11
state "on" means "led" becomes "high"
state "off" means "led" becomes "low"

initial "off"

from "off" to "on" when "button" becomes "high"
from "on" to "off" after 1000ms

export "Switch!"