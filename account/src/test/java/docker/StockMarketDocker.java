package docker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Timeout;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.ImagePullPolicy;
import utils.UrlReader;

public class StockMarketDocker {

    private static final String HOST = "localhost";
    private static final int PORT = 8090;
    private static final UrlReader urlReader = new UrlReader();

    public static GenericContainer stockMarketServer;

    public StockMarketDocker() {
        stockMarketServer = new FixedHostPortGenericContainer("stock_market:1.0-SNAPSHOT")
            .withFixedExposedPort(PORT, PORT)
            .withExposedPorts(PORT);
    }

    public void start() {
        stockMarketServer.start();
        prepareStocks();
    }

    public void stop() {
        stockMarketServer.stop();
    }

    private void prepareStocks() {
        assertEquals("Done", urlReader.readFromUrl(constructUrl("/add_stock", Map.of(
            "company_name", "AA",
            "buy_cost", "30",
            "sell_cost", "25",
            "stocks_count", "50"
        ))));
        assertEquals("Done", urlReader.readFromUrl(constructUrl("/add_stock", Map.of(
            "company_name", "BB",
            "buy_cost", "40",
            "sell_cost", "30",
            "stocks_count", "20"
        ))));
    }

    private String constructUrl(String query, Map<String, String> params) {
        String adminKeyParam = "key=ak_12345678";
        String paramsAsString = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        return "http://" + HOST + ":" + PORT + query + "?" + adminKeyParam + "&" + paramsAsString;
    }

}
