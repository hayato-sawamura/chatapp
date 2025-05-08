package in.tech_camp.chatapp.entity;

import java.security.Timestamp;

import lombok.Data;

@Data
public class MessageEntity {
  private Integer id;
  private String content;
  private Integer user_id;
  private Integer room_id;
  private String image;
  private Timestamp created_at;
  private UserEntity user;
  private RoomEntity room;
}
