package in.tech_camp.chatapp.factories;

import org.springframework.mock.web.MockMultipartFile;

import com.github.javafaker.Faker;

import in.tech_camp.chatapp.form.MessageForm;

public class MessageFormFactory {
  private static final Faker faker = new Faker();

  public static MessageForm createMessage() {
    MessageForm messageForm = new MessageForm();

    messageForm.setContent(faker.lorem().sentence());
    // MockMultipartFile
    // https://spring.pleiades.io/spring-framework/docs/current/javadoc-api/org/springframework/mock/web/MockMultipartFile.html
    messageForm.setImage(new MockMultipartFile("image", "image.jpg", "image/jpeg", faker.avatar().image().getBytes()));

    return messageForm;
  }
}