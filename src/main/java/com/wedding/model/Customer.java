package com.wedding.model;

import java.time.LocalDate;

public class Customer {


    private String customerId;
    private String brideName;
    private String groomName;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDate weddingDate;
    private double budget;


    public Customer(String customerId, String brideName, String groomName,
                    String phoneNumber, String email, String address,
                    LocalDate weddingDate, double budget) {

        this.customerId = customerId;
        this.brideName = brideName;
        this.groomName = groomName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.weddingDate = weddingDate;
        this.budget = budget;
    }


    public String getCustomerId() {
        return customerId;
    }

    public String getBrideName() {
        return brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getWeddingDate() {
        return weddingDate;
    }

    public double getBudget() {
        return budget;
    }


    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setBrideName(String brideName) {
        this.brideName = brideName;
    }

    public void setGroomName(String groomName) {
        this.groomName = groomName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWeddingDate(LocalDate weddingDate) {
        this.weddingDate = weddingDate;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}