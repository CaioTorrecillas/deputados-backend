package com.example.deputadosbackend.Controller;

import java.util.List;

import com.example.deputadosbackend.Dto.UserDTO;
import com.example.deputadosbackend.Service.UserService;
import com.example.deputadosbackend.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

}
