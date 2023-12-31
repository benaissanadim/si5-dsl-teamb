package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.OPERATOR;

import java.util.ArrayList;
import java.util.List;

public class ComposedCondition extends Condition {

    private List<Condition> conditions = new ArrayList<>();
    private OPERATOR operator;


    public void setOperator(OPERATOR operator) {
        this.operator = operator;
    }

    public void addCondition(Condition abstractCondition) {
        this.conditions.add(abstractCondition);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    public void addConditions(List<Condition> abstractCondition) {
        if(abstractCondition.size()==2){
            addCondition(abstractCondition.get(0));
            addCondition(abstractCondition.get(1));
        }else{
            System.out.println("Error: ComposedCondition can only have 2 conditions");
            System.exit(1);
        }
    }

    public List<TimeOutCondition> getTimeoutConditions(ComposedCondition composedCondition) {
        List<TimeOutCondition> timeoutConditions = new ArrayList<>();

        for (Condition condition : composedCondition.getConditions()) {
            if (condition instanceof TimeOutCondition) {
                timeoutConditions.add((TimeOutCondition) condition);
            }

            if (condition instanceof ComposedCondition) {
                timeoutConditions.addAll(getTimeoutConditions((ComposedCondition) condition));
            }
        }
        return timeoutConditions;
    }


    public OPERATOR getOperator() {
        return operator;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    @Override
    public String getCondition() {
        switch (operator) {
            case AND:
                return String.format("(%s && %s)", conditions.get(0).getCondition(), conditions.get(1).getCondition());
            case OR:
                return String.format("(%s || %s)", conditions.get(0).getCondition(), conditions.get(1).getCondition());
            default:
                throw new IllegalArgumentException("Other operator are not yet supported");
        }
    }
}