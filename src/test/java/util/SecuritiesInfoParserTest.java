package util;

import model.Product;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SecuritiesInfoParserTest {
    private static final String VALID_PATH = "jdbc:sqlite:db/securitiesInfo.db";
    private static final String EMPTY_DB_PATH = "jdbc:sqlite:db/empty.db";
    private static final String WRONG_PATH = "jdbc:sqlite:db/wrong.db";
    private static final String PATH_WITH_NO_DRIVER = "db/securitiesInfo.db";

    @Test
    public void loadValidDb() {
        Map<String, Product> result = SecuritiesInfoParser.loadSecuritiesInfo(VALID_PATH);
        assertNotNull(result);
        assertEquals(6, result.size());
    }

    @Test
    public void loadEmptyDbReturnsNull() {
        assertNull(SecuritiesInfoParser.loadSecuritiesInfo(EMPTY_DB_PATH));
    }

    @Test
    public void loadWrongDbPathReturnsNull() {
        assertNull(SecuritiesInfoParser.loadSecuritiesInfo(WRONG_PATH));
    }

    @Test
    public void loadDbPathWithNoDriverReturnsNull() {
        assertNull(SecuritiesInfoParser.loadSecuritiesInfo(PATH_WITH_NO_DRIVER));
    }
}