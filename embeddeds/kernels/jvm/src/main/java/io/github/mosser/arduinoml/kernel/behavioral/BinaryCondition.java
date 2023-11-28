package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;

import java.util.ArrayList;
import java.util.List;

public class BinaryCondition implements Condition {

    private List<Condition> conditions = new ArrayList<>();

    private OPERATOR operator;

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> expressions) {
        if (expressions.size() < 2) {
            throw new IllegalArgumentException("BinaryExpression have 2 or more unary expressions");
        }
        this.conditions = expressions;
    }

    public void setOperator(OPERATOR operator) {
        this.operator = operator;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getCondition() {
        String c1 = conditions.get(0).getCondition();
        String c2 = conditions.get(1).getCondition();
        switch (operator) {
            case AND:
                return String.format("(%s && %s)", c1, c2);
            case OR:
                return String.format("(%s || %s)", c1, c2);
            default:
                throw new IllegalArgumentException("Operator not supported");
        }
    }
}
