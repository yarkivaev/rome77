package runtime;

import emission.simple.SimpleEmission;
import ir.Program;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import rome77.antlr.Rome77Syntax;
import runtime.simple.LliExecution;
import runtime.simple.LliPath;
import runtime.simple.StrippedCombination;
import semantic.Rome77Analyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
 * End-to-end tests that compile Rome77 source code and execute
 * via lli to verify correct runtime output.
 *
 * Tests are skipped if lli is not available on the system.
 *
 * Example usage:
 * <pre>
 * mvn test -pl . -Dtest=runtime.ProgramExecutionTest
 * </pre>
 */
final class ProgramExecutionTest {

    @Test
    void compiledLiteralOutputsCorrectValue() throws Exception {
        assertThat(
            "Literal XLII did not produce expected output",
            this.executed("Grafo XLII"),
            is(equalTo("42\n"))
        );
    }

    @Test
    void compiledNegationOutputsCorrectValue() throws Exception {
        assertThat(
            "Negation of VII did not produce expected output",
            this.executed("Grafo -VII"),
            is(equalTo("-7\n"))
        );
    }

    @Test
    void compiledAdditionOutputsCorrectValue() throws Exception {
        assertThat(
            "Addition of III and V did not produce expected output",
            this.executed("Grafo III + V"),
            is(equalTo("8\n"))
        );
    }

    @Test
    void compiledSubtractionOutputsCorrectValue() throws Exception {
        assertThat(
            "Subtraction of III from X did not produce expected output",
            this.executed("Grafo X - III"),
            is(equalTo("7\n"))
        );
    }

    @Test
    void compiledMultiplicationOutputsCorrectValue() throws Exception {
        assertThat(
            "Multiplication of VI and VII did not produce expected output",
            this.executed("Grafo VI * VII"),
            is(equalTo("42\n"))
        );
    }

    @Test
    void compiledDivisionOutputsCorrectValue() throws Exception {
        assertThat(
            "Division of XV by III did not produce expected output",
            this.executed("Grafo XV / III"),
            is(equalTo("5\n"))
        );
    }

    @Test
    void compiledVariableOutputsCorrectValue() throws Exception {
        assertThat(
            "Variable with value XCIX did not produce expected output",
            this.executed("As x = XCIX\nGrafo x"),
            is(equalTo("99\n"))
        );
    }

    @Test
    void compiledFunctionCallOutputsCorrectValue() throws Exception {
        assertThat(
            "Function returning LXXVII did not produce expected output",
            this.executed(
                "Munus constant n = LXXVII\nGrafo constant I"
            ),
            is(equalTo("77\n"))
        );
    }

    @Test
    void compiledParameterizedFunctionOutputsCorrectValue() throws Exception {
        assertThat(
            "Function doubling XXI did not produce expected output",
            this.executed(
                "Munus doubled n = n + n\nGrafo doubled XXI"
            ),
            is(equalTo("42\n"))
        );
    }

    @Test
    void compiledConditionalTrueOutputsCorrectValue() throws Exception {
        assertThat(
            "Conditional with true condition did not produce expected output",
            this.executed("Grafo Sinon I X XX"),
            is(equalTo("10\n"))
        );
    }

    @Test
    void compiledConditionalFalseOutputsCorrectValue() throws Exception {
        assertThat(
            "Conditional with false condition did not produce expected output",
            this.executed("Grafo Sinon N X XX"),
            is(equalTo("20\n"))
        );
    }

    @Test
    void compiledNestedArithmeticOutputsCorrectValue() throws Exception {
        assertThat(
            "Nested arithmetic (II+III)*IV did not produce expected output",
            this.executed("Grafo (II + III) * IV"),
            is(equalTo("20\n"))
        );
    }

    @Test
    void compiledMultipleOutputsProduceCorrectValues() throws Exception {
        assertThat(
            "Multiple outputs did not produce expected values",
            this.executed("Grafo XLII\nGrafo XCIX"),
            is(equalTo("42\n99\n"))
        );
    }

    @Test
    void compiledBinarySearchSqrtOutputsCorrectValue() throws Exception {
        assertThat(
            "Integer square root of C did not produce expected output",
            this.executed("""
                Munus mid lo hi = (lo + hi + I) / II
                Munus search lo hi n = Sinon (hi - lo) \
                    (Sinon ((mid lo hi) * (mid lo hi) / (n + I)) \
                        (search lo ((mid lo hi) - I) n) \
                        (search (mid lo hi) hi n)) \
                    lo
                Grafo search I C C
                """),
            is(equalTo("10\n"))
        );
    }

    @Test
    void compiledFactorialOutputsCorrectValue() throws Exception {
        assertThat(
            "Factorial of X did not produce expected output",
            this.executed("""
                Munus fact n = Sinon (n) n * (fact n - I) I
                Grafo fact X
                """),
            is(equalTo("3628800\n"))
        );
    }

    @Test
    void compiledGcdOutputsCorrectValue() throws Exception {
        assertThat(
            "GCD of XLVIII and XVIII did not produce expected output",
            this.executed("""
                Munus mod a b = a - (a / b) * b
                Munus gcd a b = Sinon b (gcd b (mod a b)) a
                Grafo gcd XLVIII XVIII
                """),
            is(equalTo("6\n"))
        );
    }

    @Test
    void compiledStdinOutputsCorrectValue() throws Exception {
        assertThat(
            "Stdin read and doubled did not produce expected output",
            this.executed(
                """
                As x = Anagnosi
                Grafo x + x
                """,
                "21\n"
            ),
            is(equalTo("42\n"))
        );
    }

    @Test
    void compiledIsThreadSafe() throws Exception {
        final Path lli = this.lli();
        final String rt = this.runtime();
        final String ir = this.compiled("Grafo XLII", rt);
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final String result = new LliExecution(
                        ir, lli, 10
                    ).output();
                    if (!"42\n".equals(result)) {
                        failed.set(true);
                    }
                } catch (final Exception exception) {
                    failed.set(true);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(30, TimeUnit.SECONDS);
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        assertThat(
            "Concurrent executions produced inconsistent results",
            failed.get(),
            is(false)
        );
    }

    /**
     * Compiles and executes a Rome77 source program via lli.
     *
     * @param source Rome77 source code
     * @return standard output from execution
     * @throws Exception if compilation or execution fails
     */
    private String executed(final String source) throws Exception {
        final Path lli = this.lli();
        final String rt = this.runtime();
        final String ir = this.compiled(source, rt);
        return new LliExecution(ir, lli, 10).output();
    }

    /**
     * Compiles and executes a Rome77 source program with stdin via lli.
     *
     * @param source Rome77 source code
     * @param input standard input to feed
     * @return standard output from execution
     * @throws Exception if compilation or execution fails
     */
    private String executed(
        final String source,
        final String input
    ) throws Exception {
        final Path lli = this.lli();
        final String rt = this.runtime();
        final String ir = this.compiled(source, rt);
        return new LliExecution(ir, input, lli, 10).output();
    }

    /**
     * Compiles Rome77 source into combined LLVM IR ready for execution.
     *
     * @param source Rome77 source code
     * @param rt runtime LLVM IR
     * @return combined LLVM IR
     * @throws Exception if parsing, analysis, or emission fails
     */
    private String compiled(
        final String source,
        final String rt
    ) throws Exception {
        final Program program = new Rome77Analyzer(
            new Rome77Syntax(source).parsed()
        ).analyzed();
        final String emitted = new String(
            program.emitted(new SimpleEmission())
                .result().readAllBytes(),
            StandardCharsets.UTF_8
        );
        return new StrippedCombination(rt, emitted).combined();
    }

    /**
     * Resolves the lli path or skips the test.
     *
     * @return path to the lli executable
     */
    private Path lli() {
        try {
            return new LliPath(
                List.of(
                    Path.of("/opt/homebrew/opt/llvm/bin/lli"),
                    Path.of("/usr/local/opt/llvm/bin/lli"),
                    Path.of("/usr/bin/lli")
                )
            ).resolved();
        } catch (final IllegalStateException exception) {
            Assumptions.assumeTrue(
                false,
                "lli not found, skipping integration test"
            );
            throw exception;
        }
    }

    /**
     * Loads the runtime LLVM IR from resources.
     *
     * @return runtime IR content
     * @throws IOException if reading fails
     */
    private String runtime() throws IOException {
        return new String(
            this.getClass()
                .getClassLoader()
                .getResourceAsStream("runtime.ll")
                .readAllBytes(),
            StandardCharsets.UTF_8
        );
    }
}
