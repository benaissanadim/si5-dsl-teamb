sensor "button" onPin 9
actuator "led" pin 11
actuator "buzzer" pin 12

state "firstPush" means "led" becomes "low" and "buzzer" becomes "high"
state "secondPush" means "led" becomes "high" and "buzzer" becomes "low"
state "off" means "led" becomes "low" and "buzzer" becomes "low"

initial "off"

from "firstPush" to "secondPush" when "button" becomes "high"
from "secondPush" to "off" when "button" becomes "high"
from "off" to "firstPush" when "button" becomes "high"


export "Switch!"