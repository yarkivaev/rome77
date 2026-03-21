package runtime.simple;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import runtime.Execution;

/**
 * Executes LLVM IR by writing it to a file and running lli.
 *
 * Creates a temporary .ll file, invokes the interpreter,
 * optionally feeds standard input, and returns captured output.
 *
 * Example usage:
 * <pre>
 * String output = new LliExecution(
 *     irContent, "42\n", lliPath, 10
 * ).output();
 * </pre>
 */
public final class LliExecution implements Execution {

    private final String ir;
    private final String input;
    private final Path interpreter;
    private final int timeout;

    /**
     * Primary constructor.
     *
     * @param ir LLVM IR source code
     * @param input standard input to feed to the process
     * @param interpreter path to the lli executable
     * @param timeout maximum execution time in seconds
     */
    public LliExecution(
        final String ir,
        final String input,
        final Path interpreter,
        final int timeout
    ) {
        this.ir = ir;
        this.input = input;
        this.interpreter = interpreter;
        this.timeout = timeout;
    }

    /**
     * Secondary constructor without standard input.
     *
     * @param ir LLVM IR source code
     * @param interpreter path to the lli executable
     * @param timeout maximum execution time in seconds
     */
    public LliExecution(
        final String ir,
        final Path interpreter,
        final int timeout
    ) {
        this(ir, "", interpreter, timeout);
    }

    @Override
    public String output() throws Exception {
        final Path directory = Files.createTempDirectory("lli");
        final Path file = Files.createTempFile(
            directory, "test", ".ll"
        );
        Files.writeString(file, this.ir, StandardCharsets.UTF_8);
        final Process process = new ProcessBuilder(
            this.interpreter.toString(), file.toString()
        ).directory(directory.toFile())
            .redirectErrorStream(false)
            .start();
        if (!this.input.isEmpty()) {
            process.getOutputStream().write(
                this.input.getBytes(StandardCharsets.UTF_8)
            );
        }
        process.getOutputStream().close();
        final boolean finished = process.waitFor(
            this.timeout, TimeUnit.SECONDS
        );
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException(
                "Execution timed out after " + this.timeout + " seconds"
            );
        }
        final String stdout = new String(
            process.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );
        if (process.exitValue() != 0) {
            final String stderr = new String(
                process.getErrorStream().readAllBytes(),
                StandardCharsets.UTF_8
            );
            throw new IllegalStateException(
                "Execution failed with exit code "
                    + process.exitValue() + ": " + stderr
            );
        }
        return stdout;
    }
}
