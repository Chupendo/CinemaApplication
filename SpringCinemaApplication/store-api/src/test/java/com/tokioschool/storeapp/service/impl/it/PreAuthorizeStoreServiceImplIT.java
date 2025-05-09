package com.tokioschool.storeapp.service.impl.it;

import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.service.StoreService;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class PreAuthorizeStoreServiceImplIT {

    @Autowired
    private StoreService storeService;

    // CONSTANTS
    public static final String EXT_TXT = ".txt";
    private static final String FILE_NAME = "file%s".formatted(EXT_TXT);
    private static final String CONTENT = "HOLA";
    private static final String CONTENT_TYPE = MediaType.TEXT_PLAIN_VALUE;

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    public void givenUserAdminLogin_whenSaveResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);

        Assertions.assertThat(optionalResourceIdDto)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @WithMockUser(username = "anyUser", roles = {"USER"})
    public void givenUserLogin_whenSaveResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);

        Assertions.assertThat(optionalResourceIdDto)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @WithAnonymousUser
    public void givenAnonymousUser_whenSaveResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Assertions.assertThatThrownBy(() ->storeService.saveResource(multipartFile,null))
                .isInstanceOf(AuthorizationDeniedException.class);

    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    public void givenUserAdminLogin_whenFindResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);
        Optional<ResourceContentDto> resourceContentDto = storeService.findResource(optionalResourceIdDto.get().resourceId());

        Assertions.assertThat(resourceContentDto)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @WithMockUser(username = "anyUser")
    public void givenUserLogin_whenFindResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);
        Optional<ResourceContentDto> resourceContentDto = storeService.findResource(optionalResourceIdDto.get().resourceId());

        Assertions.assertThat(resourceContentDto)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @WithAnonymousUser
    public void givenAnonymousUser_whenFindResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Assertions.assertThatThrownBy(() ->storeService.saveResource(multipartFile,null))
                .isInstanceOf(AuthorizationDeniedException.class);

    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    public void givenUserAdminLogin_whenDeleteResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);
        storeService.deleteResource(optionalResourceIdDto.get().resourceId());

        // verify
        final Optional<ResourceContentDto> optionalResourceContentDto = storeService.findResource(optionalResourceIdDto.get().resourceId());

        Assertions.assertThat(optionalResourceContentDto)
                .isEmpty();
    }

    @Test
    @WithMockUser(username = "anyUser")
    public void givenUserUserLogin_whenDeleteResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);
        storeService.deleteResource(optionalResourceIdDto.get().resourceId());

        // verify
        final Optional<ResourceContentDto> optionalResourceContentDto = storeService.findResource(optionalResourceIdDto.get().resourceId());

        Assertions.assertThat(optionalResourceContentDto)
                .isEmpty();
    }

    @Test
    @WithAnonymousUser
    public void givenAnonymousUser_whenDeleteResource_returnOk() throws Exception {
        MockMultipartFile multipartFile = getMockMultipartFile();

        Assertions.assertThatThrownBy(() ->storeService.deleteResource(UUID.randomUUID()))
                .isInstanceOf(AuthorizationDeniedException.class);

    }
    private static MockMultipartFile getMockMultipartFile() {

        return new MockMultipartFile(
                FILE_NAME.replace(EXT_TXT, StringUtils.EMPTY),
                FILE_NAME,
                CONTENT_TYPE,
                CONTENT.getBytes());

    }
}
