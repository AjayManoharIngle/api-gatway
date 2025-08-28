package org.gateway.util;

import java.util.List;

import org.gateway.model.GlobalProperties;
import org.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class DynamicRouteUtil {
	
	@Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private GatewayService gatewayService;

	public void dynamicRouteDefinition() {
		List<RouteDefinition> routes = gatewayService.loadRoutes();
        routes.forEach(route -> {
            routeDefinitionWriter.delete(Mono.just(route.getId()))
                    .onErrorResume(e -> Mono.empty())
                    .then(routeDefinitionWriter.save(Mono.just(route)))
                    .subscribe();
        });
        publisher.publishEvent(new RefreshRoutesEvent(this));
	}
}
