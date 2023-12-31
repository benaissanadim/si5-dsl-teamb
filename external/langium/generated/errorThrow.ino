
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
bool startTimer = false;
long startTime; // Used for temporal transitions
enum STATE {on, off, error};

STATE currentState = off;

bool button1BounceGuard = false;
long button1LastDebounceTime = 0;



bool button2BounceGuard = false;
long button2LastDebounceTime = 0;



	void setup(){
        Serial.begin(9600);
		pinMode(9, INPUT); // button1 [Sensor]
		pinMode(10, INPUT); // button2 [Sensor]
		pinMode(11, OUTPUT); // led [Actuator]
	}
	void loop() {
              char incomingChar = Serial.read(); // Read the incoming character

			switch(currentState){

				case on:
					digitalWrite(11,HIGH);
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					button1BounceGuard = static_cast<long>(millis() - button1LastDebounceTime) > debounce;
					button2BounceGuard = static_cast<long>(millis() - button2LastDebounceTime) > debounce;
					              
					if ( ( ( digitalRead(9) == LOW && button1BounceGuard ) && ( digitalRead(10) == LOW && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = off;
                        startTimer = false;
					}
					if ( ( ( digitalRead(9) == HIGH && button1BounceGuard ) && ( digitalRead(10) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = error;
                        startTimer = false;
					}
					

				    break;
            
				case off:
					digitalWrite(11,LOW);
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					button1BounceGuard = static_cast<long>(millis() - button1LastDebounceTime) > debounce;
					button2BounceGuard = static_cast<long>(millis() - button2LastDebounceTime) > debounce;
					              
					if ( ( ( digitalRead(9) == HIGH && button1BounceGuard ) ^ ( digitalRead(10) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = on;
                        startTimer = false;
					}
					if ( ( ( digitalRead(9) == HIGH && button1BounceGuard ) && ( digitalRead(10) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = error;
                        startTimer = false;
					}
					

				    break;
            
				case error:
					// Blink the error actuator
					for (int i = 0; i < 5; i++) {
						digitalWrite(11, HIGH); // turn the error actuator on
						delay(500); // wait for 500ms
						digitalWrite(11, LOW); // turn the error actuator off
						delay(500); // wait for 500ms
					}
					delay(3 * 1000);

				    break;
            
		}
	}
	
