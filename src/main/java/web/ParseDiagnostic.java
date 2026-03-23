package web;

import parsing.ParsingException;

/**
 * Diagnostic derived from a ParsingException.
 *
 * Adapts ParsingException line, column, and message to the Diagnostic
 * interface. Converts the 0-based column to 1-based for the IDE.
 *
 * Example usage:
 * <pre>
 * Diagnostic d = new ParseDiagnostic(parsingException);
 * int line = d.line();
 * </pre>
 */
public final class ParseDiagnostic implements Diagnostic {

    private final ParsingException exception;

    /**
     * Primary constructor.
     *
     * @param exception parsing or semantic exception with location info
     */
    public ParseDiagnostic(final ParsingException exception) {
        this.exception = exception;
    }

    @Override
    public String severity() {
        return "error";
    }

    @Override
    public int line() {
        return this.exception.line();
    }

    @Override
    public int column() {
        return this.exception.column() + 1;
    }

    @Override
    public String message() {
        return this.exception.getMessage();
    }
}
