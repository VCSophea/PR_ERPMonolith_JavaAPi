package com.udaya.service;

import com.udaya.model.Module;
import com.udaya.repository.ModuleRepository;
import com.udaya.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

	private final PermissionRepository permissionRepository;
	private final ModuleRepository moduleRepository;

	public boolean checkUserHasPermission(Long userId, String moduleName) {
		Module module = moduleRepository.findByName(moduleName)
		                                .orElse(null);
		return module != null && permissionRepository.userHasModuleAccess(userId, module.getId());
	}

	public List<Module> getUserModules(Long userId) {
		return permissionRepository.findModuleIdsByUserId(userId)
		                           .stream()
		                           .map(moduleRepository::findById)
		                           .filter(Optional::isPresent)
		                           .map(Optional::get)
		                           .collect(Collectors.toList());
	}

	public List<Long> getGroupModuleIds(Long groupId) {
		return permissionRepository.findByGroupId(groupId)
		                           .stream()
		                           .map(com.udaya.model.Permission::getModuleId)
		                           .collect(Collectors.toList());
	}

	@Transactional
	public void assignPermissionToGroup(Long groupId, Long moduleId) {
		permissionRepository.assignPermission(groupId, moduleId);
	}

	@Transactional
	public void revokePermissionFromGroup(Long groupId, Long moduleId) {
		permissionRepository.revokePermission(groupId, moduleId);
	}
}
