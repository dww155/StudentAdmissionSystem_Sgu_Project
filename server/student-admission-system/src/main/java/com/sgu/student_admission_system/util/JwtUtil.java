package com.sgu.student_admission_system.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sgu.student_admission_system.entity.User;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtUtil {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret}")
    protected String SECRET_KEY;

    @NonFinal
    @Value("${jwt.expiration}")
    protected long EXPIRATION_TIME;

    @NonFinal
    @Value("${jwt.refreshExpiration}")
    protected long REFRESHABLE_TIME;

    public String tokenGenerator(User user) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .claim("scope", scopeBuilder(user))
                .issuer("SGU")
                .issueTime(new Date())
                .expirationTime(new Date(
                                Instant.now()
                                        .plus(EXPIRATION_TIME, ChronoUnit.SECONDS)
                                        .toEpochMilli()
                        )
                )
                .build();
        Payload payload = new Payload(claimSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            JwtUtil.log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String scopeBuilder(User user) {
        StringJoiner scope = new StringJoiner(" ");

        user.getRoles().forEach(role -> {
            scope.add("ROLE_" + role.getName());
            if (Objects.nonNull(role.getPermissions()))
                role.getPermissions().forEach(permission -> {
                    scope.add("PERMISSION_" + permission.getName());
                });
        });

        return scope.toString();
    }

    public SignedJWT verifyToken(String token, boolean isRefresh)
            throws ParseException, JOSEException {

        SignedJWT jwt = SignedJWT.parse(token);

        if (!jwt.verify(new MACVerifier(SECRET_KEY.getBytes()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Instant now = Instant.now();
        Date expiry;
        if (isRefresh)
            expiry = new Date (jwt.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_TIME, ChronoUnit.SECONDS).toEpochMilli());
        else
            expiry = jwt.getJWTClaimsSet().getExpirationTime();

        if (expiry == null || now.isAfter(expiry.toInstant())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String jti = jwt.getJWTClaimsSet().getJWTID();

        if (jti != null && invalidatedTokenRepository.existsById(jti)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return jwt;
    }
}
