package com.sup.event_management.service;

import com.sup.event_management.dto.request.UserCreateDTO;
import com.sup.event_management.dto.response.UserResponseDTO;
import com.sup.event_management.entity.Role;
import com.sup.event_management.entity.User;
import com.sup.event_management.exceptions.AppException;
import com.sup.event_management.exceptions.ExceptionSeverity;
import com.sup.event_management.exceptions.ExceptionType;
import com.sup.event_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<UserResponseDTO> createUser(UserCreateDTO dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(toResponseDTO(savedUser), HttpStatus.CREATED);
    }

    public ResponseEntity<?> getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        "User not found",
                        ExceptionType.RESOURCE_NOT_FOUND,
                        ExceptionSeverity.INFO,
                        HttpStatus.NOT_FOUND,
                        "User ID : " + id
                ));

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
