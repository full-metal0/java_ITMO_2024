package ru.itmo.mit.calculator;

public class JustCalculator implements Calculator {

    @Override
    public int sum(int x, int y) {
        return x + y;
    }

    @Override
    public int sub(int x, int y) {
        return x - y;
    }

    @Override
    public int mul(int x, int y) {
        return x * y;
    }

    @Override
    public double div(int x, int y) {
        if (y == 0) {
            throw new ArithmeticException("Делить на ноль нельзя...");
        }

        return (double) x / y;
    }
}

