package in.tech_camp.chatapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MessageController {
  @GetMapping("/")
  public String showMessages() {
    return "messages/index";
  }
  

}