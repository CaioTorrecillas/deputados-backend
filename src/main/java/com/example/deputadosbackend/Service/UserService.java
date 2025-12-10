package com.example.deputadosbackend.Service;

import com.example.deputadosbackend.Dto.LoginDTO;
import com.example.deputadosbackend.Dto.UserDTO;
import com.example.deputadosbackend.Model.User;

import java.util.ArrayList;
import java.util.List;

import com.example.deputadosbackend.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    @Autowired
    private UsuarioRepository repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


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
}