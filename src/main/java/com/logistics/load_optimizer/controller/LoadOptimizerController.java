package com.logistics.load_optimizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/load-optimizer")
public class LoadOptimizerController {

    @PostMapping("/optimize")
    public ResponseEntity<String> optimize() {
        return ResponseEntity.ok("Load optimizer is working!");
    }
}