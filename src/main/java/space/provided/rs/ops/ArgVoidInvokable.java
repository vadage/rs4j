package space.provided.rs.ops;

@FunctionalInterface
public interface ArgVoidInvokable<Value> {

    void invoke(Value value);
}
