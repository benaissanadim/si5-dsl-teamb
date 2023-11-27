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

export type NormalState = PerpetualState | TemporalState;

export const NormalState = 'NormalState';

export function isNormalState(item: unknown): item is NormalState {
    return reflection.isInstance(item, NormalState);
}

export interface Action extends AstNode {
    readonly $container: PerpetualState | TemporalState;
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
    readonly $container: CompositeCondition | ConditionalTransition;
    readonly $type: 'CompositeCondition';
    left: Condition
    op: LogicalOperator
    right: Condition
}

export const CompositeCondition = 'CompositeCondition';

export function isCompositeCondition(item: unknown): item is CompositeCondition {
    return reflection.isInstance(item, CompositeCondition);
}

export interface ConditionalTransition extends AstNode {
    readonly $container: ErrorState | PerpetualState | TemporalState;
    readonly $type: 'ConditionalTransition';
    condition: Condition
    next: Reference<State>
}

export const ConditionalTransition = 'ConditionalTransition';

export function isConditionalTransition(item: unknown): item is ConditionalTransition {
    return reflection.isInstance(item, ConditionalTransition);
}

export interface ErrorState extends AstNode {
    readonly $container: State;
    readonly $type: 'ErrorState';
    conditionalTransitions: Array<ConditionalTransition>
    errorActuator: Reference<Actuator>
    errorNumber: number
    pauseTime: number
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

export interface PerpetualState extends AstNode {
    readonly $container: State;
    readonly $type: 'PerpetualState';
    actions: Array<Action>
    conditionalTransitions: Array<ConditionalTransition>
}

export const PerpetualState = 'PerpetualState';

export function isPerpetualState(item: unknown): item is PerpetualState {
    return reflection.isInstance(item, PerpetualState);
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
    readonly $container: CompositeCondition | ConditionalTransition;
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

export interface TemporalState extends AstNode {
    readonly $container: State;
    readonly $type: 'TemporalState';
    actions: Array<Action>
    conditionalTransitions: Array<ConditionalTransition>
    temporalTransition: TemporalTransition
}

export const TemporalState = 'TemporalState';

export function isTemporalState(item: unknown): item is TemporalState {
    return reflection.isInstance(item, TemporalState);
}

export interface TemporalTransition extends AstNode {
    readonly $container: TemporalState;
    readonly $type: 'TemporalTransition';
    duration: number
    next?: Reference<State>
}

export const TemporalTransition = 'TemporalTransition';

export function isTemporalTransition(item: unknown): item is TemporalTransition {
    return reflection.isInstance(item, TemporalTransition);
}

export interface ArduinoMlAstType {
    Action: Action
    Actuator: Actuator
    App: App
    Brick: Brick
    CompositeCondition: CompositeCondition
    Condition: Condition
    ConditionalTransition: ConditionalTransition
    ErrorState: ErrorState
    LogicalOperator: LogicalOperator
    NegationOperator: NegationOperator
    NormalState: NormalState
    PerpetualState: PerpetualState
    Sensor: Sensor
    Signal: Signal
    SignalCondition: SignalCondition
    State: State
    TemporalState: TemporalState
    TemporalTransition: TemporalTransition
}

export class ArduinoMlAstReflection extends AbstractAstReflection {

    getAllTypes(): string[] {
        return ['Action', 'Actuator', 'App', 'Brick', 'CompositeCondition', 'Condition', 'ConditionalTransition', 'ErrorState', 'LogicalOperator', 'NegationOperator', 'NormalState', 'PerpetualState', 'Sensor', 'Signal', 'SignalCondition', 'State', 'TemporalState', 'TemporalTransition'];
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
            case PerpetualState:
            case TemporalState: {
                return this.isSubtype(NormalState, supertype);
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
            case 'ConditionalTransition:next':
            case 'TemporalTransition:next': {
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
                        { name: 'conditionalTransitions', type: 'array' }
                    ]
                };
            }
            case 'PerpetualState': {
                return {
                    name: 'PerpetualState',
                    mandatory: [
                        { name: 'actions', type: 'array' },
                        { name: 'conditionalTransitions', type: 'array' }
                    ]
                };
            }
            case 'TemporalState': {
                return {
                    name: 'TemporalState',
                    mandatory: [
                        { name: 'actions', type: 'array' },
                        { name: 'conditionalTransitions', type: 'array' }
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
