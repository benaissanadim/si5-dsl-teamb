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
			buttonBounceGuard = millis() - lastDebounceTime > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = off;
			}
		break;
		case off:
			digitalWrite(11,LOW);
			buttonBounceGuard = millis() - lastDebounceTime > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = on;
			}
		break;
	}
}
