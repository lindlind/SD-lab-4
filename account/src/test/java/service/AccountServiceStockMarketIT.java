package service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import clients.StockMarketClient;
import clients.StockMarketClientImpl;
import docker.StockMarketDocker;
import java.util.List;
import models.Stock;
import models.UserStock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.InMemAccountStorage;
import utils.UrlReader;

public class AccountServiceStockMarketIT {

    private static StockMarketDocker stockMarketDocker;
    private static StockMarketClient marketClient;
    private AccountService service;

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

    @BeforeEach
    private void before() {
        service = new Service(new InMemAccountStorage(), marketClient);
        service.addUser("test-user");
    }

    @Test
    void buySellStocksTest() {
        List<UserStock> stocks = service.getUserStocks("test-user");
        assertArrayEquals(List.of().toArray(), stocks.toArray());

        Stock availableStock = marketClient.getStocksInfo().get(0);
        service.addUserCash("test-user", 100 * availableStock.buyCost());
        double userCash = service.getUserCash("test-user");

        assertFalse(service.buyStocks("test-user", availableStock.companyName(), 2 * availableStock.count()));
        assertEquals(
            userCash,
            service.getUserCash("test-user")
        );
        assertEquals(
            0,
            service.getUserStocks("test-user").stream().mapToInt(UserStock::count).sum()
        );

        int boughtCount = 5;
        assertTrue(service.buyStocks("test-user", availableStock.companyName(), boughtCount));
        assertEquals(
            userCash - boughtCount * availableStock.buyCost(),
            service.getUserCash("test-user")
        );
        assertEquals(
            boughtCount,
            service.getUserStocks("test-user").stream().mapToInt(UserStock::count).sum()
        );

        userCash = service.getUserCash("test-user");
        UserStock boughtStock = service.getUserStocks("test-user").get(0);

        assertTrue(service.sellStocks("test-user", availableStock.companyName(), boughtCount));

        assertEquals(
            userCash + boughtCount * boughtStock.sellCost(),
            service.getUserCash("test-user")
        );
        assertEquals(
            0,
            service.getUserStocks("test-user").stream().mapToInt(UserStock::count).sum()
        );
    }

}
