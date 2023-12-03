package org.javacoders.messenger_03.controllers;

import java.util.concurrent.TimeUnit;

import org.javacoders.messenger_03.config.AppConstants;
import org.javacoders.messenger_03.exceptions.ApiException;
import org.javacoders.messenger_03.model.Status;
import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.JwtAuthenticationRequest;
import org.javacoders.messenger_03.payloads.JwtAuthenticationResponse;
import org.javacoders.messenger_03.payloads.NewPasswordRequest;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.payloads.VerificationRequest;
import org.javacoders.messenger_03.payloads.VerificationResponse;
import org.javacoders.messenger_03.repository.UserRepository;
import org.javacoders.messenger_03.security.JwtTokenHelper;
import org.javacoders.messenger_03.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/messenger/authentication")
public class AuthenticationController {
	
	private JwtTokenHelper jwtTokenHelper;
	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private UserService userService;
	private ModelMapper modelMapper;
	
	// Temporary DB while registration process
	private RedisTemplate<String, String> redisTemplate;
	
	public AuthenticationController(JwtTokenHelper jwtTokenHelper, AuthenticationManager authenticationManager,
			UserRepository userRepository, UserService userService, ModelMapper modelMapper,
			RedisTemplate<String, String> redisTemplate) {
		this.jwtTokenHelper = jwtTokenHelper;
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.userService = userService;
		this.modelMapper = modelMapper;
		this.redisTemplate = redisTemplate;
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> createToken(@RequestBody JwtAuthenticationRequest request) throws Exception {
		
		this.authenticate(request.getUsername(), request.getPassword());
		
		User user = this.userRepository
				.findByUsername(request.getUsername())
				.orElseThrow();
		
		user.setStatus(Status.ONLINE);
		
		String token = this.jwtTokenHelper.generateToken(user);
		JwtAuthenticationResponse response = new JwtAuthenticationResponse();
		response.setToken(token);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);
		return ResponseEntity.ok().headers(headers).body(this.modelMapper.map(user, UserDto.class));
	}
	
	@PostMapping("/register")
	public ResponseEntity<JwtAuthenticationResponse> createUser(@Valid @RequestBody UserDto userDto){
		
		// saving username and email till the verification completed
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		operations.set(userDto.getUsername(), userDto.getEmail(), 3, TimeUnit.MINUTES);
		
		// Sending email
		this.userService.sendVerificationToEmail(userDto.getEmail());
			
		// Generating JWT token
		User user = this.modelMapper.map(userDto, User.class);
		String token = this.jwtTokenHelper.generateToken(user);
		JwtAuthenticationResponse response = new JwtAuthenticationResponse();
		response.setToken(token);
		
		return new ResponseEntity<JwtAuthenticationResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping("/register/email/{email}/verify")
	public ResponseEntity<VerificationResponse> verifyNewUserEmail(
			@PathVariable String email,
			@RequestBody VerificationRequest verificationRequest){
		
		boolean success = this.userService.verifyConfirmationCode(email, verificationRequest.getCode());
		
		VerificationResponse response = new VerificationResponse();
		if(success) {
			response.setMessage(AppConstants.SUCCESSFUL_VERIFICATION_RESPONSE);
		} else {
			response.setMessage(AppConstants.UNSUCCESSFUL_VERIFICATION_RESPONSE);
		}
		response.setSuccess(success);
		return new ResponseEntity<VerificationResponse>(response, HttpStatus.OK);
	}
	
	@PostMapping("/register/username/{username}/setPassword")
	public ResponseEntity<UserDto> setPasswordForNewUser(
			@PathVariable String username,
			@RequestBody NewPasswordRequest newPasswordRequest) {
		
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		String email = operations.get(username);
		
		UserDto userDto = new UserDto();
		userDto.setUsername(username);
		userDto.setEmail(email);
		userDto.setPassword(newPasswordRequest.getPassword());
		
		User user = this.modelMapper.map(userDto, User.class);
		
		user.setStatus(Status.ONLINE);
		
		String token = this.jwtTokenHelper.generateToken(user);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		
		UserDto newUser = this.userService.registerNewUser(userDto);
		
		return ResponseEntity.ok().headers(headers).body(newUser);
	}
	
	private void authenticate(String username, String password) throws Exception {
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(username, password);
		try {
			this.authenticationManager.authenticate(authenticationToken);
		} catch (BadCredentialsException e) {
			throw new ApiException("Invalid Username or password !!");
		}
	}
}
