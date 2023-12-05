package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class NormalState extends State implements Visitable {
    protected List<Action> actions = new ArrayList<>();
    private List<ConditionalTransition> transitions = new ArrayList<>();

    private TemporalTransition temporalTransition;

    public TemporalTransition getTemporalTransition() {
        return temporalTransition;
    }

    public void setTemporalTransition(TemporalTransition temporalTransition) {
        this.temporalTransition = temporalTransition;
    }

    public void addTransition(ConditionalTransition transition) {
        this.transitions.add(transition);
    }

    public List<ConditionalTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<ConditionalTransition> transitions) {
        this.transitions = transitions;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
