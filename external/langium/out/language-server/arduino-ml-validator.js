"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ArduinoMlValidator = exports.registerValidationChecks = void 0;
/**
 * Register custom validation checks.
 */
function registerValidationChecks(services) {
    const registry = services.validation.ValidationRegistry;
    const validator = services.validation.ArduinoMlValidator;
    const checks = {
        App: validator.validateApp,
        State: validator.validateState,
        Transition: validator.validateTransition,
    };
    registry.register(checks, validator);
}
exports.registerValidationChecks = registerValidationChecks;
/**
 * Implementation of custom validations.
 */
class ArduinoMlValidator {
    validateApp(app, accept) {
        this.checkAppName(app, accept);
        this.validateAppBricksPins(app, accept);
    }
    checkAppName(app, accept) {
        if (app.name) {
            const firstChar = app.name.substring(0, 1);
            if (firstChar.toUpperCase() !== firstChar) {
                accept("warning", "App name should start with a capital.", {
                    node: app,
                    property: "name",
                });
            }
        }
    }
    validateAppBricksPins(app, accept) {
        const bricks = app.bricks;
        // Just adding a couple critical checks for arduino 
        // Check if bricks on the allowed pins
        bricks.forEach(brick => {
            if (brick.$type === "Actuator") {
                const actuatorBrick = brick;
                if (actuatorBrick.outputPin < 0 || actuatorBrick.outputPin > 13) {
                    accept("error", "Actuator pin must be between 0 and 13.", {
                        node: brick,
                        property: "outputPin",
                    });
                }
            }
            else if (brick.$type === "Sensor") {
                const sensorBrick = brick;
                if (sensorBrick.inputPin < 0 || sensorBrick.inputPin > 13) {
                    accept("error", "Sensor pin must be between 0 and 13.", {
                        node: brick,
                        property: "inputPin",
                    });
                }
            }
        });
        // Check if there are duplicate pins
        bricks.reduce((acc, brick) => {
            if (brick.$type === "Actuator") {
                const actuatorBrick = brick;
                if (acc.includes(actuatorBrick.outputPin)) {
                    accept("error", "Duplicate pin.", {
                        node: brick,
                        property: "outputPin",
                    });
                }
                else {
                    acc.push(actuatorBrick.outputPin);
                }
            }
            else if (brick.$type === "Sensor") {
                const sensorBrick = brick;
                if (acc.includes(sensorBrick.inputPin)) {
                    accept("error", "Duplicate pin.", {
                        node: brick,
                        property: "inputPin",
                    });
                }
                else {
                    acc.push(sensorBrick.inputPin);
                }
            }
            return acc;
        }, []);
        // Check if there are duplicate names
        bricks.reduce((acc, brick) => {
            if (acc.includes(brick.name)) {
                accept("error", "Duplicate name.", {
                    node: brick,
                    property: "name",
                });
            }
            else {
                acc.push(brick.name);
            }
            return acc;
        }, []);
    }
    isSink(state, accept) {
        if (state.transitions.length === 0) {
            accept("warning", "State is a sink.", {
                node: state,
                property: "transitions",
            });
        }
    }
    hasATimeoutCondition(compositeCondition) {
        if (compositeCondition.left.$type === "TimeoutCondition" ||
            compositeCondition.right.$type === "TimeoutCondition") {
            return true;
        }
        else if (compositeCondition.left.$type === "CompositeCondition" &&
            compositeCondition.right.$type === "CompositeCondition") {
            return this.hasATimeoutCondition(compositeCondition.left) ||
                this.hasATimeoutCondition(compositeCondition.right);
        }
        else if (compositeCondition.right.$type === "CompositeCondition" &&
            compositeCondition.left.$type !== "CompositeCondition") {
            return this.hasATimeoutCondition(compositeCondition.right);
        }
        else if (compositeCondition.left.$type === "CompositeCondition" &&
            compositeCondition.right.$type !== "CompositeCondition") {
            return this.hasATimeoutCondition(compositeCondition.left);
        }
        else {
            return false;
        }
    }
    hasMultipleTimeoutConditions(compositeCondition) {
        let count = 0;
        if (compositeCondition.left.$type === "TimeoutCondition") {
            count++;
        }
        if (compositeCondition.right.$type === "TimeoutCondition") {
            count++;
        }
        if (count > 1) {
            return true;
        }
        else if (compositeCondition.left.$type === "CompositeCondition" &&
            compositeCondition.right.$type === "CompositeCondition") {
            return this.hasMultipleTimeoutConditions(compositeCondition.left) ||
                this.hasMultipleTimeoutConditions(compositeCondition.right);
        }
        else if (compositeCondition.right.$type === "CompositeCondition" &&
            compositeCondition.left.$type !== "CompositeCondition") {
            return this.hasMultipleTimeoutConditions(compositeCondition.right);
        }
        else if (compositeCondition.left.$type === "CompositeCondition" &&
            compositeCondition.right.$type !== "CompositeCondition") {
            return this.hasMultipleTimeoutConditions(compositeCondition.left);
        }
        else {
            return false;
        }
    }
    hasIgnoredTransitions(state, accept) {
        if (state.transitions.length > 0) {
            const timeoutConditionCount = state.transitions
                .map(transition => transition.condition)
                .filter(condition => (condition.$type !== "TimeoutCondition"))
                .length;
            const compositeConditionWithTimeoutCount = state.transitions
                .map(transition => transition.condition)
                .filter(condition => (condition.$type === "CompositeCondition"))
                .filter(condition => (this.hasATimeoutCondition(condition)))
                .length;
            if (timeoutConditionCount + compositeConditionWithTimeoutCount > 1) {
                accept("warning", "State has ignored transitions.", {
                    node: state,
                    property: "transitions",
                });
            }
        }
    }
    hasIgnoredTimeoutCondition(transition, accept) {
        if (transition.condition.$type === "CompositeCondition" &&
            this.hasMultipleTimeoutConditions(transition.condition)) {
            accept("error", "Transition has multiple timeout conditions.", {
                node: transition,
                property: "condition",
            });
        }
    }
    validateTransition(transition, accept) {
        this.hasIgnoredTimeoutCondition(transition, accept);
    }
    validateState(state, accept) {
        if (state.$type === "NormalState") {
            state = state;
            this.isSink(state, accept);
            this.hasIgnoredTransitions(state, accept);
        }
    }
}
exports.ArduinoMlValidator = ArduinoMlValidator;
//# sourceMappingURL=arduino-ml-validator.js.map