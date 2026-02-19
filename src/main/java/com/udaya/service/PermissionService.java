package com.udaya.service;

import com.udaya.model.Module;
import com.udaya.model.Permission;
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
		return moduleRepository.findByName(moduleName).map(module -> permissionRepository.userHasModuleAccess(userId, module.getId())).orElse(false);
	}

	// * Check module + action permission
	public boolean checkUserHasPermission(Long userId, String moduleName, String action) {
		if (moduleName == null) return false;

		// * Strategy 1: Check "Module (Action)" pattern (e.g. "User (view)")
		String combinedName = (action != null && !action.isEmpty()) ? moduleName + " (" + action + ")" : moduleName;

		Optional<Module> moduleOpt = moduleRepository.findByName(combinedName);

		// * Strategy 2: If combined not found, try base module name (Legacy behavior)
		if (moduleOpt.isEmpty() && (action != null && !action.isEmpty())) {
			moduleOpt = moduleRepository.findByName(moduleName);
		}

		return moduleOpt.map(module -> permissionRepository.userHasModuleAccess(userId, module.getId())).orElse(false);
	}

	public List<Module> getUserModules(Long userId) {
		return permissionRepository.findModuleIdsByUserId(userId).stream().map(moduleRepository::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}

	public List<Long> getGroupModuleIds(Long groupId) {
		return permissionRepository.findByGroupId(groupId).stream().map(Permission::getModuleId).collect(Collectors.toList());
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
