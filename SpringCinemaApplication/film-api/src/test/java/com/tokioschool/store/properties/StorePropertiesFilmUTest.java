package com.tokioschool.store.properties;

import com.tokioschool.store.configs.StoreConfigurationFilm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "application.store.baseUrl=http://example.com",
        "application.store.login.users[0].username=user1",
        "application.store.login.users[0].password=pass1",
        "application.store.login.users[1].username=user2",
        "application.store.login.users[1].password=pass2"
})
@ActiveProfiles("test")
@ContextConfiguration(classes = {StoreConfigurationFilm.class})
//@EnableConfigurationProperties(StoreProperties.class)
class StorePropertiesFilmUTest {

    @Autowired
    private StorePropertiesFilm storePropertiesFilm;

    @Test
    void whenPropertiesLoaded_thenValuesAreCorrect() {
        assertThat(storePropertiesFilm.baseUrl()).isEqualTo("http://example.com");
        assertThat(storePropertiesFilm.login().users()).hasSize(2);
        assertThat(storePropertiesFilm.login().users().get(0).username()).isEqualTo("user1");
        assertThat(storePropertiesFilm.login().users().get(0).password()).isEqualTo("pass1");
        assertThat(storePropertiesFilm.login().users().get(1).username()).isEqualTo("user2");
        assertThat(storePropertiesFilm.login().users().get(1).password()).isEqualTo("pass2");
    }
}