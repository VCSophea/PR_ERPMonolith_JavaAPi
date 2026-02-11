package com.udaya.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

	private final Long userId;
	private final String username;
	private final String password;
	private final List<String> groups;
	private final boolean enabled;

	public CustomUserDetails(Long userId, String username, String password, List<String> groups, boolean enabled) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.groups = groups;
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return groups.stream()
		             .map(group -> new SimpleGrantedAuthority("GROUP_" + group))
		             .collect(Collectors.toList());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
