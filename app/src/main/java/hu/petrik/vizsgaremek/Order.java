package hu.petrik.vizsgaremek;

import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private Address selectedAddress;
    private String status;
    private int total;
    private Date orderDate;


    public Order(String id, Address selectedAddress, String status, int total, Date orderDate) {
        this.id = id;
        this.selectedAddress = selectedAddress;
        this.status = status;
        this.total = total;
        this.orderDate = orderDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Address selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
class OrderListHelper {
    private Order activeOrder;
    private List<Order> orderHistory;
    public Order getActiveOrder() {return  activeOrder;}
    public  List<Order> getOrderHistory() {return orderHistory;}
}

