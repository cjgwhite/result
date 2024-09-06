package uk.cjgwhite.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import uk.cjgwhite.utils.Result.Error;
import uk.cjgwhite.utils.Result.ExceptionResult;
import uk.cjgwhite.utils.Result.Success;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ResultTest {

  private static class ResultHolder<T> {
    private T result;
    public void capture(T obj) {
      this.result = obj;
    }

    public T get() {
      return result;
    }
  }

  @Nested
  @DisplayName("SUCCESS Result")
  class SuccessResult {
    Result<String> success = Result.success("SUCCESS");

    @Test
    @DisplayName("Get returns value")
    void get() {
        assertEquals("SUCCESS", success.get());
    }

    @Test
    @DisplayName("On success operation executed ")
    void onSuccess() {
      var result = new ResultHolder<String>();

      var returned = success.onSuccess(result::capture);

      assertEquals("SUCCESS", result.get());
      assertEquals(success, returned);
    }

    @Test
    @DisplayName("When onSuccess function throws return Error")
    void onSuccessThrows() {
      var returned = success.onSuccess(val -> {throw new RuntimeException(val);});

      assertInstanceOf(Error.class, returned);
      assertThrows(RuntimeException.class, returned::onErrorThrow);
    }

    @Test
    @DisplayName("then function is executed")
    void then() {
      var returned = success.then(value -> value + " TRANSFORMED");
      assertEquals("SUCCESS TRANSFORMED", returned.get());
    }

    @Test
    @DisplayName("then function throws return Error")
    void thenThrows() {
      var returned = success.then(value -> {throw new RuntimeException(value);});

      assertInstanceOf(Error.class, returned);
      assertThrows(RuntimeException.class, returned::onErrorThrow);
    }

    @Test
    @DisplayName("On Fail operation not executed")
    void onFail() {
      var returned = success.onFail(() -> fail("should not have been executed"));
      assertEquals(success, returned);
    }

    @Test
    @DisplayName("onError operation not executed")
    void onError() {
      var returned = success.onError(err -> fail("should not have been executed"));
      assertEquals(success, returned);
    }

    @Test
    @DisplayName("onErrorThrow not executed")
    void onErrorThrow() {
      assertDoesNotThrow(success::onErrorThrow);
    }
  }

  @Nested
  @DisplayName("FAIL Result")
  class FailResult {

    Result<String> fail = Result.fail();

    @Test
    @DisplayName("Get returns null")
    void get() {
      assertNull(fail.get());
    }

    @Test
    @DisplayName("onSuccess operation does not execute")
    void onSuccess() {
      var returned = fail.onSuccess(value -> fail("should not have been executed"));
      assertEquals(fail, returned);
    }

    @Test
    @DisplayName("onSuccess operation does not execute")
    void then() {
      var returned = fail.then(value -> fail("should not have been executed"));
      assertEquals(fail, returned);
    }

    @Test
    @DisplayName("onFail operation does execute")
    void onFail() {
      var result = new ResultHolder<String>();

      var returned = fail.onFail(() -> "FAIL-SUCCESS");
      returned.onSuccess(result::capture);

      assertEquals("FAIL-SUCCESS", result.get());
      assertInstanceOf(Success.class, returned);
    }

    @Test
    @DisplayName("When onFail function throws return Error")
    void onFailThrows() {
      var returned = fail.onFail(() -> {throw new RuntimeException("TEST");});

      assertInstanceOf(Error.class, returned);
      assertThrows(RuntimeException.class, returned::onErrorThrow);
    }

    @Test
    @DisplayName("onError operation not executed")
    void onError() {
      var returned = fail.onError(err -> fail("should not have executed"));
      assertEquals(fail, returned);
    }

    @Test
    @DisplayName("onErrorThrow not executed")
    void onErrorThrow() {
      assertDoesNotThrow(fail::onErrorThrow);
    }

  }

  @Nested
  @DisplayName("ERROR Result")
  class ErrorResult {

    Result<String> error = Result.error(new Exception("ERROR"));

    @Test
    @DisplayName("Get throws exception")
    void get() {
      assertThrows(ExceptionResult.class, error::get);
    }

    @Test
    @DisplayName("onSuccess operation does not execute")
    void onSuccess() {
      var returned = error.onSuccess(val -> fail("should not have executed"));
      assertEquals(error, returned);
    }

    @Test
    @DisplayName("onSuccess operation does not execute")
    void then() {
      var returned = error.then(val -> fail("should not have executed"));
      assertEquals(error, returned);
    }

    @Test
    @DisplayName("onFail operation does not execute")
    void onFail() {
      var returned = error.onFail(() -> fail("should not have executed"));
      assertEquals(error, returned);
    }

    @Test
    @DisplayName("onError operation does executed")
    void onError() {
      var result = new ResultHolder<Exception>();

      var returned = error.onError(result::capture);

      assertEquals("ERROR", result.get().getMessage());
      assertEquals(error, returned);
    }

    @Test
    @DisplayName("onErrorThrow not executed")
    void onErrorThrow() {
      assertThrows(Exception.class, error::onErrorThrow);
    }

  }

  @Nested
  @DisplayName("From static method")
  class From {
    @Test
    @DisplayName("Operation that returns value gives a Result.Success with that value")
    void success() {
      var result = Result.from(() -> "SUCCESS VALUE");

      assertEquals("SUCCESS VALUE", result.get());
      assertInstanceOf(Result.Success.class, result);
    }

    @Test
    @DisplayName("Operation that throws exception returns Result.Error wrapping Exception")
    void error() {
      var result = Result.from(() -> { throw new RuntimeException("ERROR"); });

      assertInstanceOf(Result.Error.class, result);
      assertThrows(RuntimeException.class, result::onErrorThrow);
    }
  }
}
