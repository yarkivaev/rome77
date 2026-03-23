package web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Compile request parsed from JSON using Gson.
 *
 * Reads an InputStream containing JSON with a "source" field
 * and extracts the Rome77 source code.
 *
 * Example usage:
 * <pre>
 * Request req = new GsonRequest(httpExchange.getRequestBody());
 * String code = req.source();
 * </pre>
 */
public final class GsonRequest implements Request {

    private final String code;
    private final String input;

    /**
     * Primary constructor.
     *
     * @param code Rome77 source code
     * @param input stdin payload
     */
    public GsonRequest(final String code, final String input) {
        this.code = code;
        this.input = input;
    }

    /**
     * Secondary constructor parsing JSON from InputStream.
     *
     * @param stream JSON input containing "source" field
     */
    public GsonRequest(final InputStream stream) {
        final JsonObject payload = JsonParser.parseReader(
            new InputStreamReader(stream, StandardCharsets.UTF_8)
        ).getAsJsonObject();
        this.code = payload.get("source").getAsString();
        this.input = payload.has("input")
            ? payload.get("input").getAsString()
            : "";
    }

    @Override
    public String source() {
        return this.code;
    }

    @Override
    public String input() {
        return this.input;
    }
}
