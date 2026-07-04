package com.org.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StockService")
class TestStockService {

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("constructor")
    class Constructor {

        @Test
        @DisplayName("accepts zero initial quantity")
        void acceptsZero() {
            StockService s = new StockService(0);
            assertAll(
                () -> assertEquals(0, s.getQuantityOnHand()),
                () -> assertTrue(s.isEmpty())
            );
        }

        @Test
        @DisplayName("accepts positive initial quantity")
        void acceptsPositive() {
            StockService s = new StockService(100);
            assertAll(
                () -> assertEquals(100, s.getQuantityOnHand()),
                () -> assertFalse(s.isEmpty())
            );
        }

        @Test
        @DisplayName("throws for negative initial quantity — kills CONDITIONALS_BOUNDARY mutant")
        void rejectsNegative() {
            assertThrows(IllegalArgumentException.class, () -> new StockService(-1));
        }
    }

    // -------------------------------------------------------------------------
    // add — verifies both return value AND persisted state (kills VOID_METHOD mutants)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("add")
    class Add {

        @ParameterizedTest(name = "start={0}, add={1}, result={2}")
        @DisplayName("Adding a quantity increases both the returned total and the stored quantity on hand")
        @CsvSource({"100, 10, 110", "0, 0, 0", "50, 50, 100", "0, 1, 1"})
        void add(int initial, int qty, int expected) {
            StockService s = new StockService(initial);
            int returned = s.add(qty);
            assertAll(
                () -> assertEquals(expected, returned),
                () -> assertEquals(expected, s.getQuantityOnHand())
            );
        }

        @Test
        @DisplayName("rejects negative qty — state must not change")
        void rejectsNegativeQty() {
            StockService s = new StockService(100);
            assertThrows(IllegalArgumentException.class, () -> s.add(-1));
            assertEquals(100, s.getQuantityOnHand());   // kills VOID_METHOD mutant on validateNonNegative
        }
    }

    // -------------------------------------------------------------------------
    // deduct
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("deduct")
    class Deduct {

        @ParameterizedTest(name = "start={0}, deduct={1}, result={2}")
        @DisplayName("Deducting a quantity decreases both the returned total and the stored quantity on hand")
        @CsvSource({"100, 10, 90", "100, 100, 0", "100, 0, 100", "1, 1, 0"})
        void deduct(int initial, int qty, int expected) {
            StockService s = new StockService(initial);
            int returned = s.deduct(qty);
            assertAll(
                () -> assertEquals(expected, returned),
                () -> assertEquals(expected, s.getQuantityOnHand())
            );
        }

        @Test
        @DisplayName("throws when deducting more than available — state must not change")
        void deductMoreThanAvailableThrows() {
            StockService s = new StockService(50);
            assertThrows(IllegalArgumentException.class, () -> s.deduct(51));
            assertEquals(50, s.getQuantityOnHand());    // verifies state was not mutated
        }

        @Test
        @DisplayName("deduct exactly zero — boundary case")
        void deductZero() {
            StockService s = new StockService(50);
            assertEquals(50, s.deduct(0));
        }

        @Test
        @DisplayName("rejects negative qty")
        void rejectsNegativeQty() {
            StockService s = new StockService(100);
            assertThrows(IllegalArgumentException.class, () -> s.deduct(-1));
        }
    }

    // -------------------------------------------------------------------------
    // isEmpty / hasEnough — kill FALSE_RETURNS and TRUE_RETURNS mutants
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("isEmpty")
    class IsEmpty {

        @Test
        @DisplayName("Stock with zero quantity reports as empty")
        void trueWhenZero() {
            assertTrue(new StockService(0).isEmpty());
        }

        @Test
        @DisplayName("Stock with at least one unit reports as not empty")
        void falseWhenOne() {
            assertFalse(new StockService(1).isEmpty());
        }

        @Test
        @DisplayName("becomes empty after deducting all stock")
        void becomesEmptyAfterFullDeduct() {
            StockService s = new StockService(10);
            s.deduct(10);
            assertTrue(s.isEmpty());
        }
    }

    @Nested
    @DisplayName("hasEnough")
    class HasEnough {

        @ParameterizedTest(name = "qty={0}, requested={1}, hasEnough={2}")
        @DisplayName("Reports whether stock on hand meets the requested quantity, across equal/over/under boundary cases")
        @CsvSource({
            "10, 10, true",    // exactly equal — kills >= vs > boundary mutant
            "10, 11, false",   // one over available
            "10,  0, true",    // zero request
            " 0,  1, false",   // empty stock
            "10,  9, true"     // strictly within
        })
        void hasEnough(int initial, int requested, boolean expected) {
            StockService s = new StockService(initial);
            assertEquals(expected, s.hasEnough(requested));
        }

        @Test
        @DisplayName("rejects negative request")
        void rejectsNegative() {
            assertThrows(IllegalArgumentException.class, () -> new StockService(10).hasEnough(-1));
        }
    }
}
