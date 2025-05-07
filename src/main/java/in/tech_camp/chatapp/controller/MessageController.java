package in.tech_camp.chatapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chatapp.login.CustomUserDetails;
import in.tech_camp.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository;

  @GetMapping("/")
  public String showMessages(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
    // AuthenticationPrincipalアノテーションインタフェースからAuthenticationクラスにあるgetPrincipalメソッドを呼び、
    // その返り値の型がUserDetailsを返すので、それを継承したCustomUserDetailsを取得する
    // https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/web/bind/annotation/AuthenticationPrincipal.html
    // Authenticationインタフェース
    // https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/core/Authentication.html#getPrincipal()

    // messages/indexに名前とeditに遷移するためのユーザーIDの情報を渡すためにログインしてるユーザーの情報を取得し
    model.addAttribute("user", userRepository.getUserById(currentUser.getId()));
    return "messages/index";
  }
}