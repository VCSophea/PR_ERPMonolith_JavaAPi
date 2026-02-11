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

    // * Using dynamic table/field generation since we haven't run code-gen yet
    // * In a real scenario, use generated Tables and Records
    
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
                .fetchOptionalInto(User.class);
    }

    // * Save User
    public User save(User user) {
        dsl.insertInto(table("users"))
                .set(field("username"), user.getUsername())
                .set(field("email"), user.getEmail())
                .set(field("password"), user.getPassword()) // Ensure password is saved
                .set(field("role"), user.getRole())
                .execute();
        return user;
    }
}
