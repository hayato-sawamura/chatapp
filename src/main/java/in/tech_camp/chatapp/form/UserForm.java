package in.tech_camp.chatapp.form;

import lombok.Data;

@Data
public class UserForm {
  private String name;
  private String email;
  private String password;
  private String passwordConfirmation;
}
