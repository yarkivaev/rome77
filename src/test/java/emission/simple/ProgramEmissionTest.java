package emission.simple;

import ir.Expression;
import ir.simple.IrCall;
import ir.simple.IrDeclaration;
import ir.simple.IrFunction;
import ir.simple.IrLiteral;
import ir.simple.IrOutput;
import ir.simple.IrProgram;
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
 * Tests for IrProgram LLVM IR emission behavior.
 *
 * Verifies that programs emit runtime declarations, function definitions,
 * and main function with statements correctly.
 */
final class ProgramEmissionTest {

    @Test
    void emittedProducesMinimalProgram() throws Exception {
        assertThat(
            "Minimal program did not produce correct LLVM IR",
            new String(
                new IrProgram(
                    Collections.emptyList(),
                    Collections.emptyList()
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "declare i64 @rome77_input()\n"
                    + "declare void @rome77_output(i64)\n"
                    + "define i32 @main() {\n"
                    + "ret i32 0\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedProducesProgramWithOutput() throws Exception {
        assertThat(
            "Program with output did not produce correct LLVM IR",
            new String(
                new IrProgram(
                    Collections.emptyList(),
                    Arrays.asList(new IrOutput(new IrLiteral(42)))
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "declare i64 @rome77_input()\n"
                    + "declare void @rome77_output(i64)\n"
                    + "define i32 @main() {\n"
                    + "call void @rome77_output(i64 42)\n"
                    + "ret i32 0\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedProducesProgramWithFunction() throws Exception {
        assertThat(
            "Program with function did not produce correct LLVM IR",
            new String(
                new IrProgram(
                    Arrays.asList(
                        new IrFunction(
                            "constant",
                            Collections.emptyList(),
                            new IrLiteral(7)
                        )
                    ),
                    Arrays.asList(
                        new IrOutput(
                            new IrCall(
                                "constant",
                                Collections.<Expression>emptyList()
                            )
                        )
                    )
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "declare i64 @rome77_input()\n"
                    + "declare void @rome77_output(i64)\n"
                    + "define i64 @constant() {\n"
                    + "ret i64 7\n"
                    + "}\n"
                    + "define i32 @main() {\n"
                    + "%t6 = call i64 @constant()\n"
                    + "call void @rome77_output(i64 %t6)\n"
                    + "ret i32 0\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedProducesProgramWithDeclarationAndOutput() throws Exception {
        assertThat(
            "Program with declaration and output did not produce correct LLVM IR",
            new String(
                new IrProgram(
                    Collections.emptyList(),
                    Arrays.asList(
                        new IrDeclaration("x", new IrLiteral(10)),
                        new IrOutput(new IrVariable("x"))
                    )
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "declare i64 @rome77_input()\n"
                    + "declare void @rome77_output(i64)\n"
                    + "define i32 @main() {\n"
                    + "%x = alloca i64\n"
                    + "store i64 10, ptr %x\n"
                    + "%t5 = load i64, ptr %x\n"
                    + "call void @rome77_output(i64 %t5)\n"
                    + "ret i32 0\n"
                    + "}"
            ))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrProgram program = new IrProgram(
            Collections.emptyList(),
            Arrays.asList(new IrOutput(new IrLiteral(1)))
        );
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final String reg = program
                        .emitted(emission).registered();
                    if (!"%t6".equals(reg)) {
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
            "Concurrent program emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
