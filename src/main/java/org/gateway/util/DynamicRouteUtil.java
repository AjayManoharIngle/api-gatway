package org.gateway.util;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.gateway.model.ApiClientDto;
import org.gateway.model.GlobalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class DynamicRouteUtil {
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
	
	@Autowired
	private ApplicationEventPublisher publisher;

	public void dynamicRouteDefinition() {
		Map<String, ApiClientDto> apiClients = globalProperties.getApiClients();
        if (commonUtil.isNotNullOrEmpty(apiClients)) {
        	 apiClients.forEach((contextPath, apiClientDto) -> {
                 String routeId = apiClientDto.getServiceName() + "-route";
                 addOrUpdateRoute(routeId,contextPath,apiClientDto.getServiceName());
             });
        }
	}
	
	public void addOrUpdateRoute(String routeId, String path, String serviceName) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(routeId);

        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName("Path");
        predicate.addArg("pattern", "/" + path + "/**");
        routeDefinition.setPredicates(List.of(predicate));

        FilterDefinition filter = new FilterDefinition();
        filter.setName("StripPrefix");
        filter.addArg("_genkey_0", "1");
        routeDefinition.setFilters(List.of(filter));

        routeDefinition.setUri(URI.create("lb://" + serviceName));

        routeDefinitionWriter.delete(Mono.just(routeId))
                .onErrorResume(e -> Mono.empty()) 
                .then(routeDefinitionWriter.save(Mono.just(routeDefinition)))
                .subscribe();

        publisher.publishEvent(new RefreshRoutesEvent(this));
    }
}
