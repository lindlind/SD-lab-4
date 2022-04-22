package service;

import java.util.List;
import models.Stock;

public interface MarketService {

    List<Stock> getStocksInfo();
    void buyStocks(String companyName, int count);
    void sellStocks(String companyName, int count);

}
