package in.tech_camp.chatapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.chatapp.entity.RoomEntity;
import in.tech_camp.chatapp.entity.RoomUserEntity;
import in.tech_camp.chatapp.entity.UserEntity;
import in.tech_camp.chatapp.form.RoomForm;
import in.tech_camp.chatapp.login.CustomUserDetails;
import in.tech_camp.chatapp.repository.RoomRepository;
import in.tech_camp.chatapp.repository.RoomUserRepository;
import in.tech_camp.chatapp.repository.UserRepository;
import in.tech_camp.chatapp.validation.ValidationOrder;
import lombok.AllArgsConstructor;



@Controller
@AllArgsConstructor
public class RoomController {
  private final UserRepository userRepository;
  private final RoomRepository roomRepository;
  private final RoomUserRepository roomUserRepository;

  @GetMapping("/rooms/new")
  public String showNewRoom(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
    List<UserEntity> users = userRepository.getUsersExceptId(currentUser.getId());
    model.addAttribute("users", users);
    model.addAttribute("roomForm", new RoomForm());
    return "rooms/new";
  }

  @PostMapping("/rooms")
  public String createRoom(@ModelAttribute("roomForm") @Validated(ValidationOrder.class) RoomForm roomForm, BindingResult result, @AuthenticationPrincipal CustomUserDetails currentUser, Model model){
    // チャットメンバーの選択してくださいのままボタンを押した場合の挙動は？
    if (roomForm.getMemberIds() == null) {
      result.rejectValue("user_id", "error.user", "This item is a questions");
    }

    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream()
              .map(DefaultMessageSourceResolvable::getDefaultMessage)
              .collect(Collectors.toList());
      List<UserEntity> users = userRepository.getUsersExceptId(currentUser.getId());
      model.addAttribute("users", users);
      model.addAttribute("roomForm", roomForm);
      model.addAttribute("errorMessages", errorMessages);
      return "rooms/new";
    }

    RoomEntity roomEntity = new RoomEntity();
    roomEntity.setName(roomForm.getName());
    try {
      roomRepository.insertRoom(roomEntity);
    } catch (Exception e) {
      System.out.println("Error：" + e);
      List<UserEntity> users = userRepository.getUsersExceptId(currentUser.getId());
      model.addAttribute("users", users);
      model.addAttribute("roomForm", new RoomForm());
      return "rooms/new";
    }

    List<Integer> memberIds = roomForm.getMemberIds();
    for (Integer userId : memberIds) {
      UserEntity userEntity = userRepository.getUserById(userId);
      RoomUserEntity roomUserEntity = new RoomUserEntity();
      roomUserEntity.setRoom(roomEntity);
      roomUserEntity.setUser(userEntity);
      try {
        roomUserRepository.insertRoomUser(roomUserEntity);
      } catch (Exception e) {
        System.out.println("Error:" + e);
        List<UserEntity> users = userRepository.getUsersExceptId(currentUser.getId());
        model.addAttribute("users", users);
        model.addAttribute("roomForm", new RoomForm());
        return "rooms/new";
      }
    }
    return "redirect:/";
  }

  @PostMapping("/rooms/{roomId}/delete")
  public String postMethodName(@PathVariable("roomId") Integer roomId) {
      roomRepository.deleteRoomById(roomId);
      return "redirect:/";
  }
  
}
