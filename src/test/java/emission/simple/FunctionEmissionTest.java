package emission.simple;

import ir.simple.IrFunction;
import ir.simple.IrLiteral;
import ir.simple.IrVariable;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
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
 * Tests for IrFunction LLVM IR emission behavior.
 *
 * Verifies that function definitions emit define, parameter allocas,
 * body instructions, and ret correctly.
 */
final class FunctionEmissionTest {

    @Test
    void emittedProducesZeroParamFunction() throws Exception {
        assertThat(
            "Function with no params did not produce correct LLVM IR",
            new String(
                new IrFunction(
                    "constant", Collections.emptyList(), new IrLiteral(42)
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "define i64 @constant() {\n"
                    + "ret i64 42\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedProducesOneParamFunction() throws Exception {
        assertThat(
            "Function with one param did not produce correct LLVM IR",
            new String(
                new IrFunction(
                    "identity",
                    Arrays.asList("n"),
                    new IrVariable("n")
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "define i64 @identity(i64 %n_arg) {\n"
                    + "%n = alloca i64\n"
                    + "store i64 %n_arg, ptr %n\n"
                    + "%t3 = load i64, ptr %n\n"
                    + "ret i64 %t3\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedProducesTwoParamFunction() throws Exception {
        assertThat(
            "Function with two params did not produce correct LLVM IR",
            new String(
                new IrFunction(
                    "first",
                    Arrays.asList("a", "b"),
                    new IrVariable("a")
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "define i64 @first(i64 %a_arg, i64 %b_arg) {\n"
                    + "%a = alloca i64\n"
                    + "store i64 %a_arg, ptr %a\n"
                    + "%b = alloca i64\n"
                    + "store i64 %b_arg, ptr %b\n"
                    + "%t5 = load i64, ptr %a\n"
                    + "ret i64 %t5\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrFunction fn = new IrFunction(
            "f", Collections.emptyList(), new IrLiteral(1)
        );
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final String reg = fn.emitted(emission).registered();
                    if (!"%t3".equals(reg)) {
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
            "Concurrent function emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
