package com.Address.demo.Controller;

import com.Address.demo.dto.Signingrequest;
import com.Address.demo.exception.ApiException;
import com.Address.demo.model.User;
import com.Address.demo.repositry.UserRepositry;
import com.Address.demo.security.JwtHelper;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class AuthController {

    private final JwtHelper jwtHelper;
    private final UserRepositry userRepositry;

    public AuthController(JwtHelper jwtHelper,
                          UserRepositry userRepositry) {

        this.jwtHelper = jwtHelper;
        this.userRepositry = userRepositry;
    }

    // ==========================
    // SIGNUP
    // ==========================
    @PostMapping("/signing")
    public Map<String, Object> signup(@RequestBody Signingrequest request) {

        if (userRepositry.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        String tenantId = "TEN" + UUID.randomUUID()
                .toString()
                .substring(0, 5);

        user.setTenantId(tenantId);

        String token = jwtHelper.generateToken(
                user.getUsername(),
                user.getRole(),
                tenantId
        );

        user.setToken(token);
        userRepositry.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Signup Success");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("tenantId", tenantId);
        response.put("token", token);

        return response;
    }

    // ==========================
    // LOGIN
    // ==========================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Signingrequest request) {

        System.out.println("LOGIN USERNAME = " + request.getUsername());
        System.out.println("LOGIN PASSWORD = " + request.getPassword());

        User user = userRepositry.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));

        System.out.println("DB USER = " + user.getUsername());
        System.out.println("DB PASS = " + user.getPassword());

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ApiException("Wrong password");
        }

        String token = jwtHelper.generateToken(
                user.getUsername(),
                user.getRole(),
                user.getTenantId()
        );

        user.setToken(token);
        userRepositry.save(user);

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Login Success");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("tenantId", user.getTenantId());
        response.put("token", token);

        return response;
    }
}