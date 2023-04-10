package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {
    private final PersonValidator personValidator;
    private final PersonService personService;

    public MainController(PersonValidator personValidator, PersonService personService) {
        this.personValidator = personValidator;
        this.personService = personService;
    }

    @GetMapping("/index")
    public String index(){
        // получаем объект аутентификации - с помощью SecurityContextHolder обращаемся к контексту и на нём вызываем метод аутентификации. из сессии текущего пользователя получаем объект, который был положен в данную сессию после аутентифкации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(personDetails.getPerson());
        System.out.println("ID пользователя: " + personDetails.getPerson().getId());
        System.out.println("Логин пользователя: " + personDetails.getPerson().getLogin());
        System.out.println("Пароль пользователя: " + personDetails.getPerson().getPassword());
        System.out.println(personDetails);
        return "index";
    }

    // 1 способ:
//    @GetMapping("/registration")
//    public String registration(Model model){
//        model.addAttribute("person", new Person());
//        return "registration";
//    }

    // 2 способ:
    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()){
            return "registration";
        }
        personService.register(person);
        return "redirect:/index";
    }
//    Idea-e5df3338=3bc9a670-59e3-4aab-8665-ec341ce15205;
//    user_id=1; JSESSIONID=89BEE21AC45DC524922CAB2AB6E8D0C2

//    Idea-e5df3338=3bc9a670-59e3-4aab-8665-ec341ce15205;
//    user_id=1; JSESSIONID=A62294A1BAE74309DF97EE717471E385
}
