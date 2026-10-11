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

        for (Budget b : service.getAllBudgets()) {
            System.out.println("Budget ID: " + b.getBudgetId());
            System.out.println("Wedding ID: " + b.getWeddingId());
            System.out.println("Category: " + b.getCategory());
            System.out.println("Amount: " + b.getAmount());
            System.out.println("Description: " + b.getDescription());
        }
    }
}