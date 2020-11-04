package com.trilobiet.doabooks.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:ehcachebeans.xml")
@DependsOn({"cacheManager","sectionService"})
public class CacheUsageConfiguration {

} 
