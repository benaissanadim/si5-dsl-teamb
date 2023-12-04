// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;
long startTime;

enum STATE {buzz, onLed, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean breakButtonBounceGuard = false;
long breakButtonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(10, INPUT);  // breakButton [Sensor]
  pinMode(11, OUTPUT); // led [Actuator]
  pinMode(12, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case buzz:
			digitalWrite(12,HIGH);
			digitalWrite(11,HIGH);
			startTime = millis();
			while(millis() - startTime < 1000){
			breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
			if ( breakButtonBounceGuard && digitalRead(10) == HIGH ){
				breakButtonLastDebounceTime = millis();
				currentState = off;
			}
			}
			currentState = onLed;
			break;
		case onLed:
			digitalWrite(11,HIGH);
			digitalWrite(12,LOW);
			startTime = millis();
			while(millis() - startTime < 1000){
			breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
			if ( breakButtonBounceGuard && digitalRead(10) == HIGH ){
				breakButtonLastDebounceTime = millis();
				currentState = off;
			}
			}
			currentState = buzz;
			break;
		case off:
			digitalWrite(11,LOW);
			digitalWrite(12,LOW);
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = onLed;
			}
			break;
	}
}
