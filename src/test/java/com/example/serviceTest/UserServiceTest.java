package com.example.serviceTest;

import nl.novi.LivingInSync.dto.UserDto;
import nl.novi.LivingInSync.model.User;
import nl.novi.LivingInSync.repository.UserRepository;
import nl.novi.LivingInSync.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String username = userService.createUser(userDto);

        assertNotNull(username, "Username should not be null.");
        assertEquals("testuser", username, "Username should be 'testuser'.");
    }

    @Test
    void testCreateUserEmailAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto), "Should throw IllegalArgumentException if email already exists.");
    }

    @Test
    void testGetUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUser("testuser");

        assertNotNull(userDto, "User DTO should not be null.");
        assertEquals("testuser", userDto.getUsername(), "Username should match.");
        assertEquals("Test User", userDto.getName(), "Name should match.");
        assertEquals("test@example.com", userDto.getEmail(), "Email should match.");
    }

    @Test
    void testGetUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("testuser"), "Should throw UsernameNotFoundException if user not found.");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setName("Old Name");
        user.setEmail("old@example.com");

        UserDto userDto = new UserDto();
        userDto.setName("New Name");
        userDto.setEmail("new@example.com");
        userDto.setPassword("newpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        userService.updateUser("testuser", userDto);

        verify(userRepository, times(1)).save(user);
        assertEquals("New Name", user.getName(), "Name should be updated.");
        assertEquals("new@example.com", user.getEmail(), "Email should be updated.");
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        userService.deleteUser("testuser");

        verify(userRepository, times(1)).delete(user);
    }
}
