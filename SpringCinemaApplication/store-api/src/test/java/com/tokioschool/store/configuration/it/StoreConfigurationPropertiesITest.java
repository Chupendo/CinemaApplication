package com.tokioschool.store.configuration.it;

import com.tokioschool.storeapp.configuration.properties.StoreConfigurationProperties;
import com.tokioschool.storeapp.core.helper.FileHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes={
        StoreConfigurationPropertiesITest.StoreConfigurationTest.class
})
class StoreConfigurationPropertiesITest {

    @Autowired
    StoreConfigurationProperties storeConfigurationProperties;

    @Test
    @Order(1)
    void givenPathRelative_whenGetResourcePathFromRelativePath_returnOk() throws IOException {
        final Path path = storeConfigurationProperties.buildResourcePathFromRelativePathGivenNameResource();

        FileHelper.createWorkIfNotExists(path);

        Assertions.assertThat(path)
                .exists()
                .isDirectory();

        FileHelper.deleteWorkIfNotExists(path);
    }

    @Test
    @Order(2)
    void givenPathRelative_whenGetResourcePathFromAbsolute_returnOk() throws IOException {
        final Path path = storeConfigurationProperties.buildResourcePathFromAbsolutePath();

        FileHelper.createWorkIfNotExists(path);

        Assertions.assertThat(path)
                .exists()
                .isDirectory();

        FileHelper.deleteWorkIfNotExists(path);
    }

    @Test
    @Order(3)
    void givenResourceName_whenGetResourcePathFromRelativePath_returnOk() throws IOException {
        final Path path = storeConfigurationProperties.getResourcePathFromRelativePathGivenNameResource("file/");

        FileHelper.createWorkIfNotExists(path);

        Assertions.assertThat(path)
                .exists()
                .isDirectory();

        FileHelper.deleteWorkIfNotExists(path);
    }

    @Configuration
    @EnableConfigurationProperties(StoreConfigurationProperties.class)
    protected static class StoreConfigurationTest {
    }
}