package in.tech_camp.chatapp.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import in.tech_camp.chatapp.entity.MessageEntity;

@Mapper
public interface MessageRepository {
  @Insert("INSERT INTO messages (content, user_id, room_id) VALUES (#{content}, #{user.id}, #{room.id})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  void insertMessage(MessageEntity messageEntity);
}
