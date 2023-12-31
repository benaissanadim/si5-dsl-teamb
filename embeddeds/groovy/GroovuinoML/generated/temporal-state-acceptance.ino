// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;
long startTime;
bool startTimer = false;

enum STATE {on, off};
STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

void setup(){
	Serial.begin(9600);
  pinMode(9, INPUT);  // button [Sensor]
	pinMode(11, OUTPUT); // led [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			if (startTimer == false) {
				startTime = millis();
				startTimer = true;
			}
			if ( millis() - startTime > 1000){
				currentState = off;
				startTimer = false;
			}
			break;
		case off:
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			if ( buttonBounceGuard && digitalRead(9) == HIGH ){
				buttonLastDebounceTime = millis();
				currentState = on;
			}
			break;
	}
}
