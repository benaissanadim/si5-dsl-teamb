package main.groovy.groovuinoml.dsl;

import java.util.*;

import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.*;

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

	public void createCompositeTransition(State from, State to, List<SingularCondition> conditions) {
		ConditionalTransition transition = new ConditionalTransition();
		System.out.println("CONDITIONS : " + conditions.size());
		transition.setNext(to);
		ComposedCondition composedCondition = new ComposedCondition();
		if(conditions.size()>1){
			for(int i =0 ; i< conditions.size() -1; i+=2){
				composedCondition.addConditions(Arrays.asList(conditions.get(i),conditions.get(i+1)));
				composedCondition.setOperator(OPERATOR.AND);
				transition.setCondition(composedCondition);

			}
		}else{
			if(conditions.size() == 1)
				transition.setCondition(conditions.get(0));

		}
		ArrayList<ConditionalTransition> transitions1 = new ArrayList<>();
		transitions1.add(transition);
		from.setTransitions(transitions1);
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
