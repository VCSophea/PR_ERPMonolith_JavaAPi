package com.udaya.controller;

import com.udaya.dto.common.ModuleCreateRequest;
import com.udaya.model.Module;
import com.udaya.response.BaseResponse;
import com.udaya.security.RequiresPermission;
import com.udaya.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Tag(name = "4. Permission Management")
public class PermissionController {

	private final PermissionService permissionService;

	@GetMapping("/users/{userId}/modules")
	@Operation(summary = "Get user's accessible modules")
	public ResponseEntity<BaseResponse<List<Module>>> getUserModules(@PathVariable Long userId) {
		return ResponseEntity.ok(BaseResponse.success(permissionService.getUserModules(userId)));
	}

	@GetMapping("/groups/{groupId}/modules")
	@RequiresPermission(module = "Group", action = "View")
	@Operation(summary = "Get group's module IDs")
	public ResponseEntity<BaseResponse<List<Long>>> getGroupModuleIds(@PathVariable Long groupId) {
		return ResponseEntity.ok(BaseResponse.success(permissionService.getGroupModuleIds(groupId)));
	}

	@PostMapping("/groups/{groupId}/modules/{moduleId}")
	@RequiresPermission(module = "Group", action = "Edit")
	@Operation(summary = "Assign permission to group")
	public ResponseEntity<BaseResponse<Void>> assignPermission(@PathVariable Long groupId, @PathVariable Long moduleId) {
		permissionService.assignPermissionToGroup(groupId, moduleId);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@DeleteMapping("/groups/{groupId}/modules/{moduleId}")
	@RequiresPermission(module = "Group", action = "Edit")
	@Operation(summary = "Revoke permission from group")
	public ResponseEntity<BaseResponse<Void>> revokePermission(@PathVariable Long groupId, @PathVariable Long moduleId) {
		permissionService.revokePermissionFromGroup(groupId, moduleId);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@PostMapping("/modules")
	@RequiresPermission(module = "Group", action = "Add")
	@Operation(summary = "Create module set (ModuleType + Modules)")
	public ResponseEntity<BaseResponse<Void>> createModuleSet(@RequestBody @Valid ModuleCreateRequest request) {
		permissionService.createModuleSet(request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}
}
