package com.tokioschool.helpers;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Collections methods to work with UUID
 *
 * @author  andres.rpeneula
 * @version 1.0
 */
public class UUIDHelper {

    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private UUIDHelper() {}

    /**
     * Method that convert to String to UUID if the string is a valid UUID, otherwise return an empty Optional
     *
     * @param uuid string to convert to UUID
     * @return optional with UUID if the string is a valid UUID, otherwise return an empty Optional
     */
    public static Optional<UUID> mapStringToUUID(String uuid) {

        final Predicate<String> isStrValidUUID = str -> UUID_REGEX.matcher(str).matches();
        return Optional.ofNullable(uuid)
                .map(StringUtils::trimToNull)
                .filter(isStrValidUUID)
                .map(UUID::fromString);
    }

}
