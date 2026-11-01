package lk.sliit.group006.service;

import lk.sliit.group006.model.Budget;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private final List<Budget> budgets = new ArrayList<>();

    // Keep the existing project data location. Do not change this path
    // without agreeing with the group because it contains existing records.
    private final Path filePath = Paths.get("src", "main", "data", "budgets.txt");

    public BudgetService() {
        loadBudgets();
    }

    // CREATE
    public synchronized void addBudget(Budget budget) {
        validateBudget(budget, false);

        boolean duplicateId = budgets.stream().anyMatch(existing ->
                existing.getBudgetId().equalsIgnoreCase(budget.getBudgetId()));

        if (duplicateId) {
            throw new IllegalArgumentException(
                    "Budget ID already exists: " + budget.getBudgetId());
        }

        budgets.add(budget);
        try {
            saveBudgets();
        } catch (RuntimeException e) {
            // Roll back the in-memory change if the file could not be saved.
            budgets.remove(budgets.size() - 1);
            throw e;
        }
    }

    // READ
    public synchronized List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }

    // UPDATE
    public synchronized boolean updateBudget(String budgetId, Budget updatedBudget) {
        if (budgetId == null || budgetId.isBlank()) {
            throw new IllegalArgumentException("Budget ID is required.");
        }

        int index = -1;
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetId().equalsIgnoreCase(budgetId.trim())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        // A legacy ID already stored in budgets.txt (for example BG0) may stay
        // unchanged while editing. New or changed IDs must follow BG001 format.
        boolean keepingExistingId = updatedBudget != null
                && budgets.get(index).getBudgetId().equals(updatedBudget.getBudgetId());
        validateBudget(updatedBudget, keepingExistingId);

        if (!budgetId.trim().equalsIgnoreCase(updatedBudget.getBudgetId())) {
            throw new IllegalArgumentException(
                    "The Budget ID in the URL must match the Budget ID in the form.");
        }

        for (int i = 0; i < budgets.size(); i++) {
            if (i != index && budgets.get(i).getBudgetId()
                    .equalsIgnoreCase(updatedBudget.getBudgetId())) {
                throw new IllegalArgumentException(
                        "Another budget already uses ID: " + updatedBudget.getBudgetId());
            }
        }

        Budget previousBudget = budgets.set(index, updatedBudget);
        try {
            saveBudgets();
            return true;
        } catch (RuntimeException e) {
            // Restore the previous record if saving failed.
            budgets.set(index, previousBudget);
            throw e;
        }
    }

    // DELETE
    public synchronized boolean deleteBudget(String budgetId) {
        if (budgetId == null || budgetId.isBlank()) {
            throw new IllegalArgumentException("Budget ID is required.");
        }

        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetId().equalsIgnoreCase(budgetId.trim())) {
                Budget removedBudget = budgets.remove(i);
                try {
                    saveBudgets();
                    return true;
                } catch (RuntimeException e) {
                    // Restore the deleted record if saving failed.
                    budgets.add(i, removedBudget);
                    throw e;
                }
            }
        }

        return false;
    }

    private void validateBudget(Budget budget, boolean allowUnchangedLegacyId) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget details are required.");
        }

        String budgetId = requireText(budget.getBudgetId(), "Budget ID");
        String weddingId = requireText(budget.getWeddingId(), "Wedding ID");
        String category = requireText(budget.getCategory(), "Category");
        String description = requireText(budget.getDescription(), "Description");

        if (!allowUnchangedLegacyId && !budgetId.matches("BG\\d{3}")) {
            throw new IllegalArgumentException(
                    "Budget ID must use the format BG001 (BG followed by 3 digits).");
        }

        if (!weddingId.matches("W\\d{3}")) {
            throw new IllegalArgumentException(
                    "Wedding ID must use the format W001 (W followed by 3 digits).");
        }

        validateFileField(category, "Category");
        validateFileField(description, "Description");

        if (!Double.isFinite(budget.getAmount()) || budget.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be a valid number greater than zero.");
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        if (!value.equals(value.trim())) {
            throw new IllegalArgumentException(fieldName + " must not start or end with spaces.");
        }

        return value;
    }

    private void validateFileField(String value, String fieldName) {
        if (value.contains("|") || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(
                    fieldName + " must not contain | or line breaks because budgets.txt uses pipe-separated records.");
        }
    }

    // Save using a temporary file first so a failed write does not truncate
    // the existing budgets.txt file.
    private void saveBudgets() {
        Path temporaryFile = filePath.resolveSibling(filePath.getFileName() + ".tmp");

        try {
            Files.createDirectories(filePath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporaryFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {

                for (Budget budget : budgets) {
                    writer.write(
                            budget.getBudgetId() + "|" +
                                    budget.getWeddingId() + "|" +
                                    budget.getCategory() + "|" +
                                    budget.getAmount() + "|" +
                                    budget.getDescription());
                    writer.newLine();
                }
            }

            try {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw new IllegalStateException(
                    "Could not save budgets to " + filePath + ". Existing file was not intentionally cleared.", e);
        }
    }

    private void loadBudgets() {
        try {
            Files.createDirectories(filePath.getParent());

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;

                    if (line.isBlank()) {
                        continue;
                    }

                    String[] data = line.split("\\|", -1);
                    if (data.length != 5) {
                        throw new IllegalStateException(
                                "Invalid budgets.txt record at line " + lineNumber
                                        + ": expected exactly 5 fields. The original file has not been rewritten.");
                    }

                    try {
                        double amount = Double.parseDouble(data[3]);
                        if (!Double.isFinite(amount)) {
                            throw new NumberFormatException("Amount is not finite");
                        }

                        // Do not discard old records just because their IDs do not
                        // follow the new input format. This preserves legacy data.
                        Budget budget = new Budget(
                                data[0],
                                data[1],
                                data[2],
                                amount,
                                data[4]);

                        budgets.add(budget);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException(
                                "Invalid amount in budgets.txt at line " + lineNumber
                                        + ". Correct or back up this record before continuing.", e);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load budgets from " + filePath, e);
        }
    }
}
