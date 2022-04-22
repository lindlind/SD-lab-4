package models;

import java.util.Map;

public record UserInfo(
    String userId,
    double cash,
    Map<String, Integer> stocks
) { }
