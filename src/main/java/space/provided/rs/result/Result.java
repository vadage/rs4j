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
            throw new ValueAccessError(String.format("Called `unwrap` on %1$s Result.", type));
        }
        return ok;
    }

    public Error unwrapError() throws ValueAccessError {
        if (!isError()) {
            throw new ValueAccessError(String.format("Called `unwrapError` on %1$s Result.", type));
        }
        return error;
    }

    public boolean isOk() {
        return type.equals(ResultType.OK);
    }

    public boolean isOkAnd(ArgInvokable<Ok, Boolean> invokable) {
        if (isError()) {
            return false;
        }
        return invokable.invoke(ok);
    }

    public boolean isError() {
        return !isOk();
    }

    public boolean isErrorAnd(ArgInvokable<Error, Boolean> invokable) {
        if (isOk()) {
            return false;
        }
        return invokable.invoke(error);
    }

    public <Mapped> Result<Mapped, Error> map(ArgInvokable<Ok, Mapped> invokable) {
        if (isOk()) {
            return Result.ok(invokable.invoke(ok));
        }
        return Result.error(error);
    }

    public <Mapped> Mapped mapOr(Mapped fallback, ArgInvokable<Ok, Mapped> invokable) {
        if (isOk()) {
            return invokable.invoke(ok);
        }
        return fallback;
    }

    public <Mapped> Mapped mapOrElse(ArgInvokable<Error, Mapped> fallback, ArgInvokable<Ok, Mapped> invokable) {
        if (isOk()) {
            return invokable.invoke(ok);
        }
        return fallback.invoke(error);
    }

    public Result<Ok, Error> and(Result<Ok, Error> result) {
        if (isOk()) {
            return result;
        }
        return Result.error(error);
    }

    public Result<Ok, Error> andThen(ArgInvokable<Ok, Result<Ok, Error>> invokable) {
        if (isOk()) {
            return invokable.invoke(ok);
        }
        return Result.error(error);
    }

    public Result<Ok, Error> andThenContinue(ArgVoidInvokable<Ok> invokable) {
        if (isOk()) {
            invokable.invoke(ok);
            return Result.ok(ok);
        }
        return Result.error(error);
    }

    public Result<Ok, Error> or(Result<Ok, Error> result) {
        if (isOk()) {
            return Result.ok(ok);
        }
        return result;
    }

    public Result<Ok, Error> orElse(ArgInvokable<Error, Result<Ok, Error>> invokable) {
        if (isOk()) {
            return Result.ok(ok);
        }
        return invokable.invoke(error);
    }

    public Result<Ok, Error> orElseContinue(ArgVoidInvokable<Error> invokable) {
        if (isOk()) {
            return Result.ok(ok);
        }
        invokable.invoke(error);
        return Result.error(error);
    }

    public Ok unwrapOrElse(ArgInvokable<Error, Ok> invokable) {
        if (isOk()) {
            return ok;
        }
        return invokable.invoke(error);
    }
}
