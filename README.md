# Result

A micro-library that provides an implementation of a return objects used 
to wrap return values from methods.

'Result' indicating SUCCESS, FAILURE and ERROR conditions.
'Either' wrapping two different possible types.

## Result Usage
Intended for use as a return value from methods
~~~java
public Result<String> doSomething() {
    Result<String> result;
    try {
        //... do something interesting ...
        if (value != null) {
            result = Result.success(value);
        } else {
            result = Result.fail();
        }
    } catch (Exception e) {
        result = Result.error(e);
    }
    
    return result;
}
~~~
~~~java
public void execute() {
  Result<String> result = doSomthing();

  result.onFail(() -> doSomethingElse())
        .onSuccess(value -> doSomethingWithValue(value))
        .onError(error -> log.error(error))
        .onErrorThrow();
}
~~~

## Either Usage
Intended for use as a return value from methods

~~~java
import com.phoundation.utils.Either;

public Either<String, Integer> convert(String value) {
  try {
    return Either.right(Integer.parseInt(value));
  } catch (NumberFormatException ex) {
    return Either.Left(value);
  }
}
~~~
~~~java
public SomeObject execute() {
  Either<String, Integer> result = convert("234");
  
  return result.match(
      str -> handleString(str), //the result was a string
      intgr -> handleInteger(intgr)
  );
}

public void execute() {
  Either<String, Integer> result = convert("234");

  result
      .ifRight(val -> doSomethingWithInteger(val))
      .ifLeft(val -> doSomethingWithString(val));
}
~~~
