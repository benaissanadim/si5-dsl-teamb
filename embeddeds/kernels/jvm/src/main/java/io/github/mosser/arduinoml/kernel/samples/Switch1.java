package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.Transition;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.mosser.arduinoml.kernel.behavioral.*;

import io.github.mosser.arduinoml.kernel.structural.OPERATOR;


import java.io.IOException;
import java.util.List;


public class Switch1 {

	public static void main(String[] args) throws IOException {

		// Declaring elementary bricks
		Sensor button1 = new Sensor();
		button1.setName("button1");
		button1.setPin(9);

		Sensor button3 = new Sensor();
		button3.setName("button3");
		button3.setPin(11);

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
		Transition off2On = new Transition();
		off2On.setNext(on);
		SingularCondition exp1 = new SingularCondition();
		exp1.setSensor(button1);
		exp1.setSignal(SIGNAL.HIGH);

		SingularCondition exp2 = new SingularCondition();
		exp2.setSensor(button2);
		exp2.setSignal(SIGNAL.HIGH);

		ComposedCondition exp3 = new ComposedCondition();
		exp3.addCondition(exp1);
		exp3.addCondition(exp2);
		exp3.setOperator(OPERATOR.AND);

		SingularCondition expr4 = new SingularCondition();
		expr4.setSensor(button3);
		expr4.setSignal(SIGNAL.HIGH);

		ComposedCondition expr5 = new ComposedCondition();
		expr5.addCondition(exp3);
		expr5.addCondition(expr4);
		expr5.setOperator(OPERATOR.OR);

		off2On.setCondition(expr5);

		Transition on2Off = new Transition();
		on2Off.setNext(off);

		SingularCondition exp4 = new SingularCondition();
		exp4.setSensor(button1);
		exp4.setSignal(SIGNAL.LOW);

		SingularCondition exp5 = new SingularCondition();
		exp5.setSensor(button2);
		exp5.setSignal(SIGNAL.LOW);

		ComposedCondition exp6 = new ComposedCondition();
		exp6.addCondition(exp4);
		exp6.addCondition(exp5);
		exp6.setOperator(OPERATOR.OR);

		on2Off.setCondition(exp6);

		// Binding transitions to states
		List<Transition> transitions = new ArrayList<>();
		transitions.add(off2On);
		off.setTransitions(transitions);
		List<Transition> transitions1 = new ArrayList<>();
		transitions1.add(on2Off);
		on.setTransitions(transitions1);

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