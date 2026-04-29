package com.logistics.load_optimizer.controller;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import com.logistics.load_optimizer.dto.OptimizeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class LoadOptimizerController {

    @PostMapping("/optimize")
    public ResponseEntity<OptimizeResponse> optimize(@Valid @RequestBody OptimizeRequest request) {

        // Stub response — we'll plug in real logic soon
        OptimizeResponse response = OptimizeResponse.builder()
                .truckId(request.getTruck().getId())
                .selectedOrderIds(List.of())
                .totalPayoutCents(0)
                .totalWeightLbs(0)
                .totalVolumeCuft(0)
                .utilizationWeightPercent(0)
                .utilizationVolumePercent(0)
                .build();

        return ResponseEntity.ok(response);
    }
}