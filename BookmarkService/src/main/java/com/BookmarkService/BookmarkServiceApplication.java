package com.BookmarkService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaRepositories("com.BookmarkService.repositories")
public class BookmarkServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmarkServiceApplication.class, args);
	}

}
