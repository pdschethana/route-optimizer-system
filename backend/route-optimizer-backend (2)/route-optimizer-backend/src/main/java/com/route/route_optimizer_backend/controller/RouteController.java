package com.route.route_optimizer_backend.controller;

import com.route.route_optimizer_backend.model.RouteResponse;
import com.route.route_optimizer_backend.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "${cors.allowed.origin}")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping("/optimize")
    public ResponseEntity<RouteResponse> getOptimizedRoute() {
        RouteResponse response = routeService.getOptimizedRoute();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Route Optimizer API is running ✅");
    }
}