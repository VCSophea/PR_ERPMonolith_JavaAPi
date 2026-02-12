package com.udaya.security;

import com.udaya.config.PermissionChecker;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

	private final PermissionChecker permissionChecker;

	@Before("@annotation(com.udaya.security.RequiresPermission)")
	public void checkPermission(JoinPoint joinPoint) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		RequiresPermission annotation = signature.getMethod().getAnnotation(RequiresPermission.class);

		if (annotation != null) {
			String module = annotation.module();
			String action = annotation.action().toUpperCase();

			// * Check permission via PermissionChecker
			if (!permissionChecker.hasPermission(auth, module, action)) {
				throw new AccessDeniedException("Access Denied: Missing permission " + module + ":" + action);
			}
		}
	}
}
