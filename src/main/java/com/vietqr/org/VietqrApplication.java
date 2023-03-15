package com.vietqr.org;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
//import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vietqr.org.security.JWTAuthorizationFilter;

@SpringBootApplication
// @EnableScheduling
public class VietqrApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		SpringApplication.run(VietqrApplication.class, args);
	}

	@EnableWebSecurity
	@Configuration
	@Order(1)
	class WebSecurityBasicConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			// UAT user mb bank
			// auth.inMemoryAuthentication()
			// .withUser("3")
			// .password(passwordEncoder().encode("MBK3cG76F"))
			// .authorities("ROLE_USER")
			// .and()
			// Product user mb bank
			auth.inMemoryAuthentication()
					.withUser("b-mb-user3")
					.password(passwordEncoder().encode("Yi1tYi11c2VyMw=="))
					.authorities("ROLE_USER")
					.and()
					.withUser("admin")
					.password(passwordEncoder().encode("Aa_123456789"))
					.authorities("ROLE_ADMIN");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().requestMatcher(new AntPathRequestMatcher("/api/token_generate")).authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.httpBasic();
		}

		@Bean
		PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	}

	@EnableWebSecurity
	@Configuration
	@Order(2)
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
					.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/api/token_generate").permitAll()
					.antMatchers(HttpMethod.POST, "/api/token_generate_without_auth").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/register").permitAll()
					.anyRequest().authenticated();
		}
	}

}
