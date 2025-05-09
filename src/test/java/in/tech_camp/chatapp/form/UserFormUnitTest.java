package in.tech_camp.chatapp.form;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.chatapp.factories.UserFormFactory;
import in.tech_camp.chatapp.validation.ValidationPriority1;
import in.tech_camp.chatapp.validation.ValidationPriority2;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
@SpringBootTest
public class UserFormUnitTest {
  private UserForm userForm;
  private Validator validator;
  private BindingResult bindingResult;

  @BeforeEach
  public void setUp() {
    userForm = UserFormFactory.createUser();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    bindingResult = Mockito.mock(BindingResult.class);
  }

  @Nested
  class ユーザーを作成できる場合 {
    @Test
    public void nameとemailとpasswordとpasswordconfirmationが存在すれば登録できる () {
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertTrue(violations.isEmpty());
    }
  }

  @Nested
  class ユーザーを作成できない場合 {
    @Test
    public void nameが空では登録できない () {
      userForm.setName("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertFalse(violations.isEmpty());;
      assertEquals("Name can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void emailが空では登録できない () {
      userForm.setEmail("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertFalse(violations.isEmpty());
      assertEquals("Email can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void emailにアットマークが存在しない場合() {
      userForm.setEmail("test");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertFalse(violations.isEmpty());
      assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが空では登録できない() {
      userForm.setPassword("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertFalse(violations.isEmpty());
      assertEquals("Password can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが6文字以下の場合() {
      userForm.setPassword("a");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertFalse(violations.isEmpty());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが129文字以上の場合() {
      // userForm.setPassword("a" * 129);
      // X文字の文字列を生成する方法
      // 1. StringBuilder + apppend()
      // StringBuilder sb = new StringBuilder(129);
      // for (int i = 0; i < sb.length(); i++) {
      //   sb.append('X');
      // }
      // String str = sb.toString();
      // 2 Charの配列 -> String
      char[] chars = new char[129];
      Arrays.fill(chars, 'X');
      String str = new String(chars);

      userForm.setPassword(str);
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertFalse(violations.isEmpty());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }
  
  @Test
  public void パスワードの不一致の場合() {
    userForm.setPasswordConfirmation("test");
    userForm.validatePasswordConfirmation(bindingResult);
    // assertTrue(bindingResult.hasErrors());
    verify(bindingResult).rejectValue("passwordConfirmation", "error.user", "Password confirmation doesn't match Password");
    }
  }
}