package com.project.notification_service.service;

import com.project.notification_service.dto.Rental;
import com.project.notification_service.entity.User;
import com.project.notification_service.entity.Vehicle;
import com.project.notification_service.repository.UserRepository;
import com.project.notification_service.repository.VehicleRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final ResourceLoader resourceLoader;
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public NotificationService(ResourceLoader resourceLoader, JavaMailSender emailSender,
                               UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.resourceLoader = resourceLoader;
        this.emailSender = emailSender;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    private String loadTemplate(String templatePath) throws IOException {
        Resource resource = resourceLoader.getResource(templatePath);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private String generateEmailContent(String templatePath, Map<String, String> variables) throws IOException {
        String template = loadTemplate(templatePath);
        StringSubstitutor substitutor = new StringSubstitutor(variables);
        return substitutor.replace(template);
    }

    public void sendRentalStartEmail(Rental rental) {
        try {
            User user = userRepository.findById(rental.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            String userName = user.getUsername();
            String userEmail = user.getEmail();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedStartTime = rental.getStartTime().format(formatter);

            Map<String, String> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("rentalId", String.valueOf(rental.getId()));
            variables.put("licensePlate", vehicle.getLicensePlate()); // Номерной знак
            variables.put("model", vehicle.getModel()); // Модель
            variables.put("startTime", formattedStartTime);

            String content = generateEmailContent("classpath:templates/rental-start.html", variables);
            sendEmail(userEmail, "Rental Started", content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRentalEndEmail(Rental rental) {
        try {
            User user = userRepository.findById(rental.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            String userName = user.getUsername();
            String userEmail = user.getEmail();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedStartTime = rental.getStartTime().format(formatter);
            String formattedEndTime = rental.getEndTime().format(formatter);

            Map<String, String> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("rentalId", String.valueOf(rental.getId()));
            variables.put("licensePlate", vehicle.getLicensePlate()); // Номерной знак
            variables.put("model", vehicle.getModel()); // Модель
            variables.put("startTime", formattedStartTime);
            variables.put("endTime", formattedEndTime);
            variables.put("duration", rental.getDuration());

            String content = generateEmailContent("classpath:templates/rental-end.html", variables);
            sendEmail(userEmail, "Rental Ended", content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML content

            emailSender.send(mimeMessage);
            System.out.println("Email sent to: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
