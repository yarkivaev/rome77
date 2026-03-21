package semantic;

/**
 * Source of function signatures from a syntax tree.
 *
 * Encapsulates pass one of semantic analysis: walking the tree
 * to collect all function declarations and their arities.
 *
 * Example usage:
 * <pre>
 * Signatures sigs = new SignatureCollection(root).signatures();
 * </pre>
 */
public interface SignatureSource {

    /**
     * Returns collected function signatures.
     *
     * Walks the syntax tree, validates no duplicate definitions,
     * and returns a registry of function names to parameter counts.
     *
     * @return function signatures
     * @throws SemanticException if duplicate function definitions exist
     */
    Signatures signatures() throws SemanticException;
}
