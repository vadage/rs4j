package space.provided.rs.result;

import org.junit.jupiter.api.Test;
import space.provided.rs.error.ValueAccessError;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void unwrapWithOk() {
        final Result<String, ?> result = Result.ok("Foo");
        assertEquals("Foo", result.unwrap());
    }

    @Test
    void unwrapWithError() {
        final Result<?, String> result = Result.error("Foo");
        assertThrowsExactly(ValueAccessError.class, result::unwrap);
    }

    @Test
    void unwrapErrorWithOk() {
        final Result<?, String> result = Result.ok("Foo");
        assertThrowsExactly(ValueAccessError.class, result::unwrapError);
    }

    @Test
    void unwrapErrorWithError() {
        final Result<String, ?> result = Result.error("Foo");
        assertEquals("Foo", result.unwrapError());
    }

    @Test
    void isOkWithOk() {
        final Result<?, ?> result = Result.ok();
        assertTrue(result.isOk());
    }

    @Test
    void isOkWithError() {
        final Result<?, ?> result = Result.error();
        assertFalse(result.isOk());
    }

    @Test
    void isOkAndWithOk() {
        final Result<?, ?> result = Result.ok();
        assertFalse(result.isOkAnd(o -> false));
    }

    @Test
    void isOkAndWithError() {
        final Result<?, ?> result = Result.error();
        assertFalse(result.isOkAnd(o -> true));
    }

    @Test
    void isErrorWithOk() {
        final Result<?, ?> result = Result.ok();
        assertFalse(result.isError());
    }

    @Test
    void isErrorWithError() {
        final Result<?, ?> result = Result.error();
        assertTrue(result.isError());
    }

    @Test
    void isErrorAndWithOk() {
        final Result<?, ?> result = Result.ok();
        assertFalse(result.isErrorAnd(o -> true));
    }

    @Test
    void isErrorAndWithError() {
        final Result<?, ?> result = Result.error();
        assertFalse(result.isErrorAnd(o -> false));
    }

    @Test
    void mapWithOk() {
        final Result<String, ?> result = Result.ok("Foo");
        assertEquals("FOO", result.map(String::toUpperCase).unwrap());
    }

    @Test
    void mapWithError() {
        final Result<String, ?> result = Result.error();
        assertTrue(result.map(String::toUpperCase).isError());
    }

    @Test
    void mapOrWithOk() {
        final Result<String, ?> result = Result.ok("Foo");
        assertEquals("FOO", result.mapOr("Bar", String::toUpperCase));
    }

    @Test
    void mapOrWithError() {
        final Result<String, ?> result = Result.error("Foo");
        assertEquals("Bar", result.mapOr("Bar", String::toUpperCase));
    }

    @Test
    void mapOrElseWithOk() {
        final Result<String, String> result = Result.ok("Foo");
        assertEquals("FOO", result.mapOrElse(s -> "Bar", String::toUpperCase));
    }

    @Test
    void mapOrElseWithError() {
        final Result<String, String> result = Result.error("Foo");
        assertEquals("Bar", result.mapOrElse(s -> "Bar", String::toUpperCase));
    }

    @Test
    void andWithOk() {
        final Result<String, String> result = Result.ok("Foo");
        final Result<String, String> comparingResult = Result.ok("Bar");

        assertEquals("Bar", result.and(comparingResult).unwrap());
    }

    @Test
    void andWithError() {
        final Result<String, String> result = Result.error("Foo");
        final Result<String, String> comparingResult = Result.ok("Bar");

        assertTrue(result.and(comparingResult).isError());
    }

    @Test
    void andThenWithOk() {
        final Result<String, String> result = Result.ok("Foo");
        assertEquals("FOO", result.andThen(s -> Result.ok(s.toUpperCase())).unwrap());
    }

    @Test
    void andThenWithError() {
        final Result<String, String> result = Result.error("Foo");
        assertTrue(result.andThen(s -> Result.ok(s.toUpperCase())).isError());
    }

    @Test
    void andThenContinueWithOk() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Result<String, String> result = Result.ok("Foo");

        result.andThenContinue(s -> reference.set(s.equals("Foo")));

        assertTrue(reference.get());
    }

    @Test
    void andThenContinueWithError() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Result<String, String> result = Result.error("Foo");

        result.andThenContinue(s -> reference.set(s.equals("Foo")));

        assertFalse(reference.get());
    }

    @Test
    void orWithOk() {
        final Result<String, String> result = Result.ok("Foo");
        final Result<String, String> comparingResult = Result.ok("Bar");

        assertEquals("Foo", result.or(comparingResult).unwrap());
    }

    @Test
    void orWithError() {
        final Result<String, String> result = Result.error("Foo");
        final Result<String, String> comparingResult = Result.ok("Bar");

        assertEquals("Bar", result.or(comparingResult).unwrap());
    }

    @Test
    void orElseWithOk() {
        final Result<String, String> result = Result.ok("Foo");
        assertEquals("Foo", result.orElse(s -> Result.ok("Bar")).unwrap());
    }

    @Test
    void orElseWithError() {
        final Result<String, String> result = Result.error("Foo");
        assertEquals("Bar", result.orElse(s -> Result.ok("Bar")).unwrap());
    }

    @Test
    void orElseContinueWithOk() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Result<String, String> result = Result.ok("Foo");

        result.orElseContinue(s -> reference.set(s.equals("Foo")));

        assertFalse(reference.get());
    }

    @Test
    void orElseContinueWithError() {
        final AtomicBoolean reference = new AtomicBoolean(false);
        final Result<String, String> result = Result.error("Foo");

        result.orElseContinue(s -> reference.set(s.equals("Foo")));

        assertTrue(reference.get());
    }

    @Test
    void unwrapOrElseWithOk() {
        final Result<String, String> result = Result.ok("Foo");
        assertEquals("Foo", result.unwrapOrElse(s -> "Bar"));
    }

    @Test
    void unwrapOrElseWithError() {
        final Result<String, String> result = Result.error("Foo");
        assertEquals("Bar", result.unwrapOrElse(s -> "Bar"));
    }
}