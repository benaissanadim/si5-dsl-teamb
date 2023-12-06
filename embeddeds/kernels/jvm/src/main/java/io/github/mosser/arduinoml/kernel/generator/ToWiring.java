package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.structural.*;

import java.util.HashSet;
import java.util.List;


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
		boolean hasTemporalState = false;
		w("long debounce = 200;\n");
		for (State state : app.getStates()) {
			if (state instanceof NormalState && !((NormalState) state).getTimeoutTransitions().isEmpty()) {
				hasTemporalState = true;
			}
		}
		if (hasTemporalState) {
			w("long startTime;\n");
		}
		w("\nenum STATE {");
		String sep ="";
		for(State state: app.getStates()){
			w(sep);
			if(state instanceof ErrorState){
				((ErrorState) state).accept(this);
			}else
				((NormalState) state).accept(this);
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

			if(state instanceof ErrorState){
				((ErrorState) state).accept(this);
			}else
				((NormalState) state).accept(this);
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
			else if (condition instanceof AtomicCondition)
				((AtomicCondition) condition).accept(this);

			if (i + 1 < conditionsCount) {
				getOperator(conditions.getOperator());
			}
		}
		w(")");

	}

	public void getOperator(OPERATOR operator){
		switch (operator) {
		case AND:
			w(" && ");
			break;
		case OR:
			w(" || ");
			break;
		case XOR:
			w("^");
			break;
		case NOT:
			w("!");
			break;
		}
	}

	@Override
	public void visit(AtomicCondition condition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w(String.format(" ( %sBounceGuard && ", condition.getSensor().getName()));
			w(String.format("digitalRead(%d) == %s )", condition.getSensor().getPin(), condition.getSignal()));
		}

	}
	@Override
	public void visit(ErrorState state) {
		if(context.get("pass") == PASS.ONE){
			w(state.getName());
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			w("\t\tcase " + state.getName() + ":\n");
			w(String.format("\t\t\tfor (int i = 0; i < %d; i++) {\n", state.getErrorNumber()));
			w(String.format("\t\t\t\tdigitalWrite(%d, HIGH);\n", state.getActuator().getPin()));
			w("\t\t\t\tdelay(500);\n");
			w(String.format("\t\t\t\tdigitalWrite(%d, LOW);\n", state.getActuator().getPin()));
			w("\t\t\t\tdelay(500);\n");
			w("\t\t\t}\n");
			w(String.format("\t\t\tdelay(%d * 1000);\n", state.getPauseTime()));
			w("\t\t\texit(0);\n");
		}

	}


	@Override
	public void visit(NormalState state) {
		if(context.get("pass") == PASS.ONE){
			w(state.getName());
			return;
		}
		if(context.get("pass") == PASS.TWO) {

			w("\t\tcase " + state.getName() + ":\n");
			for (Action action : state.getActions()) {
				action.accept(this);
			}
			printConditionBegin(state);

			if (!state.getTimeoutTransitions().isEmpty()) {
				w(String.format("\t\t\tstartTime = millis();\n"));
				w(String.format("\t\t\twhile("));
				int i = 0;
				for(TimeoutTransition t : state.getTimeoutTransitions()){
					i++;
					w(String.format("( millis() - startTime < %d", t.getDuration()));
					if(t.getCondition() != null) {
						w(" ");
						getOperator(t.getOperator());
						w(" !");
						t.getCondition().accept(this);
					}
					w(")");
					if(i < state.getTimeoutTransitions().size()) w(" || ");
				}
				w("){\n");
				for (InstantaneousTransition transition : state.getInstantaneousTransitions()) {
					transition.accept(this);
				}
				w("\t\t\tdelayMicroseconds(100);\n");
				w("\t\t\t}\n");
				if(state.getTimeoutTransitions().size() ==1 ){
					w(String.format("\t\t\tcurrentState = %s;\n",state.getTimeoutTransitions().get(0).getNext().getName()));
				}else if(state.getTimeoutTransitions().size() > 1){
					for(TimeoutTransition t : state.getTimeoutTransitions()){
						w(String.format("\t\t\tif( millis() - startTime >= %d", t.getDuration()));
						if(t.getCondition() != null) {
							w(" ");
							getOperator(t.getOperator());
							w(" ");
							t.getCondition().accept(this);
						}
						w(String.format("){\n\t\t\t\tcurrentState = %s;\n\t\t\t}\n",t.getNext().getName()));
					}
				}
				w("\t\t\tbreak;\n");
				} else {
					for (InstantaneousTransition transition : state.getInstantaneousTransitions()) {
							transition.accept(this);
					}
					w("\t\t\tbreak;\n");
				}
		}
	}

	void printConditionBegin(NormalState state) {
		HashSet<String> names = new HashSet<>();
		for (InstantaneousTransition transition : state.getInstantaneousTransitions()) {
			if(transition.getCondition() instanceof ComposedCondition){
				for (Condition condition : ((ComposedCondition) transition.getCondition()).getConditions()) {
				if (transition.getCondition() != null) {
					if (transition.getCondition() instanceof ComposedCondition) {
						ComposedCondition composedCondition = (ComposedCondition) transition.getCondition();
						String nameToAdd1 = ((AtomicCondition) composedCondition.getConditions().get(0)).getSensor().getName();
						String nameToAdd2 = ((AtomicCondition) composedCondition.getConditions().get(1)).getSensor().getName();
						names.add(nameToAdd1);
						names.add(nameToAdd2);
					}
				}
			}
			}
		}
		for (TimeoutTransition transition : state.getTimeoutTransitions()) {
			if (transition.getCondition() != null) {
				if (transition.getCondition() instanceof ComposedCondition) {
					ComposedCondition composedCondition = (ComposedCondition) transition.getCondition();
					String nameToAdd1 = ((AtomicCondition) composedCondition.getConditions().get(0)).getSensor().getName();
					String nameToAdd2 = ((AtomicCondition) composedCondition.getConditions().get(1)).getSensor().getName();
					names.add(nameToAdd1);
					names.add(nameToAdd2);
				}
				else if (transition.getCondition() instanceof AtomicCondition) {
					String nameToAdd = ((AtomicCondition) transition.getCondition()).getSensor().getName();
					names.add(nameToAdd);
				}
			}
		}
		for (String name : names) {
			w(String.format("\t\t\t%sBounceGuard = static_cast<long>(millis() - %sLastDebounceTime) > debounce;\n", name, name));
		}
	}

	@Override
	public void visit(InstantaneousTransition transition) {
		if(context.get("pass") == PASS.ONE) {
			return;
		}
		if(context.get("pass") == PASS.TWO) {
			String transitionName = transition.getNext().getName();
			if(transition.getCondition() != null) {
				if (transition.getCondition() instanceof ComposedCondition) {
					w("\t\t\tif");
					((ComposedCondition) transition.getCondition()).accept(this);
					w(String.format("{\n\t\t\t\t%sLastDebounceTime = millis();\n",((AtomicCondition)((ComposedCondition)transition.getCondition()).getConditions().get(0)).getSensor().getName()));
					w(String.format("\t\t\t\t%sLastDebounceTime = millis();\n",((AtomicCondition)((ComposedCondition)transition.getCondition()).getConditions().get(1)).getSensor().getName()));
				}else if (transition.getCondition() instanceof AtomicCondition) {
					w(String.format("\t\t\t%sBounceGuard = static_cast<long>(millis() - %sLastDebounceTime) > debounce;\n", ((AtomicCondition) transition.getCondition()).getSensor().getName(),
							((AtomicCondition) transition.getCondition()).getSensor().getName()));
					w("\t\t\tif");
					((AtomicCondition) transition.getCondition()).accept(this);
					w(String.format("{\n\t\t\t\t%sLastDebounceTime = millis();\n", ((AtomicCondition) transition.getCondition()).getSensor().getName()));
				}
				w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
				w("\t\t\t}\n");
			}
		}
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

	@Override
	public void visit(TimeoutTransition transition) {
		w(String.format("\t\t\tcurrentState = %s;\n",transition.getNext().getName()));
		w("\t\t\tbreak;\n");

	}

}