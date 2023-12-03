package io.github.mosser.arduinoml.kernel.samples;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class Scenario6 {

    public static void main(String[] args) throws IOException {

        // Declaring elementary bricks
        Sensor button = new Sensor();
        button.setName("button");
        button.setPin(9);

        Actuator led = new Actuator();
        led.setName("LED");
        led.setPin(12);


        // Declaring states
        TemporalState on = new TemporalState();
        on.setName("temporalOn");
        on.setDuration(1000);


        State off = new State();
        off.setName("off");

        TemporalTransition onToOff = new TemporalTransition();
        onToOff.setNext(off);

        on.setTransition(onToOff);

        // Creating actions
        Action switchLightOn = new Action();
        switchLightOn.setActuator(led);
        switchLightOn.setValue(SIGNAL.HIGH);


        Action switchLightOff = new Action();
        switchLightOff.setActuator(led);
        switchLightOff.setValue(SIGNAL.LOW);


        // Binding actions to states
        on.setActions(Arrays.asList(switchLightOn));
        off.setActions(Arrays.asList(switchLightOff));

        // Creating transitions
        ConditionalTransition offToOn = new ConditionalTransition();
        offToOn.setNext(on);
        SingularCondition exp1 = new SingularCondition();
        exp1.setSensor(button);
        exp1.setSignal(SIGNAL.HIGH);
        offToOn.setCondition(exp1);

        // Binding transitions to states
        off.setTransitions(Collections.singletonList(offToOn));

        // Building the App
        App theSimpleAlarm = new App();
        theSimpleAlarm.setName("Very simple alarm!");
        theSimpleAlarm.setBricks(Arrays.asList(button, led));
        theSimpleAlarm.setStates(Arrays.asList(on, off));
        theSimpleAlarm.setInitial(off);

        // Generating Code
        Visitor codeGenerator = new ToWiring();
        theSimpleAlarm.accept(codeGenerator);

        System.out.println(codeGenerator.getResult());
    }


}
