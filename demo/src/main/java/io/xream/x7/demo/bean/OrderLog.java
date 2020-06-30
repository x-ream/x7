package io.xream.x7.demo.bean;

import io.xream.x7.common.repository.X;

public class OrderLog {

    @X.Key
    private long id;
    private long orderId;
    private String log;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
