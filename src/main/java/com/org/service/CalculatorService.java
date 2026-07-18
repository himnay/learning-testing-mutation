package com.org.service;

public class CalculatorService {

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

    /** Returns the factorial. */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
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
        for (int i = 3; i * i <= n; i += 2) {
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
