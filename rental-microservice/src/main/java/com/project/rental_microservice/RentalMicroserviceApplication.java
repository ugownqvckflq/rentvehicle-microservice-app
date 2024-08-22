package com.project.rental_microservice;

import com.project.rolechecker.RoleCheckConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(RoleCheckConfig.class)
public class RentalMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentalMicroserviceApplication.class, args);
	}

}
