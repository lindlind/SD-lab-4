package models;

public record UserStock(
    String companyName,
    double sellCost,
    int count
) { }
