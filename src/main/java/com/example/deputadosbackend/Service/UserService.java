package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.DeputadoDetalhesDTO;
import com.example.deputadosbackend.Dto.LoginDTO;
import com.example.deputadosbackend.Dto.UserDTO;
import com.example.deputadosbackend.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.deputadosbackend.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    private final UsuarioRepository repository;
    private final DeputadosService deputadosService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserService(UsuarioRepository repository,
                          DeputadosService deputadosService) {
        this.repository = repository;
        this.deputadosService = deputadosService;
    }

    private final List<User> usuarios = new ArrayList<>();

    public void UserLogin(LoginDTO loginRequest) {
        //return usuarios.stream().anyMatch(u ->
        //    u.getEmail().equalsIgnoreCase(loginRequest.getEmail()) &&
        //            u.getPassword().equalsIgnoreCase(loginRequest.getPassword())
       // );
    }


    public UserDTO saveUser(User user) {
        user.setSenhaHash(passwordEncoder.encode(user.getSenha())); // 🔥 Criptografa!

        User usuarioSalvo = repository.save(user);
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(usuarioSalvo.getEmail());
        userDTO.setNome(usuarioSalvo.getNome());
        userDTO.setSobrenome(usuarioSalvo.getSobrenome());
        return userDTO;

    }
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public boolean addFavoriteDeputado(Long userId, Long deputadoId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean added = user.getFavoriteDeputados().add(deputadoId);

        if (added) {
            repository.save(user);
        }

        return added;
    }


    public void removeFavoriteDeputado(Long userId, Long deputadoId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.getFavoriteDeputados().remove(deputadoId);
        repository.save(user);
    }
    public UserDTO getUserByEmail(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new UserDTO(user.getId(),
                user.getNome(),
                user.getSobrenome(),
                user.getEmail(),
                user.getCidade(),
                user.getCpf(),
                user.getEstado());
    }

    public List< DeputadoDetalhesDTO> getFavoriteDeputados(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Long> ids = new ArrayList<>(user.getFavoriteDeputados());

        return ids.stream()
                .map(deputadosService::buscarDeputadoPorId)
                .toList();

    }
}