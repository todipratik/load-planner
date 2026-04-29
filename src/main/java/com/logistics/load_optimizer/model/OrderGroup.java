package com.logistics.load_optimizer.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderGroup {

    private String origin;
    private String destination;
    private boolean hazmat;
    private List<Order> orders;

    public String getLane() {
        return origin + "|" + destination;
    }
}