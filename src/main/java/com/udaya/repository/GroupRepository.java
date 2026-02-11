package com.udaya.repository;

import com.udaya.model.Group;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class GroupRepository {

	private final DSLContext dsl;

	// * Find All Groups
	public List<Group> findAll() {
		return dsl.selectFrom(table("groups"))
		          .fetchInto(Group.class);
	}

	// * Find Active Groups
	public List<Group> findByIsActive(Integer isActive) {
		return dsl.selectFrom(table("groups"))
		          .where(field("is_active").eq(isActive))
		          .fetchInto(Group.class);
	}

	// * Find Group by ID
	public Optional<Group> findById(Long id) {
		return dsl.selectFrom(table("groups"))
		          .where(field("id").eq(id))
		          .fetchOptionalInto(Group.class);
	}

	// * Save Group
	public Group save(Group group) {
		dsl.insertInto(table("groups"))
		   .set(field("sys_code"), group.getSysCode())
		   .set(field("name"), group.getName())
		   .set(field("created"), group.getCreated())
		   .set(field("created_by"), group.getCreatedBy())
		   .set(field("modified"), group.getModified())
		   .set(field("modified_by"), group.getModifiedBy())
		   .set(field("is_pos"), group.getIsPos())
		   .set(field("is_active"), group.getIsActive())
		   .execute();
		return group;
	}

	// * Update Group
	public void update(Group group) {
		dsl.update(table("groups"))
		   .set(field("sys_code"), group.getSysCode())
		   .set(field("name"), group.getName())
		   .set(field("modified"), group.getModified())
		   .set(field("modified_by"), group.getModifiedBy())
		   .set(field("is_pos"), group.getIsPos())
		   .set(field("is_active"), group.getIsActive())
		   .where(field("id").eq(group.getId()))
		   .execute();
	}
}
