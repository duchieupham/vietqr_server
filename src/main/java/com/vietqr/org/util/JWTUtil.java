package com.vietqr.org.util;

import com.vietqr.org.dto.AccountCustomerMerchantDTO;
import org.apache.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.ArrayList;
import java.util.List;

public class JWTUtil {
    private static final Logger logger = Logger.getLogger(JWTUtil.class);

    public static String getKeyFromToken(String token) {
        String result = "";
        try {
            if (token != null && !token.trim().isEmpty()) {
                String secretKey = "mySecretKey";
                String jwtToken = token.substring(7); // remove "Bearer " from the beginning
                Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
                String password = (String) claims.get("user");
                result = password;
            }
        } catch (Exception e) {
            logger.error("JWTUtil: getUserIdFromToken: ERROR: " + e.toString());
        }
        return result;
    }

    public static String getMerchantFromToken(String token) {
        String result = "";
        List<AccountCustomerMerchantDTO> merchant = new ArrayList<>();
        try {
            if (token != null && !token.trim().isEmpty()) {
                String secretKey = "mySecretKey";
                String jwtToken = token.substring(7); // remove "Bearer " from the beginning
                Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();
                String password = (String) claims.get("user");
                String merchants = (String) claims.get("merchant");
                result = password;
            }
        } catch (Exception e) {
            logger.error("JWTUtil: getUserIdFromToken: ERROR: " + e.toString());
        }
        return result;
    }

}
