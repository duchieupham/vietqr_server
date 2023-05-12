package com.vietqr.org.service;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vietqr.org.entity.AccountCustomerEntity;
import com.vietqr.org.repository.AccountCustomerRepository;

@Service
public class UserAuthorizedDetailServiceImpl implements UserDetailsService {

    @Autowired
    AccountCustomerRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AccountCustomerEntity entity = repo.getUserByUsername(username);
        if (entity == null || entity.getId() == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(
                entity.getUsername(),
                passwordEncoder.encode(entity.getPassword()),
                entity.isAvailable(),
                true,
                true,
                true,
                getAuthorities(entity.getRole()));

    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

}
