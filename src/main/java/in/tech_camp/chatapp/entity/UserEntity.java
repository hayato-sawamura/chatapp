package in.tech_camp.chatapp.entity;

import java.util.List;

import lombok.Data;

@Data
public class UserEntity {
  private int id;
  private String name;
  private String email;
  private String password;
  private List<RoomUserEntitny> roomUsers;
}
