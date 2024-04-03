package model;

public class Option extends Product {
    private String underlier;
    private OptionType type;
    private int strike;
    private double maturity;

    public Option(String symbol, String underlier, OptionType type, int strike, double maturity) {
        super(symbol);
        this.underlier = underlier;
        this.type = type;
        this.strike = strike;
        this.maturity = maturity;
    }

    public String getUnderlier() {
        return underlier;
    }

    public OptionType getType() {
        return type;
    }

    public int getStrike() {
        return strike;
    }

    public double getMaturity() {
        return maturity;
    }

    @Override
    public String toString() {
        return "Option(" + getSymbol() + ")";
    }
}
