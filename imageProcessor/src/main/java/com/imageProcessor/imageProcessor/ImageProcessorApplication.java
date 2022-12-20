package com.imageProcessor.imageProcessor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.imageProcessor.imageProcessor.storage.StorageProperties;
import com.imageProcessor.imageProcessor.storage.StorageService;

// RestTemplate.class
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ImageProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageProcessorApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}