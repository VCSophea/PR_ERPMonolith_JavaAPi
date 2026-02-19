package com.udaya.controller;

import com.udaya.dto.common.BaseSearchRequest;
import com.udaya.model.Group;
import com.udaya.response.BaseResponse;
import com.udaya.security.RequiresPermission;
import com.udaya.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "3. Group Management")
public class GroupController {

	private final GroupService groupService;

	@PostMapping("/list")
	@RequiresPermission(module = "Group", action = "View")
	@Operation(summary = "Get all active groups")
	public ResponseEntity<BaseResponse<List<Group>>> getAllActiveGroups(@RequestBody(required = false) BaseSearchRequest request) {
		String keyword = (request != null) ? request.getKeyword() : null;
		return ResponseEntity.ok(BaseResponse.success(groupService.getAllActiveGroups(keyword)));
	}

	@GetMapping("/{id}")
	@RequiresPermission(module = "Group", action = "View")
	@Operation(summary = "Get group by ID")
	public ResponseEntity<BaseResponse<Group>> getGroupById(@PathVariable Long id) {
		return ResponseEntity.ok(BaseResponse.success(groupService.getGroupById(id)));
	}

	@PostMapping
	@RequiresPermission(module = "Group", action = "Add")
	@Operation(summary = "Create group")
	public ResponseEntity<BaseResponse<Group>> createGroup(@RequestBody Group group) {
		return ResponseEntity.ok(BaseResponse.success(groupService.createGroup(group)));
	}

	@PutMapping("/{id}")
	@RequiresPermission(module = "Group", action = "Edit")
	@Operation(summary = "Update group")
	public ResponseEntity<BaseResponse<Void>> updateGroup(@PathVariable Long id, @RequestBody Group group) {
		group.setId(id);
		groupService.updateGroup(group);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@PostMapping("/{groupId}/users/{userId}")
	@RequiresPermission(module = "Group", action = "Edit")
	@Operation(summary = "Assign user to group")
	public ResponseEntity<BaseResponse<Void>> assignUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
		groupService.assignUserToGroup(userId, groupId);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@DeleteMapping("/{groupId}/users/{userId}")
	@RequiresPermission(module = "Group", action = "Edit")
	@Operation(summary = "Remove user from group")
	public ResponseEntity<BaseResponse<Void>> removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
		groupService.removeUserFromGroup(userId, groupId);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@GetMapping("/{groupId}/users")
	@RequiresPermission(module = "Group", action = "View")
	@Operation(summary = "Get group members")
	public ResponseEntity<BaseResponse<List<Long>>> getGroupMembers(@PathVariable Long groupId) {
		return ResponseEntity.ok(BaseResponse.success(groupService.getGroupMembers(groupId)));
	}
}
