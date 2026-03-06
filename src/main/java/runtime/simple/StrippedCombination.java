package runtime.simple;

import runtime.Combination;

/**
 * Combines runtime and program IR by stripping redundant declarations.
 *
 * Removes rome77_input and rome77_output declare lines from the program
 * output, then prepends the runtime which defines these functions.
 *
 * Example usage:
 * <pre>
 * String ir = new StrippedCombination(
 *     runtimeContent, programOutput
 * ).combined();
 * </pre>
 */
public final class StrippedCombination implements Combination {

    private final String runtime;
    private final String program;

    /**
     * Primary constructor.
     *
     * @param runtime LLVM IR runtime with rome77 function definitions
     * @param program LLVM IR program output with rome77 declarations
     */
    public StrippedCombination(
        final String runtime,
        final String program
    ) {
        this.runtime = runtime;
        this.program = program;
    }

    @Override
    public String combined() {
        final StringBuilder stripped = new StringBuilder();
        for (final String line : this.program.split("\n")) {
            if (!line.startsWith("declare")
                || (!line.contains("rome77_input")
                && !line.contains("rome77_output"))) {
                stripped.append(line).append('\n');
            }
        }
        return this.runtime + "\n" + stripped;
    }
}
