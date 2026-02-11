package com.udaya.security;

import com.udaya.config.CustomPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

	private final CustomPermissionEvaluator permissionEvaluator;

	@Before("@annotation(com.udaya.security.RequiresPermission)")
	public void checkPermission(JoinPoint joinPoint) {
		Authentication authentication = SecurityContextHolder.getContext()
		                                                     .getAuthentication();

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);

		if (annotation != null) {
			String module = annotation.module();
			String action = annotation.action();

			boolean hasPermission = permissionEvaluator.hasPermission(authentication, module, action);
			if (!hasPermission) {
				throw new AccessDeniedException("Access Denied: Missing permission " + module + ":" + action);
			}
		}
	}
}
