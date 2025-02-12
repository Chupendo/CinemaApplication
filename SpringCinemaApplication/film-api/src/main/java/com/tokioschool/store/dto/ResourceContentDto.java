package com.tokioschool.store.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.util.Arrays;
import java.util.UUID;

@Builder
@Jacksonized
public record ResourceContentDto(UUID resourceId,byte[] content,String resourceName,String contentType,String description,int size) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceContentDto that = (ResourceContentDto) o;

        if (size != that.size) return false;
        if (!resourceId.equals(that.resourceId)) return false;
        if (!resourceName.equals(that.resourceName)) return false;
        return contentType.equals(that.contentType);
    }

    @Override
    public int hashCode() {
        int result = resourceId.hashCode();
        result = 31 * result + resourceName.hashCode();
        result = 31 * result + contentType.hashCode();
        result = 31 * result + size;
        return result;
    }

    @Override
    public String toString() {
        return "ResourceContentDto{" +
                "resourceId=" + resourceId +
                ", content=" + Arrays.toString(content) +
                ", resourceName='" + resourceName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", description='" + description + '\'' +
                ", size=" + size +
                '}';
    }
}
