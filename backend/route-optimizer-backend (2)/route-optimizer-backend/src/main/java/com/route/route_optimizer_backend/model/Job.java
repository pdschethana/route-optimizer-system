

package com.route.route_optimizer_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Job {

    private String id;

    @JsonProperty("job_id")
    private String jobId;

    @JsonProperty("job_type")
    private String jobType;

    private String status;

    @JsonProperty("vendor_id")
    private String vendorId;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("geo_lat")
    private String geoLat;

    @JsonProperty("geo_lng")
    private String geoLng;

    private String territory;

    private double distanceFromPrevious;
    private int visitOrder;
    private String estimatedTime;
}