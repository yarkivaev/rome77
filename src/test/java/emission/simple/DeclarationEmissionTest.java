package emission.simple;

import ir.simple.IrDeclaration;
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
 * Tests for IrDeclaration LLVM IR emission behavior.
 *
 * Verifies that declarations emit alloca, expression,
 * and store instructions correctly.
 */
final class DeclarationEmissionTest {

    @Test
    void emittedProducesAllocaAndStore() throws Exception {
        assertThat(
            "Declaration did not produce alloca and store",
            new String(
                new IrDeclaration("x", new IrLiteral(42)).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%x = alloca i64\n"
                    + "store i64 42, ptr %x"
            ))
        );
    }

    @Test
    void emittedEmitsExpressionBetweenAllocaAndStore() throws Exception {
        assertThat(
            "Declaration did not emit expression between alloca and store",
            new String(
                new IrDeclaration("y", new IrVariable("z")).emitted(
                    new SimpleEmission(List.of(), 0)
                ).result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%y = alloca i64\n"
                    + "%t1 = load i64, ptr %z\n"
                    + "store i64 %t1, ptr %y"
            ))
        );
    }

    @Test
    void emittedIncrementsCounter() {
        assertThat(
            "Declaration did not increment emission counter",
            new IrDeclaration("v", new IrLiteral(1)).emitted(
                new SimpleEmission(List.of(), 0)
            ).registered(),
            is(equalTo("%t2"))
        );
    }

    @Test
    void emittedHandlesNonAsciiName() throws Exception {
        assertThat(
            "Declaration did not handle non-ASCII name",
            new String(
                new IrDeclaration("переменная", new IrLiteral(5))
                    .emitted(new SimpleEmission(List.of(), 0))
                    .result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%переменная = alloca i64\n"
                    + "store i64 5, ptr %переменная"
            ))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrDeclaration decl = new IrDeclaration("a", new IrLiteral(1));
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final String reg = decl.emitted(emission).registered();
                    if (!"%t2".equals(reg)) {
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
            "Concurrent declaration emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
