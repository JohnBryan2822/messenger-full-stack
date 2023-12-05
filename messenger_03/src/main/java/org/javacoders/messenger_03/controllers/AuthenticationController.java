package org.javacoders.messenger_03.controllers;

import java.util.concurrent.TimeUnit;

import org.javacoders.messenger_03.config.AppConstants;
import org.javacoders.messenger_03.exceptions.ApiException;
import org.javacoders.messenger_03.model.Status;
import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.JwtAuthenticationRequest;
import org.javacoders.messenger_03.payloads.NewPasswordRequest;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.payloads.VerificationRequest;
import org.javacoders.messenger_03.payloads.VerificationResponse;
import org.javacoders.messenger_03.repository.UserRepository;
import org.javacoders.messenger_03.security.JwtTokenHelper;
import org.javacoders.messenger_03.services.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/messenger/authentication")
public class AuthenticationController {
	
	private JwtTokenHelper jwtTokenHelper;
	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private UserService userService;
	
	// Temporary DB while registration process
	private RedisTemplate<String, String> redisTemplate;
	
	public AuthenticationController(JwtTokenHelper jwtTokenHelper, AuthenticationManager authenticationManager,
			UserRepository userRepository, UserService userService,
			RedisTemplate<String, String> redisTemplate) {
		super();
		this.jwtTokenHelper = jwtTokenHelper;
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.userService = userService;
		this.redisTemplate = redisTemplate;
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> createToken(@RequestBody JwtAuthenticationRequest request,
			HttpServletResponse response) throws Exception {
		this.authenticate(request.getUsername(), request.getPassword());
		
		User user = this.userRepository
				.findByUsername(request.getUsername())
				.orElseThrow();
		
		user.setStatus(Status.ONLINE);
		String token = this.jwtTokenHelper.generateToken(user);
		Cookie cookie = new Cookie("jwtToken", token);
		cookie.setHttpOnly(true);
//      cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setPath("/");
        response.addCookie(cookie);
        
        UserDto userDto = UserDto.builder()
        		.id(user.getId())
        		.email(user.getEmail())
        		.username(user.getUsername())
        		.lastSeen(user.getLastSeen())
        		.status(user.getStatus())
        		.build();
		
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserDto> createUser(
			@Valid @RequestBody UserDto userDto,
			HttpServletResponse response){
		
		// saving username and email till the verification completed
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		operations.set(userDto.getUsername(), userDto.getEmail(), 3, TimeUnit.MINUTES);
		
		// Sending email
		this.userService.sendVerificationToEmail(userDto.getUsername(), userDto.getEmail());
			
		// Generating JWT token
		User user = User.builder()
				.username(userDto.getUsername())
				.email(userDto.getEmail())
				.status(Status.ONLINE)
				.build();
		String token = this.jwtTokenHelper.generateToken(user);
		
		Cookie cookie = new Cookie("jwtToken", token);
		cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        
		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}
	
	@PostMapping("/register/verify")
	public ResponseEntity<VerificationResponse> verifyNewUserEmail(
			@RequestBody VerificationRequest verificationRequest,
			HttpServletRequest request){
		
		String token = this.userService.extractJwtFromRequest(request);
		boolean success = this.userService.verifyConfirmationCode(
								this.jwtTokenHelper.getUsernameFromToken(token), 
								verificationRequest.getCode());
		
		VerificationResponse verificationResponse = new VerificationResponse();
		if(success) {
			verificationResponse.setMessage(AppConstants.SUCCESSFUL_VERIFICATION_RESPONSE);
		} else {
			verificationResponse.setMessage(AppConstants.UNSUCCESSFUL_VERIFICATION_RESPONSE);
		}
		verificationResponse.setSuccess(success);
		return new ResponseEntity<VerificationResponse>(verificationResponse, HttpStatus.OK);
	}
	
	@PostMapping("/register/setPassword")
	public ResponseEntity<UserDto> setPasswordForNewUser(
			@RequestBody NewPasswordRequest newPasswordRequest,
			HttpServletRequest request) {
		
		String token = this.userService.extractJwtFromRequest(request);
		String username = this.jwtTokenHelper.getUsernameFromToken(token);
		
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		String email = operations.get(username);
		
		User user = User.builder()
			.username(username)
			.email(email)
			.password(newPasswordRequest.getPassword())
			.status(Status.ONLINE)
			.build();
		
		UserDto newUser = this.userService.registerNewUser(user);
		return new ResponseEntity<UserDto>(newUser, HttpStatus.OK);
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
