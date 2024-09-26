package ru.itmo.mit.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    Calculator calculator = new JustCalculator();

    @Test
    public void testSum() {
        assertEquals(6, calculator.sum(3, 3));
        assertEquals(100, calculator.sum(100, 0));
    }

    @Test
    public void testSub() {
        assertEquals(0, calculator.sub(2, 2));
        assertEquals(100, calculator.sub(100, 0));
    }

    @Test
    public void testMul() {
        assertEquals(9, calculator.mul(3, 3));
        assertEquals(0, calculator.mul(100, 0));
    }

    @Test
    public void testDiv() {
        assertEquals(-5.0, calculator.div(-10, 2), 0.0001);

        Exception exception = assertThrows(ArithmeticException.class, () -> {
            calculator.div(10, 0);
        });

        assertEquals("Делить на ноль нельзя...", exception.getMessage());
    }
}
