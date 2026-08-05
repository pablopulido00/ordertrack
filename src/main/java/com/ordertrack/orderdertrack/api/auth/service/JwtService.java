package com.ordertrack.orderdertrack.api.auth.service;


import com.ordertrack.orderdertrack.api.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.config.validate.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {


    private final JwtProperties jwtproperties;



    public String generateToken(UserDetails userDetails){
            return Jwts.builder()
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + jwtproperties.expiration()))
                    .signWith(getSigninKey())
                    .compact();

    }


    public String extractUserName(String token){
        return extractAllClaims(token).getSubject();
    }



    public boolean isTokenValid(String token, UserDetails userDetails){

        String username = extractUserName(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token){
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }


    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtproperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);

    }





















}
