// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;
long startTime;

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
			startTime = millis();
			while(( millis() - startTime < 1000)){
			delayMicroseconds(100)
			}
			currentState = off;
			break;
		case off:
			digitalWrite(11,LOW);
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = on;
			}
			break;
	}
}
