
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
enum STATE {on, off};

STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;

            

	void setup(){
		pinMode(9, INPUT); // button [Sensor]
		pinMode(11, OUTPUT); // led [Actuator]
	}
	void loop() {
			switch(currentState){

				case on:
					digitalWrite(11,HIGH);
		 			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
					if ( digitalRead(button.inputPin) == LOW && buttonBounceGuard ) {
					buttonLastDebounceTime = millis();
					currentState = off;
					}
		
				break;
				case off:
					digitalWrite(11,LOW);
		 			buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
					if ( digitalRead(button.inputPin) == HIGH && buttonBounceGuard ) {
					buttonLastDebounceTime = millis();
					currentState = on;
					}
		
				break;
		}
	}
	
