package in.tech_camp.chatapp.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chatapp.entity.RoomEntity;

@Mapper
public interface RoomRepository {
  @Insert("INSERT INTO rooms(name) VALUES(#{name})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  void insertRoom(RoomEntity roomEntity);

  @Select("SELECT * FROM rooms WHERE id = #{id}")
  RoomEntity getRoomById(Integer id);
}
