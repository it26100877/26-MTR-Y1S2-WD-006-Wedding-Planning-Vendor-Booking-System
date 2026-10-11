package model;

public class Review {

    // Encapsulation: Private variables to hide data
    private String reviewId;
    private String vendorName;
    private String customerName;
    private int rating; // e.g., 1 to 5 stars
    private String comment;

    // Constructor to initialize a new Review object
    public Review(String reviewId, String vendorName, String customerName, int rating, String comment) {
        this.reviewId = reviewId;
        this.vendorName = vendorName;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
    }

    // Public Getters to read the data
    public String getReviewId() {
        return reviewId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    // Public Setters to update the data
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Formats the review data into a single string for saving to the text file
    @Override
    public String toString() {
        return reviewId + "," + vendorName + "," + customerName + "," + rating + "," + comment;
    }
}