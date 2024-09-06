package uk.cjgwhite.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A return type used to represent SUCCESS, FAILURE and ERROR states.
 *
 * @param <L> the type of the return value
 */
public sealed interface Result<L> permits Result.Success, Result.Error, Result.Failure {

  /**
   * Creates a Result.Success with the given value
   *
   * @param value the value to wrap in the success
   * @param <L> the type of the return value
   * @return a Result.Success
   */
  static <L> Result<L> success(L value) {
    return new Success<>(value);
  }

  /**
   * Creates a Result.Failure
   *
   * @param <L> the type of the return value
   * @return a Result.Failure
   */
  static <L> Result<L> fail() {
    return new Failure<>();
  }

  /**
   * Creates a Result.Error with the given Exception
   *
   * @param error the Exception that was thrown to indicate error
   * @param <L> the type of the return value
   * @return a Result.Error
   */
  static <L, E extends Exception> Result<L> error(E error) {
    return new Error<>(error);
  }

  /**
   * Creates a Result object based on the result of
   * the operation If the operation completes without
   * exception then the return result is wrapped in a
   * Result.Success and returned If the operation throws
   * an exception then this returns a Result.Error wrapping
   * the thrown Exception.
   *
   * @param operation Supplier for the value to wrap
   * @param <L> the type of the return value
   * @return a Result.Success wrapping the result of the operation or a Result.Error
   */
  static <L> Result<L> from(Supplier<L> operation) {
    try {
      return success(operation.get());
    } catch (Exception e) {
      return error(e);
    }
  }

  /**
   * Returns the Wrapped value. If is a Result.Failure will
   * return null If is a Result.Error will throw the Exception
   * wrapped in a RuntimeException.
   *
   * @return the wrapped value if Result.Success or null if Result.Failure
   */
  L get();

  /**
   * Executes the given 'operation' with the value wrapped by
   * the Result if the result is a Result.Success
   *
   * <p>Will return this Result object for chaining.
   *
   * @param operation a Consumer that acts on the value wrapped by this Result
   * @return this result object for chaining
   */
  default Result<L> onSuccess(Consumer<L> operation) {
    return this;
  }

  /**
   * Execute the given 'operation' with the value wrapper by
   * the Result if the result is a Result.Success
   *
   * <p>Will return a new Result wrapping the result of the operation.
   *
   * @param operation function to perform on the value of the Result
   * @param <R> The return type of the operation, type of the returned Result.
   * @return the result of the operation wrapped in a Result
   */
  default <R> Result<?> then(Function<L, R> operation) {
    return this;
  }

  /**
   * Executes the given 'operation' if the result is a Result.Failure
   *
   * <p>If is a Result.Failure Will return  Result wrapping the result of
   * the operation If is Result.Success or Result.Error will return this
   * Result for chaining
   *
   * @param operation a Supplier that will be executed if this is a Result.Failure
   * @return a Result that wraps the outcome of the operation
   */
  default Result<L> onFail(Supplier<L> operation) {
    return this;
  }

  /**
   * Executes the given 'operation' with the provided Exception if this
   * is a Result.Error
   *
   * <p>Will return this Result for chaining purposes.
   *
   * @param operation a Consumer that will be executed on the Exception if this a Result.Error
   * @return this Result for chaining.
   */
  default Result<L> onError(Consumer<Exception> operation) {
    return this;
  }

  /**
   * Throws the wrapped Exception if it is a Result.Error
   *
   * @return This Result
   * @throws Exception the exception that was wrapped
   */
  default Result<L> onErrorThrow() throws Exception {
    return this;
  }

  /**
   * A Result representing a SUCCESS.
   *
   * @param value the return value for the success
   * @param <L> the type of the return value
   */
  record Success<L>(L value) implements Result<L> {

    @Override
    public L get() {
      return value;
    }

    @Override
    public Result<L> onSuccess(Consumer<L> operation) {
      try {
        operation.accept(value);
      } catch (Exception e) {
        return error(e);
      }
      return this;
    }

    @Override
    public <R> Result<R> then(Function<L, R> operation) {
      return from(() -> operation.apply(get()));
    }
  }

  /**
   * A Result representing a FAIL.
   *
   * @param <L> the type of the return value
   */
  record Failure<L>() implements Result<L> {

    @Override
    public L get() {
      return null;
    }

    @Override
    public Result<L> onFail(Supplier<L> operation) {
      try {
        return success(operation.get());
      } catch (Exception e) {
        return error(e);
      }
    }
  }

  /**
   * A Result representing an ERROR.
   *
   * @param error the Exception thrown
   * @param <L> the success return type
   * @param <E> the type of the Exception thrown.
   */
  record Error<L, E extends Exception>(E error) implements Result<L> {

    @Override
    public L get() {
      throw new ExceptionResult(error);
    }

    @Override
    public Result<L> onError(Consumer<Exception> operation) {
      operation.accept(error);
      return this;
    }

    @Override
    public Result<L> onErrorThrow() throws E {
      throw error;
    }
  }

  /**
   * Wraps the Throwable into a RuntimeException.
   * Used when Result.Error.get() is executed, wraps
   * the original Exception.
   */
  final class ExceptionResult extends RuntimeException {
    ExceptionResult(Throwable cause) {
      super(cause);
    }
  }
}
