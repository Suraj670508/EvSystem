package com.voltgrid.user;

import com.voltgrid.user.dto.UserDto;
import com.voltgrid.user.entity.User;
import com.voltgrid.user.exception.ResourceNotFoundException;
import com.voltgrid.user.repository.UserRepository;
import com.voltgrid.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(repository);
    }

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto(null, "Suraj", "suraj@voltgrid.com", "9876543210", "ROLE_USER", "EV-101", "ACTIVE");
        User savedUser = new User("Suraj", "suraj@voltgrid.com", "9876543210", "ROLE_USER", "EV-101", "ACTIVE");
        savedUser.setId(1L);

        when(repository.existsByEmail("suraj@voltgrid.com")).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(savedUser);

        UserDto created = userService.createUser(dto);
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Suraj", created.getName());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User("Alice", "alice@voltgrid.com", "111", "ROLE_USER", "EV-1", "ACTIVE");
        user1.setId(1L);
        when(repository.findAll()).thenReturn(List.of(user1));

        List<UserDto> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getName());
    }
}
