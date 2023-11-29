package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;

import java.util.ArrayList;
import java.util.List;

public class ComposedCondition extends Condition {

    private List<Condition> conditions = new ArrayList<>();

    public void addCondition(Condition abstractCondition) {
        this.conditions.add(abstractCondition);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public List<Condition> getConditions() {
        return conditions;
    }
}
