package clients;

import java.util.List;
import models.Stock;

public interface StockMarketClient {

    List<Stock> getStocksInfo();
    boolean buyStocks(String companyName, int count);
    boolean sellStocks(String companyName, int count);

}
