sensor "button1" onPin 9
sensor "button2" onPin 10
actuator "led" pin 11
actuator "errorLed" pin 12

state "on" means "led" becomes "high"
state "off" means "led" becomes "low" and "errorLed" becomes "low"
error "error" means "errorLed" flashes 3 times "and" pauses 3 ms

initial "off"

from "on" to "off" when "button1" becomes "low" and "button2" becomes "low"
from "on" to "error" when "button1" becomes "high" and "button2" becomes "high"

from "off" to "on" when "button1" becomes "high" xor "button2" becomes "high"
from "off" to "error" when "button1" becomes "high" and "button2" becomes "high"

from "error" to "off" when "button1" becomes "low" and "button2" becomes "low"
from "error" to "on" when  "button1" becomes "high" xor "button2" becomes "high"

export "Switch!"