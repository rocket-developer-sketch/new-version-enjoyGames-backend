package com.easygame.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final String gameType;
    private final String jti;
    private final String nickName;

    public CustomUserDetails(String gameType, String jti, String nickName) {
        this.gameType = gameType;
        this.jti = jti;
        this.nickName = nickName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return jti;
    }

    public String getGameType() {
        return gameType;
    }
    public String getNickName() {
        return nickName;
    }

    @Override public String getPassword() { return ""; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "gameType='" + gameType + '\'' +
                ", jti='" + jti + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
