package in.tech_camp.chatapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.tech_camp.chatapp.entity.UserEntity;

@Mapper
public interface UserRepository {
  // ? ユーザーからの入力の値はどうやってVALUESに反映させるんだ？
  // #{}はmybatisの構文でinsertUserの引数の情報を受け取ることができる
  // @Insert("INSERT INTO users (name, email, password) VALUES (#{name}, #{email}, #{password})")
  // @Options(useGeneratedKeys=true, keyProperty="id")
  // void insertUser(UserEntity user);

  //   @Insert("INSERT INTO users (name, email, password) VALUES (#{name}, #{email}, #{password})")
  // @Options(useGeneratedKeys = true, keyProperty = "id")
  // void insert(UserEntity user);

  @Insert("INSERT INTO users (name, email, password) VALUES (#{name}, #{email}, #{password})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertUser(UserEntity user);

  @Select("SELECT * FROM users")
  List<UserEntity> getUsers();

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity getUserByEmail(String email);

  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity getUserById(Integer id);

  @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
  void updateUser(UserEntity user);
}
