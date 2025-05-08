package in.tech_camp.chatapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chatapp.entity.RoomUserEntity;

@Mapper
public interface RoomUserRepository {
  @Insert("INSERT INTO room_users(user_id, room_id) VALUES(#{user.id}, #{room.id})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  void insertRoomUser(RoomUserEntity roomUser);

  @Select("SELECT * FROM room_users WHERE user_id = #{userId}")
  @Result(
    property="room", column="room_id", one=@One(select="in.tech_camp.chatapp.repository.RoomRepository.getRoomById")
  )
  List<RoomUserEntity> getRoomUserByUserId(Integer userId);
}
