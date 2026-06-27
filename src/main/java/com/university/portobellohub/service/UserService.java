package com.university.portobellohub.service;

import com.university.portobellohub.dto.request.RoleAssignmentRequest;
import com.university.portobellohub.dto.request.UserUpdateRequest;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.dto.response.UserResponse;
import com.university.portobellohub.entity.Role;
import com.university.portobellohub.entity.User;
import com.university.portobellohub.entity.enums.RoleName;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.exception.ResourceNotFoundException;
import com.university.portobellohub.repository.RoleRepository;
import com.university.portobellohub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CloudinaryService cloudinaryService;
    private final SecurityUtils securityUtils;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            CloudinaryService cloudinaryService,
            SecurityUtils securityUtils
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cloudinaryService = cloudinaryService;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        return UserResponse.fromEntity(securityUtils.getCurrentUser(), true);
    }

    @Transactional(readOnly = true)
    public UserResponse getPublicProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.fromEntity(user, false);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserResponse> page = userRepository.findByEnabledTrue(pageable)
                .map(user -> UserResponse.fromEntity(user, true));
        return PageResponse.from(page);
    }

    @Transactional
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        User user = securityUtils.getCurrentUser();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
        }

        return UserResponse.fromEntity(userRepository.save(user), true);
    }

    @Transactional
    public UserResponse uploadAvatar(MultipartFile file) {
        User user = securityUtils.getCurrentUser();
        cloudinaryService.deleteImage(user.getProfileImagePublicId());

        CloudinaryService.UploadResult uploadResult = cloudinaryService.uploadImage(file, "portobello-hub/avatars");
        user.setProfileImageUrl(uploadResult.url());
        user.setProfileImagePublicId(uploadResult.publicId());

        return UserResponse.fromEntity(userRepository.save(user), true);
    }

    @Transactional
    public UserResponse assignRoles(Long userId, RoleAssignmentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
            roles.add(role);
        }

        if (!roles.stream().anyMatch(role -> role.getName() == RoleName.ROLE_CUSTOMER)) {
            roles.add(roleRepository.findByName(RoleName.ROLE_CUSTOMER).orElseThrow());
        }

        user.setRoles(roles);
        return UserResponse.fromEntity(userRepository.save(user), true);
    }
}
