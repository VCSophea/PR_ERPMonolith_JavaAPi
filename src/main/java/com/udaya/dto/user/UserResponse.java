package com.udaya.dto.user;

import com.udaya.model.ModuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private Long id;
	private String username;
	private String email;
	private String role;
	private String fullName;
	private String firstName;
	private String lastName;
	private String sex;
	private LocalDate dob;
	private String address;
	private String telephone;
	private String photo;
	private Integer isActive;
	private List<ModuleType> moduleTypeList;
}
