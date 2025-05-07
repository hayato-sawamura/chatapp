package in.tech_camp.chatapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chatapp.entity.UserEntity;
import in.tech_camp.chatapp.form.RoomForm;
import in.tech_camp.chatapp.login.CustomUserDetails;
import in.tech_camp.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class RoomController {
  private final UserRepository userRepository;

  @GetMapping("/rooms/new")
  public String showNewRoom(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
    List<UserEntity> users = userRepository.getUsersExceptId(currentUser.getId());
    model.addAttribute("users", users);
    model.addAttribute("roomForm", new RoomForm());
    return "rooms/new";
  }  
}
