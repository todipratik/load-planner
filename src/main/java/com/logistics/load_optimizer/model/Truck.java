package com.logistics.load_optimizer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Truck {

    @NotBlank
    private String id;

    @Positive
    private double maxWeightLbs;

    @Positive
    private double maxVolumeCuft;
}