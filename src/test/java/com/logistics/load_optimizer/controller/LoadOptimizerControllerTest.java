package com.logistics.load_optimizer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class LoadOptimizerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testOptimizeWithValidRequest() throws Exception {
        String request = """
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": [
                    {"id": "ord-001", "payoutCents": 250000, "weightLbs": 18000, "volumeCuft": 1200, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-05", "deliveryDate": "2025-12-09", "isHazmat": false}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.truckId").value("truck-001"))
                .andExpect(jsonPath("$.selectedOrderIds[0]").value("ord-001"))
                .andExpect(jsonPath("$.totalPayoutCents").value(250000));
    }

    @Test
    void testOptimizeWithMissingTruck() throws Exception {
        String request = """
                {
                  "orders": [
                    {"id": "ord-001", "payoutCents": 250000, "weightLbs": 18000, "volumeCuft": 1200, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-05", "deliveryDate": "2025-12-09", "isHazmat": false}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testOptimizeWithEmptyOrderList() throws Exception {
        String request = """
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": []
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testOptimizeWithInvalidDateFormat() throws Exception {
        String request = """
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": [
                    {"id": "ord-001", "payoutCents": 250000, "weightLbs": 18000, "volumeCuft": 1200, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "05-12-2025", "deliveryDate": "09-12-2025", "isHazmat": false}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testOptimizeWithDeliveryBeforePickup() throws Exception {
        String request = """
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": [
                    {"id": "ord-001", "payoutCents": 250000, "weightLbs": 18000, "volumeCuft": 1200, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-09", "deliveryDate": "2025-12-05", "isHazmat": false}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testOptimizeWithDuplicateOrderIds() throws Exception {
        String request = """
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": [
                    {"id": "ord-001", "payoutCents": 250000, "weightLbs": 18000, "volumeCuft": 1200, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-05", "deliveryDate": "2025-12-09", "isHazmat": false},
                    {"id": "ord-001", "payoutCents": 180000, "weightLbs": 12000, "volumeCuft": 900, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-04", "deliveryDate": "2025-12-10", "isHazmat": false}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testOptimizeWithTooManyOrders() throws Exception {
        StringBuilder orders = new StringBuilder();
        for (int i = 1; i <= 23; i++) {
            if (i > 1) orders.append(",");
            orders.append(String.format("""
                    {"id": "ord-%03d", "payoutCents": 100000, "weightLbs": 1000, "volumeCuft": 100, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-01", "deliveryDate": "2025-12-10", "isHazmat": false}
                    """, i));
        }

        String request = String.format("""
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": [%s]
                }
                """, orders.toString());

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413));
    }

    @Test
    void testOptimizeWithNegativePayout() throws Exception {
        String request = """
                {
                  "truck": {"id": "truck-001", "maxWeightLbs": 44000, "maxVolumeCuft": 3000},
                  "orders": [
                    {"id": "ord-001", "payoutCents": -250000, "weightLbs": 18000, "volumeCuft": 1200, "origin": "Los Angeles, CA", "destination": "Dallas, TX", "pickupDate": "2025-12-05", "deliveryDate": "2025-12-09", "isHazmat": false}
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}