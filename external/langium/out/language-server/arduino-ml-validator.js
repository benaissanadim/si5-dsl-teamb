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
        App: validator.checkNothing,
    };
    registry.register(checks, validator);
}
exports.registerValidationChecks = registerValidationChecks;
/**
 * Implementation of custom validations.
 */
class ArduinoMlValidator {
    checkNothing(app, accept) {
        if (app.name) {
            const firstChar = app.name.substring(0, 1);
            if (firstChar.toUpperCase() !== firstChar) {
                accept("warning", "App name should start with a capital.", {
                    node: app,
                    property: "name",
                });
            }
        }
        app.states
            .filter((state) => state.$type === "NormalState")
            .forEach((state) => this.checkNormalState(state, accept));
    }
    checkNormalState(state, accept) {
        state.transitions
            .filter((transition) => transition.$type === "TimeoutTransition")
            .forEach((transition) => {
            const timeout = transition;
            if (timeout.duration < 0) {
                accept("error", "Timeout must be positive.", {
                    node: timeout,
                    property: "duration",
                });
            }
        });
        // there can't be more than one conditionless timeout transition
        if (state.transitions
            .filter((transition) => transition.$type === "TimeoutTransition")
            .map((transition) => transition)
            .filter((transition) => !transition.condition).length > 1) {
            accept("error", "Only one conditionless timeout transition allowed.", {
                node: state,
                property: "transitions",
            });
        }
        // there shouldn't be a timeout transition with a duration greater than the duration of the conditionless timeout transition
        const conditionlessTransition = state.transitions
            .filter((transition) => transition.$type === "TimeoutTransition")
            .map((transition) => transition)
            .filter((transition) => !transition.condition)[0];
        if (conditionlessTransition) {
            const unreachableTransitions = state.transitions
                .filter((transition) => transition.$type === "TimeoutTransition")
                .map((transition) => transition)
                .filter((transition) => transition.condition &&
                transition.duration >= conditionlessTransition.duration);
            if (unreachableTransitions.length > 0) {
                accept("warning", "Timeout transition is unreachable.", {
                    node: conditionlessTransition,
                    property: "duration",
                });
            }
        }
    }
}
exports.ArduinoMlValidator = ArduinoMlValidator;
//# sourceMappingURL=arduino-ml-validator.js.map