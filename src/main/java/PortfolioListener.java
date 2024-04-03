import model.Portfolio;
import model.Position;
import model.Product;
import model.Stock;

import java.util.List;

/**
 * Listener thread which listens to portfolio publisher in real time
 */
public class PortfolioListener extends Thread {
    private final String HEADER_FORMAT = "%-30s%15s%15s%15s\n";
    private final String DATA_FORMAT = "%-30s%15.2f%15s%15.2f\n";
    private final String NAV_FORMAT = "\n#Total Portfolio%59.2f\n";

    private final Portfolio portfolio;
    private final List<Stock> updatedStocks;
    private int counter;

    public PortfolioListener(Portfolio portfolio, List<Stock> updatedStocks) {
        this.portfolio = portfolio;
        this.updatedStocks = updatedStocks;
        this.counter = 0;
    }

    @Override
    public void run() {
            try {
                while (true) {
                    synchronized (portfolio) {
                        if (updatedStocks.isEmpty()) {
                            portfolio.wait();
                        }
                        printUpdate();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private void printUpdate() {
        System.out.printf("## %d Market Data Update\n", ++counter);
        updatedStocks.forEach(stock -> System.out.printf("%s change to %.2f\n", stock.getSymbol(), stock.getPrice()));
        updatedStocks.clear();
        prettyPrintPortfolio(portfolio);
        System.out.printf(NAV_FORMAT, portfolio.getNav());
        System.out.println("---------------------------------------------------------------------------");
    }

    private void prettyPrintPortfolio(Portfolio portfolio) {
        System.out.format(HEADER_FORMAT, "symbol", "price", "qty", "value");
        for (Position position : portfolio.getPositions()) {
            Product product = position.getProduct();
            System.out.format(DATA_FORMAT, product.getSymbol(), product.getPrice(), position.getQuantity(), position.getMarketValue());
        }
    }
}
