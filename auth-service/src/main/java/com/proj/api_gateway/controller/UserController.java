package com.proj.api_gateway.controller;

import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.security.services.UserService;
import com.project.rolechecker.RoleCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/grant-admin/{id}")
    @RoleCheck("ROLE_ADMIN")
    public ResponseEntity<?> grantAdminRole(@PathVariable Long id) {
        userService.grantAdminRole(id);
        return ResponseEntity.ok("You added admin!");
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }


    @PostMapping("/ban/{id}")
    @RoleCheck("ROLE_ADMIN")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("User has been banned!");
    }

    @PostMapping("/unban/{id}")
    @RoleCheck("ROLE_ADMIN")
    public ResponseEntity<?> unBanUser(@PathVariable Long id) {
        userService.unBanUser(id);
        return ResponseEntity.ok("User has been unbanned!");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    @RoleCheck("ROLE_ADMIN")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User has been deleted!");
    }


}