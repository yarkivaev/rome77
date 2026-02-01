package fp;

import java.util.Optional;

/**
 * Algebraic data type representing a value of one of two types.
 *
 * Either is used to represent computations that may fail. By convention,
 * Left contains an error and Right contains a success value. This follows
 * functional programming patterns and avoids null or exceptions.
 *
 * Example usage:
 * <pre>
 * Either&lt;Error, Result&gt; result = computation();
 * if (result.left().isPresent()) {
 *     Error error = result.left().get();
 * } else {
 *     Result value = result.right().get();
 * }
 * </pre>
 *
 * @param <L> Type of the left value (typically error)
 * @param <R> Type of the right value (typically success)
 */
public interface Either<L, R> {

    /**
     * Returns the left value if present.
     *
     * For Left instances, returns Optional containing the error.
     * For Right instances, returns empty Optional.
     *
     * @return Optional containing left value, or empty
     */
    Optional<L> left();

    /**
     * Returns the right value if present.
     *
     * For Right instances, returns Optional containing the success value.
     * For Left instances, returns empty Optional.
     *
     * @return Optional containing right value, or empty
     */
    Optional<R> right();
}
