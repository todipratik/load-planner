package com.logistics.load_optimizer.optimizer;

import com.logistics.load_optimizer.model.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class KnapsackOptimizer {

    public Result optimize(List<Order> orders, double maxWeightLbs, double maxVolumeCuft) {
        int n = orders.size();

        // Step 1: Precompute time-window compatibility matrix
        boolean[][] compatible = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            compatible[i][i] = true;
            for (int j = i + 1; j < n; j++) {
                compatible[i][j] = isTimeWindowCompatible(orders.get(i), orders.get(j));
                compatible[j][i] = compatible[i][j];
            }
        }

        // Step 2: Bitmask iteration over all 2^n combinations
        long bestPayout = 0;
        int bestMask = 0;

        for (int mask = 1; mask < (1 << n); mask++) {
            double totalWeight = 0;
            double totalVolume = 0;
            long totalPayout = 0;
            boolean valid = true;

            List<Integer> selectedIndices = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    selectedIndices.add(i);
                    totalWeight += orders.get(i).getWeightLbs();
                    totalVolume += orders.get(i).getVolumeCuft();
                    totalPayout += orders.get(i).getPayoutCents();
                }
            }

            // Check weight and volume limits
            if (totalWeight > maxWeightLbs || totalVolume > maxVolumeCuft) {
                continue;
            }

            // Check time-window compatibility for all pairs
            for (int i = 0; i < selectedIndices.size() && valid; i++) {
                for (int j = i + 1; j < selectedIndices.size() && valid; j++) {
                    if (!compatible[selectedIndices.get(i)][selectedIndices.get(j)]) {
                        valid = false;
                    }
                }
            }

            if (!valid) continue;

            // Track best
            if (totalPayout > bestPayout) {
                bestPayout = totalPayout;
                bestMask = mask;
            }
        }

        // Step 3: Build result from best mask
        List<String> selectedIds = new ArrayList<>();
        double totalWeight = 0;
        double totalVolume = 0;

        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                selectedIds.add(orders.get(i).getId());
                totalWeight += orders.get(i).getWeightLbs();
                totalVolume += orders.get(i).getVolumeCuft();
            }
        }

        return new Result(selectedIds, bestPayout, totalWeight, totalVolume);
    }

    private boolean isTimeWindowCompatible(Order a, Order b) {
        LocalDate aPickup = LocalDate.parse(a.getPickupDate());
        LocalDate aDelivery = LocalDate.parse(a.getDeliveryDate());
        LocalDate bPickup = LocalDate.parse(b.getPickupDate());
        LocalDate bDelivery = LocalDate.parse(b.getDeliveryDate());

        LocalDate overlapStart = aPickup.isAfter(bPickup) ? aPickup : bPickup;
        LocalDate overlapEnd = aDelivery.isBefore(bDelivery) ? aDelivery : bDelivery;

        return !overlapStart.isAfter(overlapEnd);
    }

    public record Result(
            List<String> selectedOrderIds,
            long totalPayoutCents,
            double totalWeightLbs,
            double totalVolumeCuft
    ) {}
}