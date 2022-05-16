package com.billy5804.iotnoisedetectionbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Config for allowing access to static frontend files.
@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
	@Value("${spring.resources.static-locations}")
	String resourceLocations;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/favicon.ico").addResourceLocations(resourceLocations + "favicon.ico");
		registry.addResourceHandler("/assets/**").addResourceLocations(resourceLocations + "assets/");
	}
}