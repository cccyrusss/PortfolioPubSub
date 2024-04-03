package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    public void createValidStock() {
        new Stock("test", 1, 1, 1);
    }

    @Test
    public void createStockCostLessThanZero() {
        assertThrows(RuntimeException.class, () -> new Stock("invalid", -0.01, 1, 1));
    }

    @Test
    public void createStockWithInvalidExpectedReturn() {
        assertThrows(RuntimeException.class, () -> new Stock("invalid", 1, -0.01, 0.3));
        assertThrows(RuntimeException.class, () -> new Stock("invalid", 1, 1.01, 0.3));
    }

    @Test
    public void createStockWithInvalidVolatility() {
        assertThrows(RuntimeException.class, () -> new Stock("invalid", 1, 1, -0.01));
        assertThrows(RuntimeException.class, () -> new Stock("invalid", 1, 1, 1.01));
    }

    @Test
    public void regularPriceMove() {
        Stock stock = new Stock("test", 1, 0.2, 0.3);
        stock.priceMove(0.01);
        assertEquals(1.01, stock.getPrice());
    }

    @Test
    public void priceMoveNotBelowZero() {
        Stock stock = new Stock("test", 0, 0.2, 0.3);
        stock.priceMove(-0.01);
        assertEquals(0, stock.getPrice());
    }
}