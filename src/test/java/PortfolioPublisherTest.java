import model.Option;
import model.OptionType;
import model.Portfolio;
import model.Position;
import model.Product;
import model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioPublisherTest {
    private Map<String, Product> securitiesInfo;
    private Portfolio portfolio;

    @BeforeEach
    public void setUp() {
        securitiesInfo = new HashMap<>();
        Stock aapl = new Stock("AAPL", 1, 0.1, 0.1);
        Option applC = new Option("AAPL-OCT-2024-110-C", "AAPL", OptionType.CALL, 110, 0.5);
        Option applP = new Option("AAPL-OCT-2024-110-P", "AAPL", OptionType.PUT, 110, 0.5);
        Stock tesla = new Stock("TESLA", 1, 0.1, 0.1);
        Stock azmn = new Stock("AZMN", 1, 0.1, 0.1);

        securitiesInfo.put("AAPL", aapl);
        securitiesInfo.put("AAPL-OCT-2024-110-C", applC);
        securitiesInfo.put("AAPL-OCT-2024-110-P", applP);
        securitiesInfo.put("TESLA", tesla);
        securitiesInfo.put("TESLA-NOV-2024-400-C", new Option("TESLA-NOV-2024-400-C", "TESLA", OptionType.CALL, 400, 7 / 12d));
        securitiesInfo.put("TESLA-DEC-2024-400-P", new Option("TESLA-DEC-2024-400-P", "TESLA", OptionType.PUT, 400, 8 / 12d));
        securitiesInfo.put("AZMN", azmn);
        portfolio = new Portfolio(Arrays.asList(new Position(aapl,1000), new Position(tesla, 1000), new Position(azmn, 1000)));
    }

    @Test
    public void allOptionsArePriced() {
        PortfolioPublisher publisher = new PortfolioPublisher(portfolio, securitiesInfo, new ArrayList<>());
        portfolio.getPositions().stream().map(Position::getProduct).filter(product -> product instanceof Option).forEach(option -> assertTrue(option.getPrice() > 0));
    }

    @Test
    public void scheduleStocksSortedInitially() {
        PortfolioPublisher publisher = new PortfolioPublisher(portfolio, securitiesInfo, new ArrayList<>());
        assertEquals(3, publisher.getScheduledStocks().size());
        assertTrue(isSorted(publisher.getScheduledStocks()));
    }

    @Test
    public void scheduleStocksSortedAfterRescheduled() {
        PortfolioPublisher publisher = new PortfolioPublisher(portfolio, securitiesInfo, new ArrayList<>());
        for (int i = 0; i < new Random().nextInt(100); ++i) {
            publisher.schedule(publisher.getScheduledStocks().poll().getKey());
        }
        assertTrue(isSorted(publisher.getScheduledStocks()));
    }

    private boolean isSorted(Queue<AbstractMap.SimpleEntry<Stock, Long>> scheduledStocks) {
        Iterator<AbstractMap.SimpleEntry<Stock, Long>> iterator = scheduledStocks.iterator();
        long previousTime = 0;
        while (iterator.hasNext()) {
            long scheduledTime = iterator.next().getValue();
            if (scheduledTime < previousTime) {
                return false;
            }
        }
        return true;
    }
}