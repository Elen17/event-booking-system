package com.epam.campstone.eventbookingsystem.security;

import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.model.UserPasswordHistory;
import com.epam.campstone.eventbookingsystem.model.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final Long id;
    private final String email;
    private final String password;
    @Getter
    private final String salt;

    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isActive;

    public UserDetailsImpl(Long id, String email,
                           String password, String salt,
                           Collection<? extends GrantedAuthority> authorities,
                           boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    public static UserDetailsImpl build(User user) {
        UserRole role = user.getRole();
        GrantedAuthority authority = new SimpleGrantedAuthority(role.getName());

        UserPasswordHistory password = user.getUserPasswordHistories().stream()
                .max((o1, o2) ->
                        Objects.compare(o2.getCreatedAt(), o1.getCreatedAt(), Instant::compareTo))
                .orElseThrow();

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                password.getPasswordHash(),
                password.getSalt(),
                Collections.singletonList(authority),
                user.getIsActive()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
