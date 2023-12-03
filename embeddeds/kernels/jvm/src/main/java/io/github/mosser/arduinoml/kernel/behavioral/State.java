package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.NamedElement;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class State implements NamedElement, Visitable {

	private String name;
	private List<ConditionalTransition> transitions = new ArrayList<>();
	private List<Action> actions = new ArrayList<>();
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
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

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}