package com.mycompany.myapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller for viewing service discovery data.
 */
@RestController
@RequestMapping("/api")
public class ServiceDiscoveryResource {

    private final Logger log = LoggerFactory.getLogger(ServiceDiscoveryResource.class);

    private final DiscoveryClient discoveryClient;

    public ServiceDiscoveryResource(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * GET /service-discovery/instances : Get applications instances
     * registered to the service discovery provider.
     */
    @GetMapping("/services/instances")
    public ResponseEntity<Map<String, List<ServiceInstance>>> instances() {
        List<String> services = discoveryClient.getServices();
        Map<String, List<ServiceInstance>> instances = services.stream()
            .collect(Collectors.toMap(Function.identity(), discoveryClient::getInstances));
        return new ResponseEntity<>(instances, HttpStatus.OK);
    }
}
