import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

/**
 * Class that used to write securities info to SQLite database
 * Schema
 * Ticker String
 * Type String (model.Stock, Call, Put)
 * Strike int
 */
public class SecuritiesInfoWriter {
    final String DB_DIR = "db";
    final String DB_URL = "jdbc:sqlite:" + DB_DIR + "/securitiesInfo.db";
    final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS SecuritiesInfo (\n" +
            "Ticker varchar(255) PRIMARY KEY,\n" +
            "Type varchar(255),\n" +
            "ExpectedReturn double(2,1),\n" +
            "Volatility double(2,1),\n" +
            "InitialPrice double,\n" +
            "Strike integer,\n" +
            "Maturity integer\n" +
            ")";
    final String INSERT_STOCK_QUERY = "INSERT INTO SecuritiesInfo(Ticker, Type, ExpectedReturn, Volatility, InitialPrice) VALUES(?,?,?,?,?)";
    final String INSERT_OPTION_QUERY = "INSERT INTO SecuritiesInfo(Ticker, Type, Strike, Maturity) VALUES(?,?,?,?)";
    final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS SecuritiesInfo";

    Connection connection;

    public static void main(String[] args) throws SQLException {
        SecuritiesInfoWriter writer = new SecuritiesInfoWriter();
        writer.configureConnection();
        if (writer.connection != null) {
            writer.dropTable();
            writer.createTable();
            writer.insert("AAPL", "STOCK", 0.9, 0.5, 100d, null, null);
            writer.insert("AAPL-OCT-2024-110-C", "CALL", null, null, null, 110, 6);
            writer.insert("AAPL-OCT-2024-110-P", "PUT", null, null, null, 110, 6);
            writer.insert("TESLA", "STOCK", 0.7, 0.3, 430d, null, null);
            writer.insert("TESLA-NOV-2024-400-C", "CALL", null, null, null, 400, 7);
            writer.insert("TESLA-DEC-2024-400-P", "PUT", null, null, null, 400, 8);
            writer.connection.close();
        }
    }

    private void configureConnection() {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
            connection = DriverManager.getConnection(DB_URL);
            if (connection != null) {
                System.out.println("Connection established");
            }
        } catch (IOException exception) {
            System.out.println("Unable to create securitiesInfo database");
        } catch (SQLException exception) {
            System.out.println("Unable to establish connection to database");
        }
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE_QUERY);
            System.out.println("Table created");
        } catch (SQLException exception) {
            System.out.println("Unable to create securities info table");
        }
    }

    private void insert(String symbol, String type, Double expectedReturn, Double volatility, Double initialPrice, Integer strike, Integer maturityInMonth) {
        String query;
        if (type.equals("STOCK")) {
            query = INSERT_STOCK_QUERY;
        } else {
            query = INSERT_OPTION_QUERY;
        }
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, symbol);
            preparedStatement.setString(2, type);
            if (type.equals("STOCK")) {
                preparedStatement.setDouble(3, expectedReturn);
                preparedStatement.setDouble(4, volatility);
                preparedStatement.setDouble(5, initialPrice);
            } else {
                preparedStatement.setInt(3, strike);
                preparedStatement.setInt(4, maturityInMonth);
            }
            preparedStatement.executeUpdate();
            System.out.println("Inserted (" + symbol + ", " + type + ", " + expectedReturn + ", " + volatility + ", " + initialPrice + "," + strike + ", " + maturityInMonth + ")");
        } catch (SQLException exception) {
            System.out.println("Unable to insert product to table");
        }
    }

    private void dropTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(DROP_TABLE_QUERY);
            System.out.println("Table dropped");
        } catch (SQLException exception) {
            System.out.println("Unable to drop securities info table");
        }
    }
}
