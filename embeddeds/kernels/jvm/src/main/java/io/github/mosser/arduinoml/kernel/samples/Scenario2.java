package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.io.IOException;
import java.util.Arrays;

public class Scenario2 {

	public static void main(String[] args) throws IOException {

		// Declaring elementary bricks
		Sensor button1 = new Sensor();
		button1.setName("button1");
		button1.setPin(9);

		Sensor button2 = new Sensor();
		button2.setName("button2");
		button2.setPin(10);

		Actuator buzzer = new Actuator();
		buzzer.setName("BUZZER");
		buzzer.setPin(11);

		// Declaring states
		State on = new State();
		on.setName("on");

		State off = new State();
		off.setName("off");

		// Creating actions
		Action triggerBuzzer = new Action();
		triggerBuzzer.setActuator(buzzer);
		triggerBuzzer.setValue(SIGNAL.HIGH);

		Action stopSound = new Action();
		stopSound.setActuator(buzzer);
		stopSound.setValue(SIGNAL.LOW);

		// Binding actions to states
		on.setActions(Arrays.asList(triggerBuzzer));
		off.setActions(Arrays.asList(stopSound));

		// Creating transitions
		ConditionalTransition off2On = new ConditionalTransition();
		off2On.setNext(on);

		SingularCondition exp1 = new SingularCondition();
		exp1.setSensor(button1);
		exp1.setValue(SIGNAL.HIGH);
		SingularCondition exp2 = new SingularCondition();
		exp2.setSensor(button2);
		exp2.setValue(SIGNAL.HIGH);
		ComposedCondition exp3 = new ComposedCondition();
		exp3.addConditions(Arrays.asList(exp1, exp2));
		exp3.setOperator(OPERATOR.AND);

		SingularCondition exp11 = new SingularCondition();
		exp11.setSensor(button1);
		exp11.setValue(SIGNAL.LOW);
		SingularCondition exp22 = new SingularCondition();
		exp22.setSensor(button2);
		exp22.setValue(SIGNAL.LOW);
		ComposedCondition expr33 = new ComposedCondition();
		expr33.addConditions(Arrays.asList(exp11, exp22));
		expr33.setOperator(OPERATOR.OR);

		off2On.setCondition(exp3);

		ConditionalTransition on2Off = new ConditionalTransition();
		on2Off.setNext(off);

		on2Off.setCondition(expr33);

		// Binding transitions to states
		// Binding transitions to states
		off.setTransitions(Arrays.asList(off2On));
		on.setTransitions(Arrays.asList(on2Off));

		// Building the App
		App theDualCheckAlarm = new App();
		theDualCheckAlarm.setName("Dual-check alarm!");
		theDualCheckAlarm.setBricks(Arrays.asList(button1, button2, buzzer));
		theDualCheckAlarm.setStates(Arrays.asList(on, off));
		theDualCheckAlarm.setInitial(off);

		// Generating Code
		Visitor codeGenerator = new ToWiring();
		theDualCheckAlarm.accept(codeGenerator);

		System.out.println(codeGenerator.getResult());

	}

}
