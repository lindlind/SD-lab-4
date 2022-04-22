package models;

public record Stock(
    String companyName,
    double buyCost,
    double sellCost,
    int count
) { }
