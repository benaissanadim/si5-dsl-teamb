/******************************************************************************
 * This file was generated by langium-cli 1.0.0.
 * DO NOT EDIT MANUALLY!
 ******************************************************************************/

/* eslint-disable */
import { AstNode, AbstractAstReflection, Reference, ReferenceInfo, TypeMetaData } from 'langium';

export type Brick = Actuator | Sensor;

export const Brick = 'Brick';

export function isBrick(item: unknown): item is Brick {
    return reflection.isInstance(item, Brick);
}

export type Condition = CompositeCondition | SignalCondition;

export const Condition = 'Condition';

export function isCondition(item: unknown): item is Condition {
    return reflection.isInstance(item, Condition);
}

export interface Action extends AstNode {
    readonly $container: NormalState;
    readonly $type: 'Action';
    actuator: Reference<Actuator>
    value: Signal
}

export const Action = 'Action';

export function isAction(item: unknown): item is Action {
    return reflection.isInstance(item, Action);
}

export interface Actuator extends AstNode {
    readonly $container: App;
    readonly $type: 'Actuator';
    name: string
    outputPin: number
}

export const Actuator = 'Actuator';

export function isActuator(item: unknown): item is Actuator {
    return reflection.isInstance(item, Actuator);
}

export interface App extends AstNode {
    readonly $type: 'App';
    bricks: Array<Brick>
    initial: Reference<State>
    name: string
    states: Array<State>
}

export const App = 'App';

export function isApp(item: unknown): item is App {
    return reflection.isInstance(item, App);
}

export interface CompositeCondition extends AstNode {
    readonly $container: CompositeCondition | Transition;
    readonly $type: 'CompositeCondition';
    left: Condition
    op: LogicalOperator
    right: Condition
}

export const CompositeCondition = 'CompositeCondition';

export function isCompositeCondition(item: unknown): item is CompositeCondition {
    return reflection.isInstance(item, CompositeCondition);
}

export interface ErrorState extends AstNode {
    readonly $container: State;
    readonly $type: 'ErrorState';
    errorActuator: Reference<Actuator>
    errorNumber: number
    pauseTime: number
    transitions: Array<Transition>
}

export const ErrorState = 'ErrorState';

export function isErrorState(item: unknown): item is ErrorState {
    return reflection.isInstance(item, ErrorState);
}

export interface LogicalOperator extends AstNode {
    readonly $container: CompositeCondition;
    readonly $type: 'LogicalOperator';
    AND?: 'and'
    OR?: 'or'
    XOR?: 'xor'
}

export const LogicalOperator = 'LogicalOperator';

export function isLogicalOperator(item: unknown): item is LogicalOperator {
    return reflection.isInstance(item, LogicalOperator);
}

export interface NegationOperator extends AstNode {
    readonly $container: SignalCondition;
    readonly $type: 'NegationOperator';
    NOT: 'not'
}

export const NegationOperator = 'NegationOperator';

export function isNegationOperator(item: unknown): item is NegationOperator {
    return reflection.isInstance(item, NegationOperator);
}

export interface NormalState extends AstNode {
    readonly $container: State;
    readonly $type: 'NormalState';
    actions: Array<Action>
    transitions: Array<Transition>
}

export const NormalState = 'NormalState';

export function isNormalState(item: unknown): item is NormalState {
    return reflection.isInstance(item, NormalState);
}

export interface Sensor extends AstNode {
    readonly $container: App;
    readonly $type: 'Sensor';
    inputPin: number
    name: string
}

export const Sensor = 'Sensor';

export function isSensor(item: unknown): item is Sensor {
    return reflection.isInstance(item, Sensor);
}

export interface Signal extends AstNode {
    readonly $container: Action | SignalCondition;
    readonly $type: 'Signal';
    value: string
}

export const Signal = 'Signal';

export function isSignal(item: unknown): item is Signal {
    return reflection.isInstance(item, Signal);
}

export interface SignalCondition extends AstNode {
    readonly $container: CompositeCondition | Transition;
    readonly $type: 'SignalCondition';
    ne?: NegationOperator
    sensor: Reference<Sensor>
    value: Signal
}

export const SignalCondition = 'SignalCondition';

export function isSignalCondition(item: unknown): item is SignalCondition {
    return reflection.isInstance(item, SignalCondition);
}

export interface State extends AstNode {
    readonly $container: App;
    readonly $type: 'State';
    body: ErrorState | NormalState
    name: string
}

export const State = 'State';

export function isState(item: unknown): item is State {
    return reflection.isInstance(item, State);
}

export interface Transition extends AstNode {
    readonly $container: ErrorState | NormalState;
    readonly $type: 'Transition';
    condition: Condition
    next: Reference<State>
}

export const Transition = 'Transition';

export function isTransition(item: unknown): item is Transition {
    return reflection.isInstance(item, Transition);
}

export interface ArduinoMlAstType {
    Action: Action
    Actuator: Actuator
    App: App
    Brick: Brick
    CompositeCondition: CompositeCondition
    Condition: Condition
    ErrorState: ErrorState
    LogicalOperator: LogicalOperator
    NegationOperator: NegationOperator
    NormalState: NormalState
    Sensor: Sensor
    Signal: Signal
    SignalCondition: SignalCondition
    State: State
    Transition: Transition
}

export class ArduinoMlAstReflection extends AbstractAstReflection {

    getAllTypes(): string[] {
        return ['Action', 'Actuator', 'App', 'Brick', 'CompositeCondition', 'Condition', 'ErrorState', 'LogicalOperator', 'NegationOperator', 'NormalState', 'Sensor', 'Signal', 'SignalCondition', 'State', 'Transition'];
    }

    protected override computeIsSubtype(subtype: string, supertype: string): boolean {
        switch (subtype) {
            case Actuator:
            case Sensor: {
                return this.isSubtype(Brick, supertype);
            }
            case CompositeCondition:
            case SignalCondition: {
                return this.isSubtype(Condition, supertype);
            }
            default: {
                return false;
            }
        }
    }

    getReferenceType(refInfo: ReferenceInfo): string {
        const referenceId = `${refInfo.container.$type}:${refInfo.property}`;
        switch (referenceId) {
            case 'Action:actuator':
            case 'ErrorState:errorActuator': {
                return Actuator;
            }
            case 'App:initial':
            case 'Transition:next': {
                return State;
            }
            case 'SignalCondition:sensor': {
                return Sensor;
            }
            default: {
                throw new Error(`${referenceId} is not a valid reference id.`);
            }
        }
    }

    getTypeMetaData(type: string): TypeMetaData {
        switch (type) {
            case 'App': {
                return {
                    name: 'App',
                    mandatory: [
                        { name: 'bricks', type: 'array' },
                        { name: 'states', type: 'array' }
                    ]
                };
            }
            case 'ErrorState': {
                return {
                    name: 'ErrorState',
                    mandatory: [
                        { name: 'transitions', type: 'array' }
                    ]
                };
            }
            case 'NormalState': {
                return {
                    name: 'NormalState',
                    mandatory: [
                        { name: 'actions', type: 'array' },
                        { name: 'transitions', type: 'array' }
                    ]
                };
            }
            default: {
                return {
                    name: type,
                    mandatory: []
                };
            }
        }
    }
}

export const reflection = new ArduinoMlAstReflection();