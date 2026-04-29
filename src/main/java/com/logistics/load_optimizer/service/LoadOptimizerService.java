package com.logistics.load_optimizer.service;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import com.logistics.load_optimizer.dto.OptimizeResponse;
import com.logistics.load_optimizer.model.Order;
import com.logistics.load_optimizer.model.Truck;
import com.logistics.load_optimizer.optimizer.KnapsackOptimizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadOptimizerService {

    private final KnapsackOptimizer optimizer;

    public LoadOptimizerService(KnapsackOptimizer optimizer) {
        this.optimizer = optimizer;
    }

    public OptimizeResponse optimize(OptimizeRequest request) {
        Truck truck = request.getTruck();
        List<Order> orders = request.getOrders();

        // Step 1: Group orders by route
        Map<String, List<Order>> routeGroups = new HashMap<>();
        for (Order order : orders) {
            String lane = order.getOrigin() + "|" + order.getDestination();
            routeGroups.computeIfAbsent(lane, k -> new ArrayList<>()).add(order);
        }

        // Step 2: For each route group, split by hazmat and run optimizer
        KnapsackOptimizer.Result bestResult = null;

        for (List<Order> routeGroup : routeGroups.values()) {

            // Split into hazmat and non-hazmat subgroups
            List<Order> hazmatOrders = new ArrayList<>();
            List<Order> nonHazmatOrders = new ArrayList<>();

            for (Order order : routeGroup) {
                if (order.isHazmat()) {
                    hazmatOrders.add(order);
                } else {
                    nonHazmatOrders.add(order);
                }
            }

            // Run optimizer on each subgroup
            if (!hazmatOrders.isEmpty()) {
                KnapsackOptimizer.Result result = optimizer.optimize(
                        hazmatOrders,
                        truck.getMaxWeightLbs(),
                        truck.getMaxVolumeCuft()
                );
                if (bestResult == null || result.totalPayoutCents() > bestResult.totalPayoutCents()) {
                    bestResult = result;
                }
            }

            if (!nonHazmatOrders.isEmpty()) {
                KnapsackOptimizer.Result result = optimizer.optimize(
                        nonHazmatOrders,
                        truck.getMaxWeightLbs(),
                        truck.getMaxVolumeCuft()
                );
                if (bestResult == null || result.totalPayoutCents() > bestResult.totalPayoutCents()) {
                    bestResult = result;
                }
            }
        }

        // Step 3: Handle no feasible combination
        if (bestResult == null || bestResult.selectedOrderIds().isEmpty()) {
            return OptimizeResponse.builder()
                    .truckId(truck.getId())
                    .selectedOrderIds(List.of())
                    .totalPayoutCents(0)
                    .totalWeightLbs(0)
                    .totalVolumeCuft(0)
                    .utilizationWeightPercent(0)
                    .utilizationVolumePercent(0)
                    .build();
        }

        // Step 4: Build response
        double utilizationWeight = (bestResult.totalWeightLbs() / truck.getMaxWeightLbs()) * 100;
        double utilizationVolume = (bestResult.totalVolumeCuft() / truck.getMaxVolumeCuft()) * 100;

        return OptimizeResponse.builder()
                .truckId(truck.getId())
                .selectedOrderIds(bestResult.selectedOrderIds())
                .totalPayoutCents(bestResult.totalPayoutCents())
                .totalWeightLbs(bestResult.totalWeightLbs())
                .totalVolumeCuft(bestResult.totalVolumeCuft())
                .utilizationWeightPercent(Math.round(utilizationWeight * 100.0) / 100.0)
                .utilizationVolumePercent(Math.round(utilizationVolume * 100.0) / 100.0)
                .build();
    }
}