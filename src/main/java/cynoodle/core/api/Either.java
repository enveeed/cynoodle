/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package cynoodle.core.api;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A container object which contains either the left or right value but never both.
 * Does not support null values, for null vs. not null values {@link Optional} should be used.
 * @param <L> the left value type
 * @param <R> the right value type
 */
public final class Either<L, R> {

    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Turns a left into a right either and vice-versa.
     *
     * @return A new swapped either instance.
     */
    public Either<R, L> swap() {
        return new Either<>(this.right, this.left);
    }

    /**
     * Returns an optional that will be {@link Optional#isPresent() present} is this is a left
     * instance.
     *
     * @return An optional value for the left.
     */
    public Optional<L> left() {
        return Optional.ofNullable(left);
    }

    /**
     * Returns an optional that will be {@link Optional#isPresent() present} is this is a right
     * instance.
     *
     * @return An optional value for the right.
     */
    public Optional<R> right() {
        return Optional.ofNullable(right);
    }

    /**
     * Returns {@code true} if this is a left instance.
     *
     * @return {@code true} if this is a left.
     */
    public boolean isLeft() {
        return left().isPresent();
    }

    /**
     * Returns {@code true} if this is a right instance.
     *
     * @return {@code true} if this is a right.
     */
    public boolean isRight() {
        return right().isPresent();
    }

    /**
     * Returns the underlying value if this is a left; otherwise, throws.
     *
     * @return The underlying value.
     * @throws IllegalStateException if this is a right instance.
     */
    public L getLeft() {
        return left().orElseThrow(() -> new IllegalStateException("This is a right instance!"));
    }

    /**
     * Returns the underlying value if this is a right; otherwise, throws.
     *
     * @return The underlying value.
     * @throws IllegalStateException if this is a right instance.
     */
    public R getRight() {
        return right().orElseThrow(() -> new IllegalStateException("This is a left instance!"));
    }

    // ===

    public void apply(Consumer<? super L> leftFunc, Consumer<? super R> rightFunc) {
        left().ifPresent(leftFunc);
        right().ifPresent(rightFunc);
    }

    //

    @SuppressWarnings("unchecked")
    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> function) {
        if(isLeft()) return new Either<>(function.apply(this.left), this.right);
        else return (Either<T, R>) this;
    }

    @SuppressWarnings("unchecked")
    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> function) {
        if(isRight()) return new Either<>(this.left, function.apply(this.right));
        else return (Either<L, T>) this;
    }

    //

    @SuppressWarnings("unchecked")
    public <TL, TR> Either<TL, TR> map(Function<? super L, ? extends TL> funcLeft,
                                       Function<? super R, ? extends TR> funcRight) {
        if(isLeft()) return (Either<TL, TR>) mapLeft(funcLeft);
        else return (Either<TL, TR>) mapRight(funcRight);
    }
    // ===

    /**
     * Creates a left either instance.
     *
     * @param value The left value to wrap - may not be null.
     * @param <L> The left type.
     * @param <R> The right type.
     * @return A left either instance wrapping {@code value}.
     */
    public static <L, R> Either<L, R> left(L value) {
        Checks.notNull(value, "value");
        return new Either<>(value, null);
    }

    /**
     * Creates a right either instance.
     *
     * @param value The right value to wrap - may not be null.
     * @param <L> The left type.
     * @param <R> The right type.
     * @return A right either instance wrapping {@code value}.
     */
    public static <L, R> Either<L, R> right(R value) {
        Checks.notNull(value, "value");
        return new Either<>(null, value);
    }
}
