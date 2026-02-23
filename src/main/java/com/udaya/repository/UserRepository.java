package com.udaya.repository;

import com.udaya.model.User;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
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

	// * Build keyword condition helper
	private Condition buildKeywordCondition(String keyword) {
		Condition cond = field("is_active").ne(2); // * Exclude archived
		if (keyword != null && !keyword.trim().isEmpty()) {
			String likePattern = "%" + keyword.trim() + "%";
			cond = cond.and(field("username").likeIgnoreCase(likePattern).or(field("email").likeIgnoreCase(likePattern)).or(field("full_name").likeIgnoreCase(likePattern)));
		}
		return cond;
	}

	// * Find All Users (with optional keyword + pagination)
	public List<User> findAll(String keyword, int page, int size) {
		int offset = (page - 1) * size;
		return dsl.selectFrom(table("users")).where(buildKeywordCondition(keyword)).limit(size).offset(offset).fetchInto(User.class);
	}

	// * Count All Users matching keyword (for pagination total)
	public long countAll(String keyword) {
		return dsl.selectCount().from(table("users")).where(buildKeywordCondition(keyword)).fetchOne(0, long.class);
	}

	// * Soft-delete: mark user archived
	public void deleteById(Long id) {
		dsl.update(table("users")).set(field("is_active"), 2).where(field("id").eq(id)).execute();
	}

	// * Find User by ID
	public Optional<User> findById(Long id) {
		return dsl.selectFrom(table("users")).where(field("id").eq(id)).fetchOptionalInto(User.class);
	}

	// * Find User by Username
	public Optional<User> findByUsername(String username) {
		return dsl.selectFrom(table("users")).where(field("username").eq(username)).limit(1).fetchOptionalInto(User.class);
	}

	// * Find User by Email
	public Optional<User> findByEmail(String email) {
		return dsl.selectFrom(table("users")).where(field("email").eq(email)).limit(1).fetchOptionalInto(User.class);
	}

	// * Save User — returns the user with the DB-generated ID
	public User save(User user) {
		Long generatedId = dsl.insertInto(table("users"))
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
		                      .returningResult(field("id", Long.class))
		                      .fetchOne(field("id", Long.class));
		user.setId(generatedId);
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
		dsl.update(table("users")).set(field("session_id"), sessionId).set(field("session_start"), sessionStart).set(field("session_active"), sessionStart).where(field("id").eq(userId)).execute();
	}

	// * Update Login Attempt
	public void updateLoginAttempt(Long userId, String remoteIp, String userAgent) {
		dsl.update(table("users")).set(field("login_attempt"), java.time.LocalDateTime.now()).set(field("login_attempt_remote_ip"), remoteIp).set(field("login_attempt_http_user_agent"), userAgent).where(field("id").eq(userId)).execute();
	}
}
