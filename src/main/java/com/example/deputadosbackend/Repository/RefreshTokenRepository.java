package com.example.deputadosbackend.Repository;
import com.example.deputadosbackend.Model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByJtiAndRevokedFalse(String jti);
    void deleteByUserEmail(String email);
}
