package org.example.Security.controllers;

import org.example.Security.GlobalExceptionHandler;
import org.example.Security.service.PersonApiInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Qualifier("PersonApiService")
@Controller
@RequestMapping("api/admin")
public class AdminController {
    private PersonApiInterface personApiInterface;
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    public AdminController(PersonApiInterface personApiInterface, GlobalExceptionHandler globalExceptionHandler) {
        this.personApiInterface = personApiInterface;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping("/hiAdmin")
    public ResponseEntity<String> getGreetings(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("Логин: "+userDetails.getUsername()+" Пароль:  "+userDetails.getPassword());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("Здраствуйте уважаемый, " + userDetails.getUsername() + " админам тут всегда рады!");
    }
    @GetMapping("")
    public String adminPanel(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("isAdmin", true);
        return "admin";
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
