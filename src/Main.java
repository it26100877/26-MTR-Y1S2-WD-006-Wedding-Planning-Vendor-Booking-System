
import java.util.Scanner;
import model.Vendor;
import service.VendorService;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        VendorService service = new VendorService();
        service.loadFromFile();

        while (true) {
            System.out.println("\n=== VENDOR MANAGEMENT ===");
            System.out.println("1. Add Vendor");
            System.out.println("2. View Vendors");
            System.out.println("3. Update Vendor");
            System.out.println("4. Delete Vendor");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 5) {
                System.out.println("Program closed.");
                break;
            }
            if (choice == 1) {
                Vendor vendor = new Vendor();

                System.out.print("Vendor ID: ");
                vendor.setVendorId(sc.nextLine());

                System.out.print("Vendor Name: ");
                vendor.setVendorName(sc.nextLine());

                System.out.print("Category: ");
                vendor.setCategory(sc.nextLine());

                System.out.print("Contact Number: ");
                vendor.setContactNumber(sc.nextLine());

                System.out.print("Email: ");
                vendor.setEmail(sc.nextLine());

                System.out.print("Price: ");
                vendor.setPrice(sc.nextDouble());
                sc.nextLine();

                System.out.print("Availability: ");
                vendor.setAvailability(sc.nextLine());

                boolean exists = false;

                for (Vendor v : service.getAllVendors()) {
                    if (v.getVendorId().equalsIgnoreCase(vendor.getVendorId())) {
                        exists = true;
                        break;
                    }
                }

                if (exists) {
                    System.out.println("Vendor ID already exists!");
                } else {
                    service.addVendor(vendor);
                    service.saveToFile();
                    System.out.println("Vendor added successfully!");
                }
            }
            else if (choice == 2) {
                System.out.println("=== VENDOR LIST ===");

                for (Vendor v : service.getAllVendors()) {
                    System.out.println(
                            v.getVendorId() + " | " +
                                    v.getVendorName() + " | " +
                                    v.getCategory() + " | " +
                                    v.getContactNumber() + " | " +
                                    v.getEmail() + " | " +
                                    v.getPrice() + " | " +
                                    v.getAvailability()
                    );
                }
            }
            else if (choice == 3) {
                System.out.print("Enter Vendor ID to update: ");
                String id = sc.nextLine();

                Vendor found = null;

                for (Vendor v : service.getAllVendors()) {
                    if (v.getVendorId().equals(id)) {
                        found = v;
                        break;
                    }
                }

                if (found != null) {
                    System.out.print("Enter new vendor name: ");
                    found.setVendorName(sc.nextLine());

                    service.updateVendor(found);
                    service.saveToFile();

                    System.out.println("Vendor updated successfully!");
                } else {
                    System.out.println("Vendor not found!");
                }
            }

            else if (choice == 4) {
                System.out.print("Enter Vendor ID to delete: ");
                String id = sc.nextLine();

                boolean deleted = service.deleteVendor(id.trim().toUpperCase());

                if (deleted) {
                    service.saveToFile();
                    System.out.println("Vendor deleted successfully!");
                } else {
                    System.out.println("Vendor not found!");
                }
            }
            else {
                System.out.println("Invalid choice! Please select 1-5.");
            }



        }

    }


}