package runtime;

/**
 * Executes LLVM IR and captures output.
 *
 * Runs the given IR through an LLVM interpreter and returns stdout.
 *
 * Example usage:
 * <pre>
 * String output = new LliExecution(
 *     ir, interpreter, directory, 10
 * ).output();
 * </pre>
 */
public interface Execution {

    /**
     * Executes the IR and returns the standard output.
     *
     * @return Standard output from the executed program
     * @throws Exception if execution fails or times out
     */
    String output() throws Exception;
}
