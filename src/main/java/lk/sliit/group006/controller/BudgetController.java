package lk.sliit.group006.controller;

import lk.sliit.group006.model.Budget;
import lk.sliit.group006.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController() {
        this.budgetService = new BudgetService();
    }

    // READ: Get all budget records.
    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    // CREATE: Add a new budget record.
    @PostMapping
    public Budget addBudget(@RequestBody Budget budget) {
        budgetService.addBudget(budget);
        return budget;
    }

    // UPDATE: Update an existing budget record by its ID.
    @PutMapping("/{budgetId}")
    public boolean updateBudget(
            @PathVariable String budgetId,
            @RequestBody Budget budget) {
        return budgetService.updateBudget(budgetId, budget);
    }

    // DELETE: Delete a budget record by its ID.
    @DeleteMapping("/{budgetId}")
    public boolean deleteBudget(@PathVariable String budgetId) {
        return budgetService.deleteBudget(budgetId);
    }

    // Return validation failures as HTTP 400 instead of an internal server error.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    // Keep storage failures separate from user input validation failures.
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleStorageError(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("The budget operation could not be completed because of a file storage error.");
    }
}
