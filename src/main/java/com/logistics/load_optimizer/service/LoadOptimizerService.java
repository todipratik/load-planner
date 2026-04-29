package com.logistics.load_optimizer.service;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import com.logistics.load_optimizer.dto.OptimizeResponse;
import com.logistics.load_optimizer.model.Order;
import com.logistics.load_optimizer.model.Truck;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadOptimizerService {

    public OptimizeResponse optimize(OptimizeRequest request) {
        Truck truck = request.getTruck();
        List<Order> orders = request.getOrders();

        // TODO: plug in optimizer algorithm here
        List<String> selectedOrderIds = List.of();
        long totalPayoutCents = 0;
        double totalWeightLbs = 0;
        double totalVolumeCuft = 0;

        double utilizationWeight = (totalWeightLbs / truck.getMaxWeightLbs()) * 100;
        double utilizationVolume = (totalVolumeCuft / truck.getMaxVolumeCuft()) * 100;

        return OptimizeResponse.builder()
                .truckId(truck.getId())
                .selectedOrderIds(selectedOrderIds)
                .totalPayoutCents(totalPayoutCents)
                .totalWeightLbs(totalWeightLbs)
                .totalVolumeCuft(totalVolumeCuft)
                .utilizationWeightPercent(Math.round(utilizationWeight * 100.0) / 100.0)
                .utilizationVolumePercent(Math.round(utilizationVolume * 100.0) / 100.0)
                .build();
    }
}