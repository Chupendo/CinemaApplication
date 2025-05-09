package com.tokioschool.filmapp.dto.error;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ErrorDto {
    private String url;
    private String exception;
}
