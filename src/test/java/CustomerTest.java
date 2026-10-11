import com.wedding.model.Customer;
import com.wedding.service.CustomerService;

import java.io.IOException;

public class CustomerTest {

    public static void main(String[] args) throws IOException {

        CustomerService service = new CustomerService();

        Customer customer = new Customer(
                "C001",
                "Jalani",
                "0712345678",
                "jalani@gmail.com"
        );

        service.addCustomer(customer);

        for (Customer c : service.getCustomers()) {
            System.out.println("Customer ID: " + c.getCustomerId());
            System.out.println("Name: " + c.getName());
            System.out.println("Phone: " + c.getPhone());
            System.out.println("Email: " + c.getEmail());
        }
    }

}
