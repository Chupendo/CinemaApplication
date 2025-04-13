package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.Scope;
import com.tokioschool.filmapp.dto.user.RoleDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
public class SetRoleToListRoleDtoConverterUTest {

    private SetRoleToListRoleDtoConverter converter = new SetRoleToListRoleDtoConverter();

    @Test
    void convert_withValidAuthoritySet_shouldReturnAuthorityDtoList(){
        Set<Role> roleSet = Set.of(
                Role.builder()
                        .id(1L).
                        name("USER")
                        .authorities(Set.of(
                                Authority.builder().id(1L).name("read").build()
                        ))
                        .scopes(Set.of(
                                Scope.builder().id(1L).name("openid").build(),
                                Scope.builder().id(2L).name("read").build()
                        ))
                        .build(),
                Role.builder()
                        .id(2L).
                        name("ADMIN")
                        .authorities(Set.of(
                                Authority.builder().id(1L).name("read").build(),
                                Authority.builder().id(2L).name("writer").build()
                        ))
                        .scopes(Set.of(
                                Scope.builder().id(1L).name("openid").build(),
                                Scope.builder().id(2L).name("read").build(),
                                Scope.builder().id(3L).name("writer").build()
                        ))
                        .build()
        );

        MappingContext< Set<Role>, List<RoleDto> > context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(roleSet);

        List<RoleDto> result = converter.convert(context);

        Assertions.assertThat(result).hasSize(2)
                .extracting(RoleDto::getName)
                .containsExactlyInAnyOrder("USER", "ADMIN");

        Assertions.assertThat(result.getFirst().getAuthorities()).isNotEmpty()
                        .contains("read");
        Assertions.assertThat(result.getFirst().getScopes()).isNotEmpty()
                .contains("openid", "read");
    }

    @Test
    void convert_withValidAuthoritySetWithOutAuthoritiesAndScopes_shouldReturnAuthorityDtoList(){
        Set<Role> roleSet = Set.of(
                Role.builder()
                        .id(1L).
                        name("USER")
                        .build(),
                Role.builder()
                        .id(2L).
                        name("ADMIN")
                        .authorities(Collections.EMPTY_SET)
                        .scopes(null)
                        .build()
        );

        MappingContext< Set<Role>, List<RoleDto> > context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(roleSet);

        List<RoleDto> result = converter.convert(context);

        Assertions.assertThat(result).hasSize(2)
                .extracting(RoleDto::getName)
                .containsExactlyInAnyOrder("USER", "ADMIN");

        Assertions.assertThat(result.getFirst().getAuthorities()).isEmpty();
        Assertions.assertThat(result.getFirst().getScopes()).isEmpty();
    }
    @Test
    void convert_withValidNullAuthoritySet_shouldReturnListEmpty(){

        MappingContext< Set<Role>, List<RoleDto> > context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(null);

        // Act
        List<RoleDto> result = converter.convert(context);

        // Assertions
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void convert_withNull_shouldReturnListEmpty(){

        // Act
        List<RoleDto> result = converter.convert(null);

        // Assertions
        Assertions.assertThat(result).isEmpty();
    }
}
