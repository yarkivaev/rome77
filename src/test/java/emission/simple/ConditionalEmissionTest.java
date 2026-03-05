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
 * basic blocks, and phi instructions correctly.
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
                    + "br i1 %t0, label %then_1, label %else_1\n"
                    + "then_1:\n"
                    + "br label %merge_1\n"
                    + "else_1:\n"
                    + "br label %merge_1\n"
                    + "merge_1:\n"
                    + "%t7 = phi i64 [ 10, %then_1 ], [ 20, %else_1 ]"
            ))
        );
    }

    @Test
    void emittedReturnsPhiRegister() {
        assertThat(
            "Conditional did not return phi register",
            new IrConditional(
                new IrLiteral(1),
                new IrLiteral(2),
                new IrLiteral(3)
            ).emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("%t7"))
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
                    + "br i1 %t1, label %then_2, label %else_2\n"
                    + "then_2:\n"
                    + "br label %merge_2\n"
                    + "else_2:\n"
                    + "br label %merge_2\n"
                    + "merge_2:\n"
                    + "%t8 = phi i64 [ 1, %then_2 ], [ 0, %else_2 ]"
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
            is(equalTo("%t17"))
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
                    if (!"%t7".equals(result.register())) {
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
