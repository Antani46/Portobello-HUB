package com.university.portobellohub.controller;

import com.university.portobellohub.dto.request.RoleAssignmentRequest;
import com.university.portobellohub.dto.request.UserUpdateRequest;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.dto.response.UserResponse;
import com.university.portobellohub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userService.getCurrentUserProfile();
    }

    @PutMapping("/me")
    public UserResponse updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        return userService.updateCurrentUser(request);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse uploadAvatar(@RequestPart("file") MultipartFile file) {
        return userService.uploadAvatar(file);
    }

    @GetMapping("/{id}")
    public UserResponse getPublicProfile(@PathVariable Long id) {
        return userService.getPublicProfile(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse assignRoles(@PathVariable Long id, @Valid @RequestBody RoleAssignmentRequest request) {
        return userService.assignRoles(id, request);
    }
}
