// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;
long startTime;

enum STATE {on, buzz, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean breakButtonBounceGuard = false;
long breakButtonLastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(8, INPUT);  // breakButton [Sensor]
  pinMode(11, OUTPUT); // led [Actuator]
  pinMode(10, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			digitalWrite(10,LOW);
			startTime = millis();
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			while(millis() - startTime < 1000 && ( buttonBounceGuard && digitalRead(9) == HIGH )){
			breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
			if ( breakButtonBounceGuard && digitalRead(8) == HIGH ){
				breakButtonLastDebounceTime = millis();
				currentState = off;
			}
			}
			currentState = buzz;
			break;
		case buzz:
			digitalWrite(10,HIGH);
			digitalWrite(11,HIGH);
			startTime = millis();
			while(millis() - startTime < 1000){
			breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
			if ( breakButtonBounceGuard && digitalRead(8) == HIGH ){
				breakButtonLastDebounceTime = millis();
				currentState = off;
			}
			}
			currentState = on;
			break;
		case off:
			digitalWrite(11,LOW);
			digitalWrite(10,LOW);
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = on;
			}
			break;
	}
}
