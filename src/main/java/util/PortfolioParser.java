package util;

import model.Portfolio;
import model.Position;
import model.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class that parses portfolio csv into Portfolio, for example,
 * symbol,positionSize
 * AAPL,1000
 * AAPL-OCT-2024-110-C,-20000
 * AAPL-OCT-2024-110-P,20000
 * TELSA,-500
 * TELSA-NOV-2024-400-C,10000
 * TELSA-DEC-2024-400-P,-10000
 */
public class PortfolioParser {
    public static Portfolio parsePositions(final String path, final Map<String, Product> securitiesInfo) {
        assert securitiesInfo != null;
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            List<Position> positions =  lines.skip(1).map(line -> line.split(",")).filter(line -> line.length == 2).map(line -> createPosition(securitiesInfo, line[0], line[1])).collect(Collectors.toList());
            return new Portfolio(positions);
        } catch (IOException exception) {
            System.out.println("Unable to parse position csv from " + path);
        }
        return null;
    }

    private static Position createPosition(final Map<String, Product> securitiesInfo, final String symbol, final String qty) {
        Product product = securitiesInfo.get(symbol);
        if (product == null) {
            throw new RuntimeException("Product does not exist in securities info db");
        }
        return new Position(securitiesInfo.get(symbol), Integer.parseInt(qty));
    }
}
