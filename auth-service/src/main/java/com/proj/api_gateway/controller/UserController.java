package com.proj.api_gateway.controller;

import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.security.services.UserService;
import com.project.rolechecker.RoleCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Назначить роль администратора (только для админа)",
            description = "Назначает роль администратора пользователю по его идентификатору.",
            parameters = {
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN")
            })
    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/grant-admin/{id}")
    public ResponseEntity<?> grantAdminRole(@PathVariable Long id) {
        userService.grantAdminRole(id);
        return ResponseEntity.ok("You added admin!");
    }

    @Operation(summary = "Найти пользователя по имени пользователя",
            description = "Ищет пользователя по его имени пользователя.")
    @GetMapping("/find/{username}")
    public ResponseEntity<?> findUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Забанить пользователя (только для админа)",
            description = "Забанивает пользователя по его идентификатору.",
            parameters = {
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN")
            })
    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/ban/{id}")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return ResponseEntity.ok("User has been banned!");
    }

    @Operation(summary = "Разбанить пользователя (только для админа)",
            description = "Разбанивает пользователя по его идентификатору.",
            parameters = {
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN")
            })
    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/unban/{id}")
    public ResponseEntity<?> unBanUser(@PathVariable Long id) {
        userService.unBanUser(id);
        return ResponseEntity.ok("User has been unbanned!");
    }

    @Operation(summary = "Обновить данные пользователя",
            description = "Обновляет данные пользователя по его идентификатору.")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Удалить пользователя (только для админа)",
            description = "Удаляет пользователя по его идентификатору.",
            parameters = {
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN")
            })
    @DeleteMapping("/delete/{id}")
    @RoleCheck("ROLE_ADMIN")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User has been deleted!");
    }

}