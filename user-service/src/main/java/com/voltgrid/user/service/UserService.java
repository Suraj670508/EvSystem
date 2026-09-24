package com.voltgrid.user.service;

import com.voltgrid.user.dto.UserDto;
import com.voltgrid.user.entity.User;
import com.voltgrid.user.exception.ResourceNotFoundException;
import com.voltgrid.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exists");
        }

        User user = new User(
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getRole(),
                dto.getVehicleId(),
                dto.getAccountStatus()
        );
        User saved = repository.save(user);
        return mapToDto(saved);
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getVehicleId() != null) user.setVehicleId(dto.getVehicleId());
        if (dto.getAccountStatus() != null) user.setAccountStatus(dto.getAccountStatus());

        User updated = repository.save(user);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        repository.delete(user);
    }

    private UserDto mapToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getVehicleId(),
                user.getAccountStatus()
        );
    }
}
