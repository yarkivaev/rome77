package runtime;

import emission.simple.SimpleEmission;
import ir.Expression;
import ir.Operator;
import ir.simple.IrBinaryOp;
import ir.simple.IrCall;
import ir.simple.IrConditional;
import ir.simple.IrDeclaration;
import ir.simple.IrFunction;
import ir.simple.IrLiteral;
import ir.simple.IrOutput;
import ir.simple.IrProgram;
import ir.simple.IrUnaryOp;
import ir.simple.IrVariable;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import runtime.simple.LliExecution;
import runtime.simple.LliPath;
import runtime.simple.StrippedCombination;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * Integration tests that build IR programs, emit LLVM IR, and execute
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
    void emittedLiteralOutputsCorrectValue() throws Exception {
        assertThat(
            "Literal 42 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(new IrOutput(new IrLiteral(42)))
                )
            ),
            is(equalTo("42\n"))
        );
    }

    @Test
    void emittedNegationOutputsCorrectValue() throws Exception {
        assertThat(
            "Negation of 7 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrUnaryOp(Operator.SUB, new IrLiteral(7))
                        )
                    )
                )
            ),
            is(equalTo("-7\n"))
        );
    }

    @Test
    void emittedAdditionOutputsCorrectValue() throws Exception {
        assertThat(
            "Addition of 3 and 5 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrBinaryOp(
                                Operator.ADD,
                                new IrLiteral(3),
                                new IrLiteral(5)
                            )
                        )
                    )
                )
            ),
            is(equalTo("8\n"))
        );
    }

    @Test
    void emittedSubtractionOutputsCorrectValue() throws Exception {
        assertThat(
            "Subtraction of 3 from 10 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrBinaryOp(
                                Operator.SUB,
                                new IrLiteral(10),
                                new IrLiteral(3)
                            )
                        )
                    )
                )
            ),
            is(equalTo("7\n"))
        );
    }

    @Test
    void emittedMultiplicationOutputsCorrectValue() throws Exception {
        assertThat(
            "Multiplication of 6 and 7 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrBinaryOp(
                                Operator.MUL,
                                new IrLiteral(6),
                                new IrLiteral(7)
                            )
                        )
                    )
                )
            ),
            is(equalTo("42\n"))
        );
    }

    @Test
    void emittedDivisionOutputsCorrectValue() throws Exception {
        assertThat(
            "Division of 15 by 3 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrBinaryOp(
                                Operator.DIV,
                                new IrLiteral(15),
                                new IrLiteral(3)
                            )
                        )
                    )
                )
            ),
            is(equalTo("5\n"))
        );
    }

    @Test
    void emittedVariableOutputsCorrectValue() throws Exception {
        assertThat(
            "Variable with value 99 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrDeclaration("x", new IrLiteral(99)),
                        new IrOutput(new IrVariable("x"))
                    )
                )
            ),
            is(equalTo("99\n"))
        );
    }

    @Test
    void emittedFunctionCallOutputsCorrectValue() throws Exception {
        assertThat(
            "Function returning 77 did not produce expected output",
            this.executed(
                new IrProgram(
                    List.of(
                        new IrFunction(
                            "constant",
                            Collections.emptyList(),
                            new IrLiteral(77)
                        )
                    ),
                    List.of(
                        new IrOutput(
                            new IrCall(
                                "constant",
                                Collections.<Expression>emptyList()
                            )
                        )
                    )
                )
            ),
            is(equalTo("77\n"))
        );
    }

    @Test
    void emittedParameterizedFunctionOutputsCorrectValue() throws Exception {
        assertThat(
            "Function doubling 21 did not produce expected output",
            this.executed(
                new IrProgram(
                    List.of(
                        new IrFunction(
                            "doubled",
                            List.of("n"),
                            new IrBinaryOp(
                                Operator.ADD,
                                new IrVariable("n"),
                                new IrVariable("n")
                            )
                        )
                    ),
                    List.of(
                        new IrOutput(
                            new IrCall(
                                "doubled",
                                List.<Expression>of(new IrLiteral(21))
                            )
                        )
                    )
                )
            ),
            is(equalTo("42\n"))
        );
    }

    @Test
    void emittedConditionalTrueOutputsCorrectValue() throws Exception {
        assertThat(
            "Conditional with true condition did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrConditional(
                                new IrLiteral(1),
                                new IrLiteral(10),
                                new IrLiteral(20)
                            )
                        )
                    )
                )
            ),
            is(equalTo("10\n"))
        );
    }

    @Test
    void emittedConditionalFalseOutputsCorrectValue() throws Exception {
        assertThat(
            "Conditional with false condition did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrConditional(
                                new IrLiteral(0),
                                new IrLiteral(10),
                                new IrLiteral(20)
                            )
                        )
                    )
                )
            ),
            is(equalTo("20\n"))
        );
    }

    @Test
    void emittedNestedArithmeticOutputsCorrectValue() throws Exception {
        assertThat(
            "Nested arithmetic (2+3)*4 did not produce expected output",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(
                            new IrBinaryOp(
                                Operator.MUL,
                                new IrBinaryOp(
                                    Operator.ADD,
                                    new IrLiteral(2),
                                    new IrLiteral(3)
                                ),
                                new IrLiteral(4)
                            )
                        )
                    )
                )
            ),
            is(equalTo("20\n"))
        );
    }

    @Test
    void emittedMultipleOutputsProduceCorrectValues() throws Exception {
        assertThat(
            "Multiple outputs did not produce expected values",
            this.executed(
                new IrProgram(
                    Collections.emptyList(),
                    List.of(
                        new IrOutput(new IrLiteral(42)),
                        new IrOutput(new IrLiteral(99))
                    )
                )
            ),
            is(equalTo("42\n99\n"))
        );
    }

    @Test
    void emittedIsThreadSafe() throws Exception {
        final Path lli = this.lli();
        final String rt = this.runtime();
        final IrProgram program = new IrProgram(
            Collections.emptyList(),
            List.of(new IrOutput(new IrLiteral(42)))
        );
        final String ir = new StrippedCombination(
            rt,
            new String(
                program.emitted(new SimpleEmission())
                    .result().readAllBytes(),
                StandardCharsets.UTF_8
            )
        ).combined();
        final int threads = 8;
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int idx = 0; idx < threads; idx++) {
            pool.submit(() -> {
                try {
                    final Path directory = Files.createTempDirectory(
                        "lli-thread"
                    );
                    final String result = new LliExecution(
                        ir, lli, directory, 10
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
     * Emits, combines, and executes an IR program via lli.
     *
     * @param program IR program to execute
     * @return Standard output from execution
     * @throws Exception if emission or execution fails
     */
    private String executed(final IrProgram program) throws Exception {
        final Path lli = this.lli();
        final String rt = this.runtime();
        final String emitted = new String(
            program.emitted(new SimpleEmission())
                .result().readAllBytes(),
            StandardCharsets.UTF_8
        );
        final String ir = new StrippedCombination(
            rt, emitted
        ).combined();
        final Path directory = Files.createTempDirectory("lli-test");
        return new LliExecution(ir, lli, directory, 10).output();
    }

    /**
     * Resolves the lli path or skips the test.
     *
     * @return Path to the lli executable
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
     * @return Runtime IR content
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
