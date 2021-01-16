package com.example.springboot.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.payload.JwtResponse;
import com.example.springboot.payload.LoginRequest;
import com.example.springboot.exception.ResourceNotFoundException;
import com.example.springboot.model.ERole;
import com.example.springboot.model.Role;
import com.example.springboot.model.User;
import com.example.springboot.payload.SignupRequest;
import com.example.springboot.payload.UpdateRequest;
import com.example.springboot.payload.MessageResponse;
import com.example.springboot.services.UserDetailsImpl;
import com.example.springboot.repository.*;
import com.example.springboot.security.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;
	
	//get All Users
	
		@GetMapping("/users")
		public List <User> getAllUsers()
		{
			return userRepository.findAll();
		}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginrequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginrequest.getUsername(), loginrequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getFirstname(),signUpRequest.getLastname(),signUpRequest.getEmail(),signUpRequest.getUsername(),encoder.encode(signUpRequest.getPassword()));
							 

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	// create user rest api
		@PostMapping("/users")
		public User createUser(@RequestBody User user)
		{
			user.setPassword(encoder.encode(user.getPassword()));
			return userRepository.save(user);
		}
	
	//get User by REST API
		@GetMapping("/users/{id}")
		public ResponseEntity<User> getUserById(@PathVariable Long id) {
			
			User user =userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :"+id));
			return ResponseEntity.ok(user);
			
		}
		
		//update user rest api
		@PutMapping("/users/{id}")
		public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userdetails)
		{
			
			User user =userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :"+id));
			user.setFirstname(userdetails.getFirstname());
			user.setLastname(userdetails.getLastname());
			user.setEmail(userdetails.getEmail());
			user.setUsername(userdetails.getUsername());
			user.setPassword(encoder.encode(userdetails.getPassword()));
			User updatedUser=userRepository.save(user);
			return ResponseEntity.ok(updatedUser);
			
			
		}
		
		@PutMapping("/users/{id}/lock")
		public ResponseEntity<User> lockUser(@PathVariable Long id)
		{
			
			User user =userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :"+id));
			user.setAccountNonLocked(false);
			User updatedUser=userRepository.save(user);
			return ResponseEntity.ok(updatedUser);
			
			
		}
		
		@PutMapping("/users/{id}/unlock")
		public ResponseEntity<User> unlockUser(@PathVariable Long id)
		{
			
			User user =userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :"+id));
			user.setAccountNonLocked(true);
			User updatedUser=userRepository.save(user);
			return ResponseEntity.ok(updatedUser);
			
			
		}
		
		//delete user rest api
		@DeleteMapping("/users/{id}")
		public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id)
		{
			
			User user =userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :"+id));
			userRepository.delete(user);
			Map<String,Boolean> response=new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			return ResponseEntity.ok(response);
			
		}
		
		
		@PutMapping("/users/{id}/roles")
		public ResponseEntity<User> updateUserRoles(@PathVariable Long id,@Valid @RequestBody UpdateRequest updateRequest) {
			// Create new user's account
			User user =userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exist with id :"+id));

			Set<String> strRoles = updateRequest.getRole();
			Set<Role> roles = new HashSet<>();

			if (strRoles == null) {
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				roles.add(userRole);
			} else {
				strRoles.forEach(role -> {
					switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;
					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_USER)
								.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
					}
				});
			}

			user.setRoles(roles);
			User updatedUser=userRepository.save(user);
			return ResponseEntity.ok(updatedUser);

			
		}

		
		


}
