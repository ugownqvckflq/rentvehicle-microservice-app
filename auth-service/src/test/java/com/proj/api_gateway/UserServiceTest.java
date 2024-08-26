package com.proj.api_gateway;

import com.proj.api_gateway.entity.Role;
import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.repository.RoleRepository;
import com.proj.api_gateway.repository.UserRepository;
import com.proj.api_gateway.security.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterSimpleUser_Success() {
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        User createdUser = userService.registerSimpleUser("newuser", "newuser@example.com", "password");

        assertNotNull(createdUser);
        assertEquals("newuser", createdUser.getUsername());
        assertEquals("newuser@example.com", createdUser.getEmail());
        assertEquals("encodedPassword", createdUser.getPassword()); // Проверка зашифрованного пароля
        assertTrue(createdUser.getRoles().contains(userRole));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testGrantAdminRole_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        userService.grantAdminRole(1L);

        assertTrue(user.getRoles().contains(adminRole));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testBanUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setBanned(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.banUser(1L);

        assertTrue(user.getBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUnBanUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setBanned(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.unBanUser(1L);

        assertFalse(user.getBanned());
        verify(userRepository, times(1)).save(user);
    }
}
