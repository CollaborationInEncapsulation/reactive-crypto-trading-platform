package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsManager userDetailsService() {
		UserDetails userAdmin = User.withDefaultPasswordEncoder()
		                            .username("admin")
		                            .password("admin")
		                            .roles("ADMIN")
		                            .build();
		UserDetails userUser = User.withDefaultPasswordEncoder()
		                            .username("user")
		                            .password("user")
		                            .roles("USER")
		                            .build();
		return new InMemoryUserDetailsManager(userAdmin, userUser);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.anyRequest().permitAll()
				.and()
				.formLogin();
	}
}
