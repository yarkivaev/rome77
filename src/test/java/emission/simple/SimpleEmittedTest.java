package emission.simple;

import emission.Emission;
import emission.Emitted;
import org.junit.jupiter.api.Test;

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
 * Tests for SimpleEmitted expression emission result behavior.
 *
 * Verifies that SimpleEmitted correctly pairs an emission
 * with a register name.
 */
final class SimpleEmittedTest {

    @Test
    void emissionReturnsProvidedEmission() {
        final Emission em = new SimpleEmission(
            List.of("%t0 = add i64 1, 2"), 1
        );
        final Emitted emitted = new SimpleEmitted(em, "%t0");
        assertThat(
            "Emitted did not return provided emission",
            emitted.emission().registered(),
            is(equalTo("%t1"))
        );
    }

    @Test
    void registerReturnsProvidedRegister() {
        assertThat(
            "Emitted did not return provided register",
            new SimpleEmitted(
                new SimpleEmission(List.of(), 0), "%t5"
            ).register(),
            is(equalTo("%t5"))
        );
    }

    @Test
    void registerReturnsInlineConstant() {
        assertThat(
            "Emitted did not return inline constant register",
            new SimpleEmitted(
                new SimpleEmission(List.of(), 0), "42"
            ).register(),
            is(equalTo("42"))
        );
    }

    @Test
    void registerHandlesNonAsciiValue() {
        assertThat(
            "Emitted did not handle non-ASCII register name",
            new SimpleEmitted(
                new SimpleEmission(List.of(), 0), "%αβγ"
            ).register(),
            is(equalTo("%αβγ"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final Emission em = new SimpleEmission(List.of(), 3);
        final Emitted emitted = new SimpleEmitted(em, "%t2");
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    if (!emitted.register().equals("%t2")) {
                        failed.set(true);
                    }
                    if (!emitted.emission().registered().equals("%t3")) {
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
            "Concurrent reads corrupted emitted state",
            failed.get(),
            is(false)
        );
    }
}
