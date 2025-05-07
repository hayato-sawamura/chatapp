package in.tech_camp.chatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.chatapp.entity.UserEntity;
import in.tech_camp.chatapp.form.LoginForm;
import in.tech_camp.chatapp.form.UserEditForm;
import in.tech_camp.chatapp.form.UserForm;
import in.tech_camp.chatapp.repository.UserRepository;
import in.tech_camp.chatapp.service.UserService;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserRepository userRepository;

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
        System.out.println("Error：" + e);
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
    
    @GetMapping("/users/{userId}/edit")
    public String showUserEdit(@PathVariable("userId") Integer id, Model model) {
      // ユーザーの編集画面の表示
    // UserEditFormのインスタンスをusers/editに渡し、表示させる
    UserEditForm userEditForm = new UserEditForm();
    
    try {
      UserEntity user = userRepository.getUserById(id);
      userEditForm.setId(user.getId());
      userEditForm.setName(user.getName());
      userEditForm.setEmail(user.getEmail());
    } catch (Exception e) {
      System.out.println("Error：" + e);
      return "error";
    }
    // ? URLのパスからUserIdのパラメータを受け取る方法(@PathVariable(パス名))
    // https://spring.pleiades.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/PathVariable.html
    // UserEntity userEntity = userRepository.getUserById(id);
    model.addAttribute("user", userEditForm);
    return "users/edit";
  }
  
  @PostMapping("/users/{userId}")
  public String editUser(@ModelAttribute("user") UserEditForm userEditForm, @PathVariable("userId") Integer id, Model model) {
    UserEntity user = userRepository.getUserById(id);
    user.setName(userEditForm.getName());
    user.setEmail(userEditForm.getEmail());
    try {
      userRepository.updateUser(user);
    } catch (Exception e) {
      System.out.println("Error：" + e);
      model.addAttribute("user", userEditForm);
      return "users/edit";
    }
    return "redirect:/";
  }

  // logout機能はSecurityConfigファイルで作成しているため記述しなくよい

}
