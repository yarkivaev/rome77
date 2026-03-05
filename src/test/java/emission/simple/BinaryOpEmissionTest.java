package emission.simple;

import emission.Emitted;
import ir.Operator;
import ir.simple.IrBinaryOp;
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
 * Tests for IrBinaryOp LLVM IR emission behavior.
 *
 * Verifies that binary operations emit the correct LLVM IR
 * arithmetic instruction with left and right operand registers.
 */
final class BinaryOpEmissionTest {

    @Test
    void emittedProducesAddInstruction() throws Exception {
        assertThat(
            "BinaryOp ADD did not produce add instruction",
            new String(
                new IrBinaryOp(
                    Operator.ADD, new IrLiteral(3), new IrLiteral(5)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = add i64 3, 5"))
        );
    }

    @Test
    void emittedProducesSubInstruction() throws Exception {
        assertThat(
            "BinaryOp SUB did not produce sub instruction",
            new String(
                new IrBinaryOp(
                    Operator.SUB, new IrLiteral(10), new IrLiteral(4)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = sub i64 10, 4"))
        );
    }

    @Test
    void emittedProducesMulInstruction() throws Exception {
        assertThat(
            "BinaryOp MUL did not produce mul instruction",
            new String(
                new IrBinaryOp(
                    Operator.MUL, new IrLiteral(6), new IrLiteral(7)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = mul i64 6, 7"))
        );
    }

    @Test
    void emittedProducesDivInstruction() throws Exception {
        assertThat(
            "BinaryOp DIV did not produce sdiv instruction",
            new String(
                new IrBinaryOp(
                    Operator.DIV, new IrLiteral(20), new IrLiteral(4)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = sdiv i64 20, 4"))
        );
    }

    @Test
    void emittedReturnsResultRegister() {
        assertThat(
            "BinaryOp did not return correct result register",
            new IrBinaryOp(
                Operator.ADD, new IrLiteral(1), new IrLiteral(2)
            ).emitted(
                new SimpleEmission(List.of(), 5)
            ).register(),
            is(equalTo("%t5"))
        );
    }

    @Test
    void emittedEmitsOperandsBeforeOperation() throws Exception {
        assertThat(
            "BinaryOp did not emit operand loads before operation",
            new String(
                new IrBinaryOp(
                    Operator.ADD,
                    new IrVariable("a"),
                    new IrVariable("b")
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%t0 = load i64, ptr %a\n"
                    + "%t1 = load i64, ptr %b\n"
                    + "%t2 = add i64 %t0, %t1"
            ))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrBinaryOp op = new IrBinaryOp(
            Operator.ADD, new IrLiteral(1), new IrLiteral(2)
        );
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Emitted result = op.emitted(emission);
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
            "Concurrent binary op emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
