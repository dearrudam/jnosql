grammar Method;
select: selectStart where? order? EOF;
deleteBy: 'deleteBy' where? EOF;

selectStart: 'find' limit 'By' | 'findBy' | 'countAll' | 'countBy' | 'existsBy';
where: condition (and condition| or condition)* ;
condition: eq | gt | gte | lt | lte | between | in | like | truth | untruth | nullable | contains | endsWith | startsWith;
order: 'OrderBy' orderName (orderName)*;
orderName: variable | variable asc | variable desc;
limit : 'First' max?;
and: 'And';
or: 'Or';
asc: 'Asc';
desc: 'Desc';
truth: variable 'True';
untruth: variable 'False';
eq: variable | variable ignoreCase? not? 'Equals'?;
gt: variable ignoreCase? not? 'GreaterThan';
gte: variable ignoreCase? not? 'GreaterThanEqual';
lt: variable ignoreCase? not? 'LessThan';
lte: variable ignoreCase? not? 'LessThanEqual';
between: variable ignoreCase? not? 'Between';
in: variable ignoreCase? not? 'In';
like: variable ignoreCase? not? 'Like';
contains: variable ignoreCase? not? 'Contains';
endsWith: variable ignoreCase? not? 'EndsWith';
startsWith: variable ignoreCase? not? 'StartsWith';
nullable: variable ignoreCase? not? 'Null';
ignoreCase: 'IgnoreCase';
not: 'Not';
variable: ANY_NAME;
max: INT;
ANY_NAME: [a-zA-Z_.][a-zA-Z_.0-9-]*;
WS: [ \t\r\n]+ -> skip ;
INT: [0-9]+;
fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;