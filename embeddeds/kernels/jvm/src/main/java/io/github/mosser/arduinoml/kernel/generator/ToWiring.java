package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;

/**
 * Quick and dirty visitor to support the generation of Wiring code
 */
public class ToWiring extends Visitor<StringBuffer> {
	enum PASS {ONE, TWO}


	public ToWiring() {
		this.result = new StringBuffer();
	}

	private void w(String s) {
		result.append(String.format("%s",s));
	}

	@Override
	public void visit(App app) {
		//first pass, create global vars
		context.put("pass", PASS.ONE);
		w("// Wiring code generated from an ArduinoML model\n");
		w(String.format("// Application name: %s\n", app.getName())+"\n");

		w("long debounce = 200;\n");
		w("\nenum STATE {");
		String sep ="";
		for(State state: app.getStates()){
			w(sep);
			state.accept(this);
			sep=", ";
		}
		w("};\n");
		if (app.getInitial() != null) {
			w("STATE currentState = " + app.getInitial().getName()+";\n");
		}

		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}

		//second pass, setup and loop
		context.put("pass",PASS.TWO);
		w("\nvoid setup(){\n");
		for(Brick brick: app.getBricks()){
			brick.accept(this);
		}
		w("}\n");

		w("\nvoid loop() {\n" +
				"\tswitch(currentState){\n");
		for(State state: app.getStates()){
			state.accept(this);
		}
		w("\t}\n" +
				"}");
	}

	@Override
	public void visit(Actuator actuator) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  pinMode(%d, OUTPUT); // %s [Actuator]\n", actuator.getPin(), actuator.getName()));
			return;
		}
	}


	@Override
	public void visit(Sensor sensor) {
		if(context.get("pass") == PASS.ONE) {
			w(String.format("\nboolean %sBounceGuard = false;\n", sensor.getName()));
			w(String.format("long %sLastDebounceTime = 0;\n", sensor.getName()));
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("  pinMode(%d, INPUT);  // %s [Sensor]\n", sensor.getPin(), sensor.getName()));
			return;
		}
	}

	@Override
	public void visit(ComposedCondition conditions) {
		w("(");
		int conditionsCount = conditions.getConditions().size();
		for (int i = 0; i < conditionsCount; i++) {
			Condition condition = conditions.getConditions().get(i);
			if (condition instanceof ComposedCondition)
				((ComposedCondition) condition).accept(this);
			else if (condition instanceof SingularCondition)
				((SingularCondition) condition).accept(this);

			if (i + 1 < conditionsCount) {
				if (conditions.getOperator() == OPERATOR.AND)
					w(" && ");
				else if (conditions.getOperator() == OPERATOR.OR)
					w(" || ");
				else if (conditions.getOperator() == OPERATOR.XOR)
					w("^");
				else if (conditions.getOperator() == OPERATOR.NO)
					w("!");
			}
		}
	}

	@Override
	public void visit(SingularCondition condition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format(" ( %sBounceGuard && ", condition.getSensor().getName()));
			w(String.format("digitalRead(%d) == %s )", condition.getSensor().getPin(), condition.getSignal()));
		}

	}

	@Override
	public void visit(State state) {
		if(context.get("pass") == PASS.ONE){
			w(state.getName());
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w("\t\tcase " + state.getName() + ":\n");
			for (Action action : state.getActions()) {
				action.accept(this);
			}
			if (state.getTransitions().size() == 0) {
				w("\t\t\texit(0);\n");
			}else {
				for (ConditionalTransition transition : state.getTransitions()) {
					transition.accept(this);
				}
				w("\t\tbreak;\n");
			}
		}

	}

	@Override
	public void visit(ConditionalTransition transition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			String transitionName = transition.getNext().getName();
			if(transition.getCondition() != null) {
				if (transition.getCondition() instanceof ComposedCondition) {
					w(String.format("\t\t\t%sBounceGuard = millis() - lastDebounceTime > debounce;\n",((SingularCondition)((ComposedCondition)transition.getCondition()).getConditions().get(0)).getSensor().getName()));
					w(String.format("\t\t\t%sBounceGuard = millis() - lastDebounceTime > debounce;\n",((SingularCondition)((ComposedCondition)transition.getCondition()).getConditions().get(1)).getSensor().getName()));
					w("\t\t\tif");
					((ComposedCondition) transition.getCondition()).accept(this);
					w(String.format("{\n\t\t\t\t%sLastDebounceTime = millis();\n",((SingularCondition)((ComposedCondition)transition.getCondition()).getConditions().get(0)).getSensor().getName()));
					w(String.format("{\n\t\t\t\t%sLastDebounceTime = millis();\n",((SingularCondition)((ComposedCondition)transition.getCondition()).getConditions().get(1)).getSensor().getName()));
				}else if (transition.getCondition() instanceof SingularCondition) {
					w(String.format("\t\t\t%sBounceGuard = millis() - lastDebounceTime > debounce;\n", ((SingularCondition) transition.getCondition()).getSensor().getName()));
					w("\t\t\tif");
					((SingularCondition) transition.getCondition()).accept(this);
					w(String.format("{\n\t\t\t\t%sLastDebounceTime = millis();\n", ((SingularCondition) transition.getCondition()).getSensor().getName()));
				}
				w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
				w("\t\t\t}\n");
			} else
				w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
		}
	}

	SingularCondition getBinaryDeepestUnaryExpression(ComposedCondition expression){
		Condition ex = expression.getConditions().get(0);
		if(ex instanceof SingularCondition){
			return (SingularCondition) ex;
		}
		return getBinaryDeepestUnaryExpression((ComposedCondition) ex);
	}


	@Override
	public void visit(Action action) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format("\t\t\tdigitalWrite(%d,%s);\n",action.getActuator().getPin(),action.getValue()));
			return;
		}
	}

}