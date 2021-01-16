package com.example.springboot.payload;

import java.util.Set;

public class UpdateRequest {

	private Set<String> role;

	public Set<String> getRole() {
		return role;
	}

	public void setRole(Set<String> role) {
		this.role = role;
	}
}
