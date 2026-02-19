package com.udaya.service;

import com.udaya.dto.user.UserResponse;
import com.udaya.dto.user.UserUpdateRequest;
import com.udaya.exception.GlobalException;
import com.udaya.model.Module;
import com.udaya.model.ModuleType;
import com.udaya.model.User;
import com.udaya.repository.ModuleTypeRepository;
import com.udaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PermissionService permissionService;
	private final ModuleTypeRepository moduleTypeRepository;
	private final com.udaya.mapper.UserMapper userMapper;

	public List<User> getAllUsers(String keyword) {
		return userRepository.findAll(keyword);
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
	public void updateUser(Long id, UserUpdateRequest request) {
		User user = getUserById(id); // * Verify and fetch existing
		userMapper.updateUserFromRequest(request, user); // * Merge changes
		userRepository.update(user); // * Persist
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

	public UserResponse getMyProfile(Long userId) {
		User user = getUserById(userId);
		List<Module> userModules = permissionService.getUserModules(userId);
		List<ModuleType> moduleTypes = moduleTypeRepository.findAll();

		// * Map modules to module types
		userMapper.enrichModulesForProfile(moduleTypes, userModules);

		// * Filter out empty module types (optional, but good practice)
		List<ModuleType> nonEmptyTypes = moduleTypes.stream().filter(t -> !t.getModuleList().isEmpty()).collect(Collectors.toList());

		UserResponse response = userMapper.toResponse(user);
		response.setModuleTypeList(nonEmptyTypes);
		return response;
	}
}
