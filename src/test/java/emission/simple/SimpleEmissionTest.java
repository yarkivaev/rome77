package emission.simple;

import emission.Emission;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for SimpleEmission LLVM IR instruction sequence behavior.
 *
 * Verifies that SimpleEmission correctly tracks registers,
 * appends instructions immutably, and produces LLVM IR output.
 */
final class SimpleEmissionTest {

    @Test
    void registeredReturnsFirstRegisterForFreshEmission() {
        assertThat(
            "Fresh emission did not return %t0 as first register",
            new SimpleEmission(List.of(), 0).registered(),
            is(equalTo("%t0"))
        );
    }

    @Test
    void registeredReturnsRegisterBasedOnCounter() {
        assertThat(
            "Emission did not return register based on provided counter",
            new SimpleEmission(List.of(), 7).registered(),
            is(equalTo("%t7"))
        );
    }

    @Test
    void appendedIncrementsRegisterCounter() {
        assertThat(
            "Appended emission did not increment register counter",
            new SimpleEmission(List.of(), 3)
                .appended("%t3 = add i64 1, 2")
                .registered(),
            is(equalTo("%t4"))
        );
    }

    @Test
    void appendedPreservesOriginalEmission() {
        final Emission original = new SimpleEmission(List.of(), 0);
        original.appended("%t0 = add i64 1, 2");
        assertThat(
            "Original emission was mutated after append",
            original.registered(),
            is(equalTo("%t0"))
        );
    }

    @Test
    void resultContainsAppendedInstruction() throws Exception {
        final InputStream stream = new SimpleEmission(List.of(), 0)
            .appended("%t0 = add i64 1, 2")
            .result();
        assertThat(
            "Result did not contain appended instruction",
            new String(stream.readAllBytes(), StandardCharsets.UTF_8),
            is(equalTo("%t0 = add i64 1, 2"))
        );
    }

    @Test
    void resultJoinsMultipleInstructionsWithNewlines() throws Exception {
        final InputStream stream = new SimpleEmission(List.of(), 0)
            .appended("%t0 = add i64 1, 2")
            .appended("%t1 = sub i64 %t0, 3")
            .result();
        assertThat(
            "Result did not join instructions with newlines",
            new String(stream.readAllBytes(), StandardCharsets.UTF_8),
            is(equalTo("%t0 = add i64 1, 2\n%t1 = sub i64 %t0, 3"))
        );
    }

    @Test
    void resultReturnsEmptyStreamForNoInstructions() throws Exception {
        final InputStream stream = new SimpleEmission(List.of(), 0).result();
        assertThat(
            "Empty emission did not return empty stream",
            new String(stream.readAllBytes(), StandardCharsets.UTF_8),
            is(equalTo(""))
        );
    }

    @Test
    void appendedHandlesNonAsciiInstruction() throws Exception {
        final InputStream stream = new SimpleEmission(List.of(), 0)
            .appended("; комментарий")
            .result();
        assertThat(
            "Emission did not handle non-ASCII instruction",
            new String(stream.readAllBytes(), StandardCharsets.UTF_8),
            is(equalTo("; комментарий"))
        );
    }

    @Test
    void appendedIsThreadSafe() throws Exception {
        final Emission base = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            final int num = idx;
            pool.submit(() -> {
                try {
                    final Emission appended = base
                        .appended("%t" + num + " = add i64 1, 2");
                    if (!appended.registered().equals("%t1")) {
                        failed.set(true);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(5, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        assertThat(
            "Concurrent appends corrupted emission state",
            failed.get(),
            is(false)
        );
    }
}
