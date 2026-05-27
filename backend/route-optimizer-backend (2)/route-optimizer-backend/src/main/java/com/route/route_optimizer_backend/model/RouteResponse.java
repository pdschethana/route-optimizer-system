

package com.route.route_optimizer_backend.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RouteResponse {

    private String startLocation;
    private double startLat;
    private double startLng;
    private int totalStops;
    private double totalDistanceKm;
    private String estimatedTotalTime;
    private List<Job> optimizedRoute;
    private String message;
}