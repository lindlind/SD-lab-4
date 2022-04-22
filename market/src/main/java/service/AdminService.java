package service;

import models.Stock;

public interface AdminService {

    boolean isAdmin(String key);

    void addStock(Stock stock);
    void changeStock(Stock stock);

}
