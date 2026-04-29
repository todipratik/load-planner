package com.logistics.load_optimizer.dto;

import com.logistics.load_optimizer.model.Order;
import com.logistics.load_optimizer.model.Truck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OptimizeRequest {

    @NotNull
    @Valid
    private Truck truck;

    @NotNull
    @NotEmpty
    @Size(max = 22)
    @Valid
    private List<Order> orders;
}