package syntax;

/**
 * Single node in the syntax tree for program representation.
 *
 * Each node represents a grammar rule match or terminal token.
 * Nodes are immutable and form a tree structure via children.
 *
 * Example usage:
 * <pre>
 * SyntaxNode stmt = tree.root().children().iterator().next();
 * String rule = stmt.name();    // "variableDecl"
 * int location = stmt.line();   // 1
 * </pre>
 */
public interface SyntaxNode {

    /**
     * Returns the grammar rule or token name for this node.
     *
     * For parser rules, returns the rule name (e.g., "variableDecl").
     * For terminal tokens, returns the token type (e.g., "ROMAN").
     *
     * @return Grammar rule or token name, never null or empty
     */
    String name();

    /**
     * Returns the source text matched by this node.
     *
     * For terminal tokens, returns the exact matched text.
     * For parser rules, returns concatenated text of all children.
     *
     * @return Matched source text, never null, may be empty
     */
    String text();

    /**
     * Returns the source line number where this node starts.
     *
     * Line numbers are 1-based, matching typical editor display.
     * The line number refers to the first token of this node.
     *
     * @return Line number, 1-based, always positive
     */
    int line();

    /**
     * Returns the column position where this node starts.
     *
     * Column positions are 0-based character offsets within the line.
     * The column refers to the first character of the first token.
     *
     * @return Column position, 0-based, non-negative
     */
    int column();

    /**
     * Returns the child nodes of this node.
     *
     * For terminal tokens, returns an empty iterable.
     * For parser rules, returns all matched child rules and tokens.
     * Children are ordered as they appear in the source.
     *
     * @return Iterable of child nodes, never null, may be empty
     */
    Iterable<SyntaxNode> children();
}
