package com.tokioschool.storeapp.userdetails.service.impl;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import com.tokioschool.storeapp.userdetails.service.StoreUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreUserServiceImpl implements StoreUserService {

    private final StoreUserConfigurationProperty storeUserConfigurationProperty;

    /**
     * Find user load in memory given your username
     *
     * @param username param for search user in the system
     * @return user of system
     * @throws UsernameNotFoundException if don't found the user with it's username
     */
    public UserDto findByUserName(String username) throws UsernameNotFoundException{
        return Optional.ofNullable(storeUserConfigurationProperty.users())
                .orElseThrow(() -> new UsernameNotFoundException("User list is empty"))
                .stream()
                .filter(userDto -> userDto.username().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(()-> new UsernameNotFoundException("User don't found"));
    }
}
