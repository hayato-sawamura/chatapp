package in.tech_camp.chatapp.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import in.tech_camp.chatapp.factories.RoomFormFactory;
import in.tech_camp.chatapp.validation.ValidationPriority1;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
@SpringBootTest
public class RoomFormUnitTest {
  private RoomForm roomForm;

  private Validator validator;

  @BeforeEach
  public void setUp() {
    roomForm = RoomFormFactory.createRoom();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Nested
  class ルームを作成できる場合 {
    @Test
    public void nameの値が存在すれば作成できる () {
      Set<ConstraintViolation<RoomForm>> violations = validator.validate(roomForm, ValidationPriority1.class);
      assertTrue(violations.isEmpty());
      
    }
  }
  
  @Nested
  class ルームを作成できない場合 {
    @Test
    public void nameが空では作成できない () {
      roomForm.setName("");
      Set<ConstraintViolation<RoomForm>> violations = validator.validate(roomForm, ValidationPriority1.class);
      assertFalse(violations.isEmpty());
      assertEquals("This name is empty", violations.iterator().next().getMessage());
    }
  }
}