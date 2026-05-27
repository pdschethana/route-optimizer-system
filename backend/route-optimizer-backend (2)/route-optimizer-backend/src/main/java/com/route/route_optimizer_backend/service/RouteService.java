

package com.route.route_optimizer_backend.service;

import com.route.route_optimizer_backend.client.JobApiClient;
import com.route.route_optimizer_backend.model.Job;
import com.route.route_optimizer_backend.model.RouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final JobApiClient jobApiClient;

    @Value("${start.location.lat}")
    private double startLat;

    @Value("${start.location.lng}")
    private double startLng;

    @Value("${start.location.name}")
    private String startName;

    private static final double AVG_SPEED_KMH = 40.0;

    public RouteResponse getOptimizedRoute() {
        List<Job> jobs = jobApiClient.fetchPendingJobs();

        if (jobs.isEmpty()) {
            return RouteResponse.builder()
                    .startLocation(startName)
                    .startLat(startLat)
                    .startLng(startLng)
                    .totalStops(0)
                    .totalDistanceKm(0)
                    .estimatedTotalTime("0 min")
                    .optimizedRoute(new ArrayList<>())
                    .message("No pending jobs found")
                    .build();
        }

        List<Job> optimized = nearestNeighbor(jobs);
        double totalDistance = calculateTotalDistance(optimized);
        double totalHours = totalDistance / AVG_SPEED_KMH;

        return RouteResponse.builder()
                .startLocation(startName)
                .startLat(startLat)
                .startLng(startLng)
                .totalStops(optimized.size())
                .totalDistanceKm(Math.round(totalDistance * 100.0) / 100.0)
                .estimatedTotalTime(formatTime(totalHours))
                .optimizedRoute(optimized)
                .message("Route optimized successfully")
                .build();
    }

    private List<Job> nearestNeighbor(List<Job> jobs) {
        List<Job> unvisited = new ArrayList<>(jobs);
        List<Job> route = new ArrayList<>();

        double currentLat = startLat;
        double currentLng = startLng;
        int order = 1;

        while (!unvisited.isEmpty()) {
            Job nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (Job job : unvisited) {
                double lat = Double.parseDouble(job.getGeoLat());
                double lng = Double.parseDouble(job.getGeoLng());
                double distance = haversine(currentLat, currentLng, lat, lng);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = job;
                }
            }

            if (nearest != null) {
                double rounded = Math.round(minDistance * 100.0) / 100.0;
                nearest.setDistanceFromPrevious(rounded);
                nearest.setEstimatedTime(formatTime(minDistance / AVG_SPEED_KMH));
                nearest.setVisitOrder(order++);
                route.add(nearest);
                currentLat = Double.parseDouble(nearest.getGeoLat());
                currentLng = Double.parseDouble(nearest.getGeoLng());
                unvisited.remove(nearest);
            }
        }

        return route;
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double calculateTotalDistance(List<Job> route) {
        return route.stream()
                .mapToDouble(Job::getDistanceFromPrevious)
                .sum();
    }

    private String formatTime(double hours) {
        int totalMinutes = (int) Math.round(hours * 60);
        if (totalMinutes < 60) return totalMinutes + " min";
        return (totalMinutes / 60) + "h " + (totalMinutes % 60) + "min";
    }
}