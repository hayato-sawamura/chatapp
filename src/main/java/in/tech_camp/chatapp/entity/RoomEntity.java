package in.tech_camp.chatapp.entity;

import java.util.List;

import lombok.Data;

@Data
public class RoomEntity {
  private Integer id;
  private String name;
  private List<RoomUserEntity> roomUsers;
  private List<MessageEntity> messages;
}
