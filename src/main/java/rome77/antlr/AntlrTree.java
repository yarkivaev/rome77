package rome77.antlr;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import syntax.SyntaxNode;
import syntax.SyntaxTree;

/**
 * Immutable SyntaxTree wrapper for ANTLR ParseTree.
 *
 * This final class adapts ANTLR's mutable ParseTree to the
 * immutable SyntaxTree interface. All data is captured at construction.
 *
 * Example usage:
 * <pre>
 * SyntaxTree tree = new AntlrTree(parseTree, parser);
 * </pre>
 */
public final class AntlrTree implements SyntaxTree {

    /**
     * Root node of the tree.
     */
    private final SyntaxNode root;

    /**
     * Primary constructor.
     *
     * @param pt ANTLR parse tree
     * @param prs Parser instance for rule names
     */
    public AntlrTree(final ParseTree pt, final Parser prs) {
        this.root = new AntlrNode(pt, prs);
    }

    @Override
    public SyntaxNode root() {
        return this.root;
    }
}
