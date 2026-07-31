package com.flutterbackend.admin.service;

import com.flutterbackend.admin.dto.AdminLoginRequest;
import com.flutterbackend.admin.dto.AdminLoginResponse;
import com.flutterbackend.admin.dto.AdminProfileResponse;
import com.flutterbackend.admin.dto.AdminRegisterRequest;
import com.flutterbackend.user.domain.User;

import java.util.List;
import java.util.Map;

public interface AdminService {
    String register(AdminRegisterRequest request);
    AdminLoginResponse login(AdminLoginRequest request);
    AdminProfileResponse getProfile(String email);

    List<User> getAllUsers();
    User getUserById(Long id);
    String deleteUser(Long id);
    List<User> getPendingBrokers();
    Map<String, Object> getBrokerDetails(Long userId);
    String updateBrokerStatus(Long userId, String status);
}
