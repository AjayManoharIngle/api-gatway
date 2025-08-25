package org.gateway;

import org.gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatwayApplication  implements ApplicationRunner{
	
	@Autowired
	private GatewayService gatewayService;

	public static void main(String[] args) {
		SpringApplication.run(GatwayApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		gatewayService.loadAllApiKeys();
	}
}
