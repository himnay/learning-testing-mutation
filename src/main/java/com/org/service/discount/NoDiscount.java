package com.org.service.discount;

// GoF: Null Object — a no-op strategy that avoids null checks in DiscountService.
public class NoDiscount implements DiscountStrategy {

    @Override
    public double apply(double originalPrice) {
        return originalPrice;
    }
}
