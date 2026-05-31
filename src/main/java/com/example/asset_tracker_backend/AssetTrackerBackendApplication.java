package com.example.asset_tracker_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AssetTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetTrackerBackendApplication.class, args);
	}

}
