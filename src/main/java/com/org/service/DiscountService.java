package com.org.service;

import com.org.service.discount.DiscountStrategy;

// GoF: Context class for the Strategy pattern.
public class DiscountService extends AbstractService {

    private DiscountStrategy strategy;

    public DiscountService(DiscountStrategy strategy) {
        validateNonNull(strategy, "DiscountStrategy");
        this.strategy = strategy;
    }

    public void setStrategy(DiscountStrategy strategy) {
        validateNonNull(strategy, "DiscountStrategy");
        this.strategy = strategy;
    }

    /** Calculates final price. */
    public double calculateFinalPrice(double originalPrice) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative, got: " + originalPrice);
        }
        return strategy.apply(originalPrice);
    }

    /** Calculates savings. */
    public double calculateSavings(double originalPrice) {
        return originalPrice - calculateFinalPrice(originalPrice);
    }

    // Boundary: savings >= threshold (not just >)
    public boolean isGoodDeal(double originalPrice, double threshold) {
        return calculateSavings(originalPrice) >= threshold;
    }
}
