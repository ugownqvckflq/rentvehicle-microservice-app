package com.vehicle.vehicle_microservice;

import com.project.rolechecker.RoleCheckConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(RoleCheckConfig.class)
public class VehicleMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleMicroserviceApplication.class, args);
	}

}
