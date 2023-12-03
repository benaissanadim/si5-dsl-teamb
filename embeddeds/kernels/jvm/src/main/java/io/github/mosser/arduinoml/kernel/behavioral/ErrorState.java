package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;

public class ErrorState extends State{
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
        visitor.visit(this);
    }
}