package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NormalState extends State implements Visitable {
    protected List<Action> actions = new ArrayList<>();
    private List<Transition> transitions = new ArrayList<>();

    protected List<RemoteCommunication> remotes = new ArrayList<RemoteCommunication>();

    public List<RemoteCommunication> getRemoteCommunications() {
        return remotes;
    }

    public void addRemote(RemoteCommunication remote) {
        this.remotes.add(remote);
    }

    public List<RemoteCommunication> getRemotes() {
        return remotes;
    }

    public void setRemotes(List<RemoteCommunication> remotes) {
        this.remotes = remotes;
    }



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


    public void setActions(List<Action> actions) {
        this.actions = actions;
    }


    public List<TimeOutCondition> getTimeOutConditions() {
        List<TimeOutCondition> timeOutConditions = new ArrayList<>();
        for (Transition transition : this.getTransitions()) {
            Condition condition = transition.getCondition();
            if (condition instanceof TimeOutCondition) {
                timeOutConditions.add((TimeOutCondition) condition);
            }
            if(condition instanceof ComposedCondition){
                ComposedCondition composedCondition = (ComposedCondition) condition;
                timeOutConditions.addAll(composedCondition.getTimeoutConditions((ComposedCondition) condition));
            }
        }
        return timeOutConditions;
    }

    public List<RemoteCondition> getRemoteConditions() {
        List<RemoteCondition> timeOutConditions = new ArrayList<>();
        for (Transition transition : this.getTransitions()) {
            Condition condition = transition.getCondition();
            if (condition instanceof RemoteCondition) {
                timeOutConditions.add((RemoteCondition) condition);
            }
            if(condition instanceof ComposedCondition){
                ComposedCondition composedCondition = (ComposedCondition) condition;
                Condition condition1 = composedCondition.getConditions().get(0);
                Condition condition2 = composedCondition.getConditions().get(1);
                if(condition1 instanceof RemoteCondition){
                    timeOutConditions.add((RemoteCondition) condition1);
                }
                if(condition2 instanceof RemoteCondition){
                    timeOutConditions.add((RemoteCondition) condition2);
                }
            }
        }
        return timeOutConditions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}

