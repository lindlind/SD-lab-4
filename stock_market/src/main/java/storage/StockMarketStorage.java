package storage;

import java.util.function.Function;
import java.util.stream.Stream;
import models.Stock;

public interface StockMarketStorage {

    void createStock(Stock stock);
    void upsertStock(String companyName, Function<Stock, Stock> modifier);

    Stock getStock(String companyName);
    Stream<Stock> getAllStocks();

}
