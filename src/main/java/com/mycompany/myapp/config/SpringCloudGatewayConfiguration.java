package com.mycompany.myapp.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SpringCloudGatewayConfiguration {

    private DiscoveryClient discoveryClient;

    public SpringCloudGatewayConfiguration(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routesBuilder = builder.routes();

        List<String> services = discoveryClient.getServices();
        List<List<ServiceInstance>> instances = services.stream()
            .map(discoveryClient::getInstances)
            .collect(Collectors.toList());

        routesBuilder = routesBuilder.route("direct-route",
            r -> r.remoteAddr("10.1.1.1", "10.10.1.1/24")
                .uri("https://downstream1"));


        return routesBuilder
                .build();
    }
}
