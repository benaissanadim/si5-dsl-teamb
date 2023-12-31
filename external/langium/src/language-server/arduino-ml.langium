grammar ArduinoMl

entry App:
	'app' name=ID 'initial' 'state' initial=[NormalState:ID]
	'{'
		'bricks'
		bricks+=Brick
		(bricks+=Brick)* 
		
		'states'
		states+=State
		( states+=State)* 		
	'}';

Brick :
	(Actuator | Sensor);

Actuator:
	{infer Actuator}
	'Actuator' name=ID ':' outputPin=INT;

Sensor :
	{infer Sensor}
	'Sensor'  name=ID ':' inputPin=INT;


State :
	  NormalState | ErrorState ;

NormalState :
	name=ID '{' 
		actions+=Action
		(actions+=Action)* 
		(remotes+=RemoteCommunication)*
		(transitions+=Transition)*
	'}'
	;

ErrorState :
	name=ID '{'
		"errorNumber" ':' errorNumber=INT
		"pauseTime" ':' pauseTime=INT
		"errorLed" ':' errorLed=[Actuator:ID]
		'}'
	;
	
Action :
	actuator=[Actuator:ID] '<=' value=Signal;

LogicalOperator:
	AND = 'and' |
	OR = 'or' |
	XOR = 'xor';

NegationOperator:
	NOT = 'not';

Transition :
	condition=Condition '=>' next=Next;

Next :
	nextState=[NormalState:ID] | 'throw' error=[ErrorState:ID];

Condition :
	AtomicCondition | CompositeCondition | TimeoutCondition | RemoteCondition; 

RemoteCondition :
	 key=CHAR 'is' 'pressed';

RemoteCommunication :
	'reading' sensor=[Sensor:ID];

TimeoutCondition :
	'after' duration=INT 'ms';

AtomicCondition :
	sensor=[Sensor:ID] 'is' (ne=NegationOperator)? value=Signal;

CompositeCondition :
	'(' left=Condition op=LogicalOperator right=Condition ')';

Signal:
    value=(HIGH | LOW);

terminal HIGH : 'HIGH';
terminal LOW : 'LOW';

hidden terminal WS: /\s+/;
terminal ID: /[_a-zA-Z][\w_]*/;
terminal INT returns number: /[0-9]+/;
terminal STRING: /"[^"]*"|'[^']*'/;
terminal CHAR: /'.'/;

hidden terminal ML_COMMENT: /\/\*[\s\S]*?\*\//;
hidden terminal SL_COMMENT: /\/\/[^\n\r]*/;
