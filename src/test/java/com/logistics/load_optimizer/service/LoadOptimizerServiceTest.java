package com.logistics.load_optimizer.service;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import com.logistics.load_optimizer.dto.OptimizeResponse;
import com.logistics.load_optimizer.model.Order;
import com.logistics.load_optimizer.model.Truck;
import com.logistics.load_optimizer.optimizer.KnapsackOptimizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class LoadOptimizerServiceTest {

    private LoadOptimizerService service;
    
    @Mock
    private KnapsackOptimizer optimizer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new LoadOptimizerService(optimizer);
    }

    @Test
    void testOptimizeWithSingleRoute() {
        Truck truck = new Truck();
        truck.setId("truck-001");
        truck.setMaxWeightLbs(44000);
        truck.setMaxVolumeCuft(3000);

        Order order = new Order();
        order.setId("ord-001");
        order.setPayoutCents(250000);
        order.setWeightLbs(18000);
        order.setVolumeCuft(1200);
        order.setOrigin("Los Angeles, CA");
        order.setDestination("Dallas, TX");
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("2025-12-09");
        order.setHazmat(false);

        OptimizeRequest request = new OptimizeRequest();
        request.setTruck(truck);
        request.setOrders(Arrays.asList(order));

        KnapsackOptimizer.Result mockResult = new KnapsackOptimizer.Result(
                Arrays.asList("ord-001"), 250000, 18000, 1200);
        when(optimizer.optimize(anyList(), eq(44000.0), eq(3000.0)))
                .thenReturn(mockResult);

        OptimizeResponse response = service.optimize(request);

        assertNotNull(response);
        assertEquals("truck-001", response.getTruckId());
        assertEquals(1, response.getSelectedOrderIds().size());
        assertEquals(250000, response.getTotalPayoutCents());
    }

    @Test
    void testOptimizeWithHazmatIsolation() {
        Truck truck = new Truck();
        truck.setId("truck-001");
        truck.setMaxWeightLbs(44000);
        truck.setMaxVolumeCuft(3000);

        Order nonHazmat1 = new Order();
        nonHazmat1.setId("ord-001");
        nonHazmat1.setPayoutCents(250000);
        nonHazmat1.setWeightLbs(18000);
        nonHazmat1.setVolumeCuft(1200);
        nonHazmat1.setOrigin("Los Angeles, CA");
        nonHazmat1.setDestination("Dallas, TX");
        nonHazmat1.setPickupDate("2025-12-05");
        nonHazmat1.setDeliveryDate("2025-12-09");
        nonHazmat1.setHazmat(false);

        Order nonHazmat2 = new Order();
        nonHazmat2.setId("ord-002");
        nonHazmat2.setPayoutCents(180000);
        nonHazmat2.setWeightLbs(12000);
        nonHazmat2.setVolumeCuft(900);
        nonHazmat2.setOrigin("Los Angeles, CA");
        nonHazmat2.setDestination("Dallas, TX");
        nonHazmat2.setPickupDate("2025-12-04");
        nonHazmat2.setDeliveryDate("2025-12-10");
        nonHazmat2.setHazmat(false);

        Order hazmat = new Order();
        hazmat.setId("ord-003");
        hazmat.setPayoutCents(320000);
        hazmat.setWeightLbs(30000);
        hazmat.setVolumeCuft(1800);
        hazmat.setOrigin("Los Angeles, CA");
        hazmat.setDestination("Dallas, TX");
        hazmat.setPickupDate("2025-12-06");
        hazmat.setDeliveryDate("2025-12-08");
        hazmat.setHazmat(true);

        OptimizeRequest request = new OptimizeRequest();
        request.setTruck(truck);
        request.setOrders(Arrays.asList(nonHazmat1, nonHazmat2, hazmat));

        KnapsackOptimizer.Result nonHazmatResult = new KnapsackOptimizer.Result(
                Arrays.asList("ord-001", "ord-002"), 430000, 30000, 2100);
        KnapsackOptimizer.Result hazmatResult = new KnapsackOptimizer.Result(
                Arrays.asList("ord-003"), 320000, 30000, 1800);

        when(optimizer.optimize(anyList(), eq(44000.0), eq(3000.0)))
                .thenReturn(nonHazmatResult)
                .thenReturn(hazmatResult);

        OptimizeResponse response = service.optimize(request);

        assertEquals(2, response.getSelectedOrderIds().size());
        assertEquals(430000, response.getTotalPayoutCents());
    }

    @Test
    void testOptimizeWithDifferentRoutes() {
        Truck truck = new Truck();
        truck.setId("truck-001");
        truck.setMaxWeightLbs(44000);
        truck.setMaxVolumeCuft(3000);

        Order order1 = new Order();
        order1.setId("ord-001");
        order1.setPayoutCents(150000);
        order1.setWeightLbs(10000);
        order1.setVolumeCuft(800);
        order1.setOrigin("Los Angeles, CA");
        order1.setDestination("Dallas, TX");
        order1.setPickupDate("2025-12-05");
        order1.setDeliveryDate("2025-12-09");
        order1.setHazmat(false);

        Order order2 = new Order();
        order2.setId("ord-002");
        order2.setPayoutCents(120000);
        order2.setWeightLbs(8000);
        order2.setVolumeCuft(600);
        order2.setOrigin("Los Angeles, CA");
        order2.setDestination("Dallas, TX");
        order2.setPickupDate("2025-12-04");
        order2.setDeliveryDate("2025-12-10");
        order2.setHazmat(false);

        Order order3 = new Order();
        order3.setId("ord-003");
        order3.setPayoutCents(400000);
        order3.setWeightLbs(22000);
        order3.setVolumeCuft(1500);
        order3.setOrigin("Chicago, IL");
        order3.setDestination("Miami, FL");
        order3.setPickupDate("2025-12-06");
        order3.setDeliveryDate("2025-12-10");
        order3.setHazmat(false);

        Order order4 = new Order();
        order4.setId("ord-004");
        order4.setPayoutCents(350000);
        order4.setWeightLbs(18000);
        order4.setVolumeCuft(1200);
        order4.setOrigin("Chicago, IL");
        order4.setDestination("Miami, FL");
        order4.setPickupDate("2025-12-05");
        order4.setDeliveryDate("2025-12-11");
        order4.setHazmat(false);

        OptimizeRequest request = new OptimizeRequest();
        request.setTruck(truck);
        request.setOrders(Arrays.asList(order1, order2, order3, order4));

        KnapsackOptimizer.Result laResult = new KnapsackOptimizer.Result(
                Arrays.asList("ord-001", "ord-002"), 270000, 18000, 1400);
        KnapsackOptimizer.Result chicagoResult = new KnapsackOptimizer.Result(
                Arrays.asList("ord-003", "ord-004"), 750000, 40000, 2700);

        when(optimizer.optimize(anyList(), eq(44000.0), eq(3000.0)))
                .thenReturn(laResult)
                .thenReturn(chicagoResult);

        OptimizeResponse response = service.optimize(request);

        assertEquals(2, response.getSelectedOrderIds().size());
        assertEquals(750000, response.getTotalPayoutCents());
    }

    @Test
    void testOptimizeWithNoFeasibleCombination() {
        Truck truck = new Truck();
        truck.setId("truck-001");
        truck.setMaxWeightLbs(44000);
        truck.setMaxVolumeCuft(3000);

        Order order = new Order();
        order.setId("ord-001");
        order.setPayoutCents(250000);
        order.setWeightLbs(18000);
        order.setVolumeCuft(1200);
        order.setOrigin("Los Angeles, CA");
        order.setDestination("Dallas, TX");
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("2025-12-09");
        order.setHazmat(false);

        OptimizeRequest request = new OptimizeRequest();
        request.setTruck(truck);
        request.setOrders(Arrays.asList(order));

        KnapsackOptimizer.Result emptyResult = new KnapsackOptimizer.Result(
                List.of(), 0, 0, 0);
        when(optimizer.optimize(anyList(), eq(44000.0), eq(3000.0)))
                .thenReturn(emptyResult);

        OptimizeResponse response = service.optimize(request);

        assertEquals(0, response.getSelectedOrderIds().size());
        assertEquals(0, response.getTotalPayoutCents());
    }

    @Test
    void testUtilizationCalculation() {
        Truck truck = new Truck();
        truck.setId("truck-001");
        truck.setMaxWeightLbs(44000);
        truck.setMaxVolumeCuft(3000);

        Order order = new Order();
        order.setId("ord-001");
        order.setPayoutCents(250000);
        order.setWeightLbs(30000);
        order.setVolumeCuft(2100);
        order.setOrigin("Los Angeles, CA");
        order.setDestination("Dallas, TX");
        order.setPickupDate("2025-12-05");
        order.setDeliveryDate("2025-12-09");
        order.setHazmat(false);

        OptimizeRequest request = new OptimizeRequest();
        request.setTruck(truck);
        request.setOrders(Arrays.asList(order));

        KnapsackOptimizer.Result mockResult = new KnapsackOptimizer.Result(
                Arrays.asList("ord-001"), 250000, 30000, 2100);
        when(optimizer.optimize(anyList(), eq(44000.0), eq(3000.0)))
                .thenReturn(mockResult);

        OptimizeResponse response = service.optimize(request);

        double expectedWeightUtil = (30000.0 / 44000.0) * 100;
        double expectedVolumeUtil = (2100.0 / 3000.0) * 100;

        assertEquals(Math.round(expectedWeightUtil * 100.0) / 100.0, 
                response.getUtilizationWeightPercent());
        assertEquals(Math.round(expectedVolumeUtil * 100.0) / 100.0, 
                response.getUtilizationVolumePercent());
    }
}