package space.provided.rs.ops;

@FunctionalInterface
public interface ArgInvokable<Value, ReturnType> {

    ReturnType invoke(Value value);
}
