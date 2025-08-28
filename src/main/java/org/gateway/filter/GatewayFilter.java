package org.gateway.filter;

import java.util.Map;

import org.gateway.exception.ApiGatewayException;
import org.gateway.model.ApiClientDto;
import org.gateway.model.GlobalProperties;
import org.gateway.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class GatewayFilter implements GlobalFilter, Ordered{
	
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private CommonUtil commonUtil;

	@Override
	public int getOrder() {
		return -1;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		HttpHeaders headers = exchange.getRequest().getHeaders();
		ServerHttpRequest request = exchange.getRequest();
		String apiKey = headers.getFirst("x-api-key");
		
		validateAPIKeyBasedOnServices(validateServiceName(request),apiKey);
     
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (commonUtil.isNotNullOrEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // call auth service 
            boolean isValid = validateWithAuthService(token);
            if (!isValid) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Bearer Token");
            }
        }
		return chain.filter(exchange);
	}
	
	public static boolean matches(String pattern, String requestUri) {
        return pathMatcher.match(pattern, requestUri);
    }
	
	private String validateServiceName(ServerHttpRequest request) {
		String requestUri = getServiceNameFromPath(request);
		Map<String, String> routesContextPath = globalProperties.getRoutesContextPath(); 
		boolean matched = false;
		for (Map.Entry<String, String> entry : routesContextPath.entrySet()) {
	        String pattern = entry.getKey();       
	        String serviceName = entry.getValue(); 
	        if (matches(pattern, requestUri)) {
	            matched = true;
	            return serviceName;
	        }
	    }
		if (!matched) {
	        throw new ApiGatewayException("No route service context path found for request: " + requestUri);
	    }
		return null;
	}

	private void validateAPIKeyBasedOnServices(String serviceName, String apiKey) {
		Map<String, ApiClientDto> apiClients = globalProperties.getApiClients();
		ApiClientDto apiClient = apiClients.get(serviceName);
		if(apiClient != null && apiClient.isEnabledApiKey()) {
			if (commonUtil.isNullOrEmpty(apiKey)) {
	        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Api key can't be null or empty");
	        }
	        apiKey = apiKey.replaceAll("[\\n\\r]", "");
	        if (!apiClient.getApiKey().equals(apiKey)) {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid API Key");
	        }
		}
	}

	private String getServiceNameFromPath(ServerHttpRequest request) {
        String path = request.getPath().toString();
		String[] segments = path.split("/");
        String serviceName = (segments.length > 1) ? "/"+segments[1]+"/" : null;
        if (commonUtil.isNullOrEmpty(serviceName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Service name missing in path");
        }
		return serviceName;
	}

	private boolean validateWithAuthService(String token) {
		return true;
	}
}
