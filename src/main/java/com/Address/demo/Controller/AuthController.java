package com.Address.demo.Controller;

import com.Address.demo.dto.Signingrequest;
//import com.Address.demo.dto.Signuprequest;
import com.Address.demo.exception.ApiException;
import com.Address.demo.model.User;
import com.Address.demo.repositry.UserRepositry;
import com.Address.demo.security.JwtHelper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class AuthController {

    private final JwtHelper jwtHelper;
    private final UserRepositry userRepositry;

    public AuthController(JwtHelper jwtHelper, UserRepositry userRepositry) {
        this.jwtHelper = jwtHelper;
        this.userRepositry = userRepositry;
    }

    // ================= SIGNUP =================
    @PostMapping(value = "/signing", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> signup(@RequestBody Signingrequest request) {

        if (userRepositry.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException("User already exists ");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // later BCrypt lagana
System.out.println("sfhsdlkfjsokdjflksdjflkjsdlk");
        userRepositry.save(user);

        return Map.of(
                "success", true,
                "message", "User registered successfully"
        );
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Signingrequest request) {

        User user = userRepositry.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ApiException("Incorrect password");
        }

        String token = jwtHelper.generateToken(request.getUsername());

        return Map.of(
                "success", true,
                "token", token
        );
    }
}
