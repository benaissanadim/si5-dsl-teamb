package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class SingularCondition extends Condition {

    private SIGNAL signal;
    protected Sensor sensor;

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSignal(SIGNAL signal) {
        this.signal = signal;
    }

    public SIGNAL getSignal() {
        return signal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
