package server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpServer;
import exceptions.InvalidRequestException;
import java.io.IOException;
import java.net.InetSocketAddress;
import models.Stock;
import service.AdminService;
import service.MarketService;
import utils.JsonMapper;
import utils.UriParser;

public class Server {
    private final HttpServer server;
    private final JsonMapper jsonMapper;

    public Server(int port, MarketService marketService, AdminService adminService) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        this.jsonMapper = new JsonMapper();

        server.createContext("/stocks", new HttpHandlerImpl(uri -> {
            try {
                return jsonMapper.toJson(marketService.getStocksInfo());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to convert to json");
            }
        }));
        server.createContext("/buy", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String companyName = uriParser.parseParam("company_name");
            final int count = Integer.parseInt(uriParser.parseParam("stocks_count"));
            marketService.buyStocks(companyName, count);
            return "Done";
        }));
        server.createContext("/sell", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            final String companyName = uriParser.parseParam("company_name");
            final int count = Integer.parseInt(uriParser.parseParam("stocks_count"));
            marketService.sellStocks(companyName, count);
            return "Done";
        }));
        server.createContext("/add_stock", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            try {
                final String key = uriParser.parseParam("key");
                if (!adminService.isAdmin(key)) {
                    throw new InvalidRequestException();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new InvalidRequestException();
            }
            final String companyName = uriParser.parseParam("company_name");
            final double buyCost = Double.parseDouble(uriParser.parseParam("buy_cost"));
            final double sellCost = Double.parseDouble(uriParser.parseParam("sell_cost"));
            final int count = Integer.parseInt(uriParser.parseParam("stocks_count"));
            adminService.addStock(new Stock(companyName, buyCost, sellCost, count));
            return "Done";
        }));
        server.createContext("/change_stock", new HttpHandlerImpl(uri -> {
            final UriParser uriParser = new UriParser(uri);
            try {
                final String key = uriParser.parseParam("key");
                if (!adminService.isAdmin(key)) {
                    throw new InvalidRequestException();
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException();
            }
            final String companyName = uriParser.parseParam("company_name");
            final double buyCost = Double.parseDouble(uriParser.parseParam("buy_cost"));
            final double sellCost = Double.parseDouble(uriParser.parseParam("sell_cost"));
            final int count = Integer.parseInt(uriParser.parseParam("stocks_count"));
            adminService.changeStock(new Stock(companyName, buyCost, sellCost, count));
            return "Done";
        }));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1000);
    }

}
