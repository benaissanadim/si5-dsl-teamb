package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;

public class ErrorState extends State implements Visitable {
    private int errorNumber;
    private int pauseTime;
    private Actuator actuator;
    public int getErrorNumber() {
        return errorNumber;
    }
    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    public int getPauseTime() {
        return pauseTime;
    }
    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }
    public Actuator getActuator() {
        return actuator;
    }
    public void setActuator(Actuator actuator) {
        this.actuator = actuator;
    }
    @Override
    public void accept(Visitor visitor) {
        if (errorNumber < 0)
            throw new IllegalArgumentException("Error number must be positive");
        if (pauseTime < 0)
            throw new IllegalArgumentException("Pause time must be positive");
        visitor.visit(this);
    }
}