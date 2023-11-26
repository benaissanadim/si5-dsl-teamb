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
    var _a;
    fileNode.append(`
//Wiring code generated from an ArduinoML model
// Application name: ` +
        app.name +
        `

long debounce = 200;
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
        compileState(state, fileNode);
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
function compileState(state, fileNode) {
    fileNode.append(`
				case ` +
        state.name +
        `:`);
    for (const action of state.actions) {
        compileAction(action, fileNode);
    }
    for (const transition of state.transitions) {
        compileTransition(transition, fileNode);
    }
    fileNode.append(`
				break;`);
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
    if (condition.$type === "SignalCondition") {
        const negation = condition.ne ? "! " : "";
        return `${negation}digitalRead(${(_a = condition.sensor.ref) === null || _a === void 0 ? void 0 : _a.name}.inputPin) == ${condition.value.value} && ${(_b = condition.sensor.ref) === null || _b === void 0 ? void 0 : _b.name}BounceGuard`;
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
function bounceGuardVars(condition) {
    var _a, _b;
    if (condition.$type === "SignalCondition") {
        return `${(_a = condition.sensor.ref) === null || _a === void 0 ? void 0 : _a.name}BounceGuard = millis() - ${(_b = condition.sensor.ref) === null || _b === void 0 ? void 0 : _b.name}LastDebounceTime > debounce;\n					`;
    }
    else if (condition.$type === "CompositeCondition") {
        const leftCondition = bounceGuardVars(condition.left);
        const rightCondition = bounceGuardVars(condition.right);
        return `${leftCondition}${rightCondition}`;
    }
    return "";
}
function lastBouncedTime(condition) {
    var _a;
    if (condition.$type === "SignalCondition") {
        return `${(_a = condition.sensor.ref) === null || _a === void 0 ? void 0 : _a.name}LastDebounceTime = millis();\n					`;
    }
    else if (condition.$type === "CompositeCondition") {
        const leftCondition = lastBouncedTime(condition.left);
        const rightCondition = lastBouncedTime(condition.right);
        return `${leftCondition}${rightCondition}`;
    }
    return "";
}
function compileTransition(transition, fileNode) {
    var _a;
    var condition = transition.condition;
    while (condition.$type === "CompositeCondition") {
        condition = condition.left;
    }
    fileNode.append(`
		 			` +
        bounceGuardVars(transition.condition) +
        `if ( ` +
        compileCondition(transition.condition) +
        ` ) {
					` +
        lastBouncedTime(transition.condition) +
        `currentState = ` +
        ((_a = transition.next.ref) === null || _a === void 0 ? void 0 : _a.name) +
        `;
					}
		`);
}
//# sourceMappingURL=generator.js.map