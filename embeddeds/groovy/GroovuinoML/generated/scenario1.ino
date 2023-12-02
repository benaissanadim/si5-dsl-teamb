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
  pinMode(12, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			digitalWrite(12,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if (buttonBounceGuard && ( ( buttonBounceGuard && digitalRead(9) == LOW )) ) {
				buttonLastDebounceTime = millis();
				currentState = off;
			}
		break;
		case off:
			digitalWrite(11,LOW);
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if (buttonBounceGuard && ( ( buttonBounceGuard && digitalRead(9) == HIGH )) ) {
				buttonLastDebounceTime = millis();
				currentState = on;
			}
		break;
	}
}
