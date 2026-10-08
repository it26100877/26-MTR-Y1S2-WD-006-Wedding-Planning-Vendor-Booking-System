package model;

public class Budget {
    private String budgetId;
    private String weddingId;
    private String category;
    private String description;
    private double amount;

    public Budget(String budgetId, String weddingId, String category, double amount, String description) {
        this.budgetId = budgetId;
        this.weddingId = weddingId;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getWeddingId() {
        return weddingId;
    }

    public void setWeddingId(String weddingId) {
        this.weddingId = weddingId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
