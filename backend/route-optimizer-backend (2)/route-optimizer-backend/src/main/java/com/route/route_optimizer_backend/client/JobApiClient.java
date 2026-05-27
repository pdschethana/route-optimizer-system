/*package com.route.route_optimizer_backend.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.route.route_optimizer_backend.model.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${job.api.url}")
    private String jobApiUrl;

    public List<Job> fetchPendingJobs() {
        try {
            String response = restTemplate.getForObject(jobApiUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.get("data");

            List<Job> jobs = new ArrayList<>();
            for (JsonNode node : dataArray) {
                Job job = objectMapper.treeToValue(node, Job.class);
                if ("Pending".equalsIgnoreCase(job.getStatus())) {
                    jobs.add(job);
                }
            }
            log.info("Fetched {} pending jobs from API", jobs.size());
            return jobs;

        } catch (Exception e) {
            log.error("Failed to fetch jobs from API: {}", e.getMessage());
            throw new RuntimeException("Could not fetch jobs from external API", e);
        }
    }
}*/

package com.route.route_optimizer_backend.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.route.route_optimizer_backend.model.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${job.api.url}")
    private String jobApiUrl;

    public List<Job> fetchPendingJobs() {
        try {
            String response = restTemplate.getForObject(jobApiUrl, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode dataArray = root.get("data");

            List<Job> jobs = new ArrayList<>();
            for (JsonNode node : dataArray) {
                Job job = objectMapper.treeToValue(node, Job.class);
                if ("Pending".equalsIgnoreCase(job.getStatus())) {
                    jobs.add(job);
                }
            }
            log.info("Fetched {} pending jobs from API", jobs.size());
            return jobs;

        } catch (Exception e) {
            log.warn("External API unavailable ({}). Using mock data.", e.getMessage());
            return getMockJobs();
        }
    }

    private List<Job> getMockJobs() {
        log.info("Loading 4 mock jobs as fallback");

        String[][] data = {
                {"65", "JR#100065", "Dispensing",   "Pending", "2", "DOLPHINE HOTEL",          "7.2940094", "79.8396836", "Negombo"},
                {"64", "JR#100064", "Registration", "Pending", "2", "DOLPHINE HOTEL",          "7.2940094", "79.8396836", "Negombo"},
                {"63", "JR#100063", "Power",        "Pending", "5", "GOLDI SANDS",             "7.2455846", "79.8418470", "Negombo"},
                {"62", "JR#100062", "Hardware",     "Pending", "8", "SURANGA CATERS- NEGAMBO", "7.2373457", "79.8729596", "Negombo"}
        };

        List<Job> jobs = new ArrayList<>();
        for (String[] d : data) {
            Job job = new Job();
            job.setId(d[0]);
            job.setJobId(d[1]);
            job.setJobType(d[2]);
            job.setStatus(d[3]);
            job.setVendorId(d[4]);
            job.setStoreName(d[5]);
            job.setGeoLat(d[6]);
            job.setGeoLng(d[7]);
            job.setTerritory(d[8]);
            jobs.add(job);
        }
        return jobs;
    }
}