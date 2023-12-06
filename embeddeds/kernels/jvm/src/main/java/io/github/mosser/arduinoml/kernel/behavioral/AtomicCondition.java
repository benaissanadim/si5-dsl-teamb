package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;


public class AtomicCondition extends Condition {

    private SIGNAL value;

    private Sensor sensor;

    public SIGNAL getSignal() {
        return value;
    }

    public void setSignal(SIGNAL value) {
        this.value = value;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }


    @Override
    public String getCondition() {
        return "digitalRead(" + sensor.getPin() + ") == " + value;
    }
}
