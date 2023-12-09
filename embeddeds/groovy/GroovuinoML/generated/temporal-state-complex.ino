// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;
long startTime;
bool startTimer = false;

enum STATE {on, buzz, off};
STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

bool breakButtonBounceGuard = false;
long breakButtonLastDebounceTime = 0;

void setup(){
	Serial.begin(9600);
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(8, INPUT);  // breakButton [Sensor]
	pinMode(11, OUTPUT); // led [Actuator]
	pinMode(10, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
			breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
			if (startTimer == false) {
				startTime = millis();
				startTimer = true;
			}
			if( ( buttonBounceGuard && digitalRead(9) == HIGH ) &&  ( millis() - startTime > 1000)){
				currentState = buzz;
				startTimer = false;
			}
			if( ( breakButtonBounceGuard && digitalRead(8) == LOW ) &&  ( millis() - startTime > 2000)){
				currentState = off;
				startTimer = false;
			}
			break;
		case buzz:
			if (startTimer == false) {
				startTime = millis();
				startTimer = true;
			}
			if ( millis() - startTime > 1000){
				currentState = off;
				startTimer = false;
			}
			if ( breakButtonBounceGuard && digitalRead(8) == HIGH ){
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
