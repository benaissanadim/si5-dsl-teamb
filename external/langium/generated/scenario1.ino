
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
long startTime; // Used for temporal transitions
enum STATE {on, off};

STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;



	void setup(){
		pinMode(9, INPUT); // button [Sensor]
		pinMode(11, OUTPUT); // led [Actuator]
		pinMode(12, OUTPUT); // buzzer [Actuator]
	}
	void loop() {
			switch(currentState){

				case on:
					digitalWrite(11,HIGH);
					digitalWrite(12,HIGH);
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					if ( digitalRead(9) == LOW && buttonBounceGuard ) {
						buttonLastDebounceTime = millis();
						currentState = off;
					}
					

				    break;
            
				case off:
					digitalWrite(11,LOW);
					digitalWrite(12,LOW);
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					if ( digitalRead(9) == HIGH && buttonBounceGuard ) {
						buttonLastDebounceTime = millis();
						currentState = on;
					}
					

				    break;
            
		}
	}
	
