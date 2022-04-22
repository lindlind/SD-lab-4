package service;

import java.util.List;
import models.UserStock;

public interface AccountService {

    void addUser(String userId);
    void addUserCash(String userId, double cash);
    double getUserCash(String userId);

    List<UserStock> getUserStocks(String userId);
    double getUserFinancialResources(String userId);
    boolean buyStocks(String userId, String companyName, int count);
    boolean sellStocks(String userId, String companyName, int count);

}
