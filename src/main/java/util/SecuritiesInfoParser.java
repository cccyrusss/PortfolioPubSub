package util;

import model.Option;
import model.OptionType;
import model.Product;
import model.Stock;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SecuritiesInfoParser {
    public static Map<String, Product> loadSecuritiesInfo(final String dbUrl) {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * from SecuritiesInfo");
            Map<String, Product> securitiesInfo = new HashMap<>();
            while (resultSet.next()) {
                String symbol = resultSet.getString(1);
                securitiesInfo.put(symbol, createProduct(symbol, resultSet));
            }
            return securitiesInfo;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static Product createProduct(String symbol, ResultSet resultSet) throws SQLException {
        if (resultSet.getString(2).equals("STOCK")) {
            return new Stock(symbol, resultSet.getDouble(5), resultSet.getDouble(3), resultSet.getDouble(4));
        }
        // Set maturity to remaining months / 12 for simplicity
        return new Option(symbol, getOptionUnderlier(symbol), OptionType.valueOf(resultSet.getString(2)), resultSet.getInt(6), resultSet.getInt(7) / 12d);
    }

    private static String getOptionUnderlier(String symbol) {
        return symbol.split("-", 2)[0];
    }
}
