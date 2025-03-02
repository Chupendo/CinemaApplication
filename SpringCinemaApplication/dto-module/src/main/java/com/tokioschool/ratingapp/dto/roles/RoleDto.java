package com.tokioschool.ratingapp.dto.roles;

import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {

    private Long id;
    private String name;
    private List<AuthorityDto> authorities;
}
