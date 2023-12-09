package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class TimeOutCondition extends Condition {
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String getCondition() {
        return "";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}