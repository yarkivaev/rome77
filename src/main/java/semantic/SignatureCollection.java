package semantic;

import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pass one of semantic analysis: collects function signatures from a syntax tree.
 *
 * Walks all top-level statements, identifies function definitions,
 * and records their names and parameter counts.
 * Validates that no two functions share the same name.
 *
 * Example usage:
 * <pre>
 * SyntaxNode root = tree.root();
 * Signatures sigs = new SignatureCollection(root).signatures();
 * </pre>
 */
public final class SignatureCollection implements SignatureSource {

    private final SyntaxNode root;

    /**
     * Primary constructor.
     *
     * @param root root node of the syntax tree
     */
    public SignatureCollection(final SyntaxNode root) {
        this.root = root;
    }

    @Override
    public Signatures signatures() throws SemanticException {
        final Map<String, Integer> registry = new HashMap<>();
        for (final SyntaxNode stmt : this.root.children()) {
            if (stmt.name().equals("statement")) {
                final List<SyntaxNode> children = new ArrayList<>();
                stmt.children().forEach(children::add);
                final SyntaxNode node = children.get(0);
                if (node.name().equals("functionDef")) {
                    final String name = this.identifier(node);
                    if (registry.containsKey(name)) {
                        throw new SemanticException(
                            node.line(),
                            node.column(),
                            "Function " + name + " is already defined"
                        );
                    }
                    registry.put(name, this.params(node));
                }
            }
        }
        return new CollectedSignatures(registry);
    }

    /**
     * Extracts the function name from a functionDef node.
     *
     * @param node functionDef syntax node
     * @return function identifier
     */
    private String identifier(final SyntaxNode node) {
        for (final SyntaxNode child : node.children()) {
            if (child.name().equals("IDENTIFIER")) {
                return child.text();
            }
        }
        throw new IllegalStateException(
            "Function definition without identifier at line " + node.line()
        );
    }

    /**
     * Counts the parameters in a functionDef node.
     *
     * @param node functionDef syntax node
     * @return parameter count
     */
    private int params(final SyntaxNode node) {
        for (final SyntaxNode child : node.children()) {
            if (child.name().equals("params")) {
                final List<SyntaxNode> items = new ArrayList<>();
                child.children().forEach(items::add);
                return items.size();
            }
        }
        return 0;
    }
}
