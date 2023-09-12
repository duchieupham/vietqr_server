package com.vietqr.org;

import java.io.IOException;

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
import com.vietqr.org.util.AESUtil;
// import com.vietqr.org.util.BankEncryptUtil;
// import com.vietqr.org.util.BankEncryptUtil;
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
		//
		// String password = "Hokdoithu1997";
		// String encryptAESPassword = AESUtil.encrypt(password);
		// String decryptAESPassword = AESUtil.decrypt(encryptAESPassword);
		// System.out.println("en: " + encryptAESPassword);
		// System.out.println("de: " + decryptAESPassword);
		//
		// String bankAccountEncrypted = BankEncryptUtil.encrypt("0706550526");
		// System.out.println("bankAccountEncrypted: " + bankAccountEncrypted);

		// String customerName = "NGUYEN VAN A";
		// String personalId = "387782195958";
		// String phoneNumber = "0886524111";
		// String sourceNumber = "9704222070155452";
		// String valueToEncode = customerName + personalId + phoneNumber +
		// sourceNumber;
		// String result = BankRSAUtil.generateSignature(valueToEncode);
		// System.out.println("result: " + result);
		// System.out.println("Verify data: " +
		// BankRSAUtil.verifySignature(valueToEncode, result));

		// String prefix = "unassign";
		// String resourceBank = "";
		// String result2 = BankRSAUtil.generateSignature(prefix + resourceBank);
		// String resourceCard = "";
		// String result3 = BankRSAUtil.generateSignature(prefix + resourceCard);
		// System.out.println("result2: " + result2);
		// System.out.println("result3: " + result3);
		// List<String> data = new ArrayList<>();
		// data.add("Thanh toan QR-VQR869d78447a VRCfc8QjM2sCA NG CHUYEN:CUSTOMER");
		// data.add("VQR303f2894 ce");
		// data.add("VQR303f2894.ce");
		// data.add("VQR303f2894 c eVRC");
		// data.add("VQR303f2894 ceVRC");
		// data.add("VQR303f2894ceVQC");
		// data.add("VQR303f2894ce");
		// data.add("VQR 303f2894ce");
		// data.add("V QR303f2894ce");
		// data.add("VQ R 303f 2894 ce");
		// data.add(
		// "MBVCB.4125261053.047334.VQR303f2894 ce VRCbC2btHFanN.CT tu 9000006789 P HAM
		// DUC TUAN toi 1123355589 PHAM DU C HIEU tai MB- Ma GD ACSP/ cw047334
		// 1123355589");
		// data.add(
		// "MBVCB.4125261053.047334.V.QR303f2894 ce VRCbC2btHFanN.CT tu 9000006789 P HAM
		// DUC TUAN toi 1123355589 PHAM DU C HIEU tai MB- Ma GD ACSP/ cw047334
		// 1123355589");
		// data.add(
		// "MBVCB.4125261053.0473 34.VQR303f2894 ce VRCbC2btHFanN.CT tu 9000006789 P HAM
		// DUC TUAN toi 1123355589 PHAM DU C HIEU tai MB- Ma GD ACSP/ cw047334
		// 1123355589");
		// data.add(
		// "MBVCB.4125261053.047334.VQR303f2894 ceVV VRCbC2btHFanN.CT tu 9000006789 P
		// HAM DUC TUAN toi 1123355589 PHAM DU C HIEU tai MB- Ma GD ACSP/ cw047334
		// 1123355589");
		// data.add(
		// "MBVCB.4125261053.047334.VQR303f2894ceVRCbC2btHFanN.CT tu 9000006789 P HAM
		// DUC TUAN toi 1123355589 PHAM DU C HIEU tai MB- Ma GD ACSP/ cw047334
		// 1123355589");
		// int count = 0;
		// for (String item : data) {
		// String traceId = extractCode(item);
		// System.out.println("No: " + count + " - input: " + item + " - output: " +
		// traceId);
		// count++;
		// }
	}

	// public static String extractCode(String inputString) {
	// String result = "";
	// try {
	// inputString = inputString.replaceAll("\\.", " ");
	// inputString = inputString.replaceAll("\\-", " ");
	// String[] newPaths = inputString.split("\\s+");

	// String traceId = "";
	// int indexSaved = -1;

	// for (int i = 0; i < newPaths.length; i++) {
	// if (newPaths[i].contains("VQR")) {
	// if (newPaths[i].length() >= 13) {
	// traceId = newPaths[i].substring(0, 13);
	// break;
	// }
	// traceId = newPaths[i];
	// indexSaved = i;
	// } else if (indexSaved != -1 && i == indexSaved + 1) {
	// if (traceId.length() < 13) {
	// traceId += newPaths[i].substring(0, Math.min(13 - traceId.length(),
	// newPaths[i].length()));
	// }
	// }
	// }

	// if (!traceId.isEmpty()) {
	// String pattern = "VQR.{10}";
	// Pattern r = Pattern.compile(pattern);
	// Matcher m = r.matcher(traceId);
	// if (m.find()) {
	// traceId = m.group(0);
	// } else {
	// traceId = "";
	// }
	// }

	// result = traceId;
	// } catch (Exception e) {
	// System.out.println("get traceId: ERROR: " + e.toString());
	// }
	// return result;
	// }

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// configuration.setAllowedOrigins(Arrays.asList("*"));
		// configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
		// "OPTIONS"));
		// configuration.setAllowedHeaders(Arrays.asList("Authorization",
		// "Content-Type"));
		///
		// Thêm các tiêu đề bổ sung cho hỗ trợ Multipart
		// configuration.setAllowedHeaders(Arrays.asList("Authorization",
		// "Content-Type", "X-Requested-With", "accept",
		// "Origin", "Access-Control-Request-Method",
		// "Access-Control-Request-Headers"));
		// configuration
		// .setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin",
		// "Access-Control-Allow-Credentials"));
		///
		configuration.addAllowedOrigin("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
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
					.antMatchers(HttpMethod.POST, "/api/accounts-sms").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts-admin").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/login").permitAll()
					.antMatchers(HttpMethod.GET, "/api/accounts/search/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/accounts-sms/search/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/register").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts-sms/register").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts/logout").permitAll()
					.antMatchers(HttpMethod.POST, "/api/accounts-sms/logout").permitAll()
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
