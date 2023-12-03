package main.groovy.groovuinoml.dsl;

import java.util.*;

import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class GroovuinoMLModel {
	private List<Brick> bricks;
	private List<State> states;
	private List<ConditionalTransition> transitions;
	private List<ErrorState> errors;

	private List<TemporalTransition> temporalTransitions;

	private List<TemporalState> temporalStates;

	private State initialState;


	private Binding binding;

	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<Brick>();
		this.states = new ArrayList<State>();
		this.transitions= new ArrayList<ConditionalTransition>();
		this.temporalTransitions= new ArrayList<TemporalTransition>();
		this.binding = binding;
	}

	public void createSensor(String name, Integer pinNumber) {
		Sensor sensor = new Sensor();
		sensor.setName(name);
		sensor.setPin(pinNumber);
		this.bricks.add(sensor);
		this.binding.setVariable(name, sensor);
//		System.out.println("> sensor " + name + " on pin " + pinNumber);
	}
	public void createError(ErrorState error) {
		this.states.add(error);
		this.binding.setVariable(error.getName(), error);

	}


	public void createActuator(String name, Integer pinNumber) {
		Actuator actuator = new Actuator();
		actuator.setName(name);
		actuator.setPin(pinNumber);
		this.bricks.add(actuator);
		this.binding.setVariable(name, actuator);
	}

	public void createState(String name, List<Action> actions) {
		State state = new State();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}

	public void createTemporalState(String name, List<Action> actions) {
		TemporalState state = new TemporalState();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}

	public void createTransition(State from, State to, Sensor sensor, SIGNAL value) {
		ConditionalTransition transition = new ConditionalTransition();
		transition.setNext(to);
		SingularCondition singularCondition = new SingularCondition();
		singularCondition.setSensor(sensor);
		singularCondition.setSignal(value);

		transition.setCondition(singularCondition);
		ArrayList<ConditionalTransition> transitions = new ArrayList<>();
		transitions.add(transition);
		from.setTransitions(transitions);
	}

	public void createTemporalTransition(TemporalState from, State to, Integer duration) {
		from.setDuration(duration);

		TemporalTransition transition = new TemporalTransition();
		transition.setNext(to);

		from.setTransition(transition);

		System.out.println("fromState: " + from.getName());
	}

	public void createCompositeTransition(State from, State to,ConditionalTransition transition) {
		transition.setNext(to);
		ArrayList<ConditionalTransition> transitions1 = new ArrayList<>();
		transitions1.add(transition);
		from.setTransitions(transitions1);
	}

	public void setInitialState(State state) {
		this.initialState = state;
	}

	@SuppressWarnings("rawtypes")
	public Object generateCode(String appName) {
		App app = new App();
		app.setName(appName);
		app.setBricks(this.bricks);
		app.setStates(this.states);
		app.setInitial(this.initialState);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);
		return codeGenerator.getResult();
	}
}