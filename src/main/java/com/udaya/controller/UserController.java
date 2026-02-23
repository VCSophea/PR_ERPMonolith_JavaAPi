package com.udaya.controller;

import com.udaya.dto.common.BaseFilterRequest;
import com.udaya.dto.user.UserCreateRequest;
import com.udaya.dto.user.UserResponse;
import com.udaya.dto.user.UserUpdateRequest;
import com.udaya.mapper.UserMapper;
import com.udaya.model.User;
import com.udaya.response.BaseResponse;
import com.udaya.response.Pagination;
import com.udaya.security.CustomUserDetails;
import com.udaya.security.RequiresPermission;
import com.udaya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "2. User Management")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@PostMapping("/list")
	@RequiresPermission(module = "User", action = "View")
	@Operation(summary = "Get all users")
	public ResponseEntity<BaseResponse<List<UserResponse>>> getAllUsers(@RequestBody(required = false) @Valid BaseFilterRequest request) {
		UserService.PageResult<User> result = userService.getAllUsers(request);
		List<UserResponse> data = result.data().stream().map(userMapper::toResponse).collect(Collectors.toList());
		int page = (request != null) ? request.getPage() : 1;
		int size = (request != null) ? request.getSize() : 10;
		return ResponseEntity.ok(BaseResponse.success(data, Pagination.builder().page(page).size(size).total(result.total()).build()));
	}

	@GetMapping("/{id}")
	@RequiresPermission(module = "User", action = "View")
	@Operation(summary = "Get user by ID")
	public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable Long id) {
		return ResponseEntity.ok(BaseResponse.success(userMapper.toResponse(userService.getUserById(id))));
	}

	@PostMapping
	@RequiresPermission(module = "User", action = "Add")
	@Operation(summary = "Create user")
	public ResponseEntity<BaseResponse<UserResponse>> createUser(@RequestBody @Validated(UserCreateRequest.OnCreate.class) UserCreateRequest request) {
		return ResponseEntity.ok(BaseResponse.success(userMapper.toResponse(userService.createUser(request))));
	}

	@PutMapping("/{id}")
	@RequiresPermission(module = "User", action = "Edit")
	@Operation(summary = "Update user")
	public ResponseEntity<BaseResponse<Void>> updateUser(@PathVariable Long id, @RequestBody @Validated(UserCreateRequest.OnUpdate.class) UserUpdateRequest request) {
		userService.updateUser(id, request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@DeleteMapping("/{id}")
	@RequiresPermission(module = "User", action = "Delete")
	@Operation(summary = "Delete user (soft delete)")
	public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@GetMapping("/me")
	@Operation(summary = "Get current user profile with permissions")
	public ResponseEntity<BaseResponse<UserResponse>> me(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return ResponseEntity.ok(BaseResponse.success(userService.getMyProfile(userDetails.getUserId())));
	}
}
