package com.logistics.load_optimizer.model;

import com.logistics.load_optimizer.validation.ValidOrderDates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@ValidOrderDates
public class Order {

    @NotBlank
    private String id;

    @Positive
    private long payoutCents;

    @Positive
    private double weightLbs;

    @Positive
    private double volumeCuft;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    private String pickupDate;

    @NotNull
    private String deliveryDate;

    @JsonProperty("isHazmat")
    private boolean hazmat;
}