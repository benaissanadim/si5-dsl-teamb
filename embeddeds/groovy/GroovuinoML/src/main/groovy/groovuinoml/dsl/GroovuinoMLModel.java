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

	private State initialState;


	private Binding binding;

	public GroovuinoMLModel(Binding binding) {
		this.bricks = new ArrayList<Brick>();
		this.states = new ArrayList<State>();
		this.transitions= new ArrayList<ConditionalTransition>();
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

	public void createTransition(State from, State to, Sensor sensor, SIGNAL value) {
		ConditionalTransition transition = new ConditionalTransition();
		transition.setNext(to);
		SingularCondition singularCondition = new SingularCondition();
		singularCondition.setSensor(sensor);
		singularCondition.setValue(value);
		transition.setCondition(singularCondition);
		ArrayList<ConditionalTransition> transitions = new ArrayList<>();
		transitions.add(transition);
		from.setTransitions(transitions);
	}

	public void createCompositeTransition(State from, State to, List<Sensor> sensor, List<SIGNAL> value) {
		ConditionalTransition transition = new ConditionalTransition();
		System.out.println("sensor : " + sensor.size());
		transition.setNext(to);
		ComposedCondition composedCondition = new ComposedCondition();
		if(sensor.size() != value.size()){
			System.out.println("Error : sensor and value size are not equal");
			return;
		}
		if(sensor.size() == 0){
			System.out.println("Error : sensor and value size are 0");
			return;
		}
		if(sensor.size()>1){
			for(int i =0 ; i< sensor.size() -1; i+=2){
				SingularCondition singularCondition = new SingularCondition();
				singularCondition.setSensor(sensor.get(i));
				singularCondition.setValue(value.get(i));
				SingularCondition singularCondition2 = new SingularCondition();
				singularCondition.setSensor(sensor.get(i+1));
				singularCondition.setValue(value.get(i+1));

				composedCondition.addConditions(Arrays.asList(singularCondition,singularCondition2));
			}
		}else{
			SingularCondition singularCondition = new SingularCondition();
			singularCondition.setSensor(sensor.get(0));
			singularCondition.setValue(value.get(0));
			transition.setCondition(singularCondition);
		}

		transition.setCondition(composedCondition);
		ArrayList<ConditionalTransition> transitions = new ArrayList<>();
		transitions.add(transition);
		from.setTransitions(transitions);
		this.transitions.add(transition);
		this.binding.setVariable("transition", transition);
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
		app.setTransitions(this.transitions);
		app.setInitial(this.initialState);
		Visitor codeGenerator = new ToWiring();
		app.accept(codeGenerator);

		return codeGenerator.getResult();
	}
}
