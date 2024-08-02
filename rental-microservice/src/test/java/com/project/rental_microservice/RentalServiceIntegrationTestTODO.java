package com.project.rental_microservice;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.repository.RentalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class RentalServiceIntegrationTestTODO {

    @Autowired
    private RentalRepository rentalRepository;

    @MockBean
    private KafkaTemplate<String, Rental> kafkaTemplate;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("butilka")
            .withUsername("postgres")
            .withPassword("12345");

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer();

    @BeforeEach
    public void setUp() {
        rentalRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        rentalRepository.deleteAll();
    }

    @Test
    public void testSaveRental() {
        Rental rental = new Rental();
        rental.setUserId(1L);
        rental.setVehicleId(1L);
        rental.setStartTime(LocalDateTime.now());
        Rental savedRental = rentalRepository.save(rental);

        assertThat(savedRental).isNotNull();
        assertThat(savedRental.getId()).isNotNull();
    }
}
