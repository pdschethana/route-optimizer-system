package com.route.route_optimizer_backend.service;

import com.route.route_optimizer_backend.client.JobApiClient;
import com.route.route_optimizer_backend.model.Job;
import com.route.route_optimizer_backend.model.RouteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private JobApiClient jobApiClient;

    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        // Inject @Value fields manually
        ReflectionTestUtils.setField(routeService, "startLat", 7.2906);
        ReflectionTestUtils.setField(routeService, "startLng", 79.8653);
        ReflectionTestUtils.setField(routeService, "startName", "Sunquick Lanka Pvt Ltd");
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Job makeJob(String id, String jobId, String storeName, String lat, String lng) {
        Job job = new Job();
        job.setId(id);
        job.setJobId(jobId);
        job.setStoreName(storeName);
        job.setGeoLat(lat);
        job.setGeoLng(lng);
        job.setStatus("Pending");
        job.setJobType("Dispensing");
        job.setTerritory("Negombo");
        return job;
    }

    // ─── Tests ────────────────────────────────────────────────────────────────

    @Test
    void whenNoJobs_returnsEmptyRouteWithZeroDistance() {
        when(jobApiClient.fetchPendingJobs()).thenReturn(new ArrayList<>());

        RouteResponse response = routeService.getOptimizedRoute();

        assertEquals(0, response.getTotalStops());
        assertEquals(0.0, response.getTotalDistanceKm());
        assertEquals("0 min", response.getEstimatedTotalTime());
        assertTrue(response.getOptimizedRoute().isEmpty());
        assertEquals("No pending jobs found", response.getMessage());
    }

    @Test
    void whenOneJob_returnsSingleStopRoute() {
        Job job = makeJob("1", "JR#100001", "TEST STORE", "7.2940094", "79.8396836");
        when(jobApiClient.fetchPendingJobs()).thenReturn(List.of(job));

        RouteResponse response = routeService.getOptimizedRoute();

        assertEquals(1, response.getTotalStops());
        assertEquals(1, response.getOptimizedRoute().get(0).getVisitOrder());
        assertEquals("Route optimized successfully", response.getMessage());
    }

    @Test
    void whenMultipleJobs_visitOrderStartsAtOne() {
        List<Job> jobs = List.of(
                makeJob("65", "JR#100065", "DOLPHINE HOTEL",    "7.2940094", "79.8396836"),
                makeJob("63", "JR#100063", "GOLDI SANDS",       "7.2455846", "79.8418470"),
                makeJob("62", "JR#100062", "SURANGA CATERS",    "7.2373457", "79.8729596")
        );
        when(jobApiClient.fetchPendingJobs()).thenReturn(jobs);

        RouteResponse response = routeService.getOptimizedRoute();

        assertEquals(3, response.getTotalStops());
        // Visit orders must be 1, 2, 3 in sequence
        List<Job> route = response.getOptimizedRoute();
        assertEquals(1, route.get(0).getVisitOrder());
        assertEquals(2, route.get(1).getVisitOrder());
        assertEquals(3, route.get(2).getVisitOrder());
    }

    @Test
    void nearestJobIsVisitedFirst() {
        // DOLPHINE HOTEL is ~2.85 km from start, GOLDI SANDS is ~5.5 km — so Dolphine should be #1
        List<Job> jobs = List.of(
                makeJob("63", "JR#100063", "GOLDI SANDS",    "7.2455846", "79.8418470"),
                makeJob("65", "JR#100065", "DOLPHINE HOTEL", "7.2940094", "79.8396836")
        );
        when(jobApiClient.fetchPendingJobs()).thenReturn(jobs);

        RouteResponse response = routeService.getOptimizedRoute();

        assertEquals("DOLPHINE HOTEL", response.getOptimizedRoute().get(0).getStoreName());
        assertEquals("GOLDI SANDS",    response.getOptimizedRoute().get(1).getStoreName());
    }

    @Test
    void distanceFromPreviousIsNonNegative() {
        List<Job> jobs = List.of(
                makeJob("65", "JR#100065", "DOLPHINE HOTEL", "7.2940094", "79.8396836"),
                makeJob("63", "JR#100063", "GOLDI SANDS",    "7.2455846", "79.8418470"),
                makeJob("62", "JR#100062", "SURANGA CATERS", "7.2373457", "79.8729596")
        );
        when(jobApiClient.fetchPendingJobs()).thenReturn(jobs);

        RouteResponse response = routeService.getOptimizedRoute();

        for (Job job : response.getOptimizedRoute()) {
            assertTrue(job.getDistanceFromPrevious() >= 0,
                    "Distance should be non-negative for " + job.getStoreName());
        }
    }

    @Test
    void totalDistanceEqualsSumOfStopDistances() {
        List<Job> jobs = List.of(
                makeJob("65", "JR#100065", "DOLPHINE HOTEL", "7.2940094", "79.8396836"),
                makeJob("63", "JR#100063", "GOLDI SANDS",    "7.2455846", "79.8418470"),
                makeJob("62", "JR#100062", "SURANGA CATERS", "7.2373457", "79.8729596")
        );
        when(jobApiClient.fetchPendingJobs()).thenReturn(jobs);

        RouteResponse response = routeService.getOptimizedRoute();

        double sumOfStops = response.getOptimizedRoute().stream()
                .mapToDouble(Job::getDistanceFromPrevious)
                .sum();

        assertEquals(response.getTotalDistanceKm(),
                Math.round(sumOfStops * 100.0) / 100.0, 0.01);
    }

    @Test
    void estimatedTimeIsSetForEachStop() {
        List<Job> jobs = List.of(
                makeJob("65", "JR#100065", "DOLPHINE HOTEL", "7.2940094", "79.8396836"),
                makeJob("63", "JR#100063", "GOLDI SANDS",    "7.2455846", "79.8418470")
        );
        when(jobApiClient.fetchPendingJobs()).thenReturn(jobs);

        RouteResponse response = routeService.getOptimizedRoute();

        for (Job job : response.getOptimizedRoute()) {
            assertNotNull(job.getEstimatedTime(),
                    "Estimated time should not be null for " + job.getStoreName());
            assertFalse(job.getEstimatedTime().isBlank());
        }
    }

    @Test
    void startLocationIsCorrectlySet() {
        when(jobApiClient.fetchPendingJobs()).thenReturn(new ArrayList<>());

        RouteResponse response = routeService.getOptimizedRoute();

        assertEquals("Sunquick Lanka Pvt Ltd", response.getStartLocation());
        assertEquals(7.2906, response.getStartLat());
        assertEquals(79.8653, response.getStartLng());
    }

    @Test
    void noJobIsVisitedTwice() {
        List<Job> jobs = List.of(
                makeJob("65", "JR#100065", "DOLPHINE HOTEL", "7.2940094", "79.8396836"),
                makeJob("63", "JR#100063", "GOLDI SANDS",    "7.2455846", "79.8418470"),
                makeJob("62", "JR#100062", "SURANGA CATERS", "7.2373457", "79.8729596")
        );
        when(jobApiClient.fetchPendingJobs()).thenReturn(jobs);

        RouteResponse response = routeService.getOptimizedRoute();

        long uniqueIds = response.getOptimizedRoute().stream()
                .map(Job::getId)
                .distinct()
                .count();

        assertEquals(response.getOptimizedRoute().size(), uniqueIds,
                "Each job should appear exactly once in the route");
    }
}