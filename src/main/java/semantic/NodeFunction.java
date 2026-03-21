package semantic;

import ir.Function;

/**
 * Produces an IR function from a function definition syntax node.
 *
 * Encapsulates the extraction of function name, parameters, and body
 * along with semantic validation of the definition.
 *
 * Example usage:
 * <pre>
 * Function fn = new FunctionAnalysis(node, sigs, scope).function();
 * </pre>
 */
public interface NodeFunction {

    /**
     * Returns the IR function for the encapsulated definition node.
     *
     * @return IR function
     * @throws SemanticException if semantic errors are found
     */
    Function function() throws SemanticException;
}
