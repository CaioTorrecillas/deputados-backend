package com.example.deputadosbackend.Controller;

import com.example.deputadosbackend.Dto.LoginDTO;
import com.example.deputadosbackend.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDto) {
        try {
            var tokens = authService.login(loginDto.getEmail(), loginDto.getPassword());
            return ResponseEntity.ok(Map.of(
                    "accessToken", tokens.accessToken(),
                    "refreshToken", tokens.refreshToken()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body) {
        try {
            String refresh = body.get("refreshToken");
            var tokens = authService.refresh(refresh);
            return ResponseEntity.ok(Map.of("accessToken", tokens.accessToken(), "refreshToken", tokens.refreshToken()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }


}

