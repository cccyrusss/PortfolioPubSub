package util;

import model.Option;
import model.OptionType;
import model.Stock;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

/**
 * Calculator class that calculates a stock price move, option (call, put) prices
 */
public class Calculator {
    private final static Random GAUSSIAN_GENERATOR = new Random(1);
    private final static double RISK_FREE_INTEREST_RATE = 0.02;
    private final static NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution();

    public static double calculateStockMove(Stock stock, double timeInterval) {
        return stock.getPrice() * (stock.getExpectedReturn() * timeInterval / 7257600 + stock.getVolatility() * GAUSSIAN_GENERATOR.nextGaussian() * Math.sqrt(timeInterval / 7257600));
    }

    public static double priceOption(Option option, Stock underlier) {
        double d1 = (Math.log(underlier.getPrice() / option.getStrike()) + (RISK_FREE_INTEREST_RATE + Math.pow(underlier.getVolatility(), 2) / 2) * option.getMaturity()) / (underlier.getVolatility() * Math.sqrt(option.getMaturity()));
        double d2 = d1 - underlier.getVolatility() * Math.sqrt(option.getMaturity());
        if (option.getType() == OptionType.CALL) {
            return priceCallOption(option, underlier, d1, d2);
        }
        return pricePutOption(option, underlier, d1, d2);
    }

    private static double priceCallOption(Option option, Stock underlier, double d1, double d2) {
        return underlier.getPrice() * NORMAL_DISTRIBUTION.cumulativeProbability(d1) - option.getStrike() * Math.exp(-RISK_FREE_INTEREST_RATE * option.getMaturity()) * NORMAL_DISTRIBUTION.cumulativeProbability(d2);
    }

    private static double pricePutOption(Option option, Stock underlier, double d1, double d2) {
        return option.getStrike() * Math.exp(-RISK_FREE_INTEREST_RATE * option.getMaturity()) * NORMAL_DISTRIBUTION.cumulativeProbability(-d2) - underlier.getPrice() * NORMAL_DISTRIBUTION.cumulativeProbability(-d1);
    }
}
