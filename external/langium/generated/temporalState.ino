
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
bool startTimer = false;
long startTime; // Used for temporal transitions
enum STATE {on, buzz, off};

STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;



bool breakButtonBounceGuard = false;
long breakButtonLastDebounceTime = 0;



	void setup(){
		pinMode(9, INPUT); // button [Sensor]
		pinMode(8, INPUT); // breakButton [Sensor]
		pinMode(11, OUTPUT); // led [Actuator]
		pinMode(10, OUTPUT); // buzzer [Actuator]
	}
	void loop() {
			switch(currentState){

				case on:
					digitalWrite(11,HIGH);
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					              
					breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
					if ( ( ( millis() - startTime > 3000 ) && ( digitalRead(9) == HIGH && buttonBounceGuard ) ) ) {
						buttonLastDebounceTime = millis();
						currentState = buzz;
                        startTimer = false;
					}
					if ( ( ( millis() - startTime > 3000 ) && ( digitalRead(8) == HIGH && breakButtonBounceGuard ) ) ) {
						breakButtonLastDebounceTime = millis();
						currentState = off;
                        startTimer = false;
					}
					

				    break;
            
				case buzz:
					digitalWrite(10,HIGH);
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					              
					breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
					if ( millis() - startTime > 5000 ) {
						currentState = off;
                        startTimer = false;
					}
					if ( digitalRead(8) == HIGH && breakButtonBounceGuard ) {
						breakButtonLastDebounceTime = millis();
						currentState = off;
                        startTimer = false;
					}
					

				    break;
            
				case off:
					digitalWrite(11,LOW);
					digitalWrite(10,LOW);
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					if ( digitalRead(9) == HIGH && buttonBounceGuard ) {
						buttonLastDebounceTime = millis();
						currentState = on;
                        startTimer = false;
					}
					

				    break;
            
		}
	}
	
