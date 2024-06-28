package com.example.swipeflix.services;

import com.example.swipeflix.models.ERole;
import com.example.swipeflix.models.Role;
import com.example.swipeflix.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role findByName(ERole role) {
        return roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }
}
