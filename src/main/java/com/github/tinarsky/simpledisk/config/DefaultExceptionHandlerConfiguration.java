package com.github.tinarsky.simpledisk.config;

import com.github.tinarsky.simpledisk.models.ClientError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultExceptionHandlerConfiguration {

	@Bean
	public ClientError badRequestErrorBean(){
		return new ClientError(400, "Validation Failed");
	}

	@Bean
	public ClientError notFoundErrorBean(){
		return new ClientError(404, "Item not found");
	}
}
