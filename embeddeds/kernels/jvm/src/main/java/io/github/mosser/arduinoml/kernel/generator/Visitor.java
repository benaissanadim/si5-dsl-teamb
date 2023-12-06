package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;
import io.github.mosser.arduinoml.kernel.App;

import java.util.HashMap;
import java.util.Map;

public abstract class Visitor<T> {

	public abstract void visit(App app);
	public abstract void visit(NormalState state);

	public abstract void visit(InstantaneousTransition transition);
	public abstract void visit(Action action);

	public abstract void visit(TimeoutTransition transition);

	public abstract void visit(ErrorState state);
	public abstract void visit(Actuator actuator);
	public abstract void visit(Sensor sensor);
	public abstract void visit(ComposedCondition condition);
	public abstract void visit(AtomicCondition condition);




	/***********************
	 ** Helper mechanisms **
	 ***********************/

	protected Map<String,Object> context = new HashMap<>();

	protected T result;

	public T getResult() {
		return result;
	}

}
