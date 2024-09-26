package ru.itmo.mit.functional;

@FunctionalInterface
interface Predicate<T> extends Function1<T, Boolean> {
    default Predicate<T> or(Predicate<? super T> otherPredicate) {
        return (T input) -> apply(input) || otherPredicate.apply(input);
    }

    default Predicate<T> and(Predicate<? super T> otherPredicate) {
        return (T input) -> apply(input) && otherPredicate.apply(input);
    }

    default Predicate<T> not() {
        return (T input) -> !apply(input);
    }
}

