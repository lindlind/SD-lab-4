package clients;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import docker.StockMarketDocker;
import java.util.List;
import models.Stock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.UrlReader;

public class StockMarketClientIT {

    private static StockMarketDocker stockMarketDocker;
    private static StockMarketClient marketClient;

    @BeforeAll
    private static void beforeAll() {
        stockMarketDocker = new StockMarketDocker();
        stockMarketDocker.start();
        marketClient = new StockMarketClientImpl(8090, new UrlReader());
    }

    @AfterAll
    private static void afterAll() {
        stockMarketDocker.stop();
    }

    @Test
    void stocksInfoTest() {
        List<Stock> stocks = marketClient.getStocksInfo();
        assertFalse(stocks.isEmpty());
        stocks.forEach(stock -> {
            assertNotNull(stock.companyName());
            assertNotEquals("", stock.companyName());

            assertTrue(stock.count() >= 0);
            assertTrue(stock.buyCost() >= 0);
            assertTrue(stock.sellCost() >= 0);
        });
    }

    @Test
    void buyStocksTest() {
        List<Stock> allStocks = marketClient.getStocksInfo();

        String unexistedCompany = "-";
        assertFalse(marketClient.buyStocks(unexistedCompany, 1));

        Stock stock = allStocks.stream().filter(s -> s.count() > 0).findFirst().get();
        assertFalse(marketClient.buyStocks(stock.companyName(), 2 * stock.count()));
        assertTrue(marketClient.buyStocks(stock.companyName(), stock.count()));
    }

    @Test
    void sellStocksTest() {
        List<Stock> allStocks = marketClient.getStocksInfo();

        String unexistedCompany = "-";
        assertFalse(marketClient.sellStocks(unexistedCompany, 1));

        Stock stock = allStocks.stream().findFirst().get();
        assertTrue(marketClient.sellStocks(stock.companyName(), 10));
    }

}
