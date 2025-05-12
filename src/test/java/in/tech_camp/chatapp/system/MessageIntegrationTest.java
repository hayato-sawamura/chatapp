package in.tech_camp.chatapp.system;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import in.tech_camp.chatapp.ChatappApplication;
import in.tech_camp.chatapp.ImageUrl;
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
public class MessageIntegrationTest {

  @Autowired
  private UserService userService;
  
  @Autowired
  private UserAuthenticationService userAuthenticationService;
  
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoomRepository roomRepository;

  @Autowired
  private RoomUserRepository roomUserRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ImageUrl imageUrl;

  @Autowired
  private MockMvc mockMvc;

  private UserForm userForm1;
  private UserForm userForm2;
  private RoomForm roomForm;
  private UserEntity userEntity1;
  private UserEntity userEntity2;
  private RoomEntity roomEntity;

  private MessageForm messageForm;

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

    RoomUserEntity roomUserEntity = new RoomUserEntity();
    roomUserEntity.setRoom(roomEntity);
    roomUserEntity.setUser(userEntity1);
    roomUserRepository.insertRoomUser(roomUserEntity);
    roomUserEntity.setRoom(roomEntity);
    roomUserEntity.setUser(userEntity2);
    roomUserRepository.insertRoomUser(roomUserEntity);

    messageForm = MessageFormFactory.createMessage();
  }

  @AfterEach
  public void cleanup() throws IOException {
    Path directoryPath = Paths.get(imageUrl.getImageUrl());

    // ディレクトリが存在することを確認
    if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
      // ディレクトリ内のすべてのファイルを削除
      try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
        for (Path filePath : directoryStream) {
          Files.deleteIfExists(filePath);
        }
      }
    }
  }

  @Test
  public void メッセージの送信に失敗する() throws Exception {
    // サインインする
    UserDetails customUser = userAuthenticationService.loadUserByUsername(userForm1.getEmail());
    mockMvc.perform(MockMvcRequestBuilders.get("/login").with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("messages/index"));
    // メッセージを投稿し、元のページに戻ってくることを確認する
    List<MessageEntity> beforeMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
            
    mockMvc.perform(MockMvcRequestBuilders.post("/rooms/" + roomEntity.getId() + "/messages", roomEntity.getId())
            .with(SecurityMockMvcRequestPostProcessors.user(customUser))
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isFound())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

    // メッセージの数が変化しないことを確認する
    List<MessageEntity> afterMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
    assertEquals(beforeMessages.size(), afterMessages.size());
  }
  
  @Test
  public void テキストの投稿に成功する() throws Exception {
    // サインインする
    UserDetails customUser = userAuthenticationService.loadUserByUsername(userForm1.getEmail());
    mockMvc.perform(MockMvcRequestBuilders.get("/login").with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("messages/index"));

  MessageForm messageForm = MessageFormFactory.createMessage();
  // テキストメッセージを投稿し、チャットルームに遷移していることを確認する
  List<MessageEntity> beforeMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());

  mockMvc.perform(MockMvcRequestBuilders.post("/rooms/{roomId}/messages", roomEntity.getId())
          .param("content", messageForm.getContent())
          .with(SecurityMockMvcRequestPostProcessors.user(customUser))
          .with(SecurityMockMvcRequestPostProcessors.csrf()))
          .andExpect(MockMvcResultMatchers.status().isFound())
          .andExpect(MockMvcResultMatchers.redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

    // メッセージの数が1増加していることを確認する  
    List<MessageEntity> afterMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
    assertEquals(beforeMessages.size() + 1, afterMessages.size());
    // チャットルームには先ほどの投稿が存在することを確認する（テキスト）
    assertEquals(messageForm.getContent(), afterMessages.get(0).getContent());
  }

  @Test
  public void 画像の投稿に成功する() throws Exception {
    // サインインする
    UserDetails customUser = userAuthenticationService.loadUserByUsername(userForm1.getEmail());
    mockMvc.perform(MockMvcRequestBuilders.get("/login").with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("messages/index"));
  MessageForm messageForm = MessageFormFactory.createMessage();
   // 画像を投稿し、チャットルームに遷移していることを確認する
  List<MessageEntity> beforeMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());

  mockMvc.perform(MockMvcRequestBuilders.multipart("/rooms/{roomId}/messages", roomEntity.getId())
          .file((MockMultipartFile)messageForm.getImage())
          .with(SecurityMockMvcRequestPostProcessors.user(customUser))
          .with(SecurityMockMvcRequestPostProcessors.csrf()))
          .andExpect(MockMvcResultMatchers.status().isFound())
          .andExpect(MockMvcResultMatchers.redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

    // メッセージの数が1増加していることを確認する  
    List<MessageEntity> afterMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
    assertEquals(beforeMessages.size() + 1, afterMessages.size());
    // チャットルームには先ほどの投稿が存在することを確認する（画像）
    MessageEntity targetMessage = afterMessages.get(0);
    System.out.println("Message.Image: " + targetMessage.getImage());

    
    MvcResult messageResult = mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
                      .andExpect(MockMvcResultMatchers.status().isOk())
                      .andExpect(MockMvcResultMatchers.view().name("messages/index"))
                      .andReturn();
    String messagePageContent = messageResult.getResponse().getContentAsString();
    System.out.println("messagePage: " + messagePageContent);
    Document document = Jsoup.parse(messagePageContent);
    Element divElement = document.selectFirst("img[src="+ targetMessage.getImage()+"]");
    assertNotNull(divElement);  
}

  @Test
  public void テキストと画像の投稿に成功する() throws Exception {
    // サインインする
    UserDetails customUser = userAuthenticationService.loadUserByUsername(userForm1.getEmail());
    mockMvc.perform(MockMvcRequestBuilders.get("/login").with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    // 作成されたチャットルームへ遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("messages/index"));
  
            MessageForm messageForm = MessageFormFactory.createMessage();
    // テキストと画像のメッセージを投稿し、チャットルームに遷移していることを確認する

  List<MessageEntity> beforeMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());

  // チャットルームには先ほどの投稿が存在することを確認する（テキスト）
  mockMvc.perform(MockMvcRequestBuilders.multipart("/rooms/{roomId}/messages", roomEntity.getId())
          .file((MockMultipartFile)messageForm.getImage())
          .param("content", messageForm.getContent())
          .with(SecurityMockMvcRequestPostProcessors.user(customUser))
          .with(SecurityMockMvcRequestPostProcessors.csrf()))
          .andExpect(MockMvcResultMatchers.status().isFound())
          .andExpect(MockMvcResultMatchers.redirectedUrl("/rooms/" + roomEntity.getId() + "/messages"));

    // メッセージの数が1増加していることを確認する  
    List<MessageEntity> afterMessages = messageRepository.getMessagesByRoomId(roomEntity.getId());
    assertEquals(beforeMessages.size() + 1, afterMessages.size());
    MessageEntity targetMessage = afterMessages.get(0);
    // チャットルームには先ほどの投稿が存在することを確認する（テキスト）
    assertEquals(messageForm.getContent(), targetMessage.getContent());
    // チャットルームには先ほどの投稿が存在することを確認する（画像）
    MvcResult messageResult = mockMvc.perform(MockMvcRequestBuilders.get("/rooms/{roomId}/messages", roomEntity.getId()).with(SecurityMockMvcRequestPostProcessors.user(customUser)).with(SecurityMockMvcRequestPostProcessors.csrf()))
                      .andExpect(MockMvcResultMatchers.status().isOk())
                      .andExpect(MockMvcResultMatchers.view().name("messages/index"))
                      .andReturn();
    String messagePageContent = messageResult.getResponse().getContentAsString();
    System.out.println("messagePage: " + messagePageContent);
    Document document = Jsoup.parse(messagePageContent);
    Element divElement = document.selectFirst("img[src="+ targetMessage.getImage()+"]");
    assertNotNull(divElement);  
  }
}