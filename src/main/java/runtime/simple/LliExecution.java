package runtime.simple;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import runtime.Execution;

/**
 * Executes LLVM IR by writing it to a file and running lli.
 *
 * Creates a temporary .ll file in the given directory, invokes the
 * interpreter, and returns the captured standard output.
 *
 * Example usage:
 * <pre>
 * String output = new LliExecution(
 *     irContent, lliPath, tempDir, 10
 * ).output();
 * </pre>
 */
public final class LliExecution implements Execution {

    private final String ir;
    private final Path interpreter;
    private final Path directory;
    private final int timeout;

    /**
     * Primary constructor.
     *
     * @param ir LLVM IR source code
     * @param interpreter Path to the lli executable
     * @param directory Directory for temporary files
     * @param timeout Maximum execution time in seconds
     */
    public LliExecution(
        final String ir,
        final Path interpreter,
        final Path directory,
        final int timeout
    ) {
        this.ir = ir;
        this.interpreter = interpreter;
        this.directory = directory;
        this.timeout = timeout;
    }

    @Override
    public String output() throws Exception {
        final Path file = Files.createTempFile(
            this.directory, "test", ".ll"
        );
        Files.writeString(file, this.ir, StandardCharsets.UTF_8);
        final Process process = new ProcessBuilder(
            this.interpreter.toString(), file.toString()
        ).directory(this.directory.toFile())
            .redirectErrorStream(false)
            .start();
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
