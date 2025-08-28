package org.gateway.service.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gateway.entity.ApiClient;
import org.gateway.entity.RouteDefinitionEntity;
import org.gateway.exception.ApiGatewayException;
import org.gateway.model.ApiClientDto;
import org.gateway.model.GlobalProperties;
import org.gateway.repository.ApiClientRepository;
import org.gateway.repository.RouteDefinitionRepository;
import org.gateway.repository.RouteFilterRepository;
import org.gateway.repository.RoutePredicateRepository;
import org.gateway.service.GatewayService;
import org.gateway.util.CommonUtil;
import org.gateway.util.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GatewayServiceImpl implements GatewayService{
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private ApiClientRepository apiClientRepository;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private RouteDefinitionRepository routeDefinitionRepository;
	
	@Autowired
	private RoutePredicateRepository routePredicateRepository;
	
	@Autowired
	private RouteFilterRepository routeFilterRepository;
	
	@Autowired
	private RouteValidator routeValidator;
	
	ObjectMapper objectMapper =  new ObjectMapper();

	@Override
	public void loadConfigurations() throws ApiGatewayException {
		List<ApiClient> apiClient = apiClientRepository.findAllByActiveTrue();
		if(commonUtil.isNotNullOrEmpty(apiClient)) {
			if(commonUtil.isNullOrEmpty(globalProperties.getApiClients())) {
				globalProperties.setApiClients(new HashMap<>());
			}
			apiClient.forEach(client -> globalProperties.getApiClients().put(client.getServiceId(),new ApiClientDto(client.getApiKey(),client.isActiveAPIKey())));
		}
	}

	@Override
	public List<RouteDefinition> loadRoutes() throws ApiGatewayException{
		 Map<String,ApiClientDto> apiClients = globalProperties.getApiClients();
		 List<RouteDefinitionEntity> routes = routeDefinitionRepository.findAll();
		 if(apiClients.isEmpty()) {
			 throw new ApiGatewayException("Service not registered with API clients");
		 }
		 if(routes.isEmpty() && !apiClients.isEmpty()) {
			 throw new ApiGatewayException("Routes not defined for api clients");
		 }
		 return routes.stream()
			        .filter(route -> route.isActive() && validateRouteServiceWithApiClients(apiClients, route.getUri()))
			        .map(this::convertToRouteDefinition)
			        .toList();
	}
	
	private boolean validateRouteServiceWithApiClients(Map<String, ApiClientDto> apiClients, String uri) throws ApiGatewayException {
		if (commonUtil.isNullOrEmpty(uri)) {
	        throw new ApiGatewayException("URI cannot be null or empty");
	    }
	    try {
	        String serviceName = uri.replaceFirst("^lb://", "").split("/")[0];
	        if (apiClients.containsKey(serviceName)) {
	            return true;
	        } else {
	            throw new ApiGatewayException("Service not registered with API clients: " + serviceName);
	        }
	    } catch (Exception ex) {
	        throw new ApiGatewayException("Error validating service from URI: " + uri);
	    }
	}

	private RouteDefinition convertToRouteDefinition(RouteDefinitionEntity routeEntity) {
        RouteDefinition rd = new RouteDefinition();
        rd.setId(routeEntity.getRouteId());
        rd.setOrder(routeEntity.getOrder());
        rd.setUri(URI.create(routeEntity.getUri()));
        rd.setEnabled(routeEntity.isActive());
        
        List<PredicateDefinition> preds = routePredicateRepository.findAll().stream()
                .filter(p -> p.getRoute().getId().equals(routeEntity.getId()))
                .map(p -> {
                    Map<String, String> argMap = parseJson(p.getJsonValue());
                    PredicateDefinition def = new PredicateDefinition();
                    def.setName(p.getName());
                    def.setArgs(argMap);
                    if (!routeValidator.validatePredicate(p.getName(), argMap)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Invalid predicate config: " + p.getName());
                    }
                    return def;
                }).toList();
        rd.setPredicates(preds);

        List<FilterDefinition> filts = routeFilterRepository.findAll().stream()
                .filter(f -> f.getRoute().getId().equals(routeEntity.getId()))
                .map(f -> {
                    Map<String, String> argMap = parseJson(f.getJsonValue());
                    FilterDefinition def = new FilterDefinition();
                    def.setName(f.getName());
                    def.setArgs(argMap);
                    if (!routeValidator.validateFilter(f.getName(), argMap)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Invalid filter config: " + f.getName());
                    }
                    return def;
                }).toList();
        rd.setFilters(filts);
        mapRoutesContextPathToServices(rd);
        return rd;
    }

	private void mapRoutesContextPathToServices(RouteDefinition rd) {
		Map<String, String> routesConfig = globalProperties.getRoutesContextPath();
		if(routesConfig == null) {
			routesConfig = new HashMap<>();
		}
		String path = rd.getPredicates().stream()
	            .filter(p -> "Path".equalsIgnoreCase(p.getName()))
	            .flatMap(p -> p.getArgs().values().stream())
	            .findFirst()
	            .orElse(null);
		routesConfig.put(path,rd.getId());
		globalProperties.setRoutesContextPath(routesConfig);
	}

	private Map<String, String> parseJson(String json) {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        return mapper.readValue(json, new TypeReference<Map<String, String>>() {});
	    } catch (Exception e) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "Invalid JSON args: " + json, e);
	    }
	}
}
