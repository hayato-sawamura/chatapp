package in.tech_camp.chatapp.factories;

import com.github.javafaker.Faker;

import in.tech_camp.chatapp.form.RoomForm;

public class RoomFormFactory {
    private static final Faker faker = new Faker();

  public static RoomForm createRoom() {
    RoomForm roomForm = new RoomForm();

    roomForm.setName(faker.lorem().sentence());

    return roomForm;
  }
}