package com.proj.api_gateway.controller;



import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/grant-admin/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> grantAdminRole(@PathVariable Long id) {
        return ResponseEntity.ok("You added admin!");
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
        // Поиск пользователя по имени
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Возврат найденного пользователя
        return ResponseEntity.ok(user);
    }

}