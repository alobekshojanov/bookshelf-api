package com.alobek.bookshelf.util;

import com.alobek.bookshelf.dto.JwtDTO;
import com.alobek.bookshelf.enums.ProfileRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {

    private static final int tokenLiveTime = 1000 * 3600 * 24; // for one day
    private static final String secretKey = "verylongStringFileforsecretkeyitisveryverylongandimportantthinginjwt";


    public static String encode(String username, List<ProfileRole> roleList, Integer id){
        String strList = roleList.stream().map(Enum::name)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", strList);
        claims.put("id", String.valueOf(id));

        return Jwts
                .builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .signWith(getSignInKey())
                .compact();
    }

    public static String encode1(Integer id){
        /*Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("username", username);
        extraClaims.put("role", role);*/
        return Jwts
                .builder()
                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey())
                .compact();
    }

    public static JwtDTO decode(String token){
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String username = claims.getSubject();
        Integer id = Integer.valueOf((String) claims.get("id"));
        String strRole = (String) claims.get("roles");
        // "ROLE_USER,ROLE_ADMIN"
       /* String[] roleArray = strRole.split(",");
        List<ProfileRole> roleList  = new ArrayList<>();
        for (String role : roleArray) {
            roleList.add(ProfileRole.valueOf(role));
        }*/

        List<ProfileRole> roleList2 = Arrays.stream(strRole.split(","))
                .map(ProfileRole::valueOf)
                .toList();

        return new JwtDTO(username, id, roleList2);
    }

    public static Integer decodeRegVerToken(String token){
        Claims claims = Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Integer id = Integer.valueOf(claims.getSubject());
        return id;
    }

    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
