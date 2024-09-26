package ru.itmo.mit.functional;

public class FunctionalTest {
    public static void main(String[] args) {
        runFunction1();
        runFunction2();
        runPredicate();
        System.out.println("-- Done --");
    }

    private static void runFunction1() {
        Function1<Integer, Integer> square = x -> x * x;
        Function1<Integer, Integer> increment = x -> x + 1;

        Function1<Integer, Integer> squareThenIncrement = square.compose(increment);
        assert squareThenIncrement.apply(3) == 16 : "Failed: squareThenIncrement.apply(3)";

        Function1<String, Integer> length = String::length;
        Function1<Integer, Boolean> isEven = x -> x % 2 == 0;
        Function1<String, Boolean> lengthIsEven = length.compose(isEven);
        assert lengthIsEven.apply("test") : "Failed: lengthIsEven.apply('test')";
    }

    private static void runFunction2() {
        Function2<Integer, Integer, Integer> add = (x, y) -> x + y;
        Function1<Integer, Integer> square = x -> x * x;

        Function2<Integer, Integer, Integer> addThenSquare = add.compose(square);
        assert addThenSquare.apply(3, 4) == 49 : "Failed: addThenSquare.apply(3, 4)";

        Function1<Integer, Integer> add7 = add.bind1(7);
        assert add7.apply(3) == 10 : "Failed: add7.apply(3)";

        Function1<Integer, Integer> add3 = add.bind2(3);
        assert add3.apply(7) == 10 : "Failed: add3.apply(7)";

        Function1<Integer, Function1<Integer, Integer>> curriedAdd = add.curry();
        assert curriedAdd.apply(3).apply(4) == 7 : "Failed: curriedAdd.apply(3).apply(4)";
    }

    private static void runPredicate() {
        Predicate<Integer> isEven = x -> x % 2 == 0;
        Predicate<Integer> isOdd = isEven.not();

        assert isOdd.apply(3) : "Failed: isOdd.apply(3)";
        assert !isOdd.apply(4) : "Failed: isOdd.apply(4)";

        Predicate<Integer> isPositive = x -> x > 0;
        Predicate<Integer> isPositiveOrEven = isPositive.or(isEven);

        assert isPositiveOrEven.apply(2) : "Failed: isPositiveOrEven.apply(2)";
        assert isPositiveOrEven.apply(-2) : "Failed: isPositiveOrEven.apply(-2)";
        assert !isPositiveOrEven.apply(-3) : "Failed: isPositiveOrEven.apply(-3)";

        Predicate<Integer> isPositiveAndEven = isPositive.and(isEven);

        assert isPositiveAndEven.apply(2) : "Failed: isPositiveAndEven.apply(2)";
        assert !isPositiveAndEven.apply(3) : "Failed: isPositiveAndEven.apply(3)";
        assert !isPositiveAndEven.apply(-3) : "Failed: isPositiveAndEven.apply(-3)";
    }
}

