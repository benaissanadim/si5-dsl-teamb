import { ValidationAcceptor, ValidationChecks } from "langium";
import {
  ArduinoMlAstType,
  App,
  State,
  CompositeCondition,
  NormalState,
  Actuator,
  Sensor,
  Transition,
  TimeoutCondition,
} from "./generated/ast";
import type { ArduinoMlServices } from "./arduino-ml-module";

/**
 * Register custom validation checks.
 */
export function registerValidationChecks(services: ArduinoMlServices) {
  const registry = services.validation.ValidationRegistry;
  const validator = services.validation.ArduinoMlValidator;
  const checks: ValidationChecks<ArduinoMlAstType> = {
    App: validator.validateApp,
    State: validator.validateState,
    Transition: validator.validateTransition,
  };
  registry.register(checks, validator);
}

/**
 * Implementation of custom validations.
 */
export class ArduinoMlValidator {
  validateApp(app: App, accept: ValidationAcceptor): void {
    this.checkAppName(app, accept);
    this.validateAppBricksPins(app, accept);
  }

  checkAppName(app: App, accept: ValidationAcceptor): void {
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

  validateAppBricksPins(app: App, accept: ValidationAcceptor): void {
    const bricks = app.bricks;
    // Just adding a couple critical checks for arduino
    // Check if bricks on the allowed pins
    bricks.forEach((brick) => {
      if (brick.$type === "Actuator") {
        const actuatorBrick = brick as Actuator;
        if (actuatorBrick.outputPin < 0 || actuatorBrick.outputPin > 13) {
          accept("error", "Actuator pin must be between 0 and 13.", {
            node: brick,
            property: "outputPin",
          });
        }
      } else if (brick.$type === "Sensor") {
        const sensorBrick = brick as Sensor;
        if (sensorBrick.inputPin < 0 || sensorBrick.inputPin > 13) {
          accept("error", "Sensor pin must be between 0 and 13.", {
            node: brick,
            property: "inputPin",
          });
        }
      }
    });

    // Check if there are duplicate pins
    bricks.reduce((acc: number[], brick) => {
      if (brick.$type === "Actuator") {
        const actuatorBrick = brick as Actuator;
        if (acc.includes(actuatorBrick.outputPin)) {
          accept("error", "Duplicate pin.", {
            node: brick,
            property: "outputPin",
          });
        } else {
          acc.push(actuatorBrick.outputPin);
        }
      } else if (brick.$type === "Sensor") {
        const sensorBrick = brick as Sensor;
        if (acc.includes(sensorBrick.inputPin)) {
          accept("error", "Duplicate pin.", {
            node: brick,
            property: "inputPin",
          });
        } else {
          acc.push(sensorBrick.inputPin);
        }
      }
      return acc;
    }, []);

    // Check if there are duplicate names
    bricks.reduce((acc: string[], brick) => {
      if (acc.includes(brick.name)) {
        accept("error", "Duplicate name.", {
          node: brick,
          property: "name",
        });
      } else {
        acc.push(brick.name);
      }
      return acc;
    }, []);
  }

  isSink(state: NormalState, accept: ValidationAcceptor): void {
    if (state.transitions.length === 0) {
      accept("warning", "State is a sink.", {
        node: state,
        property: "transitions",
      });
    }
  }

  hasATimeoutCondition(compositeCondition: CompositeCondition): boolean {
    if (
      compositeCondition.left.$type === "TimeoutCondition" ||
      compositeCondition.right.$type === "TimeoutCondition"
    ) {
      return true;
    } else if (
      compositeCondition.left.$type === "CompositeCondition" &&
      compositeCondition.right.$type === "CompositeCondition"
    ) {
      return (
        this.hasATimeoutCondition(compositeCondition.left) ||
        this.hasATimeoutCondition(compositeCondition.right)
      );
    } else if (
      compositeCondition.right.$type === "CompositeCondition" &&
      compositeCondition.left.$type !== "CompositeCondition"
    ) {
      return this.hasATimeoutCondition(compositeCondition.right);
    } else if (
      compositeCondition.left.$type === "CompositeCondition" &&
      compositeCondition.right.$type !== "CompositeCondition"
    ) {
      return this.hasATimeoutCondition(compositeCondition.left);
    } else {
      return false;
    }
  }

  getTimeoutCondition(
    compositeCondition: CompositeCondition
  ): TimeoutCondition {
    if (compositeCondition.left.$type === "TimeoutCondition") {
      return compositeCondition.left;
    } else if (compositeCondition.right.$type === "TimeoutCondition") {
      return compositeCondition.right;
    } else if (
      compositeCondition.left.$type === "CompositeCondition" &&
      compositeCondition.right.$type === "CompositeCondition"
    ) {
      try {
        return this.getTimeoutCondition(compositeCondition.left);
      } catch {
        return this.getTimeoutCondition(compositeCondition.right);
      }
    } else if (
      compositeCondition.right.$type === "CompositeCondition" &&
      compositeCondition.left.$type !== "CompositeCondition"
    ) {
      return this.getTimeoutCondition(compositeCondition.right);
    } else if (
      compositeCondition.left.$type === "CompositeCondition" &&
      compositeCondition.right.$type !== "CompositeCondition"
    ) {
      return this.getTimeoutCondition(compositeCondition.left);
    } else {
      throw new Error("No timeout condition found.");
    }
  }

  hasMultipleTimeoutConditions(
    compositeCondition: CompositeCondition
  ): boolean {
    let count = 0;
    if (compositeCondition.left.$type === "TimeoutCondition") {
      count++;
    }
    if (compositeCondition.right.$type === "TimeoutCondition") {
      count++;
    }
    if (count > 1) {
      return true;
    } else if (
      compositeCondition.left.$type === "CompositeCondition" &&
      compositeCondition.right.$type === "CompositeCondition"
    ) {
      return (
        this.hasMultipleTimeoutConditions(compositeCondition.left) ||
        this.hasMultipleTimeoutConditions(compositeCondition.right)
      );
    } else if (
      compositeCondition.right.$type === "CompositeCondition" &&
      compositeCondition.left.$type !== "CompositeCondition"
    ) {
      return this.hasMultipleTimeoutConditions(compositeCondition.right);
    } else if (
      compositeCondition.left.$type === "CompositeCondition" &&
      compositeCondition.right.$type !== "CompositeCondition"
    ) {
      return this.hasMultipleTimeoutConditions(compositeCondition.left);
    } else {
      return false;
    }
  }

  hasIgnoredTransitions(state: NormalState, accept: ValidationAcceptor): void {
    if (state.transitions.length > 0) {
      const timeoutCondition: TimeoutCondition[] = state.transitions
        .map((transition) => transition.condition)
        .filter((condition) => condition.$type !== "TimeoutCondition")
        .map((condition) => condition as TimeoutCondition);
      const compositeConditionWithTimeout: TimeoutCondition[] =
        state.transitions
          .map((transition) => transition.condition)
          .filter((condition) => condition.$type === "CompositeCondition")
          .filter((condition) =>
            this.hasATimeoutCondition(condition as CompositeCondition)
          )
          .map((condition) =>
            this.getTimeoutCondition(condition as CompositeCondition)
          );
      if (
        timeoutCondition.length > 0 &&
        compositeConditionWithTimeout.length > 0
      ) {
        if (
          timeoutCondition[0].duration <
          compositeConditionWithTimeout[0].duration
        ) {
          accept("warning", "State has ignored transitions.", {
            node: state,
            property: "transitions",
          });
        }
      }
    }
  }

  hasIgnoredTimeoutCondition(
    transition: Transition,
    accept: ValidationAcceptor
  ): void {
    if (
      transition.condition.$type === "CompositeCondition" &&
      this.hasMultipleTimeoutConditions(transition.condition)
    ) {
      accept("error", "Transition has multiple timeout conditions.", {
        node: transition,
        property: "condition",
      });
    }
  }

  validateTransition(transition: Transition, accept: ValidationAcceptor): void {
    this.hasIgnoredTimeoutCondition(transition, accept);
  }

  validateState(state: State, accept: ValidationAcceptor): void {
    if (state.$type === "NormalState") {
      state = state as NormalState;
      this.isSink(state, accept);
      this.hasIgnoredTransitions(state, accept);
    }
  }
}
