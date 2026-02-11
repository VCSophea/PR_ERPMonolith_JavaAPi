package com.udaya.controller;

import com.udaya.model.User;
import com.udaya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users")
    public ResponseEntity<List<User>> getAllUsers() {
        // * Fetch all users
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their unique ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // * Fetch user by ID
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Register a new user in the system")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // * Create new user
        return ResponseEntity.ok(userService.createUser(user));
    }
}
