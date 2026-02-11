package com.udaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	// * Core Identification
	private Long id;
	private String username;
	private String password;
	private String email;
	private String role;

	// * Personal Information
	private String fullName;
	private String firstName;
	private String lastName;
	private String sex;
	private LocalDate dob;
	private String address;
	private String telephone;
	private Integer nationality;
	private String photo;

	// * System & Project
	private String sysCode;
	private Long mainProjectId;
	private Long projectId;
	private String userCode;
	private Integer employeeId;
	private Integer userType; // 0: system user, 2: app user
	private Integer type; // 1: from system, 2: from active directory

	// * Session & Authentication
	private String sessionId;
	private LocalDateTime sessionStart;
	private LocalDateTime sessionActive;
	private String sessionLat;
	private String sessionLong;
	private String sessionAccuracy;
	private String token;

	// * Login Tracking
	private LocalDateTime loginAttempt;
	private String loginAttemptRemoteIp;
	private String loginAttemptHttpUserAgent;
	private String loginLat;
	private String loginLong;
	private String loginAccuracy;
	private Integer loginFrom; // 0: system, 1: e-catalog

	// * Security & Verification
	private String qrCode;
	private String secretKey;
	private Integer isLoginAuth; // 0: never, 1: google auth
	private Integer isHash;
	private Integer isVerifyAuth;
	private Integer isCgroup; // 0: customize, 1: all customer group
	private String googleAuth;

	// * Account Status
	private Integer isActive; // 0: inactive, 1: active
	private LocalDate expired;
	private Long duration;

	// * Phone Verification (for mobile app)
	private String phoneVerifyToken;
	private LocalDateTime phoneVerifyTokenExpiredDate;
	private String phoneVerifyCode;
	private LocalDateTime phoneVerifyCodeExpiredDate;
	private String phoneResetPasswordToken;
	private LocalDateTime phoneResetPasswordTokenExpiredDate;
	private String phoneResetPasswordCode;
	private LocalDateTime phoneResetPasswordCodeExpiredDate;
	private String phoneNewPasswordToken;
	private LocalDateTime phoneNewPasswordTokenExpiredDate;

	// * Third-party Integration
	private String teleUserId;
	private String provider;
	private String providerId;
	private String providerImageUrl;
	private String providerData;

	// * Audit Fields
	private LocalDateTime created;
	private Long createdBy;
	private LocalDateTime modified;
	private Long modifiedBy;
}
