package org.gateway.config;

import org.gateway.util.DynamicRouteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class DynamicRouteLocatorConfig {
	
	@Autowired
	private DynamicRouteUtil dynamicRouteUtil;
	
    @EventListener(ApplicationReadyEvent.class)
    public void dynamicRoutes() {
    	dynamicRouteUtil.dynamicRouteDefinition();
    }
}
