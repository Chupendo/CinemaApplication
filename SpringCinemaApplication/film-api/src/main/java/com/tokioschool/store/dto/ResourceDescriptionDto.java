package com.tokioschool.store.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

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
public class ResourceDescriptionDto {

    private String description;
}
