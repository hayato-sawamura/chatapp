package in.tech_camp.chatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.chatapp.form.LoginForm;
import in.tech_camp.chatapp.form.UserForm;
import in.tech_camp.chatapp.service.UserAuthenticationService;
import in.tech_camp.chatapp.service.UserService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {
  private final UserAuthenticationService userAuthenticationService;
  private final UserService userService;

  @GetMapping("/users/sign_up")
  public String showUserSignUp(Model model) {
    model.addAttribute("userForm", new UserForm());
    return "users/signUp";
  }

  // SecurityConfigで許可したPostメソッドのパス名記述（.requestMatchers(HttpMethod.POST, "/user").permitAll()）
  @PostMapping("/user")
  public String newUser(@ModelAttribute("userForm") UserForm userForm, Model model) {
      //TODO: process POST request
      // ?
      // - ユーザーからの引数の受け取り方(RepositoryからInsertを呼び出す)
      // - どうやってユーザーが入力した情報を受け取るんだろう（@ModelAttribute）
      // --> https://spring.pleiades.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/modelattrib-method-args.html
      try {
        userService.insertUserWithEncryptedPassword(userForm);
      } catch (Exception e) {
        System.out.println("エラー：" + e);
        model.addAttribute("userForm", userForm);
        return "users/signUp";
      }
      return "redirect:/";
  }

  // ログイン画面の表示
  @GetMapping("/users/login")
  public String showUserLogin(Model model) {
    model.addAttribute("loginForm", new LoginForm());
    return "users/login";
  }

  @GetMapping("/login")
  public String login(@RequestParam(value = "error", required = false) String error, @ModelAttribute("loginForm") LoginForm loginForm, Model model) {
    if (error != null) {
      model.addAttribute("loginError", "メールアドレスかパスワードが間違っています。");
    }
    return "users/login";
  }

  
}