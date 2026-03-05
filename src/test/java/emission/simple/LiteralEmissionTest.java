package emission.simple;

import emission.Emitted;
import ir.simple.IrLiteral;
import org.junit.jupiter.api.Test;

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
 * Tests for IrLiteral LLVM IR emission behavior.
 *
 * Verifies that literal expressions emit no instructions
 * and return the integer value as an inline constant.
 */
final class LiteralEmissionTest {

    @Test
    void emittedReturnsValueAsRegister() {
        assertThat(
            "Literal did not return value as register",
            new IrLiteral(42).emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("42"))
        );
    }

    @Test
    void emittedProducesNoInstructions() throws Exception {
        assertThat(
            "Literal produced instructions when it should not",
            new String(
                new IrLiteral(7).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(""))
        );
    }

    @Test
    void emittedPreservesCounter() {
        assertThat(
            "Literal changed emission counter",
            new IrLiteral(99).emitted(
                new SimpleEmission(List.of(), 5)
            ).emission().registered(),
            is(equalTo("%t5"))
        );
    }

    @Test
    void emittedHandlesZeroValue() {
        assertThat(
            "Literal did not handle zero value",
            new IrLiteral(0).emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("0"))
        );
    }

    @Test
    void emittedHandlesNegativeValue() {
        assertThat(
            "Literal did not handle negative value",
            new IrLiteral(-1).emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("-1"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrLiteral literal = new IrLiteral(13);
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Emitted result = literal.emitted(emission);
                    if (!"13".equals(result.register())) {
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
            "Concurrent literal emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
