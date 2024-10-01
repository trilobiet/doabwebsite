package com.trilobiet.doabooks.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(
	/* We use MySQL search in oapensitesearch, not Mongo Search, but mongo-java-driver 
	 * in oapensitesearch tries to connect anyway, unless we tell it not to. */	
	exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}
)
public class Application {

	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
	}
}
