package com.udaya.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

	@NotBlank(message = "username is required", groups = OnCreate.class)
	private String username;
	@NotBlank(message = "password is required", groups = OnCreate.class)
	@Size(min = 6, message = "password must be at least 6 characters", groups = OnCreate.class)
	private String password;
	@NotBlank(message = "email is required", groups = OnCreate.class)
	@Email(message = "email must be valid", groups = {OnCreate.class, OnUpdate.class})
	private String email;
	@NotBlank(message = "role is required", groups = OnCreate.class)
	private String role;
	private List<Long> groupIds;
	private String fullName;
	private String firstName;
	private String lastName;
	private String sex;
	private LocalDate dob;
	private String address;
	private String telephone;
	private String photo;

	// Validation groups
	public interface OnCreate {
	}

	public interface OnUpdate {
	}
}
