package com.org.service;

import com.org.service.discount.DiscountStrategyFactory;
import com.org.service.discount.FlatDiscount;
import com.org.service.discount.NoDiscount;
import com.org.service.discount.PercentageDiscount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiscountService (Strategy pattern)")
class TestDiscountService {

    // -------------------------------------------------------------------------
    // PercentageDiscount strategy
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("PercentageDiscount")
    class PercentageDiscountTests {

        @ParameterizedTest(name = "{0}% off £{1} = £{2}")
        @CsvSource({
            "0,   100.0, 100.0",    // 0% — kills MATH mutant (100 * 1 - 0/100)
            "10,  100.0,  90.0",
            "25,  200.0, 150.0",
            "50,  80.0,   40.0",
            "100, 50.0,    0.0"     // 100% off = zero
        })
        void finalPrice(double pct, double price, double expected) {
            DiscountService svc = new DiscountService(new PercentageDiscount(pct));
            assertEquals(expected, svc.calculateFinalPrice(price), 0.001);
        }

        @ParameterizedTest(name = "{0}% off £{1} saves £{2}")
        @CsvSource({"10, 100.0, 10.0", "25, 200.0, 50.0", "0, 100.0, 0.0"})
        void savings(double pct, double price, double expectedSaving) {
            DiscountService svc = new DiscountService(new PercentageDiscount(pct));
            assertEquals(expectedSaving, svc.calculateSavings(price), 0.001);
        }

        @Test
        @DisplayName("percentage < 0 throws")
        void negativePercentageThrows() {
            assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(-1));
        }

        @Test
        @DisplayName("percentage > 100 throws")
        void over100Throws() {
            assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(101));
        }

        @Test
        @DisplayName("getPercentage returns configured value — kills RETURN_VALUES mutant")
        void getPercentage() {
            PercentageDiscount d = new PercentageDiscount(30);
            assertEquals(30.0, d.getPercentage(), 0.001);
        }
    }

    // -------------------------------------------------------------------------
    // FlatDiscount strategy
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("FlatDiscount")
    class FlatDiscountTests {

        @ParameterizedTest(name = "price={1} flat off={0} expected={2}")
        @CsvSource({
            "10.0, 50.0, 40.0",
            "50.0, 50.0,  0.0",    // exactly equal — boundary
            "60.0, 50.0,  0.0",    // discount exceeds price — floor at 0
            "0.0,  50.0, 50.0"     // no discount
        })
        void finalPrice(double discountAmt, double price, double expected) {
            DiscountService svc = new DiscountService(new FlatDiscount(discountAmt));
            assertEquals(expected, svc.calculateFinalPrice(price), 0.001);
        }

        @Test
        @DisplayName("negative flat discount throws")
        void negativeFlatThrows() {
            assertThrows(IllegalArgumentException.class, () -> new FlatDiscount(-1));
        }

        @Test
        @DisplayName("getDiscountAmount returns configured value — kills RETURN_VALUES mutant")
        void getDiscountAmount() {
            FlatDiscount d = new FlatDiscount(25.0);
            assertEquals(25.0, d.getDiscountAmount(), 0.001);
        }
    }

    // -------------------------------------------------------------------------
    // NoDiscount (Null Object)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("NoDiscount (Null Object)")
    class NoDiscountTests {

        @Test
        @DisplayName("price is unchanged — kills RETURN_VALUES mutant")
        void priceUnchanged() {
            DiscountService svc = new DiscountService(new NoDiscount());
            assertEquals(99.99, svc.calculateFinalPrice(99.99), 0.001);
        }

        @Test
        @DisplayName("savings are zero")
        void savingsZero() {
            DiscountService svc = new DiscountService(new NoDiscount());
            assertEquals(0.0, svc.calculateSavings(99.99), 0.001);
        }
    }

    // -------------------------------------------------------------------------
    // isGoodDeal — boundary: >= threshold (not just >)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("isGoodDeal")
    class IsGoodDeal {

        @Test
        @DisplayName("true when savings exactly equal threshold — kills >= vs > mutant")
        void trueWhenSavingsEqualThreshold() {
            DiscountService svc = new DiscountService(new FlatDiscount(10));
            assertTrue(svc.isGoodDeal(100.0, 10.0));
        }

        @Test
        @DisplayName("true when savings exceed threshold")
        void trueWhenSavingsExceedThreshold() {
            DiscountService svc = new DiscountService(new FlatDiscount(20));
            assertTrue(svc.isGoodDeal(100.0, 10.0));
        }

        @Test
        @DisplayName("false when savings fall short of threshold")
        void falseWhenSavingsBelowThreshold() {
            DiscountService svc = new DiscountService(new FlatDiscount(5));
            assertFalse(svc.isGoodDeal(100.0, 10.0));
        }
    }

    // -------------------------------------------------------------------------
    // Strategy swap at runtime
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("setStrategy (runtime swap)")
    class SetStrategy {

        @Test
        @DisplayName("swapping strategy changes behaviour")
        void swapStrategy() {
            DiscountService svc = new DiscountService(new NoDiscount());
            assertEquals(100.0, svc.calculateFinalPrice(100.0), 0.001);

            svc.setStrategy(new PercentageDiscount(20));
            assertEquals(80.0, svc.calculateFinalPrice(100.0), 0.001);
        }

        @Test
        @DisplayName("null strategy throws")
        void nullStrategyThrows() {
            DiscountService svc = new DiscountService(new NoDiscount());
            assertThrows(IllegalArgumentException.class, () -> svc.setStrategy(null));
        }
    }

    // -------------------------------------------------------------------------
    // DiscountStrategyFactory (Factory Method)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("DiscountStrategyFactory")
    class FactoryTests {

        @Test
        @DisplayName("PERCENTAGE type produces PercentageDiscount")
        void createsPercentageDiscount() {
            var strategy = DiscountStrategyFactory.create(DiscountStrategyFactory.Type.PERCENTAGE, 15);
            DiscountService svc = new DiscountService(strategy);
            assertEquals(85.0, svc.calculateFinalPrice(100.0), 0.001);
        }

        @Test
        @DisplayName("FLAT type produces FlatDiscount")
        void createsFlatDiscount() {
            var strategy = DiscountStrategyFactory.create(DiscountStrategyFactory.Type.FLAT, 10);
            DiscountService svc = new DiscountService(strategy);
            assertEquals(90.0, svc.calculateFinalPrice(100.0), 0.001);
        }

        @Test
        @DisplayName("NONE type produces NoDiscount")
        void createsNoDiscount() {
            var strategy = DiscountStrategyFactory.create(DiscountStrategyFactory.Type.NONE, 0);
            DiscountService svc = new DiscountService(strategy);
            assertEquals(100.0, svc.calculateFinalPrice(100.0), 0.001);
        }
    }

    // -------------------------------------------------------------------------
    // Guard clauses on DiscountService itself
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("null strategy in constructor throws")
    void nullStrategyInConstructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new DiscountService(null));
    }

    @Test
    @DisplayName("negative price throws")
    void negativePriceThrows() {
        DiscountService svc = new DiscountService(new NoDiscount());
        assertThrows(IllegalArgumentException.class, () -> svc.calculateFinalPrice(-1));
    }
}
