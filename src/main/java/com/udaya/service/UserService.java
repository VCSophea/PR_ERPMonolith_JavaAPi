package com.udaya.service;

import com.udaya.dto.common.BaseFilterRequest;
import com.udaya.dto.user.UserCreateRequest;
import com.udaya.dto.user.UserResponse;
import com.udaya.dto.user.UserUpdateRequest;
import com.udaya.exception.GlobalException;
import com.udaya.mapper.UserMapper;
import com.udaya.model.Module;
import com.udaya.model.ModuleType;
import com.udaya.model.User;
import com.udaya.repository.ModuleTypeRepository;
import com.udaya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final GroupService groupService;

	// * List with keyword filter + pagination
	public PageResult<User> getAllUsers(BaseFilterRequest request) {
		String keyword = (request != null) ? request.getKeyword() : null;
		int page = (request != null) ? request.getPage() : 1;
		int size = (request != null) ? request.getSize() : 10;
		return new PageResult<>(userRepository.findAll(keyword, page, size), userRepository.countAll(keyword));
	}

	// * Get single user
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new GlobalException("User not found", 404));
	}

	// * Create user
	@Transactional
	public User createUser(UserCreateRequest request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) throw new GlobalException("Username already exists", 400);
		if (userRepository.findByEmail(request.getEmail()).isPresent()) throw new GlobalException("Email already exists", 400);

		User user = userMapper.fromCreateRequest(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreated(LocalDateTime.now());
		User saved = userRepository.save(user);

		// * Assign groups if provided
		if (request.getGroupIds() != null) {
			request.getGroupIds().forEach(groupId -> groupService.assignUserToGroup(saved.getId(), groupId));
		}
		return saved;
	}

	// * Update user
	@Transactional
	public void updateUser(Long id, UserUpdateRequest request) {
		User user = getUserById(id);
		userMapper.updateUserFromRequest(request, user);
		userRepository.update(user);
	}

	// * Soft-delete user
	@Transactional
	public void deleteUser(Long id) {
		getUserById(id); // verify exists
		userRepository.deleteById(id);
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
		userMapper.enrichModulesForProfile(moduleTypes, userModules);
		List<ModuleType> nonEmptyTypes = moduleTypes.stream().filter(t -> !t.getModuleList().isEmpty()).collect(Collectors.toList());
		UserResponse response = userMapper.toResponse(user);
		response.setModuleTypeList(nonEmptyTypes);
		return response;
	}

	public String getUserByUsername(String username) {
		return userRepository.findByUsername(username).map(User::getUsername).orElseThrow(() -> new GlobalException("User not found", 404));
	}

	// * Simple container for a paginated data set
	public record PageResult<T>(List<T> data, long total) {
	}
}
