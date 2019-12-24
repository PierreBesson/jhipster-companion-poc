package com.mycompany.myapp.web.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.core.style.ToStringCreator;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.Map;
import java.util.function.Predicate;

import static org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory.REGEXP_KEY;
import static org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory.REPLACEMENT_KEY;
import static org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory.PATTERN_KEY;
import static org.springframework.cloud.gateway.support.NameUtils.normalizeFilterFactoryName;
import static org.springframework.cloud.gateway.support.NameUtils.normalizeRoutePredicateName;

/**
 * Inspired by @link{org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator}
 */
public class JHipsterCompanionRouteDefinitionLocator implements RouteDefinitionLocator {

    private static final Log log = LogFactory
        .getLog(DiscoveryClientRouteDefinitionLocator.class);
    public static final String GATEWAY_PATH = "/gateway";

    private final DiscoveryClient discoveryClient;

    private final DiscoveryLocatorProperties properties;

    public JHipsterCompanionRouteDefinitionLocator(DiscoveryClient discoveryClient,
                                                   DiscoveryLocatorProperties properties) {
        this.discoveryClient = discoveryClient;
        this.properties = properties;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(discoveryClient.getServices())
            .map(discoveryClient::getInstances)
            .filter(instances -> !instances.isEmpty())
            .map(instances -> instances.get(0))
            .map(instance -> {
                String instance_route = String.format("%s/%s", instance.getServiceId(),
                    instance.getInstanceId()).toLowerCase();


                RouteDefinition routeDefinition = new RouteDefinition();
                routeDefinition.setId(instance_route);
                routeDefinition.setUri(instance.getUri());

                final ServiceInstance instanceForEval = new JHipsterCompanionRouteDefinitionLocator.DelegatingServiceInstance(
                    instance, properties);

                for (PredicateDefinition original : this.properties.getPredicates()) {
                    // add a predicate that matches the url at /gateway/$instance_route/**
                    PredicateDefinition predicate = new PredicateDefinition();
                    predicate.setName(normalizeRoutePredicateName(PathRoutePredicateFactory.class));
                    predicate.addArg(PATTERN_KEY, "/gateway/" + instance_route + "/**");
                    routeDefinition.getPredicates().add(predicate);
                }

                for (FilterDefinition original : this.properties.getFilters()) {
                    // add a filter that remove /gateway/$instance_route/ in downstream service call path                    FilterDefinition filter = new FilterDefinition();

                    FilterDefinition filter = new FilterDefinition();
                    filter.setName(normalizeFilterFactoryName(RewritePathGatewayFilterFactory.class));
                    String regex = "/gateway/" + instance_route + "/(?<remaining>.*)";
                    String replacement = "/${remaining}";
                    filter.addArg(REGEXP_KEY, regex);
                    filter.addArg(REPLACEMENT_KEY, replacement);
                    routeDefinition.getFilters().add(filter);
                }

                return routeDefinition;
            });
    }

    private static class DelegatingServiceInstance implements ServiceInstance {

        final ServiceInstance delegate;

        private final DiscoveryLocatorProperties properties;

        private DelegatingServiceInstance(ServiceInstance delegate,
                                          DiscoveryLocatorProperties properties) {
            this.delegate = delegate;
            this.properties = properties;
        }

        @Override
        public String getServiceId() {
            if (properties.isLowerCaseServiceId()) {
                return delegate.getServiceId().toLowerCase();
            }
            return delegate.getServiceId();
        }

        @Override
        public String getHost() {
            return delegate.getHost();
        }

        @Override
        public int getPort() {
            return delegate.getPort();
        }

        @Override
        public boolean isSecure() {
            return delegate.isSecure();
        }

        @Override
        public URI getUri() {
            return delegate.getUri();
        }

        @Override
        public Map<String, String> getMetadata() {
            return delegate.getMetadata();
        }

        @Override
        public String getScheme() {
            return delegate.getScheme();
        }

        @Override
        public String toString() {
            return new ToStringCreator(this).append("delegate", delegate)
                .append("properties", properties).toString();
        }

    }

}
