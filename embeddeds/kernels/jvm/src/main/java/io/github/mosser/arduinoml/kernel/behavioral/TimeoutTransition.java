package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;

public class TimeoutTransition extends Transition implements Visitable {

    private int duration;

    private OPERATOR operator;

    private Condition condition;

    public OPERATOR getOperator() {
        return operator;
    }

    public void setOperator(OPERATOR operator) {
        this.operator = operator;
    }

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
