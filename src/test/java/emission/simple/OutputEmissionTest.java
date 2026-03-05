package emission.simple;

import ir.simple.IrLiteral;
import ir.simple.IrOutput;
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
 * Tests for IrOutput LLVM IR emission behavior.
 *
 * Verifies that output statements emit expression instructions
 * followed by a call to rome77_output.
 */
final class OutputEmissionTest {

    @Test
    void emittedProducesOutputCall() throws Exception {
        assertThat(
            "Output did not produce rome77_output call",
            new String(
                new IrOutput(new IrLiteral(42)).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("call void @rome77_output(i64 42)"))
        );
    }

    @Test
    void emittedEmitsExpressionBeforeOutputCall() throws Exception {
        assertThat(
            "Output did not emit expression before output call",
            new String(
                new IrOutput(new IrVariable("x")).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%t0 = load i64, ptr %x\n"
                    + "call void @rome77_output(i64 %t0)"
            ))
        );
    }

    @Test
    void emittedIncrementsCounter() {
        assertThat(
            "Output did not increment emission counter",
            new IrOutput(new IrLiteral(1)).emitted(
                new SimpleEmission(List.of(), 0)
            ).registered(),
            is(equalTo("%t1"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrOutput output = new IrOutput(new IrLiteral(7));
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final String reg = output
                        .emitted(emission).registered();
                    if (!"%t1".equals(reg)) {
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
            "Concurrent output emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
