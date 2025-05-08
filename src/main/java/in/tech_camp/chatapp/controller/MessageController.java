package in.tech_camp.chatapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chatapp.entity.RoomEntity;
import in.tech_camp.chatapp.entity.RoomUserEntity;
import in.tech_camp.chatapp.login.CustomUserDetails;
import in.tech_camp.chatapp.repository.RoomUserRepository;
import in.tech_camp.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository;
  private final RoomUserRepository roomUserRepository;

  @GetMapping("/")
  public String showMessages(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
    // AuthenticationPrincipalアノテーションインタフェースからAuthenticationクラスにあるgetPrincipalメソッドを呼び、
    // その返り値の型がUserDetailsを返すので、それを継承したCustomUserDetailsを取得する
    // https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/web/bind/annotation/AuthenticationPrincipal.html
    // Authenticationインタフェース
    // https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/core/Authentication.html#getPrincipal()

    // messages/indexに名前とeditに遷移するためのユーザーIDの情報を渡すためにログインしてるユーザーの情報を取得し
    model.addAttribute("user", userRepository.getUserById(currentUser.getId()));
    
    List<RoomUserEntity> roomUser = roomUserRepository.getRoomUserByUserId(currentUser.getId());
    List<RoomEntity> rooms = roomUser.stream()
        .map(RoomUserEntity::getRoom)
        .collect(Collectors.toList());
    model.addAttribute("rooms", rooms);
    return "messages/index";
  }
}