// Wiring code generated from an ArduinoML model
// Application name: Switch!

long debounce = 200;

enum STATE {on, off};
STATE currentState = off;

boolean buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

boolean button2BounceGuard = false;
long button2LastDebounceTime = 0;

void setup(){
  pinMode(9, INPUT);  // button [Sensor]
  pinMode(10, INPUT);  // button2 [Sensor]
  pinMode(11, OUTPUT); // led [Actuator]
  pinMode(12, OUTPUT); // buzzer [Actuator]
}

void loop() {
	switch(currentState){
		case on:
			digitalWrite(11,HIGH);
			digitalWrite(12,HIGH);
			buttonBounceGuard = millis() - lastDebounceTime > debounce;
			button2BounceGuard = millis() - lastDebounceTime > debounce;
			if( ( buttonBounceGuard && digitalRead(9) == HIGH ) &&  ( button2BounceGuard && digitalRead(10) == LOW ){
				buttonLastDebounceTime = millis();
{
				button2LastDebounceTime = millis();
				currentState = off;
			}
		break;
		case off:
			digitalWrite(11,LOW);
			digitalWrite(12,LOW);
			buttonBounceGuard = millis() - lastDebounceTime > debounce;
			button2BounceGuard = millis() - lastDebounceTime > debounce;
			if( ( buttonBounceGuard && digitalRead(9) == LOW ) ||  ( button2BounceGuard && digitalRead(10) == LOW ){
				buttonLastDebounceTime = millis();
{
				button2LastDebounceTime = millis();
				currentState = on;
			}
		break;
	}
}
