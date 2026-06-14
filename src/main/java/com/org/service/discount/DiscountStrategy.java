package com.org.service.discount;

// GoF: Strategy — defines the contract for interchangeable discount algorithms.
@FunctionalInterface
public interface DiscountStrategy {
    double apply(double originalPrice);
}
