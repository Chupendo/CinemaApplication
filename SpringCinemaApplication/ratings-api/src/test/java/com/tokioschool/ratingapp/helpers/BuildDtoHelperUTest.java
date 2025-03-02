package com.tokioschool.ratingapp.helpers;

import com.tokioschool.ratingapp.domains.Authority;
import com.tokioschool.ratingapp.domains.Role;
import com.tokioschool.ratingapp.dto.authorities.AuthorityDto;
import com.tokioschool.ratingapp.dto.roles.RoleDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class BuildDtoHelperUTest {

    @Test
    void buildAuthorityDto_returnsAuthorityDto_whenAuthorityIsNotNull() {
        Authority authority = new Authority();
        authority.setId(1L);
        authority.setName("ROLE_USER");

        AuthorityDto authorityDto = BuildDtoHelper.buildAuthorityDto(authority);

        assertThat(authorityDto).isNotNull();
        assertThat(authorityDto.getId()).isEqualTo(1L);
        assertThat(authorityDto.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void buildAuthorityDto_returnsNull_whenAuthorityIsNull() {
        AuthorityDto authorityDto = BuildDtoHelper.buildAuthorityDto(null);

        assertThat(authorityDto).isNull();
    }

    @Test
    void buildRoleDto_returnsRoleDto_whenRoleIsNotNull() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setId(1L);
        authority.setName("ROLE_USER");
        authorities.add(authority);
        role.setAuthorities(authorities);

        RoleDto roleDto = BuildDtoHelper.buildRoleDto(role);

        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getId()).isEqualTo(1L);
        assertThat(roleDto.getName()).isEqualTo("ROLE_USER");
        assertThat(roleDto.getAuthorities()).hasSize(1);
        assertThat(roleDto.getAuthorities().get(0).getName()).isEqualTo("ROLE_USER");
    }

    @Test
    void buildRoleDto_returnsNull_whenRoleIsNull() {
        RoleDto roleDto = BuildDtoHelper.buildRoleDto(null);

        assertThat(roleDto).isNull();
    }

    @Test
    void buildAuthorityDtoList_returnsEmptyList_whenAuthoritiesIsNull() {
        List<AuthorityDto> authorityDtos = BuildDtoHelper.buildAuthorityDtoList(null);

        assertThat(authorityDtos).isEmpty();
    }

    @Test
    void buildAuthorityDtoList_returnsEmptyList_whenAuthoritiesIsEmpty() {
        List<AuthorityDto> authorityDtos = BuildDtoHelper.buildAuthorityDtoList(Collections.emptySet());

        assertThat(authorityDtos).isEmpty();
    }

    @Test
    void buildAuthorityDtoList_returnsListOfAuthorityDtos_whenAuthoritiesIsNotEmpty() {
        Set<Authority> authorities = new HashSet<>();
        Authority authority1 = new Authority();
        authority1.setId(1L);
        authority1.setName("ROLE_USER");
        Authority authority2 = new Authority();
        authority2.setId(2L);
        authority2.setName("ROLE_ADMIN");
        authorities.add(authority1);
        authorities.add(authority2);

        List<AuthorityDto> authorityDtos = BuildDtoHelper.buildAuthorityDtoList(authorities);

        assertThat(authorityDtos).hasSize(2);
        assertThat(authorityDtos).extracting(AuthorityDto::getName).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }
}