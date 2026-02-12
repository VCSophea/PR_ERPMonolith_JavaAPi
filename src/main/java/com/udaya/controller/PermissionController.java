package com.udaya.controller;

import com.udaya.model.Module;
import com.udaya.security.RequiresPermission;
import com.udaya.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management")
public class PermissionController {

	private final PermissionService permissionService;

	@GetMapping("/users/{userId}/modules")
	@Operation(summary = "Get user's accessible modules")
	public ResponseEntity<List<Module>> getUserModules(@PathVariable Long userId) {
		return ResponseEntity.ok(permissionService.getUserModules(userId));
	}

	@GetMapping("/groups/{groupId}/modules")
	@RequiresPermission(module = "PERMISSION_MANAGEMENT", action = "VIEW")
	@Operation(summary = "Get group's module IDs")
	public ResponseEntity<List<Long>> getGroupModuleIds(@PathVariable Long groupId) {
		return ResponseEntity.ok(permissionService.getGroupModuleIds(groupId));
	}

	@PostMapping("/groups/{groupId}/modules/{moduleId}")
	@RequiresPermission(module = "PERMISSION_MANAGEMENT", action = "EDIT")
	@Operation(summary = "Assign permission to group")
	public ResponseEntity<Void> assignPermission(@PathVariable Long groupId, @PathVariable Long moduleId) {
		permissionService.assignPermissionToGroup(groupId, moduleId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/groups/{groupId}/modules/{moduleId}")
	@RequiresPermission(module = "PERMISSION_MANAGEMENT", action = "EDIT")
	@Operation(summary = "Revoke permission from group")
	public ResponseEntity<Void> revokePermission(@PathVariable Long groupId, @PathVariable Long moduleId) {
		permissionService.revokePermissionFromGroup(groupId, moduleId);
		return ResponseEntity.ok().build();
	}
}
