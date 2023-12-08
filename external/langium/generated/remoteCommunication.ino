
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
bool startTimer = false;
long startTime; // Used for temporal transitions
enum STATE {on, off};

STATE currentState = off;

bool buttonBounceGuard = false;
long buttonLastDebounceTime = 0;



bool thermicSensorBounceGuard = false;
long thermicSensorLastDebounceTime = 0;



	void setup(){
        Serial.begin(9600);
		pinMode(9, INPUT); // button [Sensor]
		pinMode(10, INPUT); // thermicSensor [Sensor]
		pinMode(11, OUTPUT); // led [Actuator]
	}
	void loop() {
              char incomingChar = Serial.read(); // Read the incoming character

			switch(currentState){

				case on:
					digitalWrite(11,HIGH);              
                    Serial.println(analogRead(10));
        
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					if ( digitalRead(9) == HIGH && buttonBounceGuard ) {
						buttonLastDebounceTime = millis();
						currentState = off;
                        startTimer = false;
					}
					

				    break;
            
				case off:
					digitalWrite(11,LOW);
                    if (startTimer == false) {
                      startTime = millis();
                      startTimer = true;
                    }
                        
					buttonBounceGuard = static_cast<long>(millis() - buttonLastDebounceTime) > debounce;
					if ( ( ( digitalRead(9) == HIGH && buttonBounceGuard ) || ( incomingChar == 'A' ) ) ) {
						buttonLastDebounceTime = millis();
						currentState = on;
                        startTimer = false;
					}
					

				    break;
            
		}
	}
	
