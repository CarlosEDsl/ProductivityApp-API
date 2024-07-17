package com.eduardocarlos.productivityApp.services;

import com.eduardocarlos.productivityApp.models.enums.ProfileEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private ProfileEnum profile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.profile == ProfileEnum.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_DEFAULT"));
        else return List.of(new SimpleGrantedAuthority("ROLE_DEFAULT"));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
