package com.tokioschool.storeapp.userdetails.service;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface StoreUserService {
    UserDto findByUserName(String username) throws UsernameNotFoundException;
}
