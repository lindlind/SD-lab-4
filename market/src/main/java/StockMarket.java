import java.io.IOException;
import server.Server;
import service.StockService;
import storage.InMemStockMarketStorage;
import storage.StockMarketStorage;

public class StockMarket {
    private static final String ADMIN_KEY = "ak_12345678";

    public static void main(String[] args) throws IOException {
        StockMarketStorage storage = new InMemStockMarketStorage();
        StockService service = new StockService(storage, ADMIN_KEY);
        Server server = new Server(8090, service, service);
        server.start();
    }
}
