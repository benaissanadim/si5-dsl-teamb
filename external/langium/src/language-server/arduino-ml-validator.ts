import { ValidationAcceptor, ValidationChecks } from "langium";
import {
  ArduinoMlAstType,
  App,
  NormalState,
  TimeoutTransition,
} from "./generated/ast";
import type { ArduinoMlServices } from "./arduino-ml-module";

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: ArduinoMlServices) {
  const registry = services.validation.ValidationRegistry;
  const validator = services.validation.ArduinoMlValidator;
  const checks: ValidationChecks<ArduinoMlAstType> = {
    App: validator.checkNothing,
  };
  registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class ArduinoMlValidator {
  checkNothing(app: App, accept: ValidationAcceptor): void {
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
      .forEach((state) => this.checkNormalState(state as NormalState, accept));
  }
  checkNormalState(state: NormalState, accept: ValidationAcceptor): void {
    state.transitions
      .filter((transition) => transition.$type === "TimeoutTransition")
      .forEach((transition) => {
        const timeout = transition as TimeoutTransition;
        if (timeout.duration < 0) {
          accept("error", "Timeout must be positive.", {
            node: timeout,
            property: "duration",
          });
        }
      });

    // there can't be more than one conditionless timeout transition

    if (
      state.transitions
        .filter((transition) => transition.$type === "TimeoutTransition")
        .map((transition) => transition as TimeoutTransition)
        .filter((transition) => !transition.condition).length > 1
    ) {
      accept("error", "Only one conditionless timeout transition allowed.", {
        node: state,
        property: "transitions",
      });
    }

    // there shouldn't be a timeout transition with a duration greater than the duration of the conditionless timeout transition
    const conditionlessTransition: TimeoutTransition = state.transitions
      .filter((transition) => transition.$type === "TimeoutTransition")
      .map((transition) => transition as TimeoutTransition)
      .filter((transition) => !transition.condition)[0];
    if (conditionlessTransition) {
      const unreachableTransitions = state.transitions
        .filter((transition) => transition.$type === "TimeoutTransition")
        .map((transition) => transition as TimeoutTransition)
        .filter(
          (transition) =>
            transition.condition &&
            transition.duration >= conditionlessTransition.duration
        );
      if (unreachableTransitions.length > 0) {
        accept("warning", "Timeout transition is unreachable.", {
          node: conditionlessTransition,
          property: "duration",
        });
      }
    }
  }
}
