package web;

import emission.simple.SimpleEmission;
import ir.Program;
import parsing.ParsingException;
import rome77.antlr.Rome77Syntax;
import runtime.simple.LliExecution;
import runtime.simple.LliPath;
import runtime.simple.StrippedCombination;
import semantic.Rome77Analyzer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Full Rome77 compilation pipeline producing an HTTP response.
 *
 * Runs: parse, analyze, emit, combine with runtime, execute via lli.
 * Catches ParsingException for diagnostics and other exceptions for errors.
 * Gracefully handles missing lli by reporting the error.
 *
 * Example usage:
 * <pre>
 * Response r = new Rome77Compilation(
 *     "Grafo V", runtimeIr, candidates, 10
 * ).response();
 * </pre>
 */
public final class Rome77Compilation implements Compilation {

    private final String source;
    private final String input;
    private final String runtime;
    private final Endpoint endpoint;

    /**
     * Primary constructor.
     *
     * @param source Rome77 source code to compile
     * @param input stdin payload for execution
     * @param runtime LLVM IR runtime definitions
     * @param endpoint execution endpoint configuration
     */
    public Rome77Compilation(
        final String source,
        final String input,
        final String runtime,
        final Endpoint endpoint
    ) {
        this.source = source;
        this.input = input;
        this.runtime = runtime;
        this.endpoint = endpoint;
    }

    @Override
    public Response response() {
        final Program program;
        try {
            program = new Rome77Analyzer(
                new Rome77Syntax(this.source).parsed()
            ).analyzed();
        } catch (final ParsingException exception) {
            return new CompileResponse(
                "",
                "",
                List.of(new ParseDiagnostic(exception))
            );
        }
        final String emitted;
        try {
            emitted = new String(
                program.emitted(new SimpleEmission())
                    .result()
                    .readAllBytes(),
                StandardCharsets.UTF_8
            );
        } catch (final Exception exception) {
            return new CompileResponse(
                "",
                "Emission failed: " + exception.getMessage(),
                Collections.emptyList()
            );
        }
        final String ir = new StrippedCombination(
            this.runtime, emitted
        ).combined();
        final Path lli;
        try {
            lli = new LliPath(this.endpoint.candidates()).resolved();
        } catch (final IllegalStateException exception) {
            return new CompileResponse(
                "",
                "lli interpreter not found",
                Collections.emptyList()
            );
        }
        try {
            final String output = new LliExecution(
                ir, this.input, lli, this.endpoint.timeout()
            ).output();
            return new CompileResponse(
                output, "", Collections.emptyList()
            );
        } catch (final Exception exception) {
            return new CompileResponse(
                "",
                "Execution failed: " + exception.getMessage(),
                Collections.emptyList()
            );
        }
    }
}
