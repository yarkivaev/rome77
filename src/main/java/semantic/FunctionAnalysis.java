package semantic;

import ir.Expression;
import ir.Function;
import ir.simple.IrFunction;
import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Analyzes a functionDef syntax node into an IR function.
 *
 * Extracts the function name, parameter list, and body expression.
 * Validates that the name does not conflict with variables and
 * that no duplicate parameter names exist.
 * Function bodies see only their parameters, not global variables.
 *
 * Example usage:
 * <pre>
 * Function fn = new FunctionAnalysis(node, sigs, scope).function();
 * </pre>
 */
public final class FunctionAnalysis implements NodeFunction {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node functionDef syntax node
     * @param sigs function signatures
     * @param scope global variable scope for conflict detection
     */
    public FunctionAnalysis(
        final SyntaxNode node,
        final Signatures sigs,
        final Scope scope
    ) {
        this.node = node;
        this.sigs = sigs;
        this.scope = scope;
    }

    @Override
    public Function function() throws SemanticException {
        String name = null;
        List<String> params = new ArrayList<>();
        SyntaxNode body = null;
        for (final SyntaxNode child : this.node.children()) {
            switch (child.name()) {
                case "MUNUS":
                case "EQUALS":
                    break;
                case "IDENTIFIER":
                    name = child.text();
                    if (this.scope.contains(name)) {
                        throw new SemanticException(
                            child.line(),
                            child.column(),
                            "Variable " + name + " is already defined"
                        );
                    }
                    break;
                case "params":
                    params = this.extracted(child);
                    break;
                default:
                    body = child;
            }
        }
        if (body == null) {
            throw new SemanticException(
                this.node.line(),
                this.node.column(),
                "Function body not defined"
            );
        }
        final Set<String> locals = new HashSet<>(params);
        final Expression expression = new DispatchedExpression(
            body, this.sigs, new AnalysisScope(locals)
        ).expression();
        return new IrFunction(name, params, expression);
    }

    /**
     * Extracts parameter names from a params node.
     *
     * @param node params syntax node
     * @return list of parameter names
     * @throws SemanticException if duplicate parameter names exist
     */
    private List<String> extracted(
        final SyntaxNode node
    ) throws SemanticException {
        final List<String> params = new ArrayList<>();
        for (final SyntaxNode child : node.children()) {
            if (child.name().equals("IDENTIFIER")) {
                final String name = child.text();
                if (params.contains(name)) {
                    throw new SemanticException(
                        child.line(),
                        child.column(),
                        "Duplicate parameter name: " + name
                    );
                }
                params.add(name);
            }
        }
        return params;
    }
}
