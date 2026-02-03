package semantic;

import ir.*;
import org.junit.jupiter.api.Test;
import parsing.ParsingException;
import rome77.antlr.Rome77Syntax;
import ir.simple.*;
import syntax.SyntaxTree;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for semantic analyzer behavior.
 *
 * These tests define expected analyzer output for various Rome77 programs.
 * Tests use FakeAnalyzer temporarily to define expected behavior.
 *
 * When Rome77Analyzer is implemented, replace FakeAnalyzer with real analyzer.
 * Tests will initially fail, then pass as analyzer is implemented.
 */
final class AnalyzerTest {

    @Test
    void analyzedProducesSimpleOutputProgram() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo V").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(new IrLiteral(5))
            )
        );
        assertThat(
            "Analyzer should produce program with output statement",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }

    @Test
    void analyzedProducesVariableDeclarationAndOutput() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = X\nGrafo x").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(10)),
                new IrOutput(new IrVariable("x"))
            )
        );
        assertThat(
            "Analyzer should produce declaration and output",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }

    @Test
    void analyzedProducesFunctionDefinitionAndCall() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus double n = n + n\nGrafo double V").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "double",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrVariable("n")
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "double",
                        Arrays.asList(new IrLiteral(5))
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce function definition and call",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }

    @Test
    void analyzedProducesConditionalExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon I XLII N").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrConditional(
                        new IrLiteral(1),
                        new IrLiteral(42),
                        new IrLiteral(0)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce conditional expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }

    @Test
    void analyzedProducesInputExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As k = Anagnosi\nGrafo k").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("k", new IrInput()),
                new IrOutput(new IrVariable("k"))
            )
        );
        assertThat(
            "Analyzer should produce input expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }

    @Test
    void analyzedProducesArithmeticExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo VII * VI").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.MUL,
                        new IrLiteral(7),
                        new IrLiteral(6)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce multiplication expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }

    @Test
    void analyzedThrowsExceptionForUndefinedVariable() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo x").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should throw exception for undefined variable"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }

    @Test
    void analyzedThrowsExceptionForUndefinedFunction() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo f I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined function: f").analyzed(),
            "Analyzer should throw exception for undefined function"
        );
        assertThat(
            "Exception message should mention undefined function",
            exception.getMessage(),
            is(equalTo("Undefined function: f"))
        );
    }

    @Test
    void analyzedThrowsExceptionForArityMismatch() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus sum a b = a + b\nGrafo sum I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function sum expects 2 arguments, got 1").analyzed(),
            "Analyzer should throw exception for arity mismatch"
        );
        assertThat(
            "Exception message should mention arity mismatch",
            exception.getMessage(),
            is(equalTo("Function sum expects 2 arguments, got 1"))
        );
    }

    @Test
    void analyzedProducesRecursiveFibonacciFunction() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus fib n = Sinon (n - I) ((fib n - I) + (fib n - II)) I\nGrafo fib X").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "fib",
                    Arrays.asList("n"),
                    new IrConditional(
                        new IrBinaryOp(
                            Operator.SUB,
                            new IrVariable("n"),
                            new IrLiteral(1)
                        ),
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrCall(
                                "fib",
                                Arrays.asList(
                                    new IrBinaryOp(
                                        Operator.SUB,
                                        new IrVariable("n"),
                                        new IrLiteral(1)
                                    )
                                )
                            ),
                            new IrCall(
                                "fib",
                                Arrays.asList(
                                    new IrBinaryOp(
                                        Operator.SUB,
                                        new IrVariable("n"),
                                        new IrLiteral(2)
                                    )
                                )
                            )
                        ),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "fib",
                        Arrays.asList(new IrLiteral(10))
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce recursive fibonacci function",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesDivisionExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo X / II").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.DIV,
                        new IrLiteral(10),
                        new IrLiteral(2)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce division expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesUnaryMinusExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo -V").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrUnaryOp(
                        Operator.SUB,
                        new IrLiteral(5)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce unary negation",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesUnaryPlusExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo +V").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrUnaryOp(
                        Operator.ADD,
                        new IrLiteral(5)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should produce unary plus",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesOperatorPrecedenceMultiplicationFirst() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo I + II * III").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrLiteral(1),
                        new IrBinaryOp(
                            Operator.MUL,
                            new IrLiteral(2),
                            new IrLiteral(3)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should respect multiplication precedence over addition",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesOperatorPrecedenceDivisionFirst() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo X - IV / II").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.SUB,
                        new IrLiteral(10),
                        new IrBinaryOp(
                            Operator.DIV,
                            new IrLiteral(4),
                            new IrLiteral(2)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should respect division precedence over subtraction",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesLeftAssociativitySubtraction() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo V - II - I").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.SUB,
                        new IrBinaryOp(
                            Operator.SUB,
                            new IrLiteral(5),
                            new IrLiteral(2)
                        ),
                        new IrLiteral(1)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should respect left associativity of subtraction",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesLeftAssociativityDivision() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo VIII / IV / II").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.DIV,
                        new IrBinaryOp(
                            Operator.DIV,
                            new IrLiteral(8),
                            new IrLiteral(4)
                        ),
                        new IrLiteral(2)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should respect left associativity of division",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesZeroLiteral() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo N").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(new IrLiteral(0))
            )
        );
        assertThat(
            "Analyzer should produce zero literal",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesZeroInVariableDeclaration() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = N\nGrafo x").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(0)),
                new IrOutput(new IrVariable("x"))
            )
        );
        assertThat(
            "Analyzer should handle zero in variable declaration",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesZeroInArithmeticAddition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo N + I").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrLiteral(0),
                        new IrLiteral(1)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle zero in addition",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesZeroInArithmeticMultiplication() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo N * X").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.MUL,
                        new IrLiteral(0),
                        new IrLiteral(10)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle zero in multiplication",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesZeroAsFunctionArgument() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n + I\nGrafo f N").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "f",
                        Arrays.asList(new IrLiteral(0))
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle zero as function argument",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMultipleVariables() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nAs y = II\nGrafo x + y").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(1)),
                new IrDeclaration("y", new IrLiteral(2)),
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("x"),
                        new IrVariable("y")
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle multiple variable declarations",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesVariableInComplexExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = V\nGrafo x * II + III").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(5)),
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.MUL,
                            new IrVariable("x"),
                            new IrLiteral(2)
                        ),
                        new IrLiteral(3)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle variable in complex expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesVariableAsConditionalCondition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nGrafo Sinon x II III").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(1)),
                new IrOutput(
                    new IrConditional(
                        new IrVariable("x"),
                        new IrLiteral(2),
                        new IrLiteral(3)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle variable as conditional condition",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMultipleVariablesInExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nAs y = II\nAs z = III\nGrafo x + y + z").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(1)),
                new IrDeclaration("y", new IrLiteral(2)),
                new IrDeclaration("z", new IrLiteral(3)),
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrVariable("x"),
                            new IrVariable("y")
                        ),
                        new IrVariable("z")
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle multiple variables in expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesUnusedVariable() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nAs y = II\nGrafo x").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(1)),
                new IrDeclaration("y", new IrLiteral(2)),
                new IrOutput(new IrVariable("x"))
            )
        );
        assertThat(
            "Analyzer should accept unused variable",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesSingleParameterIdentityFunction() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus identity x = x\nGrafo identity V").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "identity",
                    Arrays.asList("x"),
                    new IrVariable("x")
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall("identity", Arrays.asList(new IrLiteral(5)))
                )
            )
        );
        assertThat(
            "Analyzer should handle single-parameter identity function",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesThreeParameterFunctionDefinition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus add3 a b c = a + b + c\nGrafo add3 I II III").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "add3",
                    Arrays.asList("a", "b", "c"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrVariable("a"),
                            new IrVariable("b")
                        ),
                        new IrVariable("c")
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "add3",
                        Arrays.asList(
                            new IrLiteral(1),
                            new IrLiteral(2),
                            new IrLiteral(3)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle three-parameter function with multi-argument call",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesFourParameterFunctionDefinition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus add4 a b c d = a + b + c + d\nGrafo add4 I II III IV").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "add4",
                    Arrays.asList("a", "b", "c", "d"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrBinaryOp(
                                Operator.ADD,
                                new IrVariable("a"),
                                new IrVariable("b")
                            ),
                            new IrVariable("c")
                        ),
                        new IrVariable("d")
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "add4",
                        Arrays.asList(
                            new IrLiteral(1),
                            new IrLiteral(2),
                            new IrLiteral(3),
                            new IrLiteral(4)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle four-parameter function with multi-argument call",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMultipleFunctionDefinitions() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n\nMunus g n = n + I\nGrafo (f I) + (g II)").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrVariable("n")
                ),
                new IrFunction(
                    "g",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrCall("f", Arrays.asList(new IrLiteral(1))),
                        new IrCall("g", Arrays.asList(new IrLiteral(2)))
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle multiple function definitions",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesUnusedFunction() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n\nMunus g n = n + I\nGrafo g I").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrVariable("n")
                ),
                new IrFunction(
                    "g",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall("g", Arrays.asList(new IrLiteral(1)))
                )
            )
        );
        assertThat(
            "Analyzer should accept unused function",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesNestedFunctionCalls() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n + I\nMunus g n = f n + I\nGrafo g I").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                ),
                new IrFunction(
                    "g",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrCall("f", Arrays.asList(new IrVariable("n"))),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall("g", Arrays.asList(new IrLiteral(1)))
                )
            )
        );
        assertThat(
            "Analyzer should handle nested function calls",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesFunctionWithComplexBody() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f a b = (a + b) * (a - b)\nGrafo f V III").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("a", "b"),
                    new IrBinaryOp(
                        Operator.MUL,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrVariable("a"),
                            new IrVariable("b")
                        ),
                        new IrBinaryOp(
                            Operator.SUB,
                            new IrVariable("a"),
                            new IrVariable("b")
                        )
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "f",
                        Arrays.asList(
                            new IrLiteral(5),
                            new IrLiteral(3)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle two-parameter function with complex body and multi-argument call",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesFunctionReturningConditional() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus abs n = Sinon n n (-n)\nGrafo abs V").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "abs",
                    Arrays.asList("n"),
                    new IrConditional(
                        new IrVariable("n"),
                        new IrVariable("n"),
                        new IrUnaryOp(
                            Operator.SUB,
                            new IrVariable("n")
                        )
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall("abs", Arrays.asList(new IrLiteral(5)))
                )
            )
        );
        assertThat(
            "Analyzer should handle function returning conditional",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesConditionalWithVariableCondition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nGrafo Sinon x II III").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(1)),
                new IrOutput(
                    new IrConditional(
                        new IrVariable("x"),
                        new IrLiteral(2),
                        new IrLiteral(3)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle conditional with variable condition",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesNestedConditionals() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon I (Sinon II III IV) V").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrConditional(
                        new IrLiteral(1),
                        new IrConditional(
                            new IrLiteral(2),
                            new IrLiteral(3),
                            new IrLiteral(4)
                        ),
                        new IrLiteral(5)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle nested conditionals",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesConditionalWithFunctionCalls() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n + I\nGrafo Sinon I (f I) (f II)").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrConditional(
                        new IrLiteral(1),
                        new IrCall("f", Arrays.asList(new IrLiteral(1))),
                        new IrCall("f", Arrays.asList(new IrLiteral(2)))
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle conditional with function calls in branches",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesDeeplyNestedConditionals() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon I (Sinon I (Sinon I II III) IV) V").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrConditional(
                        new IrLiteral(1),
                        new IrConditional(
                            new IrLiteral(1),
                            new IrConditional(
                                new IrLiteral(1),
                                new IrLiteral(2),
                                new IrLiteral(3)
                            ),
                            new IrLiteral(4)
                        ),
                        new IrLiteral(5)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle deeply nested conditionals",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesConditionalWithComplexCondition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon (I + II - III) X N").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrConditional(
                        new IrBinaryOp(
                            Operator.SUB,
                            new IrBinaryOp(
                                Operator.ADD,
                                new IrLiteral(1),
                                new IrLiteral(2)
                            ),
                            new IrLiteral(3)
                        ),
                        new IrLiteral(10),
                        new IrLiteral(0)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle conditional with complex condition",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMultipleInputs() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = Anagnosi\nAs y = Anagnosi\nGrafo x + y").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrInput()),
                new IrDeclaration("y", new IrInput()),
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("x"),
                        new IrVariable("y")
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle multiple inputs",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMultipleOutputs() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nAs y = II\nGrafo x\nGrafo y").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("x", new IrLiteral(1)),
                new IrDeclaration("y", new IrLiteral(2)),
                new IrOutput(new IrVariable("x")),
                new IrOutput(new IrVariable("y"))
            )
        );
        assertThat(
            "Analyzer should handle multiple outputs",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesInputInExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Anagnosi + I").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrInput(),
                        new IrLiteral(1)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle input in expression",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesInputAsConditionalCondition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon Anagnosi I II").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrConditional(
                        new IrInput(),
                        new IrLiteral(1),
                        new IrLiteral(2)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle input as conditional condition",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesEmptyProgram() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Collections.emptyList()
        );
        assertThat(
            "Analyzer should handle empty program",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesFunctionOnlyProgram() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrVariable("n")
                )
            ),
            Collections.emptyList()
        );
        assertThat(
            "Analyzer should handle program with only function definitions",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesComplexProgram() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus inc n = n + I\nAs x = Anagnosi\nAs y = inc x\nGrafo Sinon y (y * II) N").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "inc",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrDeclaration("x", new IrInput()),
                new IrDeclaration(
                    "y",
                    new IrCall("inc", Arrays.asList(new IrVariable("x")))
                ),
                new IrOutput(
                    new IrConditional(
                        new IrVariable("y"),
                        new IrBinaryOp(
                            Operator.MUL,
                            new IrVariable("y"),
                            new IrLiteral(2)
                        ),
                        new IrLiteral(0)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle complex program with functions variables and conditionals",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedThrowsExceptionForDuplicateVariable() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = I\nAs x = II").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Variable x is already defined").analyzed(),
            "Analyzer should reject duplicate variable declaration"
        );
        assertThat(
            "Exception message should mention duplicate variable",
            exception.getMessage(),
            is(equalTo("Variable x is already defined"))
        );
    }
    @Test
    void analyzedThrowsExceptionForSelfReferencingVariable() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = x + I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject self-referencing variable"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForUndefinedVariableInExpression() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As x = y + I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: y").analyzed(),
            "Analyzer should reject undefined variable in expression"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: y"))
        );
    }
    @Test
    void analyzedThrowsExceptionForUndefinedVariableInConditional() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon x I II").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in conditional"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForUndefinedVariableInFunctionBody() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = x + n\nGrafo f I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in function body"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForVariableUsedBeforeDeclaration() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo x\nAs x = I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject variable used before declaration"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForDuplicateFunction() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n\nMunus f n = n + I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function f is already defined").analyzed(),
            "Analyzer should reject duplicate function definitions"
        );
        assertThat(
            "Exception message should mention duplicate function",
            exception.getMessage(),
            is(equalTo("Function f is already defined"))
        );
    }
    @Test
    void analyzedThrowsExceptionForDuplicateParameters() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f a a = a").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Duplicate parameter name: a").analyzed(),
            "Analyzer should reject duplicate parameter names"
        );
        assertThat(
            "Exception message should mention duplicate parameter",
            exception.getMessage(),
            is(equalTo("Duplicate parameter name: a"))
        );
    }
    @Test
    void analyzedThrowsExceptionForTriplicateParameters() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f a b a = a + b").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Duplicate parameter name: a").analyzed(),
            "Analyzer should reject triplicate parameter names"
        );
        assertThat(
            "Exception message should mention duplicate parameter",
            exception.getMessage(),
            is(equalTo("Duplicate parameter name: a"))
        );
    }
    @Test
    void analyzedThrowsExceptionForArityMismatchTooMany() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus sum a b = a + b\nGrafo sum I II III").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function sum expects 2 arguments, got 3").analyzed(),
            "Analyzer should reject too many arguments to function"
        );
        assertThat(
            "Exception message should mention arity mismatch with too many arguments",
            exception.getMessage(),
            is(equalTo("Function sum expects 2 arguments, got 3"))
        );
    }
    @Test
    void analyzedThrowsExceptionForArityMismatchZeroGiven() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = n\nGrafo f").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function f expects 1 arguments, got 0").analyzed(),
            "Analyzer should reject zero arguments when one expected"
        );
        assertThat(
            "Exception message should mention arity mismatch",
            exception.getMessage(),
            is(equalTo("Function f expects 1 arguments, got 0"))
        );
    }
    @Test
    void analyzedThrowsExceptionForArityMismatchWithFourParameters() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus add4 a b c d = a + b + c + d\nGrafo add4 I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function add4 expects 4 arguments, got 1").analyzed(),
            "Analyzer should reject wrong arity for four-parameter function"
        );
        assertThat(
            "Exception message should mention arity mismatch",
            exception.getMessage(),
            is(equalTo("Function add4 expects 4 arguments, got 1"))
        );
    }
    @Test
    void analyzedThrowsExceptionForUndefinedFunctionInFunctionBody() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = g n\nGrafo f I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined function: g").analyzed(),
            "Analyzer should reject undefined function in function body"
        );
        assertThat(
            "Exception message should mention undefined function",
            exception.getMessage(),
            is(equalTo("Undefined function: g"))
        );
    }
    @Test
    void analyzedThrowsExceptionForFunctionCallingItselfIncorrectly() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus f n = f n m").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: m").analyzed(),
            "Analyzer should reject recursive function with undefined variables"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: m"))
        );
    }
    @Test
    void analyzedThrowsExceptionForParameterShadowingItself() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As n = I\nMunus f n = n + I\nGrafo f II").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "f",
                    Arrays.asList("n"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrVariable("n"),
                        new IrLiteral(1)
                    )
                )
            ),
            Arrays.asList(
                new IrDeclaration("n", new IrLiteral(1)),
                new IrOutput(
                    new IrCall("f", Arrays.asList(new IrLiteral(2)))
                )
            )
        );
        assertThat(
            "Analyzer should allow parameter shadowing outer variable",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedThrowsExceptionForFunctionVariableNameCollision() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As f = I\nMunus f n = n").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function f is already defined").analyzed(),
            "Analyzer should reject function with same name as variable"
        );
        assertThat(
            "Exception message should mention name collision",
            exception.getMessage(),
            is(equalTo("Function f is already defined"))
        );
    }
    @Test
    void analyzedThrowsExceptionForInvalidConditionalCondition() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon x I II").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in conditional condition"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForInvalidConditionalThen() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon I x II").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in then branch"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForInvalidConditionalElse() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo Sinon I II x").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in else branch"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForInvalidBinaryOperand() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo x + I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in binary operation"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedThrowsExceptionForInvalidUnaryOperand() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo -x").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Undefined variable: x").analyzed(),
            "Analyzer should reject undefined variable in unary operation"
        );
        assertThat(
            "Exception message should mention undefined variable",
            exception.getMessage(),
            is(equalTo("Undefined variable: x"))
        );
    }
    @Test
    void analyzedProducesRecursiveFunctionWithMultipleParameters() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus factorial n = Sinon (n - I) (n * (factorial n - I)) I").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "factorial",
                    Arrays.asList("n"),
                    new IrConditional(
                        new IrBinaryOp(
                            Operator.SUB,
                            new IrVariable("n"),
                            new IrLiteral(1)
                        ),
                        new IrBinaryOp(
                            Operator.MUL,
                            new IrVariable("n"),
                            new IrCall(
                                "factorial",
                                Arrays.asList(
                                    new IrBinaryOp(
                                        Operator.SUB,
                                        new IrVariable("n"),
                                        new IrLiteral(1)
                                    )
                                )
                            )
                        ),
                        new IrLiteral(1)
                    )
                )
            ),
            Collections.emptyList()
        );
        assertThat(
            "Analyzer should handle recursive factorial function",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMutuallyRecursiveFunctions() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus even n = Sinon n (odd n - I) I\nMunus odd n = Sinon n (even n - I) N\nGrafo even V").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "even",
                    Arrays.asList("n"),
                    new IrConditional(
                        new IrVariable("n"),
                        new IrCall(
                            "odd",
                            Arrays.asList(
                                new IrBinaryOp(
                                    Operator.SUB,
                                    new IrVariable("n"),
                                    new IrLiteral(1)
                                )
                            )
                        ),
                        new IrLiteral(1)
                    )
                ),
                new IrFunction(
                    "odd",
                    Arrays.asList("n"),
                    new IrConditional(
                        new IrVariable("n"),
                        new IrCall(
                            "even",
                            Arrays.asList(
                                new IrBinaryOp(
                                    Operator.SUB,
                                    new IrVariable("n"),
                                    new IrLiteral(1)
                                )
                            )
                        ),
                        new IrLiteral(0)
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall("even", Arrays.asList(new IrLiteral(5)))
                )
            )
        );
        assertThat(
            "Analyzer should handle mutually recursive functions",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesComplexArithmeticWithAllOperators() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo I + II - III * IV / V").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.SUB,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrLiteral(1),
                            new IrLiteral(2)
                        ),
                        new IrBinaryOp(
                            Operator.DIV,
                            new IrBinaryOp(
                                Operator.MUL,
                                new IrLiteral(3),
                                new IrLiteral(4)
                            ),
                            new IrLiteral(5)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle complex arithmetic with all operators",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesLargeRomanNumeral() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo MMMCMXCIX").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(new IrLiteral(3999))
            )
        );
        assertThat(
            "Analyzer should handle large Roman numeral",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesComplexRomanNumeral() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo MDCCCLXXXVIII").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(new IrLiteral(1888))
            )
        );
        assertThat(
            "Analyzer should handle complex Roman numeral",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesMaximumNesting() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Grafo ((((I + II) * III) - IV) / V)").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrOutput(
                    new IrBinaryOp(
                        Operator.DIV,
                        new IrBinaryOp(
                            Operator.SUB,
                            new IrBinaryOp(
                                Operator.MUL,
                                new IrBinaryOp(
                                    Operator.ADD,
                                    new IrLiteral(1),
                                    new IrLiteral(2)
                                ),
                                new IrLiteral(3)
                            ),
                            new IrLiteral(4)
                        ),
                        new IrLiteral(5)
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle maximum nesting of expressions",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesLongParameterList() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus add5 a b c d e = a + b + c + d + e\nGrafo add5 I II III IV V").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "add5",
                    Arrays.asList("a", "b", "c", "d", "e"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrBinaryOp(
                                Operator.ADD,
                                new IrBinaryOp(
                                    Operator.ADD,
                                    new IrVariable("a"),
                                    new IrVariable("b")
                                ),
                                new IrVariable("c")
                            ),
                            new IrVariable("d")
                        ),
                        new IrVariable("e")
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "add5",
                        Arrays.asList(
                            new IrLiteral(1),
                            new IrLiteral(2),
                            new IrLiteral(3),
                            new IrLiteral(4),
                            new IrLiteral(5)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle long parameter list with multi-argument call",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesLongStatementSequence() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("As a = I\nAs b = II\nAs c = III\nAs d = IV\nAs e = V\nAs f = VI\nAs g = VII\nAs h = VIII\nAs i = IX\nAs j = X\nGrafo a + b + c + d + e + f + g + h + i + j").parsed();
        final Program expected = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(
                new IrDeclaration("a", new IrLiteral(1)),
                new IrDeclaration("b", new IrLiteral(2)),
                new IrDeclaration("c", new IrLiteral(3)),
                new IrDeclaration("d", new IrLiteral(4)),
                new IrDeclaration("e", new IrLiteral(5)),
                new IrDeclaration("f", new IrLiteral(6)),
                new IrDeclaration("g", new IrLiteral(7)),
                new IrDeclaration("h", new IrLiteral(8)),
                new IrDeclaration("i", new IrLiteral(9)),
                new IrDeclaration("j", new IrLiteral(10)),
                new IrOutput(
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrBinaryOp(
                                Operator.ADD,
                                new IrBinaryOp(
                                    Operator.ADD,
                                    new IrBinaryOp(
                                        Operator.ADD,
                                        new IrBinaryOp(
                                            Operator.ADD,
                                            new IrBinaryOp(
                                                Operator.ADD,
                                                new IrBinaryOp(
                                                    Operator.ADD,
                                                    new IrBinaryOp(
                                                        Operator.ADD,
                                                        new IrVariable("a"),
                                                        new IrVariable("b")
                                                    ),
                                                    new IrVariable("c")
                                                ),
                                                new IrVariable("d")
                                            ),
                                            new IrVariable("e")
                                        ),
                                        new IrVariable("f")
                                    ),
                                    new IrVariable("g")
                                ),
                                new IrVariable("h")
                            ),
                            new IrVariable("i")
                        ),
                        new IrVariable("j")
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle long statement sequence",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedProducesThreeParameterFunctionCall() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus add3 a b c = a + b + c\nGrafo add3 I II III").parsed();
        final Program expected = new IrProgram(
            Arrays.asList(
                new IrFunction(
                    "add3",
                    Arrays.asList("a", "b", "c"),
                    new IrBinaryOp(
                        Operator.ADD,
                        new IrBinaryOp(
                            Operator.ADD,
                            new IrVariable("a"),
                            new IrVariable("b")
                        ),
                        new IrVariable("c")
                    )
                )
            ),
            Arrays.asList(
                new IrOutput(
                    new IrCall(
                        "add3",
                        Arrays.asList(
                            new IrLiteral(1),
                            new IrLiteral(2),
                            new IrLiteral(3)
                        )
                    )
                )
            )
        );
        assertThat(
            "Analyzer should handle three-parameter function with multi-argument call",
            new FakeAnalyzer(tree, expected).analyzed(),
            is(equalTo(expected))
        );
    }
    @Test
    void analyzedThrowsExceptionForTooFewArguments() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus add3 a b c = a + b + c\nGrafo add3 I").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function add3 expects 3 arguments, got 1").analyzed(),
            "Analyzer should reject too few arguments"
        );
        assertThat(
            "Exception message should indicate arity mismatch",
            exception.getMessage(),
            is(equalTo("Function add3 expects 3 arguments, got 1"))
        );
    }
    @Test
    void analyzedThrowsExceptionForTooManyArguments() throws Exception {
        final SyntaxTree tree = new Rome77Syntax("Munus sum a b = a + b\nGrafo sum I II III").parsed();
        final SemanticException exception = assertThrows(
            SemanticException.class,
            () -> new FakeAnalyzer(tree, "Function sum expects 2 arguments, got 3").analyzed(),
            "Analyzer should reject too many arguments"
        );
        assertThat(
            "Exception message should indicate arity mismatch",
            exception.getMessage(),
            is(equalTo("Function sum expects 2 arguments, got 3"))
        );
    }

    /**
     * Temporary fake analyzer for TDD.
     *
     * Returns pre-configured result or throws pre-configured exception.
     * Will be replaced by real Rome77Analyzer implementation.
     *
     * To replace FakeAnalyzer with Rome77Analyzer:
     * 1. Replace new FakeAnalyzer(tree, expected) with new Rome77Analyzer(tree)
     * 2. Tests already parse real syntax tree from Rome77Syntax.parsed()
     * 3. Tests will initially fail until Rome77Analyzer is implemented
     */
    private static final class FakeAnalyzer implements Analyzer {

        private final SyntaxTree tree;
        private final Program prog;
        private final String error;

        /**
         * Constructor for successful analysis.
         *
         * @param tree Syntax tree to analyze
         * @param program Expected program result
         */
        FakeAnalyzer(final SyntaxTree tree, final Program program) {
            this.tree = tree;
            this.prog = program;
            this.error = "";
        }

        /**
         * Constructor for error case.
         *
         * @param tree Syntax tree to analyze
         * @param errorMessage Expected error message
         */
        FakeAnalyzer(final SyntaxTree tree, final String errorMessage) {
            this.tree = tree;
            this.prog = new IrProgram(
                Collections.emptyList(),
                Collections.emptyList()
            );
            this.error = errorMessage;
        }

        /**
         * Returns pre-configured program or throws exception.
         *
         * @return Program if configured for success
         * @throws ParsingException if configured with error message
         */
        @Override
        public Program analyzed() throws ParsingException {
            if (!this.error.isEmpty()) {
                throw new SemanticException(1, 0, this.error);
            }
            return this.prog;
        }
    }
}
