package com.proj.api_gateway;

import com.project.rolechecker.RoleCheckConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@SpringBootApplication
@Import(RoleCheckConfig.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthenticationService {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationService.class, args);
	}

}
