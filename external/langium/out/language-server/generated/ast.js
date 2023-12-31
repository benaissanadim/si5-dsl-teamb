"use strict";
/******************************************************************************
 * This file was generated by langium-cli 1.0.0.
 * DO NOT EDIT MANUALLY!
 ******************************************************************************/
Object.defineProperty(exports, "__esModule", { value: true });
exports.reflection = exports.ArduinoMlAstReflection = exports.isTransition = exports.Transition = exports.isTimeoutCondition = exports.TimeoutCondition = exports.isSignal = exports.Signal = exports.isSensor = exports.Sensor = exports.isRemoteCondition = exports.RemoteCondition = exports.isRemoteCommunication = exports.RemoteCommunication = exports.isNormalState = exports.NormalState = exports.isNext = exports.Next = exports.isNegationOperator = exports.NegationOperator = exports.isLogicalOperator = exports.LogicalOperator = exports.isErrorState = exports.ErrorState = exports.isCompositeCondition = exports.CompositeCondition = exports.isAtomicCondition = exports.AtomicCondition = exports.isApp = exports.App = exports.isActuator = exports.Actuator = exports.isAction = exports.Action = exports.isState = exports.State = exports.isCondition = exports.Condition = exports.isBrick = exports.Brick = void 0;
/* eslint-disable */
const langium_1 = require("langium");
exports.Brick = 'Brick';
function isBrick(item) {
    return exports.reflection.isInstance(item, exports.Brick);
}
exports.isBrick = isBrick;
exports.Condition = 'Condition';
function isCondition(item) {
    return exports.reflection.isInstance(item, exports.Condition);
}
exports.isCondition = isCondition;
exports.State = 'State';
function isState(item) {
    return exports.reflection.isInstance(item, exports.State);
}
exports.isState = isState;
exports.Action = 'Action';
function isAction(item) {
    return exports.reflection.isInstance(item, exports.Action);
}
exports.isAction = isAction;
exports.Actuator = 'Actuator';
function isActuator(item) {
    return exports.reflection.isInstance(item, exports.Actuator);
}
exports.isActuator = isActuator;
exports.App = 'App';
function isApp(item) {
    return exports.reflection.isInstance(item, exports.App);
}
exports.isApp = isApp;
exports.AtomicCondition = 'AtomicCondition';
function isAtomicCondition(item) {
    return exports.reflection.isInstance(item, exports.AtomicCondition);
}
exports.isAtomicCondition = isAtomicCondition;
exports.CompositeCondition = 'CompositeCondition';
function isCompositeCondition(item) {
    return exports.reflection.isInstance(item, exports.CompositeCondition);
}
exports.isCompositeCondition = isCompositeCondition;
exports.ErrorState = 'ErrorState';
function isErrorState(item) {
    return exports.reflection.isInstance(item, exports.ErrorState);
}
exports.isErrorState = isErrorState;
exports.LogicalOperator = 'LogicalOperator';
function isLogicalOperator(item) {
    return exports.reflection.isInstance(item, exports.LogicalOperator);
}
exports.isLogicalOperator = isLogicalOperator;
exports.NegationOperator = 'NegationOperator';
function isNegationOperator(item) {
    return exports.reflection.isInstance(item, exports.NegationOperator);
}
exports.isNegationOperator = isNegationOperator;
exports.Next = 'Next';
function isNext(item) {
    return exports.reflection.isInstance(item, exports.Next);
}
exports.isNext = isNext;
exports.NormalState = 'NormalState';
function isNormalState(item) {
    return exports.reflection.isInstance(item, exports.NormalState);
}
exports.isNormalState = isNormalState;
exports.RemoteCommunication = 'RemoteCommunication';
function isRemoteCommunication(item) {
    return exports.reflection.isInstance(item, exports.RemoteCommunication);
}
exports.isRemoteCommunication = isRemoteCommunication;
exports.RemoteCondition = 'RemoteCondition';
function isRemoteCondition(item) {
    return exports.reflection.isInstance(item, exports.RemoteCondition);
}
exports.isRemoteCondition = isRemoteCondition;
exports.Sensor = 'Sensor';
function isSensor(item) {
    return exports.reflection.isInstance(item, exports.Sensor);
}
exports.isSensor = isSensor;
exports.Signal = 'Signal';
function isSignal(item) {
    return exports.reflection.isInstance(item, exports.Signal);
}
exports.isSignal = isSignal;
exports.TimeoutCondition = 'TimeoutCondition';
function isTimeoutCondition(item) {
    return exports.reflection.isInstance(item, exports.TimeoutCondition);
}
exports.isTimeoutCondition = isTimeoutCondition;
exports.Transition = 'Transition';
function isTransition(item) {
    return exports.reflection.isInstance(item, exports.Transition);
}
exports.isTransition = isTransition;
class ArduinoMlAstReflection extends langium_1.AbstractAstReflection {
    getAllTypes() {
        return ['Action', 'Actuator', 'App', 'AtomicCondition', 'Brick', 'CompositeCondition', 'Condition', 'ErrorState', 'LogicalOperator', 'NegationOperator', 'Next', 'NormalState', 'RemoteCommunication', 'RemoteCondition', 'Sensor', 'Signal', 'State', 'TimeoutCondition', 'Transition'];
    }
    computeIsSubtype(subtype, supertype) {
        switch (subtype) {
            case exports.Actuator:
            case exports.Sensor: {
                return this.isSubtype(exports.Brick, supertype);
            }
            case exports.AtomicCondition:
            case exports.CompositeCondition:
            case exports.RemoteCondition:
            case exports.TimeoutCondition: {
                return this.isSubtype(exports.Condition, supertype);
            }
            case exports.ErrorState:
            case exports.NormalState: {
                return this.isSubtype(exports.State, supertype);
            }
            default: {
                return false;
            }
        }
    }
    getReferenceType(refInfo) {
        const referenceId = `${refInfo.container.$type}:${refInfo.property}`;
        switch (referenceId) {
            case 'Action:actuator':
            case 'ErrorState:errorLed': {
                return exports.Actuator;
            }
            case 'App:initial':
            case 'Next:nextState': {
                return exports.NormalState;
            }
            case 'AtomicCondition:sensor':
            case 'RemoteCommunication:sensor': {
                return exports.Sensor;
            }
            case 'Next:error': {
                return exports.ErrorState;
            }
            default: {
                throw new Error(`${referenceId} is not a valid reference id.`);
            }
        }
    }
    getTypeMetaData(type) {
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
            case 'NormalState': {
                return {
                    name: 'NormalState',
                    mandatory: [
                        { name: 'actions', type: 'array' },
                        { name: 'remotes', type: 'array' },
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
exports.ArduinoMlAstReflection = ArduinoMlAstReflection;
exports.reflection = new ArduinoMlAstReflection();
//# sourceMappingURL=ast.js.map