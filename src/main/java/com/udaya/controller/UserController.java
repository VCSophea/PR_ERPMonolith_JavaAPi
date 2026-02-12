package com.udaya.controller;

import com.udaya.dto.UserCreateRequest;
import com.udaya.dto.UserResponse;
import com.udaya.dto.UserUpdateRequest;
import com.udaya.model.Module;
import com.udaya.model.User;
import com.udaya.security.RequiresPermission;
import com.udaya.service.GroupService;
import com.udaya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {

	private final UserService userService;
	private final GroupService groupService;
	private final PasswordEncoder passwordEncoder;

	@GetMapping
	@RequiresPermission(module = "USER_MANAGEMENT", action = "VIEW")
	@Operation(summary = "Get all users")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers().stream().map(this::toResponse).collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get user by ID")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(toResponse(userService.getUserById(id)));
	}

	@PostMapping
	@RequiresPermission(module = "USER_MANAGEMENT", action = "ADD")
	@Operation(summary = "Create user and assign to groups")
	public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
		// * Create user
		User user = fromCreateRequest(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		User created = userService.createUser(user);

		// * Assign to groups
		if (request.getGroupIds() != null) {
			request.getGroupIds().forEach(groupId -> groupService.assignUserToGroup(created.getId(), groupId));
		}

		return ResponseEntity.ok(toResponse(created));
	}

	@PutMapping("/{id}")
	@RequiresPermission(module = "USER_MANAGEMENT", action = "EDIT")
	@Operation(summary = "Update user and group assignments")
	public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
		request.setId(id);
		userService.updateUser(fromUpdateRequest(request));

		// * Update group assignments if provided
		if (request.getGroupIds() != null) {
			List<Long> currentGroups = groupService.getUserGroups(id).stream().map(com.udaya.model.Group::getId).collect(Collectors.toList());

			currentGroups.forEach(groupId -> groupService.removeUserFromGroup(id, groupId));
			request.getGroupIds().forEach(groupId -> groupService.assignUserToGroup(id, groupId));
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}/groups")
	@Operation(summary = "Get user's groups")
	public ResponseEntity<List<String>> getUserGroups(@PathVariable Long id) {
		return ResponseEntity.ok(groupService.getUserGroups(id).stream().map(com.udaya.model.Group::getName).collect(Collectors.toList()));
	}

	@GetMapping("/{id}/permissions")
	@Operation(summary = "Get user's accessible modules")
	public ResponseEntity<List<Module>> getUserPermissions(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserPermissions(id));
	}

	private UserResponse toResponse(User user) {
		return UserResponse.builder()
		                   .id(user.getId())
		                   .username(user.getUsername())
		                   .email(user.getEmail())
		                   .role(user.getRole())
		                   .fullName(user.getFullName())
		                   .firstName(user.getFirstName())
		                   .lastName(user.getLastName())
		                   .sex(user.getSex())
		                   .dob(user.getDob())
		                   .address(user.getAddress())
		                   .telephone(user.getTelephone())
		                   .photo(user.getPhoto())
		                   .isActive(user.getIsActive())
		                   .build();
	}

	private User fromCreateRequest(UserCreateRequest req) {
		return User.builder()
		           .username(req.getUsername())
		           .email(req.getEmail())
		           .password(req.getPassword())
		           .role("USER")
		           .fullName(req.getFullName())
		           .firstName(req.getFirstName())
		           .lastName(req.getLastName())
		           .sex(req.getSex())
		           .dob(req.getDob())
		           .address(req.getAddress())
		           .telephone(req.getTelephone())
		           .created(LocalDateTime.now())
		           .build();
	}

	private User fromUpdateRequest(UserUpdateRequest req) {
		return User.builder()
		           .id(req.getId())
		           .email(req.getEmail())
		           .fullName(req.getFullName())
		           .firstName(req.getFirstName())
		           .lastName(req.getLastName())
		           .sex(req.getSex())
		           .dob(req.getDob())
		           .address(req.getAddress())
		           .telephone(req.getTelephone())
		           .photo(req.getPhoto())
		           .isActive(req.getIsActive())
		           .modified(LocalDateTime.now())
		           .build();
	}
}
