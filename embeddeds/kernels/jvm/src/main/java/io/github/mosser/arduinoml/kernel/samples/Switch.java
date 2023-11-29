package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Switch {

	public static void main(String[] args) {

		// Declaring elementary bricks
		Sensor button = new Sensor();
		button.setName("button");
		button.setPin(9);

		Actuator led = new Actuator();
		led.setName("LED");
		led.setPin(12);

		Actuator buzzer = new Actuator();
		buzzer.setName("BUZZER");
		buzzer.setPin(11);

		// Declaring states
		State on = new State();
		on.setName("on");

		State off = new State();
		off.setName("off");

		// Creating actions
		Action switchTheLightOn = new Action();
		switchTheLightOn.setActuator(led);
		switchTheLightOn.setValue(SIGNAL.HIGH);

		Action switchTheBuzzerOn = new Action();
		switchTheBuzzerOn.setActuator(buzzer);
		switchTheBuzzerOn.setValue(SIGNAL.HIGH);

		Action switchTheLightOff = new Action();
		switchTheLightOff.setActuator(led);
		switchTheLightOff.setValue(SIGNAL.LOW);

		Action switchTheBuzzerOff = new Action();
		switchTheBuzzerOff.setActuator(buzzer);
		switchTheBuzzerOff.setValue(SIGNAL.LOW);

		// Binding actions to states
		on.setActions(Arrays.asList(switchTheLightOn,switchTheBuzzerOn));
		off.setActions(Arrays.asList(switchTheLightOff, switchTheBuzzerOff));

		// Creating transitions
		ConditionalTransition on2off = new ConditionalTransition();
		on2off.setNext(off);
		//on2off.setSensor(button);
		//on2off.setValue(SIGNAL.HIGH);

		ConditionalTransition off2on = new ConditionalTransition();
		off2on.setNext(on);
		//off2on.setSensor(button);
		//off2on.setValue(SIGNAL.HIGH);
		List<ConditionalTransition> transitions = new ArrayList<>();
		transitions.add(on2off);
		on.setTransitions(transitions);
		// Binding transitions to states
		List<ConditionalTransition> transitions1 = new ArrayList<>();
		transitions1.add(off2on);
		off.setTransitions(transitions);


		// Building the App
		App theSwitch = new App();
		theSwitch.setName("Switch!");
		theSwitch.setBricks(Arrays.asList(button, led, buzzer ));
		theSwitch.setStates(Arrays.asList(on, off));
		theSwitch.setInitial(off);

		// Generating Code
		Visitor codeGenerator = new ToWiring();
		theSwitch.accept(codeGenerator);

		// Printing the generated code on the console
		System.out.println(codeGenerator.getResult());
	}

}