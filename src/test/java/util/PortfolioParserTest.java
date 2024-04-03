package util;

import model.Option;
import model.OptionType;
import model.Portfolio;
import model.Product;
import model.Stock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioParserTest {
    private static final String VALID_PATH = "csv/positions.csv";
    private static final String NO_SECURITIES_INFO_PATH = "csv/symbolNotInSecuritiesInfo.csv";
    private static final String INVALID_QTY_PATH = "csv/invalidQty.csv";
    private static final String INVALID_FORMAT_PATH = "csv/invalidFormat.csv";
    private static final String WRONG_PATH = "csv/wrongggg.csv";
    private Map<String, Product> securitiesInfo;

    @BeforeEach
    public void setUp() {
        securitiesInfo = new HashMap<>();
        securitiesInfo.put("AAPL", new Stock("AAPL", 1, 0.1, 0.1));
        securitiesInfo.put("AAPL-OCT-2024-110-C", new Option("AAPL-OCT-2024-110-C", "AAPL", OptionType.CALL, 110, 0.5));
        securitiesInfo.put("AAPL-OCT-2024-110-P", new Option("AAPL-OCT-2024-110-P", "AAPL", OptionType.PUT, 110, 0.5));
        securitiesInfo.put("TESLA", new Stock("TESLA", 1, 0.1, 0.1));
        securitiesInfo.put("TESLA-NOV-2024-400-C", new Option("TESLA-NOV-2024-400-C", "TESLA", OptionType.CALL, 400, 7 / 12d));
        securitiesInfo.put("TESLA-DEC-2024-400-P", new Option("TESLA-DEC-2024-400-P", "TESLA", OptionType.PUT, 400, 8 / 12d));
    }

    @Test
    public void parseValidPositionFile() {
        Portfolio portfolio = PortfolioParser.parsePositions(VALID_PATH, securitiesInfo);
        assertNotNull(portfolio);
        assertEquals(6, portfolio.getPositions().size());
    }

    @Test
    public void parseValidPositionFileWithNoSecurityInfo() {
        assertThrows(RuntimeException.class, () -> PortfolioParser.parsePositions(NO_SECURITIES_INFO_PATH, securitiesInfo));
    }

    @Test
    public void parsePositionFileWithInvalidQty() {
        assertThrows(NumberFormatException.class, () -> PortfolioParser.parsePositions(INVALID_QTY_PATH, securitiesInfo));
    }

    @Test
    public void parsePositionFileWithInvalidFormat() {
        Portfolio portfolio = PortfolioParser.parsePositions(INVALID_FORMAT_PATH, securitiesInfo);
        assertNotNull(portfolio);
        assertEquals(0, portfolio.getPositions().size());
    }

    @Test
    public void parsePositionFileWithWrongPath() {
        assertNull(PortfolioParser.parsePositions(WRONG_PATH, new HashMap<>()));
    }
}