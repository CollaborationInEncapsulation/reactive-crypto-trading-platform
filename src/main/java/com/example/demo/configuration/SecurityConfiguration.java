package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
		                       .username("admin")
		                       .password("admin")
		                       .roles("ADMIN")
		                       .build();
		return new MapReactiveUserDetailsService(user);
	}

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
				.authorizeExchange()
				.anyExchange().permitAll()
				.and()
				.formLogin();
		return http.build();
	}
}
