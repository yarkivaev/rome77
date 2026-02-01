package fp;

import java.util.Optional;

/**
 * Left variant of Either ADT representing an error value.
 *
 * This final class implements Either for the failure case.
 * The left value contains the error, right is empty.
 *
 * Example usage:
 * <pre>
 * Either&lt;Error, Result&gt; result = new Left&lt;&gt;(error);
 * </pre>
 *
 * @param <L> Type of the left (error) value
 * @param <R> Type of the right (success) value
 */
public final class Left<L, R> implements Either<L, R> {

    /**
     * The error value.
     */
    private final L value;

    /**
     * Primary constructor.
     *
     * @param err The error value to wrap
     */
    public Left(final L err) {
        this.value = err;
    }

    @Override
    public Optional<L> left() {
        return Optional.of(this.value);
    }

    @Override
    public Optional<R> right() {
        return Optional.empty();
    }
}
