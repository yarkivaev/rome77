package emission.simple;

import emission.Emitted;
import ir.simple.IrInput;
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
 * Tests for IrInput LLVM IR emission behavior.
 *
 * Verifies that input expressions emit a call to rome77_input
 * and return the register where the result is stored.
 */
final class InputEmissionTest {

    @Test
    void emittedReturnsRegisterName() {
        assertThat(
            "Input did not return correct register name",
            new IrInput().emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("%t0"))
        );
    }

    @Test
    void emittedProducesCallInstruction() throws Exception {
        assertThat(
            "Input did not produce call instruction",
            new String(
                new IrInput().emitted(
                    new SimpleEmission(List.of(), 4)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t4 = call i64 @rome77_input()"))
        );
    }

    @Test
    void emittedIncrementsCounter() {
        assertThat(
            "Input did not increment emission counter",
            new IrInput().emitted(
                new SimpleEmission(List.of(), 1)
            ).emission().registered(),
            is(equalTo("%t2"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrInput input = new IrInput();
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Emitted result = input.emitted(emission);
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
            "Concurrent input emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
