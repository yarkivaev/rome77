package emission.simple;

import emission.Emission;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable LLVM IR instruction sequence backed by a list and counter.
 *
 * Each instance is immutable; appending returns a new instance.
 * The counter tracks the next available temporary register number.
 *
 * Example usage:
 * <pre>
 * Emission em = new SimpleEmission();
 * String reg = em.registered();
 * em = em.appended(reg + " = add i64 1, 2");
 * </pre>
 */
public final class SimpleEmission implements Emission {

    private final List<String> instructions;
    private final int count;

    /**
     * Primary constructor.
     *
     * @param instructions List of LLVM IR instructions
     * @param count Current register counter
     */
    public SimpleEmission(
        final List<String> instructions,
        final int count
    ) {
        this.instructions = Collections.unmodifiableList(
            new ArrayList<>(instructions)
        );
        this.count = count;
    }

    /**
     * Secondary constructor creating empty emission.
     */
    public SimpleEmission() {
        this(Collections.emptyList(), 0);
    }

    @Override
    public String registered() {
        return "%t" + this.count;
    }

    @Override
    public Emission appended(final String instruction) {
        final List<String> updated = new ArrayList<>(this.instructions);
        updated.add(instruction);
        return new SimpleEmission(updated, this.count + 1);
    }

    @Override
    public InputStream result() {
        return new ByteArrayInputStream(
            String.join("\n", this.instructions)
                .getBytes(StandardCharsets.UTF_8)
        );
    }
}
