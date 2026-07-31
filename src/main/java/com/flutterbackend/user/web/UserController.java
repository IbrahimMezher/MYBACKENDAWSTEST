package com.flutterbackend.user.web;

import com.flutterbackend.user.dto.UserLoginRequest;
import com.flutterbackend.user.dto.UserLoginResponse;
import com.flutterbackend.user.dto.UserSignupRequest;
import com.flutterbackend.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserLoginResponse signup(@RequestBody UserSignupRequest body) {
        return userService.signup(body);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginRequest body) {
        return userService.login(body);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        return userService.getUserSummary(id);
    }
}
