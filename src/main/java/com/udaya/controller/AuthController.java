package com.udaya.controller;

import com.udaya.dto.auth.LoginRequest;
import com.udaya.dto.auth.LoginResponse;
import com.udaya.model.User;
import com.udaya.response.BaseResponse;
import com.udaya.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Auth management APIs")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@Operation(summary = "User Login")
	public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
		// * Delegate to service
		return ResponseEntity.ok(BaseResponse.success(authService.login(request)));
	}

	@PostMapping("/register")
	@Operation(summary = "User Register", description = "Register new user (Public)")
	public ResponseEntity<BaseResponse<User>> register(@RequestBody User user) {
		return ResponseEntity.ok(BaseResponse.success(authService.register(user)));
	}
}
