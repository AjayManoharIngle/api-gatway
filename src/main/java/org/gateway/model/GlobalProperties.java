package org.gateway.model;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class GlobalProperties {

	private Map<String,ApiClientDto> apiClients;
	private Map<String,String> routesContextPath;
}
