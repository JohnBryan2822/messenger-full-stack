package org.javacoders.messenger_03;

import java.util.List;

import org.javacoders.messenger_03.config.AppConstants;
import org.javacoders.messenger_03.model.Role;
import org.javacoders.messenger_03.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Messenger03Application implements CommandLineRunner {
	
	private RoleRepository roleRepository;

	public Messenger03Application(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(Messenger03Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			Role role1 = new Role();
			role1.setId(AppConstants.ADMIN_USER);
			role1.setName("ROLE_ADMIN");
			
			Role role2 = new Role();
			role2.setId(AppConstants.NORMAL_USER);
			role2.setName("ROLE_NORMAL");
			
			List<Role> result = this.roleRepository.saveAll(List.of(role1, role2));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
