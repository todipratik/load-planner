package com.logistics.load_optimizer.optimizer;

import com.logistics.load_optimizer.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackOptimizerTest {

    private KnapsackOptimizer optimizer;

    @BeforeEach
    void setUp() {
        optimizer = new KnapsackOptimizer();
    }

    @Test
    void testSingleOrderFits() {
        Order order = new Order();
        order.setId("ord-001");
        order.setPayoutCents(250000);
        order.setWeightLbs(18000);
        order.setVolumeCuft(1200);
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("2025-12-09");
        order.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order), 44000, 3000);

        assertEquals(1, result.selectedOrderIds().size());
        assertEquals("ord-001", result.selectedOrderIds().get(0));
        assertEquals(250000, result.totalPayoutCents());
        assertEquals(18000, result.totalWeightLbs());
        assertEquals(1200, result.totalVolumeCuft());
    }

    @Test
    void testMultipleOrdersSelectBest() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(250000);
        order1.setWeightLbs(18000);
        order1.setVolumeCuft(1200);
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-09");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(180000);
        order2.setWeightLbs(12000);
        order2.setVolumeCuft(900);
        order2.setPickupDate("2025-12-04");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        Order order3 = new Order();
        order3.setId("ord-003");
        order3.setPayoutCents(150000);
        order3.setWeightLbs(8000);
        order3.setVolumeCuft(600);
        order3.setPickupDate("2025-12-05");
        order3.setDeliveryDate("2025-12-09");
        order3.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2, order3), 44000, 3000);

        assertEquals(3, result.selectedOrderIds().size());
        assertEquals(580000, result.totalPayoutCents());
        assertEquals(38000, result.totalWeightLbs());
        assertEquals(2700, result.totalVolumeCuft());
    }

    @Test
    void testExceedsWeightLimit() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(250000);
        order1.setWeightLbs(30000);
        order1.setVolumeCuft(1200);
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-09");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(180000);
        order2.setWeightLbs(20000);
        order2.setVolumeCuft(900);
        order2.setPickupDate("2025-12-04");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2), 44000, 3000);

        assertEquals(1, result.selectedOrderIds().size());
        assertEquals("ord-001", result.selectedOrderIds().get(0));
        assertEquals(250000, result.totalPayoutCents());
    }

    @Test
    void testExceedsVolumeLimit() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(250000);
        order1.setWeightLbs(5000);
        order1.setVolumeCuft(2000);
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-09");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(180000);
        order2.setWeightLbs(4000);
        order2.setVolumeCuft(1500);
        order2.setPickupDate("2025-12-04");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2), 44000, 3000);

        assertEquals(1, result.selectedOrderIds().size());
        assertEquals("ord-001", result.selectedOrderIds().get(0));
        assertEquals(250000, result.totalPayoutCents());
    }

    @Test
    void testTimeWindowConflict() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(250000);
        order1.setWeightLbs(18000);
        order1.setVolumeCuft(1200);
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-07");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(180000);
        order2.setWeightLbs(12000);
        order2.setVolumeCuft(900);
        order2.setPickupDate("2025-12-10");
        order2.setDeliveryDate("2025-12-14");
        order2.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2), 44000, 3000);

        assertEquals(1, result.selectedOrderIds().size());
        assertTrue(result.selectedOrderIds().contains("ord-001"));
    }

    @Test
    void testTimeWindowOverlap() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(250000);
        order1.setWeightLbs(18000);
        order1.setVolumeCuft(1200);
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-09");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(180000);
        order2.setWeightLbs(12000);
        order2.setVolumeCuft(900);
        order2.setPickupDate("2025-12-04");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2), 44000, 3000);

        assertEquals(2, result.selectedOrderIds().size());
        assertEquals(430000, result.totalPayoutCents());
    }

    @Test
    void testNoFeasibleCombination() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(250000);
        order1.setWeightLbs(50000);
        order1.setVolumeCuft(1200);
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-09");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(180000);
        order2.setWeightLbs(40000);
        order2.setVolumeCuft(900);
        order2.setPickupDate("2025-12-04");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2), 44000, 3000);

        // Both individually exceed limits, so only one fits
        assertEquals(1, result.selectedOrderIds().size());
        assertTrue(result.selectedOrderIds().contains("ord-002"));
        assertEquals(180000, result.totalPayoutCents());
    }

    @Test
    void testEmptyOrderList() {
        KnapsackOptimizer.Result result = optimizer.optimize(List.of(), 44000, 3000);

        assertEquals(0, result.selectedOrderIds().size());
        assertEquals(0, result.totalPayoutCents());
    }

    @Test
    void testMaximizesPayout() {
        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(100000);
        order1.setWeightLbs(5000);
        order1.setVolumeCuft(400);
        order1.setPickupDate("2025-12-01");
        order1.setDeliveryDate("2025-12-10");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(500000);
        order2.setWeightLbs(20000);
        order2.setVolumeCuft(1500);
        order2.setPickupDate("2025-12-01");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        KnapsackOptimizer.Result result = optimizer.optimize(
                Arrays.asList(order1, order2), 44000, 3000);

        assertEquals(600000, result.totalPayoutCents());
        assertEquals(2, result.selectedOrderIds().size());
        assertTrue(result.selectedOrderIds().contains("ord-001"));
        assertTrue(result.selectedOrderIds().contains("ord-002"));
    }
}