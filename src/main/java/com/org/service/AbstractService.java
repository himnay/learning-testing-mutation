package com.org.service;

// GoF: Template Method — defines guard clauses reused across all concrete services.
public abstract class AbstractService {

    protected void validateNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException(field + " must not be negative, got: " + value);
        }
    }

    protected void validatePositive(double value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive, got: " + value);
        }
    }

    protected void validateNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
    }
}
