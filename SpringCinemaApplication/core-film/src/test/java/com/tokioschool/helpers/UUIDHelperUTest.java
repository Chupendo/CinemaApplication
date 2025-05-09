package com.tokioschool.helpers;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UUIDHelperUTest {

    @Test
    void mapStringToUUID_withValidUUIDString_returnsUUID() {
        String validUUIDString = "123e4567-e89b-12d3-a456-426614174000";
        Optional<UUID> result = UUIDHelper.mapStringToUUID(validUUIDString);
        assertTrue(result.isPresent());
        assertEquals(UUID.fromString(validUUIDString), result.get());
    }

    @Test
    void mapStringToUUID_withInvalidUUIDString_returnsEmptyOptional() {
        String invalidUUIDString = "invalid-uuid-string";
        Optional<UUID> result = UUIDHelper.mapStringToUUID(invalidUUIDString);
        assertFalse(result.isPresent());
    }

    @Test
    void mapStringToUUID_withNullString_returnsEmptyOptional() {
        Optional<UUID> result = UUIDHelper.mapStringToUUID(null);
        assertFalse(result.isPresent());
    }

    @Test
    void mapStringToUUID_withEmptyString_returnsEmptyOptional() {
        Optional<UUID> result = UUIDHelper.mapStringToUUID("");
        assertFalse(result.isPresent());
    }

    @Test
    void mapStringToUUID_withWhitespaceString_returnsEmptyOptional() {
        Optional<UUID> result = UUIDHelper.mapStringToUUID("   ");
        assertFalse(result.isPresent());
    }

    @Test
    void mapStringToUUID_withStringContainingLeadingAndTrailingSpaces_returnsUUID() {
        String validUUIDString = "  123e4567-e89b-12d3-a456-426614174000  ";
        Optional<UUID> result = UUIDHelper.mapStringToUUID(validUUIDString);
        assertTrue(result.isPresent());
        assertEquals(UUID.fromString(validUUIDString.trim()), result.get());
    }
}