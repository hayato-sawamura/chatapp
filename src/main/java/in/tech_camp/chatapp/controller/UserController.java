package in.tech_camp.chatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chatapp.form.UserForm;


@Controller
public class UserController {
  // @GetMapping("/users/login")
  // public String showUserLogin() {
  //     return "users/login";
  // }
  

  @GetMapping("/users/sign_up")
  public String showUserSignUp(Model model) {
    model.addAttribute("userForm", new UserForm());
    return "users/signUp";
  }
  
}