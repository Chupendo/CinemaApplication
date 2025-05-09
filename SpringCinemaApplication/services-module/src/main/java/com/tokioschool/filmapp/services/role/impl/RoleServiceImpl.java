package com.tokioschool.filmapp.services.role.impl;

import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.filmapp.repositories.RoleDao;
import com.tokioschool.filmapp.services.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final ModelMapper modelMapper;

    @Override
    public List<RoleDto> getAllRoles() {
        return roleDao.findAll().stream().map(role -> modelMapper.map(role, RoleDto.class)).toList();
    }
}
