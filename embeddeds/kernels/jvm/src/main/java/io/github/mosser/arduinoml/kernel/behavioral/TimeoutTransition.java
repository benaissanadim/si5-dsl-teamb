package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class TimeoutTransition extends Transition implements Visitable {

    private int duration;

    private Condition condition;

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    @Override
    public void accept(Visitor visitor) {
        if (duration < 0)
            throw new IllegalArgumentException("Duration must be positive");
        visitor.visit(this);

    }

}
