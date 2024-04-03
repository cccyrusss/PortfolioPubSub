package model;

public class Product {
    private String symbol;
    private double price;

    public Product(String symbol) {
        this.symbol = symbol;
    }

    public Product(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
