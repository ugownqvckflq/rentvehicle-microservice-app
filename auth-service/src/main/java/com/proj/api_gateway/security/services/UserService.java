package com.proj.api_gateway.security.services;

import com.proj.api_gateway.entity.Role;
import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.repository.RoleRepository;
import com.proj.api_gateway.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public void registerUserWithCredentials(String username, String email, String password, Set<String> strRoles) {
        User user = new User(username, passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();

        logger.info(roleRepository.findByName("ROLE_ADMIN").toString());
        strRoles.forEach(role -> {
            if (role.equals("ROLE_ADMIN")) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                logger.info(adminRole.toString());
                roles.add(adminRole);
            } else {
                Role userRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
            }
        });
        user.setEmail(email);
        user.setRoles(roles);
        userRepository.save(user);
    }

    public User registerSimpleUser(String username, String email, String password) {
        User user = new User(username, passwordEncoder.encode(password));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        logger.info("---------------------------------");
        logger.info(userRole.toString());
        user.setRoles(Collections.singleton(userRole));
        user.setEmail(email);
        return userRepository.save(user);
    }



    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user;
    }
}