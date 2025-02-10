package com.example.Shares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SharesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharesApplication.class, args);
	}

}
