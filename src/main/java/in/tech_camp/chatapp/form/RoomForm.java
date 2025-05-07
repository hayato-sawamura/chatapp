package in.tech_camp.chatapp.form;

import java.util.List;

import lombok.Data;

@Data
public class RoomForm {
  private String name;
  private List<Integer> memberIds;
}
