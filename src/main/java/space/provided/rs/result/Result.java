package space.provided.rs.result;

import space.provided.rs.error.ValueAccessError;
import space.provided.rs.ops.ArgInvokable;
import space.provided.rs.ops.ArgVoidInvokable;

public final class Result<Ok, Error> {

    private final Ok ok;
    private final Error error;
    private final ResultType type;

    private Result(ResultType type, Ok ok, Error error) {
        this.type = type;
        this.ok = ok;
        this.error = error;
    }

    public static <Ok, Err> Result<Ok, Err> ok() {
        return ok(null);
    }

    public static <Ok, Err> Result<Ok, Err> ok(Ok value) {
        return new Result<>(ResultType.OK, value, null);
    }

    public static <Ok, Err> Result<Ok, Err> error() {
        return error(null);
    }

    public static <Ok, Err> Result<Ok, Err> error(Err value) {
        return new Result<>(ResultType.ERROR, null, value);
    }

    public Ok unwrap() throws ValueAccessError {
        if (!isOk()) {
            throw new ValueAccessError("Called `unwrap` on %1$s Result.".formatted(type));
        }
        return ok;
    }

    public Error unwrapError() throws ValueAccessError {
        if (!isError()) {
            throw new ValueAccessError("Called `unwrapError` on %1$s Result.".formatted(type));
        }
        return error;
    }

    public boolean isOk() {
        return type.equals(ResultType.OK);
    }

    public boolean isOkAnd(ArgInvokable<Ok, Boolean> invokable) {
        return switch (type) {
            case ERROR -> false;
            case OK -> invokable.invoke(ok);
        };
    }

    public boolean isError() {
        return !isOk();
    }

    public boolean isErrorAnd(ArgInvokable<Error, Boolean> invokable) {
        return switch (type) {
            case OK -> false;
            case ERROR -> invokable.invoke(error);
        };
    }

    public <Mapped> Result<Mapped, Error> map(ArgInvokable<Ok, Mapped> invokable) {
        return switch (type) {
            case OK -> Result.ok(invokable.invoke(ok));
            case ERROR -> Result.error(error);
        };
    }

    public <Mapped> Mapped mapOr(Mapped fallback, ArgInvokable<Ok, Mapped> invokable) {
        return switch (type) {
            case OK -> invokable.invoke(ok);
            case ERROR -> fallback;
        };
    }

    public <Mapped> Mapped mapOrElse(ArgInvokable<Error, Mapped> fallback, ArgInvokable<Ok, Mapped> invokable) {
        return switch (type) {
            case OK -> invokable.invoke(ok);
            case ERROR -> fallback.invoke(error);
        };
    }

    public Result<Ok, Error> and(Result<Ok, Error> result) {
        return switch (type) {
            case OK -> result;
            case ERROR -> Result.error(error);
        };
    }

    public Result<Ok, Error> andThen(ArgInvokable<Ok, Result<Ok, Error>> invokable) {
        return switch (type) {
            case OK -> invokable.invoke(ok);
            case ERROR -> Result.error(error);
        };
    }

    public Result<Ok, Error> andThenContinue(ArgVoidInvokable<Ok> invokable) {
        return switch (type) {
            case OK -> {
                invokable.invoke(ok);
                yield Result.ok(ok);
            }
            case ERROR -> Result.error(error);
        };
    }

    public Result<Ok, Error> or(Result<Ok, Error> result) {
        return switch (type) {
            case OK -> Result.ok(ok);
            case ERROR -> result;
        };
    }

    public Result<Ok, Error> orElse(ArgInvokable<Error, Result<Ok, Error>> invokable) {
        return switch (type) {
            case OK -> Result.ok(ok);
            case ERROR -> invokable.invoke(error);
        };
    }

    public Result<Ok, Error> orElseContinue(ArgVoidInvokable<Error> invokable) {
        return switch (type) {
            case OK -> Result.ok(ok);
            case ERROR -> {
                invokable.invoke(error);
                yield Result.error(error);
            }
        };
    }

    public Ok unwrapOrElse(ArgInvokable<Error, Ok> invokable) {
        return switch (type) {
            case OK -> ok;
            case ERROR -> invokable.invoke(error);
        };
    }
}
