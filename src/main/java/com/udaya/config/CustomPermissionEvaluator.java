package com.udaya.config;

import com.udaya.security.CustomUserDetails;
import com.udaya.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

	private final PermissionService permissionService;

	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		if (auth == null || targetDomainObject == null || !(permission instanceof String)) {
			return false;
		}

		if (!(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
			log.warn("Authentication principal is not CustomUserDetails");
			return false;
		}

		String moduleName = targetDomainObject.toString();

		// * Check database permission: user's groups → module access
		return permissionService.checkUserHasPermission(userDetails.getUserId(), moduleName);
	}

	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		return false;
	}
}
