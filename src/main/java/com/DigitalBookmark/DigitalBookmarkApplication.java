package com.DigitalBookmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableJpaRepositories("com.DigitalBookmark.repositories")
public class DigitalBookmarkApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBookmarkApplication.class, args);
	}

}
