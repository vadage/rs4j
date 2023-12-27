package space.provided.rs.option;

import space.provided.rs.error.ValueAccessError;
import space.provided.rs.ops.ArgInvokable;
import space.provided.rs.ops.ArgVoidInvokable;
import space.provided.rs.ops.Invokable;
import space.provided.rs.ops.PlainInvokable;
import space.provided.rs.result.Result;

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
        return switch (type) {
            case NONE -> false;
            case SOME -> invokable.invoke(some);
        };
    }

    public Some unwrap() throws ValueAccessError {
        if (!isSome()) {
            throw new ValueAccessError("Called `unwrap` on %1$s Option.".formatted(type));
        }
        return some;
    }

    public Some unwrapOr(Some fallback) {
        return switch (type) {
            case SOME -> some;
            case NONE -> fallback;
        };
    }

    public <Mapped> Option<Mapped> map(ArgInvokable<Some, Mapped> invokable) {
        return switch (type) {
            case SOME -> Option.some(invokable.invoke(some));
            case NONE -> Option.none();
        };
    }

    public <Mapped> Mapped mapOr(Mapped fallback, ArgInvokable<Some, Mapped> invokable) {
        return switch (type) {
            case SOME -> invokable.invoke(some);
            case NONE -> fallback;
        };
    }

    public <Mapped> Mapped mapOrElse(PlainInvokable<Mapped> fallback, ArgInvokable<Some, Mapped> invokable) {
        return switch (type) {
            case SOME -> invokable.invoke(some);
            case NONE -> fallback.invoke();
        };
    }

    public <Err> Result<Some, Err> okOr(Err error) {
        return switch (type) {
            case SOME -> (Result<Some, Err>) Result.ok(some);
            case NONE -> Result.error(error);
        };
    }

    public <Err> Result<Some, Err> okOrElse(PlainInvokable<Err> invokable) {
        return switch (type) {
            case SOME -> (Result<Some, Err>) Result.ok(some);
            case NONE -> Result.error(invokable.invoke());
        };
    }

    public Option<Some> andThen(ArgInvokable<Some, Option<Some>> invokable) {
        return switch (type) {
            case SOME -> invokable.invoke(some);
            case NONE -> Option.none();
        };
    }

    public Option<Some> andThenContinue(ArgVoidInvokable<Some> invokable) {
        return switch (type) {
            case SOME -> {
                invokable.invoke(some);
                yield Option.some(some);
            }
            case NONE -> Option.none();
        };
    }

    public Option<Some> and(Option<Some> option) {
        return switch (type) {
            case SOME -> option;
            case NONE -> Option.none();
        };
    }

    public Option<Some> filter(Predicate<Some> predicate) {
        if (isSomeAnd(predicate::test)) {
            return Option.some(some);
        }
        return Option.none();
    }

    public Option<Some> or(Option<Some> option) {
        return switch (type) {
            case SOME -> Option.some(some);
            case NONE -> option;
        };
    }

    public Option<Some> orElse(PlainInvokable<Option<Some>> invokable) {
        return switch (type) {
            case SOME -> Option.some(some);
            case NONE -> invokable.invoke();
        };
    }

    public Option<Some> orElseContinue(Invokable invokable) {
        return switch (type) {
            case SOME -> Option.some(some);
            case NONE -> {
                invokable.invoke();
                yield Option.none();
            }
        };
    }
}
