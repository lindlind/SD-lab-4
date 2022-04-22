package service;

import java.util.List;
import java.util.stream.Collectors;
import models.Stock;
import storage.StockMarketStorage;

public class StockService implements AdminService, MarketService {

    private final StockMarketStorage storage;
    private final String adminKey;

    public StockService(StockMarketStorage storage, String adminKey) {
        this.storage = storage;
        this.adminKey = adminKey;
    };

    @Override
    public boolean isAdmin(String key) {
        return key.equals(adminKey);
    }

    @Override
    public void addStock(Stock stock) {
        storage.createStock(stock);
    }

    @Override
    public void changeStock(Stock stock) {
        storage.upsertStock(stock.companyName(), oldStock -> stock);
    }

    @Override
    public List<Stock> getStocksInfo() {
        return storage.getAllStocks().collect(Collectors.toList());
    }

    @Override
    public void buyStocks(String companyName, int count) {
        storage.upsertStock(companyName, oldStock -> {
            if (oldStock.count() < count) {
                throw new IllegalArgumentException("Incorrect stocks count");
            }
            return new Stock(
                oldStock.companyName(),
                oldStock.buyCost(),
                oldStock.sellCost(),
                oldStock.count() - count
            );
        });
    }

    @Override
    public void sellStocks(String companyName, int count) {
        storage.upsertStock(companyName, oldStock -> new Stock(
            oldStock.companyName(),
            oldStock.buyCost(),
            oldStock.sellCost(),
            oldStock.count() + count
        ));
    }
}
