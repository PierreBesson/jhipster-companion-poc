package com.mycompany.myapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GatewayRouter {
    private final Logger log = LoggerFactory.getLogger(GatewayRouter.class);

    private DiscoveryClient discoveryClient;

    private DiscoveryLocatorProperties discoveryLocatorProperties;

    public GatewayRouter(DiscoveryClient discoveryClient, DiscoveryLocatorProperties discoveryLocatorProperties) {
        this.discoveryClient = discoveryClient;
        this.discoveryLocatorProperties = discoveryLocatorProperties;
    }

// Naive implementation that doesn't refresh
//    @Bean
//    public RouteLocator routes(RouteLocatorBuilder builder) {
//        RouteLocatorBuilder.Builder routeLocatorBuilder = builder.routes();
//
//        discoveryClient.getServices().forEach(service -> {
//            List<ServiceInstance> instances = discoveryClient.getInstances(service);
//            instances.forEach(instance -> {
//                String instance_route = String.format("%s/%s", service, instance.getInstanceId()).toLowerCase();
//                routeLocatorBuilder.route(instance_route, r -> r.path("/gateway/" + instance_route + "/**")
//                    .filters(f -> f.rewritePath("/gateway/" + instance_route + "/(?<remaining>.*)", "/${remaining}")).uri(instance.getUri()));
//            });
//        });
//
//        return routeLocatorBuilder.build();
//    }
//
    @Bean
    public RouteDefinitionLocator jhipsterCompanionRouteDefinitionLocator(
        DiscoveryClient discoveryClient, DiscoveryLocatorProperties discoveryLocatorProperties) {

        return new JHipsterCompanionRouteDefinitionLocator(discoveryClient, discoveryLocatorProperties);
    }
}
