package in.tech_camp.chatapp.form;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.chatapp.factories.MessageFormFactory;

@ActiveProfiles("test")
@SpringBootTest
public class MessageFormUnitTest {
  private MessageForm messageForm;
  private BindingResult bindingResult;

  @BeforeEach
  public void setUp() {
    messageForm = MessageFormFactory.createMessage();
    bindingResult = Mockito.mock(BindingResult.class);
  }

  @Nested
  class メッセージが投稿できる場合 {
    @Test
    public void contentとimageが存在していれば保存できる () {
      assertTrue(messageForm.getContent().length() > 0);
      assertTrue(messageForm.getImage().getSize() > 0);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(0)).rejectValue(anyString(), anyString(), anyString());
    }
    
    @Test
    public void contentが空でも保存できる () {
      messageForm.setContent("");
      assertTrue(messageForm.getContent().isEmpty());
      assertTrue(messageForm.getImage().getSize() > 0);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(0)).rejectValue(anyString(), anyString(), anyString());      
    }
    
    @Test
    public void contentがnullでも保存できる () {
      messageForm.setContent(null);
      assertTrue(messageForm.getContent() == null);
      assertTrue(messageForm.getImage().getSize() > 0);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(0)).rejectValue(anyString(), anyString(), anyString());
    }
    
    @Test
    public void imageが空でも保存できる () {
      byte[] emptyBytes = new byte[0];
      messageForm.setImage(new MockMultipartFile("empty", "empty.jpg", "image/jpeg", emptyBytes));
      assertTrue(messageForm.getContent().length() > 0);
      assertTrue(messageForm.getImage().isEmpty());
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(0)).rejectValue(anyString(), anyString(), anyString());
    }
    
    @Test
    public void imageがnullでも保存できる () {
      messageForm.setImage(null);
      assertTrue(messageForm.getContent().length() > 0);
      assertTrue(messageForm.getImage() == null);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(0)).rejectValue(anyString(), anyString(), anyString());
    }
  }
  
  @Nested
  class メッセージが投稿できない場合 {
    @Test
    public void contentが空かつimageが空ファイルだと保存できない() {
      messageForm.setContent("");
      assertTrue(messageForm.getContent().isEmpty());

      byte[] emptyBytes = new byte[0];
      messageForm.setImage(new MockMultipartFile("empty", "empty.jpg", "image/jpeg", emptyBytes));
      assertTrue(messageForm.getImage().isEmpty());
      
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(1)).rejectValue("content", "error.Message", "Please enter either content or image");
    }
    
    @Test
    public void contentが空かつimageがnullだと保存できない() {
      messageForm.setContent("");
      assertTrue(messageForm.getContent().isEmpty());

      messageForm.setImage(null);
      assertTrue(messageForm.getImage() == null);

      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(1)).rejectValue("content", "error.Message", "Please enter either content or image");
    }
    
    @Test
    public void contentがnullかつimageが空ファイルだと保存できない() {
      messageForm.setContent(null);
      assertTrue(messageForm.getContent() == null);

      byte[] emptyBytes = new byte[0];
      messageForm.setImage(new MockMultipartFile("empty", "empty.jpg", "image/jpeg", emptyBytes));
      assertTrue(messageForm.getImage().isEmpty());
      
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(1)).rejectValue("content", "error.Message", "Please enter either content or image");
    }
    
    @Test
    public void contentがnullかつimageがnullだと保存できない() {
      messageForm.setContent(null);
      assertTrue(messageForm.getContent() == null);
  
      messageForm.setImage(null);      
      assertTrue(messageForm.getImage() == null);
      
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, times(1)).rejectValue("content", "error.Message", "Please enter either content or image");
    }
  }
}