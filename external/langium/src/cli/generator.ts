import fs from "fs";
import { CompositeGeneratorNode, NL, toString } from "langium";
import path from "path";
import {
  Action,
  Actuator,
  App,
  Sensor,
  State,
  ConditionalTransition,
  Condition,
  NormalState,
  ErrorState,
  TemporalTransition,
} from "../language-server/generated/ast";
import { extractDestinationAndName } from "./cli-util";

export function generateInoFile(
  app: App,
  filePath: string,
  destination: string | undefined
): string {
  const data = extractDestinationAndName(filePath, destination);
  const generatedFilePath = `${path.join(data.destination, data.name)}.ino`;

  const fileNode = new CompositeGeneratorNode();
  compile(app, fileNode);

  if (!fs.existsSync(data.destination)) {
    fs.mkdirSync(data.destination, { recursive: true });
  }
  fs.writeFileSync(generatedFilePath, toString(fileNode));
  return generatedFilePath;
}

function compile(app: App, fileNode: CompositeGeneratorNode) {
  fileNode.append(
    `
//Wiring code generated from an ArduinoML model
// Application name: ` +
      app.name +
      `

long debounce = 200;
long startTime; // Used for temporal transitions
enum STATE {` +
      app.states.map((s) => s.name).join(", ") +
      `};

STATE currentState = ` +
      app.initial.ref?.name +
      `;`,
    NL
  );

  for (const brick of app.bricks) {
    if ("inputPin" in brick) {
      fileNode.append(
        `
bool ` +
          brick.name +
          `BounceGuard = false;
long ` +
          brick.name +
          `LastDebounceTime = 0;

`,
        NL
      );
    }
  }
  fileNode.append(`
	void setup(){`);
  for (const brick of app.bricks) {
    if ("inputPin" in brick) {
      compileSensor(brick, fileNode);
    } else {
      compileActuator(brick, fileNode);
    }
  }

  fileNode.append(
    `
	}
	void loop() {
			switch(currentState){`,
    NL
  );
  for (const state of app.states) {
    compileState(state, app.initial.ref?.name, fileNode);
  }
  fileNode.append(
    `
		}
	}
	`,
    NL
  );
}

function compileActuator(actuator: Actuator, fileNode: CompositeGeneratorNode) {
  fileNode.append(
    `
		pinMode(` +
      actuator.outputPin +
      `, OUTPUT); // ` +
      actuator.name +
      ` [Actuator]`
  );
}

function compileSensor(sensor: Sensor, fileNode: CompositeGeneratorNode) {
  fileNode.append(
    `
		pinMode(` +
      sensor.inputPin +
      `, INPUT); // ` +
      sensor.name +
      ` [Sensor]`
  );
}

function compileState(
  state: State,
  initial: string | undefined,
  fileNode: CompositeGeneratorNode
) {
  fileNode.append(
    `
				case ` +
      state.name +
      `:`
  );
  if (state.$type === "NormalState")
    compileNormalState(state, initial, fileNode);

  if (state.$type === "ErrorState") compileErrorState(state, fileNode);

  fileNode.append(`
				  break;`);
}

function compileNormalState(
  state: NormalState,
  initial: string | undefined,
  fileNode: CompositeGeneratorNode
) {
  for (const action of state.actions) {
    compileAction(action, fileNode);
  }
  const bounceGuards: Array<string | undefined> = [];
  for (const transition of state.conditionalTransitions) {
    fileNode.append(
      `
					` + bounceGuardVars(transition.condition, bounceGuards)
    );
  }

  if (state.temporalTransition) {
    const temporalTransition = state.temporalTransition;
    const next = temporalTransition.next
      ? temporalTransition.next.ref?.name
      : initial;
    compileTemporalTransition(state, temporalTransition, next, fileNode);
  } else {
    for (const transition of state.conditionalTransitions) {
      compileConditionalTransition(transition, fileNode);
    }
  }
}

function compileTemporalTransition(
  state: NormalState,
  temporalTransition: TemporalTransition,
  next: string | undefined,
  fileNode: CompositeGeneratorNode
) {
  let condition: string = "";
  if (temporalTransition.condition && temporalTransition.op) {
    const op = temporalTransition.op;
    const logicalOperator = op.AND ? "&&" : op.OR ? "||" : op.XOR ? "^" : "";
    condition =
      " " +
      logicalOperator +
      " " +
      compileCondition(temporalTransition.condition) +
      " ";
  }

  fileNode.append(
    `               
                    startTime = millis();
                    // Continue as long as the elapsed time is less than ` +
      temporalTransition.duration +
      ` milliseconds
                    while (millis() - startTime < ` +
      temporalTransition.duration +
      condition +
      `) {
                        `
  );

  for (const transition of state.conditionalTransitions) {
    compileConditionalTransition(transition, fileNode);
  }
  fileNode.append(
    `   
                      delayMicroseconds(100);
                    }
                    currentState = ` +
      next +
      `;`
  );
}

function compileErrorState(
  state: ErrorState,
  fileNode: CompositeGeneratorNode
) {
  const actuator = state.errorActuator;
  const blinkCount = state.errorNumber;
  const pauseTime = state.pauseTime;
  fileNode.append(
    `
					// Blink the error actuator
					for (int i = 0; i < ` +
      blinkCount +
      `; i++) {
						digitalWrite(` +
      actuator.ref?.outputPin +
      `, HIGH); // turn the error actuator on
						delay(500); // wait for 500ms
						digitalWrite(` +
      actuator.ref?.outputPin +
      `, LOW); // turn the error actuator off
						delay(500); // wait for 500ms
					}
					delay(` +
      pauseTime +
      ` * 1000);`
  );
}

function compileAction(action: Action, fileNode: CompositeGeneratorNode) {
  fileNode.append(
    `
					digitalWrite(` +
      action.actuator.ref?.outputPin +
      `,` +
      action.value.value +
      `);`
  );
}

function compileCondition(condition: Condition): string {
  if (condition.$type === "SignalCondition") {
    const negation = condition.ne ? "! " : "";
    return `${negation}digitalRead(${condition.sensor.ref?.inputPin}) == ${condition.value.value} && ${condition.sensor.ref?.name}BounceGuard`;
  } else if (condition.$type === "CompositeCondition") {
    const leftCondition = compileCondition(condition.left);
    const rightCondition = compileCondition(condition.right);
    const logicalOperator = condition.op.AND
      ? "&&"
      : condition.op.OR
      ? "||"
      : condition.op.XOR
      ? "^"
      : "";
    return `( ( ${leftCondition} ) ${logicalOperator} ( ${rightCondition} ) )`;
  }
  return "";
}

function bounceGuardVars(
  condition: Condition,
  bounceGuardsArray: Array<string | undefined>
): string {
  if (condition.$type === "SignalCondition") {
    if (!bounceGuardsArray.includes(condition.sensor.ref?.name)) {
      bounceGuardsArray.push(condition.sensor.ref?.name);
      return `${condition.sensor.ref?.name}BounceGuard = static_cast<long>(millis() - ${condition.sensor.ref?.name}LastDebounceTime) > debounce;\n					`;
    } else return "";
  } else if (condition.$type === "CompositeCondition") {
    const leftCondition = bounceGuardVars(condition.left, bounceGuardsArray);
    const rightCondition = bounceGuardVars(condition.right, bounceGuardsArray);
    return `${leftCondition}${rightCondition}`;
  }
  return "";
}

function lastBouncedTime(condition: Condition): string {
  if (condition.$type === "SignalCondition") {
    return `${condition.sensor.ref?.name}LastDebounceTime = millis();\n						`;
  } else if (condition.$type === "CompositeCondition") {
    const leftCondition = lastBouncedTime(condition.left);
    const rightCondition = lastBouncedTime(condition.right);
    return `${leftCondition}${rightCondition}`;
  }
  return "";
}
function compileConditionalTransition(
  transition: ConditionalTransition,
  fileNode: CompositeGeneratorNode
) {
  var condition: Condition = transition.condition;
  while (condition.$type === "CompositeCondition") {
    condition = condition.left;
  }

  fileNode.append(
    `if ( ` +
      compileCondition(transition.condition) +
      ` ) {
						` +
      lastBouncedTime(transition.condition) +
      `currentState = ` +
      transition.next.ref?.name +
      `;
					}
					`
  );
}
