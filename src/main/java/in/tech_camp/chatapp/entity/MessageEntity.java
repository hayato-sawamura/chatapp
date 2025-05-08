package in.tech_camp.chatapp.entity;

import java.security.Timestamp;

import lombok.Data;

@Data
public class MessageEntity {
  private Integer id;
  private String content;
  private String image;
  private Timestamp created_at;
  private UserEntity user;
  private RoomEntity room;
}
