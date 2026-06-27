package com.university.portobellohub.dto.request;

import com.university.portobellohub.entity.enums.RoleName;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class RoleAssignmentRequest {

    @NotEmpty
    private Set<RoleName> roles;

    public Set<RoleName> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleName> roles) {
        this.roles = roles;
    }
}
