package com.udaya.controller;

import com.udaya.model.Group;
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

	@GetMapping
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "VIEW")
	@Operation(summary = "Get all active groups")
	public ResponseEntity<List<Group>> getAllActiveGroups() {
		return ResponseEntity.ok(groupService.getAllActiveGroups());
	}

	@GetMapping("/{id}")
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "VIEW")
	@Operation(summary = "Get group by ID")
	public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
		return ResponseEntity.ok(groupService.getGroupById(id));
	}

	@PostMapping
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "ADD")
	@Operation(summary = "Create group")
	public ResponseEntity<Group> createGroup(@RequestBody Group group) {
		return ResponseEntity.ok(groupService.createGroup(group));
	}

	@PutMapping("/{id}")
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "EDIT")
	@Operation(summary = "Update group")
	public ResponseEntity<Void> updateGroup(@PathVariable Long id, @RequestBody Group group) {
		group.setId(id);
		groupService.updateGroup(group);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{groupId}/users/{userId}")
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "EDIT")
	@Operation(summary = "Assign user to group")
	public ResponseEntity<Void> assignUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
		groupService.assignUserToGroup(userId, groupId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{groupId}/users/{userId}")
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "EDIT")
	@Operation(summary = "Remove user from group")
	public ResponseEntity<Void> removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
		groupService.removeUserFromGroup(userId, groupId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/users")
	@RequiresPermission(module = "GROUP_MANAGEMENT", action = "VIEW")
	@Operation(summary = "Get group members")
	public ResponseEntity<List<Long>> getGroupMembers(@PathVariable Long groupId) {
		return ResponseEntity.ok(groupService.getGroupMembers(groupId));
	}
}
