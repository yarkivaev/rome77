package emission.simple;

import emission.Emitted;
import ir.simple.IrVariable;
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
 * Tests for IrVariable LLVM IR emission behavior.
 *
 * Verifies that variable expressions emit a load instruction
 * and return the register name where the value is loaded.
 */
final class VariableEmissionTest {

    @Test
    void emittedReturnsRegisterName() {
        assertThat(
            "Variable did not return correct register name",
            new IrVariable("x").emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("%t0"))
        );
    }

    @Test
    void emittedProducesLoadInstruction() throws Exception {
        assertThat(
            "Variable did not produce load instruction",
            new String(
                new IrVariable("val").emitted(
                    new SimpleEmission(List.of(), 2)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t2 = load i64, ptr %val"))
        );
    }

    @Test
    void emittedIncrementsCounter() {
        assertThat(
            "Variable did not increment emission counter",
            new IrVariable("y").emitted(
                new SimpleEmission(List.of(), 3)
            ).emission().registered(),
            is(equalTo("%t4"))
        );
    }

    @Test
    void emittedHandlesNonAsciiName() throws Exception {
        assertThat(
            "Variable did not handle non-ASCII name",
            new String(
                new IrVariable("переменная").emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = load i64, ptr %переменная"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrVariable variable = new IrVariable("z");
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Emitted result = variable.emitted(emission);
                    if (!"%t0".equals(result.register())) {
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
            "Concurrent variable emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
