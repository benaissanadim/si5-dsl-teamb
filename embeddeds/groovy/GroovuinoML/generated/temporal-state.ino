fromState: on
// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(11, OUTPUT); // led [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			long startTime = millis();
			if(millis() - startTime > 1000){
				currentState = off;
			}
		case off:
			digitalWrite(11,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if ( buttonBounceGuard && digitalRead(9) == LOW ){
				buttonLastDebounceTime = millis();
				currentState = on;
			}
			break;
	}
}
