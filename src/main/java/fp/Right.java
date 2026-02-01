package fp;

import java.util.Optional;

/**
 * Right variant of Either ADT representing a success value.
 *
 * This final class implements Either for the success case.
 * The right value contains the result, left is empty.
 *
 * Example usage:
 * <pre>
 * Either&lt;Error, Result&gt; result = new Right&lt;&gt;(value);
 * </pre>
 *
 * @param <L> Type of the left (error) value
 * @param <R> Type of the right (success) value
 */
public final class Right<L, R> implements Either<L, R> {

    /**
     * The success value.
     */
    private final R value;

    /**
     * Primary constructor.
     *
     * @param val The success value to wrap
     */
    public Right(final R val) {
        this.value = val;
    }

    @Override
    public Optional<L> left() {
        return Optional.empty();
    }

    @Override
    public Optional<R> right() {
        return Optional.of(this.value);
    }
}
