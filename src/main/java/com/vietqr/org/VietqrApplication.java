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
import com.vietqr.org.util.BankEncryptUtil;
import com.vietqr.org.util.BankRSAUtil;
import com.vietqr.org.util.RandomCodeUtil;
import com.vietqr.org.util.TransactionRefIdUtil;
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

		// ENCRYPT - DECRYPT TRANSACTION ID
		String transactionId = "a65ade25-e25d-4809-94ad-8f817e7e94ac";
		String encrypted = TransactionRefIdUtil.encryptTransactionId(transactionId);
		String decrypted = TransactionRefIdUtil.decryptTransactionId(encrypted);

		System.out.println("TRANSACTION ENCRYPTED: " + encrypted);
		System.out.println("TRANSACTION DECRYPTED: " + decrypted);

		String checkSum = BankEncryptUtil.generateMD5CheckOrderChecksum("1123355589",
				"system-admin-user2302");
		System.out.println("CHECKSUM: " + checkSum);

		// get random request Payment MB Bank
		String randomCode = "RVCK" + RandomCodeUtil.generateRandomId(8);
		System.out.println("randomCode: " + randomCode);

		//
		String checkSum2 = BankEncryptUtil.generateMD5RefundCustomerChecksum("1123355589",
				"FT23293978692076", "SABAccessKey");
		System.out.println("CHECKSUM REFUND: " + checkSum2);
		// int durationMonths = 6;
		// String startDateString = calculateStartDate(durationMonths);
		// String endDateString = calculateEndDate(startDateString, durationMonths);

		// System.out.println("Start Date: " + startDateString);
		// System.out.println("End Date: " + endDateString);

		// String inputString = "QRVQR1b49cbd3af VRCysDQYbqPH0- Ma GD ACSP/ 0m483714 NG
		// CHUYEN:CUSTOMER 1123355589";
		// String prefix = "VQR";
		// String traceId = getTraceId(inputString, prefix);
		// System.out.println("===============TRACE ID: " + traceId);
		//
		// String password = "Hokdoithu1997";
		// String encryptAESPassword = AESUtil.encrypt(password);
		// String decryptAESPassword = AESUtil.decrypt(encryptAESPassword);
		// System.out.println("en: " + encryptAESPassword);
		// System.out.println("de: " + decryptAESPassword);
		//
		String bankAccountEncrypted = BankEncryptUtil.encrypt("0300105672008");
		System.out.println("bankAccountEncrypted: " + bankAccountEncrypted);

		// LogReaderUtil.readLogFile("2023-09-13");

		// String customerName = "NGUYEN VAN A";
		// String personalId = "387782195958";
		// String phoneNumber = "0886524111";
		// String sourceNumber = "9704222070155452";
		String valueToEncode = "RSID-eef52137-86b2-4812-bc05-54a522fbf226" + "USER NAME TEST" + "5169867955365"
				+ "NGUYEN VAN A"
				+ "0868525356" + "10000";
		String result = BankRSAUtil.generateSignature(valueToEncode);
		System.out.println("result: " + result);
		System.out.println("Verify data: " +
				BankRSAUtil.verifySignature(valueToEncode, result));

		// String prefix = "unassign";
		// String resourceBank = "RSID-eef52137-86b2-4812-bc05-54a522fbf226";
		// String result2 = BankRSAUtil.generateSignature(prefix + resourceBank);
		// System.out.println("result2: " + result2);
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

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
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
					.antMatchers(HttpMethod.GET, "/api/export-transactions").permitAll()
					.antMatchers(HttpMethod.GET, "/api/merchant/transactions-export").permitAll()
					.anyRequest().authenticated();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/socket"); // Thay đổi đường dẫn WebSocket endpoint của bạn
		}
	}

}
