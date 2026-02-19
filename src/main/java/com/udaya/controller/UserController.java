package com.udaya.controller;

import com.udaya.dto.common.BaseSearchRequest;
import com.udaya.dto.user.UserResponse;
import com.udaya.dto.user.UserUpdateRequest;
import com.udaya.response.BaseResponse;
import com.udaya.security.CustomUserDetails;
import com.udaya.security.RequiresPermission;
import com.udaya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "2. User Management")
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final com.udaya.mapper.UserMapper userMapper;

	@PostMapping("/list")
	@RequiresPermission(module = "User", action = "View")
	@Operation(summary = "Get all users")
	public ResponseEntity<BaseResponse<List<UserResponse>>> getAllUsers(@RequestBody(required = false) BaseSearchRequest request) {
		String keyword = (request != null) ? request.getKeyword() : null;
		return ResponseEntity.ok(BaseResponse.success(userService.getAllUsers(keyword).stream().map(userMapper::toResponse).collect(Collectors.toList())));
	}

	@PutMapping("/{id}")
	@RequiresPermission(module = "User", action = "Edit")
	@Operation(summary = "Update user")
	public ResponseEntity<BaseResponse<Void>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
		userService.updateUser(id, request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@GetMapping("/me")
	@Operation(summary = "Get current user profile with permissions")
	public ResponseEntity<BaseResponse<UserResponse>> me(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return ResponseEntity.ok(BaseResponse.success(userService.getMyProfile(userDetails.getUserId())));
	}
}
