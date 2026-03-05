package emission.simple;

import emission.Emitted;
import ir.Operator;
import ir.simple.IrLiteral;
import ir.simple.IrUnaryOp;
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
 * Tests for IrUnaryOp LLVM IR emission behavior.
 *
 * Verifies that unary SUB emits a negation instruction
 * and unary ADD passes through with no extra instruction.
 */
final class UnaryOpEmissionTest {

    @Test
    void emittedSubProducesNegation() throws Exception {
        assertThat(
            "UnaryOp SUB did not produce negation instruction",
            new String(
                new IrUnaryOp(Operator.SUB, new IrLiteral(8)).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = sub i64 0, 8"))
        );
    }

    @Test
    void emittedAddProducesNoExtraInstruction() throws Exception {
        assertThat(
            "UnaryOp ADD produced extra instructions",
            new String(
                new IrUnaryOp(Operator.ADD, new IrLiteral(3)).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(""))
        );
    }

    @Test
    void emittedAddReturnsSameRegisterAsOperand() {
        assertThat(
            "UnaryOp ADD did not return same register as operand",
            new IrUnaryOp(Operator.ADD, new IrLiteral(5)).emitted(
                new SimpleEmission(List.of(), 0)
            ).register(),
            is(equalTo("5"))
        );
    }

    @Test
    void emittedSubReturnsResultRegister() {
        assertThat(
            "UnaryOp SUB did not return correct result register",
            new IrUnaryOp(Operator.SUB, new IrLiteral(9)).emitted(
                new SimpleEmission(List.of(), 3)
            ).register(),
            is(equalTo("%t3"))
        );
    }

    @Test
    void emittedSubEmitsLoadBeforeNegation() throws Exception {
        assertThat(
            "UnaryOp SUB did not emit load before negation",
            new String(
                new IrUnaryOp(Operator.SUB, new IrVariable("x")).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%t0 = load i64, ptr %x\n"
                    + "%t1 = sub i64 0, %t0"
            ))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrUnaryOp op = new IrUnaryOp(Operator.SUB, new IrLiteral(4));
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
            "Concurrent unary op emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
