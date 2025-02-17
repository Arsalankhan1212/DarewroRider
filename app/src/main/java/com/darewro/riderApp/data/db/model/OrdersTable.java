package com.darewro.riderApp.data.db.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.darewro.riderApp.view.models.Order;


@Table(name = "OrdersTable")
public class OrdersTable extends Model {
    @Column(name = "orderId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    String orderId;

    Order orders;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Order getOrders() {
        return orders;
    }

    public void setOrders(Order orders) {
        this.orders = orders;
    }
}
