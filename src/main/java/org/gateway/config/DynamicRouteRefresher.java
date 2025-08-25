package org.gateway.config;

import org.gateway.exception.ApiGatewayException;
import org.gateway.model.GlobalProperties;
import org.gateway.service.GatewayService;
import org.gateway.util.CommonUtil;
import org.gateway.util.DynamicRouteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class DynamicRouteRefresher {

    @Autowired
    private GatewayService gatewayService;
    
    @Autowired
    private DynamicRouteUtil dynamicRouteUtil;
    
    @Autowired
    private GlobalProperties globalProperties;
    
    @Autowired
    private CommonUtil commonUtil;

    @Scheduled(fixedDelay = 10000)
    public void refreshRoutes() throws ApiGatewayException {
    	if(commonUtil.isNotNullOrEmpty(globalProperties.getApiClients())) {
        	globalProperties.getApiClients().clear();
        }
        gatewayService.loadAllApiKeys();
        dynamicRouteUtil.dynamicRouteDefinition();
    }
}

