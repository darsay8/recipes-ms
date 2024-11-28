package dev.rm.recipes.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private String email;
  private String username;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(String email, String username, String password,
      Collection<? extends GrantedAuthority> authorities) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public String getActualUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
