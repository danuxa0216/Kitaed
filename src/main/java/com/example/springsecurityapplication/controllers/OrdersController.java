package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrdersController {
    @GetMapping("/admorders")
    public String login() {
        return "orders";
    }


}