package ru.itmo.mit.functional;

@FunctionalInterface
interface Function1<T, R> {
    R apply(T input);

    default <V> Function1<T, V> compose(Function1<? super R, ? extends V> afterFunction) {
        return (T input) -> afterFunction.apply(apply(input));
    }
}

