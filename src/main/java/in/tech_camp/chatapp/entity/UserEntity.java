package in.tech_camp.chatapp.entity;

import lombok.Data;

@Data
public class UserEntity {
  private int id;
  private String name;
  private String email;
  private String password;
}
