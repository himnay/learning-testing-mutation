package com.org.service.discount;

// GoF: Factory Method — centralises creation of DiscountStrategy implementations.
public class DiscountStrategyFactory {

    public enum Type { PERCENTAGE, FLAT, NONE }

    /** Creates. */
    public static DiscountStrategy create(Type type, double value) {
        return switch (type) {
            case PERCENTAGE -> new PercentageDiscount(value);
            case FLAT       -> new FlatDiscount(value);
            case NONE       -> new NoDiscount();
        };
    }
}
