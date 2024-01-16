package space.provided.rs.option;

import space.provided.rs.error.ValueAccessError;
import space.provided.rs.ops.ArgInvokable;
import space.provided.rs.ops.ArgVoidInvokable;
import space.provided.rs.ops.Invokable;
import space.provided.rs.ops.PlainInvokable;
import space.provided.rs.result.Result;

import java.util.Objects;
import java.util.function.Predicate;

public final class Option<Some> {

    private final Some some;
    private final OptionType type;

    private Option(Some some, OptionType type) {
        this.some = some;
        this.type = type;
    }

    public static <Some> Option<Some> some(Some some) {
        return new Option<>(some, OptionType.SOME);
    }

    public static <Some> Option<Some> none() {
        return new Option<>(null, OptionType.NONE);
    }

    public boolean isSome() {
        return type.equals(OptionType.SOME);
    }

    public boolean isNone() {
        return !isSome();
    }

    public boolean isSomeAnd(ArgInvokable<Some, Boolean> invokable) {
        if (isNone()) {
            return false;
        }
        return invokable.invoke(some);
    }

    public Some unwrap() throws ValueAccessError {
        if (!isSome()) {
            throw new ValueAccessError(String.format("Called `unwrap` on %1$s Option.", type));
        }
        return some;
    }

    public Some unwrapOr(Some fallback) {
        if (isSome()) {
            return some;
        }
        return fallback;
    }

    public <Mapped> Option<Mapped> map(ArgInvokable<Some, Mapped> invokable) {
        if (isSome()) {
            return Option.some(invokable.invoke(some));
        }
        return Option.none();
    }

    public <Mapped> Mapped mapOr(Mapped fallback, ArgInvokable<Some, Mapped> invokable) {
        if (isSome()) {
            return invokable.invoke(some);
        }
        return fallback;
    }

    public <Mapped> Mapped mapOrElse(PlainInvokable<Mapped> fallback, ArgInvokable<Some, Mapped> invokable) {
        if (isSome()) {
            return invokable.invoke(some);
        }
        return fallback.invoke();
    }

    public <Err> Result<Some, Err> okOr(Err error) {
        if (isSome()) {
            return Result.ok(some);
        }
        return Result.error(error);
    }

    public <Err> Result<Some, Err> okOrElse(PlainInvokable<Err> invokable) {
        if (isSome()) {
            return Result.ok(some);
        }
        return Result.error(invokable.invoke());
    }

    public Option<Some> andThen(ArgInvokable<Some, Option<Some>> invokable) {
        if (isSome()) {
            return invokable.invoke(some);
        }
        return Option.none();
    }

    public Option<Some> andThenContinue(ArgVoidInvokable<Some> invokable) {
        if (isSome()) {
            invokable.invoke(some);
            return Option.some(some);
        }
        return Option.none();
    }

    public Option<Some> and(Option<Some> option) {
        if (isSome()) {
            return option;
        }
        return Option.none();
    }

    public Option<Some> filter(Predicate<Some> predicate) {
        if (isSomeAnd(predicate::test)) {
            return Option.some(some);
        }
        return Option.none();
    }

    public Option<Some> or(Option<Some> option) {
        if (isSome()) {
            return Option.some(some);
        }
        return option;
    }

    public Option<Some> orElse(PlainInvokable<Option<Some>> invokable) {
        if (isSome()) {
            return Option.some(some);
        }
        return invokable.invoke();
    }

    public Option<Some> orElseContinue(Invokable invokable) {
        if (isSome()) {
            return Option.some(some);
        }
        invokable.invoke();
        return Option.none();
    }
}
