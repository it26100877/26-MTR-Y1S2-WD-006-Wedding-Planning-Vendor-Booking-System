
package service;

import model.Budget;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private final List<Budget> budgets = new ArrayList<>();

    public void addBudget(Budget budget) {
        budgets.add(budget);
    }

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }
}
