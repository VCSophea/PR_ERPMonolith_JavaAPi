package com.udaya.repository;

import com.udaya.model.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class UserRepository {

	private final DSLContext dsl;

	// * Find All Users
	public List<User> findAll() {
		return dsl.selectFrom(table("users"))
		          .fetchInto(User.class);
	}

	// * Find User by ID
	public Optional<User> findById(Long id) {
		return dsl.selectFrom(table("users"))
		          .where(field("id").eq(id))
		          .fetchOptionalInto(User.class);
	}

	// * Find User by Username
	public Optional<User> findByUsername(String username) {
		return dsl.selectFrom(table("users"))
		          .where(field("username").eq(username))
		          .limit(1)
		          .fetchOptionalInto(User.class);
	}

	// * Find User by Email
	public Optional<User> findByEmail(String email) {
		return dsl.selectFrom(table("users"))
		          .where(field("email").eq(email))
		          .limit(1)
		          .fetchOptionalInto(User.class);
	}

	// * Save User (Essential fields only for registration)
	public User save(User user) {
		dsl.insertInto(table("users"))
		   .set(field("username"), user.getUsername())
		   .set(field("email"), user.getEmail())
		   .set(field("password"), user.getPassword())
		   .set(field("role"), user.getRole())
		   .set(field("full_name"), user.getFullName())
		   .set(field("first_name"), user.getFirstName())
		   .set(field("last_name"), user.getLastName())
		   .set(field("is_active"), user.getIsActive() != null ? user.getIsActive() : 1)
		   .set(field("created"), user.getCreated())
		   .set(field("created_by"), user.getCreatedBy())
		   .execute();
		return user;
	}

	// * Update User
	public void update(User user) {
		dsl.update(table("users"))
		   .set(field("email"), user.getEmail())
		   .set(field("full_name"), user.getFullName())
		   .set(field("first_name"), user.getFirstName())
		   .set(field("last_name"), user.getLastName())
		   .set(field("sex"), user.getSex())
		   .set(field("dob"), user.getDob())
		   .set(field("address"), user.getAddress())
		   .set(field("telephone"), user.getTelephone())
		   .set(field("photo"), user.getPhoto())
		   .set(field("is_active"), user.getIsActive())
		   .set(field("modified"), user.getModified())
		   .set(field("modified_by"), user.getModifiedBy())
		   .where(field("id").eq(user.getId()))
		   .execute();
	}

	// * Update Session Info
	public void updateSessionInfo(Long userId, String sessionId, java.time.LocalDateTime sessionStart) {
		dsl.update(table("users"))
		   .set(field("session_id"), sessionId)
		   .set(field("session_start"), sessionStart)
		   .set(field("session_active"), sessionStart)
		   .where(field("id").eq(userId))
		   .execute();
	}

	// * Update Login Attempt
	public void updateLoginAttempt(Long userId, String remoteIp, String userAgent) {
		dsl.update(table("users"))
		   .set(field("login_attempt"), java.time.LocalDateTime.now())
		   .set(field("login_attempt_remote_ip"), remoteIp)
		   .set(field("login_attempt_http_user_agent"), userAgent)
		   .where(field("id").eq(userId))
		   .execute();
	}
}
