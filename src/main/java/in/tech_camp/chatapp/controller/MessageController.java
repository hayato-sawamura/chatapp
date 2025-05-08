package in.tech_camp.chatapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.chatapp.entity.MessageEntity;
import in.tech_camp.chatapp.entity.RoomEntity;
import in.tech_camp.chatapp.entity.RoomUserEntity;
import in.tech_camp.chatapp.entity.UserEntity;
import in.tech_camp.chatapp.form.MessageForm;
import in.tech_camp.chatapp.login.CustomUserDetails;
import in.tech_camp.chatapp.repository.MessageRepository;
import in.tech_camp.chatapp.repository.RoomRepository;
import in.tech_camp.chatapp.repository.RoomUserRepository;
import in.tech_camp.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;



@Controller
@AllArgsConstructor
public class MessageController {
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final RoomRepository roomRepository;
  private final RoomUserRepository roomUserRepository;

  @GetMapping("/")
  public String showRoomMessage(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
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
    return "rooms/index";
  }

  @GetMapping("/message")
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
    model.addAttribute("messageForm", new MessageForm());
    return "rooms/index";
  }
  
  @GetMapping("/rooms/{roomId}/messages")
  public String showMessages(@PathVariable("roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetails currentUser, Model model){
    model.addAttribute("user", userRepository.getUserById(currentUser.getId()));
    
    List<RoomUserEntity> roomUser = roomUserRepository.getRoomUserByUserId(currentUser.getId());
    List<RoomEntity> rooms = roomUser.stream()
        .map(RoomUserEntity::getRoom)
        .collect(Collectors.toList());
    model.addAttribute("rooms", rooms);
    model.addAttribute("messageForm", new MessageForm());
    model.addAttribute("roomId", roomId);

    List<MessageEntity> messages = messageRepository.getMessagesByRoomId(roomId);
    model.addAttribute("messages", messages);
    return "messages/index";
  }

  @PostMapping("/rooms/{roomId}/messages")
  public String saveMessage(@ModelAttribute("messageForm") MessageForm messageForm, @PathVariable("roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetails currentUser) {
    MessageEntity message = new MessageEntity();
    message.setContent(messageForm.getContent());

    UserEntity user = userRepository.getUserById(currentUser.getId());
    RoomEntity room = roomRepository.getRoomById(roomId);
    message.setUser(user);
    message.setRoom(room);

    try {
      messageRepository.insertMessage(message);
    } catch (Exception e) {
      System.out.println("Error：" + e);
      return "redirect:/rooms/" + roomId + "/messages";
    }
    return "redirect:/rooms/" + roomId + "/messages";
  }
  
}