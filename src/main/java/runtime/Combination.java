package runtime;

/**
 * Combines runtime definitions with program LLVM IR.
 *
 * Merges a runtime file (with function definitions) and a program
 * (with function declarations) into a single valid LLVM IR module.
 *
 * Example usage:
 * <pre>
 * String ir = new StrippedCombination(runtime, program).combined();
 * </pre>
 */
public interface Combination {

    /**
     * Produces combined LLVM IR from runtime and program.
     *
     * @return Complete LLVM IR module as a string
     */
    String combined();
}
