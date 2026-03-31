package com.exam.controller;

import com.exam.dto.ApiResponse;
import com.exam.dto.ChangePasswordRequest;
import com.exam.dto.UpdateProfileRequest;
import com.exam.dto.UserDTO;
import com.exam.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile() {
        UserDTO user = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserDTO user = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công", user));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping("/{id}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> lockUser(@PathVariable Integer id) {
        userService.lockUser(id);
        return ResponseEntity.ok(ApiResponse.success("Khóa tài khoản thành công", null));
    }
    
    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> unlockUser(@PathVariable Integer id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(ApiResponse.success("Mở khóa tài khoản thành công", null));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công", null));
    }
}
