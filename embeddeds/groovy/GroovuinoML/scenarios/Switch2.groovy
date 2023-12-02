sensor "button1" onPin 9
sensor "button2" pin 10
actuator "buzzer" pin 12

state "on" means "buzzer" becomes "high"
state "off" means  "buzzer" becomes "low"

initial "off"

from "on" to "off" when "button1" becomes "low" or "button2" becomes "low"

from "off" to "off" when "button1" becomes "high" and "button2" becomes "high"

export "Switch!"