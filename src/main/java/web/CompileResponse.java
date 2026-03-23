package web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;

/**
 * JSON compilation response with output, error, and diagnostics.
 *
 * Builds JSON matching the IDE frontend contract:
 * {"output":"...","error":"...","diagnostics":[...]}
 *
 * Example usage:
 * <pre>
 * Response resp = new CompileResponse(
 *     "42\n", "", List.of()
 * );
 * String json = resp.json();
 * </pre>
 */
public final class CompileResponse implements Response {

    private final String output;
    private final String error;
    private final List<Diagnostic> diagnostics;

    /**
     * Primary constructor.
     *
     * @param output program stdout, empty string if none
     * @param error critical error message, empty string if none
     * @param diagnostics list of compilation diagnostics
     */
    public CompileResponse(
        final String output,
        final String error,
        final List<Diagnostic> diagnostics
    ) {
        this.output = output;
        this.error = error;
        this.diagnostics = List.copyOf(diagnostics);
    }

    @Override
    public String json() {
        final JsonObject obj = new JsonObject();
        obj.addProperty("output", this.output);
        obj.addProperty("error", this.error);
        final JsonArray arr = new JsonArray();
        for (final Diagnostic diag : this.diagnostics) {
            final JsonObject entry = new JsonObject();
            entry.addProperty("severity", diag.severity());
            entry.addProperty("startLine", diag.line());
            entry.addProperty("startColumn", diag.column());
            entry.addProperty("endLine", diag.line());
            entry.addProperty("endColumn", diag.column());
            entry.addProperty("message", diag.message());
            arr.add(entry);
        }
        obj.add("diagnostics", arr);
        return obj.toString();
    }
}
