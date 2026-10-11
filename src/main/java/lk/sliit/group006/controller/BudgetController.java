
package lk.sliit.group006.controller;

import lk.sliit.group006.model.Budget;
import lk.sliit.group006.service.BudgetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController() {
        this.budgetService = new BudgetService();
    }

    // READ Get all budgets
    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    // CREATE Add a budget
    @PostMapping
    public Budget addBudget(@RequestBody Budget budget) {
        budgetService.addBudget(budget);
        return budget;
    }

    // UPDATE  Update an existing budget
    @PutMapping("/{budgetId}")
    public boolean updateBudget(
            @PathVariable String budgetId,
            @RequestBody Budget budget) {
        return budgetService.updateBudget(budgetId, budget);
    }

    // DELETE Delete a budget
    @DeleteMapping("/{budgetId}")
    public boolean deleteBudget(@PathVariable String budgetId) {
        return budgetService.deleteBudget(budgetId);
    }
}
