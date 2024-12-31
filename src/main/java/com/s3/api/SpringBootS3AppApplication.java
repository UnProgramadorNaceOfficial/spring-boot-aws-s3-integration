package com.s3.api;

import com.s3.api.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;
import java.util.logging.Logger;


/**
 * Documentación: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3.html
 */

@SpringBootApplication
public class SpringBootS3AppApplication implements CommandLineRunner {

	private static final Logger LOGGER = Logger.getLogger(SpringBootS3AppApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(SpringBootS3AppApplication.class, args);
	}

	@Autowired
	private IS3Service s3Service;

	@Override
	public void run(String... args) throws Exception {
		List<Bucket> response = this.s3Service.getAllBuckets();
		LOGGER.info(String.valueOf(response));
	}
}
