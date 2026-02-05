package com.sup.event_management.service;

import com.sup.event_management.dto.request.UserCreateDTO;
import com.sup.event_management.dto.response.UserResponseDTO;
import com.sup.event_management.entity.Role;
import com.sup.event_management.entity.User;
import com.sup.event_management.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository){
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
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            Map<String, String> map = new HashMap<>();
            map.put("Status", "User not present with ID : "+id);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalUser.get(), HttpStatus.FOUND);
    }

    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll()
                .stream()
                .map(user -> toResponseDTO(user))
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.FOUND);
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
