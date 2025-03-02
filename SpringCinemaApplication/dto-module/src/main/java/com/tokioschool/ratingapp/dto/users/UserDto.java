package com.tokioschool.ratingapp.dto.users;

import com.tokioschool.ratingapp.dto.roles.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String username;
    private String email;
    private LocalDateTime lastLogin;
    private LocalDateTime created;
    private List<RoleDto> roles;
    private String resourceId;
}
