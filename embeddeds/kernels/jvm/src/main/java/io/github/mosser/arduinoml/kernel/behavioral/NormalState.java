package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NormalState extends State implements Visitable {
    protected List<Action> actions = new ArrayList<>();
    private List<Transition> transitions = new ArrayList<>();


    public void addTransition(Transition transition) {
        this.transitions.add(transition);
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<TimeoutTransition> getTimeoutTransitions() {
        List<TimeoutTransition> transitions = new ArrayList<>();
        for (Transition transition : this.transitions) {
            if (transition instanceof TimeoutTransition) {
                transitions.add((TimeoutTransition) transition);
            }
        }
        return transitions;
    }

    public List<InstantaneousTransition> getInstantaneousTransitions() {
        List<InstantaneousTransition> transitions = new ArrayList<>();
        for (Transition transition : this.transitions) {
            if (transition instanceof InstantaneousTransition) {
                transitions.add((InstantaneousTransition) transition);
            }
        }
        return transitions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void accept(Visitor visitor) {
        List<Transition> transitions = this.getTransitions().stream().filter(t -> t instanceof TimeoutTransition).filter(t2 -> ((TimeoutTransition) t2).getCondition() == null).collect(java.util.stream.Collectors.toList());
        if (transitions.size() > 1)
            throw new IllegalArgumentException("Only one timeout transition without condition is allowed");
        if (transitions.size() == 1) {
            TimeoutTransition timeoutTransition = (TimeoutTransition) transitions.get(0);
            List<Transition> transitions2 = this.getTransitions().stream().filter(t -> t instanceof TimeoutTransition).filter(t2 -> ((TimeoutTransition) t2).getCondition() != null).filter(t3 -> ((TimeoutTransition) t3).getDuration()
                    > timeoutTransition.getDuration()).collect(java.util.stream.Collectors.toList());
            if (transitions2.size() > 0)
                Logger.getLogger("TimeoutTransition").warning("Timeout transition is never reached");
        }
        visitor.visit(this);
    }

}

