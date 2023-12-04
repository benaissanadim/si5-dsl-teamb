sensor "button" onPin 9
sensor "breakButton" onPin 10
actuator "led" pin 11
actuator "buzzer" pin 12
temporalstate "buzz" means "buzzer" becomes "high" and "led" becomes "high"
temporalstate "onLed" means "led" becomes "high" and "buzzer" becomes "low"
state "off" means "led" becomes "low" and  "buzzer" becomes "low"

initial "off"


from "off" to "onLed" when "button" becomes "high"
from "onLed" to "buzz" after 1000
from "buzz" to "onLed" after 1000
from "buzz" to "off" when "breakButton" becomes "high"
from "onLed" to "off" when "breakButton" becomes "high"

export "Switch!"