package util;

import model.Review;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Relative path to the text file you created
    private static final String FILE_PATH = "D:/sem 2/myData/OOP/project/code/WeddingBookingSystem/src/main/data/reviews.txt";
    // ==========================================
    // CREATE: Save a new review to the text file
    // ==========================================
    public static boolean saveReview(Review review) {
        // The 'true' parameter turns on "append mode" so new reviews are added to the bottom
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // This calls the toString() method we set up in Review.java
            bw.write(review.toString());
            bw.newLine();
            return true;

        } catch (IOException e) {
            System.out.println("Error saving review: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // READ: Load all reviews from the text file
    // ==========================================
    public static List<Review> getAllReviews() {
        List<Review> reviewList = new ArrayList<>();
        File file = new File(FILE_PATH);

        // If the file is missing or empty, just return the empty list
        if (!file.exists()) {
            return reviewList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            // Read the file line by line until the end
            while ((line = br.readLine()) != null) {

                // Split the string back into individual pieces of data using the comma
                String[] data = line.split(",");

                // Make sure the line has all 5 parts before trying to build the object
                if (data.length == 5) {
                    String reviewId = data[0];
                    String vendorName = data[1];
                    String customerName = data[2];
                    int rating = Integer.parseInt(data[3]);
                    String comment = data[4];

                    // Reconstruct the OOP object
                    Review review = new Review(reviewId, vendorName, customerName, rating, comment);
                    reviewList.add(review);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reviews: " + e.getMessage());
        }

        return reviewList;
    }

    // ==========================================
    // READ (Single): Find a review by its ID
    // ==========================================
    public static Review getReviewById(String reviewId) {
        List<Review> reviews = getAllReviews();
        for (Review r : reviews) {
            if (r.getReviewId().equals(reviewId)) {
                return r;
            }
        }
        return null; // Return null if not found
    }

    // ==========================================
    // UPDATE: Modify an existing review
    // ==========================================
    public static boolean updateReview(Review updatedReview) {
        List<Review> reviews = getAllReviews();
        boolean found = false;

        for (int i = 0; i < reviews.size(); i++) {
            if (reviews.get(i).getReviewId().equals(updatedReview.getReviewId())) {
                reviews.set(i, updatedReview); // Replace with the updated object
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        // Rewrite the entire file with the updated list
        return rewriteFile(reviews);
    }

    // ==========================================
    // DELETE: Remove a review by its ID
    // ==========================================
    public static boolean deleteReview(String reviewId) {
        List<Review> reviews = getAllReviews();
        boolean removed = reviews.removeIf(r -> r.getReviewId().equals(reviewId));

        if (!removed) {
            return false;
        }

        // Rewrite the entire file without the deleted review
        return rewriteFile(reviews);
    }

    // ==========================================
    // HELPER: Overwrite file with a given list
    // ==========================================
    private static boolean rewriteFile(List<Review> reviews) {
        try (FileWriter fw = new FileWriter(FILE_PATH, false); // false = overwrite mode
             BufferedWriter bw = new BufferedWriter(fw)) {

            for (Review r : reviews) {
                bw.write(r.toString());
                bw.newLine();
            }
            return true;

        } catch (IOException e) {
            System.out.println("Error rewriting file: " + e.getMessage());
            return false;
        }
    }
}