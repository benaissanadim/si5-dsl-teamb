// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {firstPush, secondPush, off};
STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
	Serial.begin(9600);
  pinMode(9, INPUT);  // button [Sensor]
	pinMode(11, OUTPUT); // led [Actuator]
	pinMode(12, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case firstPush:
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = secondPush;
			}
			break;
		case secondPush:
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = off;
			}
			break;
		case off:
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = firstPush;
			}
			break;
	}
}
