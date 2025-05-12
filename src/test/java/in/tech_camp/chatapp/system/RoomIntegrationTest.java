package in.tech_camp.chatapp.system;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import in.tech_camp.chatapp.ChatappApplication;
import in.tech_camp.chatapp.entity.MessageEntity;
import in.tech_camp.chatapp.entity.RoomEntity;
import in.tech_camp.chatapp.entity.RoomUserEntity;
import in.tech_camp.chatapp.entity.UserEntity;
import in.tech_camp.chatapp.factories.MessageFormFactory;
import in.tech_camp.chatapp.factories.RoomFormFactory;
import in.tech_camp.chatapp.factories.UserFormFactory;
import in.tech_camp.chatapp.form.MessageForm;
import in.tech_camp.chatapp.form.RoomForm;
import in.tech_camp.chatapp.form.UserForm;
import in.tech_camp.chatapp.repository.MessageRepository;
import in.tech_camp.chatapp.repository.RoomRepository;
import in.tech_camp.chatapp.repository.RoomUserRepository;
import in.tech_camp.chatapp.repository.UserRepository;
import in.tech_camp.chatapp.service.UserAuthenticationService;
import in.tech_camp.chatapp.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatappApplication.class)
@AutoConfigureMockMvc
public class RoomIntegrationTest {
  private UserForm userForm1;
  private UserForm userForm2;
  private RoomForm roomForm;

  private UserEntity userEntity1;
  private UserEntity userEntity2;
  private RoomEntity roomEntity;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserAuthenticationService userAuthenticationService;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private RoomUserRepository roomUserRepository;

  @Autowired
  private MessageRepository messageRepository;

  @BeforeEach
  public void setup() {
    userForm1 = UserFormFactory.createUser();
    userService.insertUserWithEncryptedPassword(userForm1);
    userEntity1 = userRepository.getUserByEmail(userForm1.getEmail());
    
    
    userForm2 = UserFormFactory.createUser();
    userService.insertUserWithEncryptedPassword(userForm2);
    userEntity2 = userRepository.getUserByEmail(userForm2.getEmail());

    roomForm = RoomFormFactory.createRoom();
    roomEntity = new RoomEntity();
    roomEntity.setName(roomForm.getName());
    roomRepository.insertRoom(roomEntity);

    RoomUserEntity roomUserEntity1 = new RoomUserEntity();
    roomUserEntity1.setRoom(roomEntity);
    roomUserEntity1.setUser(userEntity1);
    roomUserRepository.insertRoomUser(roomUserEntity1);
  
    RoomUserEntity roomUserEntity2 = new RoomUserEntity();
    roomUserEntity2.setRoom(roomEntity);
    roomUserEntity2.setUser(userEntity2);
    roomUserRepository.insertRoomUser(roomUserEntity2);
  }

  @Test
  public void チャットルームを削除すると関連するメッセージがすべて削除されている() throws Exception {
    // サインインする
    UserDetails customUser = userAuthenticationService.loadUserByUsername(userForm1.getEmail());
    mockMvc.perform(MockMvcRequestBuilders.get("/login").with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("messages/index"));
  
            // テキストと画像のメッセージを投稿し、チャットルームに遷移していることを確認する
            
    List<MessageEntity> beforeMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
    // メッセージ情報を5つDBに追加する
    MessageForm messageForm;
    for (int i = 0; i < 5; i++) {
        messageForm = MessageFormFactory.createMessage();
        mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{roomId}/messages", roomEntity.getId())
                .param("content", messageForm.getContent())
                .with(SecurityMockMvcRequestPostProcessors.user(customUser))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));
    }

    List<MessageEntity> afterMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
    assertEquals(beforeMessages.size() + 5, afterMessages.size());

    // チャットルームを削除するとトップページに遷移することを確認する
    mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{roomId}/delete", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
    .andExpect(MockMvcResultMatchers.status().isFound())
    .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

    // 作成した5つのメッセージが削除されていることを確認する
    assertEquals(beforeMessages.size(), afterMessages.size() - 5);
  }
}