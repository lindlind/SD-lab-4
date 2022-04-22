package service;

import clients.StockMarketClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Stock;
import models.UserInfo;
import models.UserStock;
import storage.AccountStorage;

public class Service implements AccountService {

    private final AccountStorage storage;
    private final StockMarketClient marketClient;

    public Service(AccountStorage storage, StockMarketClient marketClient) {
        this.storage = storage;
        this.marketClient = marketClient;
    }

    @Override
    public void addUser(String userId) {
        storage.createUser(userId);
    }

    @Override
    public void addUserCash(String userId, double cash) {
        storage.updateUserCash(userId, oldCash -> oldCash + cash);
    }

    @Override
    public double getUserCash(String userId) {
        return storage.getUserInfo(userId).cash();
    }

    @Override
    public List<UserStock> getUserStocks(String userId) {
        final UserInfo userInfo = storage.getUserInfo(userId);
        if (userInfo.stocks().isEmpty()) {
            return List.of();
        }
        final Map<String, Double> stocksCost = getStocksCostOnSell();
        return userInfo.stocks().entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 0)
            .map(entry -> new UserStock(
                entry.getKey(),
                stocksCost.get(entry.getKey()),
                entry.getValue()
            )).
            collect(Collectors.toList());
    }

    @Override
    public double getUserFinancialResources(String userId) {
        final UserInfo userInfo = storage.getUserInfo(userId);
        if (userInfo.stocks().isEmpty()) {
            return userInfo.cash();
        }
        final Map<String, Double> stocksCost = getStocksCostOnSell();
        final double userStocksAsCash = userInfo.stocks().entrySet()
            .stream()
            .mapToDouble(entry -> stocksCost.get(entry.getKey()) * entry.getValue())
            .sum();
        return userInfo.cash() + userStocksAsCash;
    }

    @Override
    public boolean buyStocks(String userId, String companyName, int count) {
        final double stockCost = getStocksCostOnBuy().get(companyName);
        storage.updateUserCash(userId, cash -> {
            if (cash < stockCost * count) {
                throw new IllegalArgumentException("You don't have enough cash");
            }
            return cash - stockCost * count;
        });
        boolean bought = marketClient.buyStocks(companyName, count);
        if (bought) {
            storage.updateStock(userId, companyName, oldCount -> oldCount + count);
        } else {
            storage.updateUserCash(userId, cash -> cash + stockCost * count);
        }
        return bought;
    }

    @Override
    public boolean sellStocks(String userId, String companyName, int count) {
        final double stockCost = getStocksCostOnSell().get(companyName);
        storage.updateStock(userId, companyName, oldCount -> {
            if (oldCount < count) {
                throw new IllegalArgumentException("You don't have enough stocks");
            }
            return oldCount - count;
        });
        boolean sold = marketClient.sellStocks(companyName, count);
        if (sold) {
            storage.updateUserCash(userId, cash -> cash + stockCost * count);
        } else {
            storage.updateStock(userId, companyName, oldCount -> oldCount + count);
        }
        return sold;
    }

    private Map<String, Double> getStocksCostOnBuy() {
        return marketClient.getStocksInfo()
            .stream()
            .collect(Collectors.toUnmodifiableMap(Stock::companyName, Stock::buyCost));
    }

    private Map<String, Double> getStocksCostOnSell() {
        return marketClient.getStocksInfo()
            .stream()
            .collect(Collectors.toUnmodifiableMap(Stock::companyName, Stock::sellCost));
    }
}
