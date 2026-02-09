package com.sup.event_management.service;

import com.sup.event_management.dto.request.UserCreateDTO;
import com.sup.event_management.dto.response.PagedResponse;
import com.sup.event_management.dto.response.UserResponseDTO;
import com.sup.event_management.entity.Role;
import com.sup.event_management.entity.User;
import com.sup.event_management.exceptions.AppException;
import com.sup.event_management.exceptions.ExceptionSeverity;
import com.sup.event_management.exceptions.ExceptionType;
import com.sup.event_management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<UserResponseDTO> createUser(UserCreateDTO dto) {
        logger.info("Creating user with email: {}", dto.getEmail());

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return new ResponseEntity<>(toResponseDTO(savedUser), HttpStatus.CREATED);
    }

    public ResponseEntity<?> getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new AppException(
                            "User not found",
                            ExceptionType.RESOURCE_NOT_FOUND,
                            ExceptionSeverity.INFO,
                            HttpStatus.NOT_FOUND,
                            "User ID : " + id
                    );
                });

        logger.info("User found with ID: {}", id);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<PagedResponse<UserResponseDTO>> getAllUsers(
            int page, int size, String name, String role) {
        logger.info("Fetching users with filters -> name: {}, role: {}, page: {}, size: {}", name, role, page, size);

        List<User> allUsers = userRepository.findAll();

        List<User> filteredUsers = allUsers.stream()
                .filter(user -> name == null || user.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(user -> role == null || user.getRole().name().equalsIgnoreCase(role))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, filteredUsers.size());
        List<UserResponseDTO> pagedUsers = filteredUsers.subList(
                        Math.min(start, filteredUsers.size()),
                        Math.min(end, filteredUsers.size())
                ).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        PagedResponse<UserResponseDTO> response = new PagedResponse<>(
                pagedUsers,
                page,
                size,
                filteredUsers.size(),
                (int) Math.ceil((double) filteredUsers.size() / size)
        );

        logger.info("Returning {} users out of total {} filtered users", pagedUsers.size(), filteredUsers.size());
        return ResponseEntity.ok(response);
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
