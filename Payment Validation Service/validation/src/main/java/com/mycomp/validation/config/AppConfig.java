package com.mycomp.validation.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {
	
	@Bean
	@LoadBalanced
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
	
	@Bean
	RestClient restClient(RestClient.Builder builder) {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(100);
		
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.evictIdleConnections(TimeValue.ofSeconds(30))
				.build(); 
		
		HttpComponentsClientHttpRequestFactory requestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectionRequestTimeout(10000); // 10 seconds - time to get connection from pool
		requestFactory.setConnectionRequestTimeout(10000);  // 10 seconds - time to establish TCP connection
		requestFactory.setReadTimeout(15000);     // 15 seconds - time waiting for server response
		
		return builder
				.requestFactory(requestFactory)
				.build();
	}
	
	@Bean
    ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
	    mapper.getConfiguration()
	            .setMatchingStrategy(MatchingStrategies.STRICT)  // exact field name matching
	            .setSkipNullEnabled(true);                       // don't overwrite with nulls
	    return mapper;
    }

}
