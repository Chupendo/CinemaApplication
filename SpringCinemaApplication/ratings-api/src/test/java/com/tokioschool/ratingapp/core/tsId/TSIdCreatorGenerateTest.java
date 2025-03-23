package com.tokioschool.ratingapp.core.tsId;

import org.assertj.core.api.Assertions;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TSIdCreatorGenerateTest {

    // Mock de la sesión de Hibernate
    @MockitoBean
    SharedSessionContractImplementor session;

    @InjectMocks
    TSIdCreatorGenerate tsIdCreatorGenerate;

    @Test
    void whenTsIdGenerate_thenReturnId(){

        // Generar un ID
        Object id = tsIdCreatorGenerate.generate(session, new Object());

        // Verificar que el ID generado no es nulo
        Assertions.assertThat(id)
                .isNotNull()
                .isInstanceOf(String.class);
    }

}