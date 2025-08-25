package org.gateway.service;

import org.gateway.exception.ApiGatewayException;

public interface GatewayService {

	void loadAllApiKeys() throws ApiGatewayException;
}
