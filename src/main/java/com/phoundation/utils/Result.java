package com.phoundation.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

sealed interface Result<L> permits Result.Success, Result.Error, Result.Failure {
  L get();

  default Result<L> onSuccess(Consumer<L> operation) {
    return this;
  }

  default Result<L> onFail(Supplier<L> operation) {
    return this;
  }

  default Result<L> onError(Consumer<Exception> operation) {
    return this;
  }

  default Result<L> onErrorThrow() throws Exception {
    return this;
  }

  static <L> Result<L> success(L value) {
    return new Success<>(value);
  }

  static <L> Result<L> fail() {
    return new Failure<>();
  }

  static <L> Result<L> error(Exception error) {
    return new Error<>(error);
  }

  static <L> Result<L> from(Supplier<L> operation) {
    try {
      return success(operation.get());
    } catch (Exception e) {
      return error(e);
    }
  }

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
  }

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

  record Error<L, E extends Exception>(E error) implements Result<L> {

    @Override
    public L get() {
      throw new RuntimeException(error);
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
}
