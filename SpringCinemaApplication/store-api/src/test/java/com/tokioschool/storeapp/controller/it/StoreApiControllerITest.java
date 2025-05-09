package com.tokioschool.storeapp.controller.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.storeapp.controller.StoreApiController;
import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import com.tokioschool.storeapp.security.filter.StoreApiSecurityConfiguration;
import com.tokioschool.storeapp.security.jwt.configuration.JwtConfiguration;
import com.tokioschool.storeapp.service.StoreService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@WebMvcTest(controllers = StoreApiController.class) // obtiente solo el contexto del contraldor especificado
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({StoreApiSecurityConfiguration.class, JwtConfiguration.class}) // Importa la configuración de seguridad
class StoreApiControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    // Filter Security
    @MockitoBean
    private RedisJwtBlackListService jwtBlackListService;


    @Test
    @WithMockUser(username = "anyUser")
    void givenResourceId_whenGetResourceEndpoint_returnOk() throws Exception {

        final UUID resourceId = UUID.randomUUID();
        final String resource = "hola";
        final ResourceContentDto resourceContentDto = ResourceContentDto.builder()
                .resourceId(resourceId)
                .content(resource.getBytes())
                .size(resource.length())
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .resourceName("ExampleFIle")
                .build();

        Mockito.when(storeService.findResource(resourceId))
                .thenReturn(Optional.of(resourceContentDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/store/api/resource/%s".formatted(resourceId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithAnonymousUser
    void givenAnonymousUser_whenGetResourceEndpoint_returnUnauthorized() throws Exception {
        final UUID resourceId = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/store/api/resource/%s".formatted(resourceId)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "anyUser")
    void givenResource_whenGetUpload_returnOk() throws Exception {

        final ResourceIdDto resourceIdDto = ResourceIdDto.builder().resourceId(UUID.randomUUID()).build();
        final String resource = "hola";

        // Arrange: Crear el archivo simulado
        final MockMultipartFile multipartFile = new MockMultipartFile(
                "content",               // Debes usar "content" (o el nombre que el controlador espera). @RequestPart("content") MultipartFile multipartFile
                "test-file.txt",               // Nombre del archivo
                MediaType.TEXT_PLAIN_VALUE,    // Tipo de contenido
                "Hello, world!".getBytes()     // Contenido del archivo
        );

        // Mockear el servicio para devolver una respuesta simulada
        Mockito.when(storeService.saveResource(Mockito.any(MultipartFile.class), Mockito.isNull()))
                .thenReturn(Optional.of(resourceIdDto));

        // Act & Assert: Simulación de la solicitud de carga
        mockMvc.perform(MockMvcRequestBuilders.multipart("/store/api/resource/upload")
                        .file(multipartFile)  // Asegúrate de usar el nombre "content"
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.resourceId").exists());

        // Verificar que el metodo del servicio se llamó exactamente una vez
        Mockito.verify(storeService, Mockito.times(1))
                .saveResource(Mockito.any(MultipartFile.class), Mockito.isNull());
    }

    @Test
    @WithAnonymousUser
    void givenResource_whenGetUpload_returnUnauthorized() throws Exception {

        final ResourceIdDto resourceIdDto = ResourceIdDto.builder().resourceId(UUID.randomUUID()).build();
        final String resource = "hola";

        // Arrange: Crear el archivo simulado
        final MockMultipartFile multipartFile = new MockMultipartFile(
                "content",               // Debes usar "content" (o el nombre que el controlador espera). @RequestPart("content") MultipartFile multipartFile
                "test-file.txt",               // Nombre del archivo
                MediaType.TEXT_PLAIN_VALUE,    // Tipo de contenido
                "Hello, world!".getBytes()     // Contenido del archivo
        );

        // Act & Assert: Simulación de la solicitud de carga
        mockMvc.perform(MockMvcRequestBuilders.multipart("/store/api/resource/upload")
                        .file(multipartFile)  // Asegúrate de usar el nombre "content"
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "anyUser")
    void givenResource_whenGetDelete_returnOk() throws Exception {

        final ResourceIdDto resourceIdDto = ResourceIdDto.builder().resourceId(UUID.randomUUID()).build();
        final String resource = "hola";

        // Arrange: Crear el archivo simulado
        final MockMultipartFile multipartFile = new MockMultipartFile(
                "content",               // Debes usar "content" (o el nombre que el controlador espera). @RequestPart("content") MultipartFile multipartFile
                "test-file.txt",               // Nombre del archivo
                MediaType.TEXT_PLAIN_VALUE,    // Tipo de contenido
                "Hello, world!".getBytes()     // Contenido del archivo
        );

        // Mockear el servicio para devolver una respuesta simulada
        Mockito.doNothing().when(storeService).deleteResource(resourceIdDto.resourceId());

        // Act & Assert: Simulación de la solicitud de carga
        mockMvc.perform(MockMvcRequestBuilders.delete("/store/api/resource/{resourceId}",resourceIdDto.resourceId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verificar que el metodo del servicio se llamó exactamente una vez
        Mockito.verify(storeService, Mockito.times(1))
                .deleteResource(resourceIdDto.resourceId());
    }

    @Test
    @WithAnonymousUser
    void givenResource_whenGetDelete_returnUnauthorized() throws Exception {

        final ResourceIdDto resourceIdDto = ResourceIdDto.builder().resourceId(UUID.randomUUID()).build();

        // Act & Assert: Simulación de la solicitud de carga
        mockMvc.perform(MockMvcRequestBuilders.delete("/store/api/resource/{resourceId}",resourceIdDto.resourceId()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}