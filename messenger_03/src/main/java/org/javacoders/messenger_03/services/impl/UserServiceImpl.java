package org.javacoders.messenger_03.services.impl;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.javacoders.messenger_03.config.AppConstants;
import org.javacoders.messenger_03.model.Role;
import org.javacoders.messenger_03.model.User;
import org.javacoders.messenger_03.payloads.UserDto;
import org.javacoders.messenger_03.repository.RoleRepository;
import org.javacoders.messenger_03.repository.UserRepository;
import org.javacoders.messenger_03.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;


@Service
public class UserServiceImpl implements UserService {
	
	// Temporary DB while registration process
	private RedisTemplate<String, String> redisTemplate;
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;

	public UserServiceImpl(RedisTemplate<String, String> redisTemplate, UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
		super();
		this.redisTemplate = redisTemplate;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.modelMapper = modelMapper;
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);
		
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		
		Role role = this.roleRepository.findById(AppConstants.NORMAL_USER).get();
		user.getRoles().add(role);
		
		User newUser = this.userRepository.save(user);
		
		return this.modelMapper.map(newUser, UserDto.class);
	}

	@Override
	public boolean sendVerificationToEmail(String email) {
		String randomCode = generateConfirmationCode();
		
		Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 587);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.transport.protocol", "smtp");
        
        // code will be sent from this email
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("otaboyev149@gmail.com", "zoebjqhtjtstfjcb");
            }
        });
        
        try {

            // preparing message to send
            MimeMessage code = new MimeMessage(session);
            code.setSubject("Confirmation from Messenger application");
            code.setText("Confirmation code: " + randomCode);

            // adding email address
            Address addressTo = new InternetAddress(email);
            code.setRecipient(Message.RecipientType.TO, addressTo);

            // sending....
            Transport.send(code);
            
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
    		operations.set(email, randomCode, 3, TimeUnit.MINUTES);
    		
    		return true;

        } catch (Exception e){
            e.printStackTrace();
        }
        
        return false;
	}
	
	@Override
	public boolean verifyConfirmationCode(String email, String password) {
		ValueOperations<String, String> operations = redisTemplate.opsForValue();
		String generatedPassword = operations.get(email);
		
		if(password.equals(generatedPassword)) {
//			operations.getAndExpire(email, 1, TimeUnit.SECONDS);
			return true;
		}
		return false;
	}

	private String generateConfirmationCode() {
		Random random = new Random();
		return Integer.toString(100000 + random.nextInt(900000));
	}
}
