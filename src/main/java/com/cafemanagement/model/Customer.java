package com.cafemanagement.model;

public class Customer {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private int ordersCount;

    public Customer() {}

    public Customer(int id, String name, String email, String phone, String address, int ordersCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.ordersCount = ordersCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getOrdersCount() { return ordersCount; }
    public void setOrdersCount(int ordersCount) { this.ordersCount = ordersCount; }

    @Override
    public String toString() {
        return name + (phone != null && !phone.isEmpty() ? " - " + phone : "");
    }
}
