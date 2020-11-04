package com.trilobiet.doabooks.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")
@PropertySource("classpath:app.dev.properties")
public class EnvironmentDevConfig {
}

