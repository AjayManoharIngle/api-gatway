package org.gateway.filter;

import java.util.List;

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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class GatewayFilter implements GlobalFilter, Ordered{

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
		
		if(commonUtil.isNullOrEmpty(globalProperties.getApiClients())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Api client can't be empty");
		}
		
		HttpHeaders headers = exchange.getRequest().getHeaders();
		ServerHttpRequest request = exchange.getRequest();
		
		String apiKey = headers.getFirst("x-api-key");
        if (commonUtil.isNullOrEmpty(apiKey)) {
        	throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Service name missing in path");
        }
        apiKey = apiKey.replaceAll("[\\n\\r]", "");
        
        String serviceName = getServiceNameFromPath(request);
        ApiClientDto apiClientDto = globalProperties.getApiClients().get(serviceName);
        if (commonUtil.isNullOrEmpty(apiClientDto.getApiKey()) || !getAllApiKeysOfServices().contains(apiClientDto.getApiKey())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid API Key");
        }
        
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

	private boolean validateWithAuthService(String token) {
		return true;
	}
	
	private List<String> getAllApiKeysOfServices(){
		return globalProperties.getApiClients().values().stream()
	            .map(ApiClientDto::getApiKey)
	            .toList();
	}

	private String getServiceNameFromPath(ServerHttpRequest request) {
        String path = request.getPath().toString();
		String[] segments = path.split("/");
        String serviceName = (segments.length > 1) ? segments[1] : null;
        if (commonUtil.isNullOrEmpty(serviceName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Service name missing in path");
        }
		return serviceName;
	}
	
	
}
