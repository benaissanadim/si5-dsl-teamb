sensor "button" onPin 9
sensor "thermicSensor" onPin 10
actuator "led" pin 11
state "on" means "led" becomes "high" and "thermicSensor" becomes "printing"
state "off" means "led" becomes "low"

initial "off"

from "off" to "on" when "button" becomes "high" or "A" becomes "pressed"
from "on" to "off" when "button" becomes "low"

export "Switch!"