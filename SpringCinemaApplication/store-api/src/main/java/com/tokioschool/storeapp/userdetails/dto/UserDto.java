package com.tokioschool.storeapp.userdetails.dto;

import java.util.List;

// clase interna de un usuario defindo en las properties
public record UserDto(String username, String password, List<String> authorities, List<String> roles ) {}