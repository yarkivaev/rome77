package syntax;

import org.junit.jupiter.api.Test;
import parsing.ParsingException;
import rome77.antlr.Rome77Syntax;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for Rome77 grammar parsing.
 *
 * Each test verifies one specific parsing behavior following
 * Elegant Objects testing principles.
 */
final class SyntaxTest {

    @Test
    void parsesEmptyProgramWithZeroStatements() throws ParsingException {
        assertThat(
            "Empty program should have no statement children",
            this.childCount(new Rome77Syntax("").parsed().root()),
            is(equalTo(1))
        );
    }

    @Test
    void parsesRomanNumeralXIV() throws ParsingException {
        assertThat(
            "Roman numeral XIV should be parsed",
            this.findText(new Rome77Syntax("Grafo XIV").parsed().root(), "XIV"),
            is(true)
        );
    }

    @Test
    void parsesZeroAsN() throws ParsingException {
        assertThat(
            "Zero represented as N should be parsed",
            this.findText(new Rome77Syntax("Grafo N").parsed().root(), "N"),
            is(true)
        );
    }

    @Test
    void parsesVariableDeclaration() throws ParsingException {
        assertThat(
            "Variable declaration should produce variableDecl node",
            this.findName(new Rome77Syntax("As x = V").parsed().root(), "variableDecl"),
            is(true)
        );
    }

    @Test
    void parsesFunctionDefinitionWithOneParameter() throws ParsingException {
        assertThat(
            "Function definition should produce functionDef node",
            this.findName(new Rome77Syntax("Munus f x = x").parsed().root(), "functionDef"),
            is(true)
        );
    }

    @Test
    void parsesFunctionDefinitionWithTwoParameters() throws ParsingException {
        assertThat(
            "Function with two parameters should be parsed",
            this.findName(new Rome77Syntax("Munus sum a b = a + b").parsed().root(), "functionDef"),
            is(true)
        );
    }

    @Test
    void parsesOutputStatement() throws ParsingException {
        assertThat(
            "Output statement should produce outputStmt node",
            this.findName(new Rome77Syntax("Grafo x").parsed().root(), "outputStmt"),
            is(true)
        );
    }

    @Test
    void parsesAdditionOperator() throws ParsingException {
        assertThat(
            "Addition operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a + b").parsed().root(), "+"),
            is(true)
        );
    }

    @Test
    void parsesSubtractionOperator() throws ParsingException {
        assertThat(
            "Subtraction operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a - I").parsed().root(), "-"),
            is(true)
        );
    }

    @Test
    void parsesMultiplicationOperator() throws ParsingException {
        assertThat(
            "Multiplication operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a * II").parsed().root(), "*"),
            is(true)
        );
    }

    @Test
    void parsesDivisionOperator() throws ParsingException {
        assertThat(
            "Division operator should be parsed",
            this.findText(new Rome77Syntax("Grafo a / II").parsed().root(), "/"),
            is(true)
        );
    }

    @Test
    void parsesSinonConditional() throws ParsingException {
        assertThat(
            "Sinon conditional should produce conditional node",
            this.findName(new Rome77Syntax("Grafo Sinon n I II").parsed().root(), "conditional"),
            is(true)
        );
    }

    @Test
    void parsesFunctionCall() throws ParsingException {
        assertThat(
            "Function call should produce funcCall node",
            this.findName(new Rome77Syntax("Grafo fib n").parsed().root(), "funcCall"),
            is(true)
        );
    }

    @Test
    void parsesFibonacciProgram() throws ParsingException {
        final String code = String.join(
            "\n",
            "Munus fib n = Sinon n I ((fib n - I) + (fib n - II))",
            "As n = Anagnosi",
            "Grafo fib n"
        );
        assertThat(
            "Fibonacci program should have three statements",
            this.statementCount(new Rome77Syntax(code).parsed().root()),
            is(equalTo(3))
        );
    }

    @Test
    void parsesAnagnosiInput() throws ParsingException {
        assertThat(
            "Anagnosi input should be parsed",
            this.findText(new Rome77Syntax("As n = Anagnosi").parsed().root(), "Anagnosi"),
            is(true)
        );
    }

    @Test
    void parsesRomanNumeralMMXXIV() throws ParsingException {
        assertThat(
            "Roman numeral MMXXIV should be parsed",
            this.findText(new Rome77Syntax("Grafo MMXXIV").parsed().root(), "MMXXIV"),
            is(true)
        );
    }

    @Test
    void throwsExceptionOnInvalidSyntax() {
        assertThrows(
            SyntaxException.class,
            () -> new Rome77Syntax("As = V").parsed()
        );
    }

    @Test
    void throwsExceptionOnMissingExpression() {
        assertThrows(
            SyntaxException.class,
            () -> new Rome77Syntax("Grafo").parsed()
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
