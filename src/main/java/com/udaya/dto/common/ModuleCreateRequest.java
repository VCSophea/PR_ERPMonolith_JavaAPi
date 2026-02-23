package com.udaya.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleCreateRequest {
	private String name;
	private List<ModuleDetail> modules;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ModuleDetail {
		private String name;
		private String type;
	}
}
