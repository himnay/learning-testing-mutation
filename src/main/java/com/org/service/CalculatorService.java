package com.org.service;

public class CalculatorService {

    /** Largest n whose factorial fits in a long: 20! = 2432902008176640000. */
    static final int MAX_LONG_FACTORIAL = 20;

    /** Adds. */
    public int add(int a, int b) {
        return a + b;
    }

    /** Returns the subtract. */
    public int subtract(int a, int b) {
        return a - b;
    }

    /** Returns the multiply. */
    public int multiply(int a, int b) {
        return a * b;
    }

    /** Returns the divide. */
    public double divide(double numerator, double denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return numerator / denominator;
    }

    // Boundary: 0 is NOT positive — critical for killing CONDITIONALS_BOUNDARY mutant
    public boolean isPositive(int number) {
        return number > 0;
    }

    public boolean isEven(int number) {
        return number % 2 == 0;
    }

    /** Returns the max. */
    public int max(int a, int b) {
        return a >= b ? a : b;
    }

    /** Returns the min. */
    public int min(int a, int b) {
        return a <= b ? a : b;
    }

    // Clamps value to [min, max] — exercises two CONDITIONALS_BOUNDARY mutants
    /** Returns the clamp. */
    public int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Returns n! for 0 <= n <= 20.
     *
     * @throws ArithmeticException for n > 20: 21! no longer fits in a long, and the
     *         multiplication would silently overflow into a wrong (even negative) result
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
        }
        if (n > MAX_LONG_FACTORIAL) {
            throw new ArithmeticException(n + "! overflows a long (max is " + MAX_LONG_FACTORIAL + "!)");
        }
        if (n == 0 || n == 1) {
            return 1L;
        }
        return n * factorial(n - 1);
    }

    public boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        // i <= n / i, not i * i <= n: i * i overflows int for i > 46340, which made
        // isPrime(Integer.MAX_VALUE) loop ~1e9 times and then report the prime 2^31-1 as composite
        for (int i = 3; i <= n / i; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    /** Returns the percentage. */
    public double percentage(double part, double total) {
        if (total == 0) {
            throw new ArithmeticException("Total must not be zero");
        }
        return (part / total) * 100;
    }
}
