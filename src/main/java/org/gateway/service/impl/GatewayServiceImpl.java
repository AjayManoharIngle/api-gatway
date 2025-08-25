package org.gateway.service.impl;

import java.util.HashMap;
import java.util.List;

import org.gateway.entity.ApiClient;
import org.gateway.exception.ApiGatewayException;
import org.gateway.model.ApiClientDto;
import org.gateway.model.GlobalProperties;
import org.gateway.repository.ApiClientRepository;
import org.gateway.service.GatewayService;
import org.gateway.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatewayServiceImpl implements GatewayService{
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private ApiClientRepository apiClientRepository;
	
	@Autowired
	private CommonUtil commonUtil;

	@Override
	public void loadAllApiKeys() throws ApiGatewayException {
		List<ApiClient> apiClient = apiClientRepository.findAll();
		if(commonUtil.isNotNullOrEmpty(apiClient)) {
			if(commonUtil.isNullOrEmpty(globalProperties.getApiClients())) {
				globalProperties.setApiClients(new HashMap<>());
			}
			apiClient.forEach(client -> globalProperties.getApiClients().put(client.getServiceContextPath(),new ApiClientDto(client.getApiKey(),client.getServiceId())));
		}
	}
}
