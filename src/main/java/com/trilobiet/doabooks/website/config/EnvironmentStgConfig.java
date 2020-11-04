package com.trilobiet.doabooks.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("stg")
@PropertySource("classpath:app.stg.properties")
public class EnvironmentStgConfig {
}

