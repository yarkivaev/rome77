package syntax;

/**
 * Immutable syntax tree for program representation.
 *
 * This interface provides access to the parsed program structure.
 * The tree is immutable once created and safe for concurrent access.
 * A SyntaxTree instance is only created for valid programs without errors.
 * If parsing fails, Either.Left with ParsingError is returned instead.
 *
 * Example usage:
 * <pre>
 * Either<ParsingError, SyntaxTree> result = syntax.parsed();
 * SyntaxTree tree = result.right().get();
 * SyntaxNode program = tree.root();
 * </pre>
 */
public interface SyntaxTree {

    /**
     * Returns the root node of the syntax tree.
     *
     * The root node represents the entire program and contains
     * all statements as its children. For empty programs, the root
     * node has zero children but is never null.
     *
     * @return Root node of the syntax tree, never null
     */
    SyntaxNode root();
}
