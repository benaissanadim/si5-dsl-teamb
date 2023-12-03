// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {on, off, error};
STATE currentState = off;

boolean button1BounceGuard = false;
long button1LastDebounceTime = 0;

boolean button2BounceGuard = false;
long button2LastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button1 [Sensor]
  pinMode(10, INPUT);  // button2 [Sensor]
  pinMode(11, OUTPUT); // led [Actuator]
  pinMode(12, OUTPUT); // errorLed [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			button1BounceGuard = millis() - button1LastDebounceTime > debounce;
			button2BounceGuard = millis() - button2LastDebounceTime > debounce;
			if( ( button1BounceGuard && digitalRead(9) == HIGH ) &&  ( button2BounceGuard && digitalRead(10) == HIGH )){
				button1LastDebounceTime = millis();
				button2LastDebounceTime = millis();
				currentState = error;
			}
			break;
		case off:
			digitalWrite(11,LOW);
			digitalWrite(12,LOW);
			button1BounceGuard = millis() - button1LastDebounceTime > debounce;
			button2BounceGuard = millis() - button2LastDebounceTime > debounce;
			if( ( button1BounceGuard && digitalRead(9) == HIGH ) &&  ( button2BounceGuard && digitalRead(10) == HIGH )){
				button1LastDebounceTime = millis();
				button2LastDebounceTime = millis();
				currentState = error;
			}
			break;
		case error:
			for (int i = 0; i < 3; i++) {
				digitalWrite(12, HIGH);
				delay(500);
				digitalWrite(12, LOW);
				delay(500);
			}
			delay(3 * 1000);
			button1BounceGuard = millis() - button1LastDebounceTime > debounce;
			button2BounceGuard = millis() - button2LastDebounceTime > debounce;
			if( ( button1BounceGuard && digitalRead(9) == HIGH )^ ( button2BounceGuard && digitalRead(10) == HIGH )){
				button1LastDebounceTime = millis();
				button2LastDebounceTime = millis();
				currentState = on;
			}
		break;
	}
}
