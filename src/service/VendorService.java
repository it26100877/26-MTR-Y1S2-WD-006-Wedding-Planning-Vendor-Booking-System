package service;

import model.Vendor;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class VendorService {

    private List<Vendor> vendors = new ArrayList<>();

    // CREATE
    public void addVendor(Vendor vendor) {
        vendors.add(vendor);
    }

    // READ
    public List<Vendor> getAllVendors() {
        return vendors;
    }

    // UPDATE
    public boolean updateVendor(Vendor updatedVendor) {

        for (Vendor vendor : vendors) {

            if (vendor.getVendorId().equals(updatedVendor.getVendorId())) {

                vendor.setVendorName(updatedVendor.getVendorName());
                vendor.setCategory(updatedVendor.getCategory());
                vendor.setContactNumber(updatedVendor.getContactNumber());
                vendor.setEmail(updatedVendor.getEmail());
                vendor.setPrice(updatedVendor.getPrice());
                vendor.setAvailability(updatedVendor.getAvailability());

                return true;
            }
        }

        return false;
    }

    // DELETE

    public boolean deleteVendor(String vendorId) {
        for (int i = 0; i < vendors.size(); i++) {
            if (vendors.get(i).getVendorId().equalsIgnoreCase(vendorId.trim())) {
                vendors.remove(i);
                return true;
            }
        }
        return false;
    }


    // SAVE DATA TO FILE
    public void saveToFile() {

        try {
            FileWriter writer = new FileWriter("vendors.txt");

            for (Vendor vendor : vendors) {

                writer.write(
                        vendor.getVendorId() + "," +
                                vendor.getVendorName() + "," +
                                vendor.getCategory() + "," +
                                vendor.getContactNumber() + "," +
                                vendor.getEmail() + "," +
                                vendor.getPrice() + "," +
                                vendor.getAvailability() + "\n"
                );
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Error saving vendors: " + e.getMessage());
        }
    }

    // LOAD DATA FROM FILE
    public void loadFromFile() {

        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader("vendors.txt")
            );

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                Vendor vendor = new Vendor();

                vendor.setVendorId(data[0]);
                vendor.setVendorName(data[1]);
                vendor.setCategory(data[2]);
                vendor.setContactNumber(data[3]);
                vendor.setEmail(data[4]);
                vendor.setPrice(Double.parseDouble(data[5]));
                vendor.setAvailability(data[6]);

                vendors.add(vendor);
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Error loading vendors: " + e.getMessage());
        }
    }
}