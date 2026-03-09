package com.example.deputadosbackend.Controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.example.deputadosbackend.Dto.DeputadoDetalhesDTO;
import com.example.deputadosbackend.Dto.UserDTO;
import com.example.deputadosbackend.Jwt.JWT;
import com.example.deputadosbackend.Service.UserService;
import com.example.deputadosbackend.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JWT jwt;
    public UserController(UserService userService, JWT jwt) {
        this.userService = userService;
        this.jwt = jwt;
    }

    @GetMapping
    public List<User> listarUsuarios() {
        System.out.println("Listando usuarios");
        return userService.getAllUsers();
    }
            @PostMapping("/salvar")
    public ResponseEntity<UserDTO> saveUser(@RequestBody User usuarioRequest) {
        //System.out.println("Listando usuarios");
        UserDTO userDTO= userService.saveUser(usuarioRequest);




        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
    @PostMapping("/{userId}/favorites/{deputadoId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long userId,
            @PathVariable Long deputadoId
    ) {
        boolean added = userService.addFavoriteDeputado(userId, deputadoId);

        if (!added) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409
                    .body(Map.of("message", "Deputado já está nos favoritos"));
        }

        return ResponseEntity.ok(
                Map.of("message", "Deputado favoritado com sucesso")
        );
    }

    @DeleteMapping("/{userId}/favorites/{deputadoId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long deputadoId
    ) {
        userService.removeFavoriteDeputado(userId, deputadoId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token inválido");
            }

            String token = authHeader.substring(7);
            String email = jwt.extractEmail(token);

            UserDTO userDTO = userService.getUserByEmail(email);

            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Erro: " + e.getMessage());
        }
    }
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<DeputadoDetalhesDTO>> getFavoriteDeputados(
            @PathVariable Long userId
    ) {
        List<DeputadoDetalhesDTO> deputados = userService.getFavoriteDeputados(userId);
        return ResponseEntity.ok(deputados);
    }
}
