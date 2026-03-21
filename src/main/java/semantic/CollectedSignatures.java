package semantic;

import syntax.SyntaxNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Pre-validated function signature registry backed by a map.
 *
 * Provides fast arity lookups for function names collected during pass one.
 * The map is assumed to be already validated for duplicates.
 *
 * Example usage:
 * <pre>
 * Map&lt;String, Integer&gt; map = new HashMap&lt;&gt;();
 * map.put("fib", 1);
 * Signatures sigs = new CollectedSignatures(map);
 * int arity = sigs.arity("fib", node);
 * </pre>
 */
public final class CollectedSignatures implements Signatures {

    private final Map<String, Integer> registry;

    /**
     * Primary constructor.
     *
     * @param registry map of function name to parameter count
     */
    public CollectedSignatures(final Map<String, Integer> registry) {
        this.registry = new HashMap<>(registry);
    }

    @Override
    public int arity(
        final String name,
        final SyntaxNode node
    ) throws SemanticException {
        final Integer count = this.registry.get(name);
        if (count == null) {
            throw new SemanticException(
                node.line(),
                node.column(),
                "Undefined function: " + name
            );
        }
        return count;
    }

    @Override
    public boolean contains(final String name) {
        return this.registry.containsKey(name);
    }
}
