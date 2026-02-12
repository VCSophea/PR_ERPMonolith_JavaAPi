package com.udaya.service;

import com.udaya.exception.GlobalException;
import com.udaya.model.Module;
import com.udaya.model.User;
import com.udaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PermissionService permissionService;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new GlobalException("User not found", 404));
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> new GlobalException("User not found", 404));
	}

	@Transactional
	public User createUser(User user) {
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new GlobalException("Username already exists", 400);
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new GlobalException("Email already exists", 400);
		}
		user.setCreated(LocalDateTime.now());
		return userRepository.save(user);
	}

	@Transactional
	public void updateUser(User user) {
		getUserById(user.getId()); // * Verify user exists
		user.setModified(LocalDateTime.now());
		userRepository.update(user);
	}

	public List<Module> getUserPermissions(Long userId) {
		return permissionService.getUserModules(userId);
	}

	@Transactional
	public void updateUserSession(Long userId, String sessionId, LocalDateTime sessionStart) {
		userRepository.updateSessionInfo(userId, sessionId, sessionStart);
	}

	@Transactional
	public void trackLoginAttempt(Long userId, String remoteIp, String userAgent) {
		userRepository.updateLoginAttempt(userId, remoteIp, userAgent);
	}
}
