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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.vietqr.org.security.JWTAuthorizationFilter;
import com.vietqr.org.util.VNPTEpayUtil;
// import com.vietqr.org.util.VNPTEpayUtil;
// import com.vietqr.org.util.BankEncryptUtil;
import com.vietqr.org.util.WebSocketConfig;

@SpringBootApplication
@ComponentScan(basePackages = { "com.vietqr.org" })
@Import(WebSocketConfig.class)
@EnableWebMvc
public class VietqrApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {
		SpringApplication.run(VietqrApplication.class, args);
		// String partnerName = "partnerTest";
		// String requestId = VNPTEpayUtil.createRequestID(partnerName);
		// System.out.println("Request ID VNPT EPAY: " + requestId);
		// System.out.println("TOPUP");
		// VNPTEpayUtil.queryBalance("partnerTest");
		// VNPTEpayUtil.topup(requestId, "partnerTest", "VTT", "0931865469", 10000);
		// String sign = VNPTEpayUtil.sign("partnerTest",
		// "MIICXAIBAAKBgQC38UMua2xwkIhemWa/hAtpOcV1mjBDUmNt55RGmJOH2xh2zgoAGrcenWOKAqZggawPHT1GA6fP+577toM5mZcfgYf+S6ALx4OER901rS+d5IsZoOEw1f5f/7IfStES9o+QH0DVUUzGVrWexti6QizvnrCoUs5/N1a1uKGtCwtNiwIDAQABAoGBAKg4JWdraLWdCInzIotdWA44fkPp6d93lmTpl6nkWW+ySDJGhdDIndWKvIB3oe66SD9eTy4bo7nKdP/gTyw7MX9atn7Rt41pNeHVx6UNkuoo6lmevvB3hsSYK0/XtccQu8QkG8nTO4v0ffn6NOp8EMVYWhIUVWdZpcc7Bg9E54UhAkEA3cyO1vthUAlRO/f34sRkctr0beYSd38nZ+HiPxIxtZd++bZLjoNxQFE7XXZaOBfTdrDpB+1cLBXi1jO77dCGewJBANROU8FhNFufe7uGVpSSHrPGjwuw8UMkgKZnkgKnVmVnY+dLwBmmcZKTdur5zWkD4LmVk/83Np578pHeCmv3sDECQAewb8NEM7tylz5c+lsCM/lHXfHxZ/J8lgEj85P8LXz+I4jEqRnntKVmK4ix8a7AJLSYrXt43xkoKRUD9h/oesECQAvIMQO5VkODbUVx5hompceKTGP0tN7qBq21b7fv+25zN2sdnBKQVxswTdgqdsjOK0mBUI5ITSKsdEC+Fsv5GfECQG68h8sCzSQxSsYhjn0lmSzetuJ5wdnGluOu25NEtelrwYmoAaHamifLXl/dCs/7Am5m0OnFKVwo8MbXabP03dk=");
		// System.out.println("sign: " + sign);
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
