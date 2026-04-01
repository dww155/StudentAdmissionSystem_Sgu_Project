package com.sgu.student_admission_system.configuration;

import com.nimbusds.jose.JOSEException;
import com.sgu.student_admission_system.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder = null;

    JwtUtil jwtUtil;

    @NonFinal
    @Value("${jwt.secret}")
    protected String SECRET_KEY;

    @Override
    public Jwt decode(String token) throws JwtException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), MacAlgorithm.HS256.getName());

        try {
            jwtUtil.verifyToken(token, false);
        } catch (ParseException | JOSEException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder))
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

        return nimbusJwtDecoder.decode(token);
    }
}
