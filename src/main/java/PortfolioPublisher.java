import model.Option;
import model.Portfolio;
import model.Position;
import model.Product;
import model.Stock;
import util.Calculator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Publisher thread that publishes stock prices which follow discrete time Brownian motion, each stock price is published randomly between 0.5 - 2.0 seconds
 */
public class PortfolioPublisher extends Thread {
    // Time interval in ms
    private final int MIN_TIME_INTERVAL = 500;
    private final int MAX_TIME_INTERVAL = 2000;
    private final Random TIME_INTERVAL_GENERATOR = new Random(1);

    private final Portfolio portfolio;
    private final Map<String, Product> securitiesInfo;
    private final Map<Stock, List<Option>> options;
    private final List<Stock> updatedStocks;
    private final Queue<AbstractMap.SimpleEntry<Stock, Long>> scheduledStocks;
    private long lastPublishTime;

    public PortfolioPublisher(Portfolio portfolio, Map<String, Product> securitiesInfo, List<Stock> updatedStocks) {
        this.portfolio = portfolio;
        this.securitiesInfo = securitiesInfo;
        this.updatedStocks = updatedStocks;
        this.options = new HashMap<>();
        this.scheduledStocks = new PriorityQueue<>(Comparator.comparingLong(AbstractMap.SimpleEntry::getValue));
        this.lastPublishTime = 0;
        init();
    }

    @Override
    public void run() {
        // Publishes initial prices
        synchronized (portfolio) {
            publish(scheduledStocks.stream().map(AbstractMap.SimpleEntry::getKey).collect(Collectors.toList()));
        }
        while (!scheduledStocks.isEmpty()) {
            try {
                AbstractMap.SimpleEntry<Stock, Long> entry = scheduledStocks.poll();
                Stock stock = entry.getKey();
                long timeInterval = entry.getValue() - lastPublishTime;
                double stockMove = Calculator.calculateStockMove(stock, timeInterval);
//                System.out.println("Publisher - Sleeping for " + timeInterval + " ms");
                Thread.sleep(timeInterval);
                synchronized (portfolio) {
                    stock.priceMove(stockMove);
//                    System.out.println("Publisher - " + stock + " price changed to " + stock.getPrice());
                    for (Option option : options.get(stock)) {
                        option.setPrice(Calculator.priceOption(option, stock));
//                        System.out.println("Publisher - Associated option " + option + " price changed to " + option.getPrice());
                    }
                    publish(Collections.singletonList(stock));
                }
                lastPublishTime = entry.getValue();
                schedule(stock);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private int getRandomTimeInterval() {
        return TIME_INTERVAL_GENERATOR.nextInt(MAX_TIME_INTERVAL - MIN_TIME_INTERVAL) + MIN_TIME_INTERVAL;
    }

    // Initialises option prices and schedule stock price change
    private void init() {
        for (Position position : portfolio.getPositions()) {
            if (position.getProduct() instanceof Stock) {
                Stock stock = (Stock) position.getProduct();
                schedule(stock);
            } else {
                Option option = (Option) position.getProduct();
                Stock underlier = (Stock) securitiesInfo.get(option.getUnderlier());
                options.putIfAbsent(underlier, new ArrayList<>());
                options.get(underlier).add(option);
                option.setPrice(Calculator.priceOption(option, underlier));
            }
        }
    }

    // Schedule next stock price change with PriorityQueue, worst case time complexity: O(nlog(n))
    private void schedule(Stock stock) {
        scheduledStocks.add(new AbstractMap.SimpleEntry<>(stock, getRandomTimeInterval() + lastPublishTime));
    }

    private void publish(List<Stock> stocks) {
        portfolio.update();
        portfolio.notify();
        updatedStocks.addAll(stocks);
    }
}
