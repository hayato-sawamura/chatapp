package in.tech_camp.chatapp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.chatapp.ImageUrl;
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
  private final ImageUrl imageUrl;

  @GetMapping("/")
  public String showRoomMessage(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
    // AuthenticationPrincipalアノテーションインタフェースからAuthenticationクラスにあるgetPrincipalメソッドを呼び、
    // その返り値の型がUserDetailsを返すので、それを継承したCustomUserDetailsを取得する
    // https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/web/bind/annotation/AuthenticationPrincipal.html
    // Authenticationインタフェース
    // https://spring.pleiades.io/spring-security/site/docs/current/api/org/springframework/security/core/Authentication.html#getPrincipal()

    // messages/indexに名前とeditに遷移するためのユーザーIDの情報を渡すためにログインしてるユーザーの情報を取得し
    model.addAttribute("currentUser", userRepository.getUserById(currentUser.getId()));
    
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
    model.addAttribute("currentUser", userRepository.getUserById(currentUser.getId()));
    
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
    model.addAttribute("currentUser", userRepository.getUserById(currentUser.getId()));
    
    List<RoomUserEntity> roomUser = roomUserRepository.getRoomUserByUserId(currentUser.getId());
    List<RoomEntity> rooms = roomUser.stream()
        .map(RoomUserEntity::getRoom)
        .collect(Collectors.toList());
    model.addAttribute("rooms", rooms);

    model.addAttribute("currentRoom", roomRepository.getRoomById(roomId));

    List<MessageEntity> messages = messageRepository.getMessagesByRoomId(roomId);
    model.addAttribute("messages", messages);
 
    model.addAttribute("messageForm", new MessageForm());
  
    return "messages/index";
  }
  
  @PostMapping("/rooms/{roomId}/messages")
  public String saveMessage(@ModelAttribute("messageForm") MessageForm messageForm, BindingResult result, @PathVariable("roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
    messageForm.validateMessage(result);
    if (result.hasErrors()) {
      System.err.println("Error" + result.getAllErrors().toString());
      return "redirect:/rooms/" + roomId + "/messages";
    }
    
    
    MessageEntity message = new MessageEntity();
    message.setContent(messageForm.getContent());
    
    MultipartFile imageFile = messageForm.getImage();
    if (imageFile != null && !imageFile.isEmpty()) {
      try {
        String uploadDir = imageUrl.getImageUrl();
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + imageFile.getOriginalFilename();
        java.nio.file.Path imagePath = Paths.get(uploadDir, fileName);
        Files.copy(imageFile.getInputStream(), imagePath);
        message.setImage("/uploads/" + fileName);
      } catch (IOException e) {
        System.out.println("エラー：" + e);
        return "redirect:/rooms/" + roomId + "/messages";
      }
    }

    UserEntity user = userRepository.getUserById(currentUser.getId());
    RoomEntity room = roomRepository.getRoomById(roomId);
    message.setUser(user);
    message.setRoom(room);
    
    try {
      messageRepository.insertMessage(message);
    } catch (Exception e) {
      System.out.println("Error：" + e);
      model.addAttribute("errorMessages", "メッセージの内容が保存できませんでした。");
      return "redirect:/rooms/" + roomId + "/messages";
    }
    return "redirect:/rooms/" + roomId + "/messages";
  }
  
}