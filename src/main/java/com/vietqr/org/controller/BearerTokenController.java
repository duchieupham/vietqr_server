package com.vietqr.org.controller;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenDTO;
import com.vietqr.org.service.AccountSettingService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class BearerTokenController {
	private static final Logger logger = Logger.getLogger(BearerTokenController.class);

	@Autowired
	AccountSettingService accountSettingService;

	@PostMapping("token_generate")
	public ResponseEntity<TokenDTO> getToken(HttpServletRequest request) {
		TokenDTO result = null;
		HttpStatus httpStatus = null;
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.toLowerCase().startsWith("basic")) {
			String base64Credentials = authHeader.substring("Basic".length()).trim();
			byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
			String credentials = new String(credDecoded, StandardCharsets.UTF_8);
			// credentials = "username:password"
			final String[] values = credentials.split(":", 2);
			String username = values[0];
			try {
				// Do something with username and password
				result = new TokenDTO(getJWTToken(Base64.getEncoder().encodeToString(username.getBytes())), "Bearer",
						59);
				httpStatus = HttpStatus.OK;
			} catch (Exception e) {
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		} else {
			httpStatus = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	@GetMapping("token")
	public ResponseEntity<ResponseMessageDTO> checkValidToken(
			@RequestHeader("Authorization") String token) {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			String userId = getUserIdFromToken(token);
			System.out.println("userId: " + userId);
			if (userId != null && !userId.trim().isEmpty()) {
				updateAccessLogin(userId);
			}
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
	}

	private String getUserIdFromToken(String token) {
		String result = "";
		if (token != null && !token.trim().isEmpty()) {
			String secretKey = "mySecretKey";
			String jwtToken = token.substring(7); // remove "Bearer " from the beginning
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
			String userId = (String) claims.get("userId");
			result = userId;
		}
		return result;
	}

	void updateAccessLogin(String userId) {
		try {
			Long currentCount = accountSettingService.getAccessCountByUserId(userId);
			long accessCount = 0;
			if (currentCount != null) {
				accessCount = currentCount + 1;
			}
			LocalDateTime currentDateTime = LocalDateTime.now();
			long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
			accountSettingService.updateAccessLogin(time, accessCount, userId);
		} catch (Exception e) {
			System.out.println("updateAccessLogin: ERROR: " + e.toString());
			logger.error("updateAccessLogin: ERROR: " + e.toString());
		}
	}

	private String getJWTToken(String username) {
		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");

		String token = Jwts
				.builder()
				// .claim("grantType",grantType)
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.claim("user", username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 59000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes())
				.compact();
		return token;
	}
}
