package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.models.Category;
import com.example.springsecurityapplication.models.Image;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.CategoryRepository;
import com.example.springsecurityapplication.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {

    private final ProductService productService;

//    private final OrderService orderService;

    @Value("${upload.path}")
    private String uploadPath;

    public AdminController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    private final CategoryRepository categoryRepository;

    @GetMapping("/admin/product/add")
    public String addProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("category", categoryRepository.findAll());
        return "product/addProduct";
    }

    @PostMapping("/admin/product/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,
                             @RequestParam("file_one") MultipartFile file_one,
                             @RequestParam("category") int category, Model model) throws IOException {
        Category category_db = (Category) categoryRepository.findById(category).orElseThrow();
        System.out.println(category_db.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryRepository.findAll());
            return "product/addProduct";
        }
        if (file_one != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file_one.getOriginalFilename();
            file_one.transferTo(new File(uploadPath + "/" + resultFileName));
            Image image = new Image();
            image.setProduct(product);
            image.setFileName(resultFileName);
            product.addImageToProduct(image);
        }
        productService.saveProduct(product, category_db);
        return "redirect:/admin";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("products", productService.getAllProduct());
        return "admin";
    }

    //    // админ удаляет продукт по id
    @GetMapping("admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin";
    }

    // админ редактирует продукт по id
    @GetMapping("/admin/product/edit/{id}")
    public String editProduct(Model model, @PathVariable("id") int id) {
        model.addAttribute("product", productService.getProductId(id));
        model.addAttribute("category", categoryRepository.findAll());
        return "product/editProduct";
    }

    @PostMapping("/admin/product/edit/{id}")
    public String editProduct(@ModelAttribute("product") @Valid Product product, BindingResult bindingResult,
                              @PathVariable("id") int id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("category", categoryRepository.findAll());
            return "product/editProduct";
        }
        productService.updateProduct(id, product);
        return "redirect:/admin";
    }


//    //    // админ выводит список всех ордеров
//    @GetMapping("/admin/orders")
//    public String orderAdmin(Model model){
//        return "orders";
//    }

}
