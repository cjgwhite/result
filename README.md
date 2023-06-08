# Result

A micro-library that provides and implementation of a 'Result' object used 
to wrap return values from methods indicating SUCCESS, FAILURE and ERROR conditions.

## Usage
Intended for use as a return value from methods
~~~
public Result<String> doSomething() {
    Result<String> result;
    try {
        ... do something interesting ...
        if (value != null) {
            result = Result.success(value);
        } else {
            result = Result.fail();
        }
    } catch (Exception e) {
        result = Result.errpr(e);
    }
    
    return result;
}

...

Result<String> result = doSomthing();

result.onFail(this::doSomethingElse)
        .onSuccess(this::handleTheResult)
        .onError(logger::error)
        .onErrorThrow();


    
~~~
