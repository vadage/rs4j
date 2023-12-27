> Java adaptation of some great concepts on which Rust was built.
# rs4j
Rust encourages developers to handle errors and non-values properly by design.<br>
This leads to fewer non-logic bugs in production and the code might look cleaner as well.

The rs4j library is written in pure Java and has just *adapted* some concepts, which are also used in Rust. The method names are kept similar (snake_case to camelCase) for developers using both languages.<br>
There are some additional wrapper methods, such as `andThenContinue()` in both `Result` and `Option` to get rid of return statements. A new instance will then be returned automatically.

## Exceptionless
Java has two types of Exceptions; `checked` and `unchecked`. Unchecked Exceptions don't have to be declared to be thrown in a method, which increases the likelihood of them not being handled.<br>
This is where `Result`s come in handy with their representation of `Ok` and `Error`. Calling `unwrap` on `Error` or `unwrapError` on `Ok` will lead to a `ValueAccessError`.
```java
final Result<User, ?> userResult = userRpc.login(email, password);
if (userResult.isOk()) {
    final User user = userResult.unwrap();
    messages.queue("Hello %1$s.".formatted(user.getUsername()));
}
```

## No NULL
It's not always obvious if a method always returns an object or if it could be null as well.<br>
Instead of `null`, an `Option` can be used, which represents one of two states (`Some` and `None`). Calling `unwrap` on `None` will lead to a `ValueAccessError`.
```java
final Option<Date> terminationDateOption = user.getTerminationDate();
if (terminationDateOption.isSome()) {
    final Date terminationDate = terminationDateOption.unwrap();
    messages.queue("Your login will be deactivated after %1$s.".formatted(dateFormatter.format(terminationDate)));
}
```

## Monads for Result and Option
Monads can improve the codes aesthetics by getting rid of some `if` statements, variable declarations and `unwrap` calls.
```java
userRpc.login(email, password).andThenContinue(user -> {
    final String username = user.getUsername();

    user.getTerminationDate()
        .andThenContinue(terminationDate -> {
            final String formattedDate = dateFormatter.format(terminationDate);
            messages.queue("Hello %1$s. Your login will be deactivated after %2$s.".formatted(username, formattedDate));
        })
        .orElseContinue(() -> {
            messages.queue("Hello %1$s.".formatted(username));
        });
});
```
