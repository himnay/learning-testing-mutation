package com.org.service.discount;

public class FlatDiscount implements DiscountStrategy {

    private final double discountAmount;

    public FlatDiscount(double discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException(
                "Discount amount cannot be negative, got: " + discountAmount);
        }
        this.discountAmount = discountAmount;
    }

    // Never returns negative — floors at zero
    @Override
    public double apply(double originalPrice) {
        double discounted = originalPrice - discountAmount;
        return discounted < 0 ? 0 : discounted;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}
