package com.tokioschool.storeapp.userdetails.service.impl.ut;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import com.tokioschool.storeapp.userdetails.service.impl.StoreUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StoreUserServiceImplUTest {

    @Mock
    private StoreUserConfigurationProperty storeUserConfigurationProperty;

    @InjectMocks
    private StoreUserServiceImpl storeUserService;

    @Test
    void givenUserName_whenFindByUserName_thenReturnOk() {
        // Arrange
        String username = "testUser";
        String encodedPassword = "encodedPassword";
        UserDto userDto = new UserDto(username, encodedPassword, List.of("read"), List.of("user"));

        Mockito.when(storeUserConfigurationProperty.users()).thenReturn(List.of(userDto));

        UserDto userDtoResponse = storeUserService.findByUserName("testUser");

        assertThat(userDtoResponse).isNotNull()
                .returns(username,UserDto::username )
                .returns(encodedPassword,UserDto::password )
                .returns( List.of("read"),UserDto::authorities )
                .returns( List.of("user"),UserDto::roles );

    }

    @Test
    void givenUserName_whenFindByUserName_thenReturnUsernameNotFoundException() {
        // Arrange
        String username = "testUser";

        Mockito.when(storeUserConfigurationProperty.users()).thenReturn(null);

        assertThatThrownBy(() -> storeUserService.findByUserName(username))
                .isInstanceOf(UsernameNotFoundException.class);

    }
}