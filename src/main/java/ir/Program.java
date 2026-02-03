package ir;

/**
 * Rome77 program in intermediate representation.
 *
 * Represents complete program with function definitions and main statements.
 * All semantic validation is complete when Program is created.
 *
 * Example usage:
 * <pre>
 * Program program = analyzer.analyzed();
 * Iterable<Function> functions = program.functions();
 * Iterable<Statement> statements = program.statements();
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
}
