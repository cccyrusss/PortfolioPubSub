package model;

public class Stock extends Product {
    private double expectedReturn;
    private double volatility;

    public Stock(String symbol, double price, double expectedReturn, double volatility) {
        super(symbol, price);
        if (price < 0) {
            throw new RuntimeException("Stock price cannot be less than 0");
        }
        if (expectedReturn < 0 || expectedReturn > 1) {
            throw new RuntimeException("Expected return has to be between 0 to 1");
        }
        if (volatility < 0 || volatility > 1) {
            throw new RuntimeException("Volatility has to be between 0 to 1");
        }
        this.expectedReturn = expectedReturn;
        this.volatility = volatility;
    }

    public double getExpectedReturn() {
        return expectedReturn;
    }

    public double getVolatility() {
        return volatility;
    }

    public void priceMove(double priceMove) {
        setPrice(Math.max(getPrice() + priceMove, 0));
    }

    @Override
    public String toString() {
        return "Stock(" + getSymbol() + ")";
    }
}
