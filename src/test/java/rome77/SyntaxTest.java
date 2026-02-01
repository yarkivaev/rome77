package rome77;

import org.junit.jupiter.api.Test;
import rome77.antlr.Rome77Syntax;
import syntax.SyntaxNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for Rome77 grammar parsing.
 *
 * Each test verifies one specific parsing behavior following
 * Elegant Objects testing principles.
 */
final class SyntaxTest {

    @Test
    void parsesEmptyProgramWithZeroStatements() {
        assertThat(
            "Empty program should have no statement children",
            this.childCount(new Rome77Syntax("").parsed().right().get().root()),
            is(equalTo(1))
        );
    }

    @Test
    void parsesRomanNumeralXIV() {
        assertThat(
            "Roman numeral XIV should be parsed",
            this.findText(new Rome77Syntax("Grafo XIV").parsed().right().get().root(), "XIV"),
            is(true)
        );
    }

    @Test
    void parsesZeroAsN() {
        assertThat(
            "Zero represented as N should be parsed",
            this.findText(new Rome77Syntax("Grafo N").parsed().right().get().root(), "N"),
            is(true)
        );
    }

    @Test
    void parsesVariableDeclaration() {
        assertThat(
            "Variable declaration should produce variableDecl node",
            this.findName(new Rome77Syntax("As x = V").parsed().right().get().root(), "variableDecl"),
            is(true)
        );
    }

    @Test
    void parsesFunctionDefinitionWithOneParameter() {
        assertThat(
            "Function definition should produce functionDef node",
            this.findName(new Rome77Syntax("Munus f x = x").parsed().right().get().root(), "functionDef"),
            is(true)
        );
    }

    @Test
    void parsesFunctionDefinitionWithTwoParameters() {
        assertThat(
            "Function with two parameters should be parsed",
            this.findName(new Rome77Syntax("Munus sum a b = a + b").parsed().right().get().root(), "functionDef"),
            is(true)
        );
    }

    @Test
    void parsesOutputStatement() {
        assertThat(
            "Output statement should produce outputStmt node",
            this.findName(new Rome77Syntax("Grafo x").parsed().right().get().root(), "outputStmt"),
            is(true)
        );
    }

    @Test
    void parsesAdditionOperator() {
        assertThat(
            "Addition operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a + b").parsed().right().get().root(), "+"),
            is(true)
        );
    }

    @Test
    void parsesSubtractionOperator() {
        assertThat(
            "Subtraction operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a - I").parsed().right().get().root(), "-"),
            is(true)
        );
    }

    @Test
    void parsesMultiplicationOperator() {
        assertThat(
            "Multiplication operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a * II").parsed().right().get().root(), "*"),
            is(true)
        );
    }

    @Test
    void parsesDivisionOperator() {
        assertThat(
            "Division operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a / II").parsed().right().get().root(), "/"),
            is(true)
        );
    }

    @Test
    void parsesSinonConditional() {
        assertThat(
            "Sinon conditional should produce conditional node",
            this.findName(new Rome77Syntax("Grafo Sinon n I II").parsed().right().get().root(), "conditional"),
            is(true)
        );
    }

    @Test
    void parsesFunctionCall() {
        assertThat(
            "Function call should produce funcCall node",
            this.findName(new Rome77Syntax("Grafo fib n").parsed().right().get().root(), "funcCall"),
            is(true)
        );
    }

    @Test
    void parsesFibonacciProgram() {
        final String code = String.join(
            "\n",
            "Munus fib n = Sinon n I ((fib n - I) + (fib n - II))",
            "As n = Anagnosi",
            "Grafo fib n"
        );
        assertThat(
            "Fibonacci program should have three statements",
            this.statementCount(new Rome77Syntax(code).parsed().right().get().root()),
            is(equalTo(3))
        );
    }

    @Test
    void parsesAnagnosiInput() {
        assertThat(
            "Anagnosi input should be parsed",
            this.findText(new Rome77Syntax("As n = Anagnosi").parsed().right().get().root(), "Anagnosi"),
            is(true)
        );
    }

    @Test
    void parsesRomanNumeralMMXXIV() {
        assertThat(
            "Roman numeral MMXXIV should be parsed",
            this.findText(new Rome77Syntax("Grafo MMXXIV").parsed().right().get().root(), "MMXXIV"),
            is(true)
        );
    }

    @Test
    void returnsErrorOnInvalidSyntax() {
        assertThat(
            "Invalid syntax should return error",
            new Rome77Syntax("As = V").parsed().left().isPresent(),
            is(true)
        );
    }

    @Test
    void returnsErrorOnMissingExpression() {
        assertThat(
            "Missing expression should return error",
            new Rome77Syntax("Grafo").parsed().left().isPresent(),
            is(true)
        );
    }

    private int childCount(final SyntaxNode node) {
        int count = 0;
        for (final SyntaxNode ignored : node.children()) {
            count = count + 1;
        }
        return count;
    }

    private int statementCount(final SyntaxNode root) {
        int count = 0;
        for (final SyntaxNode child : root.children()) {
            if (child.name().equals("statement")) {
                count = count + 1;
            }
        }
        return count;
    }

    private boolean findName(final SyntaxNode node, final String target) {
        boolean found = node.name().equals(target);
        if (!found) {
            for (final SyntaxNode child : node.children()) {
                found = this.findName(child, target);
                if (found) {
                    break;
                }
            }
        }
        return found;
    }

    private boolean findText(final SyntaxNode node, final String target) {
        boolean found = node.text().equals(target);
        if (!found) {
            for (final SyntaxNode child : node.children()) {
                found = this.findText(child, target);
                if (found) {
                    break;
                }
            }
        }
        return found;
    }
}
