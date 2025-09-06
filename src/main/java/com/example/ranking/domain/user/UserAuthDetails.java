package com.example.ranking.domain.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public record UserAuthDetails(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.password();
    }

    @Override
    public String getUsername() {
        return user.email();
    }

}
