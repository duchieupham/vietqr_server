package com.vietqr.org.controller;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vietqr.org.dto.ResponseMessageDTO;
import com.vietqr.org.dto.TokenDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api")
public class BearerTokenController {

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
	public ResponseEntity<ResponseMessageDTO> checkValidToken() {
		ResponseMessageDTO result = null;
		HttpStatus httpStatus = null;
		try {
			result = new ResponseMessageDTO("SUCCESS", "");
			httpStatus = HttpStatus.OK;
		} catch (Exception e) {
			result = new ResponseMessageDTO("FAILED", "Unexpected Error");
			httpStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(result, httpStatus);
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
