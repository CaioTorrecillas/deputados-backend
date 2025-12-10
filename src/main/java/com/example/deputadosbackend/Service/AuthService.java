package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Jwt.JWT;
import com.example.deputadosbackend.Model.RefreshToken;
import com.example.deputadosbackend.Repository.*;
import com.example.deputadosbackend.Repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final JWT jwtUtil;
    private final RefreshTokenRepository refreshRepo;
    private final UsuarioRepository userRepo; // seu repositório de usuário
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(JWT jwtUtil, RefreshTokenRepository refreshRepo, UsuarioRepository userRepo) {
        this.jwtUtil = jwtUtil;
        this.refreshRepo = refreshRepo;
        this.userRepo = userRepo;
    }

    // Login: valida usuário (pelo banco), gera tokens e salva refresh token
    @Transactional
    public AuthResponse login(String email, String senha) {

        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // valida a senha
        if (!encoder.matches(senha, user.getSenhaHash())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String accessToken = jwtUtil.createAccessToken(email);
        var refresh = jwtUtil.createRefreshToken(email);

        RefreshToken entity = new RefreshToken();
        entity.setJti(refresh.jti);
        entity.setUserEmail(email);
        entity.setExpiryDate(refresh.expiration.toInstant());
        entity.setRevoked(false);

        refreshRepo.save(entity);

        return new AuthResponse(accessToken, refresh.token);
    }

    // Refresh: verifica token, checa jti no DB e emite novo access + (opcional) novo refresh
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        var jws = jwtUtil.parseToken(refreshToken);
        String jti = jws.getBody().getId();
        String subject = jws.getBody().getSubject();

        Optional<RefreshToken> opt = refreshRepo.findByJtiAndRevokedFalse(jti);
        if (opt.isEmpty()) throw new RuntimeException("Refresh token inválido ou revogado");

        // opcional: checar expiry (o parse já checa se expirou)
        // emitir novo access token
        String newAccess = jwtUtil.createAccessToken(subject);

        // opcional: renovar refresh token (rotate) — aqui mantemos o mesmo refresh ou geramos novo jti
        // se rotacionar: marcar o jti atual revogado e criar novo refresh token salvo no DB

        return new AuthResponse(newAccess, refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        var jws = jwtUtil.parseToken(refreshToken);
        String jti = jws.getBody().getId();
        refreshRepo.findById(jti).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshRepo.save(rt);
        });
    }

    public record AuthResponse(String accessToken, String refreshToken) { }
}

