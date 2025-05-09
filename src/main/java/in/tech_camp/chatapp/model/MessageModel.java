package in.tech_camp.chatapp.model;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import in.tech_camp.chatapp.entity.MessageEntity;
import in.tech_camp.chatapp.entity.RoomEntity;
import in.tech_camp.chatapp.entity.RoomUserEntity;
import in.tech_camp.chatapp.form.MessageForm;
import in.tech_camp.chatapp.login.CustomUserDetails;
import in.tech_camp.chatapp.repository.MessageRepository;
import in.tech_camp.chatapp.repository.RoomRepository;
import in.tech_camp.chatapp.repository.RoomUserRepository;
import in.tech_camp.chatapp.repository.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MessageModel {
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final RoomRepository roomRepository;
  private final RoomUserRepository roomUserRepository;
  
  public void showMessagesModel(@PathVariable("roomId") Integer roomId, @AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
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
  }
}
