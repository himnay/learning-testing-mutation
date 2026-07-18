package com.org.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CalculatorService")
class TestCalculatorService {

    private CalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new CalculatorService();
    }

    // -------------------------------------------------------------------------
    // Arithmetic
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("add")
    class Add {

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({"0, 0, 0", "1, 2, 3", "-1, 1, 0", "-3, -4, -7", "100, -100, 0"})
        @DisplayName("Adds two integers correctly, including negative and zero operands")
        void add(int a, int b, int expected) {
            assertEquals(expected, calculator.add(a, b));
        }
    }

    @Nested
    @DisplayName("subtract")
    class Subtract {

        @ParameterizedTest(name = "{0} - {1} = {2}")
        @CsvSource({"5, 3, 2", "0, 0, 0", "-1, -1, 0", "3, 5, -2", "10, 10, 0"})
        @DisplayName("Subtracts two integers correctly, including negative and zero operands")
        void subtract(int a, int b, int expected) {
            assertEquals(expected, calculator.subtract(a, b));
        }
    }

    @Nested
    @DisplayName("multiply")
    class Multiply {

        @ParameterizedTest(name = "{0} * {1} = {2}")
        @DisplayName("Multiplies two integers correctly, including negative and zero operands")
        @CsvSource({"3, 4, 12", "0, 100, 0", "100, 0, 0", "-2, 5, -10", "-3, -4, 12", "1, 99, 99"})
        void multiply(int a, int b, int expected) {
            assertEquals(expected, calculator.multiply(a, b));
        }
    }

    @Nested
    @DisplayName("divide")
    class Divide {

        @ParameterizedTest(name = "{0} / {1} = {2}")
        @DisplayName("Divides two doubles and returns the correct quotient")
        @CsvSource({"10.0, 2.0, 5.0", "7.0, 2.0, 3.5", "-9.0, 3.0, -3.0", "0.0, 5.0, 0.0", "1.0, 4.0, 0.25"})
        void divide(double a, double b, double expected) {
            assertEquals(expected, calculator.divide(a, b), 0.0001);
        }

        @Test
        @DisplayName("throws ArithmeticException on division by zero")
        void divideByZeroThrows() {
            ArithmeticException ex = assertThrows(ArithmeticException.class,
                () -> calculator.divide(10, 0));
            assertTrue(ex.getMessage().contains("zero"));
        }
    }

    // -------------------------------------------------------------------------
    // Boolean predicates — tests must cover BOTH sides of every boundary
    // to kill CONDITIONALS_BOUNDARY and NEGATE_CONDITIONALS mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("isPositive")
    class IsPositive {

        @ParameterizedTest(name = "{0} is positive")
        @ValueSource(ints = {1, 2, 100, Integer.MAX_VALUE})
        @DisplayName("Reports numbers above zero as positive")
        void trueWhenAboveZero(int n) {
            assertTrue(calculator.isPositive(n));
        }

        @Test
        @DisplayName("zero is NOT positive — kills boundary mutant (> vs >=)")
        void falseForZero() {
            assertFalse(calculator.isPositive(0));
        }

        @ParameterizedTest(name = "{0} is not positive")
        @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
        @DisplayName("Reports negative numbers as not positive")
        void falseWhenNegative(int n) {
            assertFalse(calculator.isPositive(n));
        }
    }

    @Nested
    @DisplayName("isEven")
    class IsEven {

        @ParameterizedTest(name = "{0} is even")
        @ValueSource(ints = {0, 2, -4, 100, -100})
        @DisplayName("Recognizes even numbers correctly, including negative even numbers")
        void trueForEven(int n) {
            assertTrue(calculator.isEven(n));
        }

        @ParameterizedTest(name = "{0} is odd")
        @ValueSource(ints = {1, 3, -1, 99, -99})
        @DisplayName("Reports odd numbers as not even")
        void falseForOdd(int n) {
            assertFalse(calculator.isEven(n));
        }
    }

    // -------------------------------------------------------------------------
    // min / max / clamp — boundary tests kill CONDITIONALS_BOUNDARY mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("max")
    class Max {

        @ParameterizedTest(name = "max({0},{1}) = {2}")
        @CsvSource({"3, 5, 5", "5, 3, 5", "4, 4, 4", "-1, -2, -1", "0, 0, 0"})
        @DisplayName("Returns the larger of two integers, including equal and negative inputs")
        void max(int a, int b, int expected) {
            assertEquals(expected, calculator.max(a, b));
        }
    }

    @Nested
    @DisplayName("min")
    class Min {

        @ParameterizedTest(name = "min({0},{1}) = {2}")
        @CsvSource({"3, 5, 3", "5, 3, 3", "4, 4, 4", "-1, -2, -2", "0, 0, 0"})
        @DisplayName("Returns the smaller of two integers, including equal and negative inputs")
        void min(int a, int b, int expected) {
            assertEquals(expected, calculator.min(a, b));
        }
    }

    @Nested
    @DisplayName("clamp")
    class Clamp {

        @ParameterizedTest(name = "clamp({0},{1},{2}) = {3}")
        @DisplayName("Clamps a value to the given range, including cases just inside and just outside the boundary")
        @CsvSource({
            "5,  1, 10,  5",   // within range
            "0,  1, 10,  1",   // exactly one below min → clamp to min
            "1,  1, 10,  1",   // exactly at min → no clamp
            "10, 1, 10, 10",   // exactly at max → no clamp
            "11, 1, 10, 10",   // exactly one above max → clamp to max
            "-5, 0, 20,  0",   // well below min
            "25, 0, 20, 20"    // well above max
        })
        void clamp(int value, int min, int max, int expected) {
            assertEquals(expected, calculator.clamp(value, min, max));
        }
    }

    // -------------------------------------------------------------------------
    // factorial — tests kill RETURN_VALUES and INCREMENTS mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("factorial")
    class Factorial {

        @ParameterizedTest(name = "{0}! = {1}")
        @CsvSource({"0, 1", "1, 1", "2, 2", "3, 6", "5, 120", "10, 3628800"})
        @DisplayName("Computes the factorial of non-negative integers correctly")
        void factorial(int n, long expected) {
            assertEquals(expected, calculator.factorial(n));
        }

        @Test
        @DisplayName("throws for negative input")
        void negativeThrows() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.factorial(-1));
            assertTrue(ex.getMessage().contains("negative"));
        }
    }

    // -------------------------------------------------------------------------
    // isPrime — covers CONDITIONALS_BOUNDARY and NEGATE_CONDITIONALS mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("isPrime")
    class IsPrime {

        @ParameterizedTest(name = "{0} is prime")
        @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 97})
        @DisplayName("Recognizes prime numbers correctly")
        void trueForPrimes(int n) {
            assertTrue(calculator.isPrime(n));
        }

        @ParameterizedTest(name = "{0} is not prime")
        @ValueSource(ints = {0, 1, 4, 6, 8, 9, 15, 100})
        @DisplayName("Recognizes non-prime numbers correctly")
        void falseForNonPrimes(int n) {
            assertFalse(calculator.isPrime(n));
        }
    }

    // -------------------------------------------------------------------------
    // percentage — kills MATH and RETURN_VALUES mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("percentage")
    class Percentage {

        @ParameterizedTest(name = "{0} of {1} = {2}%")
        @DisplayName("Computes what percentage one value is of another")
        @CsvSource({"50.0, 200.0, 25.0", "100.0, 100.0, 100.0", "1.0, 4.0, 25.0", "0.0, 50.0, 0.0"})
        void percentage(double part, double total, double expected) {
            assertEquals(expected, calculator.percentage(part, total), 0.0001);
        }

        @Test
        @DisplayName("throws when total is zero")
        void zeroTotalThrows() {
            assertThrows(ArithmeticException.class, () -> calculator.percentage(10, 0));
        }
    }
}
