package com.trilobiet.doabooks.website.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc
@ComponentScan("com.trilobiet.doabooks")
public class WebConfiguration extends WebMvcConfigurerAdapter {
	
	// Forward requests for static resources to the servlet container's default servlet
	@Override
	public void configureDefaultServletHandling( DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
		registry
			.addResourceHandler("/static-assets/**","/robots.txt")
			.addResourceLocations("/static-assets/","/robots.txt")
			.setCacheControl(
				CacheControl.maxAge(3600, TimeUnit.SECONDS)
				.mustRevalidate()
			)        
			.resourceChain(true)
			.addResolver(new PathResourceResolver());
	}
	
}
