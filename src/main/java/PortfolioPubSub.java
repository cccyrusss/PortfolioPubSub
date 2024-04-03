import model.Portfolio;
import model.Product;
import model.Stock;
import util.PortfolioParser;
import util.SecuritiesInfoParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main class to start publisher and listener threads
 */
public class PortfolioPubSub {
    private final String DB_URL = "jdbc:sqlite:db/securitiesInfo.db";
    private final String POSITIONS_DIR = "csv/positions.csv";

    public static void main(String[] args) {
        new PortfolioPubSub().start();
    }

    // Entry point
    public void start() {
        Map<String, Product> securitiesInfo = SecuritiesInfoParser.loadSecuritiesInfo(DB_URL);
        if (securitiesInfo == null) {
            System.out.println("Exiting due to null securities info...");
            return;
        }
        Portfolio portfolio = PortfolioParser.parsePositions(POSITIONS_DIR, securitiesInfo);
        if (portfolio == null) {
            System.out.println("Exiting due to null portfolio...");
            return;
        }

        // List of stocks for publisher and listener to publish and listen to stock price changes
        List<Stock> updatedStocks = new ArrayList<>();

        Thread publisher = new PortfolioPublisher(portfolio, securitiesInfo, updatedStocks);
        publisher.start();

        Thread listener = new PortfolioListener(portfolio, updatedStocks);
        listener.start();

        try {
            publisher.join();
            listener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
