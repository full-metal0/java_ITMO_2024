package ru.itmo.mit.functional;

@FunctionalInterface
interface Function2<T, U, R> {
    R apply(T firstInput, U secondInput);

    default <V> Function2<T, U, V> compose(Function1<? super R, ? extends V> afterFunction) {
        return (T firstInput, U secondInput) -> afterFunction.apply(apply(firstInput, secondInput));
    }

    default Function1<U, R> bind1(T firstInput) {
        return (U secondInput) -> apply(firstInput, secondInput);
    }

    default Function1<T, R> bind2(U secondInput) {
        return (T firstInput) -> apply(firstInput, secondInput);
    }

    default Function1<T, Function1<U, R>> curry() {
        return (T firstInput) -> (U secondInput) -> apply(firstInput, secondInput);
    }
}

