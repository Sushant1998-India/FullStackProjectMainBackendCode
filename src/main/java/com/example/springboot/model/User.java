package com.example.springboot.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Entity
@Table(name="usersinfo",
uniqueConstraints = { 
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email_id") 
	})
public class User {


@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private long id;

@Column(name="first_name")
@NotBlank
private String firstname;

@Column(name="last_name")
@NotBlank
private String lastname;

@Column(name="email_id")
@NotBlank
private String email;

@Column(name="username")
@NotBlank
@Size(max = 20)
private String username;

@Column(name="password")
@NotBlank
@Size(max = 120)
private String password;

@Column(name = "account_non_locked")
private boolean accountNonLocked = true;

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(	name = "user_roles", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "role_id"))
private Set<Role> roles = new HashSet<>();


public User()
{
	
}



public User(long id, @NotBlank String firstname, @NotBlank String lastname,
		@NotBlank @Email @Size(max = 50) String email, @NotBlank @Size(max = 20) String username,
		@NotBlank @Size(max = 120) String password, boolean accountNonLocked, Set<Role> roles) {
	super();
	this.id = id;
	this.firstname = firstname;
	this.lastname = lastname;
	this.email = email;
	this.username = username;
	this.password = password;
	this.accountNonLocked = accountNonLocked;
	this.roles = roles;
}



public User(String firstname, String lastname,String email, String username,String password) {
	super();
	this.firstname = firstname;
	this.lastname = lastname;
	this.email = email;
	this.username = username;
	this.password = password;
}




public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getFirstname() {
	return firstname;
}
public void setFirstname(String firstname) {
	this.firstname = firstname;
}
public String getLastname() {
	return lastname;
}
public void setLastname(String lastname) {
	this.lastname = lastname;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}

public String getUsername() {
	return username;
}

public void setUsername(String username) {
	this.username = username;
}

public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}

public boolean isAccountNonLocked() {
	return accountNonLocked;
}

public void setAccountNonLocked(boolean accountNonLocked) {
	this.accountNonLocked = accountNonLocked;
}

public Set<Role> getRoles() {
	return roles;
}

public void setRoles(Set<Role> roles) {
	this.roles = roles;
}



}