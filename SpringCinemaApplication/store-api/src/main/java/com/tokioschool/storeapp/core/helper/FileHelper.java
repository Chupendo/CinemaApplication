package com.tokioschool.storeapp.core.helper;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


public class FileHelper {

    private FileHelper(){}
    /**
     * Method that return the segment path given aver current working directory
     * @param segmentPath framgent of path for amount  aver current working directory
     * @return path with segment gien
     */
    public static Path getCurrentWorking(@Nullable String segmentPath){

        return Optional.ofNullable(segmentPath)
                .map(FileSystems.getDefault()::getPath)
                .orElse(FileSystems.getDefault().getPath(StringUtils.EMPTY));
    }

    /**
     * Method that create the directories if not exits
     * @param path directory to builder
     * @return true if was possible the created, otherwise false
     */
    public static boolean createWorkIfNotExists(Path path) throws IOException {
        if(!Files.exists(path)){
            Files.createDirectories(path);
            return true;
        }
        return false;
    }

    /**
     * Method that delete the directory or file if exits
     * @param path directory to delete
     * @return true if was possible the deleted, otherwise false
     */
    public static boolean deleteWorkIfNotExists(Path path) throws IOException {
            return Files.deleteIfExists(path);
    }
}
