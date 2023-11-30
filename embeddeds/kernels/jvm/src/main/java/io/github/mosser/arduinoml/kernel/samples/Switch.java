package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.ConditionalTransition;
import io.github.mosser.arduinoml.kernel.behavioral.SingularCondition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.io.IOException;
import java.util.Arrays;

public class Switch {

	public static void main(String[] args) throws IOException {

		// Declaring elementary bricks
		Sensor button = new Sensor();
		button.setName("button");
		button.setPin(9);

		Actuator led = new Actuator();
		led.setName("LED");
		led.setPin(12);

		Actuator buzzer = new Actuator();
		buzzer.setName("BUZZER");
		buzzer.setPin(6);

		// Declaring states
		State on = new State();
		on.setName("on");

		State off = new State();
		off.setName("off");

		// Creating actions
		Action switchLightOn = new Action();
		switchLightOn.setActuator(led);
		switchLightOn.setValue(SIGNAL.HIGH);

		Action switchBuzzerOn = new Action();
		switchBuzzerOn.setActuator(buzzer);
		switchBuzzerOn.setValue(SIGNAL.HIGH);

		Action switchLightOff = new Action();
		switchLightOff.setActuator(led);
		switchLightOff.setValue(SIGNAL.LOW);

		Action switchBuzzerOff = new Action();
		switchBuzzerOff.setActuator(buzzer);
		switchBuzzerOff.setValue(SIGNAL.LOW);

		// Binding actions to states
		on.setActions(Arrays.asList(switchLightOn, switchBuzzerOn));
		off.setActions(Arrays.asList(switchLightOff, switchBuzzerOff));

		// Creating transitions
		ConditionalTransition t1 = new ConditionalTransition();
		t1.setNext(on);
		SingularCondition exp1 = new SingularCondition();
		exp1.setSensor(button);
		exp1.setValue(SIGNAL.HIGH);
		t1.setCondition(exp1);

		ConditionalTransition t2 = new ConditionalTransition();
		t2.setNext(off);
		SingularCondition exp2 = new SingularCondition();
		exp2.setSensor(button);
		exp2.setValue(SIGNAL.LOW);
		t2.setCondition(exp2);

		// Binding transitions to states
		off.setTransitions(Arrays.asList(t1));
		on.setTransitions(Arrays.asList(t2));

		// Building the App
		App theSimpleAlarm = new App();
		theSimpleAlarm.setName("Very simple alarm!");
		theSimpleAlarm.setBricks(Arrays.asList(button, led, buzzer));
		theSimpleAlarm.setStates(Arrays.asList(on, off));
		theSimpleAlarm.setInitial(off);

		// Generating Code
		Visitor codeGenerator = new ToWiring();
		theSimpleAlarm.accept(codeGenerator);

		System.out.println(codeGenerator.getResult());
	}

}