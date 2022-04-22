package storage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import models.Stock;
import models.UserInfo;

public class InMemAccountStorage implements AccountStorage {

    Map<String, UserInfo> users = new ConcurrentHashMap<>();

    @Override
    public UserInfo createUser(String userId) {
        if (users.containsKey(userId)) {
            throw new IllegalArgumentException("User already exists");
        }
        return users.putIfAbsent(userId, new UserInfo(userId, 0, new HashMap<>()));
    }

    @Override
    public double updateUserCash(String userId, Function<Double, Double> cashModifier) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("No such user");
        }
        return users.computeIfPresent(userId, (id, userInfo) ->
            new UserInfo(userId, cashModifier.apply(userInfo.cash()), userInfo.stocks())
        ).cash();
    }

    @Override
    public int updateStock(String userId, String companyName, Function<Integer, Integer> countModifier) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("No such user");
        }
        return users.computeIfPresent(userId, (id, userInfo) -> {
            userInfo.stocks().putIfAbsent(companyName, 0);
            userInfo.stocks().computeIfPresent(companyName, (k, count) -> countModifier.apply(count));
            return userInfo;
        }).stocks().get(companyName);
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        UserInfo user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("No such user");
        }
        return user;
    }
}
