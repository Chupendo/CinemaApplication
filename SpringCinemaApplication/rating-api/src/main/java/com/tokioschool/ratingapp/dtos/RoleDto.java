package com.tokioschool.ratingapp.dtos;

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

    private String name;
    private List<String> authorities;
    private List<String> scopes;
}
