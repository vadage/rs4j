package space.provided.rs.ops;

@FunctionalInterface
public interface PlainInvokable<ReturnType> {

    ReturnType invoke();
}
