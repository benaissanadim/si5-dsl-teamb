"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateInoFile = void 0;
const fs_1 = __importDefault(require("fs"));
const langium_1 = require("langium");
const path_1 = __importDefault(require("path"));
const cli_util_1 = require("./cli-util");
function generateInoFile(app, filePath, destination) {
    const data = (0, cli_util_1.extractDestinationAndName)(filePath, destination);
    const generatedFilePath = `${path_1.default.join(data.destination, data.name)}.ino`;
    const fileNode = new langium_1.CompositeGeneratorNode();
    compile(app, fileNode);
    if (!fs_1.default.existsSync(data.destination)) {
        fs_1.default.mkdirSync(data.destination, { recursive: true });
    }
    fs_1.default.writeFileSync(generatedFilePath, (0, langium_1.toString)(fileNode));
    return generatedFilePath;
}
exports.generateInoFile = generateInoFile;
function compile(app, fileNode) {
    var _a, _b;
    fileNode.append(`
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
        ((_a = app.initial.ref) === null || _a === void 0 ? void 0 : _a.name) +
        `;`, langium_1.NL);
    for (const brick of app.bricks) {
        if ("inputPin" in brick) {
            fileNode.append(`
bool ` +
                brick.name +
                `BounceGuard = false;
long ` +
                brick.name +
                `LastDebounceTime = 0;

`, langium_1.NL);
        }
    }
    fileNode.append(`
	void setup(){`);
    for (const brick of app.bricks) {
        if ("inputPin" in brick) {
            compileSensor(brick, fileNode);
        }
        else {
            compileActuator(brick, fileNode);
        }
    }
    fileNode.append(`
	}
	void loop() {
			switch(currentState){`, langium_1.NL);
    for (const state of app.states) {
        compileState(state, (_b = app.initial.ref) === null || _b === void 0 ? void 0 : _b.name, fileNode);
    }
    fileNode.append(`
		}
	}
	`, langium_1.NL);
}
function compileActuator(actuator, fileNode) {
    fileNode.append(`
		pinMode(` +
        actuator.outputPin +
        `, OUTPUT); // ` +
        actuator.name +
        ` [Actuator]`);
}
function compileSensor(sensor, fileNode) {
    fileNode.append(`
		pinMode(` +
        sensor.inputPin +
        `, INPUT); // ` +
        sensor.name +
        ` [Sensor]`);
}
function compileState(state, initial, fileNode) {
    fileNode.append(`
				case ` +
        state.name +
        `:`);
    if (state.$type === "NormalState")
        compileNormalState(state, initial, fileNode);
    if (state.$type === "ErrorState")
        compileErrorState(state, fileNode);
    fileNode.append(`

				    break;
            `);
}
function compileNormalState(state, initial, fileNode) {
    for (const action of state.actions) {
        compileAction(action, fileNode);
    }
    const bounceGuards = [];
    for (const transition of state.transitions) {
        if (transition.condition) {
            fileNode.append(`
					` + bounceGuardVars(transition.condition, bounceGuards));
        }
    }
    const timeoutTransitions = state.transitions
        .filter((transition) => transition.$type === "TimeoutTransition")
        .map((transition) => transition);
    if (timeoutTransitions.length > 0) {
        const instantTransitions = state.transitions
            .filter((transition) => transition.$type === "InstantaneousTransition")
            .map((transition) => transition);
        compileTimeoutTransitions(timeoutTransitions, instantTransitions, fileNode);
    }
    else {
        for (const transition of state.transitions) {
            compileInstantaneousTransition(transition, fileNode);
        }
    }
}
function compileTimeoutTransitions(temporalTransitions, instantTransitions, fileNode) {
    let condition = "";
    const temporalTransition = temporalTransitions[0];
    condition = compileTimeoutTransition(temporalTransition);
    for (const transition of temporalTransitions) {
        if (transition === temporalTransition)
            continue;
        condition += " || " + compileTimeoutTransition(transition);
    }
    fileNode.append(`               
                    startTime = millis();
                    
                    while (` +
        condition +
        `) {
                        `);
    for (const transition of instantTransitions) {
        compileInstantaneousTransition(transition, fileNode);
    }
    fileNode.append(`   
                        delayMicroseconds(100);

                    }

      `);
    compileNextState(temporalTransitions, fileNode);
}
function compileNextState(temporalTransitions, fileNode) {
    var _a, _b, _c, _d, _e;
    if (temporalTransitions.length === 1) {
        fileNode.append(`              currentState = ` +
            ((_b = (_a = temporalTransitions[0].next.nextState) === null || _a === void 0 ? void 0 : _a.ref) === null || _b === void 0 ? void 0 : _b.name) +
            `;`);
        return;
    }
    for (const transition of temporalTransitions) {
        const currentState = transition.next.nextState
            ? (_c = transition.next.nextState.ref) === null || _c === void 0 ? void 0 : _c.name
            : (_e = (_d = transition.next.error) === null || _d === void 0 ? void 0 : _d.ref) === null || _e === void 0 ? void 0 : _e.name;
        const elseIf = temporalTransitions.indexOf(transition) === 0 ? "if" : "else if";
        fileNode.append(`              ` +
            elseIf +
            `   ( ` +
            compileTimeoutBreakTransition(transition) +
            ` ) {` +
            `
                       currentState = ` +
            currentState +
            `;` +
            `
                    }
        `);
    }
}
function compileTimeoutTransition(temporalTransition) {
    let condition = "( millis() - startTime < " + temporalTransition.duration;
    if (temporalTransition.condition && temporalTransition.op) {
        const op = temporalTransition.op;
        const logicalOperator = op.AND ? "&&" : op.OR ? "||" : op.XOR ? "^" : "";
        condition +=
            " " +
                logicalOperator +
                "  ! (" +
                compileCondition(temporalTransition.condition) +
                " )";
    }
    condition += " )";
    return condition;
}
function compileTimeoutBreakTransition(temporalTransition) {
    let condition = "( millis() - startTime >= " + temporalTransition.duration;
    if (temporalTransition.condition && temporalTransition.op) {
        const op = temporalTransition.op;
        const logicalOperator = op.AND ? "&&" : op.OR ? "||" : op.XOR ? "^" : "";
        condition +=
            " " +
                logicalOperator +
                "  (" +
                compileCondition(temporalTransition.condition) +
                " )";
    }
    condition += " )";
    return condition;
}
function compileErrorState(state, fileNode) {
    var _a, _b;
    const actuator = state.errorActuator;
    const blinkCount = state.errorNumber;
    const pauseTime = state.pauseTime;
    fileNode.append(`
					// Blink the error actuator
					for (int i = 0; i < ` +
        blinkCount +
        `; i++) {
						digitalWrite(` +
        ((_a = actuator.ref) === null || _a === void 0 ? void 0 : _a.outputPin) +
        `, HIGH); // turn the error actuator on
						delay(500); // wait for 500ms
						digitalWrite(` +
        ((_b = actuator.ref) === null || _b === void 0 ? void 0 : _b.outputPin) +
        `, LOW); // turn the error actuator off
						delay(500); // wait for 500ms
					}
					delay(` +
        pauseTime +
        ` * 1000);`);
}
function compileAction(action, fileNode) {
    var _a;
    fileNode.append(`
					digitalWrite(` +
        ((_a = action.actuator.ref) === null || _a === void 0 ? void 0 : _a.outputPin) +
        `,` +
        action.value.value +
        `);`);
}
function compileCondition(condition) {
    var _a, _b;
    if (condition.$type === "AtomicCondition") {
        const negation = condition.ne ? "! " : "";
        return `${negation}digitalRead(${(_a = condition.sensor.ref) === null || _a === void 0 ? void 0 : _a.inputPin}) == ${condition.value.value} && ${(_b = condition.sensor.ref) === null || _b === void 0 ? void 0 : _b.name}BounceGuard`;
    }
    else if (condition.$type === "CompositeCondition") {
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
function bounceGuardVars(condition, bounceGuardsArray) {
    var _a, _b, _c, _d;
    if (condition.$type === "AtomicCondition") {
        if (!bounceGuardsArray.includes((_a = condition.sensor.ref) === null || _a === void 0 ? void 0 : _a.name)) {
            bounceGuardsArray.push((_b = condition.sensor.ref) === null || _b === void 0 ? void 0 : _b.name);
            return `${(_c = condition.sensor.ref) === null || _c === void 0 ? void 0 : _c.name}BounceGuard = static_cast<long>(millis() - ${(_d = condition.sensor.ref) === null || _d === void 0 ? void 0 : _d.name}LastDebounceTime) > debounce;\n					`;
        }
        else
            return "";
    }
    else if (condition.$type === "CompositeCondition") {
        const leftCondition = bounceGuardVars(condition.left, bounceGuardsArray);
        const rightCondition = bounceGuardVars(condition.right, bounceGuardsArray);
        return `${leftCondition}${rightCondition}`;
    }
    return "";
}
function lastBouncedTime(condition) {
    var _a;
    if (condition.$type === "AtomicCondition") {
        return `${(_a = condition.sensor.ref) === null || _a === void 0 ? void 0 : _a.name}LastDebounceTime = millis();\n						`;
    }
    else if (condition.$type === "CompositeCondition") {
        const leftCondition = lastBouncedTime(condition.left);
        const rightCondition = lastBouncedTime(condition.right);
        return `${leftCondition}${rightCondition}`;
    }
    return "";
}
function compileInstantaneousTransition(transition, fileNode) {
    var _a, _b, _c;
    var condition = transition.condition;
    while (condition.$type === "CompositeCondition") {
        condition = condition.left;
    }
    const currentState = transition.next.nextState
        ? (_a = transition.next.nextState.ref) === null || _a === void 0 ? void 0 : _a.name
        : (_c = (_b = transition.next.error) === null || _b === void 0 ? void 0 : _b.ref) === null || _c === void 0 ? void 0 : _c.name;
    fileNode.append(`if ( ` +
        compileCondition(transition.condition) +
        ` ) {
						` +
        lastBouncedTime(transition.condition) +
        `currentState = ` +
        currentState +
        `;
					}
					`);
}
//# sourceMappingURL=generator.js.map