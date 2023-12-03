sensor "button" onPin 9
actuator "led" pin 11
actuator "buzzer" pin 12
temporalstate "on" means "led" becomes "high" and "buzzer" becomes "high"
state "off" means "led" becomes "low" and "buzzer" becomes "low"

initial "off"


from "off" to "on" when "button" becomes "low"
from "on" to "off" after 1000

export "Switch!"