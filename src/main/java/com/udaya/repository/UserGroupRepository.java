package com.udaya.repository;

import com.udaya.model.UserGroup;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class UserGroupRepository {

	private final DSLContext dsl;

	// * Find All Groups for a User
	public List<Long> findGroupIdsByUserId(Long userId) {
		return dsl.select(field("group_id"))
		          .from(table("user_groups"))
		          .where(field("user_id").eq(userId))
		          .fetch(field("group_id"), Long.class);
	}

	// * Find All Users in a Group
	public List<Long> findUserIdsByGroupId(Long groupId) {
		return dsl.select(field("user_id"))
		          .from(table("user_groups"))
		          .where(field("group_id").eq(groupId))
		          .fetch(field("user_id"), Long.class);
	}

	// * Check if User is in Group
	public boolean isUserInGroup(Long userId, Long groupId) {
		return dsl.fetchExists(dsl.selectFrom(table("user_groups"))
		                          .where(field("user_id").eq(userId))
		                          .and(field("group_id").eq(groupId)));
	}

	// * Assign User to Group
	public void assignUserToGroup(Long userId, Long groupId) {
		if (!isUserInGroup(userId, groupId)) {
			dsl.insertInto(table("user_groups"))
			   .set(field("user_id"), userId)
			   .set(field("group_id"), groupId)
			   .execute();
		}
	}

	// * Remove User from Group
	public void removeUserFromGroup(Long userId, Long groupId) {
		dsl.deleteFrom(table("user_groups"))
		   .where(field("user_id").eq(userId))
		   .and(field("group_id").eq(groupId))
		   .execute();
	}

	// * Get User-Group mappings
	public List<UserGroup> findByUserId(Long userId) {
		return dsl.selectFrom(table("user_groups"))
		          .where(field("user_id").eq(userId))
		          .fetchInto(UserGroup.class);
	}

	// * Get User-Group mappings by Group
	public List<UserGroup> findByGroupId(Long groupId) {
		return dsl.selectFrom(table("user_groups"))
		          .where(field("group_id").eq(groupId))
		          .fetchInto(UserGroup.class);
	}
}
