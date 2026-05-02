package com.Address.demo.Controller;

//package com.Address.demo.Controller;

import com.Address.demo.dto.Signingrequest;
import com.Address.demo.exception.ApiException;
import com.Address.demo.model.User;
import com.Address.demo.model.UserActivity;
import com.Address.demo.repositry.UserActivityRepository;
import com.Address.demo.repositry.UserRepositry;
import com.Address.demo.security.JwtHelper;

import org.springframework.web.bind.annotation.*;
import com.Address.demo.dto.Signingrequest;
import com.Address.demo.exception.ApiException;
import com.Address.demo.model.User;
import com.Address.demo.repositry.UserRepositry;
import com.Address.demo.security.JwtHelper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class AuthController {

    private final JwtHelper jwtHelper;
    private final UserRepositry userRepositry;
    private final UserActivityRepository activityRepo;

    public AuthController(JwtHelper jwtHelper,
                          UserRepositry userRepositry,
                          UserActivityRepository activityRepo) {

        this.jwtHelper = jwtHelper;
        this.userRepositry = userRepositry;
        this.activityRepo=activityRepo;
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

        // ==========================
        // LOGIN TRACKING DEFAULT
        // ==========================
        user.setOnline(false);
        user.setLastLogin(null);
        user.setLastLogout(null);

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

        User user = userRepositry.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ApiException("Wrong password");
        }

        String token = jwtHelper.generateToken(
                user.getUsername(),
                user.getRole(),
                user.getTenantId()
        );

        user.setToken(token);

        // ==========================
        // USER TABLE UPDATE
        // ==========================
        user.setOnline(true);
        user.setLastLogin(LocalDateTime.now().toString());
        user.setLastLogout(null);

        userRepositry.save(user);

        // ==========================
        // USER ACTIVITY COLLECTION
        // ==========================
        // USER ACTIVITY COLLECTION

        UserActivity old =
                activityRepo.findTopByUsernameOrderByLoginTimeDesc(
                        user.getUsername()
                );

        if(old == null || old.getLogoutTime() != null){

            UserActivity activity = new UserActivity();
            activity.setUsername(user.getUsername());
            activity.setRole(user.getRole());
            activity.setStatus("Online");
            activity.setLoginTime(LocalDateTime.now().toString());
            activity.setLogoutTime(null);

            activityRepo.save(activity);
        }

        // ==========================
        // RESPONSE
        // ==========================
        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Login Success");
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("tenantId", user.getTenantId());
        response.put("token", token);

        return response;
    }

    // ==========================
    // LOGOUT
    // ==========================
    @PostMapping("/logout")
    public Map<String, Object> logout(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        String username = jwtHelper.getUsernameFromToken(token);

        User user = userRepositry.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found"));

        // ==========================
        // USER TABLE UPDATE
        // ==========================
        user.setOnline(false);
        user.setLastLogout(LocalDateTime.now().toString());
        user.setToken("");

        userRepositry.save(user);

        // ==========================
        // USER ACTIVITY TABLE UPDATE
        // ==========================
        UserActivity activity =
                activityRepo.findTopByUsernameAndStatusOrderByLoginTimeDesc(
                        username,
                        "Online"
                );

        if(activity != null){
            activity.setStatus("Offline");
            activity.setLogoutTime(LocalDateTime.now().toString());
            activityRepo.save(activity);
        }

        // ==========================
        // RESPONSE
        // ==========================
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout Success");

        return response;
    }

    // ==========================
    // ACTIVE USERS FOR RECRUITER
    // ==========================
    @GetMapping("/active-users")
    public Object activeUsers() {
        return activityRepo.findAll();
    }
}


/*
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

 */