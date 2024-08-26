package com.proj.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthenticationService {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationService.class, args);
	}

}
