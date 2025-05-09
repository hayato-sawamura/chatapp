package in.tech_camp.chatapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chatapp.entity.MessageEntity;

@Mapper
public interface MessageRepository {
  @Insert("INSERT INTO messages (content, image, user_id, room_id) VALUES (#{content}, #{image}, #{user.id}, #{room.id})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  void insertMessage(MessageEntity messageEntity);

  @Select("SELECT * FROM messages WHERE room_id = #{roomId}")
  @Results(value = {
    @Result(column = "created_at", property="createdAt"),
    @Result(
      column="user_id", property="user", one=@One(select="in.tech_camp.chatapp.repository.UserRepository.getUserById")
    )
  })
  List<MessageEntity> getMessagesByRoomId(Integer roomId);
}
