
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
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
					breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
					               
                    startTime = millis();
                    // Continue as long as the elapsed time is less than 1000 milliseconds
                    while (millis() - startTime < 1000 && digitalRead(9) == HIGH && buttonBounceGuard ) {
                        if ( digitalRead(8) == HIGH && breakButtonBounceGuard ) {
						breakButtonLastDebounceTime = millis();
						currentState = off;
					}
					   
                      delayMicroseconds(100);
                    }
                    currentState = buzz;
				  break;
				case buzz:
					digitalWrite(10,HIGH);
					breakButtonBounceGuard = static_cast<long>(millis() - breakButtonLastDebounceTime) > debounce;
					               
                    startTime = millis();
                    // Continue as long as the elapsed time is less than 1000 milliseconds
                    while (millis() - startTime < 1000) {
                        if ( digitalRead(8) == HIGH && breakButtonBounceGuard ) {
						breakButtonLastDebounceTime = millis();
						currentState = off;
					}
					   
                      delayMicroseconds(100);
                    }
                    currentState = off;
				  break;
				case off:
					digitalWrite(11,LOW);
					digitalWrite(10,LOW);
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					if ( digitalRead(9) == HIGH && buttonBounceGuard ) {
						buttonLastDebounceTime = millis();
						currentState = on;
					}
					
				  break;
		}
	}
	
