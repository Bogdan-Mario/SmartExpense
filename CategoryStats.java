package com.portfolio.smartexpense.models;

public class CategoryStats {
    private String category;
    private double totalSpent;
    private double percentage;

    public CategoryStats(String category, double totalSpent, double percentage) {
        this.category = category;
        this.totalSpent = Math.round(totalSpent * 100.0) / 100.0; // Round to 2 decimals
        this.percentage = Math.round(percentage * 10.0) / 10.0;   // Round to 1 decimal
    }

    // Getters so Spring Boot can turn this into JSON
    public String getCategory() { return category; }
    public double getTotalSpent() { return totalSpent; }
    public double getPercentage() { return percentage; }
}