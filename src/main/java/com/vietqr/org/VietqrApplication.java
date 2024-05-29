package com.vietqr.org;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Base64;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.AesKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
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

import com.google.gson.Gson;
// import com.vietqr.org.controller.TransactionBankController;
// import com.vietqr.org.dto.TokenDTO;
import com.vietqr.org.dto.example.Header;
import com.vietqr.org.dto.example.JweObj;
import com.vietqr.org.dto.example.Recipients;
// import com.vietqr.org.entity.CustomerSyncEntity;
import com.vietqr.org.security.JWTAuthorizationFilter;
import com.vietqr.org.util.BankEncryptUtil;
// import com.vietqr.org.util.BankEncryptUtil;
import com.vietqr.org.util.JwsUtil;
import com.vietqr.org.util.WebSocketConfig;
import com.vietqr.org.util.bank.bidv.CustomerVaUtil;

@SpringBootApplication
@ComponentScan(basePackages = { "com.vietqr.org" })
@Import(WebSocketConfig.class)
@EnableWebMvc
@EnableScheduling
//@EnableFeignClients
public class VietqrApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void test() {
		try {
			String clientXCertificate = JwsUtil.getClientXCertificate();
			System.out.println("client x certificate: " + clientXCertificate + "\n\n\n\n");
			///
			String myKey = JwsUtil.getSymmatricKey();
			// byte[] decodedKey =
			// Base64.getDecoder().decode("746163353431386f6b6d6e626c6f7071746163353431386f6b6d6e626c6f7033");
			Key key = new AesKey(JwsUtil.hexStringToBytes(myKey));
			JsonWebEncryption jwe = new JsonWebEncryption();
			// String payload =
			// "{\"RequestDateTime\":\"2022-12-01T02:54:32.746Z\",\"RequestID\":\"1669863273\",\"Language\":\"vi\",\"Data\":{\"CifNo\":\"136030\",\"channel\":\"SMB\"}}";
			// JWE
			String payload = JwsUtil.getPayLoad();
			jwe.setPayload(payload);
			jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
			jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
			jwe.setKey(key);
			String serializedJwe = jwe.getCompactSerialization();
			String[] split = serializedJwe.split("\\.");
			Gson gson = new Gson();
			String protected_ = split[0];
			byte[] decodedBytes = Base64.getDecoder().decode(protected_);
			String decodedString = new String(decodedBytes);
			Header h = gson.fromJson(decodedString, Header.class);
			String encryptedKey = split[1];
			String iv = split[2];
			String ciphertext = split[3];
			String tag = split[4];
			Recipients recipient = new Recipients();
			recipient.setHeader(h);
			recipient.setEncrypted_key(encryptedKey);
			Recipients[] recipients = new Recipients[1];
			recipients[0] = recipient;
			JweObj j = new JweObj(recipients, protected_, ciphertext, iv, tag);
			String jweString = gson.toJson(j);
			System.out.println("\n\n\n\nSerialized Encrypted JWE: " + jweString + "\n\n\n\n");
			// JWS
			JsonWebSignature jws = new JsonWebSignature();
			jws.setPayload(jweString);
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			PrivateKey privateKey = JwsUtil.getPrivateKey();
			jws.setKey(privateKey);
			String jwsString = jws.getCompactSerialization();
			System.out.println("Serialized Signed JWS: " + jwsString);
			// System.out.println(j.toString());
		} catch (Exception e) {
			System.out.println("test: ERROR: " + e.toString());
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {
		SpringApplication.run(VietqrApplication.class, args);
		// String checksum =
		// BankEncryptUtil.generateMD5GetBillForBankChecksum("QklEVkJMVUVDT01BY2Nlc3NLZXk=",
		// "BC0001",
		// "BCBI00004");
		// System.out.println("Checksum: " + checksum);

		// String checksum2 =
		// BankEncryptUtil.generateMD5PayBillForBankChecksum("QklEVkJMVUVDT01BY2Nlc3NLZXk=",
		// "0000000000017", "hSyeSrX9fW", "11000000");

		// System.out.println("Checksum2: " + checksum2);

		// test();
		// CustomerSyncEntity customerSyncEntity = new CustomerSyncEntity("",
		// "test-viet-qr",
		// "RCP4#qpqP7(z4qrv@8c@b4%(JaCe9DdD&N5wB$wuBbTWF&apN3aEkVJV4+DrMFvy ", "", "",
		// "vqr",
		// "https://vietqr.muadi.com.vn", true, "", "", "", false, "", "");
		// TokenDTO tokenDTO = TransactionBankController.getCustomerSyncToken2("",
		// customerSyncEntity, 0);
		// System.out.println("token customer: " + tokenDTO.getAccess_token());
		//
		// generate check sum mms sync
		// String dataCheckSum = BankEncryptUtil.generateMD5Checksum("test09",
		// "", "20231030224801", "1000");
		// System.out.println("dataCheckSum: " + dataCheckSum);
		//
		// ENCRYPT - DECRYPT TRANSACTION ID
		// String transactionId = "a65ade25-e25d-4809-94ad-8f817e7e94ac";
		// String encrypted = TransactionRefIdUtil.encryptTransactionId(transactionId);
		// String decrypted = TransactionRefIdUtil.decryptTransactionId(encrypted);

		// System.out.println("TRANSACTION ENCRYPTED: " + encrypted);
		// System.out.println("TRANSACTION DECRYPTED: " + decrypted);

		// String checkSum = BankEncryptUtil.generateMD5CheckOrderChecksum("202348866",
		// "customer-appgle-user2352");
		// System.out.println("CHECKSUM: " + checkSum);

		// // get random request Payment MB Bank
		// String randomCode = "RVCK" + RandomCodeUtil.generateRandomId(8);
		// System.out.println("randomCode: " + randomCode);

		// //
		// String checkSum2 =
		// BankEncryptUtil.generateMD5RefundCustomerChecksum("1123355589",
		// "FT23293978692076", "SABAccessKey");
		// System.out.println("CHECKSUM REFUND: " + checkSum2);

		String bankAccountEncrypted = BankEncryptUtil.encrypt("5553231868888");
		System.out.println("bankAccountEncrypted: " + bankAccountEncrypted);

		/// generate signature to request payment MB
		// String valueToEncode = "RSID-eef52137-86b2-4812-bc05-54a522fbf226" + "USER
		/// NAME TEST" + "5169867955365"
		// + "NGUYEN VAN A"
		// + "0868525356" + "25000";
		// String result = BankRSAUtil.generateSignature(valueToEncode);
		// System.out.println("result: " + result);
		// System.out.println("Verify data: " +
		// BankRSAUtil.verifySignature(valueToEncode, result));

		/////
		/////
		/////

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

		// LogReaderUtil.readLogFile("2023-09-13");

		// String customerName = "NGUYEN VAN A";
		// String personalId = "387782195958";
		// String phoneNumber = "0886524111";
		// String sourceNumber = "9704222070155452";

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
					.antMatchers(HttpMethod.POST, "/api/bidv/token_generate").permitAll()
					.antMatchers(HttpMethod.POST, "/api/peripheral/token_generate").permitAll()
					.antMatchers(HttpMethod.POST, "/api/vcard/generate").permitAll()
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
					.antMatchers(HttpMethod.POST, "/api/accounts/password/reset").permitAll()
					.antMatchers(HttpMethod.GET, "/api/bank-type/unauthenticated").permitAll()
					.antMatchers(HttpMethod.GET, "/api/bank-types").permitAll()
					//.antMatchers(HttpMethod.GET, "/bank/api/transactions/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/accountLoginSync/**").permitAll()
					.antMatchers(HttpMethod.GET, "/images/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/images/**").permitAll()
					.antMatchers(HttpMethod.GET, "/bank/api/account/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/qr/generate/unauthenticated").permitAll()
					.antMatchers(HttpMethod.GET, "/api/transactions/qr-link").permitAll()
					.antMatchers(HttpMethod.POST, "/api/transactions/qr-link/cancel").permitAll()
					.antMatchers(HttpMethod.POST, "/api/transaction/voice/**").permitAll()
					.antMatchers(HttpMethod.POST, "/api/transaction/rpa-sync").permitAll()
					.antMatchers(HttpMethod.GET, "/api/export-transactions").permitAll()
					.antMatchers(HttpMethod.GET, "/api/merchant/transactions-export").permitAll()
					.antMatchers(HttpMethod.POST, "/api/clickup/task-supporter").permitAll()
					.antMatchers(HttpMethod.POST, "/api/qr/generate-image").permitAll()
					.antMatchers(HttpMethod.GET, "/api/system-setting").permitAll()
					.antMatchers(HttpMethod.GET, "/api/terminal/web/member-detail/export/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/terminal/web/export/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/terminal/web/transaction-detail/export/**").permitAll()
					.antMatchers(HttpMethod.GET, "/api/terminal/export-excel").permitAll()
					.anyRequest().authenticated();
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/socket"); // Thay đổi đường dẫn WebSocket endpoint của bạn
		}
	}

}
