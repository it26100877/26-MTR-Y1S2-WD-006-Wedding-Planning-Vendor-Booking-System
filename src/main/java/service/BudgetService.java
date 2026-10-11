
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

    public boolean updateBudget(String budgetId, Budget updatedBudget) {
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetId().equals(budgetId)) {
                budgets.set(i, updatedBudget);
                return true;
            }
        }
        return false;
    }

    public boolean deleteBudget(String budgetId) {
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetId().equals(budgetId)) {
                budgets.remove(i);
                return true;
            }
        }
        return false;
    }
}
