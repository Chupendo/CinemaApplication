package com.tokioschool.storeapp.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * Class for model the file in extension json, that is saved at each load to system of a resource.
 *
 * Note:
 * - This information don't save in base data.
 * - The name of this file, is the ID of same, and is a reference of UUID.
 * - The name or recourse is the join of its name and extension.
 * - The resources are hosting in directory defined inside properties of application.
 *
 * @version 1
 * @autor andres.rpenuela
 */
@Value
@Builder
@Jacksonized
public class ResourceDescription {

    UUID id;
    String resourceName;
    String contentType;
    String description;
    int size;
}
