package com.proj.api_gateway;

import com.proj.api_gateway.controller.UserController;
import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.security.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindUserByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.findUserByUsername("testuser");

        assertEquals(ResponseEntity.ok(user), response);
        verify(userService, times(1)).findByUsername("testuser");
    }

    @Test
    public void testGrantAdminRole_Success() {
        doNothing().when(userService).grantAdminRole(1L);

        ResponseEntity<?> response = userController.grantAdminRole(1L);

        assertEquals(ResponseEntity.ok("You added admin!"), response);
        verify(userService, times(1)).grantAdminRole(1L);
    }

    @Test
    public void testBanUser_Success() {
        doNothing().when(userService).banUser(1L);

        ResponseEntity<?> response = userController.banUser(1L);

        assertEquals(ResponseEntity.ok("User has been banned!"), response);
        verify(userService, times(1)).banUser(1L);
    }

    @Test
    public void testUnBanUser_Success() {
        doNothing().when(userService).unBanUser(1L);

        ResponseEntity<?> response = userController.unBanUser(1L);

        assertEquals(ResponseEntity.ok("User has been unbanned!"), response);
        verify(userService, times(1)).unBanUser(1L);
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertEquals(ResponseEntity.ok("User has been deleted!"), response);
        verify(userService, times(1)).deleteUser(1L);
    }
}
