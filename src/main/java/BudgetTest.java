import model.Budget;
import service.BudgetService;

public class BudgetTest {
    public static void main(String[] args) {

        BudgetService service = new BudgetService();

        Budget budget = new Budget(
                "BG001",
                "W001",
                "Catering",
                150000,
                "Food package"
        );

        service.addBudget(budget);

        Budget updatedBudget = new Budget(
                "BG001",
                "W001",
                "Catering",
                175000,
                "Updated food package"
        );

        boolean updated = service.updateBudget("BG001", updatedBudget);
        System.out.println("Budget updated: " + updated);

        boolean deleted = service.deleteBudget("BG001");
        System.out.println("Budget deleted: " + deleted);

        for (Budget b : service.getAllBudgets()) {
            System.out.println("Budget ID: " + b.getBudgetId());
            System.out.println("Wedding ID: " + b.getWeddingId());
            System.out.println("Category: " + b.getCategory());
            System.out.println("Amount: " + b.getAmount());
            System.out.println("Description: " + b.getDescription());
        }
    }
}