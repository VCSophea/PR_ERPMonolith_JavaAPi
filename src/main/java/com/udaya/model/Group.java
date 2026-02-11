package com.udaya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

	private Long id;
	private String sysCode;
	private String name;
	private LocalDateTime created;
	private Long createdBy;
	private LocalDateTime modified;
	private Long modifiedBy;
	private Integer isPos; // 0: normal, 1: POS
	private Integer isActive; // 0: inactive, 1: active
}
