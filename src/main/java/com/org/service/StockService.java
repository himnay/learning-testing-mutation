package com.org.service;

// GoF: Template Method — inherits validation guards from AbstractService.
public class StockService extends AbstractService {

    private int qtyOnHand;

    public StockService(int qtyOnHand) {
        validateNonNegative(qtyOnHand, "Initial quantity");
        this.qtyOnHand = qtyOnHand;
    }

    /** Adds. */
    public int add(int qty) {
        validateNonNegative(qty, "Quantity");
        qtyOnHand = qtyOnHand + qty;
        return qtyOnHand;
    }

    /** Returns the deduct. */
    public int deduct(int qty) {
        validateNonNegative(qty, "Quantity");
        int newQty = qtyOnHand - qty;
        if (newQty < 0) {
            throw new IllegalArgumentException(
                "Insufficient stock. Available: " + qtyOnHand + ", Requested: " + qty);
        }
        qtyOnHand = newQty;
        return qtyOnHand;
    }

    public int getQuantityOnHand() {
        return qtyOnHand;
    }

    public boolean isEmpty() {
        return qtyOnHand == 0;
    }

    // Boundary: >= means exactly-equal quantity is sufficient
    /** Returns whether enough. */
    public boolean hasEnough(int qty) {
        validateNonNegative(qty, "Quantity");
        return qtyOnHand >= qty;
    }
}
