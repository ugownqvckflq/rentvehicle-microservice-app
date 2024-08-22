package com.project.rental_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.service.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@SpringBootTest
public class RentalControllerIntegrationTest { //TODO testsss


}
