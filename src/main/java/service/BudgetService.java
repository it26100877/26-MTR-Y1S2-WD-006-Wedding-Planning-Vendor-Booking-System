
package service;

import model.Budget;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private final List<Budget> budgets = new ArrayList<>();

    private final Path filePath =
            Paths.get("src", "main", "data", "budgets.txt");

    public BudgetService() {
        loadBudgets();
    }

    public void addBudget(Budget budget) {
        budgets.add(budget);
        saveBudgets();
    }

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }

    public boolean updateBudget(String budgetId, Budget updatedBudget) {
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetId().equals(budgetId)) {
                budgets.set(i, updatedBudget);
                saveBudgets();
                return true;
            }
        }
        return false;
    }

    public boolean deleteBudget(String budgetId) {
        for (int i = 0; i < budgets.size(); i++) {
            if (budgets.get(i).getBudgetId().equals(budgetId)) {
                budgets.remove(i);
                saveBudgets();
                return true;
            }
        }
        return false;
    }

    private void saveBudgets() {
        try {
            Files.createDirectories(filePath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(
                    filePath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                for (Budget b : budgets) {
                    writer.write(
                            b.getBudgetId() + "|" +
                                    b.getWeddingId() + "|" +
                                    b.getCategory() + "|" +
                                    b.getAmount() + "|" +
                                    b.getDescription()
                    );
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
        }
    }

    private void loadBudgets() {
        if (!Files.exists(filePath)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|", -1);

                if (data.length == 5) {
                    try {
                        Budget budget = new Budget(
                                data[0],
                                data[1],
                                data[2],
                                Double.parseDouble(data[3]),
                                data[4]
                        );

                        budgets.add(budget);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid budget amount.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
        }
    }
}
