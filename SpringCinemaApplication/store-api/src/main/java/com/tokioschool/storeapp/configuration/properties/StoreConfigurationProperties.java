package com.tokioschool.storeapp.configuration.properties;

import com.tokioschool.storeapp.core.helper.FileHelper;
import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "application.store")
public record StoreConfigurationProperties(Path absolutePath, String relativePath) {


    /**
     * Return the path of resources from relative path read in the properties
     * @return path of resource
     *
     * @SEE {@link FileHelper#getCurrentWorking(String)}
     */
    public Path buildResourcePathFromRelativePathGivenNameResource(){
        return FileHelper.getCurrentWorking(relativePath);
    }

    /**
     * Return the path of resources from relative path absolute in the properties
     * @return path of resource
     */
    public Path buildResourcePathFromAbsolutePath(){
        return absolutePath;
    }

    /**
     * Return the path of resource given from relative path read in the properties
     * @return path of resource given
     *
     * @param nameResource name of resource for get
     * @SEE {@link FileHelper#getCurrentWorking(String)}
     *
     * Other option:
     *  return Path.of(getResourcePathFromRelativePathGivenNameResource().toString(),nameResource);
     */
    public Path getResourcePathFromRelativePathGivenNameResource(@Nonnull String nameResource){
        return buildResourcePathFromRelativePathGivenNameResource().resolve(nameResource);

    }
}
