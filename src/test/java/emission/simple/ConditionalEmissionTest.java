package emission.simple;

import emission.Emitted;
import ir.simple.IrConditional;
import ir.simple.IrLiteral;
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
 * Tests for IrConditional LLVM IR emission behavior.
 *
 * Verifies that conditional expressions emit icmp, br,
 * basic blocks, and alloca/store/load instructions correctly.
 */
final class ConditionalEmissionTest {

    @Test
    void emittedProducesConditionalWithLiterals() throws Exception {
        assertThat(
            "Conditional did not produce correct LLVM IR",
            new String(
                new IrConditional(
                    new IrLiteral(1),
                    new IrLiteral(10),
                    new IrLiteral(20)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%t0 = icmp ne i64 1, 0\n"
                    + "%t1 = alloca i64\n"
                    + "br i1 %t0, label %then_2, label %else_2\n"
                    + "then_2:\n"
                    + "store i64 10, ptr %t1\n"
                    + "br label %merge_2\n"
                    + "else_2:\n"
                    + "store i64 20, ptr %t1\n"
                    + "br label %merge_2\n"
                    + "merge_2:\n"
                    + "%t10 = load i64, ptr %t1"
            ))
        );
    }

    @Test
    void emittedReturnsLoadRegister() {
        assertThat(
            "Conditional did not return load register",
            new IrConditional(
                new IrLiteral(1),
                new IrLiteral(2),
                new IrLiteral(3)
            ).emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("%t10"))
        );
    }

    @Test
    void emittedEmitsVariableLoadInCondition() throws Exception {
        assertThat(
            "Conditional did not emit variable load in condition",
            new String(
                new IrConditional(
                    new IrVariable("flag"),
                    new IrLiteral(1),
                    new IrLiteral(0)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%t0 = load i64, ptr %flag\n"
                    + "%t1 = icmp ne i64 %t0, 0\n"
                    + "%t2 = alloca i64\n"
                    + "br i1 %t1, label %then_3, label %else_3\n"
                    + "then_3:\n"
                    + "store i64 1, ptr %t2\n"
                    + "br label %merge_3\n"
                    + "else_3:\n"
                    + "store i64 0, ptr %t2\n"
                    + "br label %merge_3\n"
                    + "merge_3:\n"
                    + "%t11 = load i64, ptr %t2"
            ))
        );
    }

    @Test
    void emittedUsesCorrectCounterOffset() {
        assertThat(
            "Conditional did not use correct counter offset",
            new IrConditional(
                new IrLiteral(1),
                new IrLiteral(2),
                new IrLiteral(3)
            ).emitted(
                new SimpleEmission(List.of(), 10)
            ).register(),
            is(equalTo("%t20"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrConditional cond = new IrConditional(
            new IrLiteral(1), new IrLiteral(2), new IrLiteral(3)
        );
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Emitted result = cond.emitted(emission);
                    if (!"%t10".equals(result.register())) {
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
            "Concurrent conditional emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
