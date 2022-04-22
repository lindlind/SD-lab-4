package storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import models.Stock;

public class InMemStockMarketStorage implements StockMarketStorage {

    Map<String, Stock> stocks = new ConcurrentHashMap<>();

    @Override
    public void createStock(Stock stock) {
        if (stocks.containsKey(stock.companyName())) {
            throw new IllegalArgumentException("Stock already exists");
        }
        stocks.putIfAbsent(stock.companyName(), stock);
    }

    @Override
    public void upsertStock(String companyName, Function<Stock, Stock> modifier) {
        if (!stocks.containsKey(companyName)) {
            throw new IllegalArgumentException("Stock doesn't exist");
        }
        stocks.computeIfPresent(companyName, (k, v) -> modifier.apply(v));
    }

    @Override
    public Stock getStock(String companyName) {
        return stocks.getOrDefault(companyName, new Stock(companyName, 0, 0, 0));
    }

    @Override
    public Stream<Stock> getAllStocks() {
        return stocks.values().stream();
    }
}
