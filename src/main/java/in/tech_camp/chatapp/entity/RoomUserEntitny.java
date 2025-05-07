package in.tech_camp.chatapp.entity;

import lombok.Data;

@Data
public class RoomUserEntitny {
  private Integer id;
  private UserEntity user;
  private RoomEntity room;
}
