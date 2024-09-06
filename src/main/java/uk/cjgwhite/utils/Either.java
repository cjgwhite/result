package uk.cjgwhite.utils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A return type used where two different types can be returned.
 *
 * @param <L> the type of the 'left' value
 * @param <R> the type of the 'right' value
 */
public sealed interface Either<L, R> permits Either.Left, Either.Right {

  /**
   * Create an Either where the 'left' value is returned.
   *
   * @param value the 'left' value
   * @param <L>   the type of the 'left' value
   * @param <R>   the type of the 'right value
   * @return and Either wrapping the 'left' value
   */
  static <L, R> Either<L, R> left(L value) {
    return new Either.Left<>(value);
  }

  /**
   * Create an Either where the 'right' value is returned.
   *
   * @param value the 'right' value
   * @param <L>   the type of the 'left' value
   * @param <R>   the type of the 'right value
   * @return and Either wrapping the 'right' value
   */
  static <L, R> Either<L, R> right(R value) {
    return new Either.Right<>(value);
  }

  /**
   * Execute the given functions on the value dependant upon whether the
   * 'left' or 'right' value has been returned.
   *
   * <p>if the 'left' value has been returned then will
   * execute the 'ifLeft' function passing it the 'left' value.
   *
   * <p>if the 'right' value has been returned then will
   * execute the 'ifRight' function passing it the 'right' value.
   *
   * @param ifLeft  function to execute if the 'left' value is returned
   * @param ifRight function to execute if the 'right' value is returned
   * @param <T>     the type of the return value
   * @return the result of executing the provided function
   */
  <T> T match(Function<L, T> ifLeft, Function<R, T> ifRight);

  /**
   * If the 'right' value has been returned execute the provided consumer passing it the value.
   *
   * @param ifRight the consumer to execute.
   * @return this Either for method chaining.
   */
  default Either<L, R> ifRight(Consumer<R> ifRight) {
    return this;
  }

  /**
   * If the 'left' value has been returned execute the provided consumer passing it the value.
   *
   * @param ifLeft the consumer to execute.
   * @return this Either for method chaining.
   */
  default Either<L, R> ifLeft(Consumer<L> ifLeft) {
    return this;
  }

  /**
   * An Either where the 'left' value has been returned.
   *
   * @param <L> the type of the 'left' value
   * @param <R> the type of the 'right' value
   */
  final class Left<L, R> implements Either<L, R> {

    private final L value;

    private Left(L left) {
      value = left;
    }

    public <T> T match(Function<L, T> ifLeft, Function<R, T> ifRight) {
      return ifLeft.apply(value);
    }

    @Override
    public Either<L, R> ifLeft(Consumer<L> ifLeft) {
      ifLeft.accept(value);
      return this;
    }
  }

  /**
   * An Either where the 'right' value has been returned.
   *
   * @param <L> the type of the 'left' value
   * @param <R> the type of the 'right' value
   */
  final class Right<L, R> implements Either<L, R> {

    private final R value;

    private Right(R right) {
      value = right;
    }

    public <T> T match(Function<L, T> ifLeft, Function<R, T> ifRight) {
      return ifRight.apply(value);
    }

    @Override
    public Either<L, R> ifRight(Consumer<R> ifRight) {
      ifRight.accept(value);
      return this;
    }
  }
}
