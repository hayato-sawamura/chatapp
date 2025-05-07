package in.tech_camp.chatapp.login;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.tech_camp.chatapp.entity.UserEntity;
import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {
  private final UserEntity user;

  public CustomUserDetails(UserEntity user) {
    this.user = user;
  }

  // ???
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.emptyList();
  }

  @Override
  public String getUsername() {
    return this.user.getEmail();
  }

  @Override
  public String getPassword() {
    return this.user.getPassword();
  }

  public int getId() {
    return this.user.getId();
  }

  public String getName() {
    return this.user.getName();
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
