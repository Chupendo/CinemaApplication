package com.tokioschool.helpers;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.Scope;
import com.tokioschool.filmapp.dto.user.RoleDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class BuildDtoHelperUTest {
    @Test
    void testBuildAuthorityDto_withValidAuthority() {
        Authority authority = new Authority(1L,"ADMIN");
        String result = BuildDtoHelper.buildAuthorityDto(authority);
        assertEquals("ADMIN", result);
    }

    @Test
    void testBuildAuthorityDto_withNullAuthority() {
        String result = BuildDtoHelper.buildAuthorityDto(null);
        assertNull(result);
    }

    @Test
    void testBuildScopeDto_withValidScope() {
        Scope scope = new Scope(1L,"READ");
        String result = BuildDtoHelper.buildScopeDto(scope);
        assertEquals("READ", result);
    }

    @Test
    void testBuildScopeDto_withNullScope() {
        String result = BuildDtoHelper.buildScopeDto(null);
        assertNull(result);
    }

    @Test
    void testBuildAuthorityDtoList_withAuthorities() {
        Set<Authority> authorities = Set.of(
                new Authority(1L,"USER"),
                new Authority(2L,"ADMIN")
        );
        List<String> result = BuildDtoHelper.buildAuthorityDtoList(authorities);

        assertEquals(2, result.size());
        assertTrue(result.contains("USER"));
        assertTrue(result.contains("ADMIN"));
    }

    @Test
    void testBuildAuthorityDtoList_withNullSet() {
        List<String> result = BuildDtoHelper.buildAuthorityDtoList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testBuildRoleDto_withValidRole() {
        Set<Authority> authorities = Set.of(new Authority(1L,"ADMIN"));
        Set<Scope> scopes = Set.of(new Scope(1L,"WRITE"));

        Role role = new Role(1L,"SUPERUSER", authorities, scopes);

        RoleDto dto = BuildDtoHelper.buildRoleDto(role);

        assertNotNull(dto);
        assertEquals("SUPERUSER", dto.getName());
        assertEquals(List.of("ADMIN"), dto.getAuthorities());
        assertEquals(List.of("WRITE"), dto.getScopes());
    }

    @Test
    void testBuildRoleDto_withNullRole() {
        RoleDto dto = BuildDtoHelper.buildRoleDto(null);
        assertNull(dto);
    }
}
