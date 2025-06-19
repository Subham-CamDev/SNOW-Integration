package com.subham.serviceNowIntegration;

import com.subham.serviceNowIntegration.configuration.ServiceNowProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties(ServiceNowProperties.class)
public class ServiceNowIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceNowIntegrationApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate()
	{
		return new RestTemplate();
	}

	@Bean
	public HttpMessageConverters customConverters() {
		return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
	}

}
