package org.example.Security.controllers;

import org.example.Security.GlobalExceptionHandler;
import org.example.Security.service.PersonApiInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Qualifier("PersonApiService")//т.к. мы сделали систему гибче указав в качестве сериса-интерфейс, нужно указать его реализацию  виде класса
//таким образом  другом контроллере с тем же функционалом, но для другой платформы нужно лишь изменить qualifаir и написать новую реализацию
@Controller
@RequestMapping("api/home")
public class HomeController {
    private PersonApiInterface personApiInterface;
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    public HomeController(PersonApiInterface personApiInterface, GlobalExceptionHandler globalExceptionHandler) {
        this.personApiInterface = personApiInterface;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping("/greetings")
    public ResponseEntity<String> getGreetings(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Логин: "+userDetails.getUsername()+" Пароль:  "+userDetails.getPassword());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Ну, удачной охоты, сталкер, " + userDetails.getUsername() + "!");
    }

    @GetMapping("/loginNew")
    public String login(Model model) {
        model.addAttribute("user", "Vasya");
        return "unauthorized";
    }

//    @PostMapping("/create")
//    public ResponseEntity<PersonDTO> createUser(@AuthenticationPrincipal UserDetails userDetails
//    ){
//        return personApiInterface.createUser(userDetails, false);
//    }
//
//
//    @DeleteMapping("/deleteById")
//    public ResponseEntity<Void>deleteById(
//            @RequestParam(name = "id") Long id
//    ){
//        return personApiInterface.deleteById(id);
//    }
//    @DeleteMapping("/deleteAll")
//    public ResponseEntity<Void>deleteAll(){
//        return personApiInterface.deleteAll();
//    }
//
//    @GetMapping("/getTotalCount")
//    public ResponseEntity<Long>getTotalCount(){
//        return personApiInterface.getTotalCount();
//    }
//
//    @GetMapping("/existsById")
//    public ResponseEntity<Boolean>existsById(
//            @RequestParam(name="id")long id
//    ){
//        return personApiInterface.existsById(id);
//    }

}
