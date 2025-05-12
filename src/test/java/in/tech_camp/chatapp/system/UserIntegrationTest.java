package in.tech_camp.chatapp.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import in.tech_camp.chatapp.ChatappApplication;
import in.tech_camp.chatapp.factories.UserFormFactory;
import in.tech_camp.chatapp.form.UserForm;
import in.tech_camp.chatapp.service.UserAuthenticationService;
import in.tech_camp.chatapp.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatappApplication.class)
@AutoConfigureMockMvc
public class UserIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;
  
  @Autowired
  private UserAuthenticationService userAuthenticationService;

  @Test
  public void ログインしていない状態でトップページにアクセスした場合サインインページに移動する() throws Exception {
    // トップページに移動し、再度ログインページにリダイレクトされることを確認する
    mockMvc.perform(MockMvcRequestBuilders.get("/"))
            .andExpect(MockMvcResultMatchers.status().isFound());
            // .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("ログイン")));
            // .andReturn(MockMvcResultMatchers.view().name("/users/login"));
  }

  @Test
  public void ログインに成功しトップページに遷移する() throws Exception {
    // 予め、ユーザーをDBに保存する
    // UserFactoryからuserForm作成し、UserServiceのinsertUserWithEncryptedPasswordの引数に渡す
    UserForm userForm = UserFormFactory.createUser();
    userService.insertUserWithEncryptedPassword(userForm);

    // サインインページに遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/users/login"))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andReturn();
    // 保存したユーザーでログインする
    // Loginのリクエストにemailとpasswordを渡す
    MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("email", userForm.getEmail())
    .param("password", userForm.getPassword())
    .with(SecurityMockMvcRequestPostProcessors.csrf()))
    .andExpect(MockMvcResultMatchers.status().isFound())
    .andReturn();
    assertNotNull(loginResult.getRequest().getSession());
    assertEquals("/", loginResult.getResponse().getRedirectedUrl());    
    System.out.println("ログイン成功の場合：" + loginResult.getResponse().getContentAsString());
    
    UserDetails customUser = userAuthenticationService.loadUserByUsername(userForm.getEmail());
    // トップページに再度アクセスし、ログインできていることを確認する
    mockMvc.perform(MockMvcRequestBuilders.get("/").with(SecurityMockMvcRequestPostProcessors.user(customUser)))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void ログインに失敗し再びサインインページに戻ってくる() throws Exception {
    // 予め、ユーザーをDBに保存する
    UserForm userForm = UserFormFactory.createUser();
    userService.insertUserWithEncryptedPassword(userForm);
    // サインインページに遷移する
    mockMvc.perform(MockMvcRequestBuilders.get("/users/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    // 誤ったユーザーでログインするとエラーパスにリダイレクトされる
    MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("email", "test")
                    .param("password", "test")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(MockMvcResultMatchers.status().isFound())
                    .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error"))
                    .andReturn();

    // エラーパスにリダイレクトされたとき、サインインのビューが表示される
    
    MvcResult loginErrorResult = mockMvc.perform(MockMvcRequestBuilders.get("/login?error").param("error", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users/login"))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("登録しているユーザーでログイン")))
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("メールアドレスもしくはパスワードが間違っています。")))
                .andReturn();
    System.err.println("サインインのビュー表示：" + loginErrorResult.getResponse().getContentAsString());
  }
}
