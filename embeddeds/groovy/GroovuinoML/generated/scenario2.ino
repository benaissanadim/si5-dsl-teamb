// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

bool button1BounceGuard = false;
long button1LastDebounceTime = 0;

bool button2BounceGuard = false;
long button2LastDebounceTime = 0;

void setup(){
	Serial.begin(9600);
  pinMode(9, INPUT);  // button1 [Sensor]
  pinMode(10, INPUT);  // button2 [Sensor]
	pinMode(12, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			button1BounceGuard = static_cast<long>(millis() - button1LastDebounceTime) > debounce;
			if ( button1BounceGuard && digitalRead(9) == LOW ){
				button1LastDebounceTime = millis();
				currentState = off;
			}
			break;
		case off:
			button2BounceGuard = static_cast<long>(millis() - button2LastDebounceTime) > debounce;
			button1BounceGuard = static_cast<long>(millis() - button1LastDebounceTime) > debounce;
			if( ( button1BounceGuard && digitalRead(9) == HIGH ) &&  ( button2BounceGuard && digitalRead(10) == HIGH )){
				button1LastDebounceTime = millis();
				button2LastDebounceTime = millis();
				currentState = on;
			}
			break;
	}
}
