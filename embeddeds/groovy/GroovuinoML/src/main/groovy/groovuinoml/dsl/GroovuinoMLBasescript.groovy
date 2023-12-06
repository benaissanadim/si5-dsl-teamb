package main.groovy.groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.ComposedCondition
import io.github.mosser.arduinoml.kernel.behavioral.InstantaneousTransition
import io.github.mosser.arduinoml.kernel.behavioral.ErrorState
import io.github.mosser.arduinoml.kernel.behavioral.NormalState
import io.github.mosser.arduinoml.kernel.behavioral.AtomicCondition
import io.github.mosser.arduinoml.kernel.behavioral.TimeoutTransition
import io.github.mosser.arduinoml.kernel.structural.OPERATOR
import io.github.mosser.arduinoml.kernel.behavioral.Action
import io.github.mosser.arduinoml.kernel.behavioral.State
import io.github.mosser.arduinoml.kernel.structural.Actuator
import io.github.mosser.arduinoml.kernel.structural.Sensor
import io.github.mosser.arduinoml.kernel.structural.SIGNAL

abstract class GroovuinoMLBasescript extends Script {
	// sensor "name" pin n
	def sensor(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n) },
		onPin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createSensor(name, n)}]
	}
	
	// actuator "name" pin n
	def actuator(String name) {
		[pin: { n -> ((GroovuinoMLBinding)this.getBinding()).getGroovuinoMLModel().createActuator(name, n) }]
	}
	def error(String name) {
		def error = new ErrorState()
		error.setName(name)
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createError(error)
		def closure
		closure ={ int pause ->
			error.setPauseTime(pause)
			[ ms: {}]
		}
		[means: { actuator ->
					error.setActuator(actuator instanceof String ? (Actuator) ((GroovuinoMLBinding) this.getBinding()).getVariable(actuator) : (Actuator) actuator)
					 [flashes: { int n ->
						 error.setErrorNumber(n)
						 [times: { and ->  [pauses: closure ]
							}]
					 	}]
				}]
	}
	def then(String a ){
		return [
				[times: { int times ->
					error.setTimes(times)
				}]
		]
	}


	
	// state "name" means actuator becomes signal [and actuator becomes signal]*n
	def state(String name) {
		List<Action> actions = new ArrayList<Action>()
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
		// recursive closure to allow multiple and statements
		def closure
		closure = { actuator -> 
			[becomes: { signal ->
				Action action = new Action()
				action.setActuator(actuator instanceof String ? (Actuator)((GroovuinoMLBinding)this.getBinding()).getVariable(actuator) : (Actuator)actuator)
				action.setValue(signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal)
				actions.add(action)
				[and: closure]
			}]
		}
		[means: closure]

	}


	
	// initial state
	def initial(state) {
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().setInitialState(state instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state) : (State)state)
	}
	// from state1 to state2 when sensor becomes signal
	def from(String state1) {
		InstantaneousTransition transition = new InstantaneousTransition()
		State state =new State()
		def actualState1 = state1 instanceof String ? (NormalState)((GroovuinoMLBinding)this.getBinding()).getVariable(state1) : (NormalState)state1
		((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createCompositeTransition(actualState1, state,transition)
		def closure1
		closure1={ state2 ->
			State actualState2 = state2 instanceof String ? (State)((GroovuinoMLBinding)this.getBinding()).getVariable(state2) : (State)state2
			state.setName(actualState2.getName())
			ComposedCondition composedCondition = new ComposedCondition()
			AtomicCondition singularCondition1 = new AtomicCondition()
			def closuref
			closure = { sensor ->
				[becomes: { signal ->
					def actualSensor = sensor instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor) : (Sensor)sensor
					def actualSignal = signal instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal) : (SIGNAL)signal
					singularCondition1.setSensor(actualSensor)
					singularCondition1.setSignal(actualSignal)
					transition.setCondition(singularCondition1)
					def and
					and= { sensor1  ->
						[becomes: { signal1 ->
							def actualSensor1 = sensor1 instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor1) : (Sensor)sensor1
							def actualSignal1 = signal1 instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal1) : (SIGNAL)signal1
							AtomicCondition singularCondition = new AtomicCondition()
							singularCondition.setSensor(actualSensor1)
							singularCondition.setSignal(actualSignal1)
							composedCondition.setOperator(OPERATOR.AND)
							composedCondition.addConditions(Arrays.asList(singularCondition1,singularCondition))
							transition.setCondition(composedCondition)
						}
						]
					}
					def or
					or= { sensor1  ->
						[becomes: { signal1 ->
							def actualSensor1 = sensor1 instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor1) : (Sensor)sensor1
							def actualSignal1 = signal1 instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal1) : (SIGNAL)signal1
							AtomicCondition singularCondition = new AtomicCondition()
							singularCondition.setSensor(actualSensor1)
							singularCondition.setSignal(actualSignal1)
							composedCondition.setOperator(OPERATOR.OR)
							composedCondition.addConditions(Arrays.asList(singularCondition1,singularCondition))
							transition.setCondition(composedCondition)
						}
						]
					}
					def xor
					xor= { sensor1  ->
						[becomes: { signal1 ->
							def actualSensor1 = sensor1 instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor1) : (Sensor)sensor1
							def actualSignal1 = signal1 instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal1) : (SIGNAL)signal1
							AtomicCondition singularCondition = new AtomicCondition()
							singularCondition.setSensor(actualSensor1)
							singularCondition.setSignal(actualSignal1)
							composedCondition.setOperator(OPERATOR.XOR)
							composedCondition.addConditions(Arrays.asList(singularCondition1,singularCondition))
							transition.setCondition(composedCondition)
						}
						]
					}
					[and: and , or: or,xor: xor]

				}]
			}
			def after ;
			after = { time ->
				[ ms: {test ->
					TimeoutTransition temporalTransition = new TimeoutTransition();
					((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createTemporalTransition(
							state1 instanceof String ? (NormalState)((GroovuinoMLBinding)this.getBinding()).getVariable(state1) : (NormalState)state1,
							state2 instanceof String ? (NormalState)((GroovuinoMLBinding)this.getBinding()).getVariable(state2) : (NormalState)state2,
							time instanceof String ? (Integer)((GroovuinoMLBinding)this.getBinding()).getVariable(time) : (Integer)	time, temporalTransition)
				[and: { sensor1  ->
						[becomes: { signal1 ->
							def actualSensor1 = sensor1 instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor1) : (Sensor)sensor1
							def actualSignal1 = signal1 instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal1) : (SIGNAL)signal1
							AtomicCondition singularCondition = new AtomicCondition()
							singularCondition.setSensor(actualSensor1)
							singularCondition.setSignal(actualSignal1)
							temporalTransition.setCondition(singularCondition);
							temporalTransition.setOperator(OPERATOR.AND)
						}
						]
					}]
					}]}

			after = { time ->
					TimeoutTransition temporalTransition = new TimeoutTransition();
					((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createTemporalTransition(
							state1 instanceof String ? (NormalState)((GroovuinoMLBinding)this.getBinding()).getVariable(state1) : (NormalState)state1,
							state2 instanceof String ? (NormalState)((GroovuinoMLBinding)this.getBinding()).getVariable(state2) : (NormalState)state2,
							time instanceof String ? (Integer)((GroovuinoMLBinding)this.getBinding()).getVariable(time) : (Integer)	time, temporalTransition)
					[and: { sensor1  ->
						[becomes: { signal1 ->
							def actualSensor1 = sensor1 instanceof String ? (Sensor)((GroovuinoMLBinding)this.getBinding()).getVariable(sensor1) : (Sensor)sensor1
							def actualSignal1 = signal1 instanceof String ? (SIGNAL)((GroovuinoMLBinding)this.getBinding()).getVariable(signal1) : (SIGNAL)signal1
							AtomicCondition singularCondition = new AtomicCondition()
							singularCondition.setSensor(actualSensor1)
							singularCondition.setSignal(actualSignal1)
							temporalTransition.setCondition(singularCondition);
							temporalTransition.setOperator(OPERATOR.AND)
						}
						]
					}]
				}

			[when: closure, after : after ]
		}

		[to: closure1]
	}

	// export name
	// export name
	def export(String name) {
		println(((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().generateCode(name).toString())
	}
	
	// disable run method while running
	int count = 0
	abstract void scriptBody()
	def run() {
		if(count == 0) {
			count++
			scriptBody()
		} else {
			println "Run method is disabled"
		}
	}
}