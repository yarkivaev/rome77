package semantic;

import syntax.SyntaxNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Immutable variable scope backed by a set of names.
 *
 * Each call to {@link #with(String, SyntaxNode)} returns a new instance
 * with the additional name, leaving the original unchanged.
 *
 * Example usage:
 * <pre>
 * Scope scope = new AnalysisScope();
 * Scope extended = scope.with("x", node);
 * </pre>
 */
public final class AnalysisScope implements Scope {

    private final Set<String> names;

    /**
     * Primary constructor.
     *
     * @param names variable names in scope
     */
    public AnalysisScope(final Set<String> names) {
        this.names = new HashSet<>(names);
    }

    /**
     * Secondary constructor for an empty scope.
     */
    public AnalysisScope() {
        this(new HashSet<>());
    }

    @Override
    public Scope with(
        final String name,
        final SyntaxNode node
    ) throws SemanticException {
        if (this.names.contains(name)) {
            throw new SemanticException(
                node.line(),
                node.column(),
                "Variable " + name + " is already defined"
            );
        }
        final Set<String> extended = new HashSet<>(this.names);
        extended.add(name);
        return new AnalysisScope(extended);
    }

    @Override
    public boolean contains(final String name) {
        return this.names.contains(name);
    }
}
