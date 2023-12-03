// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {firstPush, secondPush, off};
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
		case firstPush:
			digitalWrite(11,LOW);
			digitalWrite(12,HIGH);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = secondPush;
			}
			break;
		case secondPush:
			digitalWrite(11,HIGH);
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = off;
			}
			break;
		case off:
			digitalWrite(11,LOW);
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = firstPush;
			}
			break;
	}
}
