package com.tokioschool.core.helper.ut;

import com.tokioschool.storeapp.core.helper.FileHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class FileHelperUTest {

    @Test
    @Order(1)
    void givenEmpty_getCurrentWorking_thenPathCurrentWorking() {
        final Path pathCurrent = FileHelper.getCurrentWorking(StringUtils.EMPTY);

        assertThat(pathCurrent).isNotNull()
                .isEqualTo(FileSystems.getDefault().getPath(StringUtils.EMPTY))
                .isDirectory()
                .exists();
    }

    @Test
    @Order(2)
    void givenRelativePath_whenCreateWorkIfNotExists_thenSuccess() throws IOException {
        final Path pathCurrent = FileHelper.getCurrentWorking("..\\store-test");

        final boolean result =  FileHelper.createWorkIfNotExists(pathCurrent);
        assertThat(result).isTrue();
        assertThat(pathCurrent).isNotNull()
                .isEqualTo(FileSystems.getDefault().getPath("..\\store-test"))
                .isDirectory()
                .exists();
    }

    @Test
    @Order(3)
    void givenRelativePathKnow_getDeleteWorkIfNotExists_thenSuccess() throws IOException {
        final Path pathCurrent = FileHelper.getCurrentWorking("..\\store-test");

        final boolean result = FileHelper.deleteWorkIfNotExists(pathCurrent);

        assertThat(result).isTrue();

        assertThat(pathCurrent).isNotNull()
                .isEqualTo(FileSystems.getDefault().getPath("..\\store-test"))
                .returns(true,path -> !path.toFile().exists());
    }
}