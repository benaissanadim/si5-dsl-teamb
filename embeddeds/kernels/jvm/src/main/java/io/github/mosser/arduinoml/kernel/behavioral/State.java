package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.NamedElement;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class State implements NamedElement, Visitable {

	protected String name;
	protected List<Action> actions = new ArrayList<>();
	private List<ConditionalTransition> transitions = new ArrayList<>();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
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
