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
		Actuator led = new Actuator();
		led.setName("led");
		led.setPin(11);

		Actuator buzzer = new Actuator();
		buzzer.setName("BUZZER");
		buzzer.setPin(12);

		Sensor button1 = new Sensor();
		button1.setName("button1");
		button1.setPin(9);

		Sensor button2 = new Sensor();
		button2.setName("button2");
		button2.setPin(10);

		// Declaring states
		State onButtonPressed = new State();
		onButtonPressed.setName("OneButtonPressed");

		State twoButtonPressed = new State();
		twoButtonPressed.setName("TwoButtonPressed");

		State off = new State();
		off.setName("off");

		// Creating actions
		Action triggerBuzzer = new Action();
		triggerBuzzer.setActuator(buzzer);
		triggerBuzzer.setValue(SIGNAL.HIGH);

		Action triggerLed = new Action();
		triggerLed.setActuator(led);
		triggerLed.setValue(SIGNAL.HIGH);

		Action stopLed = new Action();
		stopLed.setActuator(led);
		stopLed.setValue(SIGNAL.LOW);

		Action stopSound = new Action();
		stopSound.setActuator(buzzer);
		stopSound.setValue(SIGNAL.LOW);

		// Binding actions to states
		twoButtonPressed.setActions(Arrays.asList(triggerBuzzer, triggerLed));
		off.setActions(Arrays.asList(stopSound, stopLed));
		onButtonPressed.setActions(Arrays.asList(triggerLed, stopSound));
		SingularCondition exp1H = new SingularCondition();
		exp1H.setSensor(button1);
		exp1H.setSignal(SIGNAL.HIGH);

		SingularCondition exp2H = new SingularCondition();
		exp2H.setSensor(button2);
		exp2H.setSignal(SIGNAL.HIGH);

		ComposedCondition andH = new ComposedCondition();
		andH.addCondition(exp1H);
		andH.addCondition(exp2H);
		andH.setOperator(OPERATOR.AND);

		SingularCondition exp1L = new SingularCondition();
		exp1L.setSensor(button1);
		exp1L.setSignal(SIGNAL.LOW);

		SingularCondition exp2L = new SingularCondition();
		exp2L.setSensor(button2);
		exp2L.setSignal(SIGNAL.LOW);

		ComposedCondition andL = new ComposedCondition();
		andL.addCondition(exp1L);
		andL.addCondition(exp2L);
		andL.setOperator(OPERATOR.AND);

		ComposedCondition xor = new ComposedCondition();
		xor.addCondition(exp1L);
		xor.addCondition(exp2L);
		xor.setOperator(OPERATOR.XOR);
		// Creating transitions

		ConditionalTransition onButtonTo2Buttons = new ConditionalTransition();
		onButtonTo2Buttons.setNext(twoButtonPressed);
		onButtonTo2Buttons.setCondition(andH);

		ConditionalTransition onButtonToOff = new ConditionalTransition();
		onButtonToOff.setNext(off);
		onButtonToOff.setCondition(andL);

		ConditionalTransition offTo2 = new ConditionalTransition();
		offTo2.setNext(twoButtonPressed);
		offTo2.setCondition(andH);

		ConditionalTransition offTo1 = new ConditionalTransition();
		offTo1.setCondition(xor);
		offTo1.setNext(onButtonPressed);

		ConditionalTransition twoToOFF = new ConditionalTransition();
		twoToOFF.setNext(off);
		twoToOFF.setCondition(andH);

		ConditionalTransition twoTo1 = new ConditionalTransition();
		twoTo1.setCondition(xor);
		twoTo1.setNext(onButtonPressed);

		// Binding transitions to
		List<ConditionalTransition> transitionListOff = new ArrayList<>();
		transitionListOff.add(offTo1);
		transitionListOff.add(offTo2);
		off.setTransitions(transitionListOff);

		List<ConditionalTransition> transitionListOne = new ArrayList<>();
		transitionListOne.add(onButtonTo2Buttons);
		transitionListOne.add(onButtonToOff);
		onButtonPressed.setTransitions(transitionListOne);

		List<ConditionalTransition> transitionListTwo = new ArrayList<>();
		transitionListOne.add(twoTo1);
		transitionListOne.add(twoToOFF);
		twoButtonPressed.setTransitions(transitionListTwo);

		// Building the App
		App theDualCheckAlarm = new App();
		theDualCheckAlarm.setName("Dual-check alarm!");
		theDualCheckAlarm.setBricks(Arrays.asList(button1, button2, buzzer));
		theDualCheckAlarm.setStates(Arrays.asList(twoButtonPressed, off, onButtonPressed));
		theDualCheckAlarm.setInitial(off);

		// Generating Code
		Visitor codeGenerator = new ToWiring();
		theDualCheckAlarm.accept(codeGenerator);

		System.out.println(codeGenerator.getResult());

	}

}