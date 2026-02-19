package com.udaya.dto.user;

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
public class UserUpdateRequest {

	private Long id;
	private String email;
	private List<Long> groupIds;
	private String fullName;
	private String firstName;
	private String lastName;
	private String sex;
	private LocalDate dob;
	private String address;
	private String telephone;
	private String photo;
	private Integer isActive;
}
