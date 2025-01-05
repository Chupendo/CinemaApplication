package com.tokioschool.filmapp.converter;

import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
class RolesSetToRolesDTOListConverterUTest {

    private final RolesSetToRolesDtoListConverter converter = new RolesSetToRolesDtoListConverter();

    @Test
    void givenSetRoles_wneConvert_tenReturnListRolesDTO() {
        // Arrange

        Set<Role> roleSet = Set.of(
                Role.builder().id(1L).name("USER").authorities( Set.of(Authority.builder().id(1L).name("read").build())).build(),
                Role.builder().id(2L).name("EMPLOYEE").authorities( Set.of(Authority.builder().id(1L).name("read").build())).build(),
                Role.builder().id(3L).name("ADMIN").authorities( Set.of(
                        Authority.builder().id(1L).name("read").build()
                )).build()
        );

        MappingContext<Set<Role>, List<RoleDTO>> context = Mockito.mock(MappingContext.class);
        Mockito.when(context.getSource()).thenReturn(roleSet);

        // Act
        List<RoleDTO> roleDTOS = converter.convert(context);

        // Assertions
        Assertions.assertThat(roleDTOS)
                .hasSize(3)
                .first().returns("read",roleDTO -> roleDTO.getAuthorities().getFirst());

    }
}