package lexical;

/**
 * Lexical token category.
 *
 * Represents the type of a lexical token.
 * Categories are based on Rome77 grammar token types.
 *
 * Example usage:
 * <pre>
 * if (token.category() == TokenCategory.ROMAN) {
 *     int value = parseRomanNumeral(token.text());
 * }
 * </pre>
 */
public enum TokenCategory {
    AS,
    MUNUS,
    GRAFO,
    ANAGNOSI,
    SINON,
    PLUS,
    MINUS,
    MULT,
    DIV,
    EQUALS,
    LPAREN,
    RPAREN,
    ROMAN,
    IDENTIFIER,
    EOF
}
