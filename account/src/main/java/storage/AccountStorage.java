package storage;

import java.util.function.Function;
import models.UserInfo;

public interface AccountStorage {

    UserInfo createUser(String userId);
    double updateUserCash(String userId, Function<Double, Double> cashModifier);
    int updateStock(String userId, String companyName, Function<Integer, Integer> countModifier);
    UserInfo getUserInfo(String userId);

}
