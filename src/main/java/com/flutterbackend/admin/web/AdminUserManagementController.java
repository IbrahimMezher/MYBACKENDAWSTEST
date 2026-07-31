package com.flutterbackend.admin.web;

import com.flutterbackend.admin.service.AdminService;
import com.flutterbackend.user.domain.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserManagementController {

    private final AdminService adminService;

    public AdminUserManagementController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public User getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('superadmin')")
    public String deleteUser(@PathVariable Long id) {
        return adminService.deleteUser(id);
    }

    @GetMapping("/brokers/pending")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public List<User> getPendingBrokers() {
        return adminService.getPendingBrokers();
    }

    @GetMapping("/brokers/{userId}/details")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public Map<String, Object> getBrokerDetails(@PathVariable Long userId) {
        return adminService.getBrokerDetails(userId);
    }

    @PutMapping("/brokers/{userId}/status")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String updateBrokerStatus(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        return adminService.updateBrokerStatus(userId, body.get("status"));
    }
}
