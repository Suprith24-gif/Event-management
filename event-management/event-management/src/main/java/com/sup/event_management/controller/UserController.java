package com.sup.event_management.controller;

import com.sup.event_management.dto.request.UserCreateDTO;
import com.sup.event_management.dto.response.PagedResponse;
import com.sup.event_management.dto.response.UserResponseDTO;
import com.sup.event_management.entity.User;
import com.sup.event_management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateDTO userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role) {
        return userService.getAllUsers(page, size, name, role);
    }




}
