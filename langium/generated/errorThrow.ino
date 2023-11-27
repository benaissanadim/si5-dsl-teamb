
//Wiring code generated from an ArduinoML model
// Application name: foo

long debounce = 200;
enum STATE {on, off, error};

STATE currentState = off;

bool button1BounceGuard = false;
long button1LastDebounceTime = 0;

            

bool button2BounceGuard = false;
long button2LastDebounceTime = 0;

            

	void setup(){
		pinMode(9, INPUT); // button1 [Sensor]
		pinMode(10, INPUT); // button2 [Sensor]
		pinMode(11, OUTPUT); // led [Actuator]
		pinMode(13, OUTPUT); // ErrorLed [Actuator]
	}
	void loop() {
			switch(currentState){

				case on:
					digitalWrite(11,HIGH);
					button1BounceGuard = millis() - button1LastDebounceTime > debounce;
					button2BounceGuard = millis() - button2LastDebounceTime > debounce;
					
					if ( ( ( digitalRead(button1.inputPin) == LOW && button1BounceGuard ) && ( digitalRead(button2.inputPin) == LOW && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = off;
					}
					if ( ( ( digitalRead(button1.inputPin) == HIGH && button1BounceGuard ) && ( digitalRead(button2.inputPin) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = error;
					}
					
				break;
				case off:
					digitalWrite(11,LOW);
					digitalWrite(13,LOW);
					button1BounceGuard = millis() - button1LastDebounceTime > debounce;
					button2BounceGuard = millis() - button2LastDebounceTime > debounce;
					
					if ( ( ( digitalRead(button1.inputPin) == HIGH && button1BounceGuard ) ^ ( digitalRead(button2.inputPin) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = on;
					}
					if ( ( ( digitalRead(button1.inputPin) == HIGH && button1BounceGuard ) && ( digitalRead(button2.inputPin) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = error;
					}
					
				break;
				case error:
					// Blink the error actuator
					for (int i = 0; i < 3; i++) {
						digitalWrite(13, HIGH); // turn the error actuator on
						delay(500); // wait for 500ms
						digitalWrite(13, LOW); // turn the error actuator off
						delay(500); // wait for 500ms
					}
					delay(3 * 1000);
					button1BounceGuard = millis() - button1LastDebounceTime > debounce;
					button2BounceGuard = millis() - button2LastDebounceTime > debounce;
					
					if ( ( ( digitalRead(button1.inputPin) == LOW && button1BounceGuard ) && ( digitalRead(button2.inputPin) == LOW && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = off;
					}
					if ( ( ( digitalRead(button1.inputPin) == HIGH && button1BounceGuard ) ^ ( digitalRead(button2.inputPin) == HIGH && button2BounceGuard ) ) ) {
						button1LastDebounceTime = millis();
						button2LastDebounceTime = millis();
						currentState = on;
					}
					
				break;
		}
	}
	
