package in.tech_camp.chatapp.form;

import java.util.List;

import in.tech_camp.chatapp.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoomForm {
  @NotBlank(message="This name is empty", groups=ValidationPriority1.class)
  private String name;
  private List<Integer> memberIds;
}
