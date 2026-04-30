package com.logistics.load_optimizer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoadOptimizerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {
            "test-cases/happy-path/01-single-order.json",
            "test-cases/happy-path/02-multiple-orders-best-combination.json",
            "test-cases/happy-path/03-hazmat-only.json",
            "test-cases/happy-path/04-hazmat-isolation.json",
            "test-cases/happy-path/05-different-routes-pick-best.json",
            "test-cases/happy-path/06-time-window-conflict.json",
            "test-cases/happy-path/07-hazmat-group-wins.json",
            "test-cases/happy-path/08-n22-performance.json"
    })
    void testHappyPathCases(String testCaseFile) throws Exception {
        String jsonPayload = readJsonFile(testCaseFile);

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.truckId").isNotEmpty())
                .andExpect(jsonPath("$.selectedOrderIds").isArray())
                .andExpect(jsonPath("$.totalPayoutCents").isNumber());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test-cases/constraint-validation/09-all-orders-exceed-weight.json",
            "test-cases/constraint-validation/10-all-orders-exceed-volume.json",
            "test-cases/constraint-validation/11-only-one-order-fits.json",
            "test-cases/constraint-validation/12-all-time-windows-conflict.json"
    })
    void testConstraintValidationCases(String testCaseFile) throws Exception {
        String jsonPayload = readJsonFile(testCaseFile);

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.truckId").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test-cases/validation-errors/13-missing-truck-id.json",
            "test-cases/validation-errors/14-negative-payout.json",
            "test-cases/validation-errors/15-delivery-before-pickup.json",
            "test-cases/validation-errors/16-invalid-date-format.json",
            "test-cases/validation-errors/17-duplicate-order-ids.json",
            "test-cases/validation-errors/18-empty-orders-list.json",
            "test-cases/validation-errors/19-missing-truck.json"
    })
    void testValidationErrorCases(String testCaseFile) throws Exception {
        String jsonPayload = readJsonFile(testCaseFile);

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test-cases/payload-too-large/20-23-orders.json"
    })
    void testPayloadTooLargeCases(String testCaseFile) throws Exception {
        String jsonPayload = readJsonFile(testCaseFile);

        mockMvc.perform(post("/api/v1/load-optimizer/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413));
    }

    private String readJsonFile(String filename) throws IOException {
        String resourcePath = "src/test/resources/" + filename;
        return new String(Files.readAllBytes(Paths.get(resourcePath)), StandardCharsets.UTF_8);
    }
}