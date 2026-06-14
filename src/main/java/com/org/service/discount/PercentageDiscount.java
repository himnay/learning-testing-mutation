package com.org.service.discount;

public class PercentageDiscount implements DiscountStrategy {

    private final double percentage;

    public PercentageDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException(
                "Percentage must be between 0 and 100, got: " + percentage);
        }
        this.percentage = percentage;
    }

    @Override
    public double apply(double originalPrice) {
        return originalPrice * (1 - percentage / 100);
    }

    public double getPercentage() {
        return percentage;
    }
}
