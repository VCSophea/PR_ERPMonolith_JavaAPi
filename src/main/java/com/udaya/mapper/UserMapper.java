package com.udaya.mapper;

import com.udaya.dto.user.UserCreateRequest;
import com.udaya.dto.user.UserResponse;
import com.udaya.dto.user.UserUpdateRequest;
import com.udaya.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserMapper {

	public UserResponse toResponse(User user) {
		if (user == null) return null;

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

	public User fromCreateRequest(UserCreateRequest req) {
		if (req == null) return null;

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

	public void updateUserFromRequest(UserUpdateRequest req, User user) {
		if (req == null || user == null) return;

		if (req.getEmail() != null) user.setEmail(req.getEmail());
		if (req.getFullName() != null) user.setFullName(req.getFullName());
		if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
		if (req.getLastName() != null) user.setLastName(req.getLastName());
		if (req.getSex() != null) user.setSex(req.getSex());
		if (req.getDob() != null) user.setDob(req.getDob());
		if (req.getAddress() != null) user.setAddress(req.getAddress());
		if (req.getTelephone() != null) user.setTelephone(req.getTelephone());
		if (req.getPhoto() != null) user.setPhoto(req.getPhoto());
		if (req.getIsActive() != null) user.setIsActive(req.getIsActive());

		user.setModified(LocalDateTime.now());
	}

	public void enrichModulesForProfile(List<com.udaya.model.ModuleType> moduleTypes, List<com.udaya.model.Module> userModules) {
		moduleTypes.forEach(type -> {
			List<com.udaya.model.Module> modulesForType = userModules.stream().filter(m -> m.getModuleTypeId().equals(type.getId())).map(m -> {
				m.setType(m.getName()); // * Default to name
				m.setChecked(true); // * Accessible modules are checked
				return m;
			}).collect(java.util.stream.Collectors.toList());
			type.setModuleList(modulesForType);
		});
	}
}
