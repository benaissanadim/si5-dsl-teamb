// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

bool thermicSensorBounceGuard = false;
long thermicSensorLastDebounceTime = 0;

void setup(){
	Serial.begin(9600);
    pinMode(9, INPUT);  // button [Sensor]
    pinMode(10, INPUT);  // thermicSensor [Sensor]
	pinMode(11, OUTPUT); // led [Actuator]
}

void loop() {
	char incomingChar = Serial.read();
	switch(currentState){
		case on:
			Serial.println(analogRead(10));
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == LOW ){
				buttonLastDebounceTime = millis();
				currentState = off;
			}
			break;
		case off:
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if( ( buttonBounceGuard && digitalRead(9) == HIGH ) || incomingChar == 'A' ){
				buttonLastDebounceTime = millis();
				currentState = on;
			}
			break;
	}
}
