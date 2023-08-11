package com.vietqr.org;

import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vietqr.org.security.JWTAuthorizationFilter;
import com.vietqr.org.util.WebSocketConfig;

@SpringBootApplication
@ComponentScan(basePackages = { "com.vietqr.org" })
@Import(WebSocketConfig.class)
@EnableWebMvc
public class VietqrApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {
		SpringApplication.run(VietqrApplication.class, args);

		// LarkUtil larkUtil = new LarkUtil();
		// larkUtil.sendMessageToLark("Thanh toán thành công 🎉."
		// + "\nTài khoản: 11******89"
		// + "\nGiao dịch: +50,000 VND"
		// + "\nMã giao dịch: FT23220783825900"
		// + "\nThời gian: 08/08/2023 15:30"
		// + "\nNội dung: VQR88c7832ac4 THANH TOAN NAP TIEN DIEN THOAI");

	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@EnableWebSecurity
	@Configuration
	@Order(1)
	class WebSecurityBasicConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		UserDetailsService userDetailsService;

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors().and().csrf().disable().requestMatcher(new AntPathRequestMatcher("/api/token_generate"))
					.authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.httpBasic();
		}

	}

	@EnableWebSecurity
	@Configuration
	@Order(2)
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors().and()
					.csrf().disable()
					.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/api/token_generate").permitAll()
					.antMatchers(HttpMethod.POST, "/bank/api/get_token_bank").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/login").permitAll()
					.antMatchers(HttpMethod.GET, "/api/accounts/search/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/register").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/logout").permitAll()
					.antMatchers(HttpMethod.POST, "/api/transaction-mms").permitAll()
					.antMatchers(HttpMethod.GET, "/api/bank-type/unauthenticated").permitAll()
					.antMatchers(HttpMethod.GET, "/api/bank-types").permitAll()
					.antMatchers(HttpMethod.GET, "/images/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/images/**").permitAll()
					.antMatchers(HttpMethod.GET, "/bank/api/account/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/qr/generate/unauthenticated").permitAll()
					.antMatchers(HttpMethod.POST, "/api/transaction/voice/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/transaction/rpa-sync").permitAll()
					.anyRequest().authenticated();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/socket"); // Thay đổi đường dẫn WebSocket endpoint của bạn
		}
	}

}
