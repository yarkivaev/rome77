/**
 * ANTLR4 Grammar for Rome77 Programming Language.
 *
 * A language using Roman numerals as integers and Latin-inspired keywords.
 * Arithmetic operators have higher precedence than function application,
 * so `fib n - I` parses as `fib(n - I)`.
 *
 * Example program:
 *   Munus fib n = Sinon n I ((fib n - I) + (fib n - II))
 *   As n = Anagnosi
 *   Grafo fib n
 */
grammar Rome77;

program
    : statement* EOF
    ;

statement
    : functionDef
    | variableDecl
    | outputStmt
    ;

/**
 * Function definition: Munus name params = body.
 * Parameters are identifiers, body is an expression.
 */
functionDef
    : MUNUS IDENTIFIER params EQUALS expr
    ;

params
    : IDENTIFIER+
    ;

/**
 * Variable declaration: As name = expression.
 */
variableDecl
    : AS IDENTIFIER EQUALS expr
    ;

/**
 * Output statement: Grafo expression.
 */
outputStmt
    : GRAFO expr
    ;

/**
 * Expression with function application.
 * Functions are called with one or more arguments: f a, f a b, f a b c
 */
expr
    : SINON expr expr expr                          # Conditional
    | additive                                      # Arithmetic
    ;

/**
 * Additive expressions: addition and subtraction.
 */
additive
    : additive op=(PLUS | MINUS) multiplicative     # AddSub
    | multiplicative                                # ToMult
    ;

/**
 * Multiplicative expressions: multiplication and division.
 */
multiplicative
    : multiplicative op=(MULT | DIV) application
    | application
    ;

/**
 * application prefix operators.
 */
application
    : op=(PLUS | MINUS) application                 # UnaryOp
    | primary (primary)+                            # FuncCall
    | primary                                       # ToPrimary
    ;

/**
 * Primary expressions: atoms and parenthesized expressions.
 */
primary
    : ROMAN                                         # RomanLiteral
    | IDENTIFIER                                    # Variable
    | ANAGNOSI                                      # ReadInput
    | LPAREN expr RPAREN                            # Parens
    ;

// ============ LEXER RULES ============

AS       : 'As' ;
MUNUS    : 'Munus' ;
GRAFO    : 'Grafo' ;
ANAGNOSI : 'Anagnosi' ;
SINON    : 'Sinon' ;

PLUS     : '+' ;
MINUS    : '-' ;
MULT     : '*' ;
DIV      : '/' ;
EQUALS   : '=' ;
LPAREN   : '(' ;
RPAREN   : ')' ;

/**
 * Roman numeral token.
 * N represents zero. Standard numerals: I V X L C D M.
 * Accepts any combination; semantic validation can enforce strict rules.
 */
ROMAN
    : 'N'
    | [MDCLXVI]+
    ;

/**
 * Identifier: lowercase letters only.
 */
IDENTIFIER
    : [a-z]+[a-z0-9]*
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;
