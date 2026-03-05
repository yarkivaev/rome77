package ir;

import emission.Emission;

/**
 * Rome77 program in intermediate representation.
 *
 * Represents complete program with function definitions and main statements.
 * All semantic validation is complete when Program is created.
 *
 * Example usage:
 * <pre>
 * Program program = analyzer.analyzed();
 * Emission em = program.emitted(new SimpleEmission());
 * </pre>
 */
public interface Program {

    /**
     * Returns all function definitions.
     *
     * Functions are ordered as declared in source.
     * Empty if program has no function definitions.
     *
     * @return Function definitions, never null, may be empty
     */
    Iterable<Function> functions();

    /**
     * Returns main body statements.
     *
     * Main body consists of variable declarations and output statements.
     * Statements are ordered as they appear in source.
     * Empty if program has no main statements.
     *
     * @return Main statements, never null, may be empty
     */
    Iterable<Statement> statements();

    /**
     * Emits LLVM IR for the entire program.
     *
     * @param emission Current instruction sequence
     * @return Updated emission with full program LLVM IR appended
     */
    Emission emitted(Emission emission);
}
