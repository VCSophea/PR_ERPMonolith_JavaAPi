package com.udaya.service;

import com.udaya.dto.LoginRequest;
import com.udaya.dto.LoginResponse;
import com.udaya.exception.GlobalException;
import com.udaya.model.Group;
import com.udaya.model.User;
import com.udaya.repository.UserRepository;
import com.udaya.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final GroupService groupService;
	private final UserService userService;

	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
		                          .orElseThrow(() -> new GlobalException("Invalid credentials", 401));

		if (user.getIsActive() == null || user.getIsActive() == 0) {
			throw new GlobalException("User account is inactive", 403);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new GlobalException("Invalid credentials", 401);
		}

		// * Get user's groups and generate JWT with userId + groups
		List<String> groupNames = groupService.getUserGroups(user.getId())
		                                      .stream()
		                                      .map(Group::getName)
		                                      .collect(Collectors.toList());

		String sessionId = UUID.randomUUID()
		                       .toString();
		userService.updateUserSession(user.getId(), sessionId, LocalDateTime.now());

		String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole(), groupNames);

		return new LoginResponse(token);
	}

	public User register(User user) {
		if (userRepository.findByUsername(user.getUsername())
		                  .isPresent()) {
			throw new GlobalException("Username already exists", 400);
		}
		if (user.getEmail() != null && userRepository.findByEmail(user.getEmail())
		                                             .isPresent()) {
			throw new GlobalException("Email already exists", 400);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreated(LocalDateTime.now());
		user.setIsActive(1);

		return userRepository.save(user);
	}
}
