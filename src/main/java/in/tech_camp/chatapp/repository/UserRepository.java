package in.tech_camp.chatapp.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

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

  @Select("SELECT * from users")
  List<UserEntity> getUsers();

  @Select("SELECT * from users WHERE email = #{email}")
  UserEntity getUsersByEmail(String email);
}
