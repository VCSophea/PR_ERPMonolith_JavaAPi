package com.udaya.repository;

import com.udaya.model.Permission;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class PermissionRepository {

	private final DSLContext dsl;

	// * Find All Permissions for a Group
	public List<Permission> findByGroupId(Long groupId) {
		return dsl.selectFrom(table("permissions"))
		          .where(field("group_id").eq(groupId))
		          .fetchInto(Permission.class);
	}

	// * Find All Groups that have access to a Module
	public List<Permission> findByModuleId(Long moduleId) {
		return dsl.selectFrom(table("permissions"))
		          .where(field("module_id").eq(moduleId))
		          .fetchInto(Permission.class);
	}

	// * Check if Group has Permission for Module
	public boolean hasPermission(Long groupId, Long moduleId) {
		return dsl.fetchExists(dsl.selectFrom(table("permissions"))
		                          .where(field("group_id").eq(groupId))
		                          .and(field("module_id").eq(moduleId)));
	}

	// * Get Module IDs for User (via user's groups)
	public List<Long> findModuleIdsByUserId(Long userId) {
		return dsl.select(field("module_id"))
		          .from(table("permissions"))
		          .where(field("group_id").in(dsl.select(field("group_id"))
		                                         .from(table("user_groups"))
		                                         .where(field("user_id").eq(userId))))
		          .fetch(field("module_id"), Long.class);
	}

	// * Check if User has access to a Module
	public boolean userHasModuleAccess(Long userId, Long moduleId) {
		return dsl.fetchExists(dsl.selectFrom(table("permissions"))
		                          .where(field("module_id").eq(moduleId))
		                          .and(field("group_id").in(dsl.select(field("group_id"))
		                                                       .from(table("user_groups"))
		                                                       .where(field("user_id").eq(userId)))));
	}

	// * Assign Permission
	public void assignPermission(Long groupId, Long moduleId) {
		if (!hasPermission(groupId, moduleId)) {
			dsl.insertInto(table("permissions"))
			   .set(field("group_id"), groupId)
			   .set(field("module_id"), moduleId)
			   .execute();
		}
	}

	// * Revoke Permission
	public void revokePermission(Long groupId, Long moduleId) {
		dsl.deleteFrom(table("permissions"))
		   .where(field("group_id").eq(groupId))
		   .and(field("module_id").eq(moduleId))
		   .execute();
	}
}
