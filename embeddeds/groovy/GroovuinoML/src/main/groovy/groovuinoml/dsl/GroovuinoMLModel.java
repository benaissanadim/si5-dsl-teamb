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
	private List<InstantaneousTransition> transitions;
	private List<ErrorState> errors;

	private List<TimeoutTransition> timeoutTransitions;

	private State initialState;


	private Binding binding;

	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<Brick>();
		this.states = new ArrayList<State>();
		this.transitions= new ArrayList<InstantaneousTransition>();
		this.timeoutTransitions = new ArrayList<TimeoutTransition>();
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
		NormalState state = new NormalState();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}

	public void createTemporalState(String name, List<Action> actions) {
		NormalState state = new NormalState();
		state.setName(name);
		state.setActions(actions);
		this.states.add(state);
		this.binding.setVariable(name, state);
	}

	public void createTransition(NormalState from, NormalState to, Sensor sensor, SIGNAL value) {
		InstantaneousTransition transition = new InstantaneousTransition();
		transition.setNext(to);
		AtomicCondition atomicCondition = new AtomicCondition();
		atomicCondition.setSensor(sensor);
		atomicCondition.setSignal(value);
		transition.setCondition(atomicCondition);
		from.addTransition(transition);
	}

	public void createTemporalTransition(NormalState from, NormalState to, Integer duration, TimeoutTransition transition) {
		transition.setDuration(duration);
		transition.setNext(to);
		from.setTemporalTransition(transition);
	}

	public void createCompositeTransition(NormalState from, State to, InstantaneousTransition transition) {
		transition.setNext(to);
		from.addTransition(transition);
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