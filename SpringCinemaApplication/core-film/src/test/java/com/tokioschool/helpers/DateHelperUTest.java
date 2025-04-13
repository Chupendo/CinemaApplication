package com.tokioschool.helpers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class DateHelperUTest {

    @Test
    void testParseLocalDateToTimeToOffsetDateTimeUtc_whenValidDateTime_thenReturnOffsetDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 4, 7, 14, 30);
        OffsetDateTime expected = OffsetDateTime.of(localDateTime, ZoneOffset.UTC);

        OffsetDateTime result = DateHelper.parseLocalDateToTimeToOffsetDateTimeUtc(localDateTime);

        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(ZoneOffset.UTC, result.getOffset());
    }

    @Test
    void testParseLocalDateToTimeToOffsetDateTimeUtc_whenNull_thenReturnNull() {
        OffsetDateTime result = DateHelper.parseLocalDateToTimeToOffsetDateTimeUtc(null);

        assertNull(result);
    }
}