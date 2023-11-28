
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
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
                    long startTime = millis();
                    // Continue as long as the elapsed time is less than 1000 milliseconds
                    while (millis() - startTime < 1000) {
                        if ( digitalRead(8) == HIGH && breakButtonBounceGuard ) {
						breakButtonLastDebounceTime = millis();
						currentState = off;
					}
					   
                      delayMicroseconds(100);
                    }
                    currentState = buzz;
					breakButtonBounceGuard = millis() - breakButtonLastDebounceTime > debounce;
					
				  break;
				case buzz:
					digitalWrite(10,HIGH);               
                    long startTime = millis();
                    // Continue as long as the elapsed time is less than 1000 milliseconds
                    while (millis() - startTime < 1000) {
                        if ( digitalRead(8) == HIGH && breakButtonBounceGuard ) {
						breakButtonLastDebounceTime = millis();
						currentState = off;
					}
					   
                      delayMicroseconds(100);
                    }
                    currentState = off;
					breakButtonBounceGuard = millis() - breakButtonLastDebounceTime > debounce;
					
				  break;
				case off:
					digitalWrite(11,LOW);
					buttonBounceGuard = millis() - buttonLastDebounceTime > debounce;
					if ( digitalRead(9) == HIGH && buttonBounceGuard ) {
						buttonLastDebounceTime = millis();
						currentState = on;
					}
					
				  break;
		}
	}
	
