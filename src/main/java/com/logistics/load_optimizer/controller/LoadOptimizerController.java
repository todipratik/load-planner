package com.logistics.load_optimizer.controller;

import com.logistics.load_optimizer.dto.OptimizeRequest;
import com.logistics.load_optimizer.dto.OptimizeResponse;
import com.logistics.load_optimizer.service.LoadOptimizerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class LoadOptimizerController {

    private final LoadOptimizerService loadOptimizerService;

    public LoadOptimizerController(LoadOptimizerService loadOptimizerService) {
        this.loadOptimizerService = loadOptimizerService;
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizeResponse> optimize(@Valid @RequestBody OptimizeRequest request) {
        OptimizeResponse response = loadOptimizerService.optimize(request);
        return ResponseEntity.ok(response);
    }
}