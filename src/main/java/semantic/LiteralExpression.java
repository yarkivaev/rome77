package semantic;

import ir.Expression;
import ir.simple.IrLiteral;
import syntax.SyntaxNode;

/**
 * Analyzes a romanLiteral syntax node into an IR literal.
 *
 * Parses the Roman numeral text using subtractive notation
 * and produces an {@link IrLiteral} with the integer value.
 *
 * Example usage:
 * <pre>
 * Expression lit = new LiteralExpression(node).expression();
 * </pre>
 */
public final class LiteralExpression implements NodeExpression {

    private final SyntaxNode node;

    /**
     * Primary constructor.
     *
     * @param node romanLiteral syntax node
     */
    public LiteralExpression(final SyntaxNode node) {
        this.node = node;
    }

    @Override
    public Expression expression() throws SemanticException {
        return new IrLiteral(this.parsed());
    }

    /**
     * Parses the Roman numeral text into an integer.
     *
     * @return non-negative integer value
     * @throws SemanticException if an invalid Roman digit is encountered
     */
    private int parsed() throws SemanticException {
        final String roman = this.node.text();
        if (roman.equals("N")) {
            return 0;
        }
        int result = 0;
        int prev = 0;
        for (int i = roman.length() - 1; i >= 0; i--) {
            final int digit = this.digit(roman.charAt(i));
            if (digit < prev) {
                result -= digit;
            } else {
                result += digit;
                prev = digit;
            }
        }
        return Math.max(result, 0);
    }

    /**
     * Returns the integer value of a single Roman digit.
     *
     * @param ch Roman character
     * @return integer value
     * @throws SemanticException if the character is not a valid Roman digit
     */
    private int digit(final char ch) throws SemanticException {
        switch (ch) {
            case 'I': return 1;
            case 'V': return 5;
            case 'X': return 10;
            case 'L': return 50;
            case 'C': return 100;
            case 'D': return 500;
            case 'M': return 1000;
            default:
                throw new SemanticException(
                    this.node.line(),
                    this.node.column(),
                    "Invalid roman digit: " + ch
                );
        }
    }
}
