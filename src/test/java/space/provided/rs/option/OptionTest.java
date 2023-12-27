package space.provided.rs.option;

import org.junit.jupiter.api.Test;
import space.provided.rs.error.ValueAccessError;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void isSome() {
        final Option<String> option = Option.some("Foo");
        assertTrue(option.isSome());
    }

    @Test
    void isNone() {
        final Option<?> option = Option.none();
        assertTrue(option.isNone());
    }

    @Test
    void isSomeAndWithSome() {
        final Option<String> option = Option.some("Foo");
        assertTrue(option.isSomeAnd(s -> s.equals("Foo")));
    }

    @Test
    void isSomeAndWithNone() {
        final Option<String> option = Option.none();
        assertFalse(option.isSomeAnd(s -> s.equals("Foo")));
    }

    @Test
    void unwrapWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("Foo", option.unwrap());
    }

    @Test
    void unwrapWithNone() {
        final Option<String> option = Option.none();
        assertThrowsExactly(ValueAccessError.class, option::unwrap);
    }

    @Test
    void unwrapOrWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("Foo", option.unwrapOr("Bar"));
    }

    @Test
    void unwrapOrWithNone() {
        final Option<String> option = Option.none();
        assertEquals("Foo", option.unwrapOr("Foo"));
    }

    @Test
    void mapWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("FOO", option.map(String::toUpperCase).unwrap());
    }

    @Test
    void mapWithNone() {
        final Option<String> option = Option.none();
        assertTrue(option.map(String::toUpperCase).isNone());
    }

    @Test
    void mapOrWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("FOO", option.mapOr("Bar", String::toUpperCase));
    }

    @Test
    void mapOrWithNone() {
        final Option<String> option = Option.none();
        assertEquals("Foo", option.mapOr("Foo", String::toUpperCase));
    }

    @Test
    void mapOrElseWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("FOO", option.mapOrElse(() -> "Bar", String::toUpperCase));
    }

    @Test
    void mapOrElseWithNone() {
        final Option<String> option = Option.none();
        assertEquals("Foo", option.mapOrElse(() -> "Foo", String::toUpperCase));
    }

    @Test
    void okOrWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("Foo", option.okOr("Bar").unwrap());
    }

    @Test
    void okOrWithNone() {
        final Option<String> option = Option.none();
        assertEquals("Foo", option.okOr("Foo").unwrapError());
    }

    @Test
    void okOrElseWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("Foo", option.okOrElse(() -> "Bar").unwrap());
    }

    @Test
    void okOrElseWithNone() {
        final Option<String> option = Option.none();
        assertEquals("Foo", option.okOrElse(() -> "Foo").unwrapError());
    }

    @Test
    void andThenWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("FOO", option.andThen(s -> Option.some(s.toUpperCase())).unwrap());
    }

    @Test
    void andThenWithNone() {
        final Option<String> option = Option.none();
        assertTrue(option.andThen(s -> Option.some(s.toUpperCase())).isNone());
    }

    @Test
    void andThenContinueWithSome() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Option<String> option = Option.some("Foo");

        option.andThenContinue(s -> reference.set(s.equals("Foo")));

        assertTrue(reference.get());
    }

    @Test
    void andThenContinueWithNone() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Option<String> option = Option.none();

        option.andThenContinue(s -> reference.set(s.equals("Foo")));

        assertFalse(reference.get());
    }

    @Test
    void andWithSome() {
        final Option<String> option = Option.some("Foo");
        final Option<String> comparingOption = Option.some("Bar");

        assertEquals("Bar", option.and(comparingOption).unwrap());
    }

    @Test
    void andWithNone() {
        final Option<String> option = Option.none();
        final Option<String> comparingOption = Option.some("Foo");

        assertTrue(option.and(comparingOption).isNone());
    }

    @Test
    void filterMatching() {
        final Option<String> option = Option.some("Foo");
        assertTrue(option.filter(s -> s.equals("Foo")).isSome());
    }

    @Test
    void filterNonMatch() {
        final Option<String> option = Option.some("Foo");
        assertTrue(option.filter(s -> s.equals("Bar")).isNone());
    }

    @Test
    void orWithSome() {
        final Option<String> option = Option.some("Foo");
        final Option<String> comparingOption = Option.some("Bar");

        assertEquals("Foo", option.or(comparingOption).unwrap());
    }

    @Test
    void orWithNone() {
        final Option<String> option = Option.none();
        final Option<String> comparingOption = Option.some("Foo");

        assertEquals("Foo", option.or(comparingOption).unwrap());
    }

    @Test
    void orElseWithSome() {
        final Option<String> option = Option.some("Foo");
        assertEquals("Foo", option.orElse(() -> Option.some("Bar")).unwrap());
    }

    @Test
    void orElseWithNone() {
        final Option<String> option = Option.none();
        assertEquals("Foo", option.orElse(() -> Option.some("Foo")).unwrap());
    }

    @Test
    void orElseContinueWithSome() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Option<String> option = Option.some("Foo");

        option.orElseContinue(() -> reference.set(true));

        assertFalse(reference.get());
    }

    @Test
    void orElseContinueWithNone() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Option<String> option = Option.none();

        option.orElseContinue(() -> reference.set(true));

        assertTrue(reference.get());
    }
}