package emission.simple;

import emission.Emitted;
import ir.Expression;
import ir.simple.IrCall;
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
 * Tests for IrCall LLVM IR emission behavior.
 *
 * Verifies that function call expressions emit argument
 * instructions followed by a call instruction.
 */
final class CallEmissionTest {

    @Test
    void emittedProducesCallWithNoArgs() throws Exception {
        assertThat(
            "Call with no args did not produce correct instruction",
            new String(
                new IrCall(
                    "foo", Collections.<Expression>emptyList()
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = call i64 @foo()"))
        );
    }

    @Test
    void emittedProducesCallWithOneArg() throws Exception {
        assertThat(
            "Call with one arg did not produce correct instruction",
            new String(
                new IrCall(
                    "double",
                    Arrays.<Expression>asList(new IrLiteral(7))
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = call i64 @double(i64 7)"))
        );
    }

    @Test
    void emittedProducesCallWithMultipleArgs() throws Exception {
        assertThat(
            "Call with multiple args did not produce correct instruction",
            new String(
                new IrCall(
                    "add",
                    Arrays.<Expression>asList(
                        new IrLiteral(3), new IrLiteral(4)
                    )
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo("%t0 = call i64 @add(i64 3, i64 4)"))
        );
    }

    @Test
    void emittedEmitsArgLoadsBeforeCall() throws Exception {
        assertThat(
            "Call did not emit arg loads before call instruction",
            new String(
                new IrCall(
                    "sum",
                    Arrays.<Expression>asList(
                        new IrVariable("a"), new IrVariable("b")
                    )
                ).emitted(
                    new SimpleEmission(List.of(), 0)
                ).emission().result().readAllBytes(),
                StandardCharsets.UTF_8
            ),
            is(equalTo(
                "%t0 = load i64, ptr %a\n"
                    + "%t1 = load i64, ptr %b\n"
                    + "%t2 = call i64 @sum(i64 %t0, i64 %t1)"
            ))
        );
    }

    @Test
    void emittedReturnsResultRegister() {
        assertThat(
            "Call did not return correct result register",
            new IrCall(
                "f", Collections.<Expression>emptyList()
            ).emitted(
                new SimpleEmission(List.of(), 2)
            ).register(),
            is(equalTo("%t2"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final IrCall call = new IrCall(
            "g", Arrays.<Expression>asList(new IrLiteral(1))
        );
        final SimpleEmission emission = new SimpleEmission(List.of(), 0);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Emitted result = call.emitted(emission);
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
            "Concurrent call emissions corrupted state",
            failed.get(),
            is(false)
        );
    }
}
