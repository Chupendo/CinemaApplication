package com.tokioschool.filmapp.dto.common;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Value
@Jacksonized
public class PageDTO<T> {
    List<T> items;
    int pageSize;
    int pageNumber;
    int totalPages;
}
