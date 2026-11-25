package com.cafemanagement.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private String customerName;
    private int userId;
    private String userName;
    private double total;
    private String status; // Pending, Processing, Completed, Cancelled
    private Date orderDate;
    private List<OrderDetail> items = new ArrayList<>();

    public Order() {}

    public Order(int id, int customerId, int userId, double total, String status, Date orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.userId = userId;
        this.total = total;
        this.status = status;
        this.orderDate = orderDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public List<OrderDetail> getItems() { return items; }
    public void setItems(List<OrderDetail> items) { this.items = items; }

    public void addItem(OrderDetail item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void recalculateTotal() {
        double sum = 0.0;
        for (OrderDetail item : items) {
            sum += item.getLineTotal();
        }
        this.total = sum;
    }
}
