package in.tech_camp.chatapp.form;

import in.tech_camp.chatapp.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageForm {
  @NotBlank(message="This content is empty", groups=ValidationPriority1.class)
  private String content;
  // private String image; 
}
