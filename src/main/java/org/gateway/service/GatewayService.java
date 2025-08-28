package org.gateway.service;

import java.util.List;

import org.gateway.exception.ApiGatewayException;
import org.springframework.cloud.gateway.route.RouteDefinition;

public interface GatewayService {

	void loadConfigurations() throws ApiGatewayException;
	public List<RouteDefinition> loadRoutes() throws ApiGatewayException;
}
