import clients.StockMarketClientImpl;
import java.io.IOException;
import server.Server;
import service.Service;
import storage.AccountStorage;
import storage.InMemAccountStorage;
import utils.UrlReader;

public class PersonalAccount {
    public static void main(String[] args) throws IOException {
        AccountStorage storage = new InMemAccountStorage();
        StockMarketClientImpl marketClient = new StockMarketClientImpl(8090, new UrlReader());
        Service service = new Service(storage, marketClient);
        Server server = new Server(8091, service);
        server.start();
    }
}
