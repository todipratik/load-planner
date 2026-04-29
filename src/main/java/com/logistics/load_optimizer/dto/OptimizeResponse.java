package com.logistics.load_optimizer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OptimizeResponse {

    private String truckId;
    private List<String> selectedOrderIds;
    private long totalPayoutCents;
    private double totalWeightLbs;
    private double totalVolumeCuft;
    private double utilizationWeightPercent;
    private double utilizationVolumePercent;
}