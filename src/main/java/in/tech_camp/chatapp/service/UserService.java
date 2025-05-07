package in.tech_camp.chatapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.tech_camp.chatapp.entity.UserEntity;
import in.tech_camp.chatapp.form.UserForm;
import in.tech_camp.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;

// コントローラーで使用される想定
@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void insertUserWithEncryptedPassword(UserForm userForm) {
    // パスワードを暗号化する
    UserEntity userEntity = new UserEntity();
    userEntity.setName(userForm.getName());
    userEntity.setEmail(userForm.getEmail());
    userEntity.setPassword(getEncodedPassword(userForm.getPassword()));
    userRepository.insertUser(userEntity);
  }
  // パスワードを平文から暗号化する関数作成
  private String getEncodedPassword(String password) {
    return passwordEncoder.encode(password);
  }
}
