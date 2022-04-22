package server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import models.UserStock;
import service.AccountService;
import utils.UriParser;

public class Server {
    private final HttpServer server;
    public Server(int port, AccountService accountService) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);

        server.createContext("/add_user", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            accountService.addUser(userId);
            return "Done";
        }));
        server.createContext("/add_cash", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            final double cash = Double.parseDouble(uriParser.parseParam("cash"));
            accountService.addUserCash(userId, cash);
            return "Done";
        }));
        server.createContext("/cash", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            return Double.toString(accountService.getUserCash(userId));
        }));
        server.createContext("/stocks", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            final List<UserStock> stocks = accountService.getUserStocks(userId);
            if (stocks.isEmpty()) {
                return "No stocks";
            }
            return stocks.stream().map(Record::toString).collect(Collectors.joining("\n"));
        }));
        server.createContext("/financial_resources", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            return Double.toString(accountService.getUserFinancialResources(userId));
        }));
        server.createContext("/sell_stocks", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            final String companyName = uriParser.parseParam("company_name");
            final int count = Integer.parseInt(uriParser.parseParam("stocks_count"));
            return Boolean.toString(accountService.sellStocks(userId, companyName, count));
        }));
        server.createContext("/buy_stocks", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String userId = uriParser.parseParam("user_id");
            final String companyName = uriParser.parseParam("company_name");
            final int count = Integer.parseInt(uriParser.parseParam("stocks_count"));
            return Boolean.toString(accountService.buyStocks(userId, companyName, count));
        }));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1000);
    }

}
